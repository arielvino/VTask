package com.av.vtask.providers

import com.av.vtask.App
import com.av.vtask.datastructure.Task
import com.av.vtask.security.Authorization
import com.av.vtask.security.CryptoFactory
import com.av.vtask.security.Permissions
import com.av.vtask.security.PermissionsManager
import com.av.vtask.storage.FileFactory
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime

object InternalStorageProvider : IDataProvider {
    private val registriesDirPath: String = "${App.appContext.filesDir.absolutePath}/Registries"
    private val dictionaryPath: String = "${App.appContext.filesDir.absolutePath}/MAIN_DICTIONARY"
    private val builtInRegistriesDirPath: String =
        "${App.appContext.filesDir.absolutePath}/BuildInRegistries"
    private val tasksDirPath: String = "${App.appContext.filesDir.absolutePath}/Tasks"

    private fun createMainDictionaryIfNotExist(): Boolean {
        try {
            val file = File(dictionaryPath)

            // Check if the file already exists, create it if it doesn't
            if (!file.exists()) {
                file.createNewFile()
            }
            return true
        } catch (e: IOException) {
            println("An error occurred while creating the file: ${e.message}")
        }
        return false
    }

    private const val idIndex: Int = 0
    private const val pathIndex: Int = 1
    //--future add--

    private const val TASK_EXTENSION = ".task"
    private const val CONTENT_EXTENSION = ".content"
    private const val CHILDREN_LIST_EXTENSION = ".children"
    private const val REGISTRY_EXTENSION = ".registry"

    private fun buildItem(attrs: Array<String>): String {
        return attrs[idIndex] + " " + attrs[pathIndex] //--future add--
    }

    private fun decodeItem(item: String): List<String> {
        return item.split(" ")
    }

    override fun initialize(attrs: Array<Any>?) {
        createMainDictionaryIfNotExist()
        File(registriesDirPath).mkdir()
        File(builtInRegistriesDirPath).mkdir()
        File(tasksDirPath).mkdir()
        for (reg in Registries.values()) {
            File("$builtInRegistriesDirPath/${reg.name}$REGISTRY_EXTENSION").createNewFile()
        }
    }

    override fun exist(id: String): Boolean {
        val lines = FileFactory.readLines(dictionaryPath)
        val (_, exist) = binarySearch(lines, 0, lines.size - 1, id.substring(1).toInt())
        return exist
    }

    override fun loadTask(id: String): Task {
        //verify permissions:
        if (!PermissionsManager.allowedTo(Permissions.Read)) {
            throw SecurityException("Action is not authorized.")
        }

        //find the right path:
        val lines = FileFactory.readLines(dictionaryPath)
        val wantedId: Int = id.substring(1).toInt()
        val (index, exist) = binarySearch(lines, 0, lines.size - 1, wantedId)
        if (exist) {
            val path: String = decodeItem(lines[index])[pathIndex]

            //load attrs:
            val encryptedAttrs = FileFactory.readFile(path + TASK_EXTENSION)
            val decryptedAttrs = CryptoFactory.decrypt(encryptedAttrs)
            val attrsContent: List<String> = XmlFileParser.readTask(decryptedAttrs!!)

            //load content:
            val contentPath = path + CONTENT_EXTENSION
            var content: String? = null
            if (File(contentPath).exists()) {
                val encryptedContent = FileFactory.readFile(contentPath)
                content = CryptoFactory.decrypt(encryptedContent)
            }

            return Task(
                attrsContent[IFileParser.ID_INDEX],
                attrsContent[IFileParser.TITLE_INDEX],
                content,
                LocalDateTime.parse(
                    attrsContent[IFileParser.DEADLINE_INDEX], Task.dateTimeFormatter
                ),
                Duration.parse(attrsContent[IFileParser.DURATION_INDEX])
            )

        } else {
            throw Exception("Item not found.")
        }
    }

    override fun createTask(task: Task): String {

        //verify permission:
        if (!PermissionsManager.allowedTo(Permissions.Add)) {
            throw SecurityException("Action is not authorized.")
        }

        //generate new id:
        var exist = true
        var correctId = 0
        val lines: List<String> = FileFactory.readLines(dictionaryPath)
        var correctIndex: Int = -1

        //verify that the id is unique:
        while (exist) {
            val id = SecureRandom().nextInt()
            val (index, existent) = binarySearch(lines, 0, lines.size - 1, id)
            exist = existent
            if (!exist) {
                correctId = id
                correctIndex = index
            }
        }

        //save the id in a task:
        val newTask = Task("_$correctId", task.title, task.content, task.deadline, task.duration)

        //store attrs:
        val xml: String = XmlFileParser.writeTask(newTask)
        val encryptedTask = CryptoFactory.encrypt(xml)
        FileFactory.writeToFile(
            "$tasksDirPath/${newTask.id}$TASK_EXTENSION", encryptedTask!!
        )

        //store content:
        if (newTask.content != null) {
            val encryptedContent = CryptoFactory.encrypt(newTask.content)
            FileFactory.writeToFile(
                "$tasksDirPath/${newTask.id}$CONTENT_EXTENSION", encryptedContent!!
            )
        }

        //register id in dictionary:
        FileFactory.insertLine(
            dictionaryPath,
            buildItem(arrayOf(newTask.id, "$tasksDirPath/${newTask.id}")),
            correctIndex
        )

        //successful
        return "_$correctId"
    }

    override fun updateTask(id: String, task: Task): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteTask(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun registerItemToCustomRegistry(itemId: String, registryName: String) {
        //verify permission:
        if (!PermissionsManager.allowedTo(Permissions.Read)) {
            throw SecurityException("Action is not authorized.")
        }

        //verify that the item exist:
        if (exist(itemId)) {

            //create registry if not exist:
            val path =
                "$registriesDirPath/${CryptoFactory.encrypt(registryName)}$REGISTRY_EXTENSION"
            File(path).createNewFile()

            //check if the item is not already registered:
            val registeredItems = FileFactory.readLines(path)
            val (rIndex, rExist) = binarySearch(
                registeredItems, 0, registeredItems.size - 1, itemId.substring(1).toInt()
            )

            //add item:
            if (!rExist) {
                FileFactory.insertLine(path, itemId, rIndex)
            }
        }
    }

    override fun registerItemToBuildInRegistry(itemId: String, registry: Registries) {
        //verify permissions:
        if (!PermissionsManager.allowedTo(Permissions.Add)) {
            throw SecurityException("Operation is not authorized.")
        }

        //verify that the item exist:
        if (exist(itemId)) {

            //determine path:
            val regPath = "$builtInRegistriesDirPath/${registry.name}$REGISTRY_EXTENSION"

            //make sure that the item is not already exist in the registry:
            val rLines = FileFactory.readLines(regPath)
            val (index, exist) = binarySearch(
                rLines, 0, rLines.size - 1, itemId.substring(1).toInt()
            )
            if (!exist) {
                //insert item:
                FileFactory.insertLine(regPath, itemId, index)
            }
        }
    }

    @Authorization(Permissions.Read)
    override fun loadCustomRegistryIDs(registryName: String): List<String> {
        //verify permissions:
        if (!PermissionsManager.allowedTo(Permissions.Read)) {
            throw SecurityException("Action is not authorized.")
        }

        //determine path:
        val path = "$registriesDirPath/${CryptoFactory.encrypt(registryName)}$REGISTRY_EXTENSION"

        //return IDs:
        return FileFactory.readLines(path)
    }

    override fun loadBuiltInRegistryIDs(registry: Registries): List<String> {
        return FileFactory.readLines("$builtInRegistriesDirPath/${registry.name}$REGISTRY_EXTENSION")
    }

    override fun getCustomRegistries(): List<String> {
        //verify permissions:
        if (!PermissionsManager.allowedTo(Permissions.Read)) {
            throw SecurityException("Operation is not authorized.")
        }

        //get all files with .registry extension:
        val registries = File(registriesDirPath).listFiles { _, name ->
            name.endsWith(REGISTRY_EXTENSION)
        }

        val names = mutableListOf<String>()
        for (file in registries!!) {
            //add decrypted name:
            names.add(
                //decrypt the registry name:
                CryptoFactory.decrypt(file.nameWithoutExtension)!!
            )
        }
        return names
    }

    private fun binarySearch(
        lines: List<String>, start: Int, end: Int, wanted: Int
    ): Pair<Int, Boolean> {
        //patch for empty list:
        if (lines.isEmpty()) {
            return Pair(0, false)
        }

        //parse id:
        val limit: Int = start + (end - start) / 2
        val id: Int = decodeItem(lines[limit])[idIndex].substring(1).toInt()

        //found:
        if (id == wanted) {
            return Pair(limit, true)
        }

        //not found:
        if (end == start) {
            return Pair(limit, false)
        }

        //search higher:
        if (id < wanted) {
            return binarySearch(lines, limit + 1, end, wanted)
        }

        //search lower:
        //if (id > wanted)
        return binarySearch(lines, start, limit, wanted)
    }
}

/**
 * update item
 * delete item
 */
package com.av.vtask.providers

import com.av.vtask.datastructure.Task
import com.av.vtask.security.Authorization
import com.av.vtask.security.Permissions

interface IDataProvider {
    fun initialize(attrs: Array<Any>?)

    fun exist(id: String): Boolean

    @Authorization(Permissions.Read)
    fun loadTask(id: String): Task?

    /**
     * @return - the id of the new created task.
     */
    @Authorization(Permissions.Add)
    fun createTask(task: Task): String

    @Authorization(Permissions.Change)
    fun updateTask(id: String, task: Task): Boolean

    @Authorization(Permissions.Delete)
    fun deleteTask(id: String): Boolean

    @Authorization(Permissions.Read)
    fun registerItemToCustomRegistry(itemId: String, registryName: String)

    @Authorization(Permissions.Add)
    fun registerItemToBuildInRegistry(itemId: String, registry: Registries)

    @Authorization(Permissions.Read)
    fun loadCustomRegistryIDs(registryName: String): List<String>
    fun loadBuiltInRegistryIDs(registry: Registries): List<String>

    @Authorization(Permissions.Read)
    fun getCustomRegistries(): List<String>

    //remove item from builtin registry
    //remove item from custom registry
    //todo: add Mission and Folder
    //todo: allow relocating
}
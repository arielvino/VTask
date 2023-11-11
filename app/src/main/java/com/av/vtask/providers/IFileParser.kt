package com.av.vtask.providers

import com.av.vtask.datastructure.Task

interface IFileParser {
    companion object {
        const val ID_INDEX = 0
        const val TITLE_INDEX = 1
        const val DEADLINE_INDEX = 2
        const val DURATION_INDEX = 3
    }

    fun readTask(fileContent: String): List<String>
    fun writeTask(task: Task): String

    /**
     * add Mission and folder
     */
}
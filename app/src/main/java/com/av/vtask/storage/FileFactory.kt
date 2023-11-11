package com.av.vtask.storage

import java.io.File

class FileFactory {
    companion object {
        fun readFile(path: String): String {
            return File(path).readText()
        }

        fun writeToFile(path: String, content: String) {
            File(path).writeText(content)
        }

        fun readLines(path: String): List<String> {
            return File(path).readLines()
        }

        fun writeLines(path: String, lines: List<String>) {
            var content = ""
            for (line in lines) {
                content += line + "\r\n"
            }
            content = content.substring(0, content.lastIndexOf("\r\n"))
            writeToFile(path, content)
        }

        /**
         * This method insert a string as a line to specific file, at a specific index.
         * If you want to add the new line at the end of the file (regardless of index) leave the index param null.
         * If the index is higher than the file's lines count - exception will be thrown.
         * @param path - the path to your file.
         * @param line - the string you want to add as a line.
         * @param index - the index you want to insert the line at. Pass null index if you want to add it at the end of the file.
         */
        fun insertLine(path: String, line: String, index: Int?) {
            //read:
            val lines = readLines(path).toMutableList()


            //add line:
            if (index == null) {
                lines.add(line)
            } else {
                if (index > lines.size) {
                    throw IndexOutOfBoundsException("The specified index is higher than the lines count.")
                } else {
                    lines.add(index, line)
                }
            }

            //write:
            writeLines(path, lines)
        }

        fun removeLine(path: String, index: Int) {
            //read:
            val lines = readLines(path).toMutableList()

            //remove line:
            lines.removeAt(index)

            //write:
            writeLines(path, lines)

        }
    }
}
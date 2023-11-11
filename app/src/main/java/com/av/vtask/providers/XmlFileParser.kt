package com.av.vtask.providers

import com.av.vtask.datastructure.Task
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object XmlFileParser : IFileParser {
    private const val TASK = "task"
    private const val TITLE = "title"
    private const val ID = "id"
    private const val DEADLINE = "deadline"
    private const val DURATION = "duration"



    override fun readTask(fileContent: String): List<String> {
        val attrs: MutableList<String> = mutableListOf()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputSource = org.xml.sax.InputSource(StringReader(fileContent))
        val doc = builder.parse(inputSource)

        val taskElement = doc.getElementsByTagName(TASK).item(0) as Element
        attrs.add(taskElement.getAttribute(ID))//id

        attrs.add(doc.getElementsByTagName(TITLE).item(0).textContent)//title

        attrs.add(taskElement.getAttribute(DEADLINE))//deadline
        attrs.add(taskElement.getAttribute(DURATION))//duration

        return attrs
    }

    override fun writeTask(task: Task): String {
        val document = XmlUtils.createDocument()

        // Create Task element
        val taskElement: Element = document.createElement(TASK)
        taskElement.setAttribute(ID, task.id)//id
        taskElement.setAttribute(DEADLINE, task.deadline.format(Task.dateTimeFormatter))//deadline
        taskElement.setAttribute(DURATION, task.duration.toString())//duration
        document.appendChild(taskElement)

        //title element
        val title: Element = document.createElement(TITLE)
        title.textContent = task.title
        taskElement.appendChild(title)

        // Convert the XML document to string
        return XmlUtils.documentToString(document)
    }

    object XmlUtils {
        fun createDocument(): Document {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            return builder.newDocument()
        }

        fun documentToString(doc: Document): String {
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val source = DOMSource(doc)
            val writer = StringWriter()
            val result = StreamResult(writer)
            transformer.transform(source, result)
            return writer.toString()
        }
    }
}
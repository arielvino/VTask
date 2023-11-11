package com.av.vtask.datastructure

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Task(
    val id: String = "",
    val title: String,
    val content: String? = null,
    val deadline: LocalDateTime,
    val duration: Duration
) : IDataItem {


    companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}


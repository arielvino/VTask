package com.av.vtask.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.av.vtask.App
import com.av.vtask.R
import com.av.vtask.datastructure.Task
import com.av.vtask.ui.ui.theme.VTaskTheme
import java.time.LocalDateTime
import java.time.Month

class EditTaskActivity : ComponentActivity() {
    companion object {
        const val ID_KEY = "id"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            var title by remember {
                mutableStateOf("")
            }

            VTaskTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditor(id: String? = null) {
    //init task if provided:
    var task: Task? = null
    if (id != null) {
        task = App.dataProvider.loadTask(id)
    }

    var title by remember {
        if (task == null) {
            mutableStateOf("")
        } else {
            mutableStateOf(task!!.title)
        }
    }

    Column {
        //title:
        Text(text = App.appContext.getString(R.string.title))
        TextField(value = title, onValueChange = {
            title = it

        })

        //deadline:
        Row {

        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeInput(
    dateTime: LocalDateTime? = null,
    onValueChanged: (updatedDateTime: LocalDateTime) -> Unit
) {
    //initial time:
    var localDateTime: LocalDateTime
    if (dateTime == null) {
        localDateTime = LocalDateTime.now()
    } else {
        localDateTime = dateTime
    }

    var year: String by remember {
        mutableStateOf(localDateTime.year.toString())
    }
    var month: Month by remember {
        mutableStateOf(localDateTime.month)
    }
    var day: Int by remember {
        mutableStateOf(localDateTime.dayOfMonth)
    }
    var hour: Int by remember {
        mutableStateOf(localDateTime.hour)
    }
    var minute: Int by remember {
        mutableStateOf(localDateTime.minute)
    }

    fun internalValueUpdate() {

    }

    Row {
        TextField(value = year, onValueChange = {
            year = it
            internalValueUpdate()
        })
        Text(value = ":")
        TextField()
    }
}

@Composable
fun NumberField(value: Int, minValue: Int, maxValue: Int, onValueChanged: () -> Unit) {
    
}
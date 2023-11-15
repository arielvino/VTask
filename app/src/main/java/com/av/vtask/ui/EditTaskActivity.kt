package com.av.vtask.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.av.vtask.App
import com.av.vtask.R
import com.av.vtask.datastructure.Task
import com.av.vtask.ui.ui.theme.VTaskTheme
import java.time.LocalDate
import java.time.LocalTime

class EditTaskActivity : ComponentActivity() {
    companion object {
        const val ID_KEY = "id"
    }

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
fun TaskEditor(id: String? = null, onSaveButtonPressed: (task: Task) -> Unit) {
    //init task if provided:
    var task: Task? = null
    if (id != null) {
        task = App.dataProvider.loadTask(id)
    }

    var title by remember {
        if (task == null) {
            mutableStateOf("")
        } else {
            mutableStateOf(task.title)
        }
    }
    var deadlineDate by remember {
        if (task == null) {
            mutableStateOf(LocalDate.now())
        } else {
            mutableStateOf(task.deadline.toLocalDate())
        }
    }
    var deadlineTime by remember {
        if(task == null){
            mutableStateOf(LocalTime.now())
        }
        else{
            mutableStateOf(task.deadline.toLocalTime())
        }
    }

    Column(
        modifier = Modifier.verticalScroll(state = ScrollState(0)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //title:
        Text(text = App.appContext.getString(R.string.title))
        TextField(
            modifier = Modifier.wrapContentHeight(),
            value = title, onValueChange = {
                title = it
            })

        //deadline date:
        ComposeUtils.DateInput(
            deadlineDate
        ) { selectedDate: LocalDate -> deadlineDate = selectedDate }

        //deadline time:
        ComposeUtils.TimePickerInput(
            initialTime = deadlineTime,
            onConfirm = {
                deadlineTime = it
        })


    }


}


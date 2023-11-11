package com.av.vtask.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.av.vtask.App
import com.av.vtask.DateTimePrinter
import com.av.vtask.security.Authorization
import com.av.vtask.security.Permissions


object TaskDisplay {
    @Composable
    @Authorization(Permissions.Read)
    fun DisplayTask(
        taskId: String
    ) {
        val myTask by remember {
            mutableStateOf(
                App.dataProvider.loadTask(taskId)!!
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {//todo: support both hebrew and english.
            Box(modifier = Modifier.padding(Dp(10f))) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    //title:
                    Text(text = myTask.title, color = MaterialTheme.colorScheme.onPrimary)

                    //deadline:
                    Text(
                        text = DateTimePrinter.printRemainingTime(myTask.deadline),
                        color = Color.Red,
                        fontSize = TextUnit(9F, TextUnitType.Sp),
                    )

                    //duration:
                    Text(
                        text = DateTimePrinter.printAbsDuration(myTask.duration),
                        color = Color.Cyan,
                        fontSize = TextUnit(9F, TextUnitType.Sp),
                    )

                    //content:
                    if (myTask.content != null) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .fillMaxWidth()
                                .padding(Dp(10f))
                        ) {
                            Text(
                                text = myTask.content!!,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowTaskInList(
        taskId: String,
        onSelected: () -> Any,
        onRemoveFromRegistry: () -> Any,//todo
    ) {
        val myTask by remember {
            mutableStateOf(
                App.dataProvider.loadTask(taskId)!!
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {//todo: support both hebrew and english.
            Box(modifier = Modifier.padding(Dp(10f))) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    //title:
                    Text(
                        text = myTask.title,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.clickable { onSelected() })

                    //deadline:
                    Text(
                        text = DateTimePrinter.printRemainingTime(myTask.deadline),
                        color = Color.Red,
                        fontSize = TextUnit(9F, TextUnitType.Sp),
                    )

                    //duration:
                    Text(
                        text = DateTimePrinter.printAbsDuration(myTask.duration),
                        color = Color.Cyan,
                        fontSize = TextUnit(9F, TextUnitType.Sp),
                    )
                }
            }
        }
    }
}

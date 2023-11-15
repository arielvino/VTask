package com.av.vtask.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

object ComposeUtils {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NumberField(
        value: Int,
        minValue: Int = Int.MIN_VALUE,
        maxValue: Int = Int.MAX_VALUE,
        onValueChanged: (value: Int) -> Unit
    ) {
        var stringValue: String by remember {
            mutableStateOf(value.toString())
        }
        TextField(
            value = stringValue,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(1.dp),
            onValueChange = {
                val number = it.toIntOrNull()
                if (number != null) {
                    if (number in minValue..maxValue) {
                        stringValue = it
                        onValueChanged(stringValue.toInt())
                    }
                }
                if (it.isEmpty()) {
                    stringValue = ""
                }
            })
    }

    @Composable
    fun DateInput(
        initialDate: LocalDate = LocalDate.now(),
        onDateChanged: (
            newDate: LocalDate
        ) -> Unit
    ) {

        // Fetching the Local Context
        val mContext = LocalContext.current

        // Initializing a Calendar
        val mCalendar = Calendar.getInstance()

        //setting the initial date:
        val mYear: Int = initialDate.year
        val mMonth: Int = initialDate.monthValue
        val mDay: Int = initialDate.dayOfMonth

        mCalendar.set(mYear, mMonth, mDay)

        // Declaring a string value to
        // store date in string format
        val mDate = remember { mutableStateOf("") }

        // Declaring DatePickerDialog and setting
        // initial values as current values (present year, month and day)
        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, year: Int, month: Int, mDayOfMonth: Int ->
                mDate.value = "$mDayOfMonth/${month + 1}/$year"
                onDateChanged(LocalDate.of(mYear, mMonth, mDayOfMonth))
            }, mYear, mMonth, mDay
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Creating a button that on
            // click displays/shows the DatePickerDialog
            Button(onClick = {
                mDatePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))) {
                Text(text = "Open Date Picker", color = Color.White)
            }

            // Adding a space of 100dp height
            Spacer(modifier = Modifier.size(100.dp))

            // Displaying the mDate value in the Text
            Text(
                text = "Selected Date: ${mDate.value}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun TimePickerInput(
        title: String = "Select Time:",
        onConfirm: (selectedTime: LocalTime) -> Unit,
        initialTime: LocalTime = LocalTime.now()
    ) {
        var mTime: LocalTime by remember {
            mutableStateOf(initialTime)
        }

        val timePickerDialog = TimePickerDialog(
            LocalContext.current,
            { _: TimePicker, hour: Int, minute: Int ->
                mTime = LocalTime.of(hour, minute)
                onConfirm(mTime)
            }, mTime.hour, mTime.minute, true
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Creating a button that on
            // click displays/shows the DatePickerDialog
            Button(onClick = {
                timePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))) {
                Text(text = "Open Time Picker", color = Color.White)
            }

            // Adding a space of 100dp height
            Spacer(modifier = Modifier.size(100.dp))

            // Displaying the mDate value in the Text
            Text(
                text = "Selected Time: ${mTime.hour}${mTime.minute}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
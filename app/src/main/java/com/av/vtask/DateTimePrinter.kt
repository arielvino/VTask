package com.av.vtask

import java.sql.Time
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

object DateTimePrinter {
    const val HOURS_PER_DAY = 24

    fun printRemainingTime(dateTime: LocalDateTime): String {
        val time = Duration.between(LocalDateTime.now(), dateTime)
        val output: String
        if (time.isNegative) {
            val late: String = App.appContext.getString(R.string.late_by)
            output = "$late: ${printAbsDuration(time)}"
        } else {
            val remains: String = App.appContext.getString(R.string.remaining_time)
            output = "$remains: ${printAbsDuration(time)}"
        }
        return output
    }

    /**
     * This method print a Duration value in a customized format.
     * The abstract value is printed, regardless if the Duration is negative or positive.
     */
    fun printAbsDuration(duration: Duration): String {
        //todo: support changing grammar for single and plural.

        val numToIgnoreDecimal = 3
        val days = abs(duration.toDays())
        if (days >= numToIgnoreDecimal) {
            return "$days days"
        }
        if(days >=1){
            var output ="$days days"
            val hours = abs(duration.toHours()% HOURS_PER_DAY)
            if(hours >= numToIgnoreDecimal){
                output +=", $hours hours"
            }
            return output
        }
        val hours = abs(duration.toHours())
        if(hours >= numToIgnoreDecimal){
            return "$hours hours"
        }
        else{
            val minuets = abs(duration.toMinutes())
            if(minuets >= numToIgnoreDecimal){
                return "$minuets minutes"
            }
            else{
                val seconds = abs(duration.seconds)
                return "$seconds seconds"
            }
        }
    }
}
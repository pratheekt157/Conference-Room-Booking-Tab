package com.example.conferenceroomtabletversion.utils

import java.text.SimpleDateFormat

class ConvertTimeTo12HourFormat {
    companion object {
        fun convert12(time: String): String{
            var newTime: String = ""
            try {
                val _24HourSDF = SimpleDateFormat("HH:mm")
                val _12HourSDF = SimpleDateFormat("hh:mm a")
                val _24HourDt = _24HourSDF.parse(time)
                newTime = _12HourSDF.format(_24HourDt)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newTime
        }
        fun convertTo24(time: String): String{
            var newTime: String = ""
            try {
                val _24HourSDF = SimpleDateFormat("HH:mm")
                val _12HourSDF = SimpleDateFormat("hh:mm a")
                val _12HourDt = _12HourSDF.parse(time)
                newTime = _24HourSDF.format(_12HourDt)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newTime
        }

    }
}
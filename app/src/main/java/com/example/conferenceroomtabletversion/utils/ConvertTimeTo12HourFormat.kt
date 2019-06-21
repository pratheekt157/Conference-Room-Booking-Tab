package com.example.conferenceroomtabletversion.utils

import java.text.ParseException
import java.text.SimpleDateFormat

class ConvertTimeTo12HourFormat {
    companion object {
        fun convert12(time: String): String{
            var newTime: String = ""
            try {
                val _24HourSDF = SimpleDateFormat("HH:mm")
                val _12HourSDF = SimpleDateFormat("hh:mm a")
                val _24HourDt = _24HourSDF.parse(time)
                System.out.println(_24HourDt)
                newTime = _12HourSDF.format(_24HourDt)
                println(_12HourSDF.format(_24HourDt))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newTime
        }

    }
}
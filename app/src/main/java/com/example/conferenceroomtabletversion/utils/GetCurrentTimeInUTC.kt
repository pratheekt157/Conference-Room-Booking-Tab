package com.example.conferencerommapp.utils

import java.text.SimpleDateFormat
import java.util.*

class GetCurrentTimeInUTC {
    companion object {
        /**
         * function will return system current date and time in UTC format
         */
        fun getCurrentTimeInUTC(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm'Z'")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }
    }
}
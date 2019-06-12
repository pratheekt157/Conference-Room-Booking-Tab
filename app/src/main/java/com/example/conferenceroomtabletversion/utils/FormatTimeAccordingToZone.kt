package com.example.conferencerommapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class FormatTimeAccordingToZone {
    companion object {
        @SuppressLint("SimpleDateFormat")

        fun formatDateAsUTC(localTime: String): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'")

            // parse local date time to get the date object
            var date = simpleDateFormat.parse(localTime)

            // set time zone to UTC
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            return sdf.format(date)
        }

        // return system current time zone
        fun getCurrentTimeZone(): String {
            return Calendar.getInstance().timeZone.id
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDateAsIndianStandardTime(timeInUtc: String): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            var localTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            localTimeFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            return localTimeFormat.format((sdf.parse(timeInUtc)))
        }
    }
}
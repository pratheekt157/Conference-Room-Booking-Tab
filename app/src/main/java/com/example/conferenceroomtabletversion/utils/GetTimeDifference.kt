package com.example.conferenceroomtabletversion.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class GetTimeDifferences {
    companion object {
        // get time difference in milliseconds
        @SuppressLint("SimpleDateFormat")
        fun getMillisecondsDifferenceForExtendMeeting(startTime: String, endTime: String): Long {
            val date = startTime.split(" ")[0]
            val startTimeForNextMeeting = startTime.split(" ")[1]
            val endTimeForRunningMeeting = endTime.split(" ")[1]
            val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
            val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTimeForNextMeeting")
            val endTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $endTimeForRunningMeeting")
            return startTimeAndDateTimeInDateObject.time - endTimeAndDateTimeInDateObject.time
        }
    }
}
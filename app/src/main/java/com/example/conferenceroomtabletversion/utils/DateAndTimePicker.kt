package com.example.conferenceroomtabletversion.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DateAndTimePicker {

    /**
     * this companion object will provide a static function
     */
    companion object {

        /**
         * this function will attach a time picker to the edittext field setTime
         */
        fun getTimePickerDialog(context: Context, setTime: TextView, timeSlot: String) {
            val timeFormat = SimpleDateFormat("HH:mm ", Locale.US)
            val sdf = SimpleDateFormat("HH:mm")
            val time = sdf.parse(timeSlot)
            val now = Calendar.getInstance()
            now.time = time
            val timePickerDialog =
                CustomTimePicker(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    val mCurrentTime = timeFormat.format(selectedTime.time).toString()
                    setTime.text = mCurrentTime
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }

    }
}
package com.example.conferenceroomtabletversion.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.widget.EditText
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
        fun getTimePickerDialog(context: Context, setTime: EditText) {
            val timeFormat = SimpleDateFormat("HH:mm ", Locale.US)
            val now = Calendar.getInstance()
            val timePickerDialog =
                TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    val mCurrentTime = timeFormat.format(selectedTime.time).toString()
                    setTime.text = Editable.Factory.getInstance().newEditable(mCurrentTime)
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }

        /**
         * this function will attach a date picker to the edittext field setDate
         */
        fun getDatePickerDialog(context: Context, setDate: EditText) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val now = Calendar.getInstance()
            val datePicker =
                DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val nowDate: String = dateFormat.format(selectedDate.time).toString()
                    setDate.text = Editable.Factory.getInstance().newEditable(nowDate)
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            datePicker.show()
        }
    }
}
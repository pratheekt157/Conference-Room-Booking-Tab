package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.conferenceroomtabletversion.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags.FlagField.after







class MainActivity : AppCompatActivity() {

    private var timeSlotList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.conferenceroomtabletversion.R.layout.activity_main)
        getTimeSlot()
        for(index in 0..timeSlotList.size-1) {
            Log.i("------", "$index -->  ${timeSlotList[index]}")
            if(index == 24 || index == 28) {
                timeSlotList.add(index + 1, "Praateek Patidar")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeSlot() {
        val df = SimpleDateFormat("hh:mm a")
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startDate = cal.get(Calendar.DATE)
        while (cal.get(Calendar.DATE) == startDate) {
            timeSlotList.add(df.format(cal.time))
            cal.add(Calendar.MINUTE, 15)
        }
    }
}

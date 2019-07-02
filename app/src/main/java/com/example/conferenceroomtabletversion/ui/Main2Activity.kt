package com.example.conferenceroomtabletversion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.TimeSlotAdapter
import com.example.conferenceroomtabletversion.utils.CustomTimePicker
import kotlinx.android.synthetic.main.activity_booking_status_new.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.TimePickerDialog
import android.util.Log
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.SlotFinalList
import kotlin.collections.ArrayList
import androidx.core.os.HandlerCompat.postDelayed
import android.os.Handler
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.activity_booking_status.*
import kotlinx.android.synthetic.main.activity_booking_status_new.recycler_view_todays_booking_list
import java.lang.Exception
import java.time.LocalTime


class Main2Activity : AppCompatActivity()  {
    var timeSlotList = mutableListOf<String>()
    var bookingList = mutableListOf<BookingDeatilsForTheDay>()
    val zero : Long = 0
    var mBookingList = mutableListOf<BookingDeatilsForTheDay>()
    var finalSlotList = mutableListOf<SlotFinalList>()
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mBookingListAdapter: TimeSlotAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_status_new)
        val sdf = SimpleDateFormat("mm")
        sdf.format(Date())
        Log.e("-----------------time", "" + sdf.format(Date()))

        Log.e("-----------------time", "" + LocalTime.parse("12:10").isBefore(LocalTime.parse("11:10")))
        //mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
        //getTimeSlot()
        //initAdapter()
    }
    private fun initAdapter() {
        mBookingListAdapter = TimeSlotAdapter(
            finalSlotList as ArrayList<SlotFinalList>,
            this,
            object: TimeSlotAdapter.BookMeetingClickListener {
                override fun bookSlot(time: String) {

                }

            }
        )
        recycler_view_todays_booking_list.adapter = mBookingListAdapter

      //  Handler().postDelayed(Runnable { recycler_view_todays_booking_list.scrollToPosition(1) }, 200)

    }

    private fun getTimeSlot() {
        val df = SimpleDateFormat("HH:mm")
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startDate = cal.get(Calendar.DATE)
        while (cal.get(Calendar.DATE) === startDate) {
            timeSlotList.add(df.format(cal.time))
            cal.add(Calendar.MINUTE, 15)
        }
    }
    private fun getMilliSecondDifference(timeSlot: String, bookingTime: String): Long {
        val sdf = SimpleDateFormat("HH:mm")
        val d1 = sdf.parse(timeSlot)
        val d2 = sdf.parse(bookingTime)
        return d2.time - d1.time
    }

    private fun makeList() {
        for(slot in timeSlotList) {
            var flag = false
            var finalSlot = SlotFinalList()
            finalSlot.slot = slot
            for (item in mBookingList) {
                val startTimeDifference = getMilliSecondDifference(slot, item.fromTime!!.split(" ")[1])
                val endTimeDifference = getMilliSecondDifference(slot, item.toTime!!.split(" ")[1])
                when {
                    startTimeDifference == zero ->  {
                        finalSlot.isBooked = true
                        flag = true
                        finalSlot.status = "Start"
                    }
                    endTimeDifference == zero -> {
                        finalSlot.isBooked = true
                        flag = true
                        finalSlot.status = "End"
                    }
                    startTimeDifference < 0 && endTimeDifference > 0 -> {
                        finalSlot.isBooked = true
                        flag = true
                        finalSlot.status = "Middle"
                    }
                    else -> {
                        finalSlot.isBooked = false
                    }
                }
                if(flag) {
                    break
                }
            }
            finalSlotList.add(finalSlot)
        }
        mBookingListAdapter.notifyDataSetChanged()
    }

}

package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conferenceroomtabletversion.BaseApplication
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.SolidAdapter
import com.example.conferenceroomtabletversion.helper.TimeSlotAdapter
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.SlotFinalList
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_setting_building_conference.*
import kotlinx.android.synthetic.main.booking_layout.*
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity

import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {

    lateinit var mCustomAdapter: SolidAdapter
    lateinit var mSocket: Socket
    private var timeSlotList = mutableListOf<String>()
    private var finalList = mutableListOf<SlotFinalList>()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_layout)

        val androidDeviceId = Settings.Secure.getString(this.contentResolver,
            Settings.Secure.ANDROID_ID)
        val app = application as BaseApplication
        mSocket = app.getmSocket()!!
        mSocket.connect()
        if(!mSocket.connected()) {
           Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
        }
        mSocket.on("make_call") {
            //make api call to get refreshed data
        }

//        main_layout.setOnClickListener {
//            //hideSoftKeyboard(this)
//        }

        edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                hide(v!!)
            }
        }

        makeList()
        initRecyclerView()
    }

    fun hide(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }


    private fun initRecyclerView() {
        mCustomAdapter = SolidAdapter(finalList as ArrayList<SlotFinalList>, this, object: TimeSlotAdapter.BookMeetingClickListener {
            override fun bookSlot(time: String) {

            }

        })
        recycler_view_todays_booking_list.adapter = mCustomAdapter
    }

    private fun makeList() {

        val p1 = SlotFinalList()
        p1.isBooked = false
        p1.status = "Past"
        p1.slot = "5:00 PM"

        val p2 = SlotFinalList()
        p2.isBooked = false
        p2.status = "Past"
        p2.slot = "5:15 PM"

        val p3 = SlotFinalList()
        p3.isBooked = false
        p3.status = "Past"
        p3.slot = "5:30 PM"

        val p4 = SlotFinalList()
        p4.isBooked = false
        p4.status = "Past"
        p4.slot = "5:45 PM"

        val item = SlotFinalList()
        item.isBooked = true
        item.status = getString(R.string.start)
        item.slot = "6:00 PM"
        item.organiser = "Prateek"

        val item1 = SlotFinalList()
        item1.isBooked = true
        item1.status = "Middle"
        item1.slot = "6:15 PM"

        val item2 = SlotFinalList()
        item2.isBooked = true
        item2.status = "Middle"
        item2.slot = "6:30 PM"

        val item3 = SlotFinalList()
        item3.isBooked = true
        item3.status = "Middle"
        item3.slot = "6:45 PM"

        val item4 = SlotFinalList()
        item4.isBooked = true
        item4.status = "End"
        item4.slot = "7:00 PM"

        val item5 = SlotFinalList()
        item5.isBooked = false
        item5.status = "Available"
        item5.slot = "7:00 PM"

        val item6 = SlotFinalList()
        item6.isBooked = false
        item6.status = "Available"
        item6.slot = "7:15 PM"

        val item7 = SlotFinalList()
        item7.isBooked = false
        item7.status = "Available"
        item7.slot = "7:30 PM"

        val item8 = SlotFinalList()
        item8.isBooked = true
        item8.status = getString(R.string.start)
        item8.slot = "7:45 PM"
        item8.organiser = "Dilna"

        val item9 = SlotFinalList()
        item9.isBooked = true
        item9.status = "Middle"
        item9.slot = "8:00 PM"

        val item10 = SlotFinalList()
        item10.isBooked = true
        item10.status = "End"
        item10.slot = "8:15 PM"

        val item11 = SlotFinalList()
        item11.isBooked = false
        item11.status = "Available"
        item11.slot = "8:15 PM"

        val item12 = SlotFinalList()
        item12.isBooked = false
        item12.status = "Available"
        item12.slot = "8:30 PM"

        val item13 = SlotFinalList()
        item13.isBooked = true
        item13.status = getString(R.string.start)
        item13.slot = "8:45 PM"
        item13.endTime = "9:00 PM"
        item13.organiser = "Abhijeet Shah"
        item13.meetingDuration = getString(R.string.for_15_minutes)

        val item14 = SlotFinalList()
        item14.isBooked = true
        item14.status = getString(R.string.start)
        item14.slot = "9:00 PM"
        item14.endTime = "9:15 PM"
        item14.organiser = "Kapil Patel"
        item14.meetingDuration = getString(R.string.for_15_minutes)

        val item15 = SlotFinalList()
        item15.isBooked = false
        item15.status = "Available"
        item15.slot = "9:15 PM"

        val item16 = SlotFinalList()
        item16.isBooked = true
        item16.status = getString(R.string.start)
        item16.slot = "9:30 PM"
        item16.endTime = "9:45 PM"
        item16.organiser = "Devshree"
        item16.meetingDuration = getString(R.string.for_15_minutes)

        val item17 = SlotFinalList()
        item17.isBooked = false
        item17.status = "Available"
        item17.slot = "9:45 PM"

        val item18 = SlotFinalList()
        item18.isBooked = false
        item18.status = "Available"
        item18.slot = "10:45 PM"


        finalList.add(p1)
        finalList.add(p2)
        finalList.add(p3)
        finalList.add(p4)
        finalList.add(item)
        finalList.add(item1)
        finalList.add(item2)
        finalList.add(item3)
        finalList.add(item4)
        finalList.add(item5)
        finalList.add(item6)
        finalList.add(item7)
        finalList.add(item8)
        finalList.add(item9)
        finalList.add(item10)
        finalList.add(item11)
        finalList.add(item12)
        finalList.add(item13)
        finalList.add(item14)
        finalList.add(item15)
        finalList.add(item16)
        finalList.add(item17)
        finalList.add(item18)


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


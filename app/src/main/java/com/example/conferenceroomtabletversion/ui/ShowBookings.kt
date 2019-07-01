package com.example.conferenceroomtabletversion.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowBookings : AppCompatActivity() {

    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mBookingListAdapter: BookingForTheDayAdapter
    private lateinit var mRecyclerView: RecyclerView
    var roomId:Int=-1
    var mBookingList = ArrayList<BookingDeatilsForTheDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_bookings)

        val myDate = "18:00:00"
        val sdf = SimpleDateFormat("HH:mm:ss")
        val sdfDate = SimpleDateFormat("yyyy-MM-dd")

        val date = sdf.parse(myDate)
        Log.e("------------error", "" +FormatTimeAccordingToZone.formatDateAsUTC("${sdfDate.format(Date())} 20:10"))
        Log.e("------------error", "" +FormatTimeAccordingToZone.formatDateAsUTC("${sdfDate.format(Date())} ${addDurationToTheTimeSlot("20:10", 10)}"))

        //init()
        //getBuildingIdFromSharedPreference()
        //makeCall()
        //observeData()
    }

    private fun addDurationToTheTimeSlot(timeSlot: String, duration: Int): String {
        val sdf = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val time = sdf.parse(timeSlot)
        val cal = Calendar.getInstance()
        cal.time = time
        cal.add(Calendar.MINUTE, duration)
        return sdf.format(cal.time)
    }

    private fun init() {
        // hide status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + "Show Meetings" + "</font>")

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //show the activity in full screen
        mRecyclerView = findViewById(R.id.recycler_view_booking_list)
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }

    private fun getBuildingIdFromSharedPreference(){
        roomId=GetPreference.getRoomIdFromSharedPreference(this)
        Log.i("------------",roomId.toString())
        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE){
            showToastAtTop("Ask for tablet setup again")
            startActivity(Intent(this@ShowBookings,SettingBuildingConferenceActivity::class.java))
            finish()
        }
    }

    private fun makeCall() {
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
            //val i = Intent(this, NoInternetConnectionActivity::class.java)
            //startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun getViewModel() {
        mProgressDialog.show()
        mBookingForTheDayViewModel.getBookingList(roomId)
    }

    // change start time and end time of meeting from UTC to Indian standard time zone
    private fun changeDateTimeZone(it: List<BookingDeatilsForTheDay>) {
        var startTimeInUtc: String
        var endTimeInUtc: String
        for (booking in it) {
            startTimeInUtc = booking.fromTime!!
            endTimeInUtc = booking.toTime!!
            booking.fromTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${startTimeInUtc.split("T")[0]} ${startTimeInUtc.split("T")[1]}")
            booking.toTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${endTimeInUtc.split("T")[0]} ${endTimeInUtc.split("T")[1]}")
        }
        mBookingList.addAll(it)
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {

        /**
         * observing data for booking list
         */
        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
            mProgressDialog.dismiss()
            mBookingList.clear()
            if(it.isEmpty()) {
                Toasty.info(this@ShowBookings, "No Booking found for the day!", Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
                changeDateTimeZone(it)
                setFilteredDataToAdapter(it)
            }

        })
        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(this, it as Int)
            finish()
        })

    }
    private fun setFilteredDataToAdapter(it: List<BookingDeatilsForTheDay>) {
        mBookingListAdapter = BookingForTheDayAdapter(
            it as ArrayList<BookingDeatilsForTheDay>
        )
        mRecyclerView.adapter = mBookingListAdapter
    }
    private fun showToastAtTop(message: String) {
        var toast =
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        val toastContentView = toast!!.view as LinearLayout
        var group = toast.view as ViewGroup
        var messageTextView = group.getChildAt(0) as TextView
        messageTextView.textSize = 30F
        var imageView = ImageView(applicationContext)
        imageView.setImageResource(R.drawable.ic_layers)
        toastContentView.addView(imageView, 0)
        toast.show()
    }
}
package com.example.conferenceroomtabletversion.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.helper.TimeSlotAdapter
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.SlotFinalList
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.activity_booking_status.*

class ConferenceRoomDetailActivity: AppCompatActivity() {
//    var timeSlotList = mutableListOf<SlotFinalList>()
//    private lateinit var mBookingListAdapter1: TimeSlotAdapter
//
//    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
//    private lateinit var mProgressDialog: ProgressDialog
//    var finalSlotList = mutableListOf<SlotFinalList>()
//    private var mCountDownTimer: CountDownTimer? = null
//    private lateinit var mRecyclerView: RecyclerView
//    private lateinit var mBookingListAdapter: TimeSlotAdapter
//    private var mBookingList = ArrayList<BookingDeatilsForTheDay>()
//    private var mNextMeeting = BookingDeatilsForTheDay()
//    private var mRunningMeeting = BookingDeatilsForTheDay()
//    private var mRunningMeetingId = -1
//    private var mDurationForNewBooking = 15
//    private var mDurationForExtendBooking = 15
//    private var mTimerRunning: Boolean = false
//    private var mTimeLeftInMillis: Long = 0
//    private var isCommingFromExtendedMeeting = false
//    var isMeetingRunning = false
//    private var roomId = -1
//    val zero : Long = 0
//    private var buildingId = -1
//    private var flag = false
//    private var isNextMeetingPresent = false
//    private var mMeetingIdForFeedback = -1
//    private var feedbackMessage = "The app is pretty cool!"
//    private var feedbackRating = 2
//    private lateinit var mEndMeetingMainRelativeLayout: RelativeLayout
//    private lateinit var mBookMeetingMainRelativeLayout: RelativeLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_booking_status)
//        observeData()
//
//    }
//    private fun setRoomDetails() {
//        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
//        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
//        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
//        if (
//            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
//            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
//            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
//        ) {
//            // goForSetup()
//        } else {
//            room_name_text_view.text = "$roomName, $buildingName"
//            room_capacity.text = "$roomCapacity seater"
//            //room_amenities.text = ""
//        }
//    }
//
//    /**
//     * init recycler view
//     */
//    private fun initRecyclerView() {
//        mBookingListAdapter = TimeSlotAdapter(
//            finalSlotList as ArrayList<SlotFinalList>,
//            this
//        )
//        recycler_view_todays_booking_list.adapter = mBookingListAdapter
//    }
//
//    /**
//     * hide status bar
//     */
//    private fun initStatusBar() {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        actionBar?.hide()
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//    }
//
//    /**
//     * initialize lateinit fields
//     */
//    private fun initLateInitFields() {
//        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
//        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
//        mEndMeetingMainRelativeLayout = findViewById(R.id.end_meeting_main_layout)
//        mBookMeetingMainRelativeLayout = findViewById(R.id.book_now_main_layout)
//        mRecyclerView = findViewById(R.id.recycler_view_todays_booking_list)
//    }
//
//
//    //set values inside shared preference for room details
//    private fun setValuesInsidePreferences()  {
//        val edit = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit()
//        edit.putInt(Constants.ROOM_ID, 34)
//        edit.putBoolean(Constants.ONBORDING,true)
//        edit.putInt(Constants.BUILDING_ID, 14)
//        edit.putString(Constants.BUILDING_NAME, "Pasta")
//        edit.putInt(Constants.CAPACITY, 4)
//        edit.putString(Constants.ROOM_NAME, "Sharanam")
//        edit.apply()
//    }
//
//    //---------------------------------------------------------------------------------------observe data for api response-------------------------------------------------------------
//
//
//    private fun observeData() {
//        observeDataForBookingListForTheDay()
//    }
//
//    /**
//     * Schedule for the day (Booking list from server)
//     */
//    private fun observeDataForBookingListForTheDay() {
//        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
//            mBookingList.clear()
//            mProgressDialog.dismiss()
//            changeDateTimeZone(it)
//        })
//        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
//            ShowToast.show(this, it as Int)
//        })
//    }
//
//    //---------------------------------------------------------------------------------------api call periodically-------------------------------------------------------------------
//
//    //call api to get the updated data
//    private fun getViewModel() {
//        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
//            startActivity(Intent(this@ConferenceRoomDetailActivity, SettingBuildingConferenceActivity::class.java))
//            finish()
//        } else {
//            //roomId = 34
//            mBookingForTheDayViewModel.getBookingList(roomId)
//        }
//    }
//
//    // change start time and end time of meeting from UTC to Indian standard time zone
//    private fun changeDateTimeZone(it: List<BookingDeatilsForTheDay>) {
//        var startTimeInUtc: String
//        var endTimeInUtc: String
//        for (booking in it) {
//            startTimeInUtc = booking.fromTime!!
//            endTimeInUtc = booking.toTime!!
//            booking.fromTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(
//                "${startTimeInUtc.split("T")[0]} ${startTimeInUtc.split("T")[1]}"
//            )
//            booking.toTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(
//                "${endTimeInUtc.split("T")[0]} ${endTimeInUtc.split("T")[1]}"
//            )
//        }
//        mBookingList.addAll(it)
//        makeList()
//        //setFilteredDataToAdapter(mBookingList)
//    }
}
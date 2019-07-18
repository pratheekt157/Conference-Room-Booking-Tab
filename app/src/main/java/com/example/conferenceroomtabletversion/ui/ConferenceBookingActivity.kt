package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferencerommapp.utils.GetCurrentTimeInUTC
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.*
import com.example.conferenceroomtabletversion.utils.ConvertTimeTo12HourFormat
import com.example.conferenceroomtabletversion.utils.DateAndTimePicker
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import com.example.conferenceroomtabletversion.viewmodel.WebSocketViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking_status.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION", "UNUSED_PARAMETER")
class ConferenceBookingActivity : AppCompatActivity() {


    private lateinit var mWebSocketViewModel: WebSocketViewModel
    private var timeSlotList = mutableListOf<String>()
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private var finalSlotList = mutableListOf<SlotFinalList>()
    private var mCountDownTimer: CountDownTimer? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBookingListAdapter: SolidAdapter
    private var mBookingList = ArrayList<BookingDeatilsForTheDay>()
    private var mBookingListForSlotArrangment = ArrayList<BookingDeatilsForTheDay>()
    private var mNextMeeting = BookingDeatilsForTheDay()
    private var mRunningMeeting = BookingDeatilsForTheDay()
    private var mRunningMeetingId = -1
    private var mDurationForExtendBooking = 15
    private var mTimerRunning: Boolean = false
    private var mTimeLeftInMillis: Long = 0
    private var isCommingFromExtendedMeeting = false
    var isMeetingRunning = false
    var startTimeFromSelectedSlot = ""
    private var roomId = -1
    private val zero: Long = 0
    private var buildingId = -1
    private var flag = false
    private var isNextMeetingPresent = false
    private var mMeetingIdForFeedback = -1
    private var feedbackMessage = Constants.DEFAULT_FEEDBACK_MEESAGE
    private var feedbackRating = 2
    private lateinit var mEndMeetingMainRelativeLayout: RelativeLayout
    private lateinit var mBookMeetingMainRelativeLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_status)
        checkForSetup()
        init()
        connectToHub()
        observeFromHub()
        observeData()
        makeRequestPeriodically()
        observeTimeFromBookingList()
    }


    /**
     * code for hide soft keyboard
     */
    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * function will check room details inside shared preference
     */
    private fun checkForSetup() {
        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
        if (
            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
        ) {
            goForSetup()
        }
    }

    /**
     * function will clear activity stack on press of back button
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /**
     *  function will hide soft keyboard whenever focus is changed from edit text to other view
     */
    private fun initiateClickListener() {
        feedback_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v!!)
            }
        }

        passcode_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v!!)
            }
        }
    }

    /**
     * init recycler view
     */
    private fun initRecyclerView() {
        mBookingListAdapter = SolidAdapter(
            finalSlotList as ArrayList<SlotFinalList>,
            this,
            object : TimeSlotAdapter.BookMeetingClickListener {
                override fun bookSlot(time: String) {
                    val timeIn24Hour = ConvertTimeTo12HourFormat.convertTo24(time)
                    startTimeFromSelectedSlot = timeIn24Hour
                    start_time_text_view.text = startTimeFromSelectedSlot
                    checkForNextMeeting(timeIn24Hour)
                }
            }
        )
        recycler_view_todays_booking_list.adapter = mBookingListAdapter
    }


    // ----------------------------------------------------------------------------------------------------------------- Next meeting for future time booking -------------------------------

    private fun checkForNextMeeting(slotTime: String) {
        var flag = false
        var nextMeetingStartTime = ""
        for (booking in mBookingList) {
            if (booking.status == getString(R.string.ended)) {
                val timeDifference = getMillisecondDifferenceForTimeSlot(booking.fromTime!!, slotTime)
                if (timeDifference > 0) {
                    flag = true
                    nextMeetingStartTime = booking.fromTime!!
                    break
                }
            }

        }
        if (flag) {
            val difference = getMillisecondDifferenceForTimeSlot(nextMeetingStartTime, slotTime)
            if (difference < (Constants.MILLIS_15)) {
                showToastAtTop(getString(R.string.cant_book))
                return
            }
        }
        makeVisibilityGoneForMainLayout()
        makeVisibilityVisibilityForBookMeetingMainLayout()
    }

    private fun init() {
        initStatusBar()
        initTextChangeListener()
        initLateInitFields()
        setRoomDetails()
        setClickListenerOnExtendMeetingSlots()
        setValuesFromSharedPreference()
        initiateClickListener()
        initRecyclerView()
        hideSoftKeyboard()
        getTimeSlot()
    }

    /**
     * hind soft keyboard
     */
    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    @SuppressLint("SetTextI18n")
    private fun setRoomDetails() {
        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
        val roomAmenities = GetPreference.getRoomAmenitiesFromSharedPreference(this)
        if (
            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
        ) {
            goForSetup()
        } else {
            room_name_text_view.text = "$roomName, $buildingName"
            room_capacity.text = "$roomCapacity seater"
            room_amenities.text = roomAmenities!!
        }
    }

    private fun goForSetup() {
        startActivity(Intent(this@ConferenceBookingActivity, SettingBuildingConferenceActivity::class.java))
        finish()
    }

    private fun setValuesFromSharedPreference() {
        roomId = GetPreference.getRoomIdFromSharedPreference(this)
        buildingId = GetPreference.getBuildingIdFromSharedPreference(this)
    }

    private fun initTextChangeListener() {
        textChangeListenerOnpasscodeEditText()
        textChangeListenerOnEndTimeEditText()
    }

    //------------------------------------------------------------------------------------------------------------------ main module -------------------------------------
    // main functionality
    private fun observeTimeFromBookingList() {
        val meetingListThread = object : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        runOnUiThread {
                            if (mBookingList.isNotEmpty()) {
                                flag = false
                                for (booking in mBookingList) {
                                    if (booking.status == getString(R.string.booked) || booking.status == getString(R.string.blocked)) {
                                        val timeDifference = getMillisecondsDifference(booking.fromTime!!)
                                        if (timeDifference > 0) {
                                            mNextMeeting = booking
                                            flag = true
                                            isNextMeetingPresent = true
                                            if (!isMeetingRunning) {
                                                available_till_text_view.visibility = View.VISIBLE
                                                available_till_text_view.text =
                                                    "Free till ${ConvertTimeTo12HourFormat.convert12(
                                                        mNextMeeting.fromTime!!.split(" ")[1]
                                                    )}"
                                            } else {
                                                available_till_text_view.visibility = View.GONE
                                            }
                                            visibilityToVisibleForLayoutForNextMeeting()
                                            setNextMeetingDetails()
                                            break
                                        }
                                    }
                                }
                                if (!flag) {
                                    isNextMeetingPresent = false
                                }
                                if (!isNextMeetingPresent && !isMeetingRunning) {
                                    available_till_text_view.visibility = View.VISIBLE
                                    available_till_text_view.text = getString(R.string.free_for_the_day)
                                    loadAvailableRoomUi()
                                    visibilityToGoneForLayoutForNextMeeting()
                                }
                                for (booking in mBookingList) {
                                    if (booking.status == getString(R.string.booked) || booking.status == getString(R.string.started) || booking.status == getString(
                                            R.string.blocked
                                        )
                                    ) {
                                        val startTimeInMillis = getMilliseconds(booking.fromTime!!)
                                        val endTimeInMillis = getMilliseconds(booking.toTime!!)
                                        if (System.currentTimeMillis() in startTimeInMillis..endTimeInMillis) {
                                            if (!isMeetingRunning) {
                                                available_till_text_view.visibility = View.GONE
                                                visibilityToVisibleForLayoutForNextMeeting()
                                                mRunningMeetingId = booking.bookingId!!
                                                mRunningMeeting = booking
                                                setDataToUiForRunningMeeting(booking)
                                                when {
                                                    booking.status == getString(R.string.meeting_started) -> {
                                                        // occupied status
                                                        setVisibilityToGoneForStartMeeting()
                                                        setVisibilityToVisibleForEndMeeting()
                                                        setVisibilityToVisibleForExtendMeeting()
                                                        unblock_room.visibility = View.GONE
                                                        startTimer(getMeetingDurationInMilliseonds(booking.toTime!!))
                                                    }
                                                    booking.status == getString(R.string.booked) -> {
                                                        visibilityToVisibleForLayoutForNextMeeting()
                                                        unblock_room.visibility = View.GONE
                                                        setVisibilityToVisibleForStartMeeting()
                                                    }
                                                    booking.status == getString(R.string.blocked) -> {
                                                        changeStatusToUnderMaintenance()
                                                        setVisibilityToGoneForStartMeeting()
                                                        setVisibilityToGoneForStartMeeting()
                                                        setVisibilityToGoneForStartMeeting()
                                                        setVisibilityToVisibleForUnblockRoom()
                                                        startTimer(getMeetingDurationInMilliseonds(booking.toTime!!))
                                                    }
                                                }
                                            }
                                            break
                                        }
                                    }
                                }
                            } else {
                                isNextMeetingPresent = false
                                loadAvailableRoomUi()
                                setNextMeetingTextToFree()
                            }
                        }
                        sleep(1000)
                    }
                } catch (e: InterruptedException) {
                    Log.e(Constants.TAG, e.message)
                }
            }
        }
        meetingListThread.start()
    }


    @SuppressLint("SetTextI18n")
    fun setDataToUiForRunningMeeting(booking: BookingDeatilsForTheDay) {
        setVisibilityToGoneForRunningMeeting()
        val startTime = booking.fromTime!!.split(" ")[1]
        val endTime = booking.toTime!!.split(" ")[1]
        mRunningMeetingId = booking.bookingId!!
        setGradientToOccupied()
        changeStatusToOccupied()
        meeting_time.text =
            ConvertTimeTo12HourFormat.convert12(changeFormat(startTime)) + " - " + ConvertTimeTo12HourFormat.convert12(
                changeFormat(endTime)
            )
        meeting_organiser.text = "Booked by ${booking.organizer} ${booking.meetingDuration}"
    }

    // set visibility to gone for running meeting layout
    private fun setVisibilityToGoneForRunningMeeting() {
        setVisibilityToGoneForEndMeeting()
        setVisibilityToGoneForExtendMeeting()
    }

    private fun connectToHub() {
        mWebSocketViewModel.connectToHub()
    }

    private fun observeFromHub() {
        mWebSocketViewModel.returnPositiveAcknoledge().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            //getViewModel()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setNextMeetingDetails() {
        if (!isMeetingRunning) {
            changeStatusToAvailable()
            setGradientToAvailable()
            setVisibilityToGoneForStartMeeting()
            meeting_organiser.text = "Booked by ${mNextMeeting.organizer} ${mNextMeeting.meetingDuration}"
            meeting_time.text =
                ConvertTimeTo12HourFormat.convert12(changeFormat(mNextMeeting.fromTime!!.split(" ")[1])) + " - " + ConvertTimeTo12HourFormat.convert12(
                    changeFormat(mNextMeeting.toTime!!.split(" ")[1])
                )
        }
    }

    // set next meeting text view to free
    private fun setNextMeetingTextToFree() {
        //duration_text_view.text = getString(R.string.free_for_the_day)
    }

    /**
     * hide status bar
     */
    private fun initStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mWebSocketViewModel = ViewModelProviders.of(this).get(WebSocketViewModel::class.java)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
        mEndMeetingMainRelativeLayout = findViewById(R.id.end_meeting_main_layout)
        mBookMeetingMainRelativeLayout = findViewById(R.id.book_now_main_layout)
        mRecyclerView = findViewById(R.id.recycler_view_todays_booking_list)
    }

    // ---------------------------------------------------------------------------------------Adapter for All booking meetings----------------------------------------------------------------------
    @SuppressLint("SimpleDateFormat")
    private fun changeFormat(time: String): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val simpleDateFormat1 = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        return simpleDateFormat1.format(simpleDateFormat.parse(time))
    }
    //---------------------------------------------------------------------------------------All button clicks in application-------------------------------------------------------------------

    /**
     * start meeting
     */
    fun startMeeting(view: View) {
        val startNow = EndMeeting()
        startNow.status = true
        startNow.bookingId = mRunningMeeting.bookingId
        startNow.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
        makeCallForStartMeeting(startNow)
    }

    // confirm new booking
    @SuppressLint("SimpleDateFormat")
    fun confirmBookMeeting(view: View) {
        if (validatePasscode() && validateEndTime()) {
            val mLocalBookingInput = NewBookingInput()
            mLocalBookingInput.passcode = passcode_edit_text.text.toString()
            mLocalBookingInput.eventName = getString(R.string.local_booking)
            if (!startAndTimeTimeValidate(startTimeFromSelectedSlot, end_time_text_view.text.toString())) {
                val sdfDate = SimpleDateFormat(getString(R.string.format_in_yyyy_mm_dd))
                val currentDate = sdfDate.format(Date())
                val startTimeInLocal = "$currentDate ${startTimeFromSelectedSlot.trim()}"
                val endTimeInLocal = "$currentDate ${end_time_text_view.text.trim()}"
                mLocalBookingInput.startTime = FormatTimeAccordingToZone.formatDateAsUTC(startTimeInLocal)
                mLocalBookingInput.endTime = FormatTimeAccordingToZone.formatDateAsUTC(endTimeInLocal)
                if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE || buildingId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
                    goForSetup()
                } else {
                    mLocalBookingInput.roomId = roomId
                    mLocalBookingInput.buildingId = buildingId
                }
                addBookingDetails(mLocalBookingInput)
            } else {
                showToastAtTop(getString(R.string.message_for_invalid_time_selection))
            }
        }
    }

    private fun startAndTimeTimeValidate(startTime: String, endTime: String): Boolean {
        val simpleDateFormat = SimpleDateFormat("hh:mm")
        val difference = simpleDateFormat.parse(startTime).time - simpleDateFormat.parse(endTime).time
        return difference >= 0
    }

    // end meeting before completion of meeting
    fun endMeeting(view: View) {
        if (mTimerRunning && mCountDownTimer != null) {
            makeVisibilityGoneForMainLayout()
            makeVisibilityVisibleForEndMeetingMainLayout()
        }
    }

    fun confirmEndMeeting(view: View) {
        handleEndNowButtonClick()
    }

    fun extendMeeting(view: View) {
        handleExtendMeetingClick()
    }

    fun confirmExtendMeeting(view: View) {
        getExtendedTimeDuration()
    }

    fun submitFeedback(view: View) {
        val feedback = Feedback()
        feedback.bookingId = mMeetingIdForFeedback
        feedback.comment = feedbackMessage
        feedback.rating = feedbackRating
        makeCallForAddFeedback(feedback)
        feedbackMessage = getString(R.string.average_feedback_default_message)
        feedbackRating = 2
    }

    fun cancelFeedback(view: View) {
        makeVisibilityVisibleForMainLayout()
        makeVisibilityGoneForFeedbackLayout()
        feedback_edit_text.text.clear()
        setDefaultImageForFeedback()
    }

    fun endBack(view: View) {
        makeVisibilityGoneForEndMeetingMainLayout()
        makeVisibilityVisibleForMainLayout()
    }

    fun bookBack(view: View) {
        clearTextAndEditTextAfterBooking()
        makeVisibilityGoneForBookMeetingMainLayout()
        makeVisibilityVisibleForMainLayout()
    }

    fun extendBack(view: View) {
        makeVisibilityGoneForExtendMeetingMainLayout()
        makeVisibilityVisibleForMainLayout()
        loadDefaultTimeSlotForExtendMeeting()
    }

    fun endTimePicker(view: View) {
        DateAndTimePicker.getTimePickerDialog(this, end_time_text_view, startTimeFromSelectedSlot)
    }

    fun unBlockRoom(view: View) {
        makeVisibilityVisibleForUnblockRoomLayout()
        makeVisibilityGoneForMainLayout()
    }

    fun unblockBack(view: View) {
        makeVisibilityVisibleForMainLayout()
        makeVisibilityGoneForUnblockRoomLayout()
    }

    fun confirmUnblockRoom(view: View) {
        unblockRoomCall(mRunningMeetingId)
    }
    // -------------------------------------------------------------------------------------Handle Extend meeting button click functionality------------------------------

    /**
     * function will check for next meeting time and show slot for extension
     */
    private fun handleExtendMeetingClick() {
        setVisibilityToVisibleForAllTimeForExtendSlots()
        if (isNextMeetingPresent) {
            setVisibilityToVisibleForAllTimeForExtendSlots()
            val difference = getMillisecondsDifferenceForExtendMeeting(mNextMeeting.fromTime!!)
            when {
                difference >= (Constants.MILLIS_60) -> {
                    setVisibilityToVisibleForAllTimeForExtendSlots()
                }
                difference >= (Constants.MILLIS_45) -> {
                    setVisibilityToGoneForExtendMin60()
                }

                difference >= (Constants.MILLIS_30) -> {
                    setVisibilityToGoneForExtendMin45()
                    setVisibilityToGoneForExtendMin60()
                }
                difference >= (Constants.MILLIS_15) -> {
                    setVisibilityToGoneForExtendMin30()
                    setVisibilityToGoneForExtendMin45()
                    setVisibilityToGoneForExtendMin60()
                }
            }
            if (difference < Constants.MILLIS_15) {
                showToastAtTop(getString(R.string.cant_extend))
                return
            }
        }
        makeVisibilityVisibleForExtendMeetingMainLayout()
        makeVisibilityGoneForMainLayout()
    }


//------------------------------------------------------------------------------------- all code related to time in milliseconds ---------------------------------------------------

    // get time difference in milliseconds
    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondsDifference(startDateTime: String): Long {
        val date = startDateTime.split(" ")[0]
        val startTime = startDateTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        val currTime = System.currentTimeMillis()
        return startTimeAndDateTimeInDateObject.time - currTime
    }

    // get time difference in milliseconds
    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondsDifferenceForExtendMeeting(startTime: String): Long {
        val date = startTime.split(" ")[0]
        val startTimeForNextMeeting = startTime.split(" ")[1]
        val endTimeForRunningMeeting = mRunningMeeting.toTime!!.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTimeForNextMeeting")
        val endTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $endTimeForRunningMeeting")
        return startTimeAndDateTimeInDateObject.time - endTimeAndDateTimeInDateObject.time
    }

    private fun getExtendedTimeDuration() {
        val mUpdateMeeting = UpdateBooking()
        mUpdateMeeting.newStartTime = FormatTimeAccordingToZone.formatDateAsUTC(mRunningMeeting.fromTime!!)
        mUpdateMeeting.bookingId = mRunningMeetingId
        mUpdateMeeting.newtotime = FormatTimeAccordingToZone.formatDateAsUTC(
            getNewExtendedEndTime(
                mRunningMeeting.toTime!!,
                mDurationForExtendBooking
            )
        )
        makeCallToUpdateTimeForBooking(mUpdateMeeting)
    }

    // extend meeting duration
    @SuppressLint("SimpleDateFormat")
    private fun getNewExtendedEndTime(endDateTime: String, duration: Int): String {
        val date = endDateTime.split(" ")[0]
        val endTime = endDateTime.split(" ")[1]
        val timeFormat = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val d = timeFormat.parse(endTime)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, duration)
        val newEndTime = timeFormat.format(cal.time)
        return "$date $newEndTime"
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMeetingDurationInMilliseonds(endTime: String): Long {
        val date = endTime.split(" ")[0]
        val toTime = endTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val endTimeAndDateInDateObject = simpleDateFormatForDate.parse("$date $toTime")
        return endTimeAndDateInDateObject.time - System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    fun getMilliseconds(startDateTime: String): Long {
        val date = startDateTime.split(" ")[0]
        val startTime = startDateTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        return startTimeAndDateTimeInDateObject.time
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondDifferenceForTimeSlot(startTime: String, timeSlot: String): Long {
        val nextMeetingStartTime = startTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse(nextMeetingStartTime)
        val timeSlotInDateFormat = simpleDateFormatForDate.parse(timeSlot)
        return startTimeAndDateTimeInDateObject.time - timeSlotInDateFormat.time
    }


//---------------------------------------------------------------------------------------visibility of all layouts-------------------------------------------------------------------

    /**
     * book now layout visibility
     */
    private fun makeVisibilityGoneForBookMeetingMainLayout() {
        mBookMeetingMainRelativeLayout.visibility = View.GONE
    }

    private fun makeVisibilityVisibilityForBookMeetingMainLayout() {
        mBookMeetingMainRelativeLayout.visibility = View.VISIBLE
    }

    /**
     * End meeting layout visibility
     */
    private fun makeVisibilityGoneForEndMeetingMainLayout() {
        end_meeting_main_layout.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForEndMeetingMainLayout() {
        end_meeting_main_layout.visibility = View.VISIBLE
    }

    /**
     * main layout visibility
     */
    private fun makeVisibilityGoneForMainLayout() {
        relative_main2.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForMainLayout() {
        relative_main2.visibility = View.VISIBLE
    }

    /**
     * End meeting layout visibility
     */
    private fun makeVisibilityGoneForExtendMeetingMainLayout() {
        extend_main_layout.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForExtendMeetingMainLayout() {
        extend_main_layout.visibility = View.VISIBLE
    }

    /**
     * feedback layout visibility
     */
    private fun makeVisibilityGoneForFeedbackLayout() {
        feedback_main_layout.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForFeedbackLayout() {
        feedback_main_layout.visibility = View.VISIBLE
    }

    /**
     * unblock confirm  layout visibility
     */

    private fun makeVisibilityGoneForUnblockRoomLayout() {
        unblock_room_main_layout.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForUnblockRoomLayout() {
        unblock_room_main_layout.visibility = View.VISIBLE
    }


//---------------------------------------------------------------------------------------visibility of all action text views----------------------------------------------------

    private fun setVisibilityToGoneForExtendMeeting() {
        extend_meeting.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForExtendMeeting() {
        extend_meeting.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForEndMeeting() {
        end_meeting.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForEndMeeting() {
        end_meeting.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForStartMeeting() {
        start_meeting.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForStartMeeting() {
        start_meeting.visibility = View.VISIBLE
    }

    private fun setVisibilityToVisibleForUnblockRoom() {
        unblock_room.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForUnblockRoom() {
        unblock_room.visibility = View.GONE
    }

//---------------------------------------------------------------------------------------visibility of end meeting time slots----------------------------------------------------


//--------------------------------------------------------------------------------visibility for extend time slot -------------------------------------------------

    private fun setVisibilityToGoneForExtendMin30() {
        extend_min_30.visibility = View.GONE
    }

    private fun setVisibilityToGoneForExtendMin45() {
        extend_min_45.visibility = View.GONE
    }

    private fun setVisibilityToGoneForExtendMin60() {
        extend_min_60.visibility = View.GONE
    }


    // make visibility to visible for all booking time slots
    private fun setVisibilityToVisibleForAllTimeForExtendSlots() {
        extend_min_15.visibility = View.VISIBLE
        extend_min_30.visibility = View.VISIBLE
        extend_min_45.visibility = View.VISIBLE
        extend_min_60.visibility = View.VISIBLE
    }


//---------------------------------------------------------------------------------------change status of room------------------------------------------------------------------

    private fun changeStatusToAvailable() {
        status_of_room.text = getString(R.string.available)
    }

    private fun changeStatusToOccupied() {
        status_of_room.text = getString(R.string.occupied)
    }

    private fun changeStatusToUnderMaintenance() {
        status_of_room.text = getString(R.string.under_maintenance)
    }


//---------------------------------------------------------------------------------------api call periodically-------------------------------------------------------------------

    /**
     * function will make request for refreshed data from server in each 30 seconds with ExecutorService
     */
    private fun makeRequestPeriodically() {
        val scheduler = Executors.newScheduledThreadPool(1)
        val makeCallPeriodically = Runnable {
            getViewModel()
        }
        scheduler.scheduleAtFixedRate(makeCallPeriodically, 0, 30, TimeUnit.SECONDS)
    }

    /**
     *  call api to get the updated data
     */
    private fun getViewModel() {
        mBookingForTheDayViewModel.getBookingList(roomId)
    }

    //-----------------------------------------------------------------------------------click listener on image view for feedback ------------------------------------------------

    /**
     * function invoked when user select negative feedback icon
     */
    fun badFeedback(view: View) {
        setDefaultImageForFeedback()
        img_1.setImageResource(R.drawable.ic_rate_done_2)
        feedback_edit_text.hint = getString(R.string.bad_feedback_placeholder)
        feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
            feedback_edit_text.text.toString().trim()
        } else {
            getString(R.string.bad_feedback_default_messgae)
        }
        feedbackRating = 1
    }

    /**
     * function invoked when user select average feedback icon
     */
    fun averageFeedback(view: View) {
        setDefaultImageForFeedback()
        img_2.setImageResource(R.drawable.ic_rate_done_3)
        feedback_edit_text.hint = getString(R.string.average_feedback_placehoolder)
        feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
            feedback_edit_text.text.toString().trim()
        } else {
            getString(R.string.average_feedback_default_message)
        }
        feedbackRating = 2
    }

    /**
     * function invoked when user select positive feedback icon
     */
    fun goodFeedback(view: View) {
        setDefaultImageForFeedback()
        img_3.setImageResource(R.drawable.ic_rate_done_5)
        feedback_edit_text.hint = getString(R.string.good_feedback_placeholder)
        feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
            feedback_edit_text.text.toString().trim()
        } else {
            getString(R.string.good_feedback_default_message)
        }
        feedbackRating = 3
    }

    /**
     * load default icons for feedback
     */
    private fun setDefaultImageForFeedback() {
        img_1.setImageResource(R.drawable.ic_rate_2)
        img_2.setImageResource(R.drawable.ic_rate_3)
        img_3.setImageResource(R.drawable.ic_rate_5)
    }

    private fun validateFeedbackCommentEditText(message: String): Boolean {
        return message.isNotEmpty()
    }


//---------------------------------------------------------------------------------------observe data for api response-------------------------------------------------------------


    private fun observeData() {
        observeDataForBookingListForTheDay()
        observeDataForNewBooking()
        observerDataForEndMeeting()
        observeDataForExtendMeeting()
        observeDateForStartMeeting()
        observeDataForFeedback()
        observeDataForUnblockRoom()
    }


    /**
     * Schedule for the day (Booking list from server)
     */
    private fun observeDataForBookingListForTheDay() {
        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
            mBookingList.clear()
            mProgressDialog.dismiss()
            changeDateTimeZone(it)
        })
        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
            ShowToast.show(this, it as Int)
        })
    }
    // observe data for end meeting
    private fun observerDataForEndMeeting() {
        // positive response from server for end meeting
        mBookingForTheDayViewModel.returnSuccessForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            isMeetingRunning = false
            mCountDownTimer!!.cancel()
            mTimeLeftInMillis = 0
            makeVisibilityGoneForMainLayout()
            makeVisibilityGoneForEndMeetingMainLayout()
            makeVisibilityVisibleForFeedbackLayout()
            setVisibilityToGoneForEndMeeting()
            setVisibilityToGoneForExtendMeeting()
            setVisibilityToGoneForStartMeeting()
            changeStatusToAvailable()
            setGradientToAvailable()
            getViewModel()
            mMeetingIdForFeedback = mRunningMeeting.bookingId!!

        })
        // negative response from server for end meeting
        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityGoneForEndMeetingMainLayout()
            makeVisibilityVisibleForMainLayout()
            ShowToast.show(this, it as Int)
        })
    }

    /**
     * after booking or back button pressed just clear the passcode and end time entered by user
     */
    private fun clearTextAndEditTextAfterBooking() {
        passcode_edit_text.text.clear()
        end_time_text_view.text = ""
        passcode_error_message.visibility = View.GONE
        end_time_error_label.visibility = View.GONE
    }

    // add new booking observer
    private fun observeDataForNewBooking() {
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForBooking().observe(this, Observer {
            getViewModel()
            makeVisibilityGoneForBookMeetingMainLayout()
            makeVisibilityVisibleForMainLayout()
            clearTextAndEditTextAfterBooking()
            Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForBooking().observe(this, Observer {
            mProgressDialog.dismiss()
            when (it) {
                Constants.UNAUTHERISED -> showToastAtTop(getString(R.string.incorrect_passcode))
                Constants.UNAVAILABLE_SLOT -> showToastAtTop(getString(R.string.slot_unavailable))
                else -> {
                    ShowToast.show(this, it as Int)
                    makeVisibilityGoneForBookMeetingMainLayout()
                    makeVisibilityVisibleForMainLayout()
                    clearTextAndEditTextAfterBooking()
                }
            }
        })
    }

    // extend meeting
    private fun observeDataForExtendMeeting() {
        mBookingForTheDayViewModel.returnBookingUpdated().observe(this, Observer {
            mProgressDialog.dismiss()
            isMeetingRunning = false
            isCommingFromExtendedMeeting = true
            mCountDownTimer!!.cancel()
            mTimeLeftInMillis = 0
            makeVisibilityGoneForExtendMeetingMainLayout()
            loadDefaultTimeSlotForExtendMeeting()
            makeVisibilityVisibleForMainLayout()
            Toasty.success(this, getString(R.string.meeting_time_extended), Toast.LENGTH_SHORT, true).show()
            getViewModel()
        })
        mBookingForTheDayViewModel.returnUpdateFailed().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityGoneForExtendMeetingMainLayout()
            makeVisibilityVisibleForMainLayout()
            loadDefaultTimeSlotForExtendMeeting()
            ShowToast.show(this, it as Int)
        })
    }

    //start meeting observer
    private fun observeDateForStartMeeting() {
        // positive response for start meeting
        mBookingForTheDayViewModel.returnSuccessForStartMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            setVisibilityToGoneForStartMeeting()
            setVisibilityToVisibleForEndMeeting()
            setVisibilityToVisibleForExtendMeeting()
            //setMeetingStartedData(true)
            isMeetingRunning = true
            startTimer(
                getMeetingDurationInMilliseonds(
                    mRunningMeeting.toTime!!
                )
            )
        })
        // negative response for start meeting
        mBookingForTheDayViewModel.returnFailureForStartMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(this, it as Int)
        })
    }

    // feedback response from server
    private fun observeDataForFeedback() {
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
            setDefaultImageForFeedback()
            makeVisibilityGoneForFeedbackLayout()
            makeVisibilityVisibleForMainLayout()
            feedback_edit_text.text.clear()
            getViewModel()
        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(this, it as Int)
            makeVisibilityGoneForFeedbackLayout()
            makeVisibilityVisibleForMainLayout()
            feedback_edit_text.text.clear()
        })
    }

    // unblock room observer
    private fun observeDataForUnblockRoom() {
        /**
         * observing data for Unblocking
         */
        mBookingForTheDayViewModel.returnSuccessCodeForUnBlockRoom().observe(this, Observer {
            mProgressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_unblocked), Toast.LENGTH_SHORT, true).show()
            isMeetingRunning = false
            mCountDownTimer!!.cancel()
            mTimeLeftInMillis = 0
            changeStatusToAvailable()
            setGradientToAvailable()
            setVisibilityToGoneForEndMeeting()
            setVisibilityToGoneForExtendMeeting()
            setVisibilityToGoneForStartMeeting()
            setVisibilityToGoneForUnblockRoom()
            getViewModel()
            makeVisibilityVisibleForMainLayout()
            makeVisibilityGoneForUnblockRoomLayout()
        })
        mBookingForTheDayViewModel.returnFailureCodeForUnBlockRoom().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityVisibleForMainLayout()
            makeVisibilityGoneForUnblockRoomLayout()
            ShowToast.show(this, it as Int)
        })
    }

    //----------------------------------------------------------------------------------------Set gradient for available room -----------------------------------------------

    /**
     * function will set background gradient to green color (Available)
     */
    private fun setGradientToAvailable() {
        booking_details_layout.background = resources.getDrawable(R.drawable.gradiant_for_available_room)
    }

    /**
     * function will set background gradient to orange color(Occupied)
     */
    private fun setGradientToOccupied() {
        booking_details_layout.background = resources.getDrawable(R.drawable.room_details_gradiant)
    }

//----------------------------------------------------------------------------------------change time format for all bookings for the day -----------------------------------------------

    /**
     * change start time and end time of meeting from UTC to Indian standard time zone
     */
    private fun changeDateTimeZone(it: List<BookingDeatilsForTheDay>) {
        var startTimeInUtc: String
        var endTimeInUtc: String
        for (booking in it) {
            startTimeInUtc = booking.fromTime!!
            endTimeInUtc = booking.toTime!!
            booking.fromTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(
                "${startTimeInUtc.split("T")[0]} ${startTimeInUtc.split("T")[1]}"
            )
            booking.toTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(
                "${endTimeInUtc.split("T")[0]} ${endTimeInUtc.split("T")[1]}"
            )
        }
        mBookingList.addAll(it)
        mBookingListForSlotArrangment.clear()
        timeSlotList.clear()
        getTimeSlot()
        mBookingListForSlotArrangment.addAll(it)
        Log.i("--------local time", mBookingList.toString())
        makeList()
    }

    /**
     * function will return 15 minute time slot of complete 24 hours
     */
    @SuppressLint("SimpleDateFormat")
    private fun getTimeSlot() {
        val df = SimpleDateFormat(getString(R.string.format_in_hh_mm))
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


    private fun makeList() {
        finalSlotList.clear()
        var size = timeSlotList.size
        var index = 0
        while (index < size) {
            var flag = false
            var flagForRemoveItem = false
            val finalSlot = SlotFinalList()
            val finalSlotNew = SlotFinalList()
            var flagForNewSlot = false
            val timeIn12HourFormat = ConvertTimeTo12HourFormat.convert12(timeSlotList[index])
            finalSlot.slot = timeIn12HourFormat
            finalSlotNew.slot = timeIn12HourFormat
            for (itemIndex in mBookingListForSlotArrangment.indices) {
                finalSlot.meetingDuration = mBookingListForSlotArrangment[itemIndex].meetingDuration
                finalSlot.endTime =
                    ConvertTimeTo12HourFormat.convert12(mBookingListForSlotArrangment[itemIndex].toTime!!.split(" ")[1])
                finalSlot.organiser = mBookingListForSlotArrangment[itemIndex].organizer!!.split(" ")[0]
                val startTimeDifference = getMilliSecondDifference(
                    timeSlotList[index],
                    mBookingListForSlotArrangment[itemIndex].fromTime!!.split(" ")[1]
                )
                val endTimeDifference = getMilliSecondDifference(
                    timeSlotList[index],
                    mBookingListForSlotArrangment[itemIndex].toTime!!.split(" ")[1]
                )
                when {
                    startTimeDifference == zero -> {
                        finalSlot.isBooked = true
                        flag = true
                        finalSlot.status = getString(R.string.start)
                        if (mBookingListForSlotArrangment[itemIndex].meetingDuration == getString(R.string.for_15_minutes)) {
                            flagForNewSlot = false
                            flagForRemoveItem = true
                        }
                    }
                    endTimeDifference == zero -> {
                        finalSlot.isBooked = true
                        flagForNewSlot = true
                        finalSlot.status = getString(R.string.end)
                        flag = true
                        flagForRemoveItem = true
                    }
                    startTimeDifference < 0 && endTimeDifference > 0 -> {
                        finalSlot.isBooked = true
                        flag = true
                        finalSlot.status = getString(R.string.middle_slot)
                    }
                    else -> {
                        finalSlot.isBooked = false
                    }
                }
                if (flag) {
                    if (flagForRemoveItem) {
                        mBookingListForSlotArrangment.remove(mBookingListForSlotArrangment[itemIndex])
                    }
                    break
                }
            }
            if (checkTimeInFuture(timeSlotList[index])) {
                if (!finalSlot.isBooked!!) {
                    finalSlot.status = getString(R.string.past)
                }
            }
            finalSlotList.add(finalSlot)
            if (flagForNewSlot) {
                timeSlotList.add(index + 1, timeSlotList[index])
                size += 1
            }
            index++
        }
        var position = 0
        // check for current time slot position
        for (index in timeSlotList.indices) {
            if (!checkTimeInFuture(timeSlotList[index])) {
                position = index
                break
            }
        }
        mBookingListAdapter.notifyDataSetChanged()
        scrollRecyclerViewToPostion(position)
    }

    /**
     * function will scroll recycler view till current time
     */
    private fun scrollRecyclerViewToPostion(position: Int) {
        val max = (96 - (position + 13))
        if (max > 10) {
            Handler().postDelayed({ recycler_view_todays_booking_list.scrollToPosition(position + 8) }, 200)
        } else {
            Handler().postDelayed({ recycler_view_todays_booking_list.scrollToPosition(position) }, 200)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMilliSecondDifference(timeSlot: String, bookingTime: String): Long {
        val sdf = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val timeSlotInDateObject = sdf.parse(timeSlot)
        val bookingTimeInDateObject = sdf.parse(bookingTime)
        return bookingTimeInDateObject.time - timeSlotInDateObject.time
    }

    // function will check whether the time slot is in past or in future
    @SuppressLint("SimpleDateFormat")
    private fun checkTimeInFuture(slotTime: String): Boolean {
        val simpleDateFormat = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val difference =
            simpleDateFormat.parse(simpleDateFormat.format(Date())).time - simpleDateFormat.parse(slotTime).time
        return difference >= 0
    }


//----------------------------------------------------------------------------------------load Available room Ui -----------------------------------------------

    // load layout for available room
    private fun loadAvailableRoomUi() {
        if (!isMeetingRunning) {
            visibilityToGoneForLayoutForNextMeeting()
        }
        changeStatusToAvailable()
        setGradientToAvailable()
        setVisibilityToGoneForUnblockRoom()
        setVisibilityToGoneForEndMeeting()
        setVisibilityToGoneForExtendMeeting()
        setVisibilityToGoneForStartMeeting()
    }

    // hide layout for next meeting
    private fun visibilityToGoneForLayoutForNextMeeting() {
        line.visibility = View.GONE
        meeting_details_relative_layout.visibility = View.GONE
    }

    // un hide layout for next meeting
    private fun visibilityToVisibleForLayoutForNextMeeting() {
        line.visibility = View.VISIBLE
        meeting_details_relative_layout.visibility = View.VISIBLE
    }


//-----------------------------------------------------------------------------------------Show Toast at top ---------------------------------------------------------------------------------


    /**
     * custom toast code
     */
    private fun showToastAtTop(message: String) {
        val toast =
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        val toastContentView = toast!!.view as LinearLayout
        val group = toast.view as ViewGroup
        val messageTextView = group.getChildAt(0) as TextView
        messageTextView.textSize = 24F
        val imageView = ImageView(applicationContext)
        imageView.setImageResource(R.drawable.ic_layers)
        toastContentView.addView(imageView, 0)
        toast.show()
    }

//-----------------------------------------------------------------------------------------Click Listener on new booking slots ---------------------------------------------------------------------------------

    private fun setClickListenerOnExtendMeetingSlots() {

        extend_min_15.setOnClickListener {
            loadDefaultSlot()
            extend_min_15.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_15.setTextColor(Color.parseColor("#F4733F"))
            mDurationForExtendBooking = 15
        }

        extend_min_30.setOnClickListener {
            loadDefaultSlot()
            extend_min_30.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_30.setTextColor(Color.parseColor("#F4733F"))
            mDurationForExtendBooking = 30
        }

        extend_min_45.setOnClickListener {
            loadDefaultSlot()
            extend_min_45.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_45.setTextColor(Color.parseColor("#F4733F"))
            mDurationForExtendBooking = 45
        }
        extend_min_60.setOnClickListener {
            loadDefaultSlot()
            extend_min_60.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_60.setTextColor(Color.parseColor("#F4733F"))

            mDurationForExtendBooking = 60
        }
    }

    private fun loadDefaultTimeSlotForExtendMeeting() {

        loadDefaultSlot()
        extend_min_15.background = resources.getDrawable(R.drawable.duration_background_selected)
        extend_min_15.setTextColor(Color.parseColor("#F4733F"))
        mDurationForExtendBooking = 15
    }

    private fun loadDefaultSlot() {
        extend_min_15.background = resources.getDrawable(R.drawable.passcode_background)
        extend_min_30.background = resources.getDrawable(R.drawable.passcode_background)
        extend_min_45.background = resources.getDrawable(R.drawable.passcode_background)
        extend_min_60.background = resources.getDrawable(R.drawable.passcode_background)

        extend_min_15.setTextColor(Color.WHITE)
        extend_min_30.setTextColor(Color.WHITE)
        extend_min_45.setTextColor(Color.WHITE)
        extend_min_60.setTextColor(Color.WHITE)
    }

// -----------------------------------------------------------------------------------------validate passcode and text change listener--------------------------------------------------------------------------
    /**
     * add text change listener for the passcode
     */
    private fun textChangeListenerOnpasscodeEditText() {
        passcode_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                passcode_error_message.visibility = View.VISIBLE
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasscode()
            }
        })
    }

    /**
     * add text change listener for the to time edit text
     */
    private fun textChangeListenerOnEndTimeEditText() {
        end_time_text_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                end_time_error_label.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEndTime()
            }
        })
    }


    /**
     * validate all input fields
     */
    private fun validatePasscode(): Boolean {
        return if (passcode_edit_text.text.toString().trim().isEmpty()) {
            passcode_error_message.visibility = View.VISIBLE
            passcode_error_message.text = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = passcode_edit_text.text.toString()
            if (input.length < 6) {
                passcode_error_message.visibility = View.VISIBLE
                passcode_error_message.text = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                passcode_error_message.visibility = View.GONE
                true
            }
        }
    }

    /**
     * validate end time textview
     */
    private fun validateEndTime(): Boolean {
        val input = end_time_text_view.text.toString().trim()
        return if (input.isEmpty()) {
            end_time_error_label.visibility = View.VISIBLE
            false
        } else {
            end_time_error_label.visibility = View.GONE
            true
        }
    }


//------------------------------------------------------------------------------------------all api call request ----------------------------------------------------------------------------------------------------
    // add new Booking api call
    private fun addBookingDetails(mBooking: NewBookingInput) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addBookingDetails(mBooking)
    }

    // api call to make end meeting request
    private fun endMeetingNow(mEndMeeting: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.endMeeting(mEndMeeting)
    }

    // extend meeting api call
    private fun makeCallToUpdateTimeForBooking(mUpdateBooking: UpdateBooking) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.updateBookingDetails(mUpdateBooking)
    }

    // start meeting
    private fun makeCallForStartMeeting(startNow: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.startMeeting(startNow)
    }

    // make call to add feedback for the meeting
    private fun makeCallForAddFeedback(feedback: Feedback) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addFeedback(feedback)
    }

    /**
     * function calls the ViewModel of Unblock
     */
    private fun unblockRoomCall(mBookingId: Int) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.unBlockRoom(mBookingId)
    }

//----------------------------------------------------------------------------------------End Meeting Functionality ------------------------------------------------------------------------------------------------

    // end meeting before time
    private fun handleEndNowButtonClick() {
        mCountDownTimer!!.cancel()
        mTimeLeftInMillis = 0
        val endMeeting = EndMeeting()
        endMeeting.bookingId = mRunningMeetingId
        endMeeting.status = false
        endMeeting.currentTime = FormatTimeAccordingToZone.formatDateAsUTC(getDurationInMultipleOf15())
        endMeetingNow(endMeeting)
    }

    //-------------------------------------------------------------------------------------End meeting before completion so send the end time in the multiple of 15 -------------------------------------------------------------------------------
    @SuppressLint("SimpleDateFormat")
    private fun getDurationInMultipleOf15(): String {
        val sdf = SimpleDateFormat("m")
        var n = sdf.format(Date()).toInt()
        if (n % 15 != 0) {
            n += if ((0 < n) && (n < 15)) {
                (15 - n)

            } else if ((15 < n) && (n < 30)) {
                (30 - n)

            } else if ((30 < n) && (n < 45)) {
                (45 - n)
            } else {
                (60 - n)
            }
        }
        return getNewExtendedEndTime(n)
    }

    // extend meeting duration
    @SuppressLint("SimpleDateFormat")
    private fun getNewExtendedEndTime(duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-mm-dd")
        val timeFormat = SimpleDateFormat("HH:mm")
        val timeFormatInHHMM = SimpleDateFormat("HH")
        val cal = Calendar.getInstance()
        val hours = timeFormatInHHMM.format(Date())
        cal.time = timeFormatInHHMM.parse(hours)
        cal.add(Calendar.MINUTE, duration)
        return "${dateFormat.format(Date())} ${timeFormat.format(cal.time)}"
    }


    //-------------------------------------------------------------------------------------Timer code for running meeting duration -------------------------------------------------------------------------------

    /**
     * function will set the timer for a duration
     */
    private fun startTimer(duration: Long) {
        mTimeLeftInMillis = duration
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
                mTimerRunning = false
                isMeetingRunning = false
                changeStatusToAvailable()
                setGradientToAvailable()
                if (mRunningMeeting.status != getString(R.string.blocked)) {
                    endMeetingNow(
                        EndMeeting(
                            mRunningMeetingId,
                            false,
                            FormatTimeAccordingToZone.formatDateAsUTC(mRunningMeeting.toTime!!)
                        )
                    )
                } else {
                    setVisibilityToGoneForUnblockRoom()
                }
            }
        }.start()
        mTimerRunning = true
    }
}

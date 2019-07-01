package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferencerommapp.utils.GetCurrentTimeInUTC
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.model.*
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import com.hsalf.smilerating.SmileRating
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking_details.*
import kotlinx.android.synthetic.main.end_meeting_layout.view.*
import kotlinx.android.synthetic.main.new_booking_layout.view.*
import kotlinx.android.synthetic.main.rating_bar_dialog.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class BookingDetailsActivity : AppCompatActivity() {

//    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
//    private lateinit var mProgressDialog: ProgressDialog
//    private var mCountDownTimer: CountDownTimer? = null
//    private lateinit var mRunningBookingLayout: RelativeLayout
//    private lateinit var mBookNowLayout: RelativeLayout
//    private lateinit var startMeetingButton: Button
//    private lateinit var endMeetingButton: Button
//    private lateinit var extendMeetingButton: Button
//    private var mBookingList = ArrayList<BookingDeatilsForTheDay>()
//    private var mNextMeeting = BookingDeatilsForTheDay()
//    private var mRunningMeeting = BookingDeatilsForTheDay()
//    private var mRunningMeetingId = -1
//    private var mTimerRunning: Boolean = false
//    private var mTimeLeftInMillis: Long = 0
//    private var isCommingFromExtendedMeeting = false
//    var isMeetingRunning = false
//    private var roomId = -1
//    private var buildingId = -1
//    private var flag = false
//    private var isNextMeetingPresent = false
//    private var mMeetingIdForFeedback = -1
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_booking_details)
//        if (GetPreference.getBuildingIdFromSharedPreference(this) == -1) {
//            startActivity(Intent(this, SettingBuildingConferenceActivity::class.java))
//            finish()
//        }
//        setTimeToScreen()
//        init()
//        observeData()
//        makeRequestPeriodically()
//        observeTimeFromBookingList()
//    }
//
//    private fun init() {
//        initStatusBar()
//        initFieldsOfUi()
//        initlateInitFields()
//        setRoomDetails()
//        setTimeToScreen()
//        setValuesFromSharedPreference()
//    }
//
//    /**
//     * function will remove the status bar from activity
//     */
//    private fun initStatusBar() {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        actionBar?.hide()
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//    }
//
//    /**
//     *  function will map local variable to UI fields
//     */
//    private fun initFieldsOfUi() {
//        mRunningBookingLayout = findViewById(R.id.running_booking_layout)
//        mBookNowLayout = findViewById(R.id.book_now_layout)
//        startMeetingButton = findViewById(R.id.start_button)
//        endMeetingButton = findViewById(R.id.end_meeting_button)
//        extendMeetingButton = findViewById(R.id.extend_button)
//    }
//
//    /**
//     * initialize lateinit fields
//     */
//    private fun initlateInitFields() {
//        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
//        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
//    }
//
//    private fun setRoomDetails() {
//        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
//        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
//        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
//        if (
//            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
//            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
//            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
//        ) {
//           goForSetup()
//        } else {
//            room_name.text = "$roomName [$roomCapacity people]"
//            building_name.text = buildingName
//        }
//    }
//
//    private fun goForSetup() {
//        startActivity(Intent(this@BookingDetailsActivity, SettingBuildingConferenceActivity::class.java))
//        finish()
//    }
//
//    private fun setValuesFromSharedPreference() {
//        roomId = GetPreference.getRoomIdFromSharedPreference(this)
//        buildingId = GetPreference.getBuildingIdFromSharedPreference(this)
//    }
//
//    // change start time and end time of meeting from UTC to Indian standard time zone
//    private fun changeDateTimeZone(it: List<BookingDeatilsForTheDay>) {
//        var startTimeInUtc: String
//        var endTimeInUtc: String
//        for (booking in it) {
//            startTimeInUtc = booking.fromTime!!
//            endTimeInUtc = booking.toTime!!
//            booking.fromTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${startTimeInUtc.split("T")[0]} ${startTimeInUtc.split("T")[1]}")
//            booking.toTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${endTimeInUtc.split("T")[0]} ${endTimeInUtc.split("T")[1]}")
//        }
//        mBookingList.addAll(it)
//    }
//
//    /**
//     * all observer for LiveData
//     */
//    private fun observeData() {
//        // get list of bookings for the day
//        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
//            mBookingList.clear()
//            changeDateTimeZone(it)
//
//        })
//        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
//            Toast.makeText(this, "" + it.toString(), Toast.LENGTH_SHORT).show()
//            ShowToast.show(this, it as Int)
//
//        })
//
//        // positive response from server for end meeting
//        mBookingForTheDayViewModel.returnSuccessForEndMeeting().observe(this, Observer {
//            mProgressDialog.dismiss()
//            isMeetingRunning = false
//            loadAvailableRoomUi()
//            getViewModel()
//            mMeetingIdForFeedback = mRunningMeeting.bookingId!!
//            showDialog()
//        })
//        // negative response from server for end meeting
//        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, Observer {
//            mProgressDialog.dismiss()
//            ShowToast.show(this, it as Int)
//        })
//
//        // positive response for start meeting
//        mBookingForTheDayViewModel.returnSuccessForStartMeeting().observe(this, Observer {
//            mProgressDialog.dismiss()
//            //setMeetingStartedData(true)
//            startMeetingButton.visibility = View.GONE
//            setVisibilityToVisibleForRunningMeeting()
//            isMeetingRunning = true
//            startTimer(
//                getMeetingDurationInMilliseonds(
//                    mRunningMeeting.toTime!!
//                )
//            )
//        })
//        // negative response for start meeting
//        mBookingForTheDayViewModel.returnFailureForStartMeeting().observe(this, Observer {
//            mProgressDialog.dismiss()
//            ShowToast.show(this, it as Int)
//        })
//
//        // add new booking observer
//        // positive response from server
//        mBookingForTheDayViewModel.returnSuccessForBooking().observe(this, Observer {
//            mProgressDialog.dismiss()
//            getViewModel()
//            Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show();
//
//        })
//        // negative response from server
//        mBookingForTheDayViewModel.returnFailureForBooking().observe(this, Observer {
//            mProgressDialog.dismiss()
//            ShowToast.show(this, it as Int)
//        })
//
//        // extend meeting
//        mBookingForTheDayViewModel.returnBookingUpdated().observe(this, Observer {
//            mProgressDialog.dismiss()
//            isMeetingRunning = false
//            isCommingFromExtendedMeeting = true
//            mCountDownTimer!!.cancel()
//            mTimeLeftInMillis = 0
//            Toasty.success(this, getString(R.string.meeting_time_extended), Toast.LENGTH_SHORT, true).show()
//            getViewModel()
//        })
//        mBookingForTheDayViewModel.returnUpdateFailed().observe(this, Observer {
//            mProgressDialog.dismiss()
//            ShowToast.show(this, it as Int)
//        })
//
//        // feedback response from server
//        // positive response from server
//        mBookingForTheDayViewModel.returnSuccessForFeedback().observe(this, Observer {
//            mProgressDialog.dismiss()
//        })
//        // negative response from server
//        mBookingForTheDayViewModel.returnFailureForFeedback().observe(this, Observer {
//            mProgressDialog.dismiss()
//            ShowToast.show(this, it as Int)
//        })
//    }
//
//    private fun getViewModel() {
//        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
//            startActivity(Intent(this@BookingDetailsActivity, SettingBuildingConferenceActivity::class.java))
//            finish()
//        } else {
//            mBookingForTheDayViewModel.getBookingList(roomId)
//        }
//    }
//
//    private fun endMeeting(mEndMeeting: EndMeeting) {
//        mProgressDialog.show()
//        mBookingForTheDayViewModel.endMeeting(mEndMeeting)
//    }
//    // main functionality
//    private fun observeTimeFromBookingList() {
//        val meetingListThread = object : Thread() {
//            override fun run() {
//                try {
//                    while (!isInterrupted) {
//                        runOnUiThread {
//                            if (mBookingList.isNotEmpty()) {
//                                flag = false
//                                for (booking in mBookingList) {
//                                    val timeDifference = getMillisecondsDifference(booking.fromTime!!)
//                                    if (timeDifference > 0) {
//                                        mNextMeeting = booking
//                                        flag = true
//                                        isNextMeetingPresent = true
//                                        setNextMeetingDetails()
//                                        break
//                                    }
//                                }
//                                if (!flag) {
//                                    isNextMeetingPresent = false
//                                }
//                                if (!isNextMeetingPresent) {
//                                    setNextMeetingTextToFree()
//                                }
//                                for (booking in mBookingList) {
//                                    val startTimeInMillis = getMilliseconds(booking.fromTime!!)
//                                    val endTimeInMillis = getMilliseconds(booking.toTime!!)
//                                    if (System.currentTimeMillis() in startTimeInMillis..endTimeInMillis) {
//                                        if (!isMeetingRunning) {
//                                            mRunningMeetingId = booking.bookingId!!
//                                            mRunningMeeting = booking
//                                            setDataToUiForRunningMeeting(booking)
//                                            if (booking.status == getString(R.string.meeting_started)) {
//                                                startMeetingButton.visibility = View.GONE
//                                                setVisibilityToVisibleForRunningMeeting()
//                                                startTimer(getMeetingDurationInMilliseonds(booking.toTime!!))
//                                            } else if (booking.status == getString(R.string.booked)) {
//                                                startMeetingButton.visibility = View.VISIBLE
//                                            }
//                                        }
//                                        break
//                                    }
//                                }
//                            } else {
//                                isNextMeetingPresent = false
//                                setVisibilityToGoneForRunningMeeting()
//                                loadAvailableRoomUi()
//                                setNextMeetingTextToFree()
//                            }
//                        }
//                        sleep(1000)
//                    }
//                } catch (e: InterruptedException) {
//                    Log.i("-------------", e.message)
//                }
//            }
//        }
//        meetingListThread.start()
//
//    }
//
//    private fun makeCallForStartMeeting(startNow: EndMeeting) {
//        mProgressDialog.show()
//        mBookingForTheDayViewModel.startMeeting(startNow)
//    }
//
//    private fun makeCallForAddFeedback(feedback: Feedback) {
//        mProgressDialog.show()
//        mBookingForTheDayViewModel.addFeedback(feedback)
//    }
//
//    fun startMeeting(view: View) {
//        var startNow = EndMeeting()
//        startNow.status = true
//        startNow.bookingId = mRunningMeeting.bookingId
//        startNow.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
//        makeCallForStartMeeting(startNow)
//    }
//
//    @SuppressLint("SetTextI18n")
//    fun setDataToUiForRunningMeeting(booking: BookingDeatilsForTheDay) {
//        mBookNowLayout.visibility = View.GONE
//        mRunningBookingLayout.visibility = View.VISIBLE
//        setVisibilityToGoneForRunningMeeting()
//        val startTime = booking.fromTime!!.split(" ")[1]
//        val endTime = booking.toTime!!.split(" ")[1]
//        mRunningMeetingId = booking.bookingId!!
//        event_name_text_view.text = booking.purpose + " " + changeFormat(startTime) + " - " + changeFormat(endTime)
//        event_organizer_text_view.text = "Organized by ${booking.organizer}"
//        status_button.text = getString(R.string.occupied)
//    }
//
//    // set visibility to visible for running meeting layout
//    private fun setVisibilityToVisibleForRunningMeeting() {
//        endMeetingButton.visibility = View.VISIBLE
//        extendMeetingButton.visibility = View.VISIBLE
//    }
//
//    // set visibility to gone for running meeting layout
//    private fun setVisibilityToGoneForRunningMeeting() {
//        endMeetingButton.visibility = View.GONE
//        extendMeetingButton.visibility = View.GONE
//    }
//
//    // get time difference in milliseconds
//    fun getMillisecondsDifference(startTime: String): Long {
//        val date = startTime.split(" ")[0]
//        val startTime = startTime.split(" ")[1]
//        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
//        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
//        val currTime = System.currentTimeMillis()
//        return startTimeAndDateTimeInDateObject.time - currTime
//    }
//
//
//    // get time difference in milliseconds
//    @SuppressLint("SimpleDateFormat")
//    private fun getMillisecondsDifferenceForExtendMeeting(startTime: String): Long {
//        val date = startTime.split(" ")[0]
//        val startTimeForNextMeeting = startTime.split(" ")[1]
//        val endTimeForRunningMeeting = mRunningMeeting.toTime!!.split(" ")[1]
//        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
//        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTimeForNextMeeting")
//        val endTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $endTimeForRunningMeeting")
//        return startTimeAndDateTimeInDateObject.time - endTimeAndDateTimeInDateObject.time
//    }
//
//    fun getMilliseconds(startTime: String): Long {
//        val date = startTime.split(" ")[0]
//        val startTime = startTime.split(" ")[1]
//        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
//        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
//        return startTimeAndDateTimeInDateObject.time
//    }
//
//    private fun getMeetingDurationInMilliseonds(endTime: String): Long {
//        val date = endTime.split(" ")[0]
//        val toTime = endTime.split(" ")[1]
//        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
//        val endTimeAndDateInDateObject = simpleDateFormatForDate.parse("$date $toTime")
//        return endTimeAndDateInDateObject.time - System.currentTimeMillis()
//    }
//
//    /**
//     * function will set the timer for a duration
//     */
//    private fun startTimer(duration: Long) {
//        mTimeLeftInMillis = duration
//        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                mTimeLeftInMillis = millisUntilFinished
//            }
//
//            override fun onFinish() {
//                mTimerRunning = false
//                isMeetingRunning = false
//                setMeetingStartedData(false)
//                if (isCommingFromExtendedMeeting) {
//                    isCommingFromExtendedMeeting = false
//                } else {
//                    endMeeting(EndMeeting(mRunningMeetingId, false, getCurrentTime()))
//                }
//            }
//        }.start()
//        mTimerRunning = true
//    }
//
//    private fun loadAvailableRoomUi() {
//        status_button.text = "Available"
//        mRunningBookingLayout.visibility = View.GONE
//        mBookNowLayout.visibility = View.VISIBLE
//    }
//
//    fun endMeetingBeforeCompletion(view: View) {
//        if (mTimerRunning && mCountDownTimer != null) {
//            handleEndNowButtonClick()
//        }
//    }
//
//    // end meeting before time
//    private fun handleEndNowButtonClick() {
//        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
//        val view = layoutInflater.inflate(R.layout.end_meeting_layout, null)
//        alertDialog.setView(view)
//        val dialog = alertDialog.create()
//        dialog.show()
//        view.yes_text_view.setOnClickListener {
//            mCountDownTimer!!.cancel()
//            mTimeLeftInMillis = 0
//            var endMeeting = EndMeeting()
//            endMeeting.bookingId = mRunningMeetingId
//            endMeeting.status = false
//            endMeeting.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
//            endMeeting(endMeeting)
//            dialog.dismiss()
//        }
//        view.no_text_view.setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    fun getCurrentTime(): String {
//        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
//        val cal = Calendar.getInstance()
//        cal.time = Date()
//        return dateTimeFormat.format(cal.time)
//    }
//
//    // extend running meeting duration upto next meeting
//    fun handleExtendMeetingButtonClick(view: View) {
//        var listItems = arrayOf<String>()
//        if (!isNextMeetingPresent) {
//            listItems = arrayOf("15 minutes", "30 minutes", "45 minutes", "60 minutes")
//        } else {
//            val difference = getMillisecondsDifferenceForExtendMeeting(mNextMeeting.fromTime!!)
//            //this will checked the item when user open the dialog
//            when {
//                difference >= (Constants.MILLIS_60) -> listItems =
//                    arrayOf("15 minutes", "30 minutes", "45 minutes", "60 minutes")
//                difference >= (Constants.MILLIS_45) -> listItems = arrayOf("15 minutes", "30 minutes", "45 minutes")
//                difference >= (Constants.MILLIS_30) -> listItems = arrayOf("15 minutes", "30 minutes")
//                difference >= (Constants.MILLIS_15) -> listItems = arrayOf("15 minutes")
//            }
//        }
//        if (listItems.isNotEmpty()) {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle(getString(R.string.choose_duration))
//            val checkedItem = 0 //this will checked the item when user open the dialog
//            builder.setSingleChoiceItems(
//                listItems, checkedItem
//            ) { dialog, which ->
//                getExtendedTimeDuration(listItems[which])
//                dialog.dismiss()
//            }
//            val dialog = builder.create()
//            dialog.show()
//        } else {
//            showToastAtTop(getString(R.string.cant_extend))
//        }
//    }
//
//    private fun getExtendedTimeDuration(extendedTime: String) {
//        var time = extendedTime.split(" ")[0].toInt()
//        var duration = 0
//        when (time) {
//            Constants.MIN_15 -> duration = Constants.MIN_15
//            Constants.MIN_30 -> duration = Constants.MIN_30
//            Constants.MIN_45 -> duration = Constants.MIN_45
//            Constants.MIN_60 -> duration = Constants.MIN_60
//        }
//        val mUpdateMeeting = UpdateBooking()
//        mUpdateMeeting.newStartTime = FormatTimeAccordingToZone.formatDateAsUTC(mRunningMeeting.fromTime!!)
//        mUpdateMeeting.bookingId = mRunningMeetingId
//        mUpdateMeeting.newtotime = FormatTimeAccordingToZone.formatDateAsUTC(getNewExtendedEndTime(mRunningMeeting.toTime!!, duration))
//        makeCallToUpdateTimeForBooking(mUpdateMeeting)
//    }
//
//    private fun makeCallToUpdateTimeForBooking(mUpdateBooking: UpdateBooking) {
//        mProgressDialog.show()
//        mBookingForTheDayViewModel.updateBookingDetails(mUpdateBooking)
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun setNextMeetingDetails() {
//        next_meeting_details.text =
//            changeFormat(mNextMeeting.fromTime!!.split(" ")[1]) + " - " + changeFormat(mNextMeeting.toTime!!.split(" ")[1]) + " " + mNextMeeting.purpose
//    }
//
//    private fun changeFormat(time: String): String {
//        var simpleDateFormat = SimpleDateFormat("HH:mm:ss")
//        var simpleDateFormat1 = SimpleDateFormat("HH:mm")
//        return simpleDateFormat1.format(simpleDateFormat.parse(time))
//    }
//
//    private fun showDialogForBooking() {
//        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        var view = layoutInflater.inflate(R.layout.new_booking_layout, null)
//        setVisibilityToVisibleOfRadioButtons(view)
//        textChangeListenerOnPurposeEditText(view)
//        textChangeListenerOnpasscodeEditText(view)
//        if (isNextMeetingPresent) {
//            val difference = getMillisecondsDifference(mNextMeeting.fromTime!!)
//            when {
//                difference >= (Constants.MILLIS_45) -> {
//                    view.radio_min_60.visibility = View.GONE
//                }
//                difference >= (Constants.MILLIS_30) -> {
//                    view.radio_min_45.visibility = View.GONE
//                    view.radio_min_60.visibility = View.GONE
//                }
//                difference >= (Constants.MILLIS_15) -> {
//                    view.radio_min_60.visibility = View.GONE
//                    view.radio_min_45.visibility = View.GONE
//                    view.radio_min_30.visibility = View.GONE
//                }
//                // add else case, I don't know what to do here
//            }
//            if (difference < (Constants.MILLIS_15)) {
//                showToastAtTop(getString(R.string.cant_book))
//                return
//            }
//        }
//        builder.setView(view)
//        builder.setCancelable(false)
//        val dialog = builder.create()
//        view.book.setOnClickListener {
//            if (validate(view)) {
//                // get values for meeting
//                var mLocalBookingInput = NewBookingInput()
//                mLocalBookingInput.passcode = view.edit_text_passcode.text.toString()
//                mLocalBookingInput.eventName = view.edit_text_event_name.text.toString()
//                getMillisecondsFromSelectedRadioButton(
//                    view.radio_group.checkedRadioButtonId,
//                    view,
//                    mLocalBookingInput
//                )
//                dialog.dismiss()
//            }
//        }
//        view.clear_button.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    // book now meeeting
//    fun bookMeeting(view: View) {
//        showDialogForBooking()
//    }
//
//    private fun getMillisecondsFromSelectedRadioButton(
//        checkedRadioButtonId: Int,
//        view: View,
//        mLocalBookingInput: NewBookingInput
//    ) {
//        var duration = 0
//        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm")
//        val cal = Calendar.getInstance()
//        var selectedRadioButton = view.findViewById<RadioButton>(checkedRadioButtonId)
//        when (selectedRadioButton.text.split(" ")[0].toInt()) {
//            Constants.MIN_15 -> duration = Constants.MIN_15
//            Constants.MIN_30 -> duration = Constants.MIN_30
//            Constants.MIN_45 -> duration = Constants.MIN_45
//            Constants.MIN_60 -> duration = Constants.MIN_60
//        }
//        cal.time = Date()
//        cal.add(Calendar.MINUTE, duration)
//        val endTime = FormatTimeAccordingToZone.formatDateAsUTC(dateTimeFormat.format(cal.time))
//        mLocalBookingInput.endTime = endTime
//        mLocalBookingInput.startTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
//        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE || buildingId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
//            goForSetup()
//        } else {
//            mLocalBookingInput.roomId = roomId
//            mLocalBookingInput.buildingId = buildingId
//        }
//        addBookingDetails(mLocalBookingInput)
//    }
//
//    // make request in each 30 seconds with ExecutorService
//    private fun makeRequestPeriodically() {
//        val scheduler = Executors.newScheduledThreadPool(1)
//        val makeCallPeriodically = Runnable {
//            getViewModel()
//        }
//        scheduler.scheduleAtFixedRate(makeCallPeriodically, 0, 30, TimeUnit.SECONDS)
//    }
//
//    // show meetings for the day
//    fun showMeetings(view: View) {
//        startActivity(Intent(this@BookingDetailsActivity, ShowBookings::class.java))
//    }
//
//    private fun setVisibilityToVisibleOfRadioButtons(view: View) {
//        view.radio_min_60.visibility = View.VISIBLE
//        view.radio_min_45.visibility = View.VISIBLE
//        view.radio_min_30.visibility = View.VISIBLE
//        view.radio_min_15.visibility = View.VISIBLE
//    }
//
//    /**
//     * add text change listener for the purpose edit text
//     */
//    private fun textChangeListenerOnPurposeEditText(view: View) {
//        view.edit_text_event_name.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                // nothing here
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // nothing here
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                validatePurpose(view)
//            }
//        })
//    }
//
//    /**
//     * validate all input fields
//     */
//    private fun validatePurpose(view: View): Boolean {
//        return if (view.edit_text_event_name.text.toString().trim().isEmpty()) {
//            view.event_name_layout.error = getString(R.string.field_cant_be_empty)
//            false
//        } else {
//            view.event_name_layout.error = null
//            true
//        }
//    }
//
//    /**
//     * add text change listener for the passcode
//     */
//    private fun textChangeListenerOnpasscodeEditText(view: View) {
//        view.edit_text_passcode.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                // nothing here
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // nothing here
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                validatePasscode(view)
//            }
//        })
//    }
//
//    /**
//     * validate all input fields
//     */
//    private fun validatePasscode(view: View): Boolean {
//        return if (view.edit_text_passcode.text.toString().trim().isEmpty()) {
//            view.passcode_layout.error = getString(R.string.field_cant_be_empty)
//            false
//        } else {
//            val input = view.edit_text_passcode.text.toString()
//            if (input.length < 6) {
//                view.passcode_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
//                false
//            } else {
//                view.passcode_layout.error = null
//                true
//            }
//        }
//    }
//
//    /**
//     * check validation for all input fields
//     */
//    private fun validate(view: View): Boolean {
//
//        if (!validatePurpose(view) or !validatePasscode(view) or !validateRadioGroup(view)) {
//            return false
//        }
//        return true
//    }
//
//    /**
//     * validate all input fields
//     */
//    private fun validateRadioGroup(view: View): Boolean {
//        val radioButtonId = view.radio_group.checkedRadioButtonId
//        return if (radioButtonId == -1) {
//            Toast.makeText(this, "Select meeting duration", Toast.LENGTH_SHORT).show()
//            false
//        } else {
//            true
//        }
//    }
//
//    // set date and time on screen with another ExecutorService
//    private fun setTimeToScreen() {
//        val timeThread = object : Thread() {
//            override fun run() {
//                try {
//                    while (!isInterrupted) {
//                        runOnUiThread {
//                            val cal = Calendar.getInstance()
//                            val sdfForTime = SimpleDateFormat("hh:mm a")
//                            val sdfForDate = SimpleDateFormat("EEEE, MMMM d")
//                            Log.e("-------------",sdfForTime.format(cal.time))
//                            Log.e("-------------",sdfForDate.format(cal.time))
//                            time_text_view.text = sdfForTime.format(cal.time)
//                            date_text_view.text =  sdfForDate.format(cal.time)
//                        }
//                        sleep(1000)
//                    }
//                } catch (e: InterruptedException) {
//                    Log.i("-------------", e.message)
//                }
//            }
//        }
//        timeThread.start()
//    }
//
//    /**
//     * function will return system time and date
//     */
//    private fun getSystemTimeAndDate(): Pair<String, String> {
//        val cal = Calendar.getInstance()
//        cal.time = Date()
//        val date = System.currentTimeMillis()
//        val sdfForTime = SimpleDateFormat("hh:mm")
//        val sdfForDate = SimpleDateFormat("dd MMM yyyy")
//        return Pair(sdfForTime.format(cal.time), sdfForDate.format(date))
//    }
//
//    /**
//     * set time and date to UI
//     */
//    private fun setTimeAndDataToUI(time: String, date: String) {
//        time_text_view.text = time
//        date_text_view.text = date
//    }
//
//    // set next meeting text view to free
//    private fun setNextMeetingTextToFree() {
//        next_meeting_details.text = getString(R.string.free_for_the_day)
//    }
//
//    // extend meeting duration
//    private fun getNewExtendedEndTime(endTime: String, duration: Int): String {
//        var date = endTime.split(" ")[0]
//        var endTime = endTime.split(" ")[1]
//        val timeFormat = SimpleDateFormat("HH:mm:ss")
//        var d = timeFormat.parse(endTime)
//        val cal = Calendar.getInstance()
//        cal.time = d
//        cal.add(Calendar.MINUTE, duration)
//        val newEndTime = timeFormat.format(cal.time)
//        return "$date $newEndTime"
//    }
//
//    // add new Booking
//    private fun addBookingDetails(mBooking: NewBookingInput) {
//        mProgressDialog.show()
//        mBookingForTheDayViewModel.addBookingDetails(mBooking)
//    }
//
//    //  set values for Started to true in shared preference
//    private fun setMeetingStartedData(status: Boolean) {
//        getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).edit()
//            .putBoolean(getString(R.string.meeting_started), status).apply()
//    }
//
//    // feedback dialog
//    private fun showDialog() {
//        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        var view = layoutInflater.inflate(R.layout.rating_bar_dialog, null)
//        val smileRating = view.findViewById(R.id.smile_rating) as SmileRating
//        builder.setView(view)
//        builder.setCancelable(false)
//        val dialog = builder.create()
//        view.cancel_dialog.setOnClickListener {
//            dialog.dismiss()
//        }
//        view.submit_feedback.setOnClickListener {
//            val commentEditText = view.findViewById<EditText>(R.id.comment_edit_text)
//            var comment = ""
//            var rating = -1
//            comment = if (commentEditText.text.toString().trim().isEmpty()) {
//                getString(R.string.default_feedback)
//            } else {
//                commentEditText.text.toString().trim()
//            }
//            if (smileRating.selectedSmile == -1) {
//                Toast.makeText(this@BookingDetailsActivity, "Please give some rating", Toast.LENGTH_SHORT).show()
//            } else {
//                var feedback = Feedback()
//                rating = smileRating.selectedSmile + 1
//                feedback.bookingId = mMeetingIdForFeedback
//                feedback.comment = comment
//                feedback.rating = rating
//                makeCallForAddFeedback(feedback)
//            }
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    // show Toast
//    private fun showToastAtTop(message: String) {
//        var toast =
//            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.TOP, 0, 0)
//        val toastContentView = toast!!.view as LinearLayout
//        var group = toast.view as ViewGroup
//        var messageTextView = group.getChildAt(0) as TextView
//        messageTextView.textSize = 30F
//        var imageView = ImageView(applicationContext)
//        imageView.setImageResource(R.drawable.ic_layers)
//        toastContentView.addView(imageView, 0)
//        toast.show()
    }

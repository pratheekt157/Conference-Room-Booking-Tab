package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.utils.ConvertTimeTo12HourFormat
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.activity_booking_details.*
import kotlinx.android.synthetic.main.activity_booking_status.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.example.conferencerommapp.utils.GetCurrentTimeInUTC
import com.example.conferenceroomtabletversion.model.EndMeeting
import com.example.conferenceroomtabletversion.model.NewBookingInput
import com.example.conferenceroomtabletversion.model.UpdateBooking
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.end_meeting_layout.view.*
import kotlinx.android.synthetic.main.new_booking_layout.view.*

class ConferenceBookingActivity : AppCompatActivity() {

    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private var mCountDownTimer: CountDownTimer? = null
    private var mBookingList = ArrayList<BookingDeatilsForTheDay>()
    private var mNextMeeting = BookingDeatilsForTheDay()
    private var mRunningMeeting = BookingDeatilsForTheDay()
    private var mRunningMeetingId = -1
    private var mDurationForNewBooking = 15
    private var mDurationForExtendBooking = 15
    private var mTimerRunning: Boolean = false
    private var mTimeLeftInMillis: Long = 0
    private var isCommingFromExtendedMeeting = false
    var isMeetingRunning = false
    private var roomId = -1
    private var buildingId = -1
    private var flag = false
    private var isNextMeetingPresent = false
    private var mMeetingIdForFeedback = -1


    private lateinit var mEndMeetingMainRelativeLayout: RelativeLayout
    private lateinit var mBookMeetingMainRelativeLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_status)
        init()
    }

    private fun init() {
        initStatusBar()
        initTextChangeListener()
        initLateInitFields()
        setClick()
        setClickListenerOnExtendMeetingSlots()
    }


    private fun initTextChangeListener() {
        textChangeListenerOnpasscodeEditText()
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
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
        mEndMeetingMainRelativeLayout = findViewById(R.id.end_meeting_main_layout)
        mBookMeetingMainRelativeLayout = findViewById(R.id.book_now_main_layout)
    }


    //---------------------------------------------------------------------------------------All button clicks in application-------------------------------------------------------------------

    /**
     * start meeting
     */
    fun startMeeting(view: View) {
        var startNow = EndMeeting()
        startNow.status = true
        startNow.bookingId = mRunningMeeting.bookingId
        startNow.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
        makeCallForStartMeeting(startNow)
    }

    fun bookNowMeeting(view: View) {
        bookMeeting()
    }

    fun confirmBookMeeting(view: View) {
        if (validatePasscode()) {
            var mLocalBookingInput = NewBookingInput()
            mLocalBookingInput.passcode = passcode_edit_text.text.toString()
            mLocalBookingInput.eventName = getString(R.string.local_booking)
            getMillisecondsFromSelectedRadioButton(mLocalBookingInput)
        }
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

    }

    // -------------------------------------------------------------------------------------Handle Extend meeting button click functionality------------------------------
    private fun handleExtendMeetingClick() {
        if (!isNextMeetingPresent) {
            setVisibilityToVisibleForAllTimeForExtendSlots()
        } else {
            val difference = getMillisecondsDifferenceForExtendMeeting(mNextMeeting.fromTime!!)
            //this will checked the item when user open the dialog
            when {
                difference >= (Constants.MILLIS_60) -> {

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
        makeVisibilityGoneForMainLayout()
        makeVisibilityVisibleForExtendMeetingMainLayout()
    }


//---------------------------------------------------------------------------------------book New Meeting -------------------------------------------------------------------------

    private fun bookMeeting() {
        setVisibilityToVisibleForAllTimeSlots()
        if (isNextMeetingPresent) {
            val difference = getMillisecondsDifference(mNextMeeting.fromTime!!)
            when {
                difference >= (Constants.MILLIS_45) -> {
                    setVisibilityToGoneForMin60()
                }
                difference >= (Constants.MILLIS_30) -> {
                    setVisibilityToGoneForMin45()
                    setVisibilityToGoneForMin60()
                }
                difference >= (Constants.MILLIS_15) -> {
                    setVisibilityToGoneForMin30()
                    setVisibilityToGoneForMin45()
                    setVisibilityToGoneForMin60()
                }
            }
            if (difference < (Constants.MILLIS_15)) {
                showToastAtTop(getString(R.string.cant_book))
                return
            }
        }
        // load layout for book meeting
        relative_main2.visibility = View.GONE
        makeVisibilityVisibilityForBookMeetingMainLayout()
    }

//------------------------------------------------------------------------------------- all code related to time in milliseconds ---------------------------------------------------

    // get time difference in milliseconds
    private fun getMillisecondsDifference(startTime: String): Long {
        val date = startTime.split(" ")[0]
        val startTime = startTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
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
    private fun getNewExtendedEndTime(endTime: String, duration: Int): String {
        var date = endTime.split(" ")[0]
        var endTime = endTime.split(" ")[1]
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        var d = timeFormat.parse(endTime)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, duration)
        val newEndTime = timeFormat.format(cal.time)
        return "$date $newEndTime"
    }

    fun getCurrentTime(): String {
        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
        val cal = Calendar.getInstance()
        cal.time = Date()
        return dateTimeFormat.format(cal.time)
    }

    private fun getMeetingDurationInMilliseonds(endTime: String): Long {
        val date = endTime.split(" ")[0]
        val toTime = endTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val endTimeAndDateInDateObject = simpleDateFormatForDate.parse("$date $toTime")
        return endTimeAndDateInDateObject.time - System.currentTimeMillis()
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


//---------------------------------------------------------------------------------------visibility of all action text views----------------------------------------------------

    private fun setVisibilityToGoneForBookMeeting() {
        book_meeting.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForBookMeeting() {
        book_meeting.visibility = View.VISIBLE
    }


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

//---------------------------------------------------------------------------------------visibility of end meeting time slots----------------------------------------------------

    private fun setVisibilityToGoneForMin30() {
        min_30.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForMin30() {
        min_30.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForMin15() {
        min_15.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForMin15() {
        min_15.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForMin45() {
        min_45.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForMin45() {
        min_45.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForMin60() {
        min_60.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForMin60() {
        min_60.visibility = View.VISIBLE
    }


//--------------------------------------------------------------------------------visibility for extend time slot -------------------------------------------------

    private fun setVisibilityToGoneForExtendMin30() {
        extend_min_15.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForExtendMin30() {
        extend_min_30.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForExtendMin15() {
        extend_min_15.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForExtendMin15() {
        extend_min_15.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForExtendMin45() {
        extend_min_45.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForExtendMin45() {
        extend_min_45.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForExtendMin60() {
        extend_min_60.visibility = View.GONE
    }

    private fun setVisibilityToVisibilityForExtendMin60() {
        extend_min_60.visibility = View.VISIBLE
    }

    // make visibility to visible for all booking time slots
    private fun setVisibilityToVisibleForAllTimeSlots() {
        min_15.visibility = View.VISIBLE
        min_30.visibility = View.VISIBLE
        min_45.visibility = View.VISIBLE
        min_60.visibility = View.VISIBLE
    }

    // make visibility to visible for all booking time slots
    private fun setVisibilityToVisibleForAllTimeForExtendSlots() {
        extend_min_15.visibility = View.VISIBLE
        extend_min_30.visibility = View.VISIBLE
        extend_min_45.visibility = View.VISIBLE
        extend_min_60.visibility = View.VISIBLE
    }


//---------------------------------------------------------------------------------------change status of room------------------------------------------------------------------

    private fun chnageStatusToAvailable() {
        status_of_room.text = getString(R.string.available)
    }

    private fun chnageStatusToOccupied() {
        status_of_room.text = getString(R.string.occupied)
    }


//---------------------------------------------------------------------------------------api call periodically-------------------------------------------------------------------

    // make request in each 30 seconds with ExecutorService
    private fun makeRequestPeriodically() {
        val scheduler = Executors.newScheduledThreadPool(1)
        val makeCallPeriodically = Runnable {
            getViewModel()
        }
        scheduler.scheduleAtFixedRate(makeCallPeriodically, 0, 30, TimeUnit.SECONDS)
    }

    //call api to get the updated data
    private fun getViewModel() {
        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
            startActivity(Intent(this@ConferenceBookingActivity, SettingBuildingConferenceActivity::class.java))
            finish()
        } else {
            mBookingForTheDayViewModel.getBookingList(roomId)
        }
    }


//---------------------------------------------------------------------------------------observe data for api response-------------------------------------------------------------

    fun observeData() {
        observeDataForBookingListForTheDay()
        observeDataForNewBooking()
        observerDataForEndMeeting()
        observeDataForExtendMeeting()
    }

    /**
     * Schedule for the day (Booking list from server)
     */
    private fun observeDataForBookingListForTheDay() {
        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
            mBookingList.clear()
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
            loadAvailableRoomUi()
            getViewModel()
            mMeetingIdForFeedback = mRunningMeeting.bookingId!!
            // load UI for feedback
        })
        // negative response from server for end meeting
        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityGoneForEndMeetingMainLayout()
            makeVisibilityVisibleForMainLayout()
            ShowToast.show(this, it as Int)
        })
    }

    // add new booking observer
    private fun observeDataForNewBooking() {

        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForBooking().observe(this, Observer {
            mProgressDialog.dismiss()
            getViewModel()
            makeVisibilityGoneForBookMeetingMainLayout()
            makeVisibilityGoneForMainLayout()
            Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForBooking().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityGoneForBookMeetingMainLayout()
            makeVisibilityGoneForMainLayout()
            ShowToast.show(this, it as Int)
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
            makeVisibilityVisibleForMainLayout()
            Toasty.success(this, getString(R.string.meeting_time_extended), Toast.LENGTH_SHORT, true).show()
            getViewModel()
        })
        mBookingForTheDayViewModel.returnUpdateFailed().observe(this, Observer {
            mProgressDialog.dismiss()
            makeVisibilityGoneForExtendMeetingMainLayout()
            makeVisibilityVisibleForMainLayout()
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


    //----------------------------------------------------------------------------------------Set gradient for available room -----------------------------------------------

    private fun setGradientToAvailable() {
        booking_details_layout.background = resources.getDrawable(R.drawable.gradiant_for_available_room)
    }

    private fun setGradiantToOccupied() {
        booking_details_layout.background = resources.getDrawable(R.drawable.room_details_gradiant)
    }

//----------------------------------------------------------------------------------------change time format for all bookings for the day -----------------------------------------------

    // change start time and end time of meeting from UTC to Indian standard time zone
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
    }

//----------------------------------------------------------------------------------------load Available room Ui -----------------------------------------------

    private fun loadAvailableRoomUi() {
        makeVisibilityVisibleForMainLayout()
        chnageStatusToAvailable()
        setGradientToAvailable()
        setVisibilityToGoneForEndMeeting()
        setVisibilityToGoneForExtendMeeting()
        setVisibilityToGoneForStartMeeting()
        setVisibilityToVisibleForBookMeeting()
    }

//----------------------------------------------------------------------------------------change layout for feedback meeting-----------------------------------------------

    private fun loadUiForFeedback() {
        makeVisibilityGoneForMainLayout()
        makeVisibilityVisibleForFeedbackLayout()
    }


//-----------------------------------------------------------------------------------------Show Toast at top ---------------------------------------------------------------------------------


    // show Toast
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

//-----------------------------------------------------------------------------------------Click Listener on new booking slots ---------------------------------------------------------------------------------

    private fun setClickListenerOnExtendMeetingSlots() {

        extend_min_15.setOnClickListener {
            extend_min_30.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_45.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_60.background = resources.getDrawable(R.drawable.passcode_background)

            extend_min_30.setTextColor(Color.WHITE)
            extend_min_45.setTextColor(Color.WHITE)
            extend_min_60.setTextColor(Color.WHITE)


            extend_min_15.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_15.setTextColor(Color.parseColor("#F4733F"))

            mDurationForExtendBooking = 15


        }

        extend_min_30.setOnClickListener {
            extend_min_15.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_45.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_60.background = resources.getDrawable(R.drawable.passcode_background)

            extend_min_15.setTextColor(Color.WHITE)
            extend_min_45.setTextColor(Color.WHITE)
            extend_min_60.setTextColor(Color.WHITE)


            extend_min_30.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_30.setTextColor(Color.parseColor("#F4733F"))
            mDurationForExtendBooking = 30
        }

        extend_min_45.setOnClickListener {
            extend_min_15.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_30.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_60.background = resources.getDrawable(R.drawable.passcode_background)

            extend_min_15.setTextColor(Color.WHITE)
            extend_min_30.setTextColor(Color.WHITE)
            extend_min_60.setTextColor(Color.WHITE)

            extend_min_45.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_45.setTextColor(Color.parseColor("#F4733F"))
            mDurationForExtendBooking = 45
        }

        extend_min_60.setOnClickListener {
            extend_min_15.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_30.background = resources.getDrawable(R.drawable.passcode_background)
            extend_min_45.background = resources.getDrawable(R.drawable.passcode_background)

            extend_min_15.setTextColor(Color.WHITE)
            extend_min_30.setTextColor(Color.WHITE)
            extend_min_45.setTextColor(Color.WHITE)

            extend_min_60.background = resources.getDrawable(R.drawable.duration_background_selected)
            extend_min_60.setTextColor(Color.parseColor("#F4733F"))

            mDurationForExtendBooking = 60
        }
    }

//--------------------------------------------------------------------------------------CLick Listener on extend meeting slots---------------------------------------------------------------------

    private fun setClick() {

        min_15.setOnClickListener {
            min_30.background = resources.getDrawable(R.drawable.passcode_background)
            min_45.background = resources.getDrawable(R.drawable.passcode_background)
            min_60.background = resources.getDrawable(R.drawable.passcode_background)

            min_30.setTextColor(Color.WHITE)
            min_45.setTextColor(Color.WHITE)
            min_60.setTextColor(Color.WHITE)


            min_15.background = resources.getDrawable(R.drawable.duration_background_selected)
            min_15.setTextColor(Color.parseColor("#058F65"))

            mDurationForNewBooking = 15


        }

        min_30.setOnClickListener {
            min_15.background = resources.getDrawable(R.drawable.passcode_background)
            min_45.background = resources.getDrawable(R.drawable.passcode_background)
            min_60.background = resources.getDrawable(R.drawable.passcode_background)

            min_15.setTextColor(Color.WHITE)
            min_45.setTextColor(Color.WHITE)
            min_60.setTextColor(Color.WHITE)


            min_30.background = resources.getDrawable(R.drawable.duration_background_selected)
            min_30.setTextColor(Color.parseColor("#058F65"))
            mDurationForNewBooking = 30
        }

        min_45.setOnClickListener {
            min_15.background = resources.getDrawable(R.drawable.passcode_background)
            min_30.background = resources.getDrawable(R.drawable.passcode_background)
            min_60.background = resources.getDrawable(R.drawable.passcode_background)

            min_15.setTextColor(Color.WHITE)
            min_30.setTextColor(Color.WHITE)
            min_60.setTextColor(Color.WHITE)

            min_45.background = resources.getDrawable(R.drawable.duration_background_selected)
            min_45.setTextColor(Color.parseColor("#058F65"))
            mDurationForNewBooking = 45
        }

        min_60.setOnClickListener {
            min_15.background = resources.getDrawable(R.drawable.passcode_background)
            min_30.background = resources.getDrawable(R.drawable.passcode_background)
            min_45.background = resources.getDrawable(R.drawable.passcode_background)

            min_15.setTextColor(Color.WHITE)
            min_30.setTextColor(Color.WHITE)
            min_45.setTextColor(Color.WHITE)

            min_60.background = resources.getDrawable(R.drawable.duration_background_selected)
            min_60.setTextColor(Color.parseColor("#058F65"))

            mDurationForNewBooking = 60
        }
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
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasscode()
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


    private fun getMillisecondsFromSelectedRadioButton(
        mLocalBookingInput: NewBookingInput
    ) {
        var duration = 0
        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm")
        val cal = Calendar.getInstance()
        when (mDurationForNewBooking) {
            Constants.MIN_15 -> duration = Constants.MIN_15
            Constants.MIN_30 -> duration = Constants.MIN_30
            Constants.MIN_45 -> duration = Constants.MIN_45
            Constants.MIN_60 -> duration = Constants.MIN_60
        }
        cal.time = Date()
        cal.add(Calendar.MINUTE, duration)
        val endTime = FormatTimeAccordingToZone.formatDateAsUTC(dateTimeFormat.format(cal.time))
        mLocalBookingInput.endTime = endTime
        mLocalBookingInput.startTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
        if (roomId == Constants.DEFAULT_INT_PREFERENCE_VALUE || buildingId == Constants.DEFAULT_INT_PREFERENCE_VALUE) {
            //goForSetup()
        } else {
            mLocalBookingInput.roomId = roomId
            mLocalBookingInput.buildingId = buildingId
        }
        addBookingDetails(mLocalBookingInput)
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

//----------------------------------------------------------------------------------------End Meeting Functionality ------------------------------------------------------------------------------------------------

    // end meeting before time
    private fun handleEndNowButtonClick() {
        mCountDownTimer!!.cancel()
        mTimeLeftInMillis = 0
        var endMeeting = EndMeeting()
        endMeeting.bookingId = mRunningMeetingId
        endMeeting.status = false
        endMeeting.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
        endMeetingNow(endMeeting)
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
                //setMeetingStartedData(false)
                if (isCommingFromExtendedMeeting) {
                    isCommingFromExtendedMeeting = false
                } else {
                    endMeetingNow(EndMeeting(mRunningMeetingId, false, getCurrentTime()))
                }
            }
        }.start()
        mTimerRunning = true
    }

}

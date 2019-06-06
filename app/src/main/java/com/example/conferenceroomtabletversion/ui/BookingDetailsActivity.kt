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
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.model.*
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking_details.*
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.new_booking_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingDetailsActivity : AppCompatActivity(), RatingDialogListener {
    override fun onNegativeButtonClicked() {
        // do nothing
    }

    override fun onNeutralButtonClicked() {
        // do nothing
    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addFeedback(Feedback(mMeetingIdForFeedback, rate, comment))
    }

    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning: Boolean = false
    private var mTimeLeftInMillis: Long = 0
    private var isCommingFromExtendedMeeting = false
    private lateinit var mRunningBookingLayout: RelativeLayout
    private lateinit var mBookNowLayout: RelativeLayout
    private lateinit var startMeetingButton: Button
    private lateinit var endMeetingButton: Button
    private lateinit var extendMeetingButton: Button
    private var mRunningMeetingId = -1
    var mBookingList = ArrayList<BookingDeatilsForTheDay>()
    var mNextMeeting = BookingDeatilsForTheDay()
    var isMeetingRunning = false
    var flag = false
    var isNextMeetingPresent = false
    var mRunningMeeting = BookingDeatilsForTheDay()
    var mMeetingIdForFeedback = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)
        setTimeToScreen()
        init()
//        use_now_meeting.setOnClickListener {
//            val addPhotoBottomDialogFragment =
//                AddPhotoBottomDialogFragment(object : AddPhotoBottomDialogFragment.SendNewBookingData {
//                    override fun sendData(name: String) {
//                        Toast.makeText(this@BookingDetailsActivity, name, Toast.LENGTH_SHORT).show()
//
//                    }
//                })
//            addPhotoBottomDialogFragment.show(
//                supportFragmentManager,
//
//                "add_photo_dialog_fragment"
//            )
//
//        }
        observeData()
        makeRequestPeriodically()
        observeTimeFromBookingList()
    }

    private fun init() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mRunningBookingLayout = findViewById(R.id.running_booking_layout)
        mBookNowLayout = findViewById(R.id.book_now_layout)
        startMeetingButton = findViewById(R.id.start_button)
        endMeetingButton = findViewById(R.id.end_meeting_button)
        extendMeetingButton = findViewById(R.id.extend_button)
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
        setTimeToScreen()
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {
        // get list of bookings for the day
        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
            mProgressDialog.dismiss()
            mBookingList.clear()
            mBookingList.addAll(it)
        })
        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
            mProgressDialog.dismiss()
            Toast.makeText(this, "" + it.toString(), Toast.LENGTH_SHORT).show()
            if (it == Constants.NO_CONTENT_FOUND) {
                //upcoming_empty_view.visibility = View.VISIBLE
                //r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            } else {
                //ShowToast.show(activity!!, it as Int)
            }
        })

        // positive response from server for end meeting
        mBookingForTheDayViewModel.returnSuccessForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            isMeetingRunning = false
            loadAvailableRoomUi()
            getViewModel()
            mMeetingIdForFeedback = mRunningMeeting.bookingId!!
            showDialog()
        })
        // negative response from server for end meeting
        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            Log.i("-----------failure", "" + it)
            if (it == Constants.NO_CONTENT_FOUND) {
                //upcoming_empty_view.visibility = View.VISIBLE
                //r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            } else {
                //ShowToast.show(activity!!, it as Int)
            }
        })

        // positive response for start meeting
        mBookingForTheDayViewModel.returnSuccessForStartMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            setMeetingStartedData(true)
            startMeetingButton.visibility = View.GONE
            setVisibilityToVisibleForRunningMeeting()
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
            Log.i("------failure for start", " " + it)
        })

        // add new booking observer
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForBooking().observe(this, Observer {
            mProgressDialog.dismiss()
            getViewModel()
            //Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show();
            //Start time for this booking
        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForBooking().observe(this, Observer {
            mProgressDialog.dismiss()
            Log.i("------failure for start", " " + it)
            // show toast
        })

        // extend meeting
        mBookingForTheDayViewModel.returnBookingUpdated().observe(this, Observer {
            mProgressDialog.dismiss()
            isMeetingRunning = false
            isCommingFromExtendedMeeting = true
            mCountDownTimer!!.cancel()
            mTimeLeftInMillis = 0
            Toasty.success(this, getString(R.string.meeting_time_extended), Toast.LENGTH_SHORT, true).show()
            getViewModel()
        })
        mBookingForTheDayViewModel.returnUpdateFailed().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(this, it as Int)
        })

        // feedback response from server
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(this, it as Int)
        })
    }

    private fun getViewModel() {
        mProgressDialog.show()
        mBookingForTheDayViewModel.getBookingList(22)
    }

    private fun endMeeting(mEndMeeting: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.endMeeting(mEndMeeting)
    }

    private fun observeTimeFromBookingList() {
        val meetingListThread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        runOnUiThread {
                            if (mBookingList.isNotEmpty()) {
                                flag = false
                                for (booking in mBookingList) {
                                    val timeDifference = getMillisecondsDifference(booking.fromTime!!)
                                    if (timeDifference > 0) {
                                        mNextMeeting = booking
                                        flag = true
                                        isNextMeetingPresent = true
                                        setNextMeetingDetails()
                                        break
                                    }
                                }
                                if (!flag) {
                                    isNextMeetingPresent = false
                                }
                                if (!isNextMeetingPresent) {
                                    setNextMeetingTextToFree()
                                }
                                for (booking in mBookingList) {
                                    val startTimeInMillis = getMilliseconds(booking.fromTime!!)
                                    val endTimeInMillis = getMilliseconds(booking.toTime!!)
                                    if (System.currentTimeMillis() in startTimeInMillis..endTimeInMillis) {
                                        if (!isMeetingRunning) {
                                            mRunningMeetingId = booking.bookingId!!
                                            mRunningMeeting = booking
                                            setDataToUiForRunningMeeting(booking)
                                            if (booking.status == getString(R.string.meeting_started)) {
                                                startMeetingButton.visibility = View.GONE
                                                setVisibilityToVisibleForRunningMeeting()
                                                startTimer(getMeetingDurationInMilliseonds(booking.toTime!!))
                                            } else if (booking.status == getString(R.string.booked)) {
                                                startMeetingButton.visibility = View.VISIBLE
                                            }
                                        }
                                        break
                                    }
                                }
                            } else {
                                setNextMeetingTextToFree()
                            }
                        }
                        sleep(1000)
                    }
                } catch (e: InterruptedException) {
                    Log.i("-------------", e.message)
                }
            }
        }
        meetingListThread.start()
    }

    private fun makeCallForStartMeeting(startNow: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.startMeeting(startNow)
    }

    fun startMeeting(view: View) {
        var startNow = EndMeeting()
        startNow.status = true
        startNow.bookingId = mRunningMeeting.bookingId
        startNow.currentTime = getCurrentTime()
        makeCallForStartMeeting(startNow)
    }

    @SuppressLint("SetTextI18n")
    fun setDataToUiForRunningMeeting(booking: BookingDeatilsForTheDay) {
        mBookNowLayout.visibility = View.GONE
        mRunningBookingLayout.visibility = View.VISIBLE
        setVisibilityToGoneForRunningMeeting()
        val startTime = booking.fromTime!!.split("T")[1]
        val endTime = booking.toTime!!.split("T")[1]
        mRunningMeetingId = booking.bookingId!!
        event_name_text_view.text = booking.purpose + " " + startTime + " - " + endTime
        event_organizer_text_view.text = "Organized by ${booking.organizer}"
        status_button.text = "Occupied"
    }

    private fun setVisibilityToVisibleForRunningMeeting() {
        endMeetingButton.visibility = View.VISIBLE
        extendMeetingButton.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForRunningMeeting() {
        endMeetingButton.visibility = View.GONE
        extendMeetingButton.visibility = View.GONE
    }

    // get time difference in milliseconds
    fun getMillisecondsDifference(startTime: String): Long {
        val date = startTime.split("T")[0]
        val startTime = startTime.split("T")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        val currTime = System.currentTimeMillis()
        return startTimeAndDateTimeInDateObject.time - currTime
    }


    // get time difference in milliseconds
    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondsDifferenceForExtendMeeting(startTime: String): Long {
        val date = startTime.split("T")[0]
        val startTimeForNextMeeting = startTime.split("T")[1]
        val endTimeForRunningMeeting = mRunningMeeting.toTime!!.split("T")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTimeForNextMeeting")
        val endTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $endTimeForRunningMeeting")
        return startTimeAndDateTimeInDateObject.time - endTimeAndDateTimeInDateObject.time
    }

    fun getMilliseconds(startTime: String): Long {
        val date = startTime.split("T")[0]
        val startTime = startTime.split("T")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        return startTimeAndDateTimeInDateObject.time
    }

    private fun getMeetingDurationInMilliseonds(endTime: String): Long {
        val date = endTime.split("T")[0]
        val toTime = endTime.split("T")[1]
        val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
        val endTimeAndDateInDateObject = simpleDateFormatForDate.parse("$date $toTime")
        return endTimeAndDateInDateObject.time - System.currentTimeMillis()
    }

    private fun startTimer(duration: Long) {
        mTimeLeftInMillis = duration
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
                mTimerRunning = false
                isMeetingRunning = false
                setMeetingStartedData(false)
                if (isCommingFromExtendedMeeting) {
                    isCommingFromExtendedMeeting = false
                } else {
                    endMeeting(EndMeeting(mRunningMeetingId, false, getCurrentTime()))
                }
            }
        }.start()
        mTimerRunning = true
    }

    private fun loadAvailableRoomUi() {
        status_button.text = "Available"
        mRunningBookingLayout.visibility = View.GONE
        mBookNowLayout.visibility = View.VISIBLE
    }

    fun endMeetingBeforeCompletion(view: View) {
        if (mTimerRunning && mCountDownTimer != null) {
            handleEndNowButtonClick()
        }
    }

    // end meeting before time
    fun handleEndNowButtonClick() {
        val mDialog = AlertDialog.Builder(this)
        mDialog.setTitle("End meeting")
        mDialog.setPositiveButton("YES") { _, _ ->
            // make request to end meeting
            mCountDownTimer!!.cancel()
            mTimeLeftInMillis = 0
            var endMeeting = EndMeeting()
            endMeeting.bookingId = mRunningMeetingId
            endMeeting.status = false
            endMeeting.currentTime = getCurrentTime()
            endMeeting(endMeeting)
        }
        mDialog.setNegativeButton("CANCEL") { _, _ ->

            // do nothing
        }
        val dialog: AlertDialog = mDialog.create()
        dialog.show()
        val mPositiveButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val mNegativeButton: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        /**
         * for positive button color code is #3D5A6B
         */
        mPositiveButton.setBackgroundColor(Color.WHITE)
        mPositiveButton.setTextColor(Color.parseColor("#3D5A6B"))

        /**
         * for Negative button color code #3D5A6B
         */
        mNegativeButton.setBackgroundColor(Color.WHITE)
        mNegativeButton.setTextColor(Color.parseColor("#3D5A6B"))
    }

    fun getCurrentTime(): String {
        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
        val cal = Calendar.getInstance()
        cal.time = Date()
        return dateTimeFormat.format(cal.time)
    }

    // extend running meeting duration upto next meeting
    fun handleExtendMeetingButtonClick(view: View) {
        var listItems = arrayOf<String>()
        if (!isNextMeetingPresent) {
            listItems = arrayOf("15 minutes", "30 minutes", "45 minutes", "60 minutes")
        } else {
            val difference = getMillisecondsDifferenceForExtendMeeting(mNextMeeting.fromTime!!)
            //this will checked the item when user open the dialog
            when {
                difference >= (60 * 60 * 1000) -> listItems =
                    arrayOf("15 minutes", "30 minutes", "45 minutes", "60 minutes")
                difference >= (45 * 60 * 1000) -> listItems = arrayOf("15 minutes", "30 minutes", "45 minutes")
                difference >= (30 * 60 * 1000) -> listItems = arrayOf("15 minutes", "30 minutes")
                difference >= (15 * 60 * 1000) -> listItems = arrayOf("15 minutes")
            }
        }
        if (listItems.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Duration")
            val checkedItem = 0 //this will checked the item when user open the dialog
            builder.setSingleChoiceItems(
                listItems, checkedItem
            ) { dialog, which ->
                getExtendedTimeDuration(listItems[which])
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            showToastAtTop(getString(R.string.cant_extend))
        }
    }

    private fun getExtendedTimeDuration(extendedTime: String) {
        var time = extendedTime.split(" ")[0].toInt()
        var duration = 0
        when (time) {
            15 -> {
                duration = 15
            }
            30 -> {
                duration = 30
            }
            45 -> {
                duration = 45
            }
            60 -> {
                duration = 60
            }
        }
        val mUpdateMeeting = UpdateBooking()
        mUpdateMeeting.newStartTime = mRunningMeeting.fromTime
        mUpdateMeeting.bookingId = mRunningMeetingId
        mUpdateMeeting.newtotime = getNewExtendedEndTime(mRunningMeeting.toTime!!, duration)
        Log.i("---------------", "" + mUpdateMeeting)
        makeCallToUpdateTimeForBooking(mUpdateMeeting)
    }

    private fun makeCallToUpdateTimeForBooking(mUpdateBooking: UpdateBooking) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.updateBookingDetails(mUpdateBooking)
    }

    @SuppressLint("SetTextI18n")
    private fun setNextMeetingDetails() {
        next_meeting_details.text =
            mNextMeeting.fromTime!!.split("T")[1] + " - " + mNextMeeting.toTime!!.split("T")[1] + " " + mNextMeeting.purpose
    }

    // book now meeeting
    fun bookMeeting(view: View) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        var view = layoutInflater.inflate(R.layout.new_booking_layout, null)
        setVisibilityToVisibleOfRadioButtons(view)
        if (isNextMeetingPresent) {
            val difference = getMillisecondsDifference(mNextMeeting.fromTime!!)
            when {
                difference >= (45 * 60 * 1000) -> {
                    view.radio_min_60.visibility = View.GONE
                }
                difference >= (30 * 60 * 1000) -> {
                    view.radio_min_45.visibility = View.GONE
                    view.radio_min_60.visibility = View.GONE
                }
                difference >= (15 * 60 * 1000) -> {
                    view.radio_min_60.visibility = View.GONE
                    view.radio_min_45.visibility = View.GONE
                    view.radio_min_30.visibility = View.GONE
                }
                // add else case, I don't know what to do here
            }
            if (difference < (15 * 60 * 1000)) {
                showToastAtTop(getString(R.string.cant_book))
                return
            }
        }
        textChangeListenerOnPurposeEditText(view)
        textChangeListenerOnpasscodeEditText(view)
        builder.setPositiveButton("ok", null)
        builder.setNegativeButton("cancel", null)
        builder.setCancelable(false)
        builder.setView(view)

        val mAlertDialog = builder.create()
        mAlertDialog.setOnShowListener {
            val okButton = mAlertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                if (validate(view)) {
                    // get values for meeting
                    var mLocalBookingInput = NewBookingInput()
                    mLocalBookingInput.passcode = view.edit_text_passcode.text.toString().toInt()
                    mLocalBookingInput.eventName = view.edit_text_event_name.text.toString()
                    getMillisecondsFromSelectedRadioButton(
                        view.radio_group.checkedRadioButtonId,
                        view,
                        mLocalBookingInput
                    )
                    mAlertDialog.dismiss()
                }
            }
        }
        mAlertDialog.show()
    }

    private fun getMillisecondsFromSelectedRadioButton(
        checkedRadioButtonId: Int,
        view: View,
        mLocalBookingInput: NewBookingInput
    ) {
        var duration = 0
        val dateTimeFormat = SimpleDateFormat("YYYY-MM-dd HH:mm")
        val cal = Calendar.getInstance()
        var selectedRadioButton = view.findViewById<RadioButton>(checkedRadioButtonId)
        when (selectedRadioButton.text.split(" ")[0].toString().toInt()) {
            15 -> {
                duration = 15
            }
            30 -> {
                duration = 30
            }
            45 -> {
                duration = 45
            }
            60 -> {
                duration = 60
            }
        }
        cal.time = Date()
        cal.add(Calendar.MINUTE, duration)
        val endTime = dateTimeFormat.format(cal.time)
        mLocalBookingInput.endTime = endTime
        mLocalBookingInput.startTime = dateTimeFormat.format(Date().time)
        mLocalBookingInput.roomId = 22
        mLocalBookingInput.buildingId = 7
        Log.i("--------new Booking", "" + mLocalBookingInput)
        addBookingDetails(mLocalBookingInput)
//        //val roomId = GetPreference.getRoomId(this)
//        if (roomId == -1) {
//            // ask for tablget setup again
//        } else {
//            mLocalBookingInput.roomId = roomId
//            Log.i("--------new Booking", "" + mLocalBookingInput)
//            //make api call
//            //bookNewMeeting(mLocalBookingInput)
//        }
    }


    // make request in each minute
    private fun makeRequestPeriodically() {
        val periodicThread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        runOnUiThread {
                            getViewModel()
                        }
                        sleep(60 * 1000)
                    }
                } catch (e: InterruptedException) {
                    Log.d("Thread Exception", e.message)
                }
            }
        }
        periodicThread.start()
    }

    // show meetings for the day
    fun showMeetings(view: View) {
        startActivity(Intent(this@BookingDetailsActivity, ShowBookings::class.java))
    }

    fun setVisibilityToVisibleOfRadioButtons(view: View) {
        view.radio_min_60.visibility = View.VISIBLE
        view.radio_min_45.visibility = View.VISIBLE
        view.radio_min_30.visibility = View.VISIBLE
        view.radio_min_15.visibility = View.VISIBLE
    }

    /**
     * add text change listener for the purpose edit text
     */
    private fun textChangeListenerOnPurposeEditText(view: View) {
        view.edit_text_event_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePurpose(view)
            }
        })
    }

    /**
     * validate all input fields
     */
    private fun validatePurpose(view: View): Boolean {
        return if (view.edit_text_event_name.text.toString().trim().isEmpty()) {
            view.event_name_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            view.event_name_layout.error = null
            true
        }
    }

    /**
     * add text change listener for the passcode
     */
    private fun textChangeListenerOnpasscodeEditText(view: View) {
        view.edit_text_passcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasscode(view)
            }
        })
    }

    /**
     * validate all input fields
     */
    private fun validatePasscode(view: View): Boolean {
        return if (view.edit_text_passcode.text.toString().trim().isEmpty()) {
            view.passcode_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = view.edit_text_passcode.text.toString().toInt()
            if (input <= 0 || input <= 99999) {
                view.passcode_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                view.passcode_layout.error = null
                true
            }
        }
    }

    /**
     * check validation for all input fields
     */
    private fun validate(view: View): Boolean {

        if (!validatePurpose(view) or !validatePasscode(view) or !validateRadioGroup(view)) {
            return false
        }
        return true
    }

    /**
     * validate all input fields
     */
    private fun validateRadioGroup(view: View): Boolean {
        val radioButtonId = view.radio_group.checkedRadioButtonId
        return if (radioButtonId == -1) {
            Toast.makeText(this, "Select meeting duration", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    // set time on screen with another thread
    private fun setTimeToScreen() {
        val dateTimeThread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            val cal = Calendar.getInstance()
                            cal.time = Date()
                            val date = System.currentTimeMillis()
                            val sdfForTime = SimpleDateFormat("hh:mm")
                            val sdfForDate = SimpleDateFormat("dd MMM yyyy")
                            val timeString = sdfForTime.format(cal.time)
                            val dateString = sdfForDate.format(date)
                            time_text_view.text = timeString
                            date_text_view.text = dateString
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.i("-------------", e.message)
                }
            }
        }
        dateTimeThread.start()
    }

    // set next meeting text view to free
    private fun setNextMeetingTextToFree() {
        next_meeting_details.text = getString(R.string.free_for_the_day)
    }

    // extend meeting duration
    private fun getNewExtendedEndTime(endTime: String, duration: Int): String {
        var date = endTime.split("T")[0]
        var endTime = endTime.split("T")[1]
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        var d = timeFormat.parse(endTime)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, duration)
        val newEndTime = timeFormat.format(cal.time)
        return "$date $newEndTime"
    }

    // add new Booking
    private fun addBookingDetails(mBooking: NewBookingInput) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addBookingDetails(mBooking)
    }

    //  set values for Started to true in shared preference
    private fun setMeetingStartedData(status: Boolean) {
        getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).edit()
            .putBoolean(getString(R.string.meeting_started), status).apply()
    }

    // feedback dialog
    private fun showDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText("Submit")
            .setNegativeButtonText("Cancel")
            .setNeutralButtonText("Later")
            .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
            .setDefaultRating(2)
            .setTitle("Rate this application")
            .setTitleTextColor(R.color.textColorGray)
            .setDescription("Please select some stars and give your feedback")
            .setDescriptionTextColor(R.color.textColorGray)
            .setCommentInputEnabled(true)
            .setDefaultComment("This app is pretty cool !")
            .setCommentTextColor(R.color.textColorGray)
            .setCommentBackgroundColor(R.color.defaultTextColor)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@BookingDetailsActivity)
            .show()
    }

    // show Toast
    private fun showToastAtTop(message: String) {
        var toast =
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        val toastContentView = toast!!.view as LinearLayout
        var group = toast.view as ViewGroup
        var messageTextView = group.getChildAt(0) as TextView
        messageTextView.textSize = 30F
        var imageView = ImageView(applicationContext);
        imageView.setImageResource(R.drawable.ic_layers)
        toastContentView.addView(imageView, 0)
        toast.show()
    }
}

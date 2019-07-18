package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferencerommapp.utils.GetCurrentTimeInUTC
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.EndMeeting
import com.example.conferenceroomtabletversion.model.SlotFinalList
import com.example.conferenceroomtabletversion.utils.ConvertTimeTo12HourFormat
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.utils.GetTimeDifferences
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        Log.i("------------", "Here")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("------------", "Here1")
        init()
    }

    private fun init() {
        initLateInitFields()
        initRecyclerView()
        setValuesFromSharedPreference()
        makeRequestPeriodically()
        observeData()
        observeTimeFromBookingList()
        startMeetingClickListener()
        endMeeting()
        extendMeetingClickListener()
    }

    private fun extendMeetingClickListener() {
        extend_meeting_fragment.setOnClickListener {
            handleExtendMeetingClick()
        }
    }

    private fun observeData() {
        observeDataForBookingListForTheDay()
        observeDateForStartMeeting()
        observerDataForEndMeeting()
    }

    /**
     * end meeting before completion of meeting
     */
    private fun endMeeting() {
        end_meeting_fragment.setOnClickListener {
            if (mTimerRunning && mCountDownTimer != null) {
                (activity as MainActivity).replaceFragmentsWithData(
                    EndMeetingFragment::class.java as Class<*>,
                    mRunningMeetingId.toString()
                )
            }
        }

    }

    private fun setValuesFromSharedPreference() {
        roomId = GetPreference.getRoomIdFromSharedPreference(activity!!)
        buildingId = GetPreference.getBuildingIdFromSharedPreference(activity!!)
    }

    /**
     *  start current meeting on click of start button
     */
    private fun startMeetingClickListener() {
        start_meeting_fragment.setOnClickListener {
            val startNow = EndMeeting()
            startNow.status = true
            startNow.bookingId = mRunningMeeting.bookingId
            startNow.currentTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
            makeCallForStartMeeting(startNow)
        }
    }

    /**
     * init recycler view
     */
    private fun initRecyclerView() {
        mBookingListAdapter = SolidAdapter(
            finalSlotList as ArrayList<SlotFinalList>,
            activity!!,
            object : TimeSlotAdapter.BookMeetingClickListener {
                override fun bookSlot(time: String) {
                    val timeIn24Hour = ConvertTimeTo12HourFormat.convertTo24(time)
                    startTimeFromSelectedSlot = timeIn24Hour
                    checkForNextMeeting(timeIn24Hour)
                }
            }
        )
        recycler_view_todays_booking_list.adapter = mBookingListAdapter
    }

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

    // start meeting
    private fun makeCallForStartMeeting(startNow: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.startMeeting(startNow)
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
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
            ShowToast.show(activity!!, it as Int)
        })
    }

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

    /**
     * function will check whether the time slot is in past or in future
     */
    @SuppressLint("SimpleDateFormat")
    private fun checkTimeInFuture(slotTime: String): Boolean {
        val simpleDateFormat = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val difference =
            simpleDateFormat.parse(simpleDateFormat.format(Date())).time - simpleDateFormat.parse(slotTime).time
        return difference >= 0
    }


    // main functionality
    private fun observeTimeFromBookingList() {
        val meetingListThread = object : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        activity!!.runOnUiThread {
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
                                                available_till_text_view_fragment.visibility = View.VISIBLE
                                                available_till_text_view_fragment.text =
                                                    "Free till ${ConvertTimeTo12HourFormat.convert12(
                                                        mNextMeeting.fromTime!!.split(" ")[1]
                                                    )}"
                                            } else {
                                                available_till_text_view_fragment.visibility = View.GONE
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
                                    available_till_text_view_fragment.visibility = View.VISIBLE
                                    available_till_text_view_fragment.text = getString(R.string.free_for_the_day)
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
                                                available_till_text_view_fragment.visibility = View.GONE
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
                                                        unblock_room_fragment.visibility = View.GONE
                                                        startTimer(getMeetingDurationInMilliseonds(booking.toTime!!))
                                                    }
                                                    booking.status == getString(R.string.booked) -> {
                                                        visibilityToVisibleForLayoutForNextMeeting()
                                                        setVisibilityToGoneForUnblockRoom()
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
                } catch (e: Exception) {
                    System.out.println("----------------" + e.message)
                }
            }
        }
        meetingListThread.start()
    }

    // un hide layout for next meeting
    private fun visibilityToVisibleForLayoutForNextMeeting() {
        line_fragment.visibility = View.VISIBLE
        meeting_details_relative_layout_fragment.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isMeetingRunning = false
        mCountDownTimer!!.cancel()
        mTimeLeftInMillis = 0
        Log.i("------------", "destroyed")
    }

    @SuppressLint("SetTextI18n")
    private fun setNextMeetingDetails() {
        if (!isMeetingRunning) {
            changeStatusToAvailable()
            setGradientToAvailable()
            setVisibilityToGoneForStartMeeting()
            meeting_organiser_fragment.text = "Booked by ${mNextMeeting.organizer} ${mNextMeeting.meetingDuration}"
            meeting_time_fragment.text =
                ConvertTimeTo12HourFormat.convert12(changeFormat(mNextMeeting.fromTime!!.split(" ")[1])) + " - " + ConvertTimeTo12HourFormat.convert12(
                    changeFormat(mNextMeeting.toTime!!.split(" ")[1])
                )
        }
    }


    /**
     * get time difference in milliseconds
     */
    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondsDifference(startDateTime: String): Long {
        val date = startDateTime.split(" ")[0]
        val startTime = startDateTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        val currTime = System.currentTimeMillis()
        return startTimeAndDateTimeInDateObject.time - currTime
    }

    private fun changeStatusToAvailable() {
        status_of_room_text_view.text = getString(R.string.available)
    }

    private fun changeStatusToOccupied() {
        status_of_room_text_view.text = getString(R.string.occupied)
    }

    private fun changeStatusToUnderMaintenance() {
        status_of_room_text_view.text = getString(R.string.under_maintenance)
    }

    /**
     * function will set background gradient to green color (Available)
     */
    private fun setGradientToAvailable() {
        booking_details_layout_fragment.background = resources.getDrawable(R.drawable.gradiant_for_available_room)
    }

    /**
     * function will set background gradient to orange color(Occupied)
     */
    private fun setGradientToOccupied() {
        booking_details_layout_fragment.background = resources.getDrawable(R.drawable.room_details_gradiant)
    }

    private fun setVisibilityToGoneForExtendMeeting() {
        extend_meeting_fragment.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForExtendMeeting() {
        extend_meeting_fragment.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForEndMeeting() {
        end_meeting_fragment.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForEndMeeting() {
        end_meeting_fragment.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForStartMeeting() {
        start_meeting_fragment.visibility = View.GONE
    }

    private fun setVisibilityToVisibleForStartMeeting() {
        start_meeting_fragment.visibility = View.VISIBLE
    }

    private fun setVisibilityToVisibleForUnblockRoom() {
        unblock_room_fragment.visibility = View.VISIBLE
    }

    private fun setVisibilityToGoneForUnblockRoom() {
        unblock_room_fragment.visibility = View.GONE
    }

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

    // hide layout for neval startNow = EndMeeting()
    private fun visibilityToGoneForLayoutForNextMeeting() {
        line_fragment.visibility = View.GONE
        meeting_details_relative_layout_fragment.visibility = View.GONE
    }

    @SuppressLint("SimpleDateFormat")
    fun getMilliseconds(startDateTime: String): Long {
        val date = startDateTime.split(" ")[0]
        val startTime = startDateTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
        return startTimeAndDateTimeInDateObject.time
    }

    @SuppressLint("SetTextI18n")
    fun setDataToUiForRunningMeeting(booking: BookingDeatilsForTheDay) {
        setVisibilityToGoneForRunningMeeting()
        val startTime = booking.fromTime!!.split(" ")[1]
        val endTime = booking.toTime!!.split(" ")[1]
        mRunningMeetingId = booking.bookingId!!
        setGradientToOccupied()
        changeStatusToOccupied()
        meeting_time_fragment.text =
            ConvertTimeTo12HourFormat.convert12(changeFormat(startTime)) + " - " + ConvertTimeTo12HourFormat.convert12(
                changeFormat(endTime)
            )
        meeting_organiser_fragment.text = "Booked by ${booking.organizer} ${booking.meetingDuration}"
    }

    // set visibility to gone for running meeting layout
    private fun setVisibilityToGoneForRunningMeeting() {
        setVisibilityToGoneForEndMeeting()
        setVisibilityToGoneForExtendMeeting()
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeFormat(time: String): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val simpleDateFormat1 = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        return simpleDateFormat1.format(simpleDateFormat.parse(time))
    }

    /**
     * function will set the timer for a duration
     */
    private fun startTimer(duration: Long) {
        mTimeLeftInMillis = duration
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                Log.i("---------", "running")
            }
            override fun onFinish() {
                Log.i("---------", "timer stopped")
                mTimerRunning = false
                isMeetingRunning = false
                changeStatusToAvailable()
                setGradientToAvailable()
                if (mRunningMeeting.status != getString(R.string.blocked)) {
                    endMeetingNow(EndMeeting(mRunningMeetingId, false, FormatTimeAccordingToZone.formatDateAsUTC(mRunningMeeting.toTime!!)) )
                } else {
                    setVisibilityToGoneForUnblockRoom()
                }
            }
        }.start()
        mTimerRunning = true
    }

    // api call to make end meeting request
    private fun endMeetingNow(mEndMeeting: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.endMeeting(mEndMeeting)
    }

    // set next meeting text view to free
    private fun setNextMeetingTextToFree() {
        //duration_text_view.text = getString(R.string.free_for_the_day)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMeetingDurationInMilliseonds(endTime: String): Long {
        val date = endTime.split(" ")[0]
        val toTime = endTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_yyyy_M_dd_hh_mm))
        val endTimeAndDateInDateObject = simpleDateFormatForDate.parse("$date $toTime")
        return endTimeAndDateInDateObject.time - System.currentTimeMillis()
    }

    //start meeting observer
    private fun observeDateForStartMeeting() {
        // positive response for start meeting
        mBookingForTheDayViewModel.returnSuccessForStartMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            setVisibilityToGoneForStartMeeting()
            setVisibilityToVisibleForEndMeeting()
            setVisibilityToVisibleForExtendMeeting()
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
            ShowToast.show(activity!!, it as Int)
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
            setVisibilityToGoneForEndMeeting()
            setVisibilityToGoneForExtendMeeting()
            setVisibilityToGoneForStartMeeting()
            changeStatusToAvailable()
            setGradientToAvailable()
            (activity as MainActivity).replaceFragmentsWithData(FeedbackFragment::class.java as Class<*>, mRunningMeeting.bookingId!!.toString())
        })
        // negative response from server for end meeting
        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, Observer {
            mProgressDialog.dismiss()
            ShowToast.show(activity!!, it as Int)
        })
    }

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
                ShowToast.showToastAtTop(activity!!, getString(R.string.cant_book))
                return
            }
        }
        (activity as MainActivity).replaceFragmentsWithData(BookNewMeetingFragment::class.java as Class<*>, slotTime)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMillisecondDifferenceForTimeSlot(startTime: String, timeSlot: String): Long {
        val nextMeetingStartTime = startTime.split(" ")[1]
        val simpleDateFormatForDate = SimpleDateFormat(getString(R.string.format_in_hh_mm))
        val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse(nextMeetingStartTime)
        val timeSlotInDateFormat = simpleDateFormatForDate.parse(timeSlot)
        return startTimeAndDateTimeInDateObject.time - timeSlotInDateFormat.time
    }

    /**
     * function will check for next meeting time and show slot for extension
     */
    private fun handleExtendMeetingClick() {
        if (isNextMeetingPresent) {
            val difference = GetTimeDifferences.getMillisecondsDifferenceForExtendMeeting(
                mNextMeeting.fromTime!!,
                mRunningMeeting.toTime!!
            )
            if (difference < Constants.MILLIS_15) {
                ShowToast.showToastAtTop(activity!!, getString(R.string.cant_extend))
                return
            }
            (activity as MainActivity).replaceFragmentsWithDataForExtendMeeting(ExtendMeetingFragment::class.java as Class<*>, mNextMeeting.fromTime!!, mRunningMeeting.fromTime!!, mRunningMeeting.toTime!!, mRunningMeetingId, true)
        } else {
            (activity as MainActivity).replaceFragmentsWithDataForExtendMeeting(ExtendMeetingFragment::class.java as Class<*>, "00:00", mRunningMeeting.fromTime!!, mRunningMeeting.toTime!!, mRunningMeetingId, false)
        }

    }
}
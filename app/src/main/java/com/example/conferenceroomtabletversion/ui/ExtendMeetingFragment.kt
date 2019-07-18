package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.model.UpdateBooking
import com.example.conferenceroomtabletversion.utils.GetTimeDifferences
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_extend_meeting.*
import java.text.SimpleDateFormat
import java.util.*

class ExtendMeetingFragment : Fragment() {
    private var mNextMeetingStartTime = ""
    private var endTime = ""
    private var mRunningMeetingStartTime = ""
    private var mMeetingId = -1
    private var isNextMeetingPresemnt = false
    private var mDurationForExtendBooking = 15
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDataFromBundle()
        return inflater.inflate(R.layout.fragment_extend_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun getDataFromBundle() {
        mNextMeetingStartTime = arguments!!.getString(Constants.START_TIME)!!
        endTime = arguments!!.getString(Constants.END_TIME)!!
        mRunningMeetingStartTime = arguments!!.getString(Constants.RUNNING_MEET_START_TIME)!!
        isNextMeetingPresemnt = arguments!!.getBoolean(Constants.IS_NEXT_MEETING_PRESENT)
        mMeetingId = arguments!!.getInt(Constants.MEET_ID)
    }

    private fun init() {
        showTimeSlot()
        setClickListenerOnExtendMeetingSlots()
        initLateInitFields()
        intiClickListenerForBack()
        observeDataForExtendMeeting()
        confirmExtendMeeting()
    }

    private fun confirmExtendMeeting() {
        extend_confirm_fragment.setOnClickListener {
            getExtendedTimeDuration()
        }
    }

    /**
     * function will check for next meeting time and show slot for extension
     */
    private fun showTimeSlot() {
        if(isNextMeetingPresemnt) {
            val difference = GetTimeDifferences.getMillisecondsDifferenceForExtendMeeting(mNextMeetingStartTime, endTime)
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
        }
    }


    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }


    /**
     * click listener for back button which will load MainFragment
     */
    private fun intiClickListenerForBack() {
        extend_back.setOnClickListener {
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        }
    }

    /**
     * hide 30 min time slot
     */
    private fun setVisibilityToGoneForExtendMin30() {
        extend_min_30.visibility = View.GONE
    }

    /**
     * hide 45 min time slot
     */
    private fun setVisibilityToGoneForExtendMin45() {
        extend_min_45.visibility = View.GONE
    }

    /**
     * hide 60 min time slot
     */
    private fun setVisibilityToGoneForExtendMin60() {
        extend_min_60.visibility = View.GONE
    }


    /**
     * make visibility to visible for all booking time slots
     */
    private fun setVisibilityToVisibleForAllTimeForExtendSlots() {
        extend_min_15.visibility = View.VISIBLE
        extend_min_30.visibility = View.VISIBLE
        extend_min_45.visibility = View.VISIBLE
        extend_min_60.visibility = View.VISIBLE
    }

    private fun getExtendedTimeDuration() {
        val mUpdateMeeting = UpdateBooking()
        mUpdateMeeting.newStartTime = FormatTimeAccordingToZone.formatDateAsUTC(mRunningMeetingStartTime)
        mUpdateMeeting.bookingId = mMeetingId
        mUpdateMeeting.newtotime = FormatTimeAccordingToZone.formatDateAsUTC(
            getNewExtendedEndTime(
                endTime,
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

    /**
     * extend meeting api call
     */
    private fun makeCallToUpdateTimeForBooking(mUpdateBooking: UpdateBooking) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.updateBookingDetails(mUpdateBooking)
    }

    /**
     * observer for extend meeting
     */
    private fun observeDataForExtendMeeting() {
        mBookingForTheDayViewModel.returnBookingUpdated().observe(this, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            Toasty.success(activity!!, getString(R.string.meeting_time_extended), Toast.LENGTH_SHORT, true).show()
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
            //isMeetingRunning = false
            //isCommingFromExtendedMeeting = true
            //mCountDownTimer!!.cancel()
            //mTimeLeftInMillis = 0
            //getViewModel()
        })
        mBookingForTheDayViewModel.returnUpdateFailed().observe(this, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            //makeVisibilityGoneForExtendMeetingMainLayout()
            //makeVisibilityVisibleForMainLayout()
            //loadDefaultTimeSlotForExtendMeeting()
            ShowToast.show(activity!!, it as Int)
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        })
    }

    /**
     * function will handle click listener on each time slot
     */
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

    /**
     * load UI for time slot with deselect all time slot
     */
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
}
package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.model.EndMeeting
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.fragment_end_meeting.*
import java.text.SimpleDateFormat
import java.util.*




class EndMeetingFragment : Fragment() {

    private var mMeetId = -1
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mMeetId = arguments!!.getString(Constants.DATA)!!.toInt()
        return inflater.inflate(R.layout.fragment_end_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initLateInitFields()
        backButtonClick()
        endConfirmMeeting()
        observerDataForEndMeeting()
    }

    /**
     *  load main fregment on click of back button
     */
    private fun backButtonClick() {
        back.setOnClickListener {
            (activity as MainActivity).replaceFragments(MainFragment:: class.java as Class<*>)
        }
    }

    private fun endConfirmMeeting() {
        end_meeting_confirm.setOnClickListener {
            //mCountDownTimer!!.cancel()
            //mTimeLeftInMillis = 0
            val endMeeting = EndMeeting()
            endMeeting.bookingId = mMeetId
            endMeeting.status = false
            endMeeting.currentTime = FormatTimeAccordingToZone.formatDateAsUTC(getDurationInMultipleOf15())
            endMeetingNow(endMeeting)
        }
    }

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


    /**
     * api call to make end meeting request
     */
    private fun endMeetingNow(mEndMeeting: EndMeeting) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.endMeeting(mEndMeeting)
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }

    // observe data for end meeting
    private fun observerDataForEndMeeting() {
        // positive response from server for end meeting
        mBookingForTheDayViewModel.returnSuccessForEndMeeting().observe(this, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            (activity as MainActivity).replaceFragmentsWithData(FeedbackFragment::class.java as Class<*>, mMeetId.toString())

//            isMeetingRunning = false
//            mCountDownTimer!!.cancel()
//            mTimeLeftInMillis = 0
//            makeVisibilityGoneForMainLayout()
//            makeVisibilityGoneForEndMeetingMainLayout()
//            makeVisibilityVisibleForFeedbackLayout()
//            changeStatusToAvailable()
//            setGradientToAvailable()
//            getViewModel()
//            mMeetingIdForFeedback = mRunningMeeting.bookingId!!

        })
        // negative response from server for end meeting
        mBookingForTheDayViewModel.returnFailureForEndMeeting().observe(this, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            ShowToast.show(activity!!, it as Int)
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        })
    }
}
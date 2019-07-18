package com.example.conferenceroomtabletversion.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.model.Feedback
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private var mMeetingId = -1
    private var feedbackMessage = Constants.DEFAULT_FEEDBACK_MEESAGE
    private var feedbackRating = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mMeetingId = arguments!!.getString(Constants.DATA)!!.toInt()
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }

    /**
     * load main fragment on click of cancel button
     */
    private fun cancelClickListener() {
        cancel_feedback.setOnClickListener {
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        }
    }

    /**
     * feedback response from server
     */
    private fun observeDataForFeedback() {
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)

        })
        // negative response from server
        mBookingForTheDayViewModel.returnFailureForFeedback().observe(this, Observer {
            mProgressDialog.dismiss()
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        })
    }

    /**
     * submit feedback on click of submit button
     */
    private fun submitClickListener() {
        send_feedback.setOnClickListener {
            val feedback = Feedback()
            feedback.bookingId = mMeetingId
            getComment(feedbackRating)
            feedback.comment = feedbackMessage
            feedback.rating = feedbackRating
            Log.i("-------feedback", feedback.toString())
            makeCallForAddFeedback(feedback)
        }
    }

    /**
     * get feedback comment from feedback edit text
     */
    private fun getComment(rating: Int) {
        when (rating) {
            1 -> {
                feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
                    feedback_edit_text.text.toString().trim()
                } else {
                    getString(R.string.bad_feedback_default_messgae)
                }
            }
            2 -> {
                feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
                    feedback_edit_text.text.toString().trim()
                } else {
                    getString(R.string.average_feedback_default_message)

                }
            }
            3 -> {
                feedbackMessage = if (validateFeedbackCommentEditText(feedback_edit_text.text.toString().trim())) {
                    feedback_edit_text.text.toString().trim()
                } else {
                    getString(R.string.good_feedback_default_message)
                }
            }
        }
    }

    private fun init() {
        initLateInitFields()
        initiateClickListener()
        feedbackRatingIconCLick()
        cancelClickListener()
        submitClickListener()
        observeDataForFeedback()
    }

    /**
     *  function will hide soft keyboard whenever focus is changed from edit text to other view
     */
    private fun initiateClickListener() {
        feedback_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                (activity as MainActivity).hideKeyboard(v!!)
            }
        }
    }

    /**
     * make call to add feedback for the meeting
     */
    private fun makeCallForAddFeedback(feedback: Feedback) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addFeedback(feedback)
    }

    private fun feedbackRatingIconCLick() {
        img_1.setOnClickListener {
            badFeedback()
        }
        img_2.setOnClickListener {
            averageFeedback()

        }
        img_3.setOnClickListener {
            goodFeedback()
        }
    }

    /**
     * function invoked when user select negative feedback icon
     */
    private fun badFeedback() {
        setDefaultImageForFeedback()
        img_1.setImageResource(R.drawable.ic_rate_done_2)
        feedback_edit_text.hint = getString(R.string.bad_feedback_placeholder)
        feedbackRating = 1
    }

    /**
     * function invoked when user select average feedback icon
     */
    private fun averageFeedback() {
        setDefaultImageForFeedback()
        img_2.setImageResource(R.drawable.ic_rate_done_3)
        feedback_edit_text.hint = getString(R.string.average_feedback_placehoolder)
        feedbackRating = 2
    }

    /**
     * function invoked when user select positive feedback icon
     */
    private fun goodFeedback() {
        setDefaultImageForFeedback()
        img_3.setImageResource(R.drawable.ic_rate_done_5)
        feedback_edit_text.hint = getString(R.string.good_feedback_placeholder)
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
}
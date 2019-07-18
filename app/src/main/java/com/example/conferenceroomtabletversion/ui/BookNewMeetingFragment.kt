package com.example.conferenceroomtabletversion.ui



import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.utils.FormatTimeAccordingToZone
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.ShowToast
import com.example.conferenceroomtabletversion.model.NewBookingInput
import com.example.conferenceroomtabletversion.utils.CustomToast
import com.example.conferenceroomtabletversion.utils.DateAndTimePicker
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking_status.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*

class BookNewMeetingFragment : Fragment() {

    private var startTimeSlot = ""
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        startTimeSlot = arguments!!.getString(Constants.DATA)!!
        return inflater.inflate(R.layout.fragment_book_new_meeting, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        book_back.setOnClickListener {
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        }
        book_confirm.setOnClickListener {
            confirmBookMeeting()
        }
    }

    private fun init() {
        start_time_text_view.text = startTimeSlot
        initLateInitFields()
        addTimePicker()
        textChangeListenerOnEndTimeEditText()
        textChangeListenerOnpasscodeEditText()
        initiateClickListener()
        observeDataForNewBooking()
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }

    // add new booking observer
    private fun observeDataForNewBooking() {
        // positive response from server
        mBookingForTheDayViewModel.returnSuccessForBooking().observe(activity!!, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            Toasty.success(activity!!, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
            (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
        })

        // negative response from server
        mBookingForTheDayViewModel.returnFailureForBooking().observe(activity!!, androidx.lifecycle.Observer {
            mProgressDialog.dismiss()
            when (it) {
                Constants.UNAUTHERISED -> CustomToast.showToastAtTop(activity!!, getString(R.string.incorrect_passcode))
                Constants.UNAVAILABLE_SLOT -> CustomToast.showToastAtTop(activity!!, getString(R.string.slot_unavailable))
                else -> {
                    ShowToast.show(activity!!, it as Int)
                    (activity as MainActivity).replaceFragments(MainFragment::class.java as Class<*>)
                }
            }
        })
    }

    /**
     * add new Booking api call
     */
    private fun addBookingDetails(mBooking: NewBookingInput) {
        mProgressDialog.show()
        mBookingForTheDayViewModel.addBookingDetails(mBooking)
    }

    /**
     * function adds time picker to the edit text
     */
    private fun addTimePicker() {
        end_time_text_view.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(activity!!, end_time_text_view, startTimeSlot)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun confirmBookMeeting() {
        if (validatePasscode() && validateEndTime()) {
            val mLocalBookingInput = NewBookingInput()
            mLocalBookingInput.passcode = passcode_edit_text.text.toString()
            mLocalBookingInput.eventName = getString(R.string.local_booking)
            if (!startAndTimeTimeValidate(startTimeSlot, end_time_text_view.text.toString())) {
                val sdfDate = SimpleDateFormat(getString(R.string.format_in_yyyy_mm_dd))
                val currentDate = sdfDate.format(Date())
                val startTimeInLocal = "$currentDate $startTimeSlot"
                val endTimeInLocal = "$currentDate ${end_time_text_view.text.trim()}"
                mLocalBookingInput.startTime = FormatTimeAccordingToZone.formatDateAsUTC(startTimeInLocal)
                mLocalBookingInput.endTime = FormatTimeAccordingToZone.formatDateAsUTC(endTimeInLocal)
                mLocalBookingInput.roomId = GetPreference.getRoomIdFromSharedPreference(activity!!)
                mLocalBookingInput.buildingId = GetPreference.getBuildingIdFromSharedPreference(activity!!)
                Log.i("--------------local", mLocalBookingInput.toString())
                addBookingDetails(mLocalBookingInput)
            } else {
                CustomToast.showToastAtTop(activity!!, getString(R.string.message_for_invalid_time_selection))
            }
        }
    }

    /**
     * function will validate start and end time of the booking (start time should be earlier than end time)
     */
    private fun startAndTimeTimeValidate(startTime: String, endTime: String): Boolean {
        val simpleDateFormat = SimpleDateFormat("hh:mm")
        val difference = simpleDateFormat.parse(startTime).time - simpleDateFormat.parse(endTime).time
        return difference >= 0
    }


    /**
     *  function will hide soft keyboard whenever focus is changed from edit text to other view
     */
    private fun initiateClickListener() {
        passcode_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                (activity as MainActivity).hideKeyboard(v!!)
            }
        }
    }

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

}
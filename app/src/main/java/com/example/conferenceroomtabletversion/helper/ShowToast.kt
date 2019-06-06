package com.example.conferenceroomtabletversion.helper

import android.content.Context
import android.widget.Toast
import com.example.conferenceroomtabletversion.R
import es.dmoral.toasty.Toasty

/**
 * show different toast for different kind of error and response messages
 */
class ShowToast {
    companion object {
        fun show(mContext: Context, errorCode: Int) {
            Toasty.info(mContext, showMessageAccordingToCode(mContext, errorCode), Toast.LENGTH_SHORT, true).show()
        }


        fun showMessageAccordingToCode(mContext: Context, errorCode: Int): String {
            var message = mContext.getString(R.string.something_went_wrong)
            when (errorCode) {
                Constants.NOT_ACCEPTABLE -> {
                    message = "Not Acceptable"
                }
                Constants.NO_CONTENT_FOUND -> {
                    message = mContext.getString(R.string.no_booking_available)
                }
                Constants.NOT_FOUND -> {
                    message = mContext.getString(R.string.not_found)
                }
                Constants.INTERNAL_SERVER_ERROR -> {
                    message = mContext.getString(R.string.server_error)
                }
                Constants.UNAVAILABLE_SLOT -> {
                    message = mContext.getString(R.string.slot_unavailable)
                }
            }
            return message
        }
    }

}
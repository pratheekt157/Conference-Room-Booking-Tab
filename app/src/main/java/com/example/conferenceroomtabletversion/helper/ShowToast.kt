package com.example.conferenceroomtabletversion.helper

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

        /**
         * custom toast code
         */
        fun showToastAtTop(mContext: Context, message: String) {
            val toast =
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)
            val toastContentView = toast!!.view as LinearLayout
            val group = toast.view as ViewGroup
            val messageTextView = group.getChildAt(0) as TextView
            messageTextView.textSize = 24F
            val imageView = ImageView(mContext)
            imageView.setImageResource(R.drawable.ic_layers)
            toastContentView.addView(imageView, 0)
            toast.show()
        }
    }

}
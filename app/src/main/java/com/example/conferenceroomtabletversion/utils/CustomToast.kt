package com.example.conferenceroomtabletversion.utils

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.conferenceroomtabletversion.R

class CustomToast {
    companion object {
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
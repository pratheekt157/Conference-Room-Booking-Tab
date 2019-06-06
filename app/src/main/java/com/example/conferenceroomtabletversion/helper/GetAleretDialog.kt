package com.example.conferenceroomtabletversion.helper

import android.app.AlertDialog
import android.content.Context

class GetAleretDialog {
    companion object {
        fun getDialog(mContext: Context, title: String, message: String): AlertDialog.Builder {
            val mDialog = AlertDialog.Builder(mContext)
            mDialog.setTitle(title)
            mDialog.setMessage(message)
            return mDialog
        }

        fun showDialog(mDialog: AlertDialog.Builder): AlertDialog {
            val dialog: AlertDialog = mDialog.create()
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
            return dialog
        }
    }
}
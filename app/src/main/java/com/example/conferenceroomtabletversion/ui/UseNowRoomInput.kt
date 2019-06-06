package com.example.conferenceroomtabletversion.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.conferenceroomtabletversion.R

class UseNowRoomInput: AppCompatDialogFragment() {
//    private var editTextUsername: EditText? = null
//    private var editTextPassword: EditText? = null
//    private var listener: BookNowDialogListener? = null
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val builder = AlertDialog.Builder(activity!!)
//        val inflater = activity!!.getLayoutInflater()
//        val view = inflater.inflate(R.layout.new_booking_layout, null)
//        builder.setView(view)
//            .setTitle("Login")
//            .setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, i -> })
//            .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
//                val username = editTextUsername!!.text.toString()
//                val password = editTextPassword!!.text.toString()
//                listener!!.applyTexts(username, password)
//            })
//
//        editTextUsername = view.findViewById(R.id.edit_username)
//        editTextPassword = view.findViewById(R.id.edit_password)
//        return builder.create()
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            listener = context as BookNowDialogListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                e.message+
//                context.toString() + "must implement BookNowDialogListener")
//        }
//    }
    interface BookNowDialogListener {
        fun applyTexts(username: String, password: String)
    }

}
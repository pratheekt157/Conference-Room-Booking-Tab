package com.example.conferenceroomtabletversion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.end_meeting_layout.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            handleExtendMeetingButtonClick()
//            val alertDialog = AlertDialog.Builder(this)
//            val view = layoutInflater.inflate(R.layout.end_meeting_layout, null)
//            alertDialog.setView(view)
//            view.yes_text_view.setOnClickListener {
//
//            }
//            view.no_text_view.setOnClickListener {
//
//            }
//            alertDialog.create().show()
        }
    }

    fun handleExtendMeetingButtonClick() {
        var listItems = arrayOf<String>("Delete all reccuring meeting")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_duration))
        builder.setMessage("Are you wants to delete the meeting")
        val checkedItem = 0 //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(
            listItems, checkedItem
        ) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}

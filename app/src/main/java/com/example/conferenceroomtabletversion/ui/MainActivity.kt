package com.example.conferenceroomtabletversion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.conferenceroomtabletversion.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.end_meeting_layout.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.end_meeting_layout, null)
            alertDialog.setView(view)
            view.yes_text_view.setOnClickListener {

            }
            view.no_text_view.setOnClickListener {

            }
            alertDialog.create().show()
        }
    }
}

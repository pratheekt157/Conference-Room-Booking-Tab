package com.example.conferenceroomtabletversion.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conferenceroomtabletversion.R
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.new_booking_layout.view.*


class ActivityContainer: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        click.setOnClickListener {
            showDialog()
//            val addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment(object: AddPhotoBottomDialogFragment.SendNewBookingData {
//                override fun sendData(name: String) {
//                    Toast.makeText(this@ActivityContainer, name, Toast.LENGTH_SHORT).show()
//
//                }
//            })
//            addPhotoBottomDialogFragment.show(
//                supportFragmentManager,
//                "add_photo_dialog_fragment"
//            )
        }
    }

    private fun showDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        var view = layoutInflater.inflate(R.layout.new_booking_layout, null)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        view.book.setOnClickListener {
            dialog.dismiss()
        }
        view.clear_button.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
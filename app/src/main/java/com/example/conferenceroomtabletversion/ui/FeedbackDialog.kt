package com.example.conferenceroomtabletversion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conferenceroomtabletversion.R
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_feedback.*
import java.util.*


class FeedbackDialog: AppCompatActivity(), RatingDialogListener {
    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {

    }
    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        Toasty.info(this, "" + rate + comment, Toast.LENGTH_SHORT, true).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        feedback_button.setOnClickListener {
            showDialog()
        }
    }


    private fun showDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText("Submit")
            .setNegativeButtonText("Cancel")
            .setNeutralButtonText("Later")
            .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
            .setDefaultRating(2)
            .setTitle("Rate this application")
            .setTitleTextColor(R.color.textColorGray)
            .setDescription("Please select some stars and give your feedback")
            .setDescriptionTextColor(R.color.textColorGray)
            .setCommentInputEnabled(true)
            .setDefaultComment("This app is pretty cool !")
            .setCommentTextColor(R.color.textColorGray)
            .setCommentBackgroundColor(R.color.defaultTextColor)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@FeedbackDialog)
            .show()
    }
}
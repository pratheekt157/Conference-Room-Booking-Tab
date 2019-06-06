package com.example.conferenceroomtabletversion.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import com.example.conferenceroomtabletversion.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.Any as Any1






class AddPhotoBottomDialogFragment(var listener: SendNewBookingData) : BottomSheetDialogFragment() {
    lateinit var b: FrameLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

// get the views and attach the listener

        return inflater.inflate(
            R.layout.bottom_sheet, container,
            false
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0 // Remove this line to hide a dark background if you manually hide the dialog.
            }
        })


        var sendData = view.findViewById<Button>(R.id.sendData)
        sendData.setOnClickListener {
            listener.sendData("Prateek")
            dismiss()
        }
    }

    interface SendNewBookingData {
        fun sendData(name: String)
    }
}

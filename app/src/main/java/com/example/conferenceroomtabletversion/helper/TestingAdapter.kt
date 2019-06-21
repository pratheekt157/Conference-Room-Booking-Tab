package com.example.conferenceroomtabletversion.helper

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.Test
import java.text.SimpleDateFormat

@Suppress("NAME_SHADOWING")
class TestingAdapter(
    private val bookingDeatilsForTheDayItemList: ArrayList<String>
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<TestingAdapter.ViewHolder>() {

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.today_booking_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return bookingDeatilsForTheDayItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    }

}

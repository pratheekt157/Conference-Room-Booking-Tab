package com.example.conferenceroomtabletversion.helper

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.Test
import com.example.conferenceroomtabletversion.utils.ConvertTimeTo12HourFormat
import java.text.SimpleDateFormat

@Suppress("NAME_SHADOWING")
class BookingForTheDayAdapter(
    private val bookingDeatilsForTheDayItemList: ArrayList<BookingDeatilsForTheDay>
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<BookingForTheDayAdapter.ViewHolder>() {

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
        val mBookingDetails = bookingDeatilsForTheDayItemList[position]
        if(mBookingDetails.status == "Available") {
            holder.available.visibility = View.VISIBLE
            holder.available.text = "Available: " + ConvertTimeTo12HourFormat.convert12(changeFormat(mBookingDetails.fromTime!!.split(" ")[1])) + " - " + ConvertTimeTo12HourFormat.convert12(changeFormat(mBookingDetails.toTime!!.split(" ")[1]))
            holder.bookingDetailsRelativeLayout.visibility = View.GONE
        } else if(mBookingDetails.status == "Booked" || mBookingDetails.status == "Started") {
            holder.available.visibility = View.GONE
            holder.bookingDetailsRelativeLayout.visibility = View.VISIBLE
            holder.meetingDurationTextView.text = ConvertTimeTo12HourFormat.convert12(changeFormat(mBookingDetails.fromTime!!.split(" ")[1])) + " - " + ConvertTimeTo12HourFormat.convert12(changeFormat(mBookingDetails.toTime!!.split(" ")[1]))
            holder.organizerTextView.text = "Booked by ${mBookingDetails.organizer} ${bookingDeatilsForTheDayItemList[position].meetingDuration}"
        }
    }

    private fun changeFormat(time: String): String {
        var simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var simpleDateFormat1 = SimpleDateFormat("HH:mm")
        return simpleDateFormat1.format(simpleDateFormat.parse(time))
    }
    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return bookingDeatilsForTheDayItemList.size
    }
    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var bookingDetailsRelativeLayout: RelativeLayout = itemView.findViewById(R.id.meeting_details_main_layout)
        var available: TextView = itemView.findViewById(R.id.available_for_duration_text_view)
        var meetingDurationTextView: TextView = itemView.findViewById(R.id.meeting_time)
        var organizerTextView: TextView = itemView.findViewById(R.id.meeting_organiser_with_duration)
    }



}

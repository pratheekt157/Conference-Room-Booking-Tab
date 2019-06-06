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

@Suppress("NAME_SHADOWING")
class BookingForTheDayAdapter(
    private val bookingDeatilsForTheDayItemList: ArrayList<BookingDeatilsForTheDay>,
    private val mShowMembers: ShowMembersListener
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<BookingForTheDayAdapter.ViewHolder>() {

    /**
     * a variable which will hold the 'Instance' of interface
     */
    companion object {
        var mShowMembersListener: ShowMembersListener? = null
    }

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booked_list_items, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mShowMembersListener = mShowMembers
        val mBookingDetails = bookingDeatilsForTheDayItemList[position]
        holder.purposeTextView.text = mBookingDetails.purpose
        holder.meetingDurationTextView.text = mBookingDetails.fromTime!!.split("T")[1] + " - " + mBookingDetails.toTime!!.split("T")[1]
        holder.organizerTextView.text = "Organized by " + mBookingDetails.organizer

        holder.itemView.setOnClickListener {
           // mShowMembersListener!!.showMembers(mBookingDetails.cCMail!!)
        }
    }
    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return bookingDeatilsForTheDayItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var purposeTextView: TextView = itemView.findViewById(R.id.meeting_purpose_text_view)
        var meetingDurationTextView: TextView = itemView.findViewById(R.id.meeting_duration)
        var organizerTextView: TextView = itemView.findViewById(R.id.organizer_of_meeting_text_view)
    }

    /**
     * An interface which will be implemented by UserDashboardBookingActivity activity to pass employeeList to the activity
     */
    interface ShowMembersListener {
        fun showMembers(mEmployeeList: List<String>)
    }


}

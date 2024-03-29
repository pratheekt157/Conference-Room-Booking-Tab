package com.example.conferenceroomtabletversion.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.model.SlotFinalList
import com.example.conferenceroomtabletversion.utils.ConvertTimeTo12HourFormat


class TimeSlotAdapter(
    private val bookingDeatilsForTheDayItemList: ArrayList<SlotFinalList>,
    private val mContext: Context,
    private val listener: BookMeetingClickListener

) : androidx.recyclerview.widget.RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_line_view, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.meetingDurationTextView.text =
            ConvertTimeTo12HourFormat.convert12(bookingDeatilsForTheDayItemList[position].slot.toString())
        if (bookingDeatilsForTheDayItemList[position].isBooked!!) {
            changeColorToOccupied(holder)
            makeVisibilityGoneForVerticalLine(holder)
            if (bookingDeatilsForTheDayItemList[position].status == mContext.getString(R.string.middle_slot)) {
                makeVisibilityGoneForVerticalLine(holder)
                holder.horizontalLine.visibility = View.VISIBLE
                clearTextOfTimeSlot(holder)
            } else {
                holder.horizontalLine.visibility = View.GONE
                makeVisibilityGoneForVerticalLine(holder)
                holder.meetingDurationTextView.text =
                    ConvertTimeTo12HourFormat.convert12(bookingDeatilsForTheDayItemList[position].slot.toString())
            }
        } else {
            holder.horizontalLine.visibility = View.GONE
            makeVisibilityVisibleForVerticalLine(holder)
            holder.mainLayout.setBackgroundColor(Color.parseColor("#00D4AB"))
        }
        if (bookingDeatilsForTheDayItemList[position].inPast == false && bookingDeatilsForTheDayItemList[position].isBooked != true) {
            holder.mainLayout.setBackgroundColor(Color.parseColor("#808080"))
        } else {
            if (!bookingDeatilsForTheDayItemList[position].isBooked!!)
                holder.mainLayout.setBackgroundColor(Color.parseColor("#00D4AB"))
        }
        holder.itemView.setOnClickListener {
            if (bookingDeatilsForTheDayItemList[position].inPast!! && !bookingDeatilsForTheDayItemList[position].isBooked!!) {
                listener.bookSlot(bookingDeatilsForTheDayItemList[position].slot!!)
            }
        }
    }

    private fun changeColorToOccupied(holder: ViewHolder) {
        holder.mainLayout.setBackgroundColor(Color.parseColor("#FF0000"))
    }

    private fun clearTextOfTimeSlot(holder: ViewHolder) {
        holder.meetingDurationTextView.text = ""
    }

    private fun makeVisibilityGoneForVerticalLine(holder: ViewHolder) {
        holder.verticalLine.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForVerticalLine(holder: ViewHolder) {
        holder.verticalLine.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return bookingDeatilsForTheDayItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var mainLayout: RelativeLayout = itemView.findViewById(R.id.main_relative_layout)
        var meetingDurationTextView: TextView = itemView.findViewById(R.id.time_card_view_text)
        var verticalLine: View = itemView.findViewById(R.id.line0)
        val horizontalLine: View = itemView.findViewById(R.id.line_horizontal)
    }

    interface BookMeetingClickListener {
        fun bookSlot(time: String)
    }
}

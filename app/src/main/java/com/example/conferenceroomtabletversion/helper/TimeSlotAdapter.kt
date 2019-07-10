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
    private val bookingDetailsForTheDayItemList: ArrayList<SlotFinalList>,
    private val mContext: Context,
    private val listener: BookMeetingClickListener

) : androidx.recyclerview.widget.RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    /**
     * this override function will set a view for the recycler view items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_for_time_slot, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.meetingDurationTextView.text = bookingDetailsForTheDayItemList[position].slot.toString()
        if (bookingDetailsForTheDayItemList[position].isBooked!!) {
            changeColorToOccupied(holder)
            makeVisibilityGoneForSlotStatus(holder)
            if (bookingDetailsForTheDayItemList[position].status == mContext.getString(R.string.middle_slot)) {
                makeVisibilityGoneForVerticalLine(holder)
                holder.horizontalLine.visibility = View.VISIBLE
                clearTextOfTimeSlot(holder)
            } else {
                holder.horizontalLine.visibility = View.GONE
                if (bookingDetailsForTheDayItemList[position].meetingDuration != mContext.getString(R.string.for_15_minutes) && bookingDetailsForTheDayItemList[position].status == mContext.getString(
                        R.string.start
                    )
                ) {
                    makeVisibilityGoneForVerticalLine(holder)
                } else {
                    makeVisibilityVisibleForVerticalLine(holder)
                }
                holder.meetingDurationTextView.text =
                    ConvertTimeTo12HourFormat.convert12(bookingDetailsForTheDayItemList[position].slot.toString())
            }
        } else {
            holder.horizontalLine.visibility = View.GONE
            makeVisibilityVisibleForSlotStatus(holder)
            makeVisibilityVisibleForVerticalLine(holder)
            holder.mainLayout.setBackgroundColor(Color.parseColor("#479F78"))
        }
        if (bookingDetailsForTheDayItemList[position].inPast == false && bookingDetailsForTheDayItemList[position].isBooked != true) {
            makeVisibilityGoneForSlotStatus(holder)
            holder.mainLayout.setBackgroundColor(Color.parseColor("#4B5365"))
        } else {
            if (!bookingDetailsForTheDayItemList[position].isBooked!!)
                holder.mainLayout.setBackgroundColor(Color.parseColor("#479F78"))
        }
        holder.itemView.setOnClickListener {
            if (bookingDetailsForTheDayItemList[position].inPast!! && !bookingDetailsForTheDayItemList[position].isBooked!!) {
                listener.bookSlot(bookingDetailsForTheDayItemList[position].slot!!)
            }
        }
    }

    private fun changeColorToOccupied(holder: ViewHolder) {
        holder.mainLayout.setBackgroundColor(Color.parseColor("#E54B4B"))
    }

    private fun clearTextOfTimeSlot(holder: ViewHolder) {
        holder.meetingDurationTextView.text = ""
    }

    private fun makeVisibilityGoneForVerticalLine(holder: ViewHolder) {
        holder.verticalLine.visibility = View.GONE
    }

    private fun makeVisibilityGoneForSlotStatus(holder: ViewHolder) {
        holder.availableLebal.visibility = View.GONE
    }

    private fun makeVisibilityVisibleForSlotStatus(holder: ViewHolder) {
        holder.availableLebal.visibility = View.VISIBLE
    }

    private fun makeVisibilityVisibleForVerticalLine(holder: ViewHolder) {
        holder.verticalLine.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return bookingDetailsForTheDayItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var mainLayout: RelativeLayout = itemView.findViewById(R.id.main_relative_layout)
        var meetingDurationTextView: TextView = itemView.findViewById(R.id.time_card_view_text)
        var verticalLine: View = itemView.findViewById(R.id.line0)
        val horizontalLine: View = itemView.findViewById(R.id.line_horizontal)
        val availableLebal: View = itemView.findViewById(R.id.slot_status_text_view)
    }

    interface BookMeetingClickListener {
        fun bookSlot(time: String)
    }
}


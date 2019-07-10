package com.example.conferenceroomtabletversion.helper

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.model.SlotFinalList


class SolidAdapter(
    private val bookingDetailsForTheDayItemList: ArrayList<SlotFinalList>
) : RecyclerView.Adapter<SolidAdapter.ViewHolder>() {

    /**
     * this override function will set a view for the recycler view items
     */


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        var holder: ViewHolder? = null
        when (viewType) {
            R.layout.available_time_slot -> holder = AvailableTimeSlot(view)
            R.layout.booked_start_time_slot -> holder = BookedStartTimeSlot(view)
            R.layout.booked_middle_time_slot -> holder = BookedMiddleTimeSlot(view)
            R.layout.past_time_slot_layout -> holder = PastTimeSlot(view)
            R.layout.booked_end_time_slot -> holder = BookedEndTimeSlot(view)
            R.layout.booked_start_time_slot_for_15_minutes -> holder = BookedStartTimeSlotFor15Minutes(view)
        }
        return holder!!
    }
    override fun getItemViewType(position: Int): Int {
        return if (bookingDetailsForTheDayItemList[position].isBooked!!) {
            when {
                bookingDetailsForTheDayItemList[position].status == "Middle" -> R.layout.booked_middle_time_slot
                bookingDetailsForTheDayItemList[position].status == "Start" ->  {
                    if(bookingDetailsForTheDayItemList[position].meetingDuration == "15 minutes") {
                        R.layout.booked_start_time_slot_for_15_minutes
                    } else {
                        R.layout.booked_start_time_slot
                    }

                }
                bookingDetailsForTheDayItemList[position].status == "End" -> R.layout.booked_end_time_slot
                bookingDetailsForTheDayItemList[position].status == "Past" -> R.layout.past_time_slot_layout
                else -> R.layout.available_time_slot
            }
        } else {
            if(bookingDetailsForTheDayItemList[position].status == "Past") {
                R.layout.past_time_slot_layout
            } else {
                R.layout.available_time_slot
            }

        }
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder.itemViewType) {
            R.layout.available_time_slot ->  {
                holder.bind(bookingDetailsForTheDayItemList[position])
            }
            R.layout.booked_start_time_slot ->  {
                holder.bind(bookingDetailsForTheDayItemList[position])
            }
            R.layout.booked_end_time_slot ->  {
                holder.bind(bookingDetailsForTheDayItemList[position])
            }
            R.layout.past_time_slot_layout ->  {
                holder.bind(bookingDetailsForTheDayItemList[position])
            }
            R.layout.booked_start_time_slot_for_15_minutes -> {
                holder.bind(bookingDetailsForTheDayItemList[position])
            }
            R.layout.booked_middle_time_slot -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return bookingDetailsForTheDayItemList.size
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal abstract fun bind(item: SlotFinalList)
    }

    class AvailableTimeSlot(itemView: View): ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.time_card_view_text)
        override fun bind(item: SlotFinalList) {
            title.text = item.slot
        }
    }

    class BookedStartTimeSlot(itemView: View): ViewHolder(itemView) {
        private var slotTime: TextView = itemView.findViewById(R.id.time_card_view_text)
        private var organiserTextView: TextView = itemView.findViewById(R.id.slot_status_text_view)
        override fun bind(item: SlotFinalList) {
            slotTime.text = item.slot
            organiserTextView.text = "Organiser: ${item.organiser}"
        }
    }

    class BookedStartTimeSlotFor15Minutes(itemView: View): ViewHolder(itemView) {
        private var slotTime: TextView = itemView.findViewById(R.id.time_card_view_text)
        private var slotEndTime: TextView = itemView.findViewById(R.id.end_time_slot)
        private var organiserTextView: TextView = itemView.findViewById(R.id.slot_status_text_view)
        override fun bind(item: SlotFinalList) {
            slotTime.text = item.slot
            slotEndTime.text = item.endTime
            organiserTextView.text = "Organiser: ${item.organiser}"
        }
    }

    class BookedMiddleTimeSlot(itemView: View): ViewHolder(itemView) {
        override fun bind(item: SlotFinalList) {
        }
    }

    class BookedEndTimeSlot(itemView: View): ViewHolder(itemView) {
        private var slotTime: TextView = itemView.findViewById(R.id.time_card_view_text)
        override fun bind(item: SlotFinalList) {
            slotTime.text = item.slot
        }
    }

    class PastTimeSlot(itemView: View): ViewHolder(itemView) {
        private var slotTime: TextView = itemView.findViewById(R.id.time_card_view_text)
        override fun bind(item: SlotFinalList) {
            slotTime.text = item.slot
        }
    }
}


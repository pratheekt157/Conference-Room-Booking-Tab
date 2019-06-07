package com.example.conferenceroomtabletversion.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.model.ConferenceList

class ConferenceAdapter(private val conferencceList: List<ConferenceList>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ConferenceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conference_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conferencceList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.conferencelist = conferencceList[position]
        holder.conferenceName.text = conferencceList[position].roomName
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val conferenceName: TextView = itemView.findViewById(R.id.conference_name)
        var conferencelist: ConferenceList? = null
    }

}
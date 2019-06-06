package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName
import org.w3c.dom.Comment

data class Feedback(
    @SerializedName("MeetId")
    var bookingId: Int? = null,

    @SerializedName("Rating")
    var rating: Int? = null,

    @SerializedName("Comment")
    var comment: String? = null
)
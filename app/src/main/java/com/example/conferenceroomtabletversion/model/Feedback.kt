package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName
import org.w3c.dom.Comment

data class Feedback(
    @SerializedName("meetId")
    var bookingId: Int? = null,

    @SerializedName("rating")
    var rating: Int? = null,

    @SerializedName("comment")
    var comment: String? = null
)
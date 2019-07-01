package com.example.conferenceroomtabletversion.model


import com.google.gson.annotations.SerializedName

//Model Class Of the Buildings
data class Buildings(
        @SerializedName("buildingId")
        var buildingId: Int? = null,

        @SerializedName("buildingName")
        var buildingName: String? = null,

        @SerializedName("place")
        var buildingPlace: String? = null
)
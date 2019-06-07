package com.example.conferenceroomtabletversion.model


import com.google.gson.annotations.SerializedName

//Model Class Of the Buildings
data class Buildings(
        @SerializedName("BuildingId")
        var buildingId: Int? = null,

        @SerializedName("BuildingName")
        var buildingName: String? = null,

        @SerializedName("Place")
        var buildingPlace: String? = null
)
package com.raywenderlich.android.rwandroidtutorial.data

import com.google.gson.annotations.SerializedName

data class LocationSunrise(
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String
)
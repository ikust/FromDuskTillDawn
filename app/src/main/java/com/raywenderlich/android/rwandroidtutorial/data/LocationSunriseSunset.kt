package com.raywenderlich.android.rwandroidtutorial.data

import com.google.gson.annotations.SerializedName

data class LocationSunriseSunset(
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String
)
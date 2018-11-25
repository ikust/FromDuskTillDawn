package com.raywenderlich.android.rwandroidtutorial.data

import com.google.gson.annotations.SerializedName

data class LocationSunData(
    val locationName: String,
    val sunrise: String,
    val sunset: String
)
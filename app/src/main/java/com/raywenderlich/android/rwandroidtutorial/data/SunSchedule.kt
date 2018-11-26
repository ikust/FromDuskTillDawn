package com.raywenderlich.android.rwandroidtutorial.data

import com.google.gson.annotations.SerializedName

data class SunSchedule(
    val locationName: String,
    val sunrise: String,
    val sunset: String
)
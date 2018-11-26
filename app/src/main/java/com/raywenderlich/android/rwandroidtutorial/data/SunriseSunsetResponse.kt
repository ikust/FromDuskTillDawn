package com.raywenderlich.android.rwandroidtutorial.data

import com.google.gson.annotations.SerializedName

data class SunriseSunsetResponse(
      @SerializedName("status") val status: String,
      @SerializedName("results") val result: LocationSunriseSunset
)
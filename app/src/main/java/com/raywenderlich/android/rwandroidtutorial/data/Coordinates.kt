package com.raywenderlich.android.rwandroidtutorial.data

import java.io.Serializable

data class Coordinates(
    val latitude: Double,
    val longitude: Double
) : Serializable
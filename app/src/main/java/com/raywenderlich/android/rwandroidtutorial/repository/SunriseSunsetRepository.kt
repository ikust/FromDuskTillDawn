package com.raywenderlich.android.rwandroidtutorial.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raywenderlich.android.rwandroidtutorial.api.SunriseApi
import com.raywenderlich.android.rwandroidtutorial.data.Coordinates
import com.raywenderlich.android.rwandroidtutorial.data.SunSchedule
import com.raywenderlich.android.rwandroidtutorial.data.SunriseResponse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SunriseSunsetRepository(val app: Application) {

  private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)

  private val sunriseApi: SunriseApi = SunriseApi.create()

  private fun getLastLocation(): LiveData<Location?> {
    val lastLocation = MutableLiveData<Location?>()

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
          lastLocation.value = location
        }

    return lastLocation
  }

  fun getCoordinates(locationName: String): LiveData<Coordinates?> {
    val coordinates = MutableLiveData<Coordinates?>()

    doAsync {
      val geocoder = Geocoder(app, Locale.getDefault())

      val addresses = geocoder.getFromLocationName(locationName, 1)

      uiThread {
        if(addresses.size > 0) {
          coordinates.value = Coordinates(addresses[0].latitude, addresses[0].longitude)
        } else {
          coordinates.value = null
        }
      }
    }

    return coordinates
  }

  fun getSunriseSunset(): LiveData<SunSchedule?> {
    return Transformations.switchMap(getLastLocation()) { location ->
      getSunriseSunset(location?.latitude, location?.longitude)
    }
  }

  fun getSunriseSunset(latitude: Double?, longitude: Double?): LiveData<SunSchedule?> {
    val sunSchedule = MutableLiveData<SunSchedule?>()

    if(latitude == null || longitude == null) {
      sunSchedule.value = null
      return sunSchedule
    }

    val call = sunriseApi.getSunriseAndSunset(latitude, longitude)
    call.enqueue(object : Callback<SunriseResponse> {

      override fun onResponse(call: Call<SunriseResponse>, response: Response<SunriseResponse>) {
        if (response.isSuccessful) {
          val geocoder = Geocoder(app, Locale.getDefault())
          val addresses = geocoder.getFromLocation(latitude, longitude, 1)

          val locationName = if(addresses.size > 0) {
            addresses?.get(0)?.locality ?: "${latitude}, ${longitude}"
          } else {
            "${latitude}, ${longitude}"
          }

          sunSchedule.value = SunSchedule(
              locationName,
              response.body()?.result?.sunrise ?: "No data",
              response.body()?.result?.sunset ?: "No data"
          )
        } else {
          // Show error
          sunSchedule.value = null
        }
      }

      override fun onFailure(call: Call<SunriseResponse>, t: Throwable) {
        t.printStackTrace()
        sunSchedule.value = null
      }
    })

    return sunSchedule
  }
}
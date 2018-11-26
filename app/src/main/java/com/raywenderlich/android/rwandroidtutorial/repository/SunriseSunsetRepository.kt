/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.rwandroidtutorial.repository

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raywenderlich.android.rwandroidtutorial.api.SunriseSunsetApi
import com.raywenderlich.android.rwandroidtutorial.data.Coordinates
import com.raywenderlich.android.rwandroidtutorial.data.LocationSunTimetable
import com.raywenderlich.android.rwandroidtutorial.data.SunriseSunsetResponse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SunriseSunsetRepository(val app: Application) {

  private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)

  private val sunriseSunsetApi: SunriseSunsetApi = SunriseSunsetApi.create()

  @SuppressLint("MissingPermission")
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

  fun getSunriseSunset(): LiveData<LocationSunTimetable?> {
    return Transformations.switchMap(getLastLocation()) { location ->
      getSunriseSunset(location?.latitude, location?.longitude)
    }
  }

  fun getSunriseSunset(latitude: Double?, longitude: Double?): LiveData<LocationSunTimetable?> {
    val sunSchedule = MutableLiveData<LocationSunTimetable?>()

    if(latitude == null || longitude == null) {
      sunSchedule.value = null
      return sunSchedule
    }

    val call = sunriseSunsetApi.getSunriseAndSunset(latitude, longitude)
    call.enqueue(object : Callback<SunriseSunsetResponse> {

      override fun onResponse(call: Call<SunriseSunsetResponse>, response: Response<SunriseSunsetResponse>) {
        if (response.isSuccessful) {
          val geocoder = Geocoder(app, Locale.getDefault())
          val addresses = geocoder.getFromLocation(latitude, longitude, 1)

          val locationName = if(addresses.size > 0) {
            addresses?.get(0)?.locality ?: "$latitude, $longitude"
          } else {
            "$latitude, $longitude"
          }

          val locale = if(addresses.size > 0) {
            addresses?.get(0)?.locale
          } else {
            null
          }

          sunSchedule.value = LocationSunTimetable(
              locationName,
              response.body()?.result?.sunrise,
              response.body()?.result?.sunset,
              locale
          )
        } else {
          // Show error
          sunSchedule.value = null
        }
      }

      override fun onFailure(call: Call<SunriseSunsetResponse>, t: Throwable) {
        t.printStackTrace()
        sunSchedule.value = null
      }
    })

    return sunSchedule
  }
}
package com.raywenderlich.android.rwandroidtutorial

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raywenderlich.android.rwandroidtutorial.api.SunriseApi
import com.raywenderlich.android.rwandroidtutorial.data.LocationSunData
import com.raywenderlich.android.rwandroidtutorial.data.SunriseResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainViewModel(app: Application): AndroidViewModel(app) {

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private val sunriseApi: SunriseApi = SunriseApi.create()

  val sunriseData: MutableLiveData<LocationSunData?> = MutableLiveData()

  fun load(arguments: Bundle?) {
    val app:Application = getApplication()
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
          if(location != null) {
            val call = sunriseApi.getSunriseAndSunset(36.7201600, 4.4203400)
            call.enqueue(object : Callback<SunriseResponse> {

              override fun onResponse(call: Call<SunriseResponse>, response: Response<SunriseResponse>) {
                if (response.isSuccessful) {
                  val geocoder = Geocoder(app, Locale.getDefault())
                  val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                  sunriseData.value = LocationSunData(
                      addresses?.get(0)?.locality ?: "${location.latitude}, ${location.longitude}",
                      response.body()?.result?.sunrise ?: "No data",
                      response.body()?.result?.sunset ?: "No data"
                  )
                } else {
                  // Show error
                }
              }

              override fun onFailure(call: Call<SunriseResponse>, t: Throwable) {
                t.printStackTrace()
              }
            })
          } else {
            sunriseData.value = null
          }
        }
  }
}
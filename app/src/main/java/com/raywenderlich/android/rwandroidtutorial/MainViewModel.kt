package com.raywenderlich.android.rwandroidtutorial

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.raywenderlich.android.rwandroidtutorial.api.SunriseApi
import com.raywenderlich.android.rwandroidtutorial.data.SunriseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(app: Application): AndroidViewModel(app) {

  private val sunriseApi: SunriseApi = SunriseApi.create()

  val sunriseData: MutableLiveData<SunriseResponse?> = MutableLiveData()

  fun load(arguments: Bundle?) {
    val call = sunriseApi.getSunriseAndSunset(36.7201600, 4.4203400)
    call.enqueue(object : Callback<SunriseResponse> {

      override fun onResponse(call: Call<SunriseResponse>, response: Response<SunriseResponse>) {
        if (response.isSuccessful) {
          sunriseData.value = response.body()
        } else {
          // Show error
        }
      }

      override fun onFailure(call: Call<SunriseResponse>, t: Throwable) {
        t.printStackTrace()
      }
    })

  }
}
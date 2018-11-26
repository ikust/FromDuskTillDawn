package com.raywenderlich.android.rwandroidtutorial

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.raywenderlich.android.rwandroidtutorial.data.Coordinates
import com.raywenderlich.android.rwandroidtutorial.data.SunSchedule
import com.raywenderlich.android.rwandroidtutorial.repository.SunriseSunsetRepository

class LocationDetailViewModel(app: Application) : AndroidViewModel(app) {

  companion object {
    private const val COORDINATES_ARGUMENT = "coordinates"

    fun createIntent(context: Context, work: Coordinates): Intent {
      val intent = Intent(context, LocationDetailActivity::class.java)
      intent.putExtra(COORDINATES_ARGUMENT, work)

      return intent
    }
  }

  private val repository = SunriseSunsetRepository(app)

  val locationSunTimetable = MediatorLiveData<SunSchedule?>()

  fun load(params: Bundle?) {
    val coordinates = params?.get(COORDINATES_ARGUMENT) as? Coordinates

    if(coordinates != null) {
      locationSunTimetable.addSource(repository.getSunriseSunset(
          coordinates.latitude,
          coordinates.longitude
      )) { value -> locationSunTimetable.value = value }
    } else {
      locationSunTimetable.value = null
    }
  }
}

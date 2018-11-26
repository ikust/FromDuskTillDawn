package com.raywenderlich.android.rwandroidtutorial.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.raywenderlich.android.rwandroidtutorial.data.Coordinates
import com.raywenderlich.android.rwandroidtutorial.data.LocationSunTimetable
import com.raywenderlich.android.rwandroidtutorial.repository.SunriseSunsetRepository

class MainViewModel(app: Application) : AndroidViewModel(app) {

  private val repository = SunriseSunsetRepository(app)

  val currentLocationSunTimetable = MediatorLiveData<LocationSunTimetable?>()

  fun load() {
    currentLocationSunTimetable.addSource(repository.getSunriseSunset()) { value ->
      currentLocationSunTimetable.value = value
    }
  }

  fun searchFor(locationName: String): LiveData<Coordinates?> {
    return repository.getCoordinates(locationName)
  }
}
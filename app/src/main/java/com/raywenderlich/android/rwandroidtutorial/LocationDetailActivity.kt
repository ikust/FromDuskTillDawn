package com.raywenderlich.android.rwandroidtutorial

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_location_detail.*

class LocationDetailActivity : AppCompatActivity() {

  private lateinit var viewModel: LocationDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_location_detail)

    viewModel = ViewModelProviders.of(this).get(LocationDetailViewModel::class.java)

    viewModel.locationSunTimetable.observe(this, Observer { sunTimetable ->
      tvLocation.text = sunTimetable?.locationName
      tvSunrise.text = sunTimetable?.sunrise
      tvSunset.text = sunTimetable?.sunset
    })

    viewModel.load(intent.extras)
  }

}
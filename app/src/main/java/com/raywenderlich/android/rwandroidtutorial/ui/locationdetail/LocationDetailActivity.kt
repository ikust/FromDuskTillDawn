package com.raywenderlich.android.rwandroidtutorial.ui.locationdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.raywenderlich.android.rwandroidtutorial.R
import com.raywenderlich.android.rwandroidtutorial.formatTimeString
import kotlinx.android.synthetic.main.activity_location_detail.*

class LocationDetailActivity : AppCompatActivity() {

  private lateinit var viewModel: LocationDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_location_detail)

    progressBar.visibility = View.VISIBLE

    viewModel = ViewModelProviders.of(this).get(LocationDetailViewModel::class.java)

    viewModel.locationSunTimetable.observe(this, Observer { sunTimetable ->
      progressBar.visibility = View.GONE

      tvLocation.text = sunTimetable?.locationName
      tvSunrise.text = formatTimeString(this, R.string.sunrise_format, sunTimetable?.sunrise)
      tvSunset.text = formatTimeString(this, R.string.sunset_format, sunTimetable?.sunset)
    })

    viewModel.load(intent.extras)
  }

}
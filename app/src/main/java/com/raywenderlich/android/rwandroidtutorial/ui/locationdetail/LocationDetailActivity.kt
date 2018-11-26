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

package com.raywenderlich.android.rwandroidtutorial.ui.locationdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.raywenderlich.android.rwandroidtutorial.R
import com.raywenderlich.android.rwandroidtutorial.formatTimeString
import com.raywenderlich.android.rwandroidtutorial.openUrlInBrowser
import kotlinx.android.synthetic.main.activity_location_detail.*

class LocationDetailActivity : AppCompatActivity() {

  private lateinit var viewModel: LocationDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_location_detail)

    progressBar.visibility = View.VISIBLE

    tvSunriseSunsetApi.setOnClickListener { openUrlInBrowser(this, getString(R.string.sunrise_sunset_page)) }

    viewModel = ViewModelProviders.of(this).get(LocationDetailViewModel::class.java)

    viewModel.locationSunTimetable.observe(this, Observer { sunTimetable ->
      progressBar.visibility = View.GONE

      tvLocation.text = sunTimetable?.locationName
      tvSunrise.text = formatTimeString(this, R.string.sunrise_format, sunTimetable?.sunrise)
      tvSunset.text = formatTimeString(this, R.string.sunset_format, sunTimetable?.sunset)
      tvNoon.text = formatTimeString(this, R.string.noon_format, sunTimetable?.noon)
      tvDayLength.text = formatTimeString(this, R.string.day_length, sunTimetable?.dayLength)
    })

    viewModel.load(intent.extras)
  }

}
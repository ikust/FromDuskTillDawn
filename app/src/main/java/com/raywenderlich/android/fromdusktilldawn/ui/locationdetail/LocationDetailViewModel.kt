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

package com.raywenderlich.android.fromdusktilldawn.ui.locationdetail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.raywenderlich.android.fromdusktilldawn.data.Coordinates
import com.raywenderlich.android.fromdusktilldawn.data.LocationSunTimetable
import com.raywenderlich.android.fromdusktilldawn.repository.SunriseSunsetRepository

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

  val locationSunTimetable = MediatorLiveData<LocationSunTimetable?>()

  fun load(intent: Intent) {
    val coordinates = if(intent.data != null) {
      Coordinates(
          intent.data?.getQueryParameter("lat")?.toDouble() ?: 0.0,
          intent.data?.getQueryParameter("lng")?.toDouble() ?: 0.0
      )
    } else {
      intent.extras?.get(COORDINATES_ARGUMENT) as? Coordinates
    }

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

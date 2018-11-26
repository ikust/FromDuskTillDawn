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

package com.raywenderlich.android.rwandroidtutorial.ui.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.raywenderlich.android.rwandroidtutorial.R
import com.raywenderlich.android.rwandroidtutorial.formatTimeString
import com.raywenderlich.android.rwandroidtutorial.ui.locationdetail.LocationDetailViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

  private lateinit var viewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    viewModel.currentLocationSunTimetable.observe(this, Observer { sunTimetable ->
      tvLocation.text = sunTimetable?.locationName ?: getString(R.string.couldnt_find_location)
      tvSunrise.text = formatTimeString(this, R.string.sunrise_format, sunTimetable?.sunrise)
      tvSunset.text = formatTimeString(this, R.string.sunset_format, sunTimetable?.sunset)
    })

    etSearch.setOnEditorActionListener { textView, actionId, _ ->
      when (actionId) {
        EditorInfo.IME_ACTION_SEARCH -> {
          hideKeyboard(textView)
          searchForLocation(textView.text.toString())
          true
        }
        EditorInfo.IME_ACTION_DONE -> {
          searchForLocation(textView.text.toString())
          true
        }
        else -> false
      }
    }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      viewModel.load()
    } else {
      // Show rationale and request permission.
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == 0) {
      if (permissions.size == 1 &&
          permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION &&
          grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        viewModel.load()
      } else {
        tvLocation.text = getString(R.string.location_permission_denied)
      }
    }
  }

  private fun searchForLocation(locationName: String) {
    viewModel.searchFor(locationName).observe(this, Observer { coordinates ->
      if (coordinates != null) {
        startActivity(LocationDetailViewModel.createIntent(this, coordinates))
      } else {
        AlertDialog.Builder(this)
            .setMessage(R.string.cant_find_given_location)
            .setPositiveButton(R.string.ok, null)
            .show()
      }
    })
  }

  private fun hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }
}

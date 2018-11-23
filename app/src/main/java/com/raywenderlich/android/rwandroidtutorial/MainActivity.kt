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

package com.raywenderlich.android.rwandroidtutorial

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Main Screen
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

  private lateinit var viewModel: MainViewModel

  private lateinit var fusedLocationClient: FusedLocationProviderClient


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {

      viewModel.load(intent.extras)
      viewModel.sunriseData.observe(this, Observer {
        tvLocation.text = it?.locationName ?: "Couldn't find your location"
        tvSunrise.text = it?.sunrise
        tvSunset.text = it?.sunset
      })
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
        viewModel.load(intent.extras)
      } else {
        // Permission was denied. Display an error message.
      }

    }
  }
}

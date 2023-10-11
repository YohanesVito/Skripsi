package com.example.mokuramqtt.services

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mokuramqtt.Constants.Companion.locationPermissionCode

class DataRepository(private val context: Context) : LocationListener {
    private val _dataLocation = MutableLiveData<Array<Double>>()
    val dataLocation: LiveData<Array<Double>> = _dataLocation

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            1f,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        val dataLocation = arrayOf(location.latitude, location.longitude)
        _dataLocation.postValue(dataLocation)
    }
}

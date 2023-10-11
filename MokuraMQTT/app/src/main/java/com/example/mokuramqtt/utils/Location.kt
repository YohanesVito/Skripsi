package com.example.mokuramqtt.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mokuramqtt.Constants.Companion.locationPermissionCode
import com.example.mokuramqtt.viewmodel.MonitorViewModel

class Location(private val context: Context, private val monitorViewModel: MonitorViewModel): LocationListener {

    fun setUpLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, this)
    }

    override fun onLocationChanged(location: Location) {
        val dataLocation = arrayOf(location.latitude,location.longitude)
        monitorViewModel.dataLocation.value = dataLocation
    }

}
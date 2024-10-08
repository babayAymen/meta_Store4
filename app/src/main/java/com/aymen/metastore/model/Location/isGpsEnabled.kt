package com.aymen.metastore.model.Location

import android.content.Context
import android.location.LocationManager

fun Context.isGpsEnabled(): Boolean{
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
     locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
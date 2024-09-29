package com.aymen.metastore.model.Location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context : Context,
    private val client : FusedLocationProviderClient
): LocationClient {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun getLocatipnUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


            if(!isGpsEnabled && !isNetworkEnabled){
             //   throw LocationClient.LocationException("GPS is disabled")
                val enableGpsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                enableGpsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(enableGpsIntent)


                // Inform the user
                Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()

                // Register a receiver to listen for location provider changes
                val locationProviderChangeReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {

                        if (isGpsEnabled || isNetworkEnabled) {
                            context.unregisterReceiver(this) // Unregister the receiver
                            // Continue with location tracking or other tasks

                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_START
                                context.startService(this)
                            }
                        }
                    }
                }

                context.registerReceiver(
                    locationProviderChangeReceiver,
                    IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
                    Context.RECEIVER_NOT_EXPORTED
                )


                // You can also add a check to wait for the user to enable GPS before proceeding
                // This example just returns and won't continue until GPS is enabled
                return@callbackFlow

            }



            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500L)
                .setMaxUpdateDelayMillis(10000L)
                .build()

            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }

                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                context.mainLooper
            )
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        }
    }


}




















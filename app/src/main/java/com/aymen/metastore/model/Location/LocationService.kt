package com.aymen.metastore.model.Location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

//class LocationService : Service() {
//
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//    private lateinit var locationClient: LocationClient
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        locationClient = DefaultLocationClient(
//            applicationContext,
//            LocationServices.getFusedLocationProviderClient(applicationContext)
//        )
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//       when(intent?.action){
//           ACTION_START -> start()
//           ACTION_STOP -> stop()
//       }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private fun start() {
////        if (!hasLocationPermission()) {
////            showPermissionRequiredNotification()
////        } else {
////            val notification = NotificationCompat.Builder(
////                this, "location"
////            )
////                .setContentTitle("Tracking location ...")
////                .setContentText("Location : null")
////                .setSmallIcon(R.drawable.ic_launcher_background)
////                .setOngoing(true)
////
////            val notificationManager =
////                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            locationClient
//                .getLocatipnUpdates(1000L)
//                .catch { e -> e.printStackTrace() }
//                .onEach { location ->
//                    val lat = location.latitude.toString()
//                    val long = location.longitude.toString()
//                    Log.d("LocationService", "Location: ($lat, $long)")
////                    val updatedNotification = notification.setContentText(
////                        "Location: ($lat, $long)"
////                    )
////
////                    notificationManager.notify(1, updatedNotification.build())
//                }
//                .launchIn(serviceScope)
//
////            startForeground(1, notification.build())
//        }
////    }
//
//    private fun stop(){
//        stopForeground(Service.STOP_FOREGROUND_REMOVE)
//        stopSelf()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        serviceScope.cancel()
//    }
//    companion object {
//        const val ACTION_START = "ACTION_START"
//
//        const val ACTION_STOP = "ACTION_STOP"
//    }
//
////    private fun showPermissionRequiredNotification() {
////        // Notify the user that location permission is required
////        val notification = NotificationCompat.Builder(
////            this, "permission"
////        )
////            .setContentTitle("Location Permission Required")
////            .setContentText("Please grant location permissions to use this feature.")
////            .setSmallIcon(R.drawable.ic_launcher_background)
////            .setPriority(NotificationCompat.PRIORITY_HIGH)
////
////        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        notificationManager.notify(1, notification.build())
////    }
//}

//@AndroidEntryPoint
//class LocationService : LifecycleService() {
//
//    // Injected dependencies
//    @Inject
//    lateinit var locationClient: LocationClient
//
//    @Inject
//    lateinit var sharedViewModel: SharedViewModel
//
//
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//    private lateinit var locationManager: LocationManager
//
//    override fun onBind(intent: Intent): IBinder? {
//        super.onBind(intent)
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        startLocationTracking()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        when (intent?.action) {
//            ACTION_START -> startLocationTracking()
//            ACTION_STOP -> stopLocationTracking()
//        }
//        return START_STICKY
//    }
//
//    private fun startLocationTracking() {
//        if (isLocationEnabled()) {
//            locationClient
//                .getLocatipnUpdates(1000L)
//                .catch { e -> Log.e("LocationService", "Error getting location updates", e) }
//                .onEach { location ->
//                    val lat = location.latitude
//                    val long = location.longitude
//                    Log.d("LocationService", "Location: ($lat, $long)")
//
//                    // Update SharedViewModel
//                    sharedViewModel.updateLocation(Pair(lat, long))
//                }
//                .launchIn(serviceScope)
//
//    }else
//    {
//        Log.w("LocationService", "Location services are disabled. Cannot start tracking.")
//        // Optionally, notify the user that location services are disabled
//        stopSelf()
//    }
//}
//    private fun isLocationEnabled(): Boolean {
//        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        return isGpsEnabled || isNetworkEnabled
//    }
//
//
//    private fun stopLocationTracking(){
//        serviceScope.cancel()
//        stopSelf()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        serviceScope.cancel()
//    }
//
//    companion object {
//        const val ACTION_START = "ACTION_START"
//        const val ACTION_STOP = "ACTION_STOP"
//    }
//}


@AndroidEntryPoint
class LocationService : LifecycleService() {

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var appViewModel: AppViewModel

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var locationManager: LocationManager

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Initialize locationManager here
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Now you can safely start location tracking
        startLocationTracking()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> startLocationTracking()
            ACTION_STOP -> stopLocationTracking()
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocationTracking() {
        if (isLocationEnabled()) {
            locationClient
                .getLocatipnUpdates(1000L)
                .catch { e -> Log.e("LocationService", "Error getting location updates", e) }
                .onEach { location ->
                    val lat = location.latitude
                    val long = location.longitude
                    Log.d("LocationService", "Location: ($lat, $long)")

                    // Update SharedViewModel
                    appViewModel.updateLocation(Pair(lat, long))
                }
                .launchIn(serviceScope)
        } else {
            Log.w("LocationService", "Location services are disabled. Cannot start tracking.")
//            notifyUserToEnableLocation()
            stopSelf() // Optionally, stop the service if location is not enabled
        }
    }

//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun notifyUserToEnableLocation() {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = "location"
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Location Service Channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//
//        // Use FLAG_IMMUTABLE for targeting Android 12 and above
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification: Notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Enable Location Services")
//            .setContentText("Please enable location services to start tracking.")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        startForeground(1, notification)
//    }

    private fun isLocationEnabled(): Boolean {
        // Check if either GPS or Network location providers are enabled
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    private fun stopLocationTracking() {
        serviceScope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}

























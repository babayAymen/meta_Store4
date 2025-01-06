package com.aymen.metastore.dependencyInjection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi

object NetworkUtil {
    private var isConnected = false
    private var initialToastDisplayed = false
    private var networkCallbackRegistered = false

    @RequiresApi(Build.VERSION_CODES.N)
    @Synchronized
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        // Check the current network state
        val hasNetworkConnection = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> true
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> true
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> true
            else -> false
        }

        if (!hasNetworkConnection && !initialToastDisplayed) {
            // Notify the user about no initial network connection
            initialToastDisplayed = true
            showToast(context, "No network connection!")
        }
        isConnected = hasNetworkConnection

        // Register the callback for future network changes, if not already registered
        if (!networkCallbackRegistered) {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (!isConnected) {
                        isConnected = true
                        showToast(context, "Network connection is back!")
                    }
                }

                override fun onLost(network: Network) {
                    isConnected = false
                    showToast(context, "No network connection!")
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            networkCallbackRegistered = true
        }

        return hasNetworkConnection
    }

    private fun showToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

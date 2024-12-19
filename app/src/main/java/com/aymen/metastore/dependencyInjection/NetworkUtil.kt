package com.aymen.metastore.dependencyInjection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object NetworkUtil {
    private var isConnected = false
    private var initialToastDisplayed = false

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        // Check the initial network state
        val hasNetworkConnection = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> true
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> true
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> true
            else -> false
        }

        if (!hasNetworkConnection  && !initialToastDisplayed) {
            // Notify the user about no initial network connection
            initialToastDisplayed = true

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show()
            }
        }
        isConnected = hasNetworkConnection

        // Register the callback for future network changes
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isConnected) {
                    isConnected = true
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Network connection is back!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onLost(network: Network) {
                isConnected = false
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        return hasNetworkConnection
    }
}
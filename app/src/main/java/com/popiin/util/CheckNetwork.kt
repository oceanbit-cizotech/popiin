package com.popiin.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class CheckNetwork {
    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            return isNetworkConnected(context)
        }

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) // Ensures internet access
        }
        fun isInternet(): Boolean {
            return try {
                val url = URL("https://clients3.google.com/generate_204") // Lightweight Google internet check
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 3000 // 3 seconds timeout
                connection.readTimeout = 3000
                connection.requestMethod = "GET"
                connection.connect()
                connection.responseCode == 204 // No content response means internet is available
            } catch (e: Exception) {
                false
            }
        }

        fun checkInternet(context: Context, callback: (Boolean) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val isConnected = isNetworkConnected(context) // Fast check
                val isInternetWorking = if (isConnected) isInternet() else false // Actual test
                withContext(Dispatchers.Main) {
                    callback(isInternetWorking)
                }
            }
        }
    }

}

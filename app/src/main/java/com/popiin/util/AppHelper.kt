package com.popiin.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class AppHelper {
    // private val TAG = "_" + AppHelper::class.java.simpleName
//    fun isLocationProviderEnabled(context: Context): Boolean {
//        val enableGPS: Boolean
//        val enableNetwork: Boolean
//        val lmngr = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        enableNetwork = lmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        enableGPS = lmngr.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        Log.d("is gps enabled", "-$enableGPS----$enableNetwork")
//        return enableGPS
//    }

    private var bestLocation: Location? = null

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Activity): Location? {
        val enableGPS: Boolean
        val enableNetwork: Boolean
        var locationGps: Location? = null
        var locationNetwork: Location? = null
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        enableNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        enableGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d("is gps enabled", "-$enableGPS----$enableNetwork")
        val locationListener = LocationListener { location ->
            bestLocation = location
            Log.e("changed location", bestLocation!!.latitude.toString() + " received")
        }
        if (enableNetwork) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener)
            locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (enableGPS) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener)
            locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (enableNetwork && enableGPS) {
            if (locationGps != null && locationNetwork != null) {
                bestLocation = if (locationGps.accuracy < locationNetwork.accuracy) {
                    locationGps
                } else locationNetwork
            } else if (locationGps != null) {
                bestLocation = locationGps
            } else if (locationNetwork != null) {
                bestLocation = locationNetwork
            }
        } else if (enableNetwork) bestLocation = locationNetwork else if (enableGPS) bestLocation =
            locationGps
        Log.d("last know location", "dd$bestLocation")
        locationManager.removeUpdates(locationListener)

        if (bestLocation == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient!!.lastLocation.addOnSuccessListener(context) { location ->
                if (location != null) {
                    bestLocation = location
                }
            }
        }

        return bestLocation
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null

//    fun getBounds(location: Location, mDistanceInMeters: Int): LatLngBounds {
//        val latRadian = Math.toRadians(location.getLatitude())
//        val degLatKm = 110.574235
//        val degLongKm = 110.572833 * Math.cos(latRadian)
//        val deltaLat = mDistanceInMeters / 1000.0 / degLatKm
//        val deltaLong = mDistanceInMeters / 1000.0 / degLongKm
//        val minLat: Double = location.getLatitude() - deltaLat
//        val minLong: Double = location.getLongitude() - deltaLong
//        val maxLat: Double = location.getLatitude() + deltaLat
//        val maxLong: Double = location.getLongitude() + deltaLong
//        Log.d(TAG,
//            "Min: " + java.lang.Double.toString(minLat) + "," + java.lang.Double.toString(minLong))
//        Log.d(TAG,
//            "Max: " + java.lang.Double.toString(maxLat) + "," + java.lang.Double.toString(maxLong))
//        return LatLngBounds(LatLng(minLat, minLong), LatLng(maxLat, maxLong))
//    }
}
package com.popiin.locationutils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.popiin.util.PrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.internal.ConnectionCallbacks
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


@Suppress("DEPRECATION")
class OceanLocation private constructor() : ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {
    var location : Location = getLocation()

    @JvmName("getLocation1")
    fun getLocation(): Location {
        return location
    }

    @JvmName("setListener1")
    fun setListener(listener: onLocationChangedListener) {
        this.listener = listener
    }

    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    private var listener : onLocationChangedListener? = null
    override fun onConnected(bundle: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 200
        locationRequest!!.fastestInterval = 200
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        try {
            if (ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient!!,
                    locationRequest!!,
                    this)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    override fun onLocationChanged(location: Location) {
        this.location = location
        PrefManager.lastLocation = location
        if (listener != null) {
            listener?.onLocationChange(location)
        }
    }

    @Synchronized
    fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(activity!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient!!.connect()
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: OceanLocation? = null
        @SuppressLint("StaticFieldLeak")
        var activity: Context? = null
        fun getInstance(act: Context?): OceanLocation? {
            activity = act
            if (instance == null) {
                instance = OceanLocation()
            }
            return instance
        }
    }
}
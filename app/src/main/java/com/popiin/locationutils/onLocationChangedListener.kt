package com.popiin.locationutils

import android.location.Location

abstract class onLocationChangedListener {
    fun onLocationChange(location: Location) {}
}
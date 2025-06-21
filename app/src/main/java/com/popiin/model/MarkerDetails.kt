package com.popiin.model

import com.google.android.gms.maps.model.Marker

open class MarkerDetails(var marker: Marker, var id: Int){
    override fun toString(): String {
        return "MarkerDetails(marker=${marker.toString()}, id='$id')"
    }
}
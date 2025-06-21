package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateVenueRes {
    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val isSuccess = false

    @Expose
    @SerializedName("data")
    var venue: Venue? = null

    inner class Venue {
        @Expose
        @SerializedName("id")
        var id = 0
    }
}
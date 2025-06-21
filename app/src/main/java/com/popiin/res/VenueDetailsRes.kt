package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.popiin.res.VenueListRes.Venue

class VenueDetailsRes {
    @Expose
    @SerializedName("data")
    val venues: Venue? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val isSuccess = false
}
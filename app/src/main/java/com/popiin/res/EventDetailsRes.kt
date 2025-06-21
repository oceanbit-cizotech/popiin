package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EventDetailsRes {
    @Expose
    @SerializedName("data")
    val data: EventListRes.Event? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false
}
package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EarningRes {
    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    @Expose
    @SerializedName("data")
    var earning:ArrayList<Earning>? = ArrayList()
}
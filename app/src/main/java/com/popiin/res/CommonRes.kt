package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonRes {
    @Expose
    @SerializedName("message")
    val msg: String? = null

    @Expose
    @SerializedName("success")
    val status = false
}

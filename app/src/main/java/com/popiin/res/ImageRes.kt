package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ImageRes {
    @Expose
    @SerializedName("url")
    val url: String? = null

    @Expose
    @SerializedName("url_dec")
    val url_dec: String? = null

    @Expose
    @SerializedName("success")
    val success = false
}
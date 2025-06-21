package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateStripeLinkRes(
    @SerializedName("success")
    @Expose
    val success: Boolean,

    @SerializedName("data")
    @Expose
    val data: Data,
) {
    data class Data(
        @SerializedName("object")
        @Expose
        val obj: String?,

        @SerializedName("url")
        @Expose
        val url: String?,
    )
}

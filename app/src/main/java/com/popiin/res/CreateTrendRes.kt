package com.popiin.res

import com.google.gson.annotations.SerializedName

data class CreateTrendRes(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val trend: VenueTrend
)


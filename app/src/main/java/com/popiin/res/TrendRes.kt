package com.popiin.res

import com.google.gson.annotations.SerializedName

data class TrendRes(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ArrayList<Trend>
)

data class Trend(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("summary")
    val summary: String,

    @SerializedName("start_datetime")
    val startDatetime: String,

    @SerializedName("end_datetime")
    val endDatetime: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)

package com.popiin.res

import com.google.gson.annotations.SerializedName

class VenueTrendRes (
     @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val venueTrend: ArrayList<VenueTrend>
)

data class VenueTrend(
    @SerializedName("id")
    val id: Int,

    @SerializedName("trend_id")
    val trend_id: Int,

    @SerializedName("venue_id")
    val venue_id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("banner_image")
    val banner_image: String,

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

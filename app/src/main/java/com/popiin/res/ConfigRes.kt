package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ConfigRes (
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: Config)

data class Config(
    @SerializedName("trendingflag")
    val trendingFlag: Int,

    @SerializedName("popularflag")
    val popularFlag: Int,

    @SerializedName("exploreScreenRadius")
    val exploreScreenRadius: Int,

    @SerializedName("mapScreenRadius")
    val mapScreenRadius: Int,

    @SerializedName("storyScreenRadius")
    val storyScreenRadius: Int
)
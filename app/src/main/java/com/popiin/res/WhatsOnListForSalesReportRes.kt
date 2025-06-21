package com.popiin.res
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class WhatsOnListForSalesReportRes(
    @SerializedName("data")
    @Expose
    var data: List<Data?>?,
    @SerializedName("message")
    @Expose
    var message: String?,
    @SerializedName("success")
    @Expose
    var success: Boolean
) {
    data class Data(
        @SerializedName("id")
        @Expose
        var id: Int?,
        @SerializedName("title")
        @Expose
        var title: String?
    )
}
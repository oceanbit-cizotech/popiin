package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Earning(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName(value = "name", alternate = ["venue_name"])
    val name: String,
    @Expose
    @SerializedName("start_date_time")
    val startDateTime: String,
    @Expose
    @SerializedName("end_date_time")
    val endDateTime: String,
    @Expose
    @SerializedName("unpaid_amount")
    var unpaidAmount: Double,
    @Expose
    @SerializedName("paid_amount")
    var paidAmount: Double,
    @Expose
    @SerializedName("total_bookings")
    var totalBookings: Int,
    @Expose
    @SerializedName("total_cancelled")
    var totalCancelled: Int,
    @Expose
    @SerializedName("withdrwal_request")
    var withdrwalRequest: String,
)

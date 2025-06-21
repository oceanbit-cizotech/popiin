package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WhatsonEarningRes(
    @SerializedName("data")
    @Expose
    var `data`: List<Data?>?,
    @SerializedName("message")
    @Expose
    var message: String?,
    @SerializedName("success")
    @Expose
    var success: Boolean?
) {
    data class Data(
        @SerializedName("created_at")
        @Expose
        var createdAt: String?,
        @SerializedName("email")
        @Expose
        var email: String?,
        @SerializedName("id")
        @Expose
        var id: Int?,
        @SerializedName("is_scanned")
        @Expose
        var isScanned: Int?,
        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String?,
        @SerializedName("platform_fees")
        @Expose
        var platformFees: Double?,
        @SerializedName("price")
        @Expose
        var price: Int?,
        @SerializedName("quantity")
        @Expose
        var quantity: Int?,
        @SerializedName("ref_number")
        @Expose
        var refNumber: String?,
        @SerializedName("status")
        @Expose
        var status: Int?,
        @SerializedName("stripe_trans_id")
        @Expose
        var stripeTransId: String?,
        @SerializedName("ticket_id")
        @Expose
        var ticketId: Int?,
        @SerializedName("updated_at")
        @Expose
        var updatedAt: String?,
        @SerializedName("user")
        @Expose
        var user: User?,
        @SerializedName("user_id")
        @Expose
        var userId: Int?,
        @SerializedName("venue")
        @Expose
        var venue: Venue?,
        @SerializedName("venue_id")
        @Expose
        var venueId: Int?,
        @SerializedName("whatson")
        @Expose
        var whatson: Whatson?,
        @SerializedName("whatson_id")
        @Expose
        var whatsonId: Int?
    ) {
        data class User(
            @SerializedName("first_name")
            @Expose
            var firstName: String?,
            @SerializedName("id")
            @Expose
            var id: Int?,
            @SerializedName("last_name")
            @Expose
            var lastName: String?
        )

        data class Venue(
            @SerializedName("id")
            @Expose
            var id: Int?,
            @SerializedName("venue_name")
            @Expose
            var venueName: String?
        )

        data class Whatson(
            @SerializedName("end_time")
            @Expose
            var endTime: String?,
            @SerializedName("id")
            @Expose
            var id: Int?,
            @SerializedName("title")
            @Expose
            var title: String?,
            @SerializedName("what_datetime")
            @Expose
            var whatDatetime: String?
        )
    }
}
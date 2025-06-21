package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

public class BookEventRes {
    @Expose
    @SerializedName("data")
    val data: Data? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class Data : Serializable {
        @Expose
        @SerializedName("id")
        val id = 0

        @Expose
        @SerializedName("updated_at")
        val updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("stripe_trans_id")
        val stripe_trans_id: String? = null

        @Expose
        @SerializedName("ref_number")
        val ref_number: String? = null

        @Expose
        @SerializedName("promo_code")
        val promo_code: String? = null

        @Expose
        @SerializedName("booking_type")
        val booking_type: String? = null

        @Expose
        @SerializedName("discounts")
        val discounts = 0

        @Expose
        @SerializedName("booking_fees")
        val booking_fees = 0.0

        @Expose
        @SerializedName("qty")
        val qty = 0

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("price")
        val price = 0.0

        @Expose
        @SerializedName("ticket_id")
        val ticket_id = 0

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("event_id")
        val event_id = 0
    }
}
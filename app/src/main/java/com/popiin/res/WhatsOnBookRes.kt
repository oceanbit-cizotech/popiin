package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WhatsOnBookRes {
    @Expose
    @SerializedName("data")
    var data: Data? = null

    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("id")
        var id = 0

        @Expose
        @SerializedName("updated_at")
        var updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null

        @Expose
        @SerializedName("stripe_trans_id")
        var stripe_trans_id: String? = null

        @Expose
        @SerializedName("ref_number")
        var ref_number: String? = null

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("price")
        var price = 0.0f

        @Expose
        @SerializedName("ticket_id")
        var ticket_id = 0

        @Expose
        @SerializedName("user_id")
        var user_id = 0
    }
}
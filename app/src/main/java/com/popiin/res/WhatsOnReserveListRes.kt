package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WhatsOnReserveListRes {
    @Expose
    @SerializedName("data")
    var data: List<Data>? = null

    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("updated_at")
        var updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("available_quantity")
        var available_quantity = 0

        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("price")
        var price = 0

        @Expose
        @SerializedName("ticket_type")
        var ticket_type: String? = null

        @Expose
        @SerializedName("whatson_id")
        var whatson_id = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }
}
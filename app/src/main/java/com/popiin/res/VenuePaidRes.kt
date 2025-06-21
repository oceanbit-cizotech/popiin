package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenuePaidRes {
    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("data")
    var data: List<Data>? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("source_type")
        var source_type: String? = null

        @Expose
        @SerializedName("reversed")
        var reversed = false

        @Expose
        @SerializedName("reversals")
        var reversals: Reversals? = null

        @Expose
        @SerializedName("metadata")
        var metadata: List<String>? = null

        @Expose
        @SerializedName("livemode")
        var livemode = false

        @Expose
        @SerializedName("destination_payment")
        var destination_payment: String? = null

        @Expose
        @SerializedName("destination")
        var destination: String? = null

        @Expose
        @SerializedName("currency")
        var currency: String? = null

        @Expose
        @SerializedName("created")
        var created = 0

        @Expose
        @SerializedName("balance_transaction")
        var balance_transaction: String? = null

        @Expose
        @SerializedName("amount_reversed")
        var amount_reversed = 0

        @Expose
        @SerializedName("amount")
        var amount = 0

        @Expose
        @SerializedName("object")
        var obj: String? = null


        @Expose
        @SerializedName("id")
        var id: String? = null
    }

    class Reversals {
        @Expose
        @SerializedName("url")
        var url: String? = null

        @Expose
        @SerializedName("total_count")
        var total_count = 0

        @Expose
        @SerializedName("has_more")
        var has_more = false

        @Expose
        @SerializedName("data")
        var data: List<String>? = null

        @Expose
        @SerializedName("object")
        var obj: String? = null

    }
}

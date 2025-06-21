package com.popiin.res

import com.popiin.res.ValidatePromoRes.ValidatePromo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ValidatePromoRes {
    @Expose
    @SerializedName("data")
    val validatePromoData: ValidatePromo? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class ValidatePromo {
        @Expose
        @SerializedName("updated_at")
        val updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("expired_date")
        val expired_date: String? = null

        @Expose
        @SerializedName("value")
        val value = 0.0

        @Expose
        @SerializedName("promo_code")
        val promo_code: String? = null

        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }
}
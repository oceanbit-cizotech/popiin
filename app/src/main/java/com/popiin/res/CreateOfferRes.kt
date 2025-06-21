package com.popiin.res

import com.popiin.res.VenueListRes.Offerslive
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateOfferRes {
    @Expose
    @SerializedName("success")
    var isSuccess = false

    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("data")
    var offers: Offerslive? = null
}
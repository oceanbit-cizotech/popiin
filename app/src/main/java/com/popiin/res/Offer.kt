package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Offer : Serializable {

    constructor(title: String) {
        this.title = title
    }

    @Expose
    @SerializedName("title")
    var title: String? = null

    @Expose
    @SerializedName("status")
    val status = 0

    @Expose
    @SerializedName("id")
    val id = 0


    @Expose
    @SerializedName("venue_id")
    val venue_id = 0

    @Expose
    @SerializedName("created_at")
    val created_at = ""

    @Expose
    @SerializedName("updated_at")
    val updated_at = ""
}

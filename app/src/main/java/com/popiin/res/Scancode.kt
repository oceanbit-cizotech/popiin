package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Scancode : Serializable {
    @Expose
    @SerializedName("is_scanned")
    var is_scanned = 0

    @Expose
    @SerializedName("booking_id")
    var booking_id = 0

    @Expose
    @SerializedName("id")
    var id = 0
}
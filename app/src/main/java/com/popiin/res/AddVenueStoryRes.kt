package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddVenueStoryRes {
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
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("video_url")
        var video_url: String? = null

        @Expose
        @SerializedName("video_id")
        var video_id: String? = null

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0
    }
}
package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VenueStoryListRes : Serializable {
    @Expose
    @SerializedName("total")
    var total = 0

    @Expose
    @SerializedName("data")
    var data: ArrayList<Data>? = null

    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data : Serializable {
        @Expose
        @SerializedName("updated_at")
        var updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null


        @Expose
        @SerializedName("venue")
        var venue: VenueListRes.Venue? = null

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("video_url")
        var video_url: String? = null

        @Expose
        @SerializedName("story_text")
        var story_text: String = ""

        @Expose
        @SerializedName("video_id")
        var video_id: String? = null

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0

        override fun toString(): String {
            return "Data(video_url=$video_url, id=$id)"
        }

        var isExpanded: Boolean = false // track expansion state

    }
}

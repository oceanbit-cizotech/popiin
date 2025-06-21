package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StoryListRes : Serializable {
    @Expose
    @SerializedName("data")
    var data: List<Data>? = null

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
    }
}
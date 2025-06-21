package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationsRes {
    @Expose
    @SerializedName("data")
    var data: ArrayList<Data> = ArrayList()

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
        @SerializedName("push_type")
        var push_type = 0

        @Expose
        @SerializedName("body")
        var body: String? = null

        @Expose
        @SerializedName("title")
        var title: String? = null

        @Expose
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }
}
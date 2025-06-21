package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class EventBookingAttendeesRes {
    @Expose
    @SerializedName("data")
    val data: List<Data>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class Data {
        @Expose
        @SerializedName("ticket")
        val ticket: Ticket? = null

        @Expose
        @SerializedName("user")
        val user: User? = null

        @Expose
        @SerializedName("event")
        val event: Event? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("is_scanned")
        val is_scanned = 0

        @Expose
        @SerializedName("ticket_id")
        val ticket_id = 0

        @Expose
        @SerializedName("qty")
        val qty = 0

        @Expose
        @SerializedName("special_request")
        val special_request: String? = null

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("event_id")
        val event_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Ticket {
        @Expose
        @SerializedName("price")
        val price = 0.0f

        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class User {
        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("first_name")
        val first_name: String? = null

        @Expose
        @SerializedName("last_name")
        val last_name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
        val indexCharter: Char
            get() {
                val sortString = first_name!!.substring(0, 1).uppercase(Locale.getDefault())
                var regex : Regex = "[A-Z]".toRegex()
                return if (sortString.matches(regex)) {
                    sortString.uppercase(Locale.getDefault())[0]
                } else {
                    "#"[0]
                }
            }
    }

    class Event {
        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }
}
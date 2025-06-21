package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class VenueBookingAttendeeRes {
    @Expose
    @SerializedName("data")
    var data: List<Data>? = null

    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("ticket")
        var ticket: Ticket? = null

        @Expose
        @SerializedName("user")
        var user: User? = null

        @Expose
        @SerializedName("venue")
        var venue: Venue? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("is_scanned")
        var is_scanned = 0

        @Expose
        @SerializedName("number_of_people")
        var number_of_people = 0

        @Expose
        @SerializedName("ticket_id")
        var ticket_id = 0

        @Expose
        @SerializedName("end_datetime")
        var end_datetime: String? = null

        @Expose
        @SerializedName("booking_datetime")
        var booking_datetime: String? = null

        @Expose
        @SerializedName("special_request")
        var special_request: String? = null

        @Expose
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class Ticket {
        @Expose
        @SerializedName("price")
        var price = 0

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class User {
        @Expose
        @SerializedName("profile_pic")
        var profile_pic: String? = null

        @Expose
        @SerializedName("last_name")
        var last_name: String? = null

        @Expose
        @SerializedName("first_name")
        var first_name: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
        val indexCharter: Char
            get() {
                val sortString = first_name!!.substring(0, 1).uppercase(Locale.getDefault())
                var regex: Regex = "[A-Z]".toRegex()
                return if (sortString.matches(regex)) {
                    sortString.uppercase(Locale.getDefault())[0]
                } else {
                    "#"[0]
                }
            }
    }

    class Venue {
        @Expose
        @SerializedName("venue_name")
        var venue_name: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
    }
}
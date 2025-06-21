package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.popiin.res.Scancode
import java.io.Serializable

class VenueAttendeesRes {
    @Expose
    @SerializedName("data")
    var data: ArrayList<Data>? = ArrayList()

    @Expose
    @SerializedName("success")
    var success = false

    class Data : Serializable {
        @Expose
        @SerializedName("scancode")
        var scancode: ArrayList<Scancode>? = null

        @Expose
        @SerializedName("ticket")
        var ticket: Ticket? = null

        @Expose
        @SerializedName("venue")
        var venue: Venue? = null

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
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("special_request")
        var special_request: String? = null

        @Expose
        @SerializedName("is_paid")
        var is_paid = 0

        @Expose
        @SerializedName("number_of_people")
        var number_of_people = 0

        @Expose
        @SerializedName("is_scanned")
        var is_scanned = 0

        @Expose
        @SerializedName("end_datetime")
        var end_datetime: String? = null

        @Expose
        @SerializedName("booking_datetime")
        var booking_datetime: String? = null

        @Expose
        @SerializedName("ratings")
        var ratings = 0

        @Expose
        @SerializedName("ref_number")
        var ref_number: String? = null

        @Expose
        @SerializedName("stripe_trans_id")
        var stripe_trans_id: String? = null

        @Expose
        @SerializedName("price")
        var price = 0.0

        @Expose
        @SerializedName("ticket_id")
        var ticket_id = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class Ticket : Serializable {
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

    class Venue : Serializable {
        @Expose
        @SerializedName("venue_banner_image")
        var venue_banner_image: String? = null

        @Expose
        @SerializedName("venue_address")
        var venue_address: String? = null

        @Expose
        @SerializedName("venue_name")
        var venue_name: String? = null

        @Expose
        @SerializedName("id")
        var id = 0

        @Expose
        @SerializedName("venue_latitude")
        var venue_latitude: Double? = null

        @Expose
        @SerializedName("venue_longitude")
        var venue_longitude: Double? = null

        @Expose
        @SerializedName("venue_share_link")
        var venue_share_link: String? = null
    }
}
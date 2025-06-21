package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList

class MyWhatsonBookRes {
    @Expose
    @SerializedName("data")
    var data: ArrayList<Data>? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("user")
        var user: User? = null

        @Expose
        @SerializedName("ticket")
        var ticket: Ticket? = null

        @Expose
        @SerializedName("scancode")
        var scancode: ArrayList<Scancode> = arrayListOf<Scancode>()

        @Expose
        @SerializedName("venue")
        var venue : Venue?=null

        @Expose
        @SerializedName("whatson")
        var whatson : Whatson?=null

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
        @SerializedName("is_scanned")
        var is_scanned = 0

        @Expose
        @SerializedName("stripe_trans_id")
        var stripe_trans_id: String? = null

        @Expose
        @SerializedName("ref_number")
        var ref_number: String? = null

        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("price")
        var price = 0.0f

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("ticket_id")
        var ticket_id = 0

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
    }

    class Ticket {
        @Expose
        @SerializedName("whatson")
        var whatson: Whatson? = null

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
        @SerializedName("available_quantity")
        var available_quantity = 0

        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("price")
        var price = 0.0f

        @Expose
        @SerializedName("ticket_type")
        var ticket_type: String? = null

        @Expose
        @SerializedName("whatson_id")
        var whatson_id = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class Whatson {
        @Expose
        @SerializedName("description")
        var description: String? = null

        @Expose
        @SerializedName("end_time")
        var end_time: String? = null

        @Expose
        @SerializedName("what_datetime")
        var what_datetime: String? = null

        @Expose
        @SerializedName("title")
        var title: String? = null

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0

        @Expose
        @SerializedName("whatsonimages")
        var whatsonimages : ArrayList<Whatsonimages> = arrayListOf<Whatsonimages>()


    }

    class Whatsonimages{
        @Expose
        @SerializedName("id")
        var id = 0

        @Expose
        @SerializedName("image_url")
        var imageUrl : String? = null

    }

    class Venue {
        @Expose
        @SerializedName("id")
        var id: Int = 0

        @Expose
        @SerializedName("venue_name")
        var venueName: String? = null

        @Expose
        @SerializedName("venue_address")
        var venue_address: String? = null

        @Expose
        @SerializedName("venue_latitude")
        var venue_latitude: Double? = null

        @Expose
        @SerializedName("venue_longitude")
        var venue_longitude: Double? = null


    }
}
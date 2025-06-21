package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.popiin.res.Scancode
import java.io.Serializable

class MyBookingRes {
    @Expose
    @SerializedName("data")
    val data: ArrayList<Data>? = ArrayList()

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class Data : Serializable {
        @Expose
        @SerializedName("images")
        val images: List<Images>? = null

        @Expose
        @SerializedName("scancode")
        val scancode: ArrayList<Scancode>? = null

        @Expose
        @SerializedName("event")
        val event: Event? = null

        @Expose
        @SerializedName("reff")
        val reff: List<String>? = null

        @Expose
        @SerializedName("updated_at")
        val updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("is_paid")
        var is_paid = 0

        @Expose
        @SerializedName("discounts")
        var discounts = 0.0
            private set

        @Expose
        @SerializedName("booking_fees")
        var booking_fees = 0.0
            private set

        @Expose
        @SerializedName("is_scanned")
        var is_scanned = 0

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
        @SerializedName("special_request")
        var special_request: String? = null

        @Expose
        @SerializedName("booking_type")
        var booking_type: String? = null

        @Expose
        @SerializedName("qty")
        var qty = 0

        @Expose
        @SerializedName("price")
        var price = 0.0
            private set

        @Expose
        @SerializedName("ticket_id")
        var ticket_id = 0

        @Expose
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("event_id")
        var event_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
        fun setDiscounts(discounts: Int) {
            this.discounts = discounts.toDouble()
        }

        fun setBooking_fees(booking_fees: Int) {
            this.booking_fees = booking_fees.toDouble()
        }

        fun setPrice(price: Int) {
            this.price = price.toDouble()
        }
    }

    class Images : Serializable {
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
        @SerializedName("image")
        var image: String? = null

        @Expose
        @SerializedName("event_id")
        var event_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }

    class Event : Serializable {
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
        @SerializedName("is_draft")
        var is_draft = 0

        @Expose
        @SerializedName("share_link")
        var share_link: String? = null

        @Expose
        @SerializedName("total_ratings")
        var total_ratings = 0

        @Expose
        @SerializedName("ratings")
        val ratings = 0

        @Expose
        @SerializedName("other_information")
        val other_information: String? = null

        @Expose
        @SerializedName("has_ticket")
        var has_ticket = 0

        @Expose
        @SerializedName("is_public")
        var is_public = 0

        @Expose
        @SerializedName("is_boosted")
        var is_boosted = 0

        @Expose
        @SerializedName("is_promoted")
        var is_promoted = 0

        @Expose
        @SerializedName("total_capacity")
        var total_capacity = 0

        @Expose
        @SerializedName("last_entry")
        var last_entry: String? = null

        @Expose
        @SerializedName("entry_policy")
        var entry_policy: String? = null

        @Expose
        @SerializedName("dress_code")
        val dress_code: String? = null

        @Expose
        @SerializedName("entertainment")
        private val entertainment: String? = null

        @Expose
        @SerializedName("music_djlineup")
        private val music_djlineup: String? = null

        //        public String getCategory() {
        //            return category;
        //        }
        @Expose
        @SerializedName("music")
        var music: String? = null

        @Expose
        @SerializedName("end_date_time")
        var end_date_time: String? = null

        @Expose
        @SerializedName("start_date_time")
        val start_date_time: String? = null

        @Expose
        @SerializedName("postal_code")
        var postal_code: String? = null

        @Expose
        @SerializedName("longi")
        var longi = 0.0

        @Expose
        @SerializedName("lat")
        val lat = 0.0

        @Expose
        @SerializedName("state")
        val state: String? = null

        @Expose
        @SerializedName("city")
        var city: String? = null

        @Expose
        @SerializedName("address")
        var address: String? = null

        @Expose
        @SerializedName("venue")
        var venue: String? = null

        @Expose
        @SerializedName("age")
        var age: String? = null

        @Expose
        @SerializedName("description")
        var description: String? = null

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("user_id")
        var user_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
    }
}
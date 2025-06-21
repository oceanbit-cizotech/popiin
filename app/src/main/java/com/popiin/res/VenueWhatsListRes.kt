package com.popiin.res

import com.google.gson.annotations.SerializedName

class VenueWhatsListRes {
    @SerializedName("data")
    val data: ArrayList<Data> = ArrayList()

    @SerializedName("message")
    val message: String? = null

    @SerializedName("success")
    val success = false

    class Data {
        @SerializedName("whatsonimages")
        val whatsonimages: List<Whatsonimages>? = null

        @SerializedName("whatsonticket")
        val whatsonticket: List<Whatsonticket>? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("status")
        val status = 0

        @SerializedName("whatson_djline_up")
        val whatson_djline_up: String? = null

        @SerializedName("whatson_entry_policy")
        val whatson_entry_policy: String? = null

        @SerializedName("whatson_dress_code")
        val whatson_dress_code: String? = null

        @SerializedName("other_information")
        val other_information: String? = null

        @SerializedName("entertainment")
        val entertainment: String? = null

        @SerializedName("other_music")
        val other_music: String? = null

        @SerializedName("end_time")
        val end_time: String? = null

        @SerializedName("what_datetime")
        val what_datetime: String? = null

        @SerializedName("music")
        val music: String? = null

        @SerializedName("description")
        val description: String? = null

        @SerializedName("title")
        val title: String? = null

        @SerializedName("venue_id")
        val venue_id = 0

        @SerializedName("id")
        val id = 0
    }

    class Whatsonimages {
        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("status")
        val status = 0

        @SerializedName("image_url")
        val image_url: String? = null

        @SerializedName("venue_whats_on_id")
        val venue_whats_on_id = 0

        @SerializedName("id")
        val id = 0
    }

    class Whatsonticket {
        @SerializedName("quantity")
        var quantity = 0

        @SerializedName("ticket_type")
        var ticket_type: String? = null

        @SerializedName("price")
        var price:Float? =null

        @SerializedName("whatson_id")
        var whatson_id = 0

        @SerializedName("venue_id")
        var venue_id = 0

        @SerializedName("id")
        var id = 0

        constructor(
            quantity: Int,
            ticket_type: String?,
            price: Float,
            whatson_id: Int,
            venue_id: Int,
            id: Int,
        ) {
            this.quantity = quantity
            this.ticket_type = ticket_type
            this.price = price
            this.whatson_id = whatson_id
            this.venue_id = venue_id
            this.id = id
        }


        constructor()

        constructor(
            quantity: Int,
            ticket_type: String?,
            price: Float,
            venue_id: Int,
        ) {
            this.quantity = quantity
            this.ticket_type = ticket_type
            this.price = price
            this.venue_id = venue_id
        }


        override fun toString(): String {
            return "Whatsonticket(quantity=$quantity, ticket_type=$ticket_type, price=$price, whatson_id=$whatson_id, venue_id=$venue_id, id=$id)"
        }


    }
}
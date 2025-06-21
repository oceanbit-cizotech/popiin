package com.popiin.res


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EventListRes : Serializable {

    @Expose
    @SerializedName("total")
    private val total = 0

    @Expose
    @SerializedName("message")
    private val message: String? = null

    @Expose
    @SerializedName("data")
    private var events: ArrayList<Event?> = ArrayList()

    @Expose
    @SerializedName("success")
    private val success = false

    fun getTotal(): Int {
        return total
    }

    fun getMessage(): String? {
        return message
    }

    fun getEvents(): ArrayList<Event?> {
        return events
    }

    fun getSuccess(): Boolean {
        return success
    }

    class Event : Serializable {
        @Expose
        @SerializedName("tickets")
        val tickets: ArrayList<Tickets>? = null

        @Expose
        @SerializedName("images")
        val images: ArrayList<Images>? = null

        @SerializedName("whatsonimages")
        val whatsonimages: ArrayList<Whatsonimages>? = null

        @SerializedName("whatsonticket")
        val whatsonticket: ArrayList<Whatsonticket>? = null

        @Expose
        @SerializedName("venueevent")
        val venueevent: VenueEvent? = null

        @Expose
        @SerializedName("updated_at")
        val updated_at: String? = null

        @Expose
        @SerializedName("type")
        val type: String? = null

        @Expose
        @SerializedName("what_datetime")
        val what_datetime: String? = null

        @Expose
        @SerializedName("end_time")
        val end_time: String? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("is_draft")
        val is_draft = 0

        @Expose
        @SerializedName("share_link")
        val share_link: String? = null

        @Expose
        @SerializedName("total_ratings")
        val total_ratings = 0

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
        val is_public = 0

        @Expose
        @SerializedName("is_boosted")
        val is_boosted = 0

        @Expose
        @SerializedName("is_promoted")
        val is_promoted = 0

        @Expose
        @SerializedName("total_capacity")
        val total_capacity = 0

        @Expose
        @SerializedName("last_entry")
        val last_entry: String? = null

        @Expose
        @SerializedName("entry_policy")
        val entry_policy: String? = null

        @Expose
        @SerializedName("whatson_entry_policy")
        val whatson_entry_policy: String? = null

        @Expose
        @SerializedName("dress_code")
        val dress_code: String? = null

        @Expose
        @SerializedName("whatson_dress_code")
        val whatson_dress_code: String? = null

        @Expose
        @SerializedName("category")
        val category: String? = null

        @Expose
        @SerializedName("entertainment")
        var entertainment: String? = null

        @Expose
        @SerializedName("music")
        var music: String? = null

        @Expose
        @SerializedName("music_djlineup")
        val music_djlineup: String? = null

        @Expose
        @SerializedName("whatson_djline_up")
        val whatson_djline_up: String? = null

        @Expose
        @SerializedName("end_date_time")
        val end_date_time: String? = null

        @Expose
        @SerializedName("start_date_time")
        val start_date_time: String? = null

        @Expose
        @SerializedName("postal_code")
        val postal_code: String? = null

        @Expose
        @SerializedName("longi")
        val longi = 0.0

        @Expose
        @SerializedName("lat")
        val lat = 0.0

        @Expose
        @SerializedName("state")
        val state: String? = null

        @Expose
        @SerializedName("city")
        val city: String? = null

        @Expose
        @SerializedName("address")
        val address: String? = null

        @Expose
        @SerializedName("venue")
        val venue: String? = null

        @Expose
        @SerializedName("age")
        val age: String? = null

        @Expose
        @SerializedName("description")
        val description: String? = null

        @Expose
        @SerializedName("title")
        val title: String? = null

        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
        var lowestPrice = 0.0
        var isFavorite = 0
        var isSelected = false

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is Event && this.id == other.id
        }
    }

    class Tickets(
        @field:SerializedName("ticket_type")
        @field:Expose val ticket_type: String,
        @field:SerializedName("available_quantity")
        @field:Expose val available_quantity: Int,
        @field:SerializedName("quantity")
        @field:Expose val quantity: Int,
        price: Int, name: String, event_id: Int, id: Int,
    ) :
        Serializable, Comparable<Tickets?> {
        @Expose
        @SerializedName("price")
        val price: Double

        @Expose
        @SerializedName("name")
        val name: String

        @Expose
        @SerializedName("event_id")
        val event_id: Int

        @Expose
        @SerializedName("id")
        val id: Int
        var isSelected = false


        init {
            this.price = price.toDouble()
            this.name = name
            this.event_id = event_id
            this.id = id
        }

        override fun compareTo(t2: Tickets?): Int {
            return if (price > t2!!.price) {
                1
            } else if (price < t2.price) {
                -1
            } else {
                0
            }
        }
    }

    class Images(
        @field:SerializedName("image") @field:Expose val image: String,
        @field:SerializedName(
            "event_id"
        ) @field:Expose val event_id: Int,
        @field:SerializedName("id") @field:Expose val id: Int,
    ) : Serializable


    class Whatsonimages : Serializable {
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

    class Whatsonticket(
        @field:SerializedName("quantity")
        @field:Expose val quantity: Int,

        @field:SerializedName("ticket_type")
        @field:Expose val ticket_type: String,

        @field:SerializedName("whatson_id")
        @field:Expose val whatson_id: Int,

        @field:SerializedName("venue_id")
        @field:Expose val venue_id: Int,
        price :Double ,id:Int
    ) : Serializable ,Comparable<Whatsonticket?> {

        @SerializedName("id")
        var id = 0

        @SerializedName("price")
        var price = 0.0
        var isSelected = false


        init {
            this.price = price.toDouble()
            this.id = id
        }


        override fun compareTo(t2: Whatsonticket?): Int {
            return if (price > t2!!.price) {
                1
            } else if (price < t2.price) {
                -1
            } else {
                0
            }
        }

    }

    class VenueEvent : Serializable {
        @SerializedName("venue_postal_code")
        val venue_postal_code: String? = null

        @SerializedName("venue_city")
        val venue_city: String? = null

        @SerializedName("venue_longitude")
        val venue_longitude = 0.0

        @SerializedName("venue_latitude")
        val venue_latitude = 0.0

        @SerializedName("venue_address")
        val venue_address: String? = null

        @SerializedName("venue_name")
        val venue_name: String? = null

        @SerializedName("id")
        var id = 0

    }
}
package com.popiin.res

import com.popiin.res.FavouriteEventsRes.FavouriteEvent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.popiin.res.EventListRes.Tickets
import java.io.Serializable

class FavouriteEventsRes: Serializable {
    @Expose
    @SerializedName("data")
    val favouriteEvent: List<FavouriteEvent>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class FavouriteEvent : Serializable {
        @Expose
        @SerializedName("tickets")
        val tickets: List<Tickets>? = null

        @Expose
        @SerializedName("images")
        val images: ArrayList<Images>? = ArrayList()

        @Expose
        @SerializedName("event")
        val event: Event? = null

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
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("event_id")
        val event_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Tickets(
        @field:SerializedName("ticket_type")
        @field:Expose val ticket_type: String?=null,

        @field:SerializedName("available_quantity")
        @field:Expose val available_quantity:Int= 0,

        @field:SerializedName("quantity")
        @field:Expose val quantity: Int,
        price: Int, name: String, event_id: Int, id: Int,
        ) : Serializable,Comparable<Tickets?>{

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
            } else if (price < t2!!.price) {
                -1
            } else {
                0
            }
        }
    }

    class Images : Serializable{
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
        @SerializedName("image")
        val image: String? = null

        @Expose
        @SerializedName("event_id")
        val event_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Event : Serializable{
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
        @SerializedName("entertainment")
        val entertainment: String? = null

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
        @SerializedName("dress_code")
        val dress_code: String? = null

        @Expose
        @SerializedName("category")
        val category: String? = null

        @Expose
        @SerializedName("music")
        var music: String? = null

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
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("id")
        val id = 0

        @Expose
        @SerializedName("music_djlineup")
        val music_djlineup: String? = null
        var isSelected = false
    }
}
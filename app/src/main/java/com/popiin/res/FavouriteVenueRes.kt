package com.popiin.res

import com.popiin.res.FavouriteVenueRes.FavouriteVenue
import com.popiin.res.VenueListRes.TimingsEntity
import com.popiin.res.VenueListRes.Offerslive
import com.popiin.res.VenueListRes.Menus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

class FavouriteVenueRes {
    @Expose
    @SerializedName("data")
    val favouriteVenue: List<FavouriteVenue>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class FavouriteVenue {
        @Expose
        @SerializedName("images")
        val images: List<Images>? = null

        @Expose
        @SerializedName("timings")
        var timingsEntities: List<TimingsEntity>? = null

        @Expose
        @SerializedName("venue")
        val venue: Venue? = null

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
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
        var isFavorite = 0

        @Expose
        @SerializedName("venuetypes")
        var venuetypes: List<VenueListRes.Venuetypes>? = null

        @Expose
        @SerializedName("offerstatic")
        val offers: ArrayList<Offer>? = null
    }

    inner class Offer : Serializable {
        @Expose
        @SerializedName("title")
        val title: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Images {
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
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Venue {
        @Expose
        @SerializedName("venue_other_information")
        val venue_other_information: String? = null

        @Expose
        @SerializedName("venue_share_link")
        val venue_share_link: String? = null

        @Expose
        @SerializedName("venue_ratings")
        val venue_ratings = 0

        @Expose
        @SerializedName("venue_last_entry")
        val venue_last_entry: String? = null

        @Expose
        @SerializedName("venue_door_policy")
        val venue_door_policy: String? = null

        @Expose
        @SerializedName("venue_dress_code")
        val venue_dress_code: String? = null

        @Expose
        @SerializedName("venue_music")
        val venue_music: String? = null

        @Expose
        @SerializedName("venue_age_group")
        val venue_age_group: String? = null

        @Expose
        @SerializedName("venue_postal_code")
        val venue_postal_code: String? = null

        @Expose
        @SerializedName("venue_city")
        val venue_city: String? = null

        @Expose
        @SerializedName("venue_type")
        val venue_type: String? = null

        @Expose
        @SerializedName("venue_description")
        val venue_description: String? = null

        @Expose
        @SerializedName("venue_banner_image")
        val venue_banner_image: String? = null

        @Expose
        @SerializedName("venue_address")
        val venue_address: String? = null

        @Expose
        @SerializedName("venue_latitude")
        var venue_latitude = 0.0

        @Expose
        @SerializedName("venue_longitude")
        var venue_longitude = 0.0

        @Expose
        @SerializedName("venue_name")
        val venue_name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0

        @Expose
        @SerializedName("venuecategories")
        val venuecategories: List<VenueListRes.Venuecategories>? = null

        @Expose
        @SerializedName("venuetypes")
        val venuetypes: List<VenueListRes.Venuetypes>? = null

        @Expose
        @SerializedName("liveratings")
        val liveratings: List<String>? = null

        @Expose
        @SerializedName("whatson")
        val whatson: List<String>? = null

        @Expose
        @SerializedName("offerslive")
        val offerslive: List<Offerslive>? = null

        @Expose
        @SerializedName("offertickets")
        val offertickets: List<String>? = null

        @Expose
        @SerializedName("menus")
        val menus: List<Menus>? = null

        @Expose
        @SerializedName("timings")
        val timings: List<TimingsEntity>? = null
        var isSelected = false
    }
}
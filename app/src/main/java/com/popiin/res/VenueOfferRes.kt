package com.popiin.res

import com.popiin.res.VenueListRes.Menus
import com.popiin.res.VenueListRes.Offerslive
import com.popiin.res.VenueListRes.Offer
import com.google.gson.annotations.SerializedName

class VenueOfferRes {
    @SerializedName("data")
    val data: Data? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("success")
    val success = false

    class Data {
        @SerializedName("venuecategories")
        val venuecategories: ArrayList<Venuecategories>? = ArrayList()

        @SerializedName("venuetypes")
        val venuetypes: ArrayList<Venuetypes> = ArrayList()

        @SerializedName("menus")
        val menus: List<Menus>? = null

        @SerializedName("offers")
        val offers: List<Offerslive>? = null

        @SerializedName("offerstatic")
        val offersStatic: List<Offer>? = null

        @SerializedName("venue_updated_at")
        val venue_updated_at: String? = null

        @SerializedName("venue_created_at")
        val venue_created_at: String? = null

        @SerializedName("venue_status")
        val venue_status = 0

        @SerializedName("venue_approved")
        val venue_approved = 0

        @SerializedName("venue_other_information")
        val venue_other_information: String? = null

        @SerializedName("venue_is_public")
        val venue_is_public = 0

        @SerializedName("venue_has_ticket")
        val venue_has_ticket = 0

        @SerializedName("venue_is_draft")
        val venue_is_draft = 0

        @SerializedName("venue_share_link")
        val venue_share_link: String? = null

        @SerializedName("venue_total_ratings")
        val venue_total_ratings = 0

        @SerializedName("venue_ratings")
        val venue_ratings = 0

        @SerializedName("venue_total_capacity")
        val venue_total_capacity = 0

        @SerializedName("venue_type")
        val venue_type: String? = null

        @SerializedName("is_outdoor_area")
        val is_outdoor_area = 0

        @SerializedName("venue_last_entry")
        val venue_last_entry: String? = null

        @SerializedName("venue_door_policy")
        val venue_door_policy: String? = null

        @SerializedName("venue_dress_code")
        val venue_dress_code: String? = null

        @SerializedName("venue_djline")
        val venue_djline: String? = null

        @SerializedName("venue_music")
        val venue_music: String? = null

        @SerializedName("venue_age_group")
        val venue_age_group: String? = null

        @SerializedName("venue_price")
        val venue_price = 0

        @SerializedName("venue_longitude")
        val venue_longitude = 0.0

        @SerializedName("venue_latitude")
        val venue_latitude = 0.0

        @SerializedName("venue_postal_code")
        val venue_postal_code: String? = null

        @SerializedName("venue_city")
        val venue_city: String? = null

        @SerializedName("venue_address")
        val venue_address: String? = null

        @SerializedName("venue_banner_image")
        val venue_banner_image: String? = null

        @SerializedName("venue_description")
        val venue_description: String? = null

        @SerializedName("venue_name")
        val venue_name: String? = null

        @SerializedName("venue_user_id")
        val venue_user_id = 0

        @SerializedName("venue_place_id")
        val venue_place_id: String? = null

        @SerializedName("id")
        val id = 0
    }

    class Venuecategories {
        @SerializedName("category_name")
        val category_name: String? = null

        @SerializedName("venue_type")
        val venue_type: String? = null

        @SerializedName("venue_id")
        val venue_id = 0

        @SerializedName("id")
        val id = 0
    }

    class Venuetypes {
        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("status")
        val status = 0

        @SerializedName("venue_type")
        val venue_type: String? = null

        @SerializedName("venue_id")
        val venue_id = 0

        @SerializedName("id")
        val id = 0
    }
}
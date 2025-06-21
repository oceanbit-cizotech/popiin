package com.popiin.res

import com.popiin.res.PlaceSearchRes.Plus_code
import com.popiin.res.PlaceSearchRes.Opening_hours
import com.popiin.res.PlaceSearchRes.Southwest
import com.popiin.res.PlaceSearchRes.Northeast
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlaceSearchRes : Serializable {
    @Expose
    @SerializedName("status")
    var status: String? = null

    @Expose
    @SerializedName("results")
    var results: List<Results>? = null

    @Expose
    @SerializedName("html_attributions")
    var html_attributions: List<String>? = null

    @Expose
    @SerializedName("next_page_token")
    var next_page_token: String? = null

    class Results : Serializable {
        @Expose
        @SerializedName("user_ratings_total")
        var user_ratings_total = 0

        @Expose
        @SerializedName("types")
        var types: List<String>? = null

        @Expose
        @SerializedName("reference")
        var reference: String? = null

        @Expose
        @SerializedName("rating")
        var rating = 0.0

        @Expose
        @SerializedName("plus_code")
        var plus_code: Plus_code? = null

        @Expose
        @SerializedName("place_id")
        var place_id: String? = null

        @Expose
        @SerializedName("photos")
        var photos: List<Photos>? = null

        @Expose
        @SerializedName("opening_hours")
        var opening_hours: Opening_hours? = null

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("icon_mask_base_uri")
        var icon_mask_base_uri: String? = null

        @Expose
        @SerializedName("icon_background_color")
        var icon_background_color: String? = null

        @Expose
        @SerializedName("icon")
        var icon: String? = null

        @Expose
        @SerializedName("geometry")
        var geometry: Geometry? = null

        @Expose
        @SerializedName("formatted_address")
        var formatted_address: String? = null

        @Expose
        @SerializedName("business_status")
        var business_status: String? = null
    }

    class Plus_code : Serializable {
        @Expose
        @SerializedName("global_code")
        var global_code: String? = null

        @Expose
        @SerializedName("compound_code")
        var compound_code: String? = null
    }

    class Photos : Serializable {
        @Expose
        @SerializedName("width")
        var width = 0

        @Expose
        @SerializedName("photo_reference")
        var photo_reference: String? = null

        @Expose
        @SerializedName("html_attributions")
        var html_attributions: List<String>? = null

        @Expose
        @SerializedName("height")
        var height = 0
    }

    class Opening_hours : Serializable {
        @Expose
        @SerializedName("open_now")
        var open_now = false
    }

    class Geometry : Serializable {
        @Expose
        @SerializedName("viewport")
        var viewport: Viewport? = null

        @Expose
        @SerializedName("location")
        var location: Location? = null
    }

    class Viewport : Serializable {
        @Expose
        @SerializedName("southwest")
        var southwest: Southwest? = null

        @Expose
        @SerializedName("northeast")
        var northeast: Northeast? = null
    }

    class Southwest : Serializable {
        @Expose
        @SerializedName("lng")
        var lng = 0.0

        @Expose
        @SerializedName("lat")
        var lat = 0.0
    }

    class Northeast : Serializable {
        @Expose
        @SerializedName("lng")
        var lng = 0.0

        @Expose
        @SerializedName("lat")
        var lat = 0.0
    }

    class Location : Serializable {
        @Expose
        @SerializedName("lng")
        var lng = 0.0

        @Expose
        @SerializedName("lat")
        var lat = 0.0
    }
}
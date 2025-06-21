package com.popiin.res

import com.popiin.res.AutoCompateSearchRes.Results
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class AutoCompateSearchRes {
    @SerializedName("results")
    @Expose
    var results: ArrayList<Results>? = null

    inner class Results {
        @SerializedName("formatted_address")
        @Expose
        var formatted_address: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("geometry")
        @Expose
        var geometry: Geometry? = null

        inner class Geometry {
            @SerializedName("location")
            @Expose
            var location: Location? = null

            inner class Location {
                @SerializedName("lat")
                @Expose
                var lat: Double? = null

                @SerializedName("lng")
                @Expose
                var lng: Double? = null
            }
        }
    }
}
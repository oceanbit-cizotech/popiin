package com.popiin.res

import com.popiin.res.GetEventVenueRes.EventVenueData
import com.popiin.res.GetEventVenueRes.Venues
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetEventVenueRes {
    @Expose
    @SerializedName("data")
    val eventVenueData: EventVenueData? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class EventVenueData {
        @Expose
        @SerializedName("venues")
        val venues: List<Venues>? = null

        @Expose
        @SerializedName("events")
        val events: List<Events>? = null
    }

    class Venues {
        @Expose
        @SerializedName("venue_name")
        val venue_name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Events {
        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }
}
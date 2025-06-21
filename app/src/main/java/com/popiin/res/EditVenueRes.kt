package com.popiin.res


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class EditVenueRes(
    @Expose
    var venue: Data?,
    @Expose
    var message: String?,
    @Expose
    var success: Boolean?,
) {
    data class Data(
        @Expose
        var entertainment: String?,
        @SerializedName("free_wifi")
        @Expose
        var freeWifi: Int?,
        @Expose
        var id: Int?,
        @SerializedName("is_outdoor_area")
        @Expose
        var isOutdoorArea: Int?,
        @SerializedName("need_booking_confirmation")
        @Expose
        var needBookingConfirmation: Int?,
        @SerializedName("reservation_enabled")
        @Expose
        var reservationEnabled: Int?,
        @SerializedName("venue_address")
        @Expose
        var venueAddress: String?,
        @SerializedName("venue_age_group")
        @Expose
        var venueAgeGroup: String?,
        @SerializedName("venue_approved")
        @Expose
        var venueApproved: Int?,
        @SerializedName("venue_banner_image")
        @Expose
        var venueBannerImage: String?,
        @SerializedName("venue_category")
        @Expose
        var venueCategory: Any?,
        @SerializedName("venue_city")
        @Expose
        var venueCity: String?,
        @SerializedName("venue_created_at")
        @Expose
        var venueCreatedAt: String?,
        @SerializedName("venue_description")
        @Expose
        var venueDescription: String?,
        @SerializedName("venue_djline")
        @Expose
        var venueDjline: String?,
        @SerializedName("venue_door_policy")
        @Expose
        var venueDoorPolicy: String?,
        @SerializedName("venue_dress_code")
        @Expose
        var venueDressCode: String?,
        @SerializedName("venue_has_ticket")
        @Expose
        var venueHasTicket: Int?,
        @SerializedName("venue_is_draft")
        @Expose
        var venueIsDraft: Int?,
        @SerializedName("venue_is_public")
        @Expose
        var venueIsPublic: Int?,
        @SerializedName("venue_last_entry")
        @Expose
        var venueLastEntry: String?,
        @SerializedName("venue_latitude")
        @Expose
        var venueLatitude: Double?,
        @SerializedName("venue_longitude")
        @Expose
        var venueLongitude: Double?,
        @SerializedName("venue_music")
        @Expose
        var venueMusic: String?,
        @SerializedName("venue_name")
        @Expose
        var venueName: String?,
        @SerializedName("venue_other_information")
        @Expose
        var venueOtherInformation: String?,
        @SerializedName("venue_place_id")
        @Expose
        var venuePlaceId: String?,
        @SerializedName("venue_postal_code")
        @Expose
        var venuePostalCode: String?,
        @SerializedName("venue_price")
        @Expose
        var venuePrice: Int?,
        @SerializedName("venue_ratings")
        @Expose
        var venueRatings: Int?,
        @SerializedName("venue_share_link")
        @Expose
        var venueShareLink: Any?,
        @SerializedName("venue_status")
        @Expose
        var venueStatus: Int?,
        @SerializedName("venue_total_capacity")
        @Expose
        var venueTotalCapacity: Int?,
        @SerializedName("venue_total_ratings")
        @Expose
        var venueTotalRatings: Int?,
        @SerializedName("venue_type")
        @Expose
        var venueType: Any?,
        @SerializedName("venue_updated_at")
        @Expose
        var venueUpdatedAt: String?,
        @SerializedName("venue_user_id")
        @Expose
        var venueUserId: Int?,
    )
}
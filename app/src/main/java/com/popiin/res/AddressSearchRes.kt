package com.popiin.res


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class AddressSearchRes(
    @SerializedName("html_attributions")
    @Expose
    val htmlAttributions: List<Any?>?,
    @SerializedName("next_page_token")
    @Expose
    val nextPageToken: String?,
    @SerializedName("results")
    @Expose
    val results: ArrayList<Result?>?,
    @SerializedName("status")
    @Expose
    val status: String?,
) {
    data class Result(
        @SerializedName("business_status")
        @Expose
        val businessStatus: String?,
        @SerializedName("formatted_address")
        @Expose
        val formattedAddress: String?,
        @SerializedName("geometry")
        @Expose
        val geometry: Geometry?,
        @SerializedName("icon")
        @Expose
        val icon: String?,
        @SerializedName("icon_background_color")
        @Expose
        val iconBackgroundColor: String?,
        @SerializedName("icon_mask_base_uri")
        @Expose
        val iconMaskBaseUri: String?,
        @SerializedName("name")
        @Expose
        val name: String?,
        @SerializedName("opening_hours")
        @Expose
        val openingHours: OpeningHours?,
        @SerializedName("photos")
        @Expose
        val photos: List<Photo?>?,
        @SerializedName("place_id")
        @Expose
        val placeId: String?,
        @SerializedName("plus_code")
        @Expose
        val plusCode: PlusCode?,
        @SerializedName("price_level")
        @Expose
        val priceLevel: Int?,
        @SerializedName("rating")
        @Expose
        val rating: Double?,
        @SerializedName("reference")
        @Expose
        val reference: String?,
        @SerializedName("types")
        @Expose
        val types: List<String?>?,
        @SerializedName("user_ratings_total")
        @Expose
        val userRatingsTotal: Int?,
    ) {
        data class Geometry(
            @SerializedName("location")
            @Expose
            val location: Location?,
            @SerializedName("viewport")
            @Expose
            val viewport: Viewport?,
        ) {
            data class Location(
                @SerializedName("lat")
                @Expose
                val lat: Double?,
                @SerializedName("lng")
                @Expose
                val lng: Double?,
            )

            data class Viewport(
                @SerializedName("northeast")
                @Expose
                val northeast: Northeast?,
                @SerializedName("southwest")
                @Expose
                val southwest: Southwest?,
            ) {
                data class Northeast(
                    @SerializedName("lat")
                    @Expose
                    val lat: Double?,
                    @SerializedName("lng")
                    @Expose
                    val lng: Double?,
                )

                data class Southwest(
                    @SerializedName("lat")
                    @Expose
                    val lat: Double?,
                    @SerializedName("lng")
                    @Expose
                    val lng: Double?,
                )
            }
        }

        data class OpeningHours(
            @SerializedName("open_now")
            @Expose
            val openNow: Boolean?,
        )

        data class Photo(
            @SerializedName("height")
            @Expose
            val height: Int?,
            @SerializedName("html_attributions")
            @Expose
            val htmlAttributions: List<String?>?,
            @SerializedName("photo_reference")
            @Expose
            val photoReference: String?,
            @SerializedName("width")
            @Expose
            val width: Int?,
        )

        data class PlusCode(
            @SerializedName("compound_code")
            @Expose
            val compoundCode: String?,
            @SerializedName("global_code")
            @Expose
            val globalCode: String?,
        )
    }
}
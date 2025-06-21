package com.popiin.res


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class SearchAddressDetailRes(
    @SerializedName("html_attributions")
    @Expose
    val htmlAttributions: List<Any?>?,
    @SerializedName("result")
    @Expose
    val result: Result?,
    @SerializedName("status")
    @Expose
    val status: String?,
) {
    data class Result(
        @SerializedName("address_components")
        @Expose
        val addressComponents: List<AddressComponent?>?,
        @SerializedName("adr_address")
        @Expose
        val adrAddress: String?,
        @SerializedName("business_status")
        @Expose
        val businessStatus: String?,
        @SerializedName("curbside_pickup")
        @Expose
        val curbsidePickup: Boolean?,
        @SerializedName("current_opening_hours")
        @Expose
        val currentOpeningHours: CurrentOpeningHours?,
        @SerializedName("delivery")
        @Expose
        val delivery: Boolean?,
        @SerializedName("dine_in")
        @Expose
        val dineIn: Boolean?,
        @SerializedName("formatted_address")
        @Expose
        val formattedAddress: String?,
        @SerializedName("formatted_phone_number")
        @Expose
        val formattedPhoneNumber: String?,
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
        @SerializedName("international_phone_number")
        @Expose
        val internationalPhoneNumber: String?,
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
        @SerializedName("rating")
        @Expose
        val rating: Double?,
        @SerializedName("reference")
        @Expose
        val reference: String?,
        @SerializedName("reviews")
        @Expose
        val reviews: List<Review?>?,
        @SerializedName("serves_beer")
        @Expose
        val servesBeer: Boolean?,
        @SerializedName("serves_dinner")
        @Expose
        val servesDinner: Boolean?,
        @SerializedName("serves_lunch")
        @Expose
        val servesLunch: Boolean?,
        @SerializedName("serves_vegetarian_food")
        @Expose
        val servesVegetarianFood: Boolean?,
        @SerializedName("takeout")
        @Expose
        val takeout: Boolean?,
        @SerializedName("types")
        @Expose
        val types: List<String?>?,
        @SerializedName("url")
        @Expose
        val url: String?,
        @SerializedName("user_ratings_total")
        @Expose
        val userRatingsTotal: Int?,
        @SerializedName("utc_offset")
        @Expose
        val utcOffset: Int?,
        @SerializedName("vicinity")
        @Expose
        val vicinity: String?,
        @SerializedName("wheelchair_accessible_entrance")
        @Expose
        val wheelchairAccessibleEntrance: Boolean?,
    ) {
        data class AddressComponent(
            @SerializedName("long_name")
            @Expose
            val longName: String?,
            @SerializedName("short_name")
            @Expose
            val shortName: String?,
            @SerializedName("types")
            @Expose
            val types: List<String?>?,
        )

        data class CurrentOpeningHours(
            @SerializedName("open_now")
            @Expose
            val openNow: Boolean?,
            @SerializedName("periods")
            @Expose
            val periods: List<Period?>?,
            @SerializedName("weekday_text")
            @Expose
            val weekdayText: List<String?>?,
        ) {
            data class Period(
                @SerializedName("close")
                @Expose
                val close: Close?,
                @SerializedName("open")
                @Expose
                val `open`: Open?,
            ) {
                data class Close(
                    @SerializedName("date")
                    @Expose
                    val date: String?,
                    @SerializedName("day")
                    @Expose
                    val day: Int?,
                    @SerializedName("time")
                    @Expose
                    val time: String?,
                )

                data class Open(
                    @SerializedName("date")
                    @Expose
                    val date: String?,
                    @SerializedName("day")
                    @Expose
                    val day: Int?,
                    @SerializedName("time")
                    @Expose
                    val time: String?,
                )
            }
        }

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
            @SerializedName("periods")
            @Expose
            val periods: List<Period?>?,
            @SerializedName("weekday_text")
            @Expose
            val weekdayText: List<String?>?,
        ) {
            data class Period(
                @SerializedName("close")
                @Expose
                val close: Close?,
                @SerializedName("open")
                @Expose
                val `open`: Open?,
            ) {
                data class Close(
                    @SerializedName("day")
                    @Expose
                    val day: Int?,
                    @SerializedName("time")
                    @Expose
                    val time: String?,
                )

                data class Open(
                    @SerializedName("day")
                    @Expose
                    val day: Int?,
                    @SerializedName("time")
                    @Expose
                    val time: String?,
                )
            }
        }

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

        data class Review(
            @SerializedName("author_name")
            @Expose
            val authorName: String?,
            @SerializedName("author_url")
            @Expose
            val authorUrl: String?,
            @SerializedName("language")
            @Expose
            val language: String?,
            @SerializedName("original_language")
            @Expose
            val originalLanguage: String?,
            @SerializedName("profile_photo_url")
            @Expose
            val profilePhotoUrl: String?,
            @SerializedName("rating")
            @Expose
            val rating: Int?,
            @SerializedName("relative_time_description")
            @Expose
            val relativeTimeDescription: String?,
            @SerializedName("text")
            @Expose
            val text: String?,
            @SerializedName("time")
            @Expose
            val time: Int?,
            @SerializedName("translated")
            @Expose
            val translated: Boolean?,
        )
    }
}
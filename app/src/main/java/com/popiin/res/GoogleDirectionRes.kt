package com.popiin.res

import com.google.gson.annotations.SerializedName


data class GoogleDirectionRes(
    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoint?>?,
    var routes: List<Route?>?,
    var status: String?,
) {
    data class GeocodedWaypoint(
        @SerializedName("geocoder_status")
        var geocoderStatus: String?,
        @SerializedName("place_id")
        var placeId: String?,
        var types: List<String?>?,
    )

    data class Route(
        var bounds: Bounds?,
        var copyrights: String?,
        var legs: List<Leg?>?,
        @SerializedName("overview_polyline")
        var overviewPolyline: OverviewPolyline?,
        var summary: String?,
        var warnings: List<String?>?,
        @SerializedName("waypoint_order")
        var waypointOrder: List<Any?>?,
    ) {
        data class Bounds(
            var northeast: Northeast?,
            var southwest: Southwest?,
        ) {
            data class Northeast(
                var lat: Double?,
                var lng: Double?,
            )

            data class Southwest(
                var lat: Double?,
                var lng: Double?,
            )
        }

        data class Leg(
            var distance: Distance?,
            var duration: Duration?,
            @SerializedName("end_address")
            var endAddress: String?,
            @SerializedName("end_location")
            var endLocation: EndLocation?,
            @SerializedName("start_address")
            var startAddress: String?,
            @SerializedName("start_location")
            var startLocation: StartLocation?,
            var steps: List<Step?>?,
            @SerializedName("traffic_speed_entry")
            var trafficSpeedEntry: List<Any?>?,
            @SerializedName("via_waypoint")
            var viaWaypoint: List<Any?>?,
        ) {
            data class Distance(
                var text: String?,
                var value: Int?,
            )

            data class Duration(
                var text: String?,
                var value: Int?,
            )

            data class EndLocation(
                var lat: Double?,
                var lng: Double?,
            )

            data class StartLocation(
                var lat: Double?,
                var lng: Double?,
            )

            data class Step(
                var distance: Distance?,
                var duration: Duration?,
                @SerializedName("end_location")
                var endLocation: EndLocation?,
                @SerializedName("html_instructions")
                var htmlInstructions: String?,
                var maneuver: String?,
                var polyline: Polyline?,
                @SerializedName("start_location")
                var startLocation: StartLocation?,
                @SerializedName("travel_mode")
                var travelMode: String?,
            ) {
                data class Distance(
                    var text: String?,
                    var value: Int?,
                )

                data class Duration(
                    var text: String?,
                    var value: Int?,
                )

                data class EndLocation(
                    var lat: Double?,
                    var lng: Double?,
                )

                data class Polyline(
                    var points: String?,
                )

                data class StartLocation(
                    var lat: Double?,
                    var lng: Double?,
                )
            }
        }

        data class OverviewPolyline(
            var points: String?,
        )
    }
}
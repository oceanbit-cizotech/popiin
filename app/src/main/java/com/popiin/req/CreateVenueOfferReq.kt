package com.popiin.req

class CreateVenueOfferReq(
    var title: String,
    var working_day: String,
    var open_time: String,
    var close_time: String,
    var start_time_display: String,
    var end_time_display: String,
    var venue_id: String,
    var redeem_allowed_time: String,
    var is_unique: Int
)
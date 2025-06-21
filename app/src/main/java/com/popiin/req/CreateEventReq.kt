package com.popiin.req

import com.popiin.model.TicketBook
import java.util.ArrayList

class CreateEventReq(
    var lang: String,
    var is_public: Int,
    var name: String,
    var age: String,
    var description: String,
    var venue: String,
    var address: String,
    var lat: String,
    var longi: String,
    var city: String,
    var postal_code: String,
    var start_date_time: String,
    var end_date_time: String,
    var music_on: Boolean,
    var music: String,
    var music_djlineup: String,
    var category: String,
    var dress_on: Boolean,
    var dress_code: String,
    var entry_policy_on: Boolean,
    var entry_policy: String,
    var last_entry: String,
    var other_information: String,
    var total_capacity: Int,
    var has_ticket: String,
    var state: String,
    var is_draft: Int,
    var images: Array<String?>,
    var tickets: ArrayList<TicketBook>,
    var entertainment: String
)
package com.popiin.req

import com.popiin.model.TimingModel
import com.popiin.model.VenueTypeCategory
import com.popiin.res.VenueListRes
import java.util.ArrayList

class EditVenueReq {
    var venue_id: String
    var lang: String
    var is_public: Int
    var name: String
    var age: String
    var description: String
    var address: String
    var lat: String
    var longi: String
    var city: String
    var postal_code: String
    var music_on: Boolean
    var music: String
    var entertainment: String
    var venue_djline: String
    var dress_on: Boolean
    var dress_code: String
    var entry_policy_on: Boolean
    var entry_policy: String
    var other_information: String
    var has_ticket: String
    var state: String
    var is_draft: Int
    var images: Array<String?>
    var timing: ArrayList<TimingModel>
    var menus: Array<String?>
    var is_outdoor_area: Int
    var last_entry: String
    var venue: String
    var venue_types: ArrayList<String>
    var venue_type_category: ArrayList<VenueTypeCategory>
    var venue_offer: Array<String?>? = null
    var reservation_enabled = 0
    var venue_total_capacity = 0
    var free_wifi = 0

    constructor(
        venue_id: String,
        lang: String,
        is_public: Int,
        name: String,
        age: String,
        description: String,
        address: String,
        lat: String,
        longi: String,
        city: String,
        postal_code: String,
        music_on: Boolean,
        music: String,
        entertainment: String,
        DJLine: String,
        dress_on: Boolean,
        dress_code: String,
        entry_policy_on: Boolean,
        entry_policy: String,
        other_information: String,
        has_ticket: String,
        state: String,
        is_draft: Int,
        images: Array<String?>,
        timing: ArrayList<TimingModel>,
        menus: Array<String?>,
        is_outdoor_area: Int,
        last_entry: String,
        venue: String,
        venue_types: ArrayList<String>,
        venue_type_category: ArrayList<VenueTypeCategory>,
        venue_offer: Array<String?>?,
        reservation_enabled: Int,
        venue_total_capacity: Int,
        free_wifi: Int,
    ) {
        this.venue_id = venue_id
        this.lang = lang
        this.is_public = is_public
        this.name = name
        this.age = age
        this.description = description
        this.address = address
        this.lat = lat
        this.longi = longi
        this.city = city
        this.postal_code = postal_code
        this.music_on = music_on
        this.music = music
        this.entertainment = entertainment
        venue_djline = DJLine
        this.dress_on = dress_on
        this.dress_code = dress_code
        this.entry_policy_on = entry_policy_on
        this.entry_policy = entry_policy
        this.other_information = other_information
        this.has_ticket = has_ticket
        this.state = state
        this.is_draft = is_draft
        this.images = images
        this.timing = timing
        this.menus = menus
        this.is_outdoor_area = is_outdoor_area
        this.last_entry = last_entry
        this.venue = venue
        this.venue_types = venue_types
        this.venue_type_category = venue_type_category
        this.venue_offer = venue_offer
        this.reservation_enabled = reservation_enabled
        this.venue_total_capacity = venue_total_capacity
        this.free_wifi = free_wifi
    }

    constructor(
        venue_id: String,
        lang: String,
        is_public: Int,
        name: String,
        age: String,
        description: String,
        address: String,
        lat: String,
        longi: String,
        city: String,
        postal_code: String,
        music_on: Boolean,
        music: String,
        entertainment: String,
        DJLine: String,
        dress_on: Boolean,
        dress_code: String,
        entry_policy_on: Boolean,
        entry_policy: String,
        other_information: String,
        has_ticket: String,
        state: String,
        is_draft: Int,
        images: Array<String?>,
        timing: ArrayList<TimingModel>,
        menus: Array<String?>,
        is_outdoor_area: Int,
        last_entry: String,
        venue: String,
        venue_types: ArrayList<String>,
        venue_type_category: ArrayList<VenueTypeCategory>,
        venue_total_capacity: Int,
        free_wifi: Int,
    ) {
        this.venue_id = venue_id
        this.lang = lang
        this.is_public = is_public
        this.name = name
        this.age = age
        this.description = description
        this.address = address
        this.lat = lat
        this.longi = longi
        this.city = city
        this.postal_code = postal_code
        this.music_on = music_on
        this.music = music
        this.entertainment = entertainment
        venue_djline = DJLine
        this.dress_on = dress_on
        this.dress_code = dress_code
        this.entry_policy_on = entry_policy_on
        this.entry_policy = entry_policy
        this.other_information = other_information
        this.has_ticket = has_ticket
        this.state = state
        this.is_draft = is_draft
        this.images = images
        this.timing = timing
        this.menus = menus
        this.is_outdoor_area = is_outdoor_area
        this.last_entry = last_entry
        this.venue = venue
        this.venue_types = venue_types
        this.venue_type_category = venue_type_category
        this.venue_total_capacity = venue_total_capacity
        this.free_wifi = free_wifi
    }
}
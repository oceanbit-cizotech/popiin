package com.popiin.model

import java.io.Serializable

class RegisterVenueModel : Serializable {
    var venueName:String = ""
    var age:String = ""
    var description:String = ""
    var venueImages: Array<String?>? = null
    var venueMenuImages: Array<String?>? = null
    var address: String = ""
    var city: String = ""
    var postal_code: String = ""
    var timingList: ArrayList<TimingModel> = ArrayList()
    var entertainment: String = ""
    var venueEntertainmentName: String = ""
    var venueType: String? = ""
    var venueTypes: ArrayList<String> = ArrayList()
    var venueTypeCategory: ArrayList<VenueTypeCategory> = ArrayList()
    var dress_code: String = ""
    var entry_policy: String = ""
    var last_entry: String = ""
    var offers: Array<String?>? = null
    var reservationEnabled = false
    var outDoorEnabled = false
    var other_info: String = ""
    var isBasicInfoVerified = false
    var isAddressVerfied = false
    var isOpenCloseVerified = false
    var isOtherInfoVerified = false
    var latitude: String = ""
    var longitude: String = ""
    var free_wifi = false
    var music: String = ""
    var venueMusicName: String = ""
    var venueMusicDjLine: String = ""

    constructor()

    constructor(
        venueName: String,
        age: String,
        description: String,
        venueImages: Array<String?>?,
        venueMenuImages: Array<String?>?,
        address: String,
        city: String,
        postal_code: String,
        timingList: ArrayList<TimingModel>,
        entertainment: String,
        venueEntertainmentName: String,
        venueType: String?,
        venueTypes: ArrayList<String>,
        venueTypeCategory: ArrayList<VenueTypeCategory>,
        dress_code: String,
        entry_policy: String,
        last_entry: String,
        offers: Array<String?>?,
        reservationEnabled: Boolean,
        outDoorEnabled: Boolean,
        other_info: String,
        isBasicInfoVerified: Boolean,
        isAddressVerfied: Boolean,
        isOpenCloseVerified: Boolean,
        isOtherInfoVerified: Boolean,
        latitude: String,
        longitude: String,
        free_wifi: Boolean,
        music: String,
        venueMusicName: String,
    ) {
        this.venueName = venueName
        this.age = age
        this.description = description
        this.venueImages = venueImages
        this.venueMenuImages = venueMenuImages
        this.address = address
        this.city = city
        this.postal_code = postal_code
        this.timingList = timingList
        this.entertainment = entertainment
        this.venueEntertainmentName = venueEntertainmentName
        this.venueType = venueType
        this.venueTypes = venueTypes
        this.venueTypeCategory = venueTypeCategory
        this.dress_code = dress_code
        this.entry_policy = entry_policy
        this.last_entry = last_entry
        this.offers = offers
        this.reservationEnabled = reservationEnabled
        this.outDoorEnabled = outDoorEnabled
        this.other_info = other_info
        this.isBasicInfoVerified = isBasicInfoVerified
        this.isAddressVerfied = isAddressVerfied
        this.isOpenCloseVerified = isOpenCloseVerified
        this.isOtherInfoVerified = isOtherInfoVerified
        this.latitude = latitude
        this.longitude = longitude
        this.free_wifi = free_wifi
        this.music = music
        this.venueMusicName = venueMusicName
    }
}
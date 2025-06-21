package com.popiin.req


class VenuesReq(
    var search: String?=null,
    var longi: String,
    var lat: String,
    var distance: Int,
    var venue_type:List<String>? =null,
    var trending:Int?=null
)
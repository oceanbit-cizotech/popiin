package com.popiin.annotation

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class CATEGORY_TYPE {
    companion object {
        var BAR = "Bar"
        var NIGHT_CLUB = "Nightclub"
        var PUB = "Pub"
        var RESTAURANT = "Restaurant"
        var HOTEL = "Hotel"
        var CAFE = "Cafe"
    }
}

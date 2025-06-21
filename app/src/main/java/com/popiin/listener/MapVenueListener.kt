package com.popiin.listener

abstract class MapVenueListener<T> {
    open fun onInfoDirectionClick(data: T) {}
    open fun onInfoFavoriteClick(data: T, status: Boolean) {}
    open fun onInfoOfferClick(data: T) {}
    open fun onInfoBookNowClick(data: T, position: Int) {}
    open fun onInfoShareClick(data: T, position: Int) {}
    open fun onInfoItemClick(data: T, position: Int) {}
}
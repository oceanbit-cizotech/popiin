package com.popiin.listener

import com.popiin.res.VenueListRes.Offerslive
import com.popiin.res.VenueListRes.Menus
import com.popiin.res.VenueListRes.Whatson

abstract class AdapterEventListener<T> {
    open fun onItemClick(item: T, position: Int) {}
    open fun onDirectionClick(item: T, position: Int) {}
    open fun onShareClick(item: T, position: Int) {}
    open fun onPhotoClick(item: T, position: Int) {}
    open fun onFavouriteClick(item: T, position: Int, isFavourite: Boolean) {}
    open fun onBookNowClick(item: T, position: Int) {}
    open fun onAddToCalendarClick(item: T, position: Int) {}
    open fun onReadMoreClick(item: T, position: Int) {}
    open fun onRecentVenuesClick(item: T, position: Int) {}
    open fun onOfferVenuesClick(item: Offerslive?, position: Int) {}
    open fun onImageVenuesClick(item: Offerslive?, menusList: List<Menus?>?, position: Int) {}
    open fun onWhatsOnReadMoreClick(item: Whatson?, position: Int) {}
    open fun onMenuImageClick(item: T, position: Int) {}
    open fun onEventAddressSearch(item: T, position: Int) {}
}
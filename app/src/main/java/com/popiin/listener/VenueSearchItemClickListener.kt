package com.popiin.listener

import com.popiin.res.VenueListRes.Venue

abstract class VenueSearchItemClickListener<T> {
    open fun onItemClick(venue: T?) {}
    open fun onDirections(venue: T?){}
}
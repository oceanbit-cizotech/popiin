package com.popiin.listener

import com.popiin.res.VenueListRes

abstract class MapSearchItemClickListener {
    open fun onItemClick(venue: VenueListRes.Venue?) {}
}
package com.popiin.listener

import com.popiin.res.VenueListRes

abstract class ChooseDateListener {
    open fun onSearchClick(dateFrom: String, dateTo: String) {}
}
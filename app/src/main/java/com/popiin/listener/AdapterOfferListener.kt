package com.popiin.listener

import com.popiin.model.TicketBook

abstract class AdapterOfferListener<T> {
    open fun onOfferItemClick(item: T, position: Int) {}
    open fun onSavedOfferClick(item: T, position: Int) {}
    open fun onSavedOfferDeleteClick(item: T, position: Int) {}
}
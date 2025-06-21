package com.popiin.listener

abstract class AdapterMyVenuesListener<T> {
    open fun onEventShareClick(item: T, position: Int) {}
    open fun onEventDeleteClick(item: T, position: Int) {}
    open fun onEventEditClick(item: T, position: Int) {}
    open fun onEvenCopyClick(item: T, position: Int) {}
    open fun onEventClick(item: T, position: Int) {}
    open fun onEventSelected(item: T, position: Int) {}
    open fun onDocumentVerifyClick(item: T, position: Int) {}
    open fun onVenueItemCopyClick(item: T, position: Int) {}
}
package com.popiin.listener

open class AdapterWhatsReserveItemListener<T> {
    open fun onBookingType(position: Int, bookingType: String?) {}
    open fun onPrice(item: T, position: Int, price: String) {}
    open fun onQuantity(item: T, position: Int, quantity: String) {}
    open fun onDeleteClick(item: T, position: Int) {}
}
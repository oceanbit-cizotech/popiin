package com.popiin.model

import com.popiin.util.Common

data class TicketBook(
    var name: String,
    var price: String,
    var quantity: String,
    var booking_type: String,
    var position: Int,
    var isNew: Boolean
) {

    @JvmName("getPrice1")
    fun getPrice(): String {
        return price
    }

    @JvmName("setPrice1")
    fun setPrice(price: String) {
        var price = price
        if (price.length > 0 && price.toDouble() > 0) {
            price = Common.instance!!.formatter.getDecimalFormatValue(price.toDouble(), "#.##")
        }
        this.price = price
    }
}
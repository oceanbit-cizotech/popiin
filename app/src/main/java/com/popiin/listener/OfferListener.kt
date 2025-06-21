package com.popiin.listener

abstract class OfferListener<T> {
    open fun onItemClick(item: T, position: Int) {}
    open fun onCloseClick(item: T, position: Int) {}
}
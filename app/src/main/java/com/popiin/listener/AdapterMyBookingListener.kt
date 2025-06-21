package com.popiin.listener

abstract class AdapterMyBookingListener<T> {
    open fun onItemClick(item: T, position: Int) {}
    open fun onDirectionClick(item: T, position: Int) {}
    open fun onViewBookingCode(item: T, position: Int) {}
    open fun onCancel(item: T, position: Int) {}
    open fun onInviteFriends(item: T, position: Int) {}
    open fun onAddToCalender(item: T, position: Int) {}
    open fun onAddSpecialRequirement(item: T, position: Int) {}
    open fun onFavorite(item: T,position: Int,isFavorites :Boolean){}
}
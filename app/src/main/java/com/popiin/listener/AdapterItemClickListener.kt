package com.popiin.listener

import com.popiin.res.EventListRes

abstract class AdapterItemClickListener<T> {
    open fun onAdapterItemClick(item: T, position: Int) {}
    open fun onAdapterEditClick(item: T, position: Int) {}
    open fun onAdapterDeleteClick(item: T, position: Int) {}
    open fun onSwitchClick(item: T, position: Int, status: Int) {}
    open fun onVenueEditClick(item: T, position: Int) {}
    open fun onEventDeleteClick(item: T, position: Int) {}
    open fun onEventCopyClick(item: T, position: Int) {}
    open fun onEventShareClick(item: T, position: Int) {}
    open fun onEventImageCloseClick(item: T, position: Int) {}
    open fun onVenueReserveEditClick(item: T, position: Int) {}
    open fun onWhatsOnReserveEditClick(item: T, position: Int) {}
    open fun onEventItemCopyClick(item: T, position: Int) {}
    open fun onWhatsonAdapterItemClick(item: EventListRes.Whatsonticket, position: Int) {}
}
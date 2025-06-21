package com.popiin.listener

abstract class AdapterReportListItemListner<T> {
    open fun onItemSelect(item: T, position: Int, isSelect: Boolean) {}
    open fun onAllItemSelect(isSelect: Boolean) {}
    open fun onSelectEvent(list:ArrayList<T>){}
}
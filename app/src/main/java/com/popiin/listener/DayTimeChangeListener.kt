package com.popiin.listener

import com.popiin.model.TimingModel

abstract class DayTimeChangeListener<T> {
    open fun onAddOpenTime(daysModel: TimingModel, position: Int) {}
    open fun onAddCloseTime(daysModel: TimingModel, position: Int) {}
    open fun onClearTime(isClear: Boolean, position: Int) {}
}
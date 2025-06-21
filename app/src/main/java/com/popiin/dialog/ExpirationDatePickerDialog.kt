package com.popiin.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import com.popiin.R

class ExpirationDatePickerDialog(
    context: Context,
    callBack: OnDateSetListener?,
    year: Int,
    monthOfYear: Int,
    dayOfMonth: Int
) : DatePickerDialog(
    context,
    R.style.MyDatePickerTheme,
    callBack,
    year,
    monthOfYear,
    dayOfMonth
), DatePicker.OnDateChangedListener {
    init {
        init(context)
    }

    @SuppressLint("DiscouragedApi")
    private fun init(context: Context) {
        setTitle("")
        datePicker.minDate = System.currentTimeMillis() - 1000
        @Suppress("DEPRECATION")
        datePicker.calendarViewShown = false
        val day = context.resources.getIdentifier("android:id/day", null, null)
        if (day != 0) {
            val dayPicker = datePicker.findViewById<View>(day)
            if (dayPicker != null) {
                dayPicker.visibility = View.GONE
            }
        }
    }

    override fun onDateChanged(view: DatePicker, year: Int, month: Int, day: Int) {}
}
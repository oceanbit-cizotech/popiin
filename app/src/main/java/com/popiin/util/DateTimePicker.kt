package com.popiin.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
open class DateTimePicker(
    private val activity: Activity?,
    format: String,
    @field:PICKER_TYPE private val picker_type: Int,
    minDate: Long,
    maxDate: Long,
    currentDate: Long,
    listener: DateTimePickerListener?,
) {
    private var dateFormat: SimpleDateFormat

    @Retention(AnnotationRetention.SOURCE)
    annotation class PICKER_TYPE {
        companion object {
            var BOTH = 0
            var DATE = 1
            var TIME = 2
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class FORMATS {
        companion object {
            var DEFAULT = "dd-MM-yyyy"

            //            var DATE_MON_STRING = "dd-MMM-yyyy"
//            var DATE_TIME = "dd-MM-yyyy hh:mm"
            var TIME = "hh:mm"
            var TIME_AP = "hh:mma"
//            var TIME_SECOND = "HH:mm:ss"
//            var DATE_CUSTOM = "dd MMM yyyy hh:mma"
//            var ONLY_DATE_CUSTOM = "dd MMM yyyy"
//            var DATE_TIME_AMERICAN = "yyyy-MM-dd hh:mm:ss"
//            var DATE_TIME_CUSTOM = "MMM d yyyy h:mma"
//            var DATE_EVENT = "MMM dd, yyyy"
//            var TIME_EVENT = "hh a"
        }
    }

    private var minDate: Long = -1
    private var maxDate: Long = -1
    private var currentDate: Long = -1
    private val listener: DateTimePickerListener?
    fun show(): DateTimePicker {
        calendar = Calendar.getInstance()
        if (currentDate > 0) {
            calendar!!.timeInMillis = currentDate
        }
        mYear = calendar!!.get(Calendar.YEAR)
        mMonth = calendar!!.get(Calendar.MONTH)
        mDay = calendar!!.get(Calendar.DAY_OF_MONTH)
        if (picker_type != PICKER_TYPE.TIME) {
            val datePickerDialog = DatePickerDialog(activity!!,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar = Calendar.getInstance()
                    calendar!!.set(Calendar.YEAR, year)
                    calendar!!.set(Calendar.MONTH, monthOfYear)
                    calendar!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    if (picker_type == PICKER_TYPE.BOTH) {
                        openTimePicker(activity, calendar)
                    } else {
                        if (listener != null) {
                            val strSelectDate = dateFormat.format(calendar!!.time)
                            listener.onDateSelected(strSelectDate)
                            listener.onDateSelected(year, monthOfYear + 1, dayOfMonth)
                            listener.onDateSelected(year,
                                monthOfYear + 1,
                                dayOfMonth,
                                strSelectDate)
                        }
                    }
                }, mYear, mMonth, mDay)
            if (minDate > 0) {
                datePickerDialog.datePicker.minDate = minDate
            }
            if (maxDate > 0) {
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            }
            datePickerDialog.show()
        } else {
            openTimePicker(activity, calendar)
        }
        return this
    }

    private fun openTimePicker(context: Activity?, calendar: Calendar?) {
        val timePickerDialog = TimePickerDialog(context,
            { _, hourOfDay, minute -> //binding.textDate.setText(ConvertDate(date_time + " " + hourOfDay + ":" + minute,isFrom));
                calendar!![Calendar.HOUR_OF_DAY] = hourOfDay
                calendar[Calendar.MINUTE] = minute
                mYear = calendar[Calendar.YEAR]
                mMonth = calendar[Calendar.MONTH]
                mDay = calendar[Calendar.DAY_OF_MONTH]
                val strSelectDate = dateFormat.format(calendar.time)
                if (listener != null) {
                    if (picker_type == PICKER_TYPE.BOTH) {
                        listener.onDateTimeSelected(strSelectDate)
                        listener.onDateTimeSelected(mYear, mMonth + 1, mDay, hourOfDay, minute)
                        listener.onDateTimeSelected(mYear,
                            mMonth + 1,
                            mDay,
                            hourOfDay,
                            minute,
                            strSelectDate)
                    } else {
                        listener.onTimeSelected(strSelectDate)
                        listener.onTimeSelected(hourOfDay, minute)
                        listener.onTimeSelected(hourOfDay, minute, strSelectDate)
                    }
                }
            }, 0, 0, false)
        timePickerDialog.show()
    }

    class DateTimePickerBuilder//        private constructor()
        (private var activity: Activity?) {
        private var format = FORMATS.DEFAULT

        @PICKER_TYPE
        private var pickerType = PICKER_TYPE.DATE
        private var listner: DateTimePickerListener? = null
        private var minDate: Long = -1
        private var maxDate: Long = -1
        private var currentDate: Long = -1
        fun setFormat(format: String): DateTimePickerBuilder {
            this.format = format
            return this
        }

        fun setListener(listener: DateTimePickerListener?): DateTimePickerBuilder {
            this.listner = listener
            return this
        }

        fun setPickerType(@PICKER_TYPE picker_type: Int): DateTimePickerBuilder {
            this.pickerType = picker_type
            return this
        }
//
//        fun setMinDate(minDate: Long): DateTimePickerBuilder {
//            this.minDate = minDate
//            return this
//        }
//
//        fun setMaxDate(maxDate: Long): DateTimePickerBuilder {
//            this.maxDate = maxDate
//            return this
//        }
//
//        fun setCurrentDate(currentDate: Long): DateTimePickerBuilder {
//            this.currentDate = currentDate
//            return this
//        }

        fun build(): DateTimePicker {
            return DateTimePicker(activity,
                format,
                pickerType,
                minDate,
                maxDate,
                currentDate,
                listner)
        }

        fun show(): DateTimePicker {
            return this.build().show()
        }
    }

    abstract class DateTimePickerListener {
        open fun onTimeSelected(hour: Int, minute: Int) {}
        open fun onTimeSelected(hour: Int, minute: Int, timeInString: String?) {}

        // open fun onTimeSelected(hour: Int, minute: Int, seconds: Int, timeInString: String?) {}
        open fun onTimeSelected(timeInString: String) {
            println("$TAG : $timeInString")
        }

        open fun onDateTimeSelected(year: Int, month: Int, date: Int, hour: Int, minute: Int) {}
        open fun onDateTimeSelected(
            year: Int,
            month: Int,
            date: Int,
            hour: Int,
            minute: Int,
            dateTimeInString: String?,
        ) {
        }

        open fun onDateTimeSelected(dateTimeInString: String) {
            println("$TAG : $dateTimeInString")
        }

        open fun onDateSelected(year: Int, month: Int, date: Int) {}
        open fun onDateSelected(year: Int, month: Int, date: Int, dateInString: String?) {}
        open fun onDateSelected(dateInString: String?) {}
    }

    companion object {
        val TAG: String = DateTimePicker::class.java.simpleName
        private var mYear = 0
        private var mMonth = 0
        private var mDay = 0
        private var calendar: Calendar? = null
    }


    init {
        this.minDate = minDate
        this.maxDate = maxDate
        this.listener = listener
        this.currentDate = currentDate
        dateFormat = SimpleDateFormat(format)
    }
}
package com.popiin.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DecimalFormatter private constructor() {
    var weekDayInWord = arrayOf(
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday")

    fun getDecimalFormatValue(input: Double): String {
        return try {
            String.format("%.2f", input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun getTwoDigitInteger(input: Int): String {
        return try {
            String.format("%02d", input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun getTwoDigit(input: String): String {
        return try {
            String.format("%02d", input.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            input
        }
    }

    fun getOneDecimalFormatValue( input: Double): String {
        if(input<0.01){
        }
        return try {
            String.format("%.1f", input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun getDecimalFormatValue(input: Double, format: String?): String {
        return try {
            val doubleFormattor = DecimalFormat(format)
            doubleFormattor.format(input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun getDecimalFormatValue(input: Int, format: String?): String {
        return try {
            val doubleFormattor = DecimalFormat(format)
            doubleFormattor.format(input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun getDecimalFormatValue(input: String, format: String?): String {
        return try {
            val doubleFormattor = DecimalFormat(format)
            doubleFormattor.format(input.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            input
        }
    }

    fun getWeekDayInWord(): String {
        val weekDay: String
        val dayFormat = SimpleDateFormat("EEEE", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        weekDay = dayFormat.format(calendar.time)
        //        System.out.println("WEEK DAY IN NUMBER : " + getWeekDayInNumber());
//        System.out.println("WEEK DAY IN WORD : " + weekDay);
        return weekDay
    }

    val weekDayInNumber: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            return calendar.get(Calendar.DAY_OF_WEEK)
        }

    fun checkIsBetweenGivenDate(currentDate: Date, minDate: Date?, maxDate: Date?): Boolean {
        return currentDate.after(minDate) && currentDate.before(maxDate)
    }

    companion object {
        var instance: DecimalFormatter? = null
            get() {
                if (field == null) {
                    field = DecimalFormatter()
                }
                return field
            }
            private set
    }
}
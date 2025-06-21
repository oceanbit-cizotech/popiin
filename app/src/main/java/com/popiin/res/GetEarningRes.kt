package com.popiin.res

import com.popiin.res.GetEarningRes.Earnings

class GetEarningRes {
    val message: String? = null
    val data: List<Earnings>? = null
    val success = false

    inner class Earnings {
        val total_bookings = 0
        val paid_amount = 0.0
        val unpaid_amount = 0
        val end_date_time: String? = null
        val start_date_time: String? = null
        val name: String? = null
        val id = 0
    }
}
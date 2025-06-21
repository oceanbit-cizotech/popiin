package com.popiin.req

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SalesReportReq(
    @field:SerializedName("type")
    @field:Expose
    private val type: String,
    @field:SerializedName("event_id")
    @field:Expose private
    val event_id: String,
    @field:SerializedName("whatson_id")
    @field:Expose
    private val whatson_id: String
)
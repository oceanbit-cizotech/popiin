package com.popiin.model

import java.io.Serializable

data class EventModel(var eventName:String,var cateogory:String,
                      var address:String,var date:String,var reviewTag:String,var price:String) : Serializable

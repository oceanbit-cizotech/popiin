package com.popiin.model

import java.io.Serializable

data class ExploreModel(var image : Int,var venueType:String,var venueName:String, var location:String,
                        var musicType:String,var activityName:String,var awardTag:String,var offerTag:String) : Serializable

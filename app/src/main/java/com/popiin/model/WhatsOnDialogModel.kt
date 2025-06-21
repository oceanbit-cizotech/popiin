package com.popiin.model

import java.io.Serializable

data class WhatsOnDialogModel(var image:Int,var whatOnName:String,var musicName:String,
                              var restroName:String,var activityName:String,var date:String) : Serializable

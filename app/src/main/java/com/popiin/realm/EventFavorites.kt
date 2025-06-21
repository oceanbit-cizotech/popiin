package com.popiin.realm

import io.realm.RealmObject

open class EventFavorites(var userId: Int?=0, var id: Int?=0, var status: Int?=0) : RealmObject()
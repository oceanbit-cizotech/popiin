package com.popiin.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VenuesFavorites(
    var userId: Int? = 0,
    @PrimaryKey var id: Int? = 0,
    var status: Int? = 0,
) : RealmObject()
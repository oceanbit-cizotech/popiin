package com.popiin.realm

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import io.realm.Realm

class RealmController(application: Application?) {
    val realm: Realm = Realm.getDefaultInstance()

    // Crate Country Related Database
    fun clearPost() {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    fun isFavorites(UserID: Int, ids: Int): Int? {
        val postsLikes = realm.where(
            VenuesFavorites::class.java)
            .equalTo("userId", UserID)
            .equalTo("id", ids)
            .findAll()
        return if (postsLikes.size > 0) {
            postsLikes[0]!!.status
        } else {
            3
        }
    }

    fun isFavoritesEvents(UserID: Int, ids: Int): Int? {
        val postsLikes = realm.where(
            EventFavorites::class.java)
            .equalTo("userId", UserID)
            .equalTo("id", ids)
            .findAll()
        return if (postsLikes.size > 0) {
            postsLikes[0]!!.status
        } else {
            3
        }
    }

    companion object {
        var instance: RealmController? = null
            private set

        fun with(fragment: Fragment): RealmController? {
            if (instance == null) {
                instance = RealmController(fragment.requireActivity().application)
            }
            return instance
        }

        fun with(activity: Activity): RealmController? {
            if (instance == null) {
                instance = RealmController(activity.application)
            }
            return instance
        }

        fun with(application: Application?): RealmController? {
            if (instance == null) {
                instance = RealmController(application)
            }
            return instance
        }
    }

}
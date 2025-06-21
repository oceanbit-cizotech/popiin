package com.popiin.util

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.provider.Settings
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.popiin.ApiClient
import com.popiin.BuildConfig
import com.popiin.R
import com.popiin.res.*
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import io.branch.referral.Branch
import io.realm.Realm
import io.realm.RealmConfiguration


open class PrefManager : MultiDexApplication() {
    lateinit var algoliaClient: ClientSearch

    companion object {
        var config:Config?=null
        var instance: PrefManager? = null
        var lastLocation: Location? = null
        private var mPref: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        var DEVICE_ID: String? = null

        //   private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        var isStripeVerified = 0
        lateinit var cloudinary: Cloudinary
        val ALGOLIA_APP_ID = if (!ApiClient.BASE_URL.contains("dev")) "1GJ7R1WX2I" else "3JJ1Q575OK"
        val ALGOLIA_API_KEY = if (!ApiClient.BASE_URL.contains("dev")) "37b8686f8def3325dd220ea7b2453f75" else "e47cc5a83cd6fcff8f391e9fb6ee48b8"

        fun clearPreferences() {
            editor!!.clear()
            editor!!.apply()
        }


        fun setUserId(_id: String?) {
            editor!!.putString(Parameters().userId, _id)
            editor!!.commit()
        }

        fun getFirebaseToken(): String? {
            return mPref!!.getString(Parameters().firebaseToken, "")
        }

        fun setFirebaseToken(_id: String?) {
            editor!!.putString(Parameters().firebaseToken, _id)
            editor!!.commit()
        }

        fun getAccessToken(): String? {
            return mPref!!.getString(Parameters().accessToken, "")
        }


        fun setAccessToken(_id: String?) {
            editor!!.putString(Parameters().accessToken, _id)
            editor!!.commit()
        }

        fun setUser(userData: LoginRes) {
            val gson = Gson()
            editor!!.putString(Parameters().user, gson.toJson(userData))
            editor!!.apply()
        }

        fun getUser(): LoginRes {
            val gsonObj = Gson()
            return gsonObj.fromJson(mPref!!.getString(Parameters().user, ""), LoginRes::class.java)
        }

        fun getName():String{
            return getUser().user?.firstName+" "+ getUser().user?.lastName
        }

        fun setEvent(userData: EventListRes.Event?) {
            val gson = Gson()
            editor!!.putString(Parameters().eventListRes, gson.toJson(userData))
            editor!!.apply()
        }

        fun getEvent(): EventListRes.Event? {
            val gsonObj = Gson()
            return gsonObj.fromJson(mPref!!.getString(Parameters().eventListRes, ""),
                EventListRes.Event::class.java)
        }


        fun setPassword(password: String?) {
            editor!!.putString(Parameters().password, password)
            editor!!.commit()
        }

        fun getPassword(): String? {
            return mPref!!.getString(Parameters().password, "")
        }

        fun setSelected(selected: Int) {
            editor!!.putInt(Parameters().selected, selected)
            editor!!.commit()
        }

        fun getSelected(): Int {
            return mPref!!.getInt(Parameters().selected, 0)
        }


        fun setOpenNow(selected: Int) {
            editor!!.putInt(Parameters().selected, selected)
            editor!!.commit()
        }

        fun getOpenNow(): Int {
            return mPref!!.getInt(Parameters().selected, -1)
        }


        fun setEventFilterSelected(selected: String) {
            editor!!.putString(Parameters().filterSelected, selected)
            editor!!.commit()
        }

        fun getEventFilterSelected(): String {
            return mPref!!.getString(Parameters().filterSelected, "")!!
        }



        fun setScannedEventVenue(data: ScannedEventVenue?) {
            val gson = Gson()
            editor!!.putString(Parameters().scannedEventVenue, gson.toJson(data))
            editor!!.apply()
        }


        fun setDeepLinkData(data: String?) {
            editor!!.putString(Parameters().deepLinkData, data)
            editor!!.commit()
        }

        fun getDeepLinkData(): String? {
            return mPref!!.getString(Parameters().deepLinkData, "")
        }

        fun setHasEvent(data: Int) {
            editor!!.putInt(Parameters().hasEvent, data)
            editor!!.commit()
        }

        fun getHasEvent(): Int {
            return mPref!!.getInt(Parameters().hasEvent, 0)
        }

        fun setHasVenue(data: Int) {
            editor!!.putInt(Parameters().hasVenue, data)
            editor!!.commit()
        }

        fun getHasVenue(): Int {
            return mPref!!.getInt(Parameters().hasVenue, 0)
        }


        fun setEmailVerify(isVerify: Int) {
            editor!!.putInt(Parameters().emailVerify, isVerify)
            editor!!.commit()
        }



        fun getRecentVenues(): VenueListRes? {
            val gsonObj = Gson()
            val venueListRes: VenueListRes
            return if (mPref!!.getString(Parameters().recentVenues, "")!!.isEmpty()) {
                venueListRes = VenueListRes()
                venueListRes
            } else {
                gsonObj.fromJson(mPref!!.getString(Parameters().recentVenues, ""),
                    VenueListRes::class.java)
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this


        FirebaseApp.initializeApp(this)

        Realm.init(this)
        mPref = this.getSharedPreferences(resources.getString(R.string.app_name),
            Context.MODE_PRIVATE)
        editor = mPref!!.edit()

        val config = RealmConfiguration.Builder().name("popiin.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .allowWritesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)

        DEVICE_ID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        val branch = Branch.getAutoInstance(this)

        if (BuildConfig.DEBUG) {
            branch.setDebug()
        }

        val configM: MutableMap<String, String> = HashMap()
        configM.put("cloud_name", "devomvmjs")
        configM.put("api_key", "998665658329649")
        configM.put("api_secret", "1du65hnx9qyLyeLfywvOtQXOscM")
        MediaManager.init(this, configM)

        cloudinary = Cloudinary(Constant().cloudinaryUrl)

        isStripeVerified = 0
        System.out.println("@@@@@. ID:: "+ALGOLIA_APP_ID+".  KEY:: "+ALGOLIA_API_KEY)
        algoliaClient = ClientSearch(ApplicationID(ALGOLIA_APP_ID), APIKey(ALGOLIA_API_KEY))

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}
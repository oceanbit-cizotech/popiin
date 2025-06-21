package com.popiin.activity


import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.databinding.ActivityHomeBinding
import com.popiin.fragment.*
import com.popiin.req.EventDetailsReq
import com.popiin.req.VenueDetailsReq
import com.popiin.res.EventDetailsRes
import com.popiin.res.VenueDetailsRes
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


open class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    lateinit var dialog : Dialog
    override fun getLayout(): Int {
        return R.layout.activity_home
    }

    override fun initViews() {
        @Suppress("DEPRECATION")
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectListener)
        binding.bottomNavigation.selectedItemId = R.id.menu_explore
        checkDeepLinkData()
        binding.bottomNavigation.itemIconTintList = null
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    if (!isFinishing) {
                        finishAffinity()
                    }
                }
            }
        })

    }


    @Suppress("DEPRECATION")
    private val navigationItemSelectListener = BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        when (item.itemId) {
            R.id.menu_explore -> {
                setFragment(ExploreFragment.newInstance(), ExploreFragment::class.java.simpleName)
            }
            R.id.menu_events -> {
                setFragment(EventsFragment.newInstance(), EventsFragment::class.java.simpleName)
            }
            R.id.menu_favourites -> {
                setFragment(FavouritesFragment.newInstance(), FavouritesFragment::class.java.simpleName)
            }
            R.id.menu_location -> {
                setFragment(LocationFragment.newInstance(), LocationFragment::class.java.simpleName)
            }
            R.id.menu_settings -> {
                setFragment(SettingsFragment.newInstance(), SettingsFragment::class.java.simpleName)
            }
        }
        true
    }

    private fun checkDeepLinkData() {
        Log.i("BRANCH SDK", "checkDeepLinkData()")
        if (PrefManager.getDeepLinkData()!!.isNotEmpty()) {
            Log.i("BRANCH SDK", "! isEmpty")
            try {
                val deepLinkData = JSONObject(PrefManager.getDeepLinkData()!!)
                Log.i("BRANCH SDK", "" + deepLinkData.toString())
                if (deepLinkData.has(Parameters().eventId)) {
                    callToGetEventDetail(deepLinkData.getString(Parameters().eventId))
                } else if (deepLinkData.has(Parameters().venueId)) {
                    callToGetVenueDetail(deepLinkData.getString(Parameters().venueId))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun callToGetVenueDetail(venue_id: String) {
        showProgress()
        val call: Call<VenueDetailsRes?>? =
            apiInterface.postVenueDetails(PrefManager.getAccessToken()!!,
                VenueDetailsReq(venue_id))
        call!!.enqueue(object : Callback<VenueDetailsRes?> {
            override fun onResponse(
                call: Call<VenueDetailsRes?>,
                response: Response<VenueDetailsRes?>
            ) {
                hideProgress()
            }

            override fun onFailure(call: Call<VenueDetailsRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    private fun callToGetEventDetail(event_id: String) {
        showProgress()
        val call: Call<EventDetailsRes>? =
            apiInterface.postEventDetails(PrefManager.getAccessToken()!!,
                EventDetailsReq(event_id))
        call!!.enqueue(object : Callback<EventDetailsRes?> {
            override fun onResponse(call: Call<EventDetailsRes?>, response: Response<EventDetailsRes?>) {
                hideProgress()
                //               if (isValidResponse(response)) {
                //  setFragment(EventBranchIoFragment.newInstance(response.body()))
                //               }
            }

            override fun onFailure(call: Call<EventDetailsRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0) // No animation on exit
    }
}
package com.popiin.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.VideoViewAdapter
import com.popiin.databinding.ActivityStoryPlayerBinding
import com.popiin.dialog.DirectionsDialog
import com.popiin.fragment.VenueBookFragment
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.res.VenueStoryListRes
import com.popiin.util.PrefManager
import com.google.android.gms.maps.model.LatLng
import com.popiin.activity.ExploreDetailsFragment.Companion.exploreItem
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.FavoriteListener
import com.popiin.realm.VenuesFavorites
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryPlayerActivity : BaseFragment<ActivityStoryPlayerBinding>() {
    private var reels: ArrayList<VenueStoryListRes.Data> = ArrayList()
    private var tempList: ArrayList<VenueStoryListRes.Data> = ArrayList()
    private var adapterVideoView: VideoViewAdapter? = null
    private var helper: PagerSnapHelper? = null
    companion object {
        private var venueStoryData: VenueStoryListRes? = null
        private var venueStories: ArrayList<VenueStoryListRes.Data>? = null
        private var position: Int = 0

        fun newInstance(venueStoryListItem: VenueStoryListRes?,position: Int,):StoryPlayerActivity{
            venueStoryData=venueStoryListItem
            this.position=position
            return StoryPlayerActivity()
        }

        fun newInstance(venueStoriesItem: ArrayList<VenueStoryListRes.Data>? = null,position: Int):StoryPlayerActivity{
            venueStories=venueStoriesItem
            this.position=position
            return StoryPlayerActivity()
        }

    }

    var favorites : FavoriteListener?=null

    override fun getLayout(): Int {
        return R.layout.activity_story_player
    }

    override fun initViews() {

        if (venueStoryData != null) {
            reels.clear()
            reels.addAll(venueStoryData!!.data!!)
        }


        if (position == -1) {
            tempList.addAll(venueStories!!)
        } else {
            tempList.addAll(reels.subList(position, reels.size))
            tempList.addAll(reels.subList(0, position))
        }
        val userId: Int = PrefManager.getUser()?.user?.id ?: return

        mActivity?.runOnUiThread {
            // Update favorite status for new items
            tempList.forEach { item ->
                val isFavorite = realmController?.isFavorites(userId, item.venue!!.id) ?: 0
                item.venue!!.isFavorite = isFavorite
                if(isFavorite==3){
                    item.venue!!.isFavorite =0
                    val newFavorite = VenuesFavorites(userId, item.venue!!.id, 0)
                    realm?.executeTransaction { it.insertOrUpdate(newFavorite) }
                }else if (isFavorite == -1) {
                    val newFavorite = VenuesFavorites(userId, item.venue!!.id, 0)
                    realm?.executeTransaction { it.insertOrUpdate(newFavorite) }
                }
            }
        }

        adapterVideoView = VideoViewAdapter(tempList, binding.ivOnOff, initGlide(),common,adapterMyBookingListener)
        binding.rvVideo.layoutManager = LinearLayoutManager(mActivity)
        binding.rvVideo.adapter = adapterVideoView
        binding.rvVideo.setMediaObjects(tempList)

        binding.ivOnOff.setOnClickListener {
            binding.rvVideo.toggleVolume()
        }

        helper = PagerSnapHelper()
        helper!!.attachToRecyclerView(binding.rvVideo)

        binding.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

    }

    private fun initGlide(): RequestManager {
        val options = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
    var venuePosition:Int=0

    override fun onDestroy() {
        super.onDestroy()
        binding.rvVideo.releasePlayer()
    }

    var adapterMyBookingListener : AdapterMyBookingListener<VenueStoryListRes.Data> = object : AdapterMyBookingListener<VenueStoryListRes.Data>(){

        override fun onDirectionClick(item: VenueStoryListRes.Data, position: Int) {
            super.onDirectionClick(item, position)
            val directionDialog = DirectionsDialog(
                getBaseActivity()!!, LatLng(
                    PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude
                ), LatLng(
                    item.venue?.venue_latitude!!.toDouble(), item.venue?.venue_longitude!!.toDouble()
                )
            )
            directionDialog.show()
        }

        override fun onItemClick(item: VenueStoryListRes.Data, position: Int) {
            super.onItemClick(item, position)
            setFragmentWithStack(
                VenueBookFragment.newInstance(item.venue!!),
                VenueBookFragment::class.java.simpleName
            )
        }

        override fun onFavorite(item: VenueStoryListRes.Data, position: Int, isFavorites: Boolean) {
            super.onFavorite(item, position, isFavorites)
            venuePosition=position
            if (isFavorites) {
                setFavoriteData(item.venue!!.id)
                callEventFavourite(item.venue!!.id, 1)
                favorites?.onStatusUpdates(item.venue!!.id.toInt(), 1, SHARE_MESSAGE_TYPE.VENUE)
            } else {
                setFavoriteData(item.venue!!.id)
                callEventFavourite(item.venue!!.id, 0)
                favorites?.onStatusUpdates(item.venue!!.id.toInt(), 0, SHARE_MESSAGE_TYPE.VENUE)
            }
        }
    }
    fun setFavoriteData(id:Int) {
        val userId: Int = PrefManager.getUser()!!.user!!.id
        if (realmController!!.isFavorites(userId, id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl = realm.where(VenuesFavorites::class.java).equalTo("id",id).findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavorites(userId, id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(VenuesFavorites::class.java).equalTo("id", id)
                        .findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = VenuesFavorites(userId, id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }

    private fun callEventFavourite(id:Int, status: Int) {
        tempList.get(position).venue?.isFavorite = status
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(
            PrefManager.getAccessToken(),
            MarkFavouriteReq("" +id, 1, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                if (isValidResponse(response)) {
                }
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

}
package com.popiin.fragment

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.ExploreDetailsFragment
import com.popiin.activity.FavouriteEventDetailsFragment
import com.popiin.adapter.FavouriteVenueAdapter
import com.popiin.adapter.FavouritesAdapter
import com.popiin.databinding.FragmentFavouritesBinding
import com.popiin.listener.AdapterEventListner
import com.popiin.realm.EventFavorites
import com.popiin.realm.VenuesFavorites
import com.popiin.req.CommonReq
import com.popiin.req.FavouriteVenueReq
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import com.popiin.res.FavouriteEventsRes
import com.popiin.res.FavouriteEventsRes.FavouriteEvent
import com.popiin.res.VenueListRes
import com.popiin.res.VenueListRes.*
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.material.tabs.TabLayout
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import com.popiin.listener.FavoriteListener
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>(), FavoriteListener {
    private var pageNoVenue = defaultPage
    private var pageNoEvent = defaultPage
    private var isNextFragment = false
    private var selectedIndex = 0
    private var layoutManager: LinearLayoutManager? = null
    private var favouriteVenueAdapter: FavouriteVenueAdapter? = null
    private var favouriteVenues: ArrayList<Venue> = ArrayList()
    private var favouriteEventAdapter: FavouritesAdapter? = null
    private var favouriteEvents: ArrayList<FavouriteEvent> = ArrayList()

    companion object {
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_favourites
    }

    override fun initViews() {
        layoutManager = LinearLayoutManager(mActivity)
        binding.rvFavEvents.layoutManager = layoutManager
        favouriteEventAdapter = FavouritesAdapter(favouriteEvents, adapterEventListener)
        favouriteVenueAdapter = FavouriteVenueAdapter(favouriteVenues, adapterVenueListener)
        binding.rvFavEvents.addOnScrollListener(recyclerViewOnScrollListener)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: View = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.height = 78
            layoutParams.setMargins(0, 0, 24, 0)
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isLoading = true
                if (tab.position == 0) {
                    selectedIndex = 0
                    binding.rvFavEvents.adapter = favouriteVenueAdapter

                    if (favouriteVenues.size == 0) {
                        showProgress()
                        pageNoVenue = defaultPage
                        callToGetFavouriteVenues(pageNoVenue)
                    }
                } else {
                    selectedIndex = 1
                    binding.rvFavEvents.adapter = favouriteEventAdapter
                    if (favouriteEvents.size == 0) {
                        if (isNextFragment) {
                            isNextFragment = false
                        } else {

                            showProgress()
                            pageNoEvent = defaultPage
                            callToGetFavouriteEvents(pageNoEvent)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedIndex))
        if (selectedIndex == 0) {
            binding.rvFavEvents.adapter = favouriteVenueAdapter
            if (favouriteVenues.size == 0) {
                showProgress()
                pageNoVenue = defaultPage
                callToGetFavouriteVenues(pageNoVenue)
            }
        } else {
            binding.rvFavEvents.adapter = favouriteEventAdapter
            if (favouriteEvents.size == 0) {
                showProgress()
                pageNoEvent = defaultPage
                callToGetFavouriteEvents(pageNoEvent)
            }
        }

        binding.inclNoData.tvNoData.text = getString(R.string.txt_oops_error)

        binding.inclNoData.tvReload.setOnClickListener {
            if (selectedIndex == 0) {
                showProgress()
                callToGetFavouriteVenues(pageNoVenue)
            } else {
                showProgress()
                callToGetFavouriteEvents(pageNoEvent)
            }
        }


    }
    var venue:Venue?=null
    override fun onStatusUpdates(id: Int, status: Int, type: Int) {
        super.onStatusUpdates(id, status, type)
        if(type==SHARE_MESSAGE_TYPE.VENUE){
            if(status==0) {
                 venue = favouriteVenues.find { it.id == id }
                val isDeleted = favouriteVenues.removeAll() { it.id == id }
                favouriteEventAdapter?.notifyDataSetChanged()
            }else{
                if(venue!=null){
                    favouriteVenues.add(0, venue!!)
                    favouriteEventAdapter?.notifyDataSetChanged()
                }
            }
        }else if(type==SHARE_MESSAGE_TYPE.EVENT){
            if(status==0) {
                favouriteEventAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun callToGetFavouriteEvents(page: Int) {
        if (!isInternetConnect()) {
            return
        }
        binding.inclNoData.root.visibility = View.GONE
        if (this@FavouritesFragment.pageNoEvent == defaultPage) {
            favouriteEvents.clear()
        }
        val call: Call<FavouriteEventsRes?>? = apiInterface.getFavouriteEvents(
            PrefManager.getAccessToken(),
            CommonReq("" + pageLimit, "" + page)
        )
        call!!.enqueue(object : Callback<FavouriteEventsRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<FavouriteEventsRes?>,
                response: Response<FavouriteEventsRes?>,
            ) {
                if (isValidResponse(response)) {
                    if (response.body()!!.success) {
                        isLoading = true
                        favouriteEvents.addAll(response.body()!!.favouriteEvent!!)
                        favouriteEventAdapter!!.notifyDataSetChanged()
                        pageNoEvent++
                    } else {
                        isLoading = false
                    }

                    if (favouriteEvents.size == 0) {
                        binding.inclNoData.root.visibility = View.VISIBLE
                        binding.inclNoData.ivNoData.setImageDrawable(ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_fav_no_events))
                        binding.inclNoData.tvNoDataMessage.text =
                            getString(R.string.txt_no_favourites)
                        showToast(response.body()!!.message)
                    } else {
                        binding.inclNoData.root.visibility = View.GONE
                    }
                }
                hideProgress()
            }

            override fun onFailure(call: Call<FavouriteEventsRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    private fun callToGetFavouriteVenues(page: Int) {
        if (!isInternetConnect()) {
            return
        }
        binding.inclNoData.root.visibility = View.GONE
        if (this@FavouritesFragment.pageNoVenue == defaultPage) {
            favouriteVenues.clear()
        }
        val call: Call<VenueListRes?>? = apiInterface.getFavouriteVenue(
            PrefManager.getAccessToken(),
            FavouriteVenueReq(
                "" + pageLimit,
                "" + page,
                "" + PrefManager.lastLocation!!.latitude,
                "" + PrefManager.lastLocation!!.longitude
            )
        )
        call!!.enqueue(object : Callback<VenueListRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<VenueListRes?>, response: Response<VenueListRes?>) {
                if (mActivity!!.isValidResponse(response)) {
                    isLoading = if (response.body()!!.success) {
                        favouriteVenues.addAll(response.body()!!.venues)
                        pageNoVenue++
                        for (favouriteVenue in favouriteVenues) {
                            if (favouriteVenue == null) {
                                favouriteVenues.remove(favouriteVenue)
                            }
                        }
                        true
                    } else {
                        false
                    }

                    favouriteVenueAdapter!!.notifyDataSetChanged()
                    if (favouriteVenues.size == 0) {
                        binding.inclNoData.root.visibility = View.VISIBLE
                        binding.inclNoData.ivNoData.setImageDrawable(ContextCompat.getDrawable(
                           mActivity!!.mActivity,
                            R.drawable.ic_fav_no_events))
                        binding.inclNoData.tvNoDataMessage.text =
                            mActivity!!.getString(R.string.txt_no_favourites)
                        showToast(response.body()!!.message)
                    } else {
                        binding.inclNoData.root.visibility = View.GONE
                    }
                }
                hideProgress()
            }

            override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    var isLoading = true
    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                if (isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                        isLoading = false
                        if (selectedIndex == 0) {
                            callToGetFavouriteVenues(pageNoVenue)
                        } else {
                            callToGetFavouriteEvents(pageNoEvent)
                        }
                    }
                }
            }
        }

    var adapterEventListener: AdapterEventListner<FavouriteEvent?> =
        object : AdapterEventListner<FavouriteEvent?>() {
            override fun onItemClick(item: FavouriteEvent?, position: Int) {
                var favouriteEventDetailsFragment=FavouriteEventDetailsFragment.newInstance(item);
                favouriteEventDetailsFragment.favorite=this@FavouritesFragment
                setFragmentWithStack(
                    favouriteEventDetailsFragment,
                    FavouriteEventDetailsFragment::class.java.simpleName
                )
            }

            override fun onShareClick(item: FavouriteEvent?, position: Int) {
                super.onShareClick(item, position)

                if(item!!.event!!.share_link!=null && item!!.event!!.share_link!!.isNotEmpty()){
                    shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.event!!.name, url = item!!.event!!.share_link!! )
                }else {
                    val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().eventId, "" + id)
                    shareBranchIOLink(properties, object : BranchIOListener() {
                        override fun onLinkCreate(url: String?) {
                            super.onLinkCreate(url)
                            shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.event!!.name, url =url!! )
                            callPostUpdateLinkApi(item.id, url!!)
                        }
                    })
                }
            }

            override fun onFavouriteClick(
                item: FavouriteEvent?,
                position: Int,
                isFavourite: Boolean,
            ) {
                callEventFavourite(item!!.event!!.id, if (isFavourite) 1 else 0)
                val userId: Int = PrefManager.getUser()!!.user!!.id
                if (realmController!!.isFavoritesEvents(userId, item.event!!.id) == 1 || realmController!!.isFavoritesEvents(userId, item.event.id) == 0) {
                    realm!!.executeTransaction { realm ->
                        val rl = realm.where(EventFavorites::class.java)
                            .equalTo(
                                Constant().idConst, item.event
                                    .id
                            ).findFirst()
                        rl!!.status = 0
                    }
                } else {
                    val rl = EventFavorites(userId, item.event.id, 0)
                    realm!!.beginTransaction()
                    realm!!.copyToRealm(rl)
                    realm!!.commitTransaction()
                }
            }


        }


    private var adapterVenueListener: AdapterEventListner<Venue?> =
        object : AdapterEventListner<Venue?>() {
            override fun onItemClick(item: Venue?, position: Int) {
                item?.isFavorite=1
                var exploreDetailsFragment=ExploreDetailsFragment.newInstance(item)
                exploreDetailsFragment.favorites=this@FavouritesFragment
                setFragmentWithStack(
                    exploreDetailsFragment,
                    ExploreDetailsFragment::class.java.simpleName
                )
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            override fun onFavouriteClick(item: Venue?, position: Int, isFavourite: Boolean) {
                callVenueFavourite(item!!.id, if (isFavourite) 1 else 0)
                val userId: Int = PrefManager.getUser()!!.user!!.id
                if (realmController!!.isFavorites(
                        userId,
                        item.id
                    ) == 1 || realmController!!.isFavorites(userId, item.id) == 0
                ) {
                    realm!!.executeTransaction { realm ->
                        val rl =
                            realm.where(VenuesFavorites::class.java)
                                .equalTo(Constant().idConst, item.id)
                                .findFirst()
                        rl!!.status = 0
                    }
                } else {
                    val rl = VenuesFavorites(userId, item.id, 0)
                    realm!!.beginTransaction()
                    realm!!.copyToRealm(rl)
                    realm!!.commitTransaction()
                }
            }


            override fun onShareClick(item: Venue?, position: Int) {
                super.onShareClick(item, position)
                if(item!!.venue_share_link!=null && item.venue_share_link!!.isNotEmpty()){
                    shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =item.venue_share_link!! )
                }else {
                    val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
                    shareBranchIOLink(properties, object : BranchIOListener() {
                        override fun onLinkCreate(url: String?) {
                            super.onLinkCreate(url)
                            shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =url!! )
                            callPostVenueUpdateLinkApi(item.id, url!!)
                        }
                    })
                }
            }


            override fun onImageVenuesClick(
                item: Offerslive?,
                menusList: List<Menus?>?,
                position: Int,
            ) {

            }

        }


    private fun callEventFavourite(ids: Int, status: Int) {
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(PrefManager.getAccessToken(), MarkFavouriteReq("" + ids, 2, status))
        call!!.enqueue(object : Callback<CommonRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                if (isValidResponse(response)) {
//                    callLastEventFavourite();
                    if (response.body()!!.status) {
                        if (status == 0) {
                            for (favouriteEvent in favouriteEvents) {
                                if (ids == favouriteEvent.event!!.id) {
                                    favouriteEvents.remove(favouriteEvent)
                                    break
                                }
                            }
                            if (favouriteEvents.isEmpty()) {
                                binding.inclNoData.root.visibility = View.VISIBLE
                                binding.inclNoData.tvNoDataMessage.text =
                                    getString(R.string.txt_no_favourites)
                            }
                            favouriteEventAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }



    private fun callVenueFavourite(ids: Int, status: Int) {
        val call: Call<CommonRes?>? = apiInterface.marFavourite(
            PrefManager.getAccessToken(), MarkFavouriteReq("" + ids, 1, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                if (isValidResponse(response)) {
//                    callLastVenueFavourite();
                    if (response.body()!!.status) {
                        if (status == 0) {
                            for (favouriteVenue in favouriteVenues) {
                                if (ids == favouriteVenue.id) {
                                    favouriteVenues.remove(favouriteVenue)
                                    break
                                }
                            }
                            if (favouriteVenues.isEmpty()) {
                                binding.inclNoData.root.visibility = View.VISIBLE
                                binding.inclNoData.tvNoDataMessage.text =
                                    getString(R.string.txt_no_favourites)
                            }
                            favouriteVenueAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

}
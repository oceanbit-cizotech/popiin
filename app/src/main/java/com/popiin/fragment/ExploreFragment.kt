package com.popiin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.algolia.search.client.Index
import com.algolia.search.model.IndexName
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.EditProfileActivity
import com.popiin.activity.ExploreDetailsFragment
import com.popiin.activity.NotificationFragment
import com.popiin.activity.StoryPlayerActivity
import com.popiin.adapter.ExploreAdapter
import com.popiin.adapter.TagAdapter
import com.popiin.adapter.TrendingStoriesAdapter
import com.popiin.adapter.VenueSearchAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.bottomDialogFragment.SelectVenueFilterBottomFragment
import com.popiin.databinding.BottomFragmentSelectVenueFilterBinding
import com.popiin.databinding.FragmentExploreBinding
import com.popiin.listener.AdapterEventListener
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.AdapterTagClickListener
import com.popiin.listener.FilterClickListener
import com.popiin.listener.InfoDialogListener
import com.popiin.listener.VenueSearchItemClickListener
import com.popiin.listener.VerifyAccountListener
import com.popiin.model.TagModel
import com.popiin.model.VenueModel
import com.popiin.realm.VenuesFavorites
import com.popiin.req.ResendEmailReq
import com.popiin.req.VenueDetailsReq
import com.popiin.req.VenuesReq
import com.popiin.req.VerifyAccountReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueDetailsRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueListRes.Venue
import com.popiin.res.VenueStoryListRes
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import com.popiin.listener.FavoriteListener
import com.popiin.res.LoginRes
import com.popiin.util.LogHandler
import io.branch.referral.util.LinkProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExploreFragment : BaseFragment<FragmentExploreBinding>() ,FavoriteListener{
    private var searchText:String? =null
    private var selectedTag = 0
    var pageNo = 0
    var layoutManager: LinearLayoutManager? = null
    private var mainList: ArrayList<Venue> = ArrayList()
    private val list: ArrayList<Venue> = ArrayList()
    private var original: ArrayList<Venue> = ArrayList()
    private val searchList: ArrayList<VenueModel> = ArrayList()
    private lateinit var recentVenues: VenueListRes
    private var storiesList: ArrayList<VenueStoryListRes> = ArrayList()
    private lateinit var trendingStoriesAdapter: TrendingStoriesAdapter
    private var tagList: ArrayList<TagModel> = ArrayList()
    private lateinit var tagAdapter: TagAdapter
    private lateinit var venueAdapter: ExploreAdapter
    private lateinit var venueSearchAdapter: VenueSearchAdapter
    lateinit var index: Index
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    companion object {
        fun newInstance(): ExploreFragment {
            return ExploreFragment()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_explore
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ExploreFragment.requireActivity())

        startBackgroundHandlerThread()
        recentVenues = PrefManager.getRecentVenues()!!
        index = PrefManager.instance!!.algoliaClient.initIndex(IndexName(Constant().venues))
        layoutManager = LinearLayoutManagerWrapper(requireActivity(), LinearLayoutManager.VERTICAL, false)


        trendingStoriesAdapter = TrendingStoriesAdapter(storiesList, storyListener)
        binding.rvTrendStories.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendStories.adapter = trendingStoriesAdapter


        binding.ivNotification.setOnClickListener {
            setFragmentWithStack(
                NotificationFragment.newInstance(),
                NotificationFragment::class.java.simpleName
            )
        }

        binding.civProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }


        tagAdapter = TagAdapter(tagList, tagListener, selectedTag)
        binding.rvTags.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTags.adapter = tagAdapter

        tagList.clear()
        tagList.add(TagModel("\uD83C\uDF77 Bar", false, "Bar"))
        tagList.add(TagModel("\uD83C\uDFB6 Nightclub", false, "Nightclub"))
        tagList.add(TagModel("\uD83C\uDF54 Restaurant", false, "Restaurant"))
        tagList.add(TagModel("\uD83C\uDF7B Pub", false, "Pub"))
        tagList.add(TagModel("☕ Cafe", false, "Cafe"))
        tagList.add(TagModel("\uD83D\uDCB8 Offers", false, "Offers"))
        if(PrefManager.config?.trendingFlag==1) {
            tagList.add(TagModel("⭐Trending", false, getString(R.string.strtrending)))
        }
        if(PrefManager.config?.popularFlag==1) {
            tagList.add(TagModel("\uD83D\uDD25 Most Popular", false, getString(R.string.str_most_popular)))
        }

        binding.clVerify.setOnClickListener {
            callResendEmailApi()
            showVerifyAccountBottomFragment(verifyAccountListener)
        }
        venueAdapter = ExploreAdapter(list, listener)
        binding.rvExplore.layoutManager = LinearLayoutManagerWrapper(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvExplore.layoutManager!!.isAutoMeasureEnabled
        binding.rvExplore.setHasFixedSize(true)
        binding.rvExplore.setItemViewCacheSize(20)
        binding.rvExplore.adapter = venueAdapter

        binding.tvViewAll.setOnClickListener {
            val tempData: ArrayList<VenueStoryListRes.Data> = ArrayList()
            for (i in 0 until storiesList.size) {
                tempData.add(storiesList[i].data!![i])
            }
            val story=StoryPlayerActivity.newInstance(tempData, -1)
            story.favorites=this
            setFragmentAdd(
                story,
                StoryPlayerActivity::class.java.simpleName
            )
        }

        common.loadImageFromUrl(binding.civProfile, PrefManager.getUser().user!!.profilePic)

        binding.nsv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                setVenuesData(false)
            }
        })

        binding.edtSearchExplore.setOnClickListener{
            setFragmentAdd(
                VenueSearchFragment.newInstance(),
                VenueSearchFragment::class.java.simpleName
            )
        }

        binding.ivCancel.setOnClickListener {
            binding.edtSearchExplore.clearFocus()
            binding.ivCancel.visibility = View.GONE
            binding.edtSearchExplore.setText("")
            hideKeyBoard(requireActivity())
            if(isInternetConnect()) {
                updateList(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude)
            }
        }

        binding.ivFilter.setOnClickListener {
            openVenueFilterDialog()
        }

        binding.srl.setOnRefreshListener {
            if(isInternetConnect()) {
               fetchData()
            }
        }
        binding.tvUserName.text = PrefManager.getUser().user?.firstName



        val linearLayoutManager = LinearLayoutManager(getBaseActivity())
        linearLayoutManager.isAutoMeasureEnabled
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvSearch.layoutManager = linearLayoutManager
        venueSearchAdapter = VenueSearchAdapter(searchList, venueSearchListener)
        binding.rvSearch.adapter = venueSearchAdapter

        val animator: ItemAnimator? = binding.rvExplore.itemAnimator

        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }


        val text = "A code has been sent to your email, check your inbox or spam folder. " +
                "<b><font color='#000000'><big>Verify now</big></font></b>"
        binding.tvVerifyDesc.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            if(common.venueList.size>0){
                list.addAll(common.venueList)
                mainList.addAll(common.venueList)
               storiesList.addAll(common.oldStoriesList)
            }else{
                getLastLocation()
            }
        }
    }

    private fun fetchData(){
        LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} fetchData())", "INFO")
        if(isInternetConnect()) {
            LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} isInternetConnect true ", "INFO")
            common.venueList.clear()
            common.oldStoriesList.clear()
            callUserApi()
            updateList(
                PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude
            )
            callVenueStoryListApi()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
            fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {
                    val location: Location? = task.result
                    PrefManager.lastLocation = location
                    PrefManager.lastLocation = task.result
                    fetchData()
                }
            }
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied to access location", Toast.LENGTH_SHORT).show()
                Log.e("Permission", "Location permission denied by user")
            }
        }



    private fun showVerifyAccountBottomFragment(verifyAccountListener: VerifyAccountListener) {
        val verifyAccountBottomFragment =
            com.popiin.bottomDialogFragment.VerifyAccountBottomFragment(verifyAccountListener)
        verifyAccountBottomFragment.isCancelable = false
        childFragmentManager.let { verifyAccountBottomFragment.show(it, "") }
    }


    private var verifyAccountListener: VerifyAccountListener = object : VerifyAccountListener() {
        override fun onResendClick() {
            super.onResendClick()
            callResendEmailApi()
        }

        override fun onSubmitClick(
            verifyCode: String,
            bottomSheetDialog: com.popiin.bottomDialogFragment.VerifyAccountBottomFragment,
        ) {
            super.onSubmitClick(verifyCode, bottomSheetDialog)
            callVerifyAccountApi(verifyCode, bottomSheetDialog)
        }
    }

    private fun callResendEmailApi() {
        if (isInternetConnect()) {
            val call: Call<CommonRes?>? =
                apiInterface.sendVerificationEmail(
                    PrefManager.getAccessToken(),
                    ResendEmailReq(PrefManager.getUser().user!!.email!!, Constant().en)
                )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    hideProgress()
                    if(response.body()!!.status){
                        showToast(response.body()!!.msg)
                    }

                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })

        }
    }


    lateinit var bottomDialog: com.popiin.bottomDialogFragment.VerifyAccountBottomFragment
    private fun callVerifyAccountApi(
        verifyCode: String, bottomSheetDialog: com.popiin.bottomDialogFragment.VerifyAccountBottomFragment,
    ) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? = apiInterface.verifyAccount(
                PrefManager.getAccessToken(),
                VerifyAccountReq(PrefManager.getUser().user!!.email!!, verifyCode, Constant().en)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            isPayment = true
                            bottomDialog = bottomSheetDialog
                            callUserApi()
                            openErrorDialog(
                                requireContext(),
                                childFragmentManager,
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_pass_success
                                ),
                                getString(R.string.txt_success),
                                response.body()!!.msg!!,
                                isPayment,
                                "",
                                context!!.getString(R.string.txt_done)
                            )
                        } else {
                            isPayment = false
                            openErrorDialog(
                                requireContext(),
                                childFragmentManager,
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_sure_delete
                                ),
                                getString(R.string.txt_sorry),
                                response.body()!!.msg!!,
                                isPayment,
                                context!!.getString(R.string.txt_ok),
                                context!!.getString(R.string.txt_resend)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })

        }
    }


    var isPayment = false
    fun openErrorDialog(
        context: Context,
        manager: FragmentManager,
        image: Drawable?,
        title: String,
        desc: String,
        isPayment: Boolean,
        negativeText: String,
        positiveText: String,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                title, desc, negativeText, positiveText,
                ContextCompat.getColor(context, R.color.colorBlack),
                ContextCompat.getColor(context, R.color.colorBlack), isPayment, infoDialogListener
            )
        commonInfoBottomFragment.isCancelable = false
        commonInfoBottomFragment.show(manager, "")
    }

    var infoDialogListener: InfoDialogListener = object : InfoDialogListener() {

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
            if (isPayment) {
                bottomDialog.dismiss()
            } else {
                callResendEmailApi()
            }
        }

    }


    private fun callUserApi() {
        if (isInternetConnect()) {
            LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} callUserApi :: isInternetConnect true ", "INFO")
            val call: Call<LoginRes> = apiInterface.doGetUser(PrefManager.getAccessToken())
            call.enqueue(object : Callback<LoginRes> {
                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    if(isValidResponse(response)) {
                        if (response.body()!!.success) {
                            LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} callUserApi :: response "+response.body(), "INFO")
                            PrefManager.setUser(response.body()!!)
                            PrefManager.setHasEvent(response.body()!!.user!!.hasEvent)
                            PrefManager.setHasEvent(response.body()!!.user!!.hasVenue)
                            PrefManager.isStripeVerified = response.body()!!.user!!.is_stripe_verified
                            if (response.body()!!.user!!.status == 1) {
                                binding.clVerify.visibility = View.GONE
                            } else {
                                binding.clVerify.visibility = View.VISIBLE
                            }
                            if (response.body()!!.user!!.unreadCount > 0) {
                                binding.ivNotification.setBackgroundResource(R.drawable.ic_alert_with_red)
                            } else {
                                binding.ivNotification.setBackgroundResource(R.drawable.ic_alert)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                    LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} callUserApi :: onFailure true "+t.printStackTrace() , "INFO")
                }
            })
        }
    }




    private fun callVenueStoryListApi() {

        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueStoryListRes?>? =apiInterface.getVenueStoryList(
                   PrefManager.getAccessToken(),
                   PrefManager.lastLocation!!.latitude,
                   PrefManager.lastLocation!!.longitude,
                   PrefManager.config!!.storyScreenRadius
               )
          /*  val call: Call<VenueStoryListRes?>? =
                apiInterface.getVenueStoryList(
                    PrefManager.getAccessToken(),
                    51.3913458,
                    -0.100036,
                    PrefManager.config!!.storyScreenRadius
                )*/
            //    val latitude = 51.3913458
            //    val longitude = -0.100036
            call!!.enqueue(object : Callback<VenueStoryListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueStoryListRes?>,
                    response: Response<VenueStoryListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success) {
                            storiesList.clear()
                            for (i in 0 until response.body()!!.data!!.size) {
                                storiesList.add(response.body()!!)
                            }

                            if (storiesList.size == 0) {
                                binding.tvTrendingStories.visibility = View.GONE
                                binding.tvViewAll.visibility = View.GONE
                            } else {
                                binding.tvTrendingStories.visibility = View.VISIBLE
                                binding.tvViewAll.visibility = View.VISIBLE
                            }
                            common.oldStoriesList.clear()
                            common.oldStoriesList.addAll(storiesList)
                            trendingStoriesAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<VenueStoryListRes?>, t: Throwable) {
                    binding.srl.isRefreshing = false
                    LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} callVenueStoryListApi onFailure:: "+t.printStackTrace() , "INFO")
                    onApiFailure(throwable = t)
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    private var storyListener: AdapterItemClickListener<VenueStoryListRes> =
        object : AdapterItemClickListener<VenueStoryListRes>() {
            override fun onAdapterItemClick(item: VenueStoryListRes, position: Int) {
                super.onAdapterItemClick(item, position)

                val story=StoryPlayerActivity.newInstance(item, position)
                story.favorites=this@ExploreFragment
                setFragmentAdd(
                    story,
                    StoryPlayerActivity::class.java.simpleName
                )
                /* startActivity(Intent(requireActivity(), StoryPlayerActivity::class.java).putExtra(
                     Constant().item,
                     item).putExtra(Constant().position, position))*/
             ///   requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }

    private fun callToGetVenueDetail(venueId: String) {
        showProgress()
        val call: Call<VenueDetailsRes?>? =
            apiInterface.postVenueDetails(PrefManager.getAccessToken()!!,
                VenueDetailsReq(venueId, PrefManager.lastLocation?.longitude.toString(),
                    PrefManager.lastLocation?.latitude.toString()
                )
            )
        call!!.enqueue(object : Callback<VenueDetailsRes?>, FavoriteListener {
            override fun onResponse(call: Call<VenueDetailsRes?>, response: Response<VenueDetailsRes?>) {
                hideProgress()
                if(response.isSuccessful){
                    val exploreDetailsFragment = ExploreDetailsFragment.newInstance(response.body()!!.venues)
                    exploreDetailsFragment.favorites=this
                    setFragmentAdd(
                        exploreDetailsFragment,
                        ExploreDetailsFragment::class.java.simpleName
                    )
                }

            }

            override fun onFailure(call: Call<VenueDetailsRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }
    private var venueSearchListener: VenueSearchItemClickListener<VenueModel> =
        object : VenueSearchItemClickListener<VenueModel>() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(venue: VenueModel?) {
                super.onItemClick(venue)
                callToGetVenueDetail(venueId = ""+venue!!.id)
                hideKeyBoard(requireActivity())
                binding.edtSearchExplore.setText("")
                venueSearchAdapter.notifyDataSetChanged()
                binding.rvSearch.visibility = View.GONE
            }
        }

    private var tagListener: AdapterTagClickListener<TagModel> =
        object : AdapterTagClickListener<TagModel>() {
            override fun onTagClick(item: TagModel, position: Int) {
                super.onTagClick(item, position)
                selectedTag = position
                selectedVenueType = tagList[position].name
                pageNo = defaultPage
                mainList.clear()
                list.clear()
                mainList.clear()
                original.clear()
                venueAdapter.notifyDataSetChanged()
                if(isInternetConnect()) {
                    updateList(
                        PrefManager.lastLocation!!.latitude,
                        PrefManager.lastLocation!!.longitude
                    )
                }
            }
        }


    private fun openVenueFilterDialog() {
        val selectVenueFilterFragment = SelectVenueFilterBottomFragment(venueFilterListener)
        selectVenueFilterFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectVenueFilterFragment.show(it, "") }
    }

    private var venueFilterListener: FilterClickListener = object : FilterClickListener() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onFilterItemClick(
            binding: BottomFragmentSelectVenueFilterBinding,
            filter: Int,
            dialog: Dialog?,
            isReset: Boolean,
        ) {
            super.onFilterItemClick(binding, filter, dialog, isReset)
            if (PrefManager.getOpenNow() == 0) {
                if(isInternetConnect()) {
                    updateList(
                        PrefManager.lastLocation!!.latitude,
                        PrefManager.lastLocation!!.longitude
                    )
                }
            } else {
                if(isInternetConnect()) {
                    updateList(
                        PrefManager.lastLocation!!.latitude,
                        PrefManager.lastLocation!!.longitude
                    )
                }
            }
        }
    }

    var listener: AdapterEventListener<Venue> = object : AdapterEventListener<Venue>() {
        override fun onItemClick(item: Venue, position: Int) {
            super.onItemClick(item, position)
            val exploreDetailsFragment = ExploreDetailsFragment.newInstance(item)
            exploreDetailsFragment.favorites=this@ExploreFragment
            setFragmentAdd(
                exploreDetailsFragment,
                ExploreDetailsFragment::class.java.simpleName
            )
        }

        override fun onFavouriteClick(item: Venue, position: Int, isFavourite: Boolean) {
            super.onFavouriteClick(item, position, isFavourite)
            if (isFavourite) {
                setFavoriteData(item)
                callEventFavourite(item, 1)
            } else {
                setFavoriteData(item)
                callEventFavourite(item, 0)
            }

        }

        override fun onBookNowClick(item: Venue, position: Int) {
            super.onBookNowClick(item, position)
            setFragmentWithStack(
                VenueBookFragment.newInstance(item),
                VenueBookFragment::class.java.simpleName
            )
        }

        override fun onShareClick(item: Venue, position: Int) {
            super.onShareClick(item, position)
            if(item.venue_share_link!=null && item.venue_share_link!!.isNotEmpty()){
                shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =item.venue_share_link!! )
            }else {
                val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
                shareBranchIOLink(properties, object : BranchIOListener() {
                    override fun onLinkCreate(url: String?) {
                        super.onLinkCreate(url)
                        shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =url!! )
                        callPostVenueUpdateLinkApi(item.id, url)
                    }
                })
            }
        }
    }

    fun setFavoriteData(item: Venue) {
        val userId: Int = PrefManager.getUser().user!!.id
        if (realmController!!.isFavorites(userId, item.id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl = realm.where(VenuesFavorites::class.java).equalTo("id", item.id).findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavorites(userId, item.id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(VenuesFavorites::class.java).equalTo("id", item.id)
                        .findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = VenuesFavorites(userId, item.id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }

    private fun callEventFavourite(item: Venue, status: Int) {
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(
            PrefManager.getAccessToken(),
            com.popiin.req.MarkFavouriteReq("" + item.id, 1, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                if (isValidResponse(response)) {
                    item.isFavorite = status
                }
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }
    private lateinit var strMostPopular: String
    private lateinit var strTrending: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        strMostPopular = getString(R.string.str_most_popular)
        strTrending = getString(R.string.strtrending)
    }
    var selectedVenueType: String = "Bar"
    private fun updateList( latitude: Double, longitude: Double) {
        if(isInternetConnect()) {
            showProgress()
           // val latitude = 51.3913458
           // val longitude = -0.100036

          var trending:Int?=null
          var myList: MutableList<String>? = null
            if(selectedVenueType==strMostPopular){
                searchText=null
            }else if(selectedVenueType==strTrending){
                trending=1
            }else{
                myList = (myList ?: mutableListOf()).apply {
                    add(selectedVenueType)
                }
            }

          val call: Call<VenueListRes?>?

          if (selectedVenueType == Constant().Offers) {
              myList?.clear()
              call = apiInterface.getVenueList(
                  VenuesReq(
                      search = searchText,
                      longi = "" + longitude,
                      lat = "" + latitude,
                      distance = PrefManager.config!!.exploreScreenRadius,
                      trending = trending
                  )
              )
          } else {
              call = apiInterface.getVenueList(
                  VenuesReq(
                      search = searchText,
                      longi = "" + longitude,
                      lat = "" + latitude,
                      distance = PrefManager.config!!.exploreScreenRadius,
                      venue_type = myList,
                      trending = trending
                  )
              )
          }
          call!!.enqueue(object : Callback<VenueListRes?> {
              override fun onResponse(
                  call: Call<VenueListRes?>,
                  response: Response<VenueListRes?>
              ) {
                  list.clear()
                  mainList.clear()
                  original.clear()
                  if (isValidResponse(response)) {
                      if (pageNo == defaultPage) {
                          mainList.clear()
                      }

                      if (response.body()!!.success) {

                          mainList.addAll(response.body()!!.venues)
                          pageNo++
                          isLoading = true
                          if(selectedVenueType==strMostPopular){
                            val sortedList = mainList.sortedByDescending { it.total_views }
                              mainList.clear()
                              mainList.addAll(sortedList)
                          }
                          for (i in 0 until mainList.size) {
                              if (selectedVenueType == Constant().Offers) {
                                  if (mainList[i].offerslive != null && mainList[i].offerslive!!.isNotEmpty()) {
                                      var isOfferLiveVisible = false
                                      for (item in mainList[i].offerslive!!) {
                                          if (common.isOfferAvailable(
                                                  item.working_day!!,
                                                  item.open_time!!,
                                                  item.close_time!!
                                              )
                                          ) {
                                              isOfferLiveVisible = true
                                              break
                                          }
                                      }
                                      if (isOfferLiveVisible) {
                                          val isOpen=common.getOpenCloseText(mainList[i])!!
                                          mainList[i].isOpen = isOpen
                                          if(PrefManager.getOpenNow()==0){
                                              if(isOpen.equals("Open Now")) {
                                                  original.add(mainList[i])
                                              }
                                          }else{
                                              original.add(mainList[i])
                                          }

                                      }
                                  }
                              } else {
                                  val isOpen=common.getOpenCloseText(mainList[i])!!
                                  mainList[i].isOpen = isOpen
                                  if(PrefManager.getOpenNow()==0){
                                      if(isOpen.equals("Open Now")) {
                                          original.add(mainList[i])
                                      }
                                  }else{
                                      original.add(mainList[i])
                                  }                              }
                          }
                          if (selectedVenueType == Constant().Offers || PrefManager.getOpenNow()==0) {
                              mainList.clear()
                              mainList.addAll(original)
                          }

                      }
                      isLoading = true
                      setVenuesData(true)
                      if (mainList.size == 0) {
                          binding.tvNoVenueData.visibility = View.VISIBLE
                          binding.tvNoVenueData.text = getString(R.string.txt_no_venues_display)
                      } else {
                          binding.tvNoVenueData.visibility = View.GONE
                      }
                  }
                  Handler(Looper.myLooper()!!).postDelayed({ hideProgress() }, 1000)
                  binding.srl.isRefreshing = false
              }

              override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                  binding.srl.isRefreshing = false
                  LogHandler.writeLog(mActivity!!, "${ExploreFragment::class.java.simpleName} onFailure true "+t.printStackTrace() , "INFO")
                  onApiFailure(throwable = t)
              }
          })
      }
    }


    var isLoading = true

    fun setVenuesData(isShowMessage: Boolean) {
        val userId: Int = PrefManager.getUser().user?.id ?: return

        mHandler?.post {
            val currentSize = list.size
            val mainVenueSize = mainList.size
            val nextVenues = (currentSize + pageLimit).coerceAtMost(mainVenueSize)

            // Add new venues to the list
            list.addAll(mainList.subList(currentSize, nextVenues))

            mActivity?.runOnUiThread {
                // Update favorite status for new items
                list.subList(currentSize, nextVenues).forEach { venue ->
                    val isFavorite = realmController?.isFavorites(userId, venue.id) ?: 0
                    venue.isFavorite = isFavorite
                    if (isFavorite == -1) {
                        val newFavorite = VenuesFavorites(userId, venue.id, 0)
                        realm?.executeTransaction { it.insertOrUpdate(newFavorite) }
                    }
                }

                // Handle empty list message visibility
                if (isShowMessage) {
                    binding.tvNoVenueData.apply {
                        visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        text = if (list.isEmpty()) getString(R.string.txt_no_venues_display) else ""
                    }
                }

                binding.srl.isRefreshing = false
                common.venueList.clear()
                common.venueList.addAll(list)
                // Notify adapter of data changes
                if (currentSize == 0) {
                    venueAdapter.notifyDataSetChanged()
                } else {
                    venueAdapter.notifyItemRangeInserted(currentSize, nextVenues - currentSize)
                }
            }
        }
    }

    private var mHandler: Handler? = null

    private var mHandlerThread: HandlerThread? = null

    private fun startBackgroundHandlerThread() {
        mHandlerThread = HandlerThread("BackgroundThread")
        mHandlerThread!!.start()
        mHandler = Handler(mHandlerThread!!.looper)
    }


    class LinearLayoutManagerWrapper(context: Context?, orientation: Int, reverseLayout: Boolean) :
        LinearLayoutManager(
            context,
            orientation,
            reverseLayout
        ) {

        override fun supportsPredictiveItemAnimations(): Boolean {
            return false
        }
    }

    private fun resetData() {
        list.clear()
        mainList.clear()
        mainList.addAll(original)
        mainList = ArrayList(mainList.distinct())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        venueAdapter.let {
            venueAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onStatusUpdates(venueId: Int, status: Int, type: Int) {
        super.onStatusUpdates(venueId, status, type)
        list.find { it.id == venueId }?.apply {
            isFavorite = status
            venueAdapter.notifyItemChanged(list.indexOf(this)) // Optimized update
        } ?: Log.e("StatusUpdate", "Venue with ID $venueId not found")
    }
}
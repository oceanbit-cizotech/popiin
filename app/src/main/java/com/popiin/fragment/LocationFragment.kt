package com.popiin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.search.client.Index
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.AroundRadius
import com.algolia.search.model.search.Point
import com.algolia.search.model.search.Query
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.ExploreDetailsFragment
import com.popiin.activity.StoryPlayerActivity
import com.popiin.adapter.MapListAdapter
import com.popiin.adapter.MapSearchAdapter
import com.popiin.adapter.TrendingStoriesAdapter
import com.popiin.annotation.FILTER_MAP
import com.popiin.bottomDialogFragment.SelectMapFilterBottomFragment
import com.popiin.databinding.BottomFragmentSelectMapFilterBinding
import com.popiin.databinding.DialogEmptyLocationBinding
import com.popiin.databinding.FragmentLocationBinding
import com.popiin.dialog.DirectionsDialog
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.MapFilterListener
import com.popiin.listener.MapSearchItemClickListener
import com.popiin.listener.MapVenueListener
import com.popiin.model.MarkerDetails
import com.popiin.model.TagModel
import com.popiin.realm.VenuesFavorites
import com.popiin.req.VenuesReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueListRes.Venue
import com.popiin.res.VenueStoryListRes
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import io.branch.referral.util.LinkProperties
import io.realm.Realm
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LocationFragment : BaseFragment<FragmentLocationBinding>(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    private var mMap: GoogleMap? = null
    private lateinit var lastLocation: Location
    private var markerOptions: MarkerOptions? = null
    private var venue: Venue? = null
    private var apiLatitude: Double = 0.0
    private var apiLongitude: Double = 0.0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var mapList: ArrayList<Venue> = ArrayList()
    private val venues: ArrayList<Venue> = ArrayList()
    private val venuesSearch: ArrayList<Venue> = ArrayList()
    private val markerDetails: ArrayList<MarkerDetails> = ArrayList()
    private val original: ArrayList<Venue> = ArrayList()
    private lateinit var mapListAdapter: MapListAdapter
    private lateinit var mapSearchAdapter: MapSearchAdapter
    private var storiesList: ArrayList<VenueStoryListRes> = ArrayList()
    private lateinit var trendingStoriesAdapter: TrendingStoriesAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    lateinit var index: Index
    private lateinit var inputMethodManager: InputMethodManager
    companion object {
        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_location
    }

    override fun initViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.includeBottomSheet.clMain)
        if (mapList.size > 0) {
            binding.includeBottomSheet.root.visibility = View.VISIBLE
        } else {
            binding.includeBottomSheet.root.visibility = View.GONE
        }
        bottomSheetBehavior.peekHeight = 250

        setCurrentLocation()
        index = PrefManager.instance!!.algoliaClient.initIndex(IndexName(Constant().venues))
        inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        binding.searchBar.ivFilter.setOnClickListener {
            openFilterBottomDialog()
        }


        binding.ivCurrentLocationIcon.setOnClickListener {
            setCurrentLocation()
        }

        binding.rvSearch.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        mapSearchAdapter = MapSearchAdapter(venuesSearch, mapSearchItemClickListener)
        binding.rvSearch.adapter = mapSearchAdapter

        trendingStoriesAdapter = TrendingStoriesAdapter(storiesList, storyListener)
        binding.rvTrendStories.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendStories.adapter = trendingStoriesAdapter

        binding.searchBar.edtSearch.setOnClickListener {
            setFragmentAdd(
                VenueSearchFragment.newInstance(),
                VenueSearchFragment::class.java.simpleName
            )
        }
      /*  binding.searchBar.edtSearch.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.searchBar.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    binding.rvSearch.visibility = View.VISIBLE
                    binding.searchBar.ivSearchClose.visibility = View.VISIBLE
                    searchVenues(s.toString(),
                        LatLng(PrefManager.lastLocation!!.latitude,
                            PrefManager.lastLocation!!.longitude)) { venues ->
                        requireActivity().runOnUiThread {
                            if (venues.isNotEmpty()) {
                                venuesSearch.clear()
                                venuesSearch.addAll(venues)
                                mapSearchAdapter.filter(s.toString())
                                mapSearchAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    binding.searchBar.ivSearchClose.visibility = View.GONE
                    binding.rvSearch.visibility = View.GONE
                    binding.searchBar.edtSearch.clearFocus()
                    hideKeyBoard(requireActivity())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchBar.ivSearchClose.setOnClickListener {
            hideKeyBoard(requireActivity())
            binding.searchBar.edtSearch.clearFocus()
            binding.searchBar.ivSearchClose.visibility = View.GONE
            binding.searchBar.edtSearch.setText("")
        }*/


        mapListAdapter = MapListAdapter(mapList, listener)
        binding.includeBottomSheet.rvMap.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.includeBottomSheet.rvMap.adapter = mapListAdapter

        binding.tvSearch.setOnClickListener {
            hitAPIForVenuesList(true, apiLatitude, apiLongitude, true)
        }

        tagList.add(TagModel("\uD83C\uDF77 Bar", false, "Bar"))
        tagList.add(TagModel("\uD83C\uDFB6 Nightclub", false, "Nightclub"))
        tagList.add(TagModel("\uD83C\uDF54 Restaurant", false, "Restaurant"))
        tagList.add(TagModel("\uD83C\uDF7B Pub", false, "Pub"))
        tagList.add(TagModel("☕ Cafe", false, "Cafe"))
        tagList.add(TagModel("\uD83D\uDCB8 Offers", false, "Offers"))
        tagList.add(TagModel("⭐Trending", false, getString(R.string.strtrending)))
        tagList.add(
            TagModel(
                "\uD83D\uDD25 Most Popular",
                false,
                getString(R.string.str_most_popular)
            )
        )
        if(PrefManager.config?.trendingFlag==1) {
            tagList.add(TagModel("⭐Trending", false, getString(R.string.strtrending)))
        }
        if(PrefManager.config?.popularFlag==1) {

        }

        callVenueStoryListApi(true)

        binding.tvViewAll.setOnClickListener {
            val tempData: ArrayList<VenueStoryListRes.Data> = ArrayList()
            for (i in 0 until storiesList.size) {
                tempData.add(storiesList[i].data!![i])
            }
            setFragmentAdd(
                StoryPlayerActivity.newInstance(tempData, -1),
                StoryPlayerActivity::class.java.simpleName
            )
        }

    }
    private var tagList: ArrayList<TagModel> = ArrayList()

    private fun callVenueStoryListApi(isLoader: Boolean) {
        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }
            val call: Call<VenueStoryListRes?>? =
                apiInterface.getVenueStoryList(PrefManager.getAccessToken(),
                    PrefManager.lastLocation!!.latitude,
                    PrefManager.lastLocation!!.longitude,
                    PrefManager.config!!.storyScreenRadius)
            call!!.enqueue(object : Callback<VenueStoryListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueStoryListRes?>,
                    response: Response<VenueStoryListRes?>,
                ) {
                    if (mActivity!!.isValidResponse(response)) {
                        storiesList.clear()
                        for (i in 0 until response.body()!!.data!!.size) {
                            storiesList.add(response.body()!!)
                        }

                        if (storiesList.size == 0) {
                            binding.clStories.visibility = View.GONE
                        } else {
                            binding.clStories.visibility = View.VISIBLE
                        }

                        trendingStoriesAdapter.notifyDataSetChanged()
                        hideProgress()
                    }
                }

                override fun onFailure(call: Call<VenueStoryListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private var storyListener: AdapterItemClickListener<VenueStoryListRes> =
        object : AdapterItemClickListener<VenueStoryListRes>() {
            override fun onAdapterItemClick(item: VenueStoryListRes, position: Int) {
                super.onAdapterItemClick(item, position)

                setFragmentAdd(
                    StoryPlayerActivity.newInstance(item,position),
                    StoryPlayerActivity::class.java.simpleName
                )
            }
        }

    private var mapSearchItemClickListener: MapSearchItemClickListener =
        object : MapSearchItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(venue: Venue?) {
                super.onItemClick(venue)
                hideKeyBoard(requireActivity())
                binding.searchBar.edtSearch.setText("")
//                binding.searchBar.edtSearch.setSelection(venue.venue_name!!.length)
//                venuesSearch.clear()
                mapSearchAdapter.notifyDataSetChanged()
                binding.rvSearch.visibility = View.GONE

                setFragmentAdd(
                    ExploreDetailsFragment.newInstance(venue),
                    ExploreDetailsFragment::class.java.simpleName
                )
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)


            }
        }

    private fun openFilterBottomDialog() {
        val selectMapFilterFragment = SelectMapFilterBottomFragment(mapFilterListener)
        selectMapFilterFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectMapFilterFragment.show(it, "") }
    }

    private var mapFilterListener: MapFilterListener = object : MapFilterListener() {
        override fun onFilterItemClick(binding: BottomFragmentSelectMapFilterBinding, filter: Int) {
            super.onFilterItemClick(binding, filter)
            performFilterSearch(filter)
            selectedVenueType=tagList.get(filter).name
            hitAPIForVenuesList(true, apiLatitude, apiLongitude, true)
        }
    }
    var selectedVenueType : String="Bar"
    private fun setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.lastLocation.addOnSuccessListener(
            requireActivity()
        ) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                apiLatitude = location.latitude
                apiLongitude = location.longitude

            } else {
                apiLatitude = PrefManager.lastLocation!!.latitude
                apiLongitude = PrefManager.lastLocation!!.longitude
            }

            moveMap()

        }
    }

    private fun moveMap() {
        if (activity != null && isAdded) {
            val supportFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportFragment.getMapAsync(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
       // setUpMap(latitude, longitude)
         setUpMap(apiLatitude, apiLongitude)
    }

    private fun setMapStyle() {
        try {
            val styleJson = loadJsonFromAssets("mapstyle.json")
            val success = mMap!!.setMapStyle(MapStyleOptions(styleJson))

        } catch (e: Exception) {
            Log.e("MapsActivity", "Error loading map style.", e)
        }
    }

    private fun loadJsonFromAssets(filename: String): String {
        return mActivity!!.assets.open(filename).bufferedReader().use { it.readText() }
    }

    private var circle: Circle? = null
    private fun setUpMap(latitude: Double, longitude: Double) {

        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val iMeter: Double = PrefManager.config!!.mapScreenRadius * 1509.34

        if (circle != null && circle!!.isVisible) {
            circle!!.remove()
        }

        setMapStyle()
        circle = mMap!!.addCircle(CircleOptions()
            .center(LatLng(latitude, longitude))
            .radius(iMeter)
            .strokeColor(Color.BLUE)
            .strokeWidth(2f)
            .fillColor(Color.parseColor("#500084d3")))

        circle!!.isVisible


        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(circle!!.center,
            12.8f))
        // mMap!!.setOnMarkerClickListener(this)
        markerOptions = MarkerOptions()
        markerOptions!!.position(
            LatLng(apiLatitude,apiLongitude)
        )
        markerOptions!!.icon(
            common.bitmapFromVector(
                requireContext(),
                R.drawable.ic_current_location_new
            )
        )
        mMap!!.addMarker(markerOptions!!)!!

        binding.ivCurrentLocation.setMarginBottom(48)

        mMap!!.setOnMarkerClickListener(this)


        var iMeterMove = 0.0
        var target = LatLng(0.0, 0.0)


        mMap!!.setOnCameraMoveStartedListener {
            android.os.Handler(Looper.getMainLooper()).postDelayed({

                iMeterMove = PrefManager.config!!.mapScreenRadius * 1609.34

                target = mMap!!.cameraPosition.target


                if (circle!!.isVisible) {
                    circle!!.remove()
                }
                circle = mMap!!.addCircle(CircleOptions()
                    .center(LatLng(target.latitude, target.longitude))
                    .radius(iMeter)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(2f)
                    .fillColor(Color.parseColor("#500084d3")))


                circle!!.isVisible
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(circle!!.center))

                apiLatitude = target.latitude
                apiLongitude = target.longitude


            }, 2500)


        }


    }

    private fun setMapListData() {
        if (mapList.size > 0) {
            binding.includeBottomSheet.root.visibility = View.VISIBLE

            bottomSheetBehavior.peekHeight = 290


            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    println("onStateChanged : STATE : $newState")
                    println("onStateChanged : BottomSheet : $bottomSheet")
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    println("onSlide : slideOffset : $slideOffset")
                    println("onSlide : BottomSheet : $bottomSheet")

                    binding.ivCurrentLocation.animate().scaleX(1f).scaleY(1f).setDuration(0).start()
                }
            })
        }

    }



    private fun callVenueFavourite(data: Venue, status: Int) {
        if(isInternetConnect()) {
            val call: Call<CommonRes?>? = apiInterface.marFavourite(
                PrefManager.getAccessToken(),
                com.popiin.req.MarkFavouriteReq("" + data.id, 1, status)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        data.isFavorite = status
                        refreshMarker()
                    }
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    fun setFavoriteData(item: Venue) {
        val userId: Int = PrefManager.getUser()!!.user!!.id
        if (realmController!!.isFavorites(userId, item.id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl: VenuesFavorites? =
                    realm.where(VenuesFavorites::class.java).equalTo("id", item.id).findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavorites(userId, item.id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl: VenuesFavorites? =
                    realm.where(VenuesFavorites::class.java).equalTo("id", item.id).findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = VenuesFavorites(userId, item.id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }

    fun setVenuesData() {
        val userId: Int = PrefManager.getUser()!!.user!!.id
        requireActivity().runOnUiThread {
            for (i in 0 until venues.size) {
                if (realmController!!.isFavorites(userId, venues[i].id) == 1) {
                    venues[i].isFavorite = 1
                } else if (realmController!!.isFavorites(userId, venues[i].id) == 0) {
                    venues[i].isFavorite = 0
                } else {
                    venues[i].isFavorite = 0
                    val rl = VenuesFavorites(userId, venues[i].id, 0)
                    realm!!.beginTransaction()
                    realm!!.copyToRealmOrUpdate(rl)
                    realm!!.commitTransaction()
                }
            }


            mapList.clear()
            mapList.addAll(venues)

            mapListAdapter.notifyDataSetChanged()
            setMapListData()
        }
    }

    private fun onLocationChanged(location: Location) {
        lastLocation = location
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), common.getZoomLevel(circle).toFloat()
            )
        )

    }
    private lateinit var strMostPopular: String
    private lateinit var strTrending: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        strMostPopular = getString(R.string.str_most_popular)
        strTrending = getString(R.string.strtrending)
    }
    private var searchText:String? =null

    private fun hitAPIForVenuesList(
        isLoader: Boolean,
        latitude: Double,
        longitude: Double,
        isSearchClicked: Boolean,
    ) {
        if (isInternetConnect()) {
            original.clear()
            if (isLoader) {
                showProgress()
            }
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

            if (selectedVenueType.equals(Constant().Offers)) {
                call = apiInterface.getVenueList(
                    VenuesReq(
                        search = searchText,
                        longi = "" + longitude,
                        lat = "" + latitude,
                        distance = PrefManager.config!!.mapScreenRadius,
                        trending = trending
                    )
                )
            }else{
                call = apiInterface.getVenueList(
                VenuesReq(
                    search = searchText,
                    longi = "" + longitude,
                    lat = "" + latitude,
                    distance = PrefManager.config!!.mapScreenRadius,
                    venue_type = myList,
                    trending = trending
                )
                )
            }


            call!!.enqueue(object : Callback<VenueListRes?> {
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        venues.clear()
                        venues.addAll(response.body()!!.venues)
                        for (i in 0 until venues.size) {
                            if (selectedVenueType.equals(Constant().Offers)) {
                                venues[i].offerslive = (common.getActiveOffer(venues[i].offerslive!!))
                                if (venues[i].offerslive != null && venues[i].offerslive!!.size > 0) {
                                    var isOfferLiveVisible = false
                                    for (item in venues[i].offerslive!!) {
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
                                        venues[i].isOpen = common.getOpenCloseText(venues[i])!!
                                        original.add(venues[i])
                                    }
                                }

                            }else{
                                venues[i].offerslive = (common.getActiveOffer(venues[i].offerslive!!))
                            }
                        }
                        if (selectedVenueType.equals(Constant().Offers)) {
                            venues.clear()
                            venues.addAll(original)
                            original.clear()
                        }
                        original.addAll(venues)

                        if (isSearchClicked) {
                            setVenuesData()
                            refreshMarker()
                        }

                    } else {
                        if (isSearchClicked) {
                            removeAllMarkers()
                            binding.includeBottomSheet.root.visibility = View.GONE
                            openNoLocationDialog()
                        }
                    }
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun refreshMarker() {
        mMap!!.clear()
        markerOptions = MarkerOptions()
        markerOptions!!.position(
            LatLng(apiLatitude,apiLongitude)
        )
        markerOptions!!.icon(
            common.bitmapFromVector(
                requireContext(),
                R.drawable.ic_current_location_new
            )
        )
        mMap!!.addMarker(markerOptions!!)
        for (i in 0 until mapList.size) {
            markerOptions = MarkerOptions()
            markerOptions!!.position(
                LatLng(
                    mapList[i].venue_latitude
                    !!.toDouble(), mapList[i].venue_longitude!!.toDouble()
                )
            )
            markerOptions!!.title("")
            markerOptions!!.flat(true)
            markerOptions!!.infoWindowAnchor(0.5f, 0.2f)
            if (mapList[i].isFavorite == 1) {
                markerOptions!!.icon(
                    common.bitmapFromVector(
                        requireContext(),
                        R.drawable.ic_venue_fav_marker
                    )
                )
            } else {
                markerOptions!!.icon(
                    common.bitmapFromVector(
                        requireContext(),
                        R.drawable.ic_blue_map_marker
                    )
                )
            }

            markerDetails.add(
                MarkerDetails(
                    mMap!!.addMarker(markerOptions!!)!!,
                    mapList[i].id
                )
            )
        }
    }

    private fun removeAllMarkers() {
        for (mLocationMarker in markerDetails) {
            mLocationMarker.marker.remove()
        }
        markerDetails.clear()
    }

    private fun performFilterSearch(filter: Int) {

        when (filter) {
            FILTER_MAP.BAR -> {
                venues.clear()
                for (i in original.indices) {
                    var venueType = ""
                    val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(
                        original[i]
                    )
                    if (topThreeVenueType.isEmpty()) {
                        venueType = getString(R.string.txt_not_available)
                    } else {
                        for (venueTypeString in topThreeVenueType) {
                            venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
                        }
                    }
                    if (venueType.lowercase(Locale.getDefault()).contains(Constant().bar)) {
                        venues.add(original[i])
                    }
                }

            }

            FILTER_MAP.NIGHTCLUB -> {
                venues.clear()
                for (i in original.indices) {
                    var venueType = ""
                    val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(
                        original[i]
                    )
                    if (topThreeVenueType.isEmpty()) {
                        venueType = getString(R.string.txt_not_available)
                    } else {
                        for (venueTypeString in topThreeVenueType) {
                            venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
                        }
                    }
                    if (venueType.lowercase(Locale.getDefault()).contains(Constant().nightclub)) {
                        venues.add(original[i])
                    }
                }

            }
            FILTER_MAP.RESTAURANT -> {
                venues.clear()
                for (i in original.indices) {
                    var venueType = ""
                    val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(
                        original[i]
                    )
                    if (topThreeVenueType.isEmpty()) {
                        venueType = getString(R.string.txt_not_available)
                    } else {
                        for (venueTypeString in topThreeVenueType) {
                            venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
                        }
                    }
                    if (venueType.lowercase(Locale.getDefault()).contains(Constant().restaurant)) {
                        venues.add(original[i])
                    }
                }

            }

            FILTER_MAP.PUB -> {
                venues.clear()
                for (i in original.indices) {
                    var venueType = ""
                    val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(
                        original[i]
                    )
                    if (topThreeVenueType.isEmpty()) {
                        venueType = getString(R.string.txt_not_available)
                    } else {
                        for (venueTypeString in topThreeVenueType) {
                            venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
                        }
                    }
                    if (venueType.lowercase(Locale.getDefault()).contains(Constant().pub)) {
                        venues.add(original[i])
                    }
                }
            }

            FILTER_MAP.CAFE -> {
                venues.clear()
                for (i in original.indices) {
                    var venueType = ""
                    val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(
                        original[i]
                    )
                    if (topThreeVenueType.isEmpty()) {
                        venueType = getString(R.string.txt_not_available)
                    } else {
                        for (venueTypeString in topThreeVenueType) {
                            venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
                        }
                    }
                    if (venueType.lowercase(Locale.getDefault()).contains(Constant().cafe)) {
                        venues.add(original[i])
                    }
                }
            }

            FILTER_MAP.OFFER -> {
                venues.clear()
                for (i in original.indices) {
                    if (original[i].offerslive!!.isNotEmpty()) {
                        venues.add(original[i])
                    }
                }
            }

            else -> {
                venues.addAll(original)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMarkerClick(marker: Marker): Boolean {

        mapListAdapter = MapListAdapter(mapList, listener)

        binding.includeBottomSheet.rvMap.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        binding.includeBottomSheet.rvMap.adapter = mapListAdapter

        mapList.clear()
        for (i in 0 until markerDetails.size) {
            if (marker == markerDetails[i].marker) {
                val markerId = markerDetails[i].id
                for (j in 0 until venues.size) {
                    if (markerId == venues[j].id) {
                        mapList.add(venues[j])
                        mapListAdapter.notifyDataSetChanged()
                        break
                    }
                }
            }
        }

        setMapListData()

        return venue != null
    }

    var listener: MapVenueListener<Venue> = object : MapVenueListener<Venue>() {
        override fun onInfoDirectionClick(data: Venue) {
            super.onInfoDirectionClick(data)
            // Direction Dialog
            val directionDialog = DirectionsDialog(
                mActivity!!,
                LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude),
                LatLng(data.venue_latitude!!.toDouble(), data.venue_longitude!!.toDouble())
            )
            directionDialog.show()
        }

        override fun onInfoFavoriteClick(data: Venue, status: Boolean) {
            super.onInfoFavoriteClick(data, status)
            if (status) {
                setFavoriteData(data)
                callVenueFavourite(data, 1)
            } else {
                setFavoriteData(data)
                callVenueFavourite(data, 0)
            }

//            refreshMarker()
        }

        override fun onInfoShareClick(item: Venue, position: Int) {
            super.onInfoShareClick(item, position)

            if(item.venue_share_link!=null && item.venue_share_link!!.isNotEmpty()){
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

        override fun onInfoBookNowClick(data: Venue, position: Int) {
            super.onInfoBookNowClick(data, position)
            setFragmentWithStack(
                VenueBookFragment.newInstance(data),
                VenueBookFragment::class.java.simpleName
            )
        }

        override fun onInfoItemClick(data: Venue, position: Int) {
            super.onInfoItemClick(data, position)
            setFragmentWithStack(ExploreDetailsFragment.newInstance(data),
                ExploreDetailsFragment::class.java.simpleName)
        }
    }

    private fun openNoLocationDialog() {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogEmptyLocationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_empty_location, null, false
        )

        binding.tvError.text = getString(R.string.txt_oops)
        binding.tvErrorDesc.text = getString(R.string.txt_no_venues_display)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnRetry.setOnClickListener {
            dialog.dismiss()
          //  hitAPIForVenuesList(true, apiLatitude, apiLongitude, true)
        }


        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    fun searchVenues(query: String, userLocation: LatLng, callback: (List<Venue>) -> Unit) {
        val nameArr = mutableListOf<Venue>()

        val queryObj = Query(query)
        queryObj.aroundLatLng = Point(userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat())
        queryObj.aroundRadius = AroundRadius.InMeters(1680 * PrefManager.config!!.mapScreenRadius)
        nameArr.clear()
        scope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    index.search(queryObj)
                }

                results.hits.forEach { hit ->
                    val jsonObject = hit.json
                    println("AlgoliaHelper : jsonObject$jsonObject")
                    val name = jsonObject[Constant().venueName]
                    val id = jsonObject[Constant().idConst]

                    for (j in original.indices) {
                        if (name.toString().lowercase(Locale.getDefault())
                                .contains(original[j].venue_name!!.lowercase(Locale.getDefault()))
                        ) {
                            nameArr.add(original[j])
                            if (venuesSearch.size > 40) {
                                break
                            }
                        }
                    }

                    Log.d("AlgoliaHelper", "$name --- $id")
                }
                callback(nameArr)
            } catch (e: Exception) {
                Log.e("AlgoliaHelper", "Error: ${e.message}")
                callback(emptyList()) // or handle error case as needed
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
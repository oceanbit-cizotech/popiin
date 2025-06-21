package com.popiin.fragment
import android.annotation.SuppressLint
import android.text.Editable
import android.view.View
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.databinding.FragmentVenueSearchBinding
import  android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.client.Index
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.AroundRadius
import com.algolia.search.model.search.Point
import com.algolia.search.model.search.Query
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.popiin.activity.ExploreDetailsFragment
import com.popiin.adapter.VenueSearchAdapter
import com.popiin.util.PrefManager
import com.popiin.annotation.FILTER_VENUE
import com.popiin.dialog.DirectionsDialog
import com.popiin.listener.FavoriteListener
import com.popiin.listener.VenueSearchItemClickListener
import com.popiin.model.VenueModel
import com.popiin.req.VenueDetailsReq
import com.popiin.res.VenueDetailsRes
import com.popiin.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.*


class VenueSearchFragment : BaseFragment<FragmentVenueSearchBinding>() {
    private lateinit var venueSearchAdapter: VenueSearchAdapter
    private var searchText:String? =null
    lateinit var index: Index
    private val searchList: ArrayList<VenueModel> = ArrayList()

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun getLayout(): Int {
        return R.layout.fragment_venue_search
    }

    companion object {
        fun newInstance(): VenueSearchFragment {
            return VenueSearchFragment()
        }
    }

    override fun initViews() {

        binding.tvNoVenueData.visibility=View.VISIBLE
        binding.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        index = PrefManager.instance!!.algoliaClient.initIndex(IndexName(Constant().venues))
        val linearLayoutManager = LinearLayoutManager(getBaseActivity())
        linearLayoutManager.isAutoMeasureEnabled
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvSearch.layoutManager = linearLayoutManager
        venueSearchAdapter = VenueSearchAdapter(searchList, venueSearchListener)
        binding.rvSearch.adapter = venueSearchAdapter

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            //    val latitude = 51.3913458
            //    val longitude = -0.100036
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchText = charSequence.toString()
                if (charSequence.toString().isNotEmpty() && charSequence.toString().length>2) {
                    PrefManager.setSelected(FILTER_VENUE.NONE)
                    binding.ivCancel.visibility = View.VISIBLE
                    searchVenues(
                        charSequence.toString(),
                     //   LatLng(51.3913458,-0.100036)
                       LatLng(
                            PrefManager.lastLocation!!.latitude,
                            PrefManager.lastLocation!!.longitude
                        )
                    ) { venues ->
                        requireActivity().runOnUiThread {
                            val venueWithDistance = venues.mapNotNull { venue ->
                                try {
                                    val lat = venue.venue_latitude?.toDoubleOrNull()
                                    val lng = venue.venue_longitude?.toDoubleOrNull()
                                    if (lat != null && lng != null && PrefManager.lastLocation != null) {
                                        val distance = calculateDistanceInMiles(
                                            PrefManager.lastLocation!!.latitude,
                                            PrefManager.lastLocation!!.longitude,
                                            lat, lng
                                        )
                                        venue.venue_distance = distance
                                        venue // return the updated venue
                                    } else null
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            val sortedVenues = venueWithDistance.sortedBy { it.venue_distance }
                            searchList.clear()
                            searchList.addAll(sortedVenues)
                            venueSearchAdapter.notifyDataSetChanged()
                            if(sortedVenues.isEmpty()){
                                binding.tvNoVenueData.visibility=View.VISIBLE
                            }else{
                                binding.tvNoVenueData.visibility=View.GONE
                            }
                        }
                    }

                } else {
                    if(searchList.size>0){
                        binding.rvSearch.visibility = View.VISIBLE
                        binding.ivCancel.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.ivCancel.setOnClickListener {
            binding.edtSearch.clearFocus()
            binding.ivCancel.visibility = View.GONE
            binding.edtSearch.setText("")
            searchList.clear()
            venueSearchAdapter.notifyDataSetChanged()
            hideKeyBoard(requireActivity())
            binding.tvNoVenueData.visibility=View.VISIBLE
        }
    }

    fun searchVenues(query: String, userLocation: LatLng, callback: (List<VenueModel>) -> Unit) {
        val venues = mutableListOf<VenueModel>()

        val queryObj = Query(query).apply {
            aroundLatLng =Point(
                userLocation.latitude.toFloat(),
                userLocation.longitude.toFloat()
            )
            aroundRadius = AroundRadius.InMeters(1608 * 40) // 50 miles
            hitsPerPage = 100 // Fetch up to 100 results
        }

        scope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    index.search(queryObj)
                }
                venues.clear()
                results.hits.forEach { hit ->
                    val jsonObject: JSONObject = convertGsonToJson(hit.json)
                    val venue: VenueModel = Gson().fromJson(jsonObject.toString(), VenueModel::class.java)
                    venues.add(venue)
                }
                callback(venues)

            } catch (e: Exception) {
                callback(emptyList()) // or handle error case as needed
            }
        }
    }

    /*
       fun searchVenues(query: String, userLocation: LatLng, callback: (List<VenueModel>) -> Unit) {
            val venues = mutableListOf<VenueModel>()

            val queryObj = Query(query)
          */
/*  queryObj.aroundLatLng = Point(
            userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat()
        ) *//*

        queryObj.aroundLatLng = Point(
            51.3913458f,
            -0.100036f,
        )
        queryObj.aroundRadius = AroundRadius.InMeters((1608 * 40)) // 50 miles
        scope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    index.search(queryObj)
                }
                venues.clear()
                results.hits.forEach { hit ->
                    val jsonObject: JSONObject =convertGsonToJson(hit.json)
                    val venue: VenueModel = Gson().fromJson(jsonObject.toString(), VenueModel::class.java)
                        venues.add(venue)
                }
                callback(venues)

            } catch (e: Exception) {
                callback(emptyList()) // or handle error case as needed
            }
        }

    }
*/

/*
    fun searchVenues(query: String, userLocation: LatLng, callback: (List<VenueModel>) -> Unit) {
        val venues = mutableListOf<VenueModel>()

        val queryObj = Query(query).apply {
            aroundLatLng = Point(51.3913458f, -0.100036f) // Use static or userLocation if needed
            aroundRadius = AroundRadius.InMeters((1608 * 40)) // 50 miles
            hitsPerPage = 1000 // Max allowed by Algolia
        }

        scope.launch {
            try {
                var page = 0
                var totalFetched = 0
                val allVenues = mutableListOf<VenueModel>()

                while (true) {
                    queryObj.page = page

                    val results = withContext(Dispatchers.IO) {
                        index.search(queryObj)
                    }

                    if (results.hits.isEmpty()) break

                    results.hits.forEach { hit ->
                        val jsonObject: JSONObject = convertGsonToJson(hit.json)
                        val venue: VenueModel = Gson().fromJson(jsonObject.toString(), VenueModel::class.java)
                        allVenues.add(venue)
                    }

                    totalFetched += results.hits.size
                    if (totalFetched >= results.nbHits) break

                    page++
                }

                callback(allVenues)

            } catch (e: Exception) {
                callback(emptyList()) // or log the error
            }
        }
    }
*/

    fun calculateDistanceInMiles(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 3958.8 // Radius in miles

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
    private fun convertGsonToJson(jsonObject: JsonObject): JSONObject {
        return JSONObject(jsonObject.toString())
    }
    private var venueSearchListener: VenueSearchItemClickListener<VenueModel> =
        object : VenueSearchItemClickListener<VenueModel>() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(venue: VenueModel?) {
                super.onItemClick(venue)
                callToGetVenueDetail(venueId = ""+venue!!.id)
                hideKeyBoard(requireActivity())
                binding.edtSearch.setText("")
                venueSearchAdapter.notifyDataSetChanged()
            }

            override fun onDirections(venue: VenueModel?) {
                super.onDirections(venue)
                val directionDialog = DirectionsDialog(
                    mActivity!!,
                    LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude),
                    LatLng(venue!!.venue_latitude!!.toDouble(), venue.venue_longitude!!.toDouble())
                )
                directionDialog.show()
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


}
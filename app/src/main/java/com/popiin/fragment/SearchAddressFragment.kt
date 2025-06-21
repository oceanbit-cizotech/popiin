package com.popiin.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.APIClientMap
import com.popiin.ApiInterface
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.SearchAddressAdapter
import com.popiin.databinding.FragmentSearchAddressBinding
import com.popiin.listener.AdapterEventListener
import com.popiin.listener.AddressSelectionListener
import com.popiin.res.AddressSearchRes
import com.popiin.res.SearchAddressDetailRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchAddressFragment : BaseFragment<FragmentSearchAddressBinding>() {
    var location = ""
    var addressListener: AddressSelectionListener? = null
    private lateinit var searchAddressAdapter: SearchAddressAdapter
    private var searchAddressList = ArrayList<AddressSearchRes.Result?>()
    override fun getLayout(): Int {
        return R.layout.fragment_search_address
    }

    companion object {
        var type = -1
        fun newInstance(i: Int): SearchAddressFragment {
            type = i
            return SearchAddressFragment()
        }
    }

    override fun initViews() {

        binding.edtSearchAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.rvAddressSearch.visibility = View.VISIBLE
                    location =
                        "" + PrefManager.lastLocation!!.latitude + "," + PrefManager.lastLocation!!.longitude
                    callAddressSearchApi(s.toString())
                } else {
                    hideKeyBoard(requireActivity())
                    binding.rvAddressSearch.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable) {
                // No action needed
            }
        })

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStackImmediate()
        }

        searchAddressAdapter = SearchAddressAdapter(searchAddressList, listener)
        binding.rvAddressSearch.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvAddressSearch.adapter = searchAddressAdapter
    }

    private fun callAddressSearchApi(searchText: String) {
        if (isInternetConnect()) {
            val apiInterface: ApiInterface =
                APIClientMap().placeSearchClient!!.create(ApiInterface::class.java)
            val call: Call<AddressSearchRes?>? = apiInterface.doAddressSearch(
                searchText,
                location,
                getString(R.string.google_maps_key)
            )
            call!!.enqueue(object : Callback<AddressSearchRes?> {
                override fun onResponse(
                    call: Call<AddressSearchRes?>,
                    response: Response<AddressSearchRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        searchAddressList.clear()
                        searchAddressList.addAll(response.body()!!.results!!)
                        searchAddressAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<AddressSearchRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }

    var listener: AdapterEventListener<AddressSearchRes.Result?> =
        object : AdapterEventListener<AddressSearchRes.Result?>() {
            override fun onEventAddressSearch(item: AddressSearchRes.Result?, position: Int) {
                super.onEventAddressSearch(item, position)
                callAddressDetailApi(item!!.placeId!!)
            }
        }

    var lat: String? = null
    var lng: String? = null
    var street_number: String? = null
    var route: String? = null
    var locality: String = ""
    var sublocality_level_1: String? = null
    var sublocality_level_2: String? = null
    var postal_code: String? = null
    var customerAddress: String = ""
    var city: String = ""


    fun callAddressDetailApi(placeId: String) {
        showProgress()
        if (isInternetConnect()) {
            val apiInterface: ApiInterface =
                APIClientMap().placeDetailsClient!!.create(ApiInterface::class.java)
            val call: Call<SearchAddressDetailRes?>? =
                apiInterface.doAddressDetails(placeId, getString(R.string.google_maps_key))
            call!!.enqueue(object : Callback<SearchAddressDetailRes?> {
                override fun onResponse(
                    call: Call<SearchAddressDetailRes?>,
                    response: Response<SearchAddressDetailRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (type == 0) {
                            common.createEventModel.address =
                                response.body()!!.result!!.formattedAddress!!
                        } else {
                            //  addressListener?.onAddressSelected(response.body()!!.result!!.formattedAddress!!)

                            if (response.body()?.result?.geometry?.location != null) {
                                lat =
                                    response.body()!!.result!!.geometry!!.location!!.lat.toString()
                                lng =
                                    response.body()!!.result!!.geometry!!.location!!.lng.toString()
                            }

                            if (lat?.isEmpty() == true && response.body()?.result?.geometry?.viewport?.northeast != null) {
                                lat =
                                    response.body()!!.result?.geometry?.viewport?.northeast?.lat.toString()
                                lng =
                                    response.body()!!.result?.geometry?.viewport?.northeast?.lng.toString()
                            }

                            if (lat?.isEmpty() == true && response.body()?.result?.geometry?.viewport?.southwest != null) {
                                lat =
                                    response.body()!!.result?.geometry?.viewport?.southwest?.lat.toString()
                                lng =
                                    response.body()!!.result?.geometry?.viewport?.southwest?.lng.toString()
                            }

                            if (response.body()?.result?.addressComponents != null) {
                                var addressComponents = response.body()?.result?.addressComponents
                                if (addressComponents != null) {
                                    for (address in addressComponents) {
                                        for (type in address?.types!!) {
                                            if (type.equals("street_number")) {
                                                address.longName.let {
                                                    street_number = address.longName
                                                }
                                            } else if (type.equals("route")) {
                                                address.longName.let {
                                                    route = address.longName
                                                }
                                            } else if (type.equals("locality")) {
                                                    locality = address.longName!!

                                            } else if (type.equals("postal_town")) {
                                                city = address.longName.toString()

                                            } else if (type.equals("postal_code")) {
                                                address.longName.let {
                                                    postal_code = address.longName
                                                }
                                            } else if (type.equals("sublocality_level_2")) {
                                                address.longName.let {
                                                    sublocality_level_2 = address.longName
                                                }
                                            } else if (type.equals("sublocality_level_1")) {
                                                address.longName.let {
                                                    sublocality_level_1 = address.longName
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            val formatedAddress=response.body()?.result?.formattedAddress
                            val add = arrayOf(
                                sublocality_level_2,
                                sublocality_level_1,
                                street_number,
                                route
                            )

                            for (ads in add) {
                                if(ads!=null) {
                                    if (customerAddress.isEmpty() == true) {
                                        customerAddress = customerAddress + ads
                                    } else {
                                        customerAddress = customerAddress + ", " + ads
                                    }
                                }
                            }

                            println(lat)
                            println(lng)
                            println(customerAddress)
                            println(locality)
                            println(city)
                            println(postal_code)

                            if (locality.isEmpty()) {
                                locality = city
                            }
                            val comArr=formatedAddress?.split(locality)

                            if(comArr?.size!! >= 2){
                                customerAddress= comArr[0]
                            }

                            if(type ==1) {
                                common.registerVenueModel.address = customerAddress
                                common.registerVenueModel.city = locality.toString()
                                common.registerVenueModel.postal_code = postal_code.toString()
                                common.registerVenueModel.latitude = lat.toString()
                                common.registerVenueModel.longitude = lng.toString()
                            }else{
                                common.createEventModel.address = customerAddress
                                common.createEventModel.city = locality.toString()
                                common.createEventModel.postCode = postal_code.toString()
                                common.createEventModel.latitude = lat.toString()
                                common.createEventModel.longitude = lng.toString()
                            }

                            addressListener?.onAddressSelected(customerAddress)
                            if(type == 1) {
                                common.registerVenueModel.address =
                                    response.body()!!.result!!.formattedAddress!!
                            }else{
                                common.createEventModel.address =
                                    response.body()!!.result!!.formattedAddress!!
                            }
                        }

                        mActivity!!.supportFragmentManager.popBackStack()
                    }
                }

                override fun onFailure(call: Call<SearchAddressDetailRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }
}
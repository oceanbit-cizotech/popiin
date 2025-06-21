package com.popiin.activity

import android.annotation.SuppressLint
import android.view.View
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.databinding.FragmentBusinessBinding
import com.popiin.fragment.AccountManagementFragment
import com.popiin.req.EventReq
import com.popiin.req.VenuesReq
import com.popiin.res.EventListRes
import com.popiin.res.VenueListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusinessFragment : BaseFragment<FragmentBusinessBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_business
    }

    companion object {
        fun newInstance(): BusinessFragment {
            return BusinessFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        if (PrefManager.getHasEvent() == 0 && PrefManager.getHasVenue() == 0) {
            binding.inclAccountManagement.root.visibility = View.GONE
        }

        binding.inclRegisterVenue.root.setOnClickListener {
            common.refreshRegisterVenueModel()
            setFragmentAdd(
                RegisterVenueFragment.newInstance(null, false),
                RegisterVenueFragment::class.java.simpleName
            )
        }

        binding.inclCreateEvent.root.setOnClickListener {
            common.refreshCreateEventModel()
            setFragmentAdd(
                CreateEventFragment.newInstance( false),
                CreateEventFragment::class.java.simpleName
            )
        }

        binding.inclAccountManagement.root.setOnClickListener {
            setFragmentAdd(
                AccountManagementFragment.newInstance(),
                AccountManagementFragment::class.java.simpleName
            )
        }

        if (PrefManager.getHasVenue() == 0 && PrefManager.getHasEvent() == 0) {
            getEventData()
        } else {
            binding.inclAccountManagement.root.visibility = View.VISIBLE
        }
    }

    private fun getEventData() {
            showProgress()
        if (!isInternetConnect()) {
            return
        }
        val call: Call<EventListRes?>? = apiInterface.doGetEventList(PrefManager.getAccessToken(),
            EventReq(pageLimit, "", 0))

        call!!.enqueue(object : Callback<EventListRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<EventListRes?>, response: Response<EventListRes?>) {
                if (isValidResponse(response)) {
                    if (response.body()!!.getSuccess()) {
                        if (response.body()!!.getEvents().size > 0) {
                            hideProgress()
                            binding.inclAccountManagement.root.visibility = View.VISIBLE
                        } else {
                            getVenueData(
                                PrefManager.lastLocation!!.latitude,
                                PrefManager.lastLocation!!.longitude
                            )
                        }
                        PrefManager.setHasEvent(response.body()!!.getEvents().size)
                    }else{
                        hideProgress()
                    }
                }

            }

            override fun onFailure(call: Call<EventListRes?>, t: Throwable) {
                onApiFailure(throwable = t )
            }

        })
    }

    private fun getVenueData( lat: Double, lng: Double) {
        if (!isInternetConnect()) {
            return
        }
            showProgress()

            val call: Call<VenueListRes?>? =
                apiInterface.getVenueList(
                    VenuesReq(
                        "", "" + lng, "" + lat,
                        PrefManager.config!!.exploreScreenRadius,null
                    )
                )
            call!!.enqueue(object : Callback<VenueListRes?> {
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (response.body()!!.venues.size > 0) {
                                binding.inclAccountManagement.root.visibility = View.VISIBLE
                            } else {
                                binding.inclAccountManagement.root.visibility = View.GONE
                            }

                            PrefManager.setHasVenue(response.body()!!.venues.size)
                        }

                    }

                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t )
                }
            })

    }


}
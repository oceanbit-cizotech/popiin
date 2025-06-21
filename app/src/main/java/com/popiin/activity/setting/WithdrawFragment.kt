package com.popiin.activity.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import carbon.widget.RecyclerView.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.EventEarningAdapter
import com.popiin.adapter.VenueEarningAdapter
import com.popiin.databinding.FragmentWithdrawBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.AdapterWithdrawListener
import com.popiin.req.EarningsReq
import com.popiin.req.PaidReq
import com.popiin.req.PaidVenueReq
import com.popiin.res.CommonRes
import com.popiin.res.CreateStripeLinkRes
import com.popiin.res.Earning
import com.popiin.res.EarningRes
import com.popiin.res.VenuePaidRes
import com.popiin.res.WhatsonEarningRes
import com.popiin.util.PrefManager
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WithdrawFragment : BaseFragment<FragmentWithdrawBinding>() {
    var pageNo = defaultPage
    var pageNoVenue = defaultPage
    var isNextFragment = false
    var isLoading = false
    var layoutManager: LinearLayoutManager? = null
    var eventEarningAdapter: EventEarningAdapter? = null
    var venueEarningAdapter: VenueEarningAdapter? = null
    var earnings: ArrayList<Earning>? = ArrayList()
    var venueEarnings: ArrayList<WhatsonEarningRes.Data>? = ArrayList()
    var selectedIndex = 0

    override fun getLayout(): Int {
        return R.layout.fragment_withdraw
    }

    companion object {
        fun newInstance(): WithdrawFragment {
            return WithdrawFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: View = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.height = 80
            layoutParams.setMargins(0, 0, 24, 0)
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isLoading = true
                if (tab.position == 0) {
                    selectedIndex = 0
                    binding.rvMyEarning.adapter = eventEarningAdapter
                    showProgress()
                    pageNo = defaultPage
                    callGetEventEarning(true, pageNo)
                } else {
                    selectedIndex = 1
                    binding.rvMyEarning.adapter = venueEarningAdapter
                    if (isNextFragment) {
                        isNextFragment = false
                    } else {
                        showProgress()
                        pageNoVenue = defaultPage
                        callGetVenueEarning(true, pageNoVenue)

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedIndex))
        if (selectedIndex == 0) {
            binding.rvMyEarning.adapter = eventEarningAdapter
            if (earnings?.size == 0) {
                showProgress()
                pageNo = defaultPage
                callGetEventEarning(true, pageNo)
            }
        } else {
            binding.rvMyEarning.adapter = venueEarningAdapter
            if (venueEarnings?.size == 0) {
                showProgress()
                pageNoVenue = defaultPage
                callGetVenueEarning(true, pageNoVenue)
            }
        }

        binding.inclMessage.tvReload.setOnClickListener {
            if (selectedIndex == 0) {
                showProgress()
                callGetEventEarning(true, pageNo)
            } else {
                showProgress()
                callGetVenueEarning(true, pageNoVenue)
            }
        }

        layoutManager = LinearLayoutManager(mActivity)
        eventEarningAdapter = EventEarningAdapter(earning = earnings, adapterWithdrawListener)
        venueEarningAdapter = VenueEarningAdapter(earning = venueEarnings)
        binding.rvMyEarning.layoutManager = layoutManager
        binding.rvMyEarning.adapter = eventEarningAdapter
        callGetEventEarning(true, 0)
        // callGetVenueEarning()
    }

    private fun callGetEventEarning(
        @Suppress("SameParameterValue") isLoaderDisplay: Boolean,
        pageNo: Int,
    ) {
        if (isInternetConnect()) {
            showProgress()
            apiInterface.doGetEarnings(
                PrefManager.getAccessToken(),
                EarningsReq(pageLimit, pageNo)
            ).enqueue(object : Callback<EarningRes> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<EarningRes>, response: Response<EarningRes>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (this@WithdrawFragment.pageNo == defaultPage) {
                            earnings?.clear()
                        }
                        if (response.body()!!.success) {
                            earnings?.clear()
                            response.body()?.earning?.let { earnings?.addAll(it) }
                            eventEarningAdapter!!.notifyDataSetChanged()

                            isLoading = true
                            this@WithdrawFragment.pageNo++

                        } else {
                            isLoading = false
                        }

                        if (earnings!!.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text =
                                getString(R.string.txt_no_information_display)
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                        }

                        if (isLoaderDisplay) {
                            hideProgress()
                        }
                    }
                }

                override fun onFailure(call: Call<EarningRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun callGetVenueEarning(
        @Suppress("SameParameterValue") isLoaderDisplay: Boolean,
        pageNo: Int,
    ) {

        if (isInternetConnect()) {
            showProgress()
            apiInterface.doGetWhatsonEarnings(
                PrefManager.getAccessToken(),
                EarningsReq(pageLimit, pageNo)
            ).enqueue(object : Callback<WhatsonEarningRes> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<WhatsonEarningRes>, response: Response<WhatsonEarningRes>) {
                    hideProgress()

                    if (isValidResponse(response)) {
                        if (this@WithdrawFragment.pageNo == defaultPage) {
                            venueEarnings?.clear()
                        }

                        if (response.body()!!.success!!) {
                            venueEarnings?.clear()
                            response.body()?.data?.forEach { item ->
                                if (item != null) {
                                    venueEarnings?.add(item)
                                }
                                // Process each 'item' here
                            }
                            isLoading = true
                            this@WithdrawFragment.pageNo++
                        } else {
                            isLoading = false
                        }

                        venueEarningAdapter!!.notifyDataSetChanged()


                        if (venueEarnings!!.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text =
                                getString(R.string.txt_no_information_display)
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                        }

                        if (isLoaderDisplay) {
                            hideProgress()
                        }
                    }
                }

                override fun onFailure(call: Call<WhatsonEarningRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private var adapterWithdrawListener: AdapterWithdrawListener<Earning> =
        object : AdapterWithdrawListener<Earning>() {
            override fun onWithdrawEventClick(item: Earning, position: Int) {
                super.onWithdrawEventClick(item, position)
                if (PrefManager.getUser().user!!.is_stripe_verified==0) {
                    val commonDialog = CommonDialog(
                        requireActivity(),
                        getString(R.string.lbl_ok),
                        "",
                        getString(R.string.txt_add_payment_details),
                        getString(R.string.withdraw_payment_not_added_message)

                    )
                    commonDialog.binding.btnPositive.setOnClickListener {
                        commonDialog.dismiss()
                        getCreateStripeLink(true)

                    }
                    commonDialog.show()
                } else {
                    callGetEventPaid(item)
                }
            }

            override fun onWithdrawVenueClick(item: Earning, position: Int) {
                super.onWithdrawVenueClick(item, position)
                if (PrefManager.getUser().user!!.is_stripe_verified==0) {
                    val commonDialog = CommonDialog(
                        requireActivity(),
                        getString(R.string.lbl_ok),
                        "",
                        getString(R.string.txt_add_payment_details),
                        getString(R.string.withdraw_payment_not_added_message)

                    )
                    commonDialog.binding.btnPositive.setOnClickListener {
                        commonDialog.dismiss()
                        getCreateStripeLink(true)
                       /* setFragmentAdd(
                            AddPaymentMethodFragment.newInstance(),
                            AddPaymentMethodFragment::class.java.simpleName
                        )*/
                    }
                    commonDialog.show()
                } else {
                    callGetVenuePaid(item)
                }
            }

        }


    private fun callGetVenuePaid(item: Earning) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doGetVenuePaid(
                PrefManager.getAccessToken(), PaidVenueReq(
                    item.unpaidAmount, item.id,
                    PrefManager.getUser().user!!.stripe_customer_id!!
                )
            )
            call!!.enqueue(object : Callback<VenuePaidRes?> {
                override fun onResponse(
                    call: Call<VenuePaidRes?>,
                    response: Response<VenuePaidRes?>
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            common.openDialog(requireActivity(), response.body()!!.message)
                            venueEarnings!!.clear()
                            callGetVenueEarning(true, pageNoVenue)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.message)
                        }
                    } else {
                        common.openDialog(requireActivity(), response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<VenuePaidRes?>, t: Throwable) {
                    hideProgress()
                    t.printStackTrace()
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    private fun callGetEventPaid(item: Earning) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doGetPaid(
                PrefManager.getAccessToken(), PaidReq(
                    "" + item.unpaidAmount,
                    "" + item.id
                )
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            common.openDialog(requireActivity(), response.body()!!.msg)
                            earnings!!.clear()
                            callGetEventEarning(true, pageNo)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.msg)
                        }
                    } else {
                        common.openDialog(requireActivity(), response.body()!!.msg)
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }
    private fun getCreateStripeLink(isLoaderDisplay: Boolean) {
        if (isLoaderDisplay) {
            showProgress()
        }
        if (!isInternetConnect()) {
            return
        }

        val call: Call<CreateStripeLinkRes?>? = apiInterface.createStripeLink(
            PrefManager.getAccessToken(),
        )
        call!!.enqueue(object : Callback<CreateStripeLinkRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CreateStripeLinkRes?>,
                response: Response<CreateStripeLinkRes?>,
            ) {
                if (isValidResponse(response)) {
                    if (response.body()?.success == true) {
                        var url = response.body()!!.data.url
                        val i = Intent(Intent.ACTION_VIEW)
                        i.setData(Uri.parse(url))
                        startActivity(i)
                        hideProgress()

                    }else{
                        hideProgress()
                    }
                }

            }

            override fun onFailure(call: Call<CreateStripeLinkRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

}
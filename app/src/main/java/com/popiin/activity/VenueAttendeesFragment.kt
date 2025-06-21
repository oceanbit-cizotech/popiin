package com.popiin.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.VenueAttendeesAdapter
import com.popiin.databinding.FragmentVenueAttendeesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.EventReq
import com.popiin.res.VenueListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VenueAttendeesFragment : BaseFragment<FragmentVenueAttendeesBinding>() {
    var venueAttendeeList: ArrayList<VenueListRes.Venue> = ArrayList()
    lateinit var venueAttendeeAdapter: VenueAttendeesAdapter
    var pageNo = defaultPage
    var strSearch = ""
    override fun getLayout(): Int {
        return R.layout.fragment_venue_attendees
    }

    companion object {
        fun newInstance(): VenueAttendeesFragment {
            return VenueAttendeesFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        if (venueAttendeeList.size > 0) {
            venueAttendeeList.clear()
        }

        venueAttendeeAdapter = VenueAttendeesAdapter(venueAttendeeList, listener)
        binding.rvMyVenues.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvMyVenues.adapter = venueAttendeeAdapter

        binding.inclMessage.tvReload.setOnClickListener {
            callVenueAttendeesListApi()
        }

        callVenueAttendeesListApi()

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.searchBar.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard(getBaseActivity())
            }
            true
        }

        binding.topHeader.ivSearch.setOnClickListener {
            if (binding.searchBar.root.isVisible) {
                binding.searchBar.root.visibility = View.GONE
                if (binding.searchBar.edtSearch.text.toString().isNotEmpty()) {
                    strSearch = ""
                    binding.searchBar.edtSearch.setText("")
                    callVenueAttendeesListApi()
                }
            } else {
                binding.searchBar.root.visibility = View.VISIBLE
            }
        }

        binding.searchBar.edtSearch.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                pageNo = defaultPage
                strSearch = searchText.toString()
                if (searchText!!.isNotEmpty()) {
                    binding.searchBar.ivCancel.visibility = View.VISIBLE
                } else {
                    Handler(Looper.myLooper()!!).postDelayed({ hideKeyBoard(getBaseActivity()) },
                        200)
                    binding.searchBar.ivCancel.visibility = View.GONE
                }
                callVenueAttendeesListApi()
            }
        })


        binding.searchBar.ivCancel.setOnClickListener {
            binding.searchBar.edtSearch.setText("")
            binding.searchBar.ivCancel.visibility = View.GONE
        }
    }

    var listener: AdapterItemClickListener<VenueListRes.Venue?> =
        object : AdapterItemClickListener<VenueListRes.Venue?>() {
            override fun onAdapterItemClick(item: VenueListRes.Venue?, position: Int) {
                super.onAdapterItemClick(item, position)
                setFragmentWithStack(
                    VenueBookingAttendeesFragment.newInstance(item!!.id),
                    VenueBookingAttendeesFragment::class.java.simpleName
                )
                mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }

    private fun callVenueAttendeesListApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueListRes?>? = apiInterface.doGetVenuesList(
                PrefManager.getAccessToken(),
                EventReq(pageLimit, strSearch, pageNo)
            )
            call!!.enqueue(object : Callback<VenueListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (pageNo == defaultPage) {
                            venueAttendeeList.clear()
                        }
                        if (response.body()!!.success) {
                            venueAttendeeList.clear()
                            venueAttendeeList.addAll(response.body()!!.venues)

                            if (venueAttendeeList.size == 0) {
                                binding.inclMessage.root.visibility = View.VISIBLE
                                binding.inclMessage.tvNoDataMessage.text =
                                    getString(R.string.txt_no_venues)
                            } else {
                                binding.inclMessage.root.visibility = View.GONE
                                binding.tvMessage.text = ""
                            }
                            venueAttendeeAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t )
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }
}
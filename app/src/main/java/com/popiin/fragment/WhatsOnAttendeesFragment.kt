package com.popiin.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.WhatsOnAttendeesAdapter
import com.popiin.databinding.FragmentWhatsonAttendeesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.EventReq
import com.popiin.res.VenueListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatsOnAttendeesFragment : BaseFragment<FragmentWhatsonAttendeesBinding>() {
    var whatsOnAttendeeList: ArrayList<VenueListRes.Venue> = ArrayList()
    lateinit var whatsOnAttendeeAdapter: WhatsOnAttendeesAdapter
    var pageNo = defaultPage
    var strSearch = ""
    override fun getLayout(): Int {
        return R.layout.fragment_whatson_attendees
    }

    companion object {
        fun newInstance(): WhatsOnAttendeesFragment {
            return WhatsOnAttendeesFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        if (whatsOnAttendeeList.size > 0) {
            whatsOnAttendeeList.clear()
        }

        whatsOnAttendeeAdapter = WhatsOnAttendeesAdapter(whatsOnAttendeeList, listener)
        binding.rvMyVenues.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvMyVenues.adapter = whatsOnAttendeeAdapter

        binding.inclMessage.tvReload.setOnClickListener {
            callVenueAttendeesListApi()
        }

        callVenueAttendeesListApi()

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.searchBar.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requireActivity().runOnUiThread { hideKeyBoard(getBaseActivity()) }
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
                    binding.searchBar.ivCancel.visibility = View.GONE
                    Handler(Looper.myLooper()!!).postDelayed({ hideKeyBoard(getBaseActivity()) },
                        200)
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
                    WhatsOnBookingAttendeesFragment.newInstance(item!!.id),
                    WhatsOnBookingAttendeesFragment::class.java.simpleName
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
                    response: Response<VenueListRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (pageNo == defaultPage) {
                                whatsOnAttendeeList.clear()
                            }
                            whatsOnAttendeeList.clear()
                            whatsOnAttendeeList.addAll(response.body()!!.venues)

                            if (whatsOnAttendeeList.size == 0) {
                                binding.inclMessage.root.visibility = View.VISIBLE
                                binding.inclMessage.tvNoDataMessage.text =
                                    getString(R.string.txt_no_venues)
                            } else {
                                binding.inclMessage.root.visibility = View.GONE
                                binding.tvMessage.text = ""
                            }

                            whatsOnAttendeeAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }
}
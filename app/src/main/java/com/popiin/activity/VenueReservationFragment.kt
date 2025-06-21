package com.popiin.activity

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.VenueReservationsAdapter
import com.popiin.databinding.FragmentVenueReservationsBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.EventReq
import com.popiin.req.VenueReserveReq
import com.popiin.res.VenueListRes
import com.popiin.res.VenueReserveRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VenueReservationFragment : BaseFragment<FragmentVenueReservationsBinding>() {
    var list: ArrayList<VenueListRes.Venue> = ArrayList()
    lateinit var venueReserveAdapter: VenueReservationsAdapter
    var pageNo = defaultPage
    var isLoading = true
    var strSearch = ""
    override fun getLayout(): Int {
        return R.layout.fragment_venue_reservations
    }

    companion object {
        fun newInstance(): VenueReservationFragment {
            return VenueReservationFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        if (list.size > 0) {
            list.clear()
        }

        venueReserveAdapter = VenueReservationsAdapter(list, listener)
        binding.rvMyVenues.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvMyVenues.adapter = venueReserveAdapter


        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.inclMessage.tvReload.setOnClickListener {
            callVenueListApi(true)
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
                    callVenueListApi(false)
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
                    hideKeyBoard(getBaseActivity())
                    binding.searchBar.ivCancel.visibility = View.GONE
                }
                callVenueListApi(false)
            }
        })


        binding.searchBar.ivCancel.setOnClickListener {
            binding.searchBar.edtSearch.setText("")
            binding.searchBar.ivCancel.visibility = View.GONE
        }
    }

    private fun callVenueListApi(isLoader: Boolean) {
        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }

            val call: Call<VenueListRes?>? =
                apiInterface.doGetVenuesList(
                    PrefManager.getAccessToken(),
                    EventReq(pageLimit, strSearch, pageNo)
                )
            call!!.enqueue(object : Callback<VenueListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (pageNo == defaultPage) {
                                list.clear()
                            }

                            list.addAll(response.body()!!.venues)
                            venueReserveAdapter.notifyDataSetChanged()
                        }

                        if (list.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                            showToast(response.body()!!.message)
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                        }
                    }

                    isLoading = true
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    var listener: AdapterItemClickListener<VenueListRes.Venue> =
        object : AdapterItemClickListener<VenueListRes.Venue>() {
            override fun onSwitchClick(item: VenueListRes.Venue, position: Int, status: Int) {
                super.onSwitchClick(item, position, status)
                if (isLoading) {
                    item.id.callVenueReserveApi(status)
                }
            }

            override fun onVenueReserveEditClick(item: VenueListRes.Venue, position: Int) {
                super.onVenueReserveEditClick(item, position)
                setFragmentWithStack(EditReserveFragment.newInstance(item),
                    EditReserveFragment::class.java.simpleName)
            }
        }


    private fun Int.callVenueReserveApi(isChecked: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueReserveRes?>? = apiInterface.doVenueReserve(
                PrefManager.getAccessToken(),
                VenueReserveReq(isChecked, this)
            )
            call!!.enqueue(object : Callback<VenueReserveRes?> {
                override fun onResponse(
                    call: Call<VenueReserveRes?>,
                    response: Response<VenueReserveRes?>,
                ) {
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueReserveRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        callVenueListApi(true)
    }

}
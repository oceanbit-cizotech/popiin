package com.popiin.activity

import android.annotation.SuppressLint
import android.nfc.tech.MifareUltralight
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.EventAttendeesAdapter
import com.popiin.databinding.FragmentEventAttendeesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.EventReq
import com.popiin.res.EventListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventAttendeesFragment : BaseFragment<FragmentEventAttendeesBinding>() {
    var myEvents = ArrayList<EventListRes.Event?>()
    var myEventsAdapter: EventAttendeesAdapter? = null
    var pageNo = defaultPage
    var strSearch = ""
    var layoutManager: LinearLayoutManager? = null
    var isLoading = true
    override fun getLayout(): Int {
        return R.layout.fragment_event_attendees
    }

    companion object {
        fun newInstance(): EventAttendeesFragment {
            return EventAttendeesFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        layoutManager = LinearLayoutManager(mActivity)
        binding.rvMyEvents.layoutManager = layoutManager
        myEventsAdapter = EventAttendeesAdapter(myEvents, listener)
        binding.rvMyEvents.adapter = myEventsAdapter
        binding.rvMyEvents.addOnScrollListener(recyclerViewOnScrollListener)
        callMyEventsApi(true)

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.inclMessage.tvReload.setOnClickListener {
            callMyEventsApi(true)
        }

        binding.topHeader.ivSearch.setOnClickListener {
            if (binding.searchBar.root.isVisible) {
                binding.searchBar.root.visibility = View.GONE
                if (binding.searchBar.edtSearch.text.toString().isNotEmpty()) {
                    strSearch = ""
                    binding.searchBar.edtSearch.setText("")
                    callMyEventsApi(false)
                }
            } else {
                binding.searchBar.root.visibility = View.VISIBLE
            }
        }

        binding.searchBar.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requireActivity().runOnUiThread { hideKeyBoard(getBaseActivity()) }
            }
            true
        }

        binding.searchBar.edtSearch.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                pageNo = defaultPage
                strSearch = searchText!!
                if (searchText.isNotEmpty()) {
                    binding.searchBar.ivCancel.visibility = View.VISIBLE
                } else {
                    binding.searchBar.ivCancel.visibility = View.GONE
                    Handler(Looper.myLooper()!!).postDelayed({ hideKeyBoard(getBaseActivity()) },
                        200)
                }
                callMyEventsApi(false)
            }

        })


        binding.searchBar.ivCancel.setOnClickListener {
            binding.searchBar.edtSearch.setText("")
            binding.searchBar.ivCancel.visibility = View.GONE

        }
    }

    var listener: AdapterItemClickListener<EventListRes.Event?> =
        object : AdapterItemClickListener<EventListRes.Event?>() {
            override fun onAdapterItemClick(item: EventListRes.Event?, position: Int) {
                super.onAdapterItemClick(item, position)
                setFragmentAdd(
                    EventBookingAttendeesFragment.newInstance((item!!.id).toString()),
                    EventBookingAttendeesFragment::class.java.simpleName
                )
              //  requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }

    private fun callMyEventsApi(isLoader: Boolean) {
        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }
            val call: Call<EventListRes?>? = apiInterface.doGetEventList(
                PrefManager.getAccessToken(),
                EventReq(pageLimit, strSearch, pageNo)
            )
            call!!.enqueue(object : Callback<EventListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<EventListRes?>,
                    response: Response<EventListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (pageNo == defaultPage) {
                            myEvents.clear()
                        }

                        if (response.body()!!.getSuccess()) {
                            myEvents.addAll(response.body()!!.getEvents())
                            isLoading = true
                        }else{
                            isLoading = false
                        }
                        pageNo++

                        if (myEvents.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.getMessage()
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                            binding.inclMessage.tvNoDataMessage.text = ""
                        }
                        myEventsAdapter!!.notifyDataSetChanged()
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EventListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                if(isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                        if (this@EventAttendeesFragment.isLoading) {
                            isLoading = false
                            if(isInternetConnect()) {
                                callMyEventsApi(false)
                            }
                        }
                    }
                }

            }
        }

}
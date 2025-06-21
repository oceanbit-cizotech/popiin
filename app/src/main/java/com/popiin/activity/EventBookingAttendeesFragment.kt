package com.popiin.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.EventBookAttendeesAdapter
import com.popiin.databinding.FragmentEventBookingAttendeesBinding
import com.popiin.fragment.BusinessScanCodeFragment
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.req.EventAttendesBusinessReq
import com.popiin.res.EventBookingAttendeesRes
import com.popiin.util.PrefManager
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventBookingAttendeesFragment : BaseFragment<FragmentEventBookingAttendeesBinding>() {
    var bookingsData = ArrayList<EventBookingAttendeesRes.Data>()
    var eventBookingAttendeesAdapter: EventBookAttendeesAdapter? = null

    override fun getLayout(): Int {
        return R.layout.fragment_event_booking_attendees
    }

    companion object {
        var eventId = ""
        fun newInstance(id: String): EventBookingAttendeesFragment {
            eventId = id
            return EventBookingAttendeesFragment()
        }
    }

    override fun initViews() {

        binding.rvEventBookAttendees.layoutManager = LinearLayoutManager(mActivity)
        eventBookingAttendeesAdapter =
            EventBookAttendeesAdapter(bookingsData, adapterEventBookingAttendeesListener)
        binding.rvEventBookAttendees.adapter = eventBookingAttendeesAdapter
        val headersDecor = StickyRecyclerHeadersDecoration(eventBookingAttendeesAdapter)
        binding.rvEventBookAttendees.addItemDecoration(headersDecor)

        binding.inclMessage.tvReload.setOnClickListener {
            callMyEvents()
        }

        eventBookingAttendeesAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                headersDecor.invalidateHeaders()
            }
        })

        binding.zsideview.setupWithRecycler(binding.rvEventBookAttendees)

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        callMyEvents()
    }

    private var adapterEventBookingAttendeesListener: AdapterEventBookingAttendeesListener<EventBookingAttendeesRes.Data> =
        object : AdapterEventBookingAttendeesListener<EventBookingAttendeesRes.Data>() {
            override fun onScanCode(item: EventBookingAttendeesRes.Data, position: Int) {
                super.onScanCode(item, position)
                if (item.is_scanned == 0) {
                    setFragmentAdd(
                        BusinessScanCodeFragment.newInstance(true),
                        BusinessScanCodeFragment::class.java.simpleName
                    )
                }
            }
        }

    private fun callMyEvents() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<EventBookingAttendeesRes?>? = apiInterface.getEventBookingAttendees(
                PrefManager.getAccessToken(),
                EventAttendesBusinessReq(eventId)
            )
            call!!.enqueue(object : Callback<EventBookingAttendeesRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<EventBookingAttendeesRes?>,
                    response: Response<EventBookingAttendeesRes?>
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (bookingsData.size > 0) {
                                bookingsData.clear()
                            }
                            bookingsData.addAll(response.body()!!.data!!)
                            eventBookingAttendeesAdapter!!.notifyDataSetChanged()
                        }
                        if (bookingsData.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                            binding.inclMessage.tvNoDataMessage.text = ""
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EventBookingAttendeesRes?>, t: Throwable) {
                    onApiFailure(throwable = t )
                }
            })
        }
    }
}
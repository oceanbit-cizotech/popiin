package com.popiin.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.VenueBookAttendeesAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentVenueBookingAttendeesBinding
import com.popiin.fragment.BusinessScanCodeFragment
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.ConfirmVenueBookReq
import com.popiin.res.ConfirmVenueBookRes
import com.popiin.res.VenueBookingAttendeeRes
import com.popiin.util.PrefManager
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VenueBookingAttendeesFragment : BaseFragment<FragmentVenueBookingAttendeesBinding>() {
    var bookingsData = ArrayList<VenueBookingAttendeeRes.Data>()
    var venueBookingAttendeesAdapter: VenueBookAttendeesAdapter? = null
    override fun getLayout(): Int {
        return R.layout.fragment_venue_booking_attendees
    }

    companion object {
        var venueId = 0
        fun newInstance(id: Int): VenueBookingAttendeesFragment {
            venueId = id
            return VenueBookingAttendeesFragment()
        }
    }

    override fun initViews() {


        binding.rvEventBookAttendees.layoutManager = LinearLayoutManager(mActivity)
        venueBookingAttendeesAdapter =
            VenueBookAttendeesAdapter(bookingsData, adapterVenueBookingAttendeesListener)
        binding.rvEventBookAttendees.adapter = venueBookingAttendeesAdapter
        val headersDecor = StickyRecyclerHeadersDecoration(venueBookingAttendeesAdapter)
        binding.rvEventBookAttendees.addItemDecoration(headersDecor)


        venueBookingAttendeesAdapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                headersDecor.invalidateHeaders()
            }
        })

        binding.inclMessage.tvReload.setOnClickListener {
            callVenueAttendeesApi(venueId, true)
        }

        binding.zsideview.setupWithRecycler(binding.rvEventBookAttendees)

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        callVenueAttendeesApi(venueId, true)
    }

    var isDecline = false
    var bookingId = ""
    private var adapterVenueBookingAttendeesListener: AdapterEventBookingAttendeesListener<VenueBookingAttendeeRes.Data> =
        object : AdapterEventBookingAttendeesListener<VenueBookingAttendeeRes.Data>() {
            override fun onScanCode(item: VenueBookingAttendeeRes.Data, position: Int) {
                super.onScanCode(item, position)
                if (item.is_scanned == 0) {
                    setFragmentAdd(
                        BusinessScanCodeFragment.newInstance(true),
                        BusinessScanCodeFragment::class.java.simpleName
                    )
                }
            }

            override fun onAcceptClick(item: VenueBookingAttendeeRes.Data, position: Int) {
                super.onAcceptClick(item, position)
                isDecline = false
                bookingId = "" + item.id
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_sure_delete),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_accept_booking),
                    getString(R.string.txt_no_cancel),
                    getString(R.string.txt_yes_accept),
                    infoDialogListener)
            }

            override fun onRejectClick(item: VenueBookingAttendeeRes.Data, position: Int) {
                super.onRejectClick(item, position)
                isDecline = true
                bookingId = "" + item.id
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_failed),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_decline_booking),
                    getString(R.string.txt_no_cancel),
                    getString(R.string.txt_yes_decline),
                    infoDialogListener)
            }
        }

    fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }

    var infoDialogListener: InfoDialogListener = object : InfoDialogListener() {
        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
            if (isDecline) {
                callConfirmVenueBookingApi(bookingId, 2)
            } else {
                callConfirmVenueBookingApi(bookingId, 1)
            }
        }

    }

    private fun callConfirmVenueBookingApi(bookingId: String, status: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<ConfirmVenueBookRes?>? =
                apiInterface.confirmVenueBooking(PrefManager.getAccessToken(),
                    ConfirmVenueBookReq(bookingId, status))
            call!!.enqueue(object : Callback<ConfirmVenueBookRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ConfirmVenueBookRes?>,
                    response: Response<ConfirmVenueBookRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            callVenueAttendeesApi(venueId, false)
                        }
                    }
                }

                override fun onFailure(call: Call<ConfirmVenueBookRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun callVenueAttendeesApi(venueId: Int, isLoad: Boolean) {
        if (isInternetConnect()) {
            if (isLoad) {
                showProgress()
            }
            val call: Call<VenueBookingAttendeeRes?>? =
                apiInterface.getVenueBookAttendeesList(PrefManager.getAccessToken(), venueId)
            call!!.enqueue(object : Callback<VenueBookingAttendeeRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueBookingAttendeeRes?>,
                    response: Response<VenueBookingAttendeeRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (response.body()!!.success) {
                                if (bookingsData.size > 0) {
                                    bookingsData.clear()
                                }
                                bookingsData.addAll(response.body()!!.data!!)
                                venueBookingAttendeesAdapter!!.notifyDataSetChanged()
                            }
                            if (bookingsData.size == 0) {
                                binding.inclMessage.root.visibility = View.VISIBLE
                                binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                            } else {
                                binding.inclMessage.root.visibility = View.GONE
                                binding.inclMessage.tvNoDataMessage.text = ""
                            }
                        } else {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                        }
                    }
                }

                override fun onFailure(call: Call<VenueBookingAttendeeRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }
}
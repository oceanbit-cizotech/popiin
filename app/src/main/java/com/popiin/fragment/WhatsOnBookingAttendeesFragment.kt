package com.popiin.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.VenueBookingAttendeesFragment
import com.popiin.adapter.WhatsOnBookAttendeesAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentWhatsonBookingAttendeesBinding
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.CancelBookingReq
import com.popiin.res.CancelBookingRes
import com.popiin.res.CommonRes
import com.popiin.res.VenueBookingAttendeeRes
import com.popiin.res.WhatsOnBookingAttendeesRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatsOnBookingAttendeesFragment : BaseFragment<FragmentWhatsonBookingAttendeesBinding>() {
    var whatsOnBookingsData = ArrayList<WhatsOnBookingAttendeesRes.Data>()
    var whatsOnBookingAttendeesAdapter: WhatsOnBookAttendeesAdapter? = null
    override fun getLayout(): Int {
        return R.layout.fragment_whatson_booking_attendees
    }

    companion object {
        var venue_id: Int = 0
        fun newInstance(id: Int): WhatsOnBookingAttendeesFragment {
            venue_id = id
            return WhatsOnBookingAttendeesFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rvEventBookAttendees.layoutManager = LinearLayoutManager(mActivity)
        whatsOnBookingAttendeesAdapter =
            WhatsOnBookAttendeesAdapter(whatsOnBookingsData, adapterWhatsOnBookingAttendeesListener)
        binding.rvEventBookAttendees.adapter = whatsOnBookingAttendeesAdapter
        val headersDecor = StickyRecyclerHeadersDecoration(whatsOnBookingAttendeesAdapter)
        binding.rvEventBookAttendees.addItemDecoration(headersDecor)


        whatsOnBookingAttendeesAdapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                headersDecor.invalidateHeaders()
            }
        })

        binding.inclMessage.tvReload.setOnClickListener {
            callWhatsOnAttendeesApi(VenueBookingAttendeesFragment.venueId, true)
        }

        binding.zsideview.setupWithRecycler(binding.rvEventBookAttendees)

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        callWhatsOnAttendeesApi(venue_id, true)
    }

    private var adapterWhatsOnBookingAttendeesListener: AdapterEventBookingAttendeesListener<WhatsOnBookingAttendeesRes.Data> =
        object : AdapterEventBookingAttendeesListener<WhatsOnBookingAttendeesRes.Data>() {
            override fun onScanCode(item: WhatsOnBookingAttendeesRes.Data, position: Int) {
                super.onScanCode(item, position)
                if (item.is_scanned == 0) {
                    setFragmentAdd(
                        BusinessScanCodeFragment.newInstance(true),
                        BusinessScanCodeFragment::class.java.simpleName
                    )
                }
            }

            override fun onCancel(item: WhatsOnBookingAttendeesRes.Data, position: Int) {
                super.onCancel(item, position)
                bookingId = "" + item.id
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_failed),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_cancel_booking),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_cancel), infoDialogListener)
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

    var bookingId = ""
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
            callCancelBookingApi(bookingId)
        }

    }

    var cancelListener: InfoDialogListener = object : InfoDialogListener() {
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
        }

    }

    private fun callCancelBookingApi(bookingId: String) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CancelBookingRes?>? =
                apiInterface.cancelBooking(PrefManager.getAccessToken(),
                    CancelBookingReq(Constant().owner, bookingId, 2))
            call!!.enqueue(object : Callback<CancelBookingRes?> {
                override fun onResponse(
                    call: Call<CancelBookingRes?>,
                    response: Response<CancelBookingRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                            R.drawable.ic_pass_success),
                            getString(R.string.txt_whats_on_booking_cancel),
                            response.body()!!.message!!, "",
                            getString(R.string.txt_done), cancelListener)
                        callWhatsOnAttendeesApi(venue_id, false)
                    } else {
                        if (response.body()!!.message != null) {
                            showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                                R.drawable.ic_failed),
                                getString(R.string.txt_failed),
                                response.body()!!.message!!, "",
                                getString(R.string.txt_done), cancelListener)
                        }

                    }
                }

                override fun onFailure(call: Call<CancelBookingRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }

    private fun callWhatsOnAttendeesApi(venueId: Int, isLoader: Boolean) {
        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }
            val call: Call<WhatsOnBookingAttendeesRes?>? =
                apiInterface.getWhatsOnBookAttendeesList(PrefManager.getAccessToken(), venueId)
            call!!.enqueue(object : Callback<WhatsOnBookingAttendeesRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<WhatsOnBookingAttendeesRes?>,
                    response: Response<WhatsOnBookingAttendeesRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if (response.body()!!.success) {
                                if (whatsOnBookingsData.size > 0) {
                                    whatsOnBookingsData.clear()
                                }
                                whatsOnBookingsData.addAll(response.body()!!.data!!)
                                whatsOnBookingAttendeesAdapter!!.notifyDataSetChanged()
                            }
                            if (whatsOnBookingsData.size == 0) {
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

                override fun onFailure(call: Call<WhatsOnBookingAttendeesRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }
}
package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentWhatsOnReservationBinding
import com.popiin.listener.InfoDialogListener
import com.popiin.req.CreateWhatsOnReserveReq
import com.popiin.res.CreateWhatsonReserveRes
import com.popiin.res.VenueWhatsListRes
import com.popiin.res.WhatsOnReserveListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatsOnReservationFragment : BaseFragment<FragmentWhatsOnReservationBinding>() {
    lateinit var ticketType: Array<String>
    var ticketPosition = -1
    var ticket: String? = null
    override fun getLayout(): Int {
        return R.layout.fragment_whats_on_reservation
    }

    companion object {
        var whatsOnData: WhatsOnReserveListRes.Data? = null
        var venueData: VenueWhatsListRes.Data? = null
        fun newInstance(
            item: WhatsOnReserveListRes.Data? = null,
            venue: VenueWhatsListRes.Data? = null,
        ): WhatsOnReservationFragment {
            whatsOnData = item
            venueData = venue
            return WhatsOnReservationFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        common.handleErrorView(binding.edtTicketPrice.edtName, binding.edtTicketPrice.tvError)
        common.handleErrorView(
            binding.edtTicketQuantity.edtName,
            binding.edtTicketQuantity.tvError
        )

        ticketType = resources.getStringArray(R.array.ticketType)
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireActivity(), R.layout.spin_gender, ticketType)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBookingOption.spinner.adapter = aa
        binding.spBookingOption.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    ticketPosition = position
                    ticket = ticketType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.btnAddReservation.setOnClickListener {
            if (isValidate()) {
                // call add reservation api
                if (whatsOnData != null) {
                    callUpdateWhatsOnReserveApi()
                } else {
                    callCreateWhatsOnReserveApi()
                }
            }
        }

        if (whatsOnData != null) {
            binding.edtTicketQuantity.edtName.text = whatsOnData!!.quantity.toString()
            binding.edtTicketPrice.edtName.text = whatsOnData!!.price.toString()

            binding.btnAddReservation.text = getString(R.string.txt_update_reservation)

            for (i in ticketType.indices) {
                if (whatsOnData!!.ticket_type.equals(ticketType[i])) {
                    binding.spBookingOption.spinner.setSelection(i, true)
                    break
                }
            }
        }

    }

    private fun callCreateWhatsOnReserveApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateWhatsonReserveRes?>? =
                apiInterface.createWhatsOnReservation(
                    PrefManager.getAccessToken(),
                    CreateWhatsOnReserveReq(binding.edtTicketQuantity.edtName.getText().toString()
                        .toInt(),
                        "" + venueData!!.venue_id,
                        ticket!!,
                        binding.edtTicketPrice.edtName.getText().toString().toInt(),
                        "" + venueData!!.id))
            call!!.enqueue(object : Callback<CreateWhatsonReserveRes?> {
                override fun onResponse(
                    call: Call<CreateWhatsonReserveRes?>,
                    response: Response<CreateWhatsonReserveRes?>,
                ) {
                    hideProgress()
                    showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_pass_success),
                        getString(R.string.txt_whatson_reserve_created),
                        response.body()!!.message!!,
                        "",
                        getString(R.string.txt_done),
                        infoDialogListener)
                }

                override fun onFailure(call: Call<CreateWhatsonReserveRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun callUpdateWhatsOnReserveApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateWhatsonReserveRes?>? =
                apiInterface.updateWhatsOnReservation(
                    PrefManager.getAccessToken(),
                    CreateWhatsOnReserveReq(binding.edtTicketQuantity.edtName.getText().toString()
                        .toInt(),
                        "" + whatsOnData!!.venue_id,
                        ticket!!,
                        binding.edtTicketPrice.edtName.getText().toString().toInt(),
                        "" + whatsOnData!!.whatson_id, "" + whatsOnData!!.id))
            call!!.enqueue(object : Callback<CreateWhatsonReserveRes?> {
                override fun onResponse(
                    call: Call<CreateWhatsonReserveRes?>,
                    response: Response<CreateWhatsonReserveRes?>,
                ) {
                    hideProgress()
                    showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_pass_success),
                        getString(R.string.txt_whatson_reserve_updated),
                        response.body()!!.message!!,
                        "",
                        getString(R.string.txt_done),
                        infoDialogListener)
                }

                override fun onFailure(call: Call<CreateWhatsonReserveRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
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
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                false,
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
            requireActivity().supportFragmentManager.popBackStack()
        }

    }


    private fun isValidate(): Boolean {
        var isValid = true

        if (ticketPosition == -1) {
            binding.spBookingOption.tvError.visibility = View.VISIBLE
            binding.spBookingOption.tvError.text = getString(R.string.txt_err_select_option)
            isValid = false
        }

        if (binding.edtTicketPrice.edtName.getText().toString().isEmpty()) {
            binding.edtTicketPrice.tvError.visibility = View.VISIBLE
            binding.edtTicketPrice.tvError.text = getString(R.string.edt_error_add_price)
            isValid = false
        }
        if (binding.edtTicketQuantity.edtName.getText().toString().isEmpty()) {
            binding.edtTicketQuantity.tvError.visibility = View.VISIBLE
            binding.edtTicketQuantity.tvError.text = getString(R.string.edt_error_add_quantity)
            isValid = false
        }

        return isValid

    }
}
package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.WhatsOnReserveAdapter
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.FragmentWhatsonReserveListBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.CommonRes
import com.popiin.res.VenueWhatsListRes
import com.popiin.res.WhatsOnReserveListRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatOnReserveListFragment : BaseFragment<FragmentWhatsonReserveListBinding>() {
    var list: ArrayList<WhatsOnReserveListRes.Data> = ArrayList()
    lateinit var adapter: WhatsOnReserveAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_whatson_reserve_list
    }

    companion object {
        var whatsOnData: VenueWhatsListRes.Data? = null
        fun newInstance(item: VenueWhatsListRes.Data): WhatOnReserveListFragment {
            whatsOnData = item
            return WhatOnReserveListFragment()
        }
    }

    override fun initViews() {

        adapter = WhatsOnReserveAdapter(list, listener)
        binding.rvWhatsOnReservation.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvWhatsOnReservation.adapter = adapter

        binding.ivAddReservation.setOnClickListener {
            setFragmentWithStack(WhatsOnReservationFragment.newInstance(null, whatsOnData),
                WhatsOnReservationFragment::class.java.simpleName)
        }

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // callWhatsOnTicketListApi()
    }

    var listener: AdapterItemClickListener<WhatsOnReserveListRes.Data> =
        object : AdapterItemClickListener<WhatsOnReserveListRes.Data>() {
            override fun onAdapterEditClick(item: WhatsOnReserveListRes.Data, position: Int) {
                super.onAdapterEditClick(item, position)
                setFragmentWithStack(WhatsOnReservationFragment.newInstance(item),
                    WhatsOnReservationFragment::class.java.simpleName)
            }

            override fun onAdapterDeleteClick(item: WhatsOnReserveListRes.Data, position: Int) {
                super.onAdapterDeleteClick(item, position)
                openDeleteWhatsOnDialog(item.id, position, "")
            }
        }

    private fun openDeleteWhatsOnDialog(id: Int, position: Int, msg: String?) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )


        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if (position == -1 && id == 0) {
            binding.btnNo.visibility = View.GONE
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_pass_success
                )
            )
            binding.ivClose.visibility = View.GONE
            binding.tvPassSuccess.text = getString(R.string.txt_reservation_removed)
            binding.tvSuccess.text = msg
        } else {
            binding.tvPassSuccess.text = getString(R.string.txt_do_you_want_to)
            binding.tvSuccess.text = getString(R.string.txt_remove_reservation)
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_sure_delete
                )
            )
            binding.ivClose.visibility = View.VISIBLE
            binding.btnNo.visibility = View.VISIBLE
            binding.btnNo.text = getString(R.string.txt_no)
            binding.btnDone.text = getString(R.string.txt_yes_remove)
        }


        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (position != -1 && id != 0) {
                callDeleteWhatsOn(position, id)
            } else {
                if (list.size == 0) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }

        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun callDeleteWhatsOn(position: Int, id: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? = apiInterface.deleteWhatsOnReservation(
                PrefManager.getAccessToken(),
                id, whatsOnData!!.venue_id
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            list.removeAt(position)
                            adapter.notifyDataSetChanged()
                            openDeleteWhatsOnDialog(0, -1, response.body()!!.msg)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.msg)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    private fun callWhatsOnTicketListApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<WhatsOnReserveListRes?>? = apiInterface.getWhatsOnTicketList(
                PrefManager.getAccessToken(), whatsOnData!!.id)
            call!!.enqueue(object : Callback<WhatsOnReserveListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<WhatsOnReserveListRes?>,
                    response: Response<WhatsOnReserveListRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            list.clear()
                            list.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                        }

                        if (list.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                            binding.inclMessage.tvNoDataMessage.text = ""
                        }
                    }
                }

                override fun onFailure(call: Call<WhatsOnReserveListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        callWhatsOnTicketListApi()
    }
}
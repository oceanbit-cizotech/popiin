package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueEarninigBinding
import com.popiin.listener.AdapterWithdrawListener
import com.popiin.res.Earning
import com.popiin.res.WhatsonEarningRes
import com.popiin.util.Common

class VenueEarningAdapter(
    var earning: ArrayList<WhatsonEarningRes.Data>?,
) : BaseRVAdapter<AdapterVenueEarninigBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_venue_earninig
    }

    override fun getItemCount(): Int {
        return earning!!.size
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterVenueEarninigBinding>,
        position: Int
    ) {
        holder.binding.data = earning?.get(position)
        holder.binding.common = Common.instance

    /*    holder.binding.tvWithdraw.setOnClickListener {
           // adapterWithdrawListener.onWithdrawEventClick(earning!![position], position)
        }*/
    }
}
package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterSearchAddressBinding
import com.popiin.listener.AdapterEventListener
import com.popiin.res.AddressSearchRes

class SearchAddressAdapter(
    var addressList: ArrayList<AddressSearchRes.Result?>,
    var listener: AdapterEventListener<AddressSearchRes.Result?>,
) : BaseRVAdapter<AdapterSearchAddressBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_search_address
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterSearchAddressBinding>,
        position: Int,
    ) {
        holder.binding.tvAddress.text = addressList[position]!!.formattedAddress

        holder.binding.clMain.setOnClickListener {
            listener.onEventAddressSearch(addressList[position]!!, position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }
}
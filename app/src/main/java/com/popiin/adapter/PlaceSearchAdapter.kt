package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterPlaceSearchBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.PlaceSearchRes

class PlaceSearchAdapter(
    var list: ArrayList<PlaceSearchRes.Results>,
    var listener: AdapterItemClickListener<PlaceSearchRes.Results>,
) : BaseRVAdapter<AdapterPlaceSearchBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_place_search
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterPlaceSearchBinding>,
        position: Int,
    ) {
        holder.binding.model = list[position]

        holder.binding.clCurrentAddress.setOnClickListener {
            listener.onAdapterItemClick(list[position], position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
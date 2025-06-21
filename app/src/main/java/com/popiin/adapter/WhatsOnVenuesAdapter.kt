package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterWhatsonVenuesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueWhatsListRes
import com.popiin.util.Common

class WhatsOnVenuesAdapter (var list: List<VenueWhatsListRes.Data>,var listener:AdapterItemClickListener<VenueWhatsListRes.Data>) : BaseRVAdapter<AdapterWhatsonVenuesBinding>(){
    override fun getLayout(): Int {
        return R.layout.adapter_whatson_venues
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterWhatsonVenuesBinding>, position: Int) {
        holder.binding.whatsOn = list[position]
        holder.binding.bc = Common.instance

        holder.binding.ivEditWhatsOn.setOnClickListener {
            listener.onAdapterEditClick(list[position], position)
        }

        holder.binding.ivDeleteWhatsOn.setOnClickListener {
            listener.onEventDeleteClick(list[position], position)
        }

        holder.binding.ivWhatsonReservations.setOnClickListener {
            listener.onWhatsOnReserveEditClick(list[position], position)
        }
    }

}
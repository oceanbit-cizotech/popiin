package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterWhatsOnReserveBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.WhatsOnReserveListRes

class WhatsOnReserveAdapter(
    var list: ArrayList<WhatsOnReserveListRes.Data>,
    var listener: AdapterItemClickListener<WhatsOnReserveListRes.Data>,
) : BaseRVAdapter<AdapterWhatsOnReserveBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_whats_on_reserve
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterWhatsOnReserveBinding>,
        position: Int,
    ) {
        holder.binding.model = list[position]

        holder.binding.ivDeleteVenue.setOnClickListener {
            listener.onAdapterDeleteClick(list[position], position)
        }

        holder.binding.ivEditVenue.setOnClickListener {
            listener.onAdapterEditClick(list[position], position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
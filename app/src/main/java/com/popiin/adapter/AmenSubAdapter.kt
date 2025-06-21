package com.popiin.adapter

import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterAmenSubBinding
import com.popiin.model.AmensSubModel


class AmenSubAdapter(private var featureList: List<AmensSubModel>) :
    BaseRVAdapter<AdapterAmenSubBinding>() {

    override fun getLayout(): Int {
        return R.layout.adapter_amen_sub
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterAmenSubBinding>, position: Int) {

    }

    override fun getItemCount(): Int {
       return featureList.size
    }

}
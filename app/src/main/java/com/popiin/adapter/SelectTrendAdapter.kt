package com.popiin.adapter

import android.annotation.SuppressLint
import android.widget.PopupWindow
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterTrendBinding
import com.popiin.listener.AdapterOfferListener
import com.popiin.res.Trend
import com.popiin.util.Common

class SelectTrendAdapter(
    var list: ArrayList<Trend>,
    var listener: AdapterOfferListener<Trend>,
    var dialog: PopupWindow
) : BaseRVAdapter<AdapterTrendBinding>() {
    var c: Common = Common.instance!!

    override fun getLayout(): Int {
        return R.layout.adapter_trend
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterTrendBinding>, position: Int) {
        val trend=list[position]
        holder.binding.trend=trend
        holder.binding.common=c
        holder.binding.tvTitle.text=trend.title
        holder.binding.tvDes.text=trend.summary

        holder.binding.clMain.setOnClickListener {
            dialog.dismiss()
            listener.onOfferItemClick(list[position], position)

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}
package com.popiin.adapter

import android.annotation.SuppressLint
import android.widget.PopupWindow
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterTrendBinding
import com.popiin.databinding.AdapterTrendingBinding
import com.popiin.databinding.AdapterVenueTrendBinding
import com.popiin.listener.AdapterOfferListener
import com.popiin.listener.AdapterVenueTrendsListener
import com.popiin.res.Trend
import com.popiin.res.VenueTrend
import com.popiin.util.Common

class VenueTrendsListAdapter(
    var list: ArrayList<VenueTrend>,
) : BaseRVAdapter<AdapterTrendingBinding>() {
    var c: Common = Common.instance!!

    override fun getLayout(): Int {
        return R.layout.adapter_trending
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterTrendingBinding>, position: Int) {
        val trend=list[position]
        holder.binding.trend=trend
        holder.binding.common=c
        holder.binding.tvTitle.text=trend.title
        holder.binding.tvDes.text=trend.summary
        c.loadImageFromUrl(holder.binding.ivTrending,trend.banner_image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
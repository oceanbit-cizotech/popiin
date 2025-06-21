package com.popiin.adapter

import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterExploreOpenCloseHourBinding
import com.popiin.model.ExploreOpenCloseModel

class ExploreOpenCloseAdapter(private var openCloseList: ArrayList<ExploreOpenCloseModel>) :
    BaseRVAdapter<AdapterExploreOpenCloseHourBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_explore_open_close_hour
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterExploreOpenCloseHourBinding>,
        position: Int,
    ) {
        holder.binding.img = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_clock)
        holder.binding.title = openCloseList[position].title
        holder.binding.time = openCloseList[position].time

        holder.binding.viewHide = openCloseList.size > 1
    }

    override fun getItemCount(): Int {
        return openCloseList.size
    }
}
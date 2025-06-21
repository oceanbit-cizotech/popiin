package com.popiin.adapter

import android.view.View
import android.widget.PopupWindow
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterReserveExploreDetailsBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.model.ExploreDetailReserveModel

class ExploreDetailReserveAdapter(
    var list: ArrayList<ExploreDetailReserveModel>,
    private var reserveListener: AdapterItemClickListener<ExploreDetailReserveModel>,
    var dialog: PopupWindow,
    private var isTicketEmpty: Boolean,
) : BaseRVAdapter<AdapterReserveExploreDetailsBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_reserve_explore_details
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterReserveExploreDetailsBinding>,
        position: Int
    ) {
        holder.binding.inclReserveTickets.title = list[position].title

        holder.binding.inclReserveTickets.viewHide = list.size <= 1
        holder.binding.inclReserveTickets.btn.setOnClickListener {
            dialog.dismiss()
            reserveListener.onAdapterItemClick(list[position], position)
        }

        if (!isTicketEmpty) {
            holder.binding.inclReserveTickets.btn.alpha = 0.5f
            holder.binding.inclReserveTickets.btn.isEnabled = false
        } else {
            holder.binding.inclReserveTickets.btn.alpha = 1f
            holder.binding.inclReserveTickets.btn.isEnabled = true
        }

        if (position == list.size - 1) {
            holder.binding.inclReserveTickets.view.visibility = View.VISIBLE
        } else {
            holder.binding.inclReserveTickets.view.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
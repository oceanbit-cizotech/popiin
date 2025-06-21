package com.popiin.adapter

import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueReservationsBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes


class VenueReservationsAdapter(var list: ArrayList<VenueListRes.Venue>,var listener : AdapterItemClickListener<VenueListRes.Venue>) : BaseRVAdapter<AdapterVenueReservationsBinding>() {
    private var checkStatus = 0
    override fun getLayout(): Int {
        return R.layout.adapter_venue_reservations
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterVenueReservationsBinding>, position: Int) {
        holder.binding.venue = list[position]
        holder.binding.switchEventConfirm.isChecked =
            list[holder.absoluteAdapterPosition].reservation_enabled == 1

        setEditVisible(holder)


        holder.binding.switchEventConfirm.setOnCheckedChangeListener { _, isChecked ->
            checkStatus = if (isChecked) {
                1
            } else {
                0
            }
            setEditVisible(holder)
            if (list.size > 0) {
                listener.onSwitchClick(
                    list[holder.absoluteAdapterPosition],
                    holder.absoluteAdapterPosition,
                    checkStatus
                )
            }
        }

        holder.binding.ivEdit.setOnClickListener {
            if (holder.binding.switchEventConfirm.isChecked) {
                listener.onVenueReserveEditClick(
                    list[holder.absoluteAdapterPosition],
                    holder.absoluteAdapterPosition
                )
            }
        }
    }

    private fun setEditVisible(holder: BaseViewHolder<AdapterVenueReservationsBinding>) {
        if (holder.binding.switchEventConfirm.isChecked) {
            holder.binding.ivEdit.visibility = View.VISIBLE
        } else {
            holder.binding.ivEdit.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
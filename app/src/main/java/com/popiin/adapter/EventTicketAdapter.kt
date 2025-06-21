package com.popiin.adapter
import android.annotation.SuppressLint
import com.popiin.listener.AdapterItemClickListener
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterBookingTicketBinding
import com.popiin.res.EventListRes

class EventTicketAdapter(
    var list: List<EventListRes.Tickets>,
    private var listener: AdapterItemClickListener<EventListRes.Tickets?>,
) : BaseRVAdapter<AdapterBookingTicketBinding?>() {
    override fun getLayout(): Int {
        return R.layout.adapter_booking_ticket
    }


    override fun getItemCount(): Int {
        return list.size
    }

    private fun Boolean.setAllSelect() {
        for (event in list) {
            event.isSelected = this
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelect(isSelected: Boolean, position: Int) {
        if (position >= list.size) {
            return
        }
        false.setAllSelect()
        list[position].isSelected = isSelected
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterBookingTicketBinding?>,
        position: Int
    ) {
        holder.binding!!.ticket = list[position]
        holder.binding!!.favouriteTicket = null
        if (list[position].available_quantity > 0) {
            holder.binding!!.root.setOnClickListener {
                if (!list[position].isSelected) {
                    false.setAllSelect()
                    list[position].isSelected = true
                    notifyDataSetChanged()
                }
                listener.onAdapterItemClick(list[position], position)
            }
        }
        if (list[position].isSelected) {
            listener.onAdapterItemClick(list[position], position)
        }
    }
}
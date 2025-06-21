package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterBookingTagBinding
import com.popiin.listener.AdapterTicketTagListener
import com.popiin.model.TicketBook


class BookingTagAdapter(
    private var ticketBooks: ArrayList<TicketBook>,
    var listener: AdapterTicketTagListener<TicketBook?>,
) : BaseRVAdapter<AdapterBookingTagBinding?>() {
    override fun getLayout(): Int {
        return R.layout.adapter_booking_tag
    }

    override fun getItemCount(): Int {
        return ticketBooks.size
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterBookingTagBinding?>,
        position: Int,
    ) {
        holder.binding!!.ticket = ticketBooks[position]
    }
}
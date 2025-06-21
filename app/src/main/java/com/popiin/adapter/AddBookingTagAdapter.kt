package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterAddBookingTagBinding
import com.popiin.listener.AdapterTicketTagListener
import com.popiin.model.TicketBook
import com.popiin.util.Common

class AddBookingTagAdapter(var ticketBooks: ArrayList<TicketBook>, var listner: AdapterTicketTagListener<TicketBook?>, var isDelete: Boolean) : BaseRVAdapter<AdapterAddBookingTagBinding?>() {
    var common  = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_add_booking_tag
    }

    override fun getItemCount(): Int {
        return ticketBooks.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterAddBookingTagBinding?>, position: Int) {
        holder.binding!!.isdelete = isDelete
        holder.binding!!.ticket = ticketBooks[position]
        holder.binding!!.price = common!!.formatter.getOneDecimalFormatValue(ticketBooks[position].getPrice().toDouble())
        holder.binding!!.imgCross.setOnClickListener {
            listner.onItemDeleteClick(ticketBooks[position], position)
        }
        //binding.eventTotalCapacity.edtName.setFilters(new InputFilter[]{ new MinMaxFilter("0", ""+MAX_CAPACITY)});
        holder.binding!!.root.setOnClickListener {
            listner.onItemClick(ticketBooks[position], position)
        }
    }
}
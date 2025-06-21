package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterBookOptionsBinding
import com.popiin.model.TicketBook

class BookOptionsAdapter(var list: ArrayList<TicketBook>) : BaseRVAdapter<AdapterBookOptionsBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_book_options
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterBookOptionsBinding>,
        position: Int,
    ) {
        holder.binding.bookOptions = list[position].name
        holder.binding.price = list[position].price
        holder.binding.qty = list[position].quantity
    }

    override fun getItemCount(): Int {
       return list.size
    }
}
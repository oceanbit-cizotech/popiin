package com.popiin.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterWhatsonReserveBookingInfoBinding
import com.popiin.listener.AdapterWhatsReserveItemListener
import com.popiin.listener.SearchTextListener
import com.popiin.res.VenueWhatsListRes
import java.util.ArrayList

class WhatsonReserveBookingInfoAdapter(
    var list: ArrayList<VenueWhatsListRes.Whatsonticket>,
    var listener: AdapterWhatsReserveItemListener<VenueWhatsListRes.Whatsonticket>,
) : BaseRVAdapter<AdapterWhatsonReserveBookingInfoBinding>() {
    lateinit var ticketType: Array<String>
    var ticketName = ""
    var ticketPosition = 0
    override fun getLayout(): Int {
        return R.layout.adapter_whatson_reserve_booking_info
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterWhatsonReserveBookingInfoBinding>, @SuppressLint("RecyclerView") position: Int, ) {
        holder.binding.edtTicketPrice.edtName.text = if(list[position].price!=null) ""+list[position].price else ""
        holder.binding.edtTicketQuantity.edtName.text = if (("" + list[position].quantity).isEmpty() || list[position].quantity == 0) "" else "" + list[position].quantity
        ticketType = holder.itemView.resources.getStringArray(R.array.whatsTicketType)

        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(holder.itemView.context, R.layout.spinner_item, ticketType)
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
        holder.binding.spBookingOption.spinner.adapter = aa

        holder.binding.ivClose.setOnClickListener {
            listener.onDeleteClick(list[position], position)
        }

        if(list[position].ticket_type!=null && list[position].ticket_type!!.isNotEmpty()){
            if(list[position].price==null){
                holder.binding.edtTicketPrice.tvError.visibility=View.VISIBLE
                holder.binding.edtTicketPrice.tvError.text="Add price."
            }/*else if(list[position].price!! < 1.0f){
                holder.binding.edtTicketPrice.tvError.visibility=View.VISIBLE
                holder.binding.edtTicketPrice.tvError.text="Please enter a price greater than 1."
            }*/else{
                holder.binding.edtTicketPrice.tvError.visibility=View.GONE
            }

            if(list[position].quantity== 0){
                holder.binding.edtTicketQuantity.tvError.visibility=View.VISIBLE
                holder.binding.edtTicketQuantity.tvError.text="Add quantity."
            }else{
                holder.binding.edtTicketQuantity.tvError.visibility=View.GONE
            }
        }

        holder.binding.edtTicketPrice.edtName.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                if (searchText!!.isNotEmpty()) {
                    listener.onPrice(list[position], position, searchText)
                }
            }
        })

        holder.binding.edtTicketQuantity.edtName.addTextChangedListener(object :
            SearchTextListener() {
            override fun performSearch(searchText: String?) {
                if (searchText!!.isNotEmpty()) {
                    listener.onQuantity(list[position], position, searchText)
                }
            }
        })


        holder.binding.spBookingOption.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    ticketPosition = position


                    (view as TextView).setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorHeaderText
                        )
                    )

                    if (position > 0) {
                        ticketName = ticketType[position]
                        listener.onBookingType(holder.absoluteAdapterPosition, ticketName)
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        for (i in ticketType.indices) {
            if (ticketType[i].equals(list[position].ticket_type, true)) {
                holder.binding.spBookingOption.spinner.setSelection(i, true)
                break
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
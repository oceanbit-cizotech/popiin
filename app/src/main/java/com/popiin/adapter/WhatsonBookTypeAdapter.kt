package com.popiin.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterWhatsonBookTypeBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes

class WhatsonBookTypeAdapter(
    var tickets: ArrayList<VenueListRes.WhatsonTicket>,
    var selectedPosition: Int,
    private var bookTypeListener: AdapterItemClickListener<VenueListRes.WhatsonTicket>,
    var dialog: Dialog?,
) : BaseRVAdapter<AdapterWhatsonBookTypeBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_whatson_book_type
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterWhatsonBookTypeBinding>, position: Int) {
        holder.binding.tvTitle.text = tickets[position].ticket_type!!
        if (selectedPosition == position) {
            setItem(holder, selectedPosition)
        }

        holder.binding.clMain.setOnClickListener {
            dialog!!.dismiss()
            tickets[position].isSelected = true

            if (selectedPosition != position && tickets[position].isSelected) {
                tickets[position].isSelected = false
            }
            selectedPosition = position
            notifyDataSetChanged()
            bookTypeListener.onAdapterItemClick(tickets[position], position)

        }
    }

    private fun setItem(holder: BaseViewHolder<AdapterWhatsonBookTypeBinding>, selectedPosition: Int) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.clMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorWhite
                )
            )
        } else {
            holder.binding.clMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorSemiGrey
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return tickets.size
    }
}
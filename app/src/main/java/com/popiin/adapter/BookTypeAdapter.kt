package com.popiin.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterBookTypeBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes

class BookTypeAdapter(
    var tickets: ArrayList<VenueListRes.Tickets>,
    var selectedPosition: Int,
    private var bookTypeListener: AdapterItemClickListener<VenueListRes.Tickets>,
    var dialog: Dialog?,
) : BaseRVAdapter<AdapterBookTypeBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_book_type
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterBookTypeBinding>, position: Int) {
        holder.binding.tvTitle.text = tickets[position].name!!
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

    private fun setItem(holder: BaseViewHolder<AdapterBookTypeBinding>, selectedPosition: Int) {
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
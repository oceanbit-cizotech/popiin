package com.popiin.adapter

import android.annotation.SuppressLint
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterAvailableOfferBinding
import com.popiin.listener.AdapterOfferListener
import com.popiin.model.AvailOfferModel

class AvailOfferAdapter(
    var list: ArrayList<AvailOfferModel>,
    var listener: AdapterOfferListener<AvailOfferModel>,
    var selectedPosition: Int,
    var dialog: PopupWindow
) : BaseRVAdapter<AdapterAvailableOfferBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_available_offer
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterAvailableOfferBinding>, position: Int) {
        holder.binding.tvTitle.setText(list[position].title)

        if (selectedPosition == position) {
            setItem(holder, selectedPosition)
        }

        holder.binding.clMain.setOnClickListener {
            dialog.dismiss()
            list[position].isSelected = true

            if (selectedPosition != position && list[position].isSelected) {
                list[position].isSelected = false
            }
            selectedPosition = position
            notifyDataSetChanged()
            listener.onOfferItemClick(list[position], position)

        }
    }

    private fun setItem(
        holder: BaseViewHolder<AdapterAvailableOfferBinding>,
        selectedPosition: Int,
    ) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorWhite))
        } else {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorSemiGrey))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
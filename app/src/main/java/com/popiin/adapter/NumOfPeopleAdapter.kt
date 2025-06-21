package com.popiin.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterNumPeopleBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.model.NumOfPeopleModel

class NumOfPeopleAdapter(
    private var numberPeoples: ArrayList<NumOfPeopleModel>,
    var selectedPosition: Int,
    private var numOfPeopleListener: AdapterItemClickListener<NumOfPeopleModel>,
    var dialog: Dialog?,
) : BaseRVAdapter<AdapterNumPeopleBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_num_people
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterNumPeopleBinding>, position: Int) {
        holder.binding.tvTitle.text = numberPeoples[position].title

        if (selectedPosition == position) {
            setItem(holder, selectedPosition)
        }

        holder.binding.clMain.setOnClickListener {
            dialog!!.dismiss()
            numberPeoples[position].isSelected = true

            if (selectedPosition != position && numberPeoples[position].isSelected) {
                numberPeoples[position].isSelected = false
            }
            selectedPosition = position
            notifyDataSetChanged()
            numOfPeopleListener.onAdapterItemClick(numberPeoples[position], position)

        }
    }

    private fun setItem(holder: BaseViewHolder<AdapterNumPeopleBinding>, selectedPosition: Int) {
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
        return numberPeoples.size
    }
}
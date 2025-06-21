package com.popiin.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterSelectDurationBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.model.DurationModel

class SelectDurationAdapter(
    var list: ArrayList<DurationModel>,
    var listener: AdapterMyVenuesListener<DurationModel>,
    var selectedPosition:Int=0
) : BaseRVAdapter<AdapterSelectDurationBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_select_duration
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterSelectDurationBinding>, position: Int) {
        holder.binding.tvFilter.setText(list[position].title)


        setItem(holder,selectedPosition)

        holder.itemView.setOnClickListener {

            if(selectedPosition!=holder.absoluteAdapterPosition){
                list[selectedPosition].isSelected=false
                list[position].isSelected = true
                selectedPosition = holder.absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }

        holder.binding.checkbox.setOnClickListener {
            if(selectedPosition!=holder.absoluteAdapterPosition){
                list[selectedPosition].isSelected=false
                list[position].isSelected = true
                selectedPosition = holder.absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }
    }


    private fun setItem(holder: BaseViewHolder<AdapterSelectDurationBinding>, selectedPosition: Int) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorWhite))
            holder.binding.checkbox.isChecked = true
            listener.onEventSelected(list[selectedPosition], selectedPosition)
        } else {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorSemiGrey))
            holder.binding.checkbox.isChecked = false
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}
package com.popiin.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventTagBinding
import com.popiin.model.TagModel


class EventTagAdapter(private var tagList: ArrayList<TagModel>) :
    BaseRVAdapter<AdapterEventTagBinding>() {
    private var selectedPosition = -1
    override fun getLayout(): Int {
        return R.layout.adapter_event_tag
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterEventTagBinding>, position: Int) {
        holder.binding.model = tagList[position]

        setItem(holder, selectedPosition)

        holder.itemView.setOnClickListener {
            selectedPosition = holder.absoluteAdapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    private fun setItem(holder: BaseViewHolder<AdapterEventTagBinding>, selectedPosition: Int) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.tvTag.setBackgroundResource(R.drawable.bg_black_btn_with_round_corner)
            holder.binding.tvTag.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorWhite))
        } else {
            holder.binding.tvTag.setBackgroundResource(R.drawable.bg_unselected_tag)
            holder.binding.tvTag.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorStoryText))
        }
    }
}
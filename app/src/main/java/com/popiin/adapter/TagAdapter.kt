package com.popiin.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterTagBinding
import com.popiin.listener.AdapterTagClickListener
import com.popiin.model.TagModel

class TagAdapter(
    private var tagList: ArrayList<TagModel>,
    var listener: AdapterTagClickListener<TagModel>,
    var selectedPosition: Int,
) : BaseRVAdapter<AdapterTagBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_tag
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterTagBinding>, position: Int) {
        holder.binding.model = tagList[position]

        setItem(holder, selectedPosition)

        holder.binding.clMain.setOnClickListener {
            onItemClicked(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onItemClicked(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
        listener.onTagClick(tagList[position], position)
    }

    fun resetData() {
        onItemClicked(0)
    }

    private fun setItem(holder: BaseViewHolder<AdapterTagBinding>, selectedPosition: Int) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.tvTag.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorHeaderText
                )
            )
            holder.binding.tvTag.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorWhite
                )
            )
        } else {
            holder.binding.tvTag.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorSemiGrey
                )
            )
            holder.binding.tvTag.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorHeaderText
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}
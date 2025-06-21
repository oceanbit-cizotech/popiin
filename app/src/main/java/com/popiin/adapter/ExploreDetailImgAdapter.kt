package com.popiin.adapter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterExploreDetailImgBinding
import com.popiin.listener.ImageOnclick

class ExploreDetailImgAdapter(var list: ArrayList<String>, var listener: ImageOnclick) :
    BaseRVAdapter<AdapterExploreDetailImgBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_explore_detail_img
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterExploreDetailImgBinding>,
        position: Int
    ) {
        Glide.with(holder.itemView.context).load(list[position]).into(holder.binding.ivImgVenues)

        holder.binding.ivImgVenues.setOnClickListener {
            listener.onImageSelected(position)
        }
        if (list.size > 5 && position == 4) {
            holder.binding.tvNumber.visibility = View.VISIBLE
            holder.binding.tvNumber.text = "+${list.size - 5}"
        }else{
            holder.binding.tvNumber.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
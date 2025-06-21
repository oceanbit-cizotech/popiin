package com.popiin.adapter

import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterTrendingStoriesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueStoryListRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.cloudinary.Transformation
import java.util.ArrayList


class TrendingStoriesAdapter(
    private var storiesList: ArrayList<VenueStoryListRes>,
    private var listener: AdapterItemClickListener<VenueStoryListRes>,
) :
    BaseRVAdapter<AdapterTrendingStoriesBinding>() {

    override fun getLayout(): Int {
        return R.layout.adapter_trending_stories
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterTrendingStoriesBinding>,
        position: Int,
    ) {
        var url = PrefManager.cloudinary.url()
            .resourcType(Constant().video)
            .transformation(Transformation<Transformation<*>?>()
                .quality(Constant().auto))
            .generate(storiesList[position].data!![position].video_id + ".jpg")
        val originalText = storiesList[position].data!![position].venue?.venue_name
        url = storiesList[position].data!![position].venue?.venue_banner_image

        holder.binding.tvName.text = if (originalText!!.length > 7) {
            "${originalText.substring(0, 7)}..."
        } else {
            originalText
        }

      //  holder.binding.tvName.text=storiesList[position].data!![position].venue?.venue_name
        Glide.with(holder.itemView.context).load(url).into(holder.binding.civStory)

        holder.binding.flStoryImage.setOnClickListener {
            listener.onAdapterItemClick(storiesList[position], position)
        }

    }

    override fun getItemCount(): Int {
        return storiesList.size
    }
}
package com.popiin.adapter

import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterStoryListBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.StoryListRes
import com.popiin.res.VenueStoryListRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.cloudinary.Transformation

class StoryListAdapter(
    private var storyList: ArrayList<VenueStoryListRes>,
    private var storyListener: AdapterItemClickListener<VenueStoryListRes>,
) : BaseRVAdapter<AdapterStoryListBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_story_list
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterStoryListBinding>, position: Int) {
        val url = PrefManager.cloudinary.url()
            .resourcType(Constant().video)
            .transformation(Transformation<Transformation<*>?>()
                .quality(Constant().auto))
            .generate(storyList[position].data?.get(position)!!.video_id + ".jpg")


        Glide.with(holder.itemView.context).load(url).into(holder.binding.ivStory)

        holder.binding.tvStoryText.text = storyList[position].data?.get(position)!!.story_text

        holder.binding.ivDeleteVenue.setOnClickListener {
            storyListener.onAdapterDeleteClick(storyList[position], position)
        }

        holder.binding.root.setOnClickListener {
            storyListener.onAdapterItemClick(storyList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
}
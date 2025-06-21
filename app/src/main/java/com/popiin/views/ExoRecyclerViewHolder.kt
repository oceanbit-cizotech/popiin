package com.popiin.views

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.popiin.R
import com.popiin.res.VenueStoryListRes
import io.ktor.http.Url

open class ExoRecyclerViewHolder(private val parent: View) : RecyclerView.ViewHolder(
    parent) {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    var mediaContainer: FrameLayout
    var mediaCoverImage: ImageView
    var volumeControl: ImageView? = null
    var progressBar: ProgressBar
    var requestManager: RequestManager? = null
    private val title: TextView
    private val des: TextView
    private val userHandle: TextView

    init {
        mediaContainer = itemView.findViewById(R.id.mediaContainer)
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage)
        title = itemView.findViewById(R.id.tvTitle)
        des = itemView.findViewById(R.id.tv_description)
        userHandle = itemView.findViewById(R.id.tvUserHandle)
        progressBar = itemView.findViewById(R.id.progressBar)

    }

    fun onBind(
        mediaObject: VenueStoryListRes.Data?,
        ivOnOff: ImageView,
        requestManager: RequestManager?,
        url: String?
    ) {
        parent.tag = this
        this.requestManager = requestManager
        volumeControl = ivOnOff

        Glide.with(ivOnOff.context)
            .load(url)
            .into(mediaCoverImage)
    }
}
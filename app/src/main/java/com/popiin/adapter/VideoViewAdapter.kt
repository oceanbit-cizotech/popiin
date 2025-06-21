package com.popiin.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.ImageView
import com.bumptech.glide.RequestManager
import com.popiin.R
import com.popiin.databinding.AdapterVideoViewBinding
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.res.VenueStoryListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.popiin.views.ExoRecyclerViewHolder
import com.cloudinary.Transformation
class VideoViewAdapter(
    private var videoList: ArrayList<VenueStoryListRes.Data>,
    private var ivOnOff: ImageView,
    private var requestManager: RequestManager?,
    private var common: Common,
   private var listener: AdapterMyBookingListener<VenueStoryListRes.Data>
) : RecyclerView.Adapter<VideoViewAdapter.VideoHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VideoHolder {
        val binding: AdapterVideoViewBinding =
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                R.layout.adapter_video_view,
                viewGroup,
                false)
        return VideoHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val url = PrefManager.cloudinary.url()
            .resourcType(Constant().video)
            .transformation(Transformation<Transformation<*>?>().quality(Constant().auto))
            .generate(videoList[position].video_id + ".jpg")

        holder.onBind(videoList[position], ivOnOff, requestManager,url)

        holder.binding.isFavorite=videoList[position].venue!!.isFavorite

        holder.binding.cbLike.setOnClickListener {
            listener.onFavorite(
                videoList[position],
                position,
                holder.binding.cbLike.isChecked
            )
            if(holder.binding.cbLike.isChecked){
                holder.binding.isFavorite=1
                videoList[position].venue!!.isFavorite=1
            }else{
                videoList[position].venue!!.isFavorite=0
                holder.binding.isFavorite=0

            }
        }

        holder.binding.tvUserName.text=videoList.get(position).venue?.venue_name
        common.loadImageFromUrl(holder.binding.civProfile,  videoList.get(position).venue?.venue_banner_image )
        holder.binding.tvDescription.text=videoList.get(position).story_text

        if (videoList[position].venue?.distance != null && videoList[position].venue?.distance != 0.0) {
            holder.binding.tvLocation.text=common.getOneDecimalFormatValueWithMiles(videoList[position].venue?.distance!!.toDouble())
        } else {
            val distance = common.calculateDistanceInMiles(
                PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude,
                videoList[position].venue?.venue_latitude!!.toDouble(),
                videoList[position].venue?.venue_longitude!!.toDouble()
            )
            holder.binding.tvLocation.text=common.getOneDecimalFormatValueWithMiles(distance)
        }

        if (videoList[position].venue?.reservation_enabled == 1 && videoList[position].venue?.tickets!!.isNotEmpty()) {
            holder.binding.llBookNow.visibility = View.VISIBLE
        } else {
            holder.binding.llBookNow.visibility = View.GONE
        }
        holder.binding.llBookNow.setOnClickListener {

            listener.onItemClick(videoList.get(position),position)
        }

        holder.binding.llDirection.setOnClickListener {
            listener.onDirectionClick(videoList.get(position),position)
        }
        val openCloseText = common.getOpenCloseText(videoList[position].venue!!)
        if (openCloseText.equals(common.closedText)) {
           holder.binding.tvOpenCloseTag.text=Constant().close

        } else {
            holder.binding.tvOpenCloseTag.text=Constant().open
        }

        val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(videoList[position].venue!!)
        var venueType = ""
        if (!topThreeVenueType.isEmpty()) {
            for (venueTypeString in topThreeVenueType) {
                venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
            }

            venueTypeAdapter= VenueTypeAdapter(topThreeVenueType as ArrayList<String?>)
            holder.binding.rvVenueType.layoutManager = LinearLayoutManager(holder.binding.imageView.context,LinearLayoutManager.HORIZONTAL,false)
            holder.binding.rvVenueType.adapter = venueTypeAdapter
        }
      //  makeTextViewResizableWithStyledSpans(holder.binding.tvDescription,2)

//      /*  val item = videoList[position]
//        val textView = holder.binding.tvDescription
//
//        if (item.isExpanded) {
//            val fullText = "${item.story_text } Read less"
//            val spannable = SpannableString(fullText)
//
//            val clickableSpan = object : ClickableSpan() {
//                override fun onClick(widget: View) {
//                    item.isExpanded = false
//                    notifyItemChanged(position)
//                }
//
//                override fun updateDrawState(ds: TextPaint) {
//                    ds.color = Color.WHITE
//                    ds.isFakeBoldText = true
//                    ds.isUnderlineText = false
//                }
//            }
//
//            spannable.setSpan(
//                clickableSpan,
//                spannable.length - "Read less".length,
//                spannable.length,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//
//            textView.text = spannable
//            textView.movementMethod = LinkMovementMethod.getInstance()
//        } else {
//            textView.text = item.story_text
//            makeTextViewResizableWithStyledSpans(
//                textView = holder.binding.tvDescription,
//                maxLines = 3,
//                isExpanded = item.isExpanded,
//                onToggle = { expanded ->
//                    item.isExpanded = expanded
//                    notifyItemChanged(position)
//                }
//            )
//        }*/

    }
   var  venueTypeAdapter:VenueTypeAdapter?=null

    override fun getItemCount(): Int {
        return videoList.size
    }

    class VideoHolder(binding: AdapterVideoViewBinding) : ExoRecyclerViewHolder(binding.root) {
        var binding: AdapterVideoViewBinding
        init {
            this.binding = binding
        }
    }

    fun makeTextViewResizableWithStyledSpans(
        textView: TextView,
        maxLines: Int,
        expandText: String = "Read more",
        collapseText: String = "Read less",
        expandColor: Int = Color.WHITE,
        collapseColor: Int = Color.WHITE,
        isExpanded: Boolean,
        onToggle: (Boolean) -> Unit
    ) {
        val originalText = textView.tag as? String ?: textView.text.toString()
        textView.tag = originalText // Save full text in tag to reuse

        textView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (!isExpanded) {
                    if (textView.lineCount <= maxLines) return

                    val lastCharShown = textView.layout.getLineEnd(maxLines - 1)
                        .coerceAtMost(originalText.length)
                    val visibleText = originalText.substring(0, lastCharShown).trimEnd()

                    val displayText = "$visibleText... $expandText"
                    val spannable = SpannableString(displayText)

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onToggle(true) // Set to expanded
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = expandColor
                            ds.isUnderlineText = false
                            ds.isFakeBoldText = true
                        }
                    }

                    spannable.setSpan(
                        clickableSpan,
                        spannable.length - expandText.length,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    textView.text = spannable
                    textView.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    val displayText = "$originalText $collapseText"
                    val spannable = SpannableString(displayText)

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onToggle(false) // Collapse
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = collapseColor
                            ds.isUnderlineText = false
                            ds.isFakeBoldText = true
                        }
                    }

                    spannable.setSpan(
                        clickableSpan,
                        spannable.length - collapseText.length,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    textView.text = spannable
                    textView.movementMethod = LinkMovementMethod.getInstance()
                }
            }
        })
    }




}
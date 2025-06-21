package com.popiin.views

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cloudinary.Transformation
import com.popiin.R
import com.popiin.res.VenueStoryListRes
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import java.util.*

class ExoPlayerRecyclerView : RecyclerView {
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
//    private var mediaCoverImage: ImageView? = null
    var volumeControl: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var mediaContainer: FrameLayout? = null
    private var videoSurfaceView: PlayerView? = null
    var videoPlayer: ExoPlayer? = null

    /**
     * variable declaration
     */
    // Media List
    private var mediaObjects = ArrayList<VenueStoryListRes.Data>()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var myContext: Context? = null
    private var playPosition = -1
    private var isVideoViewAdded = false
    private var requestManager: RequestManager? = null

    // controlling volume state
    private var volumeState: VolumeState? = null
   // private val videoViewClickListener = OnClickListener { toggleVolume() }
  private val videoViewClickListener = OnClickListener {
      if (videoPlayer != null) {
          if (videoPlayer!!.isPlaying) {
              videoPlayer!!.pause()
          } else {
              videoPlayer!!.play()
          }
      }
  }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        this.myContext = context.applicationContext
        val display = (Objects.requireNonNull(
            getContext().getSystemService(Context.WINDOW_SERVICE)
        ) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
        videoSurfaceView = PlayerView(this.context)
        videoSurfaceView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayer.Builder(context).setPauseAtEndOfMediaItems(true).build()
        // Disable Player Control
        videoSurfaceView!!.useController = false
        // Bind the player to the view.
        videoSurfaceView!!.player = videoPlayer
        // Turn on Volume
        setVolumeControl(VolumeState.ON)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
//                    if (mediaCoverImage != null) {
//                        // show the old thumbnail
//                        mediaCoverImage!!.visibility = VISIBLE
//                    }

                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }else if(newState == SCROLL_STATE_DRAGGING){

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    playVideo(true)
                } else {
                    playVideo(false)
                }
            }
        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view) {
                    resetVideoView()
                }
            }
        })

        videoPlayer!!.addListener(object : Player.Listener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        progressBar?.visibility = VISIBLE
//                        mediaCoverImage!!.visibility = VISIBLE
                    }
                        Player.STATE_READY -> {
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.")
                        if (!isVideoViewAdded) {
                            addVideoView()
                        }
                        progressBar?.visibility = GONE
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "onPlayerStateChanged: Video ended.")
                        videoPlayer!!.seekTo(0)
                        val startPosition =
                            (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                        if (startPosition < this@ExoPlayerRecyclerView.adapter!!.itemCount)
                            this@ExoPlayerRecyclerView.smoothScrollToPosition(startPosition + 1)
                    }
                    Player.STATE_IDLE -> { }
                }
            }

            override fun onRenderedFirstFrame() {
                videoSurfaceView?.animate()
                    ?.alpha(1f)
                    ?.setDuration(500)
                    ?.setInterpolator(AccelerateDecelerateInterpolator())
                    ?.start()

                // Fade out and remove the cover image
//                mediaCoverImage?.animate()
//                    ?.alpha(0f)
//                    ?.setDuration(700)
//                    ?.withEndAction {
//                        mediaCoverImage!!.visibility = GONE
//                    }?.start()
            }

            // Other overrides...
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
    }



    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition =
                (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            // if there is more than 1 list-item on the screen
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else {
                startPosition
            }
        } else {
            targetPosition = mediaObjects.size - 1
        }
        Log.d(TAG, "playVideo: target position: $targetPosition")

        // video is already playing so return
        if (targetPosition == playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition
        if (videoSurfaceView == null) {
            return
        }

        // remove any old surface views from previously playing videos
        videoSurfaceView!!.visibility = INVISIBLE
        removeVideoView(videoSurfaceView)
        val currentPosition = targetPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as ExoRecyclerViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
//        mediaCoverImage = holder.mediaCoverImage
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
        requestManager = holder.requestManager
        mediaContainer = holder.mediaContainer
        videoSurfaceView!!.player = videoPlayer
        viewHolderParent!!.setOnClickListener(videoViewClickListener)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context, Util.getUserAgent(context, AppName)
        )
        val mediaUrl = mediaObjects[targetPosition].video_url

        val url = PrefManager.cloudinary.url()
            .resourcType(Constant().video)
            .transformation(Transformation<Transformation<*>?>().quality(Constant().auto))
            .generate(mediaObjects[targetPosition].video_id + ".jpg")
//        requestManager?.load(url)?.into(mediaCoverImage!!)

        if (mediaUrl != null) {
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
            videoPlayer!!.prepare(videoSource)
            videoPlayer!!.playWhenReady = true
        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */
    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at = playPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: $at")
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView!!.parent as? ViewGroup ?: return
        val index = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            viewHolderParent!!.setOnClickListener(null)
        }
    }

    private fun addVideoView() {
        mediaContainer!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView!!.requestFocus()
        videoSurfaceView!!.alpha = 0f
        videoSurfaceView!!.visibility = VISIBLE
//        mediaCoverImage!!.visibility = INVISIBLE

    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView!!.visibility = INVISIBLE
//            mediaCoverImage!!.visibility = VISIBLE
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    fun onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.stop(true)
        }
    }

    public fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.")
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.")
                setVolumeControl(VolumeState.OFF)
            }
        }
    }


    fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer!!.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer!!.volume = 1f
            animateVolumeControl()
        }
    }

    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
                requestManager!!.load(R.drawable.ic_speaker_off)
                    .into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
                requestManager!!.load(R.drawable.ic_speaker_on)
                    .into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
        }
    }

    fun setMediaObjects(mediaObjects: ArrayList<VenueStoryListRes.Data>) {
        this.mediaObjects = mediaObjects
    }

    /**
     * Volume ENUM
     */
    enum class VolumeState {
        ON, OFF
    }

    companion object {
        private const val TAG = "ExoPlayerRecyclerView"
        private const val AppName = "Android ExoPlayer"
    }
}

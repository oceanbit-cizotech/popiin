package com.popiin.views

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.popiin.R
import com.popiin.upload.ImageUpload
import com.popiin.upload.ImageUpload.UploadImageListener
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ImageVideoView : RelativeLayout {
    var imgClose: ImageView? = null
    var image: ImageView? = null
    var seekbar: SeekBar? = null
   private var ids: String? = null
    var listener: OnViewClickListener? = null
    var imageUrl: String? = ""

    constructor(context: Context?, listener: OnViewClickListener?, ids: String) : super(context) {
        init(listener, ids, "")
    }

    constructor(
        context: Context?,
        listener: OnViewClickListener?,
        ids: String,
        resourcesUrl: String,
    ) : super(context) {
        init(listener, ids, resourcesUrl)
    }

    private fun init(listener: OnViewClickListener?, ids: String, resourcesUrl: String) {
        this.ids = ids
        inflate(context, R.layout.row_image_view, this)
        imgClose = findViewById(R.id.img_close)
        seekbar = findViewById(R.id.seekbar)
        image = findViewById(R.id.img_event)
        imgClose!!.setOnClickListener{
            listener?.onClose(this@ImageVideoView,
                imgClose,
                ids)
        }
        image!!.setOnClickListener { listener?.onImageClick(this@ImageVideoView, image, ids) }
        if (resourcesUrl.isNotEmpty()) {
            displayView(resourcesUrl)
        }
    }

    open class OnViewClickListener {
        open fun onClose(parent: View?, child: View?, ids: String?) {}
        open fun onImageClick(parent: ImageVideoView?, child: View?, ids: String?) {}
    }

    fun uploadVideo(url: String) {
        if (url.lowercase(Locale.getDefault()).contains("pdf")) {
            Glide.with(this).load(R.drawable.ic_pdf).into(image!!)
            val file = File(url)
            imageUpload = ImageUpload(file, ids!!, uploadImageListener,image!!.context)
            imageUpload!!.startUpload()
        } else {
            Glide.with(this).load(url).into(image!!)
            val file = File(url)
            fetchDataWithCallback(file){ result ->
                imageUpload = ImageUpload(result, ids!!, uploadImageListener,image!!.context)
                imageUpload!!.startUpload()
            }
        }
    }
   private fun fetchDataWithCallback(file: File, callback: (File) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = compressImage(file)
            callback(result)
        }
    }

   private suspend fun compressImage(file: File):File{
        return Compressor.compress(context, file) {
            quality(15)
            format(Bitmap.CompressFormat.JPEG) // Specify the format
            size(2_097_152) // Set maximum size in bytes (2 MB in this example)
        }
    }

   private fun displayView(url: String) {
        imageUrl = url
        if (url.lowercase(Locale.getDefault()).contains("pdf")) {
            Glide.with(this).load(R.drawable.ic_pdf).into(image!!)
        } else {
            Glide.with(this)
                .load(url)
                .into(image!!)
        }
        seekbar!!.visibility = INVISIBLE
       imgClose!!.visibility = VISIBLE
        uploadImageListener.onUploadComplete(url)
    }

  private  var imageUpload: ImageUpload? = null
  private  var uploadImageListener: UploadImageListener = object : UploadImageListener() {
        override fun onUploadComplete(url: String?) {
            super.onUploadComplete(url)
            imageUrl = url
            seekbar!!.visibility = INVISIBLE
        }

        override fun onUploadProgress(progress: Int, id: String?) {
            super.onUploadProgress(progress, id)
            seekbar!!.progress = progress
            imgClose!!.visibility = VISIBLE
        }

    }
}

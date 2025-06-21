package com.popiin.upload

import android.os.Handler
import okhttp3.RequestBody
import kotlin.Throws
import okio.BufferedSink
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(
    private val mFile: File,
    private val content_type: String,
    private val mListener: UploadCallbacks,
    private val uploadids: String
) : RequestBody() {
    private val mPath: String? = null

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int, id: String?)
        fun onError()
        fun onFinish(url: String?)
    }

    override fun contentType(): MediaType? {
        return content_type.toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0
        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            `in`.close()
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            mListener.onProgressUpdate((100 * mUploaded / mTotal).toInt(), uploadids)
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}
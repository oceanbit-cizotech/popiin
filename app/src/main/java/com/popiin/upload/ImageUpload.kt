package com.popiin.upload

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.popiin.upload.ProgressRequestBody.UploadCallbacks
import com.popiin.ApiInterface
import com.popiin.ApiClient
import com.popiin.res.ImageRes
import com.popiin.util.PrefManager
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ImageUpload(
    private val selectedImages: File,
    private val ids: String,
    private val listener: UploadImageListener?,
    private val context: Context
) : UploadCallbacks {

    private val apiInterface: ApiInterface = ApiClient(context).client!!.create(ApiInterface::class.java)

    fun startUpload() {
        CoroutineScope(Dispatchers.IO).launch {
            uploadImage()
        }
    }

    private suspend fun uploadImage() {
        withContext(Dispatchers.IO) {
            val fileBody = ProgressRequestBody(selectedImages, "images/*", this@ImageUpload, ids)
            val image = MultipartBody.Part.createFormData("file", selectedImages.name, fileBody)

            val call = apiInterface.getEventImage(PrefManager.getAccessToken(), image)
            call?.enqueue(object : Callback<ImageRes?> {
                override fun onResponse(call: Call<ImageRes?>, response: Response<ImageRes?>) {
                    response.body()?.let { imageRes ->
                        if (imageRes.success) {
                            Handler(Looper.getMainLooper()).post {
                                listener?.onUploadComplete(imageRes.url)
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                listener?.onUploadError(ids)
                            }
                        }
                    } ?: run {
                        Handler(Looper.getMainLooper()).post {
                            listener?.onUploadError(ids)
                        }
                    }
                }

                override fun onFailure(call: Call<ImageRes?>, t: Throwable) {
                    t.printStackTrace()
                    Handler(Looper.getMainLooper()).post {
                        listener?.onUploadError(ids)
                    }
                }
            })
        }
    }

    override fun onProgressUpdate(percentage: Int, id: String?) {
        Handler(Looper.getMainLooper()).post {
            listener?.onUploadProgress(percentage, ids)
        }
    }

    override fun onError() {
        Handler(Looper.getMainLooper()).post {
            listener?.onUploadError(ids)
        }
    }

    override fun onFinish(url: String?) {
        Handler(Looper.getMainLooper()).post {
            listener?.onUploadComplete(url)
        }
    }

    abstract class UploadImageListener {
        open fun onUploadComplete(url: String?) {}
        open fun onUploadProgress(progress: Int, id: String?) {}
        open fun onUploadError(id: String?) {}
    }
}

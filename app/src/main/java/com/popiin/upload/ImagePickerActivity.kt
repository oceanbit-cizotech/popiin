package com.popiin.upload

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.popiin.R
import android.content.Intent
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import android.provider.MediaStore
import com.karumi.dexter.PermissionToken
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.DialogInterface
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.popiin.util.Constant
import com.karumi.dexter.listener.PermissionRequest
import com.yalantis.ucrop.UCrop
import java.io.File

class ImagePickerActivity : AppCompatActivity() {
    private var lockAspectRatio = false
    private var setBitmapMaxWidthHeight = false
    private var aspectRationX = 16
    private var aspectRatioY = 9
    private var bitmapMaxWidth = 1000
    private var bitmapMaxHeight = 1000
    private var imageCompression = 80

    interface PickerOptionListener {
        fun onTakeCameraSelected()
        fun onChooseGallerySelected()
        fun onCancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        val intent = intent
        if (intent == null) {
            Toast.makeText(applicationContext,
                getString(R.string.toast_image_intent_null),
                Toast.LENGTH_LONG).show()
            return
        }
        aspectRationX = intent.getIntExtra(INTENT_ASPECT_RATIO_X, aspectRationX)
        aspectRatioY = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, aspectRatioY)
        imageCompression = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, imageCompression)
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth)
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight)
        when (intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1)) {
            REQUEST_IMAGE_CAPTURE -> {
                takeCameraImage()
            }
            REQUEST_GALLERY_IMAGE -> {
                chooseImageFromGallery()
            }
            REQUEST_PDF -> {
                choosePdfFile()
            }
            else -> {
                cancel()
            }
        }
    }

    private fun takeCameraImage() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName = System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            getCacheImagePath(fileName))
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                        if (takePictureIntent.resolveActivity(packageManager) != null) {
//                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun chooseImageFromGallery() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun choosePdfFile() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = Constant().applicationPdf
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intentPDF, "ChooseFile"), REQUEST_PDF)
    }

    private fun openPDFIntent() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = Constant().applicationPdf
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intentPDF, "ChooseFile"), REQUEST_PDF)
    }

    private fun cancel() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                cropImage(getCacheImagePath(fileName))
            } else {
                setResultCancelled()
            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                val imageUri = data!!.data
                cropImage(imageUri)
            } else {
                setResultCancelled()
            }
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                handleUCropResult(data)
            } else {
                setResultCancelled()
            }
            REQUEST_PDF -> if (resultCode == RESULT_OK) {
                val uri = data!!.data
                println("PDF : URI : " + uri.toString())
                println("PDF : FILE PATH : " + getFilePathFromUri(uri, this@ImagePickerActivity))
                val intent = Intent()
                intent.putExtra(Constant().path, getFilePathFromUri(uri, this@ImagePickerActivity))
                setResult(RESULT_OK, intent)
                finish()
            } else {
                setResultCancelled()
            }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
                Log.e(TAG, "Crop error: $cropError")
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun cropImage(sourceUri: Uri?) {
        val destinationUri = Uri.fromFile(File(cacheDir, queryName(
            contentResolver, sourceUri)))
        val options = UCrop.Options()
        options.setCompressionQuality(imageCompression)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        if (lockAspectRatio) options.withAspectRatio(aspectRationX.toFloat(),
            aspectRatioY.toFloat())
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri!!, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath: Uri?) {
        val intent = Intent()
        intent.putExtra(Constant().path, imagePath)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String?): Uri {
        val path = File(externalCacheDir, Constant().camera)
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName!!)
        return FileProvider.getUriForFile(this@ImagePickerActivity, "$packageName.provider", image)
    }

    companion object {
        private val TAG = ImagePickerActivity::class.java.simpleName
        const val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        const val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        const val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        const val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        const val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        const val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        const val INTENT_BITMAP_MAX_WIDTH = "max_width"
        const val INTENT_BITMAP_MAX_HEIGHT = "max_height"
        const val REQUEST_IMAGE_CAPTURE = 0
        const val REQUEST_GALLERY_IMAGE = 1
        const val REQUEST_PDF = 2
        var fileName: String? = null
        fun showImagePickerOptions(context: Context, listener: PickerOptionListener) {
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.lbl_set_profile_photo))

            // add a list
            val animals = arrayOf(context.getString(R.string.lbl_take_camera_picture),
                context.getString(R.string.lbl_choose_from_gallery),
                context.getString(R.string.lbl_cancel))
            builder.setItems(animals) { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> listener.onTakeCameraSelected()
                    1 -> listener.onChooseGallerySelected()
                    2 -> listener.onCancel()
                }
            }

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }

        fun getFilePathFromUri(_uri: Uri?, myContext: Context): String? {
            val filePath: String?
            if (_uri != null && "content" == _uri.scheme) {
                val cursor = myContext.contentResolver.query(_uri,
                    arrayOf(MediaStore.Images.ImageColumns.DATA),
                    null,
                    null,
                    null)
                cursor!!.moveToFirst()
                filePath = cursor.getString(0)
                cursor.close()
            } else {
                filePath = _uri!!.path
            }
            return filePath
        }

        private fun queryName(resolver: ContentResolver, uri: Uri?): String {
            val returnCursor = resolver.query(uri!!, null, null, null, null)!!
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

        /**
         * Calling this will delete the images from cache directory
         * useful to clear some memory
         */
        fun clearCache(context: Context) {
            val path = File(context.externalCacheDir, Constant().camera)
            if (path.exists() && path.isDirectory) {
                for (child in path.listFiles()!!) {
                    child.delete()
                }
            }
        }
    }
}
package com.popiin.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


object AttachmentHelper {
//    const val ATTACHMENT_CHOICE_CHOOSE_IMAGE = 0x0301
//    const val ATTACHMENT_CHOICE_TAKE_PHOTO = 0x0302
//    const val ATTACHMENT_CHOICE_CHOOSE_FILE = 0x0303
//    const val ATTACHMENT_CHOICE_CHOOSE_VIDEO = 0x0304
//    const val ATTACHMENT_CHOICE_LOCATION = 0x0305
//    const val ATTACHMENT_CHOICE_INVALID = 0x0306
//    const val ATTACHMENT_CHOICE_RECORD_VIDEO = 0x0307
//    const val ATTACHMENT_CHOICE_CHOOSE_TEXT_FILE = 0x0308
//    const val ATTACHMENT_CHOICE_CHOOSE_PDF_FILE = 0x0309
//    private val IMAGE_DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
//    private val mimeTypes = arrayOf(
//        "application/pdf"
//    )

//    fun invokeAttachFileIntent(mActivity: Activity, attachmentChoice: Int) {
//        val intent = Intent()
//        var chooser = false
//        when (attachmentChoice) {
//            ATTACHMENT_CHOICE_CHOOSE_IMAGE -> {
//                intent.action = Intent.ACTION_GET_CONTENT
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//                intent.type = "image/*"
//                chooser = true
//            }
//            ATTACHMENT_CHOICE_CHOOSE_VIDEO -> {
//                intent.action = Intent.ACTION_GET_CONTENT
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//                intent.type = "video/*"
//                chooser = true
//            }
//            ATTACHMENT_CHOICE_RECORD_VIDEO -> intent.action = MediaStore.ACTION_VIDEO_CAPTURE
//            ATTACHMENT_CHOICE_TAKE_PHOTO -> {
//                val uri = Uri.fromFile(File(mActivity.cacheDir.absolutePath,
//                    "Camera/IMG_" + IMAGE_DATE_FORMAT.format(
//                        Date()) + ".jpg"))
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                intent.action = MediaStore.ACTION_IMAGE_CAPTURE
//            }
//            ATTACHMENT_CHOICE_CHOOSE_FILE -> {
//                chooser = true
//                intent.type = "*/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                intent.action = Intent.ACTION_GET_CONTENT
//            }
//            ATTACHMENT_CHOICE_CHOOSE_PDF_FILE -> {
//                chooser = true
//                intent.type = "application/pdf"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                intent.action = Intent.ACTION_GET_CONTENT
//            }
//            ATTACHMENT_CHOICE_CHOOSE_TEXT_FILE -> {
//                chooser = true
//                //                intent.setType("application/*");
//                intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
//                if (mimeTypes.isNotEmpty()) {
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                }
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                intent.action = Intent.ACTION_GET_CONTENT
//            }
//        }
//        val context = mActivity ?: return
//        if (intent.resolveActivity(context.packageManager) != null) {
//            if (chooser) {
//                mActivity.startActivityForResult(
//                    Intent.createChooser(intent, "Perform action with"),
//                    attachmentChoice)
//            } else {
//                mActivity.startActivityForResult(intent, attachmentChoice)
//            }
//        } else {
//            Toast.makeText(context, "No Application Found.", Toast.LENGTH_LONG).show()
//        }
//    }

    @Throws(Exception::class)
    fun saveFileIntoExternalStorageByUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalSize = inputStream!!.available()
        val bis: BufferedInputStream?
        val bos: BufferedOutputStream?
        val fileName = getFileName(context, uri)
        val file = makeEmptyFileIntoExternalStorageWithTitle(context, fileName)
        bis = BufferedInputStream(inputStream)
        bos = BufferedOutputStream(FileOutputStream(
            file, false))
        val buf = ByteArray(originalSize)
        bis.read(buf)
        do {
            bos.write(buf)
        } while (bis.read(buf) != -1)
        bos.flush()
        bos.close()
        bis.close()
        return file
    }

    @SuppressLint("Range", "Recycle")
    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun makeEmptyFileIntoExternalStorageWithTitle(context: Context, title: String?): File {
        val root = context.cacheDir.absolutePath
        return File(root, title!!)
    }

    @Throws(Exception::class)
    fun getFileFromUri(activity: Activity, uri: Uri?): File {
        return if (isGoogleDrive(uri!!)) // check if file selected from google drive
        {
            saveFileIntoExternalStorageByUri(activity, uri)
        } else {
            val path =
                getFilePathFromUri(Uri.parse(uri.toString()), activity)
            println("AttachmentHelper : getFileFromUri : path : $path")
            if (path != null) {
                File(Uri.parse(path).path!!)
            } else {
                saveFileIntoExternalStorageByUri(activity, uri)
            }
        }
    }

    private fun getFilePathFromUri(_uri: Uri?, myContext: Context): String? {
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


    //    public static boolean isGoogleDrive(Uri uri) {
    //        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    //    }
    private fun isGoogleDrive(uri: Uri): Boolean {
        return uri.authority!!.contains(Constant().drivePath)
    }

//    fun getDataColumn(
//        context: Context, uri: Uri?, selection: String?,
//        selectionArgs: Array<String?>?,
//    ): String? {
//        var cursor: Cursor? = null
//        val column = "_data"
//        val projection = arrayOf(
//            column
//        )
//        try {
//            cursor =
//                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val column_index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(column_index)
//            }
//        } catch (e: Exception) {
//            return null
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    fun getRealPathFromUri(activity: Activity, contentUri: Uri?): String {
//        val proj = arrayOf(MediaStore.MediaColumns.DATA)
//        val cursor = activity.managedQuery(contentUri, proj, null, null, null)
//        println("AttachmentHelper : getRealPathFromUri : getColumnNames : " + listOf(*cursor.columnNames)
//            .toString())
//        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//        cursor.moveToFirst()
//        println("AttachmentHelper : getRealPathFromUri : _data : " + cursor.getString(column_index))
//        return cursor.getString(column_index)
//    }
//
//    fun getRealPathFromUri2(activity: Activity, contentUri: Uri?): String? {
//        var path: String? = null
//        val proj = arrayOf(MediaStore.MediaColumns.DATA)
//        val cursor = activity.contentResolver.query(contentUri!!, null, null, null, null)
//        if (cursor != null) {
//            println("AttachmentHelper : getRealPathFromUri : getColumnNames : " + Arrays.asList(*cursor.columnNames)
//                .toString())
//            cursor.moveToFirst()
//            // Loop in the cursor to get each row.
//            do {
//                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//                path = cursor.getString(column_index)
//                println("AttachmentHelper : getRealPathFromUri : _data : $path")
//            } while (cursor.moveToNext())
//        }
//        cursor!!.moveToFirst()
//        return path
//    }
}
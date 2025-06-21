package com.popiin.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.io.File
import java.net.URISyntaxException

open class PathUtil {
    @SuppressLint("NewApi", "Recycle")
    @Throws(URISyntaxException::class)
    fun getPath(context: Context, pathUri: Uri): String? {
        var uri = pathUri
        val needToCheckUri = true
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                when (split[0]) {
                    Constant().image -> {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    Constant().video -> {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    Constant().audio -> {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if (Constant().content.equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor?
            try {
                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (Constant().file.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getFilePathFromURI(context: Context, contentUri: Uri): String? {
        //copy file and send new file path
        val fileName: String? = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val TEMP_DIR_PATH = Environment.getExternalStorageDirectory().path
            val copyFile = File(TEMP_DIR_PATH + File.separator.toString() + fileName)
            return copyFile.absolutePath
        }
        return null
    }

    @SuppressLint("Recycle")
    open fun getPathFromURI(context: Context, contentUri: Uri): String? {
//        if (contentUri.toString().indexOf("file:///") > -1) {
//            return contentUri.path
//        }
//        var cursor: Cursor? = null
//        return try {
//            val proj = arrayOf(MediaStore.Video.Media.DATA)
//            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
//            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            cursor.moveToFirst()
//            cursor.getString(column_index)
//        } finally {
//            cursor?.close()
//        }

        val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.contentResolver.query(contentUri, null, null, null)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
            return cursor.getString(idx)
        }
    }

    open fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
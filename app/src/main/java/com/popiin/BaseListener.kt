package com.popiin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ParseException
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.databinding.DialogLoaderBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.BranchIOListener
import com.popiin.req.UpdateLinkReq
import com.popiin.res.CommonRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


interface BaseListener {
    var TAG: String
        get() = "BaseListener"
        set(value) = TODO()

    fun isInternetConnect(): Boolean
    fun isValidEmailId(email: String?):Boolean
    fun isValidPhoneNUmber(phone: String):Boolean
    fun isValidTime(startTime:String,endTime:String):Boolean
    fun isValidDate(startDate:String,endDate:String)
    fun showProgress()
    fun hideProgress()
    fun hideKeyBoard(context: Activity?)
    fun onApiFailure(t: Throwable)


    val pageLimit: Int
        get() = 20
    val defaultPage: Int
        get() = 0

    val rateDuration: Int
        get() = 30

    fun getBaseActivity(): BaseActivity<*>?

    fun initProgress(dialogLoaderBinding: DialogLoaderBinding): Dialog? {
        val progressDialog =
            Dialog(getBaseActivity()!!, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.window!!
            .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window!!.setGravity(Gravity.CENTER)
        progressDialog.window!!.setDimAmount(0.5f)


        progressDialog.setContentView(dialogLoaderBinding.root)
        return progressDialog
    }
    fun showErrorMessage(message: String?){
      val commonDialog = CommonDialog(getBaseActivity()!!.baseContext,
            "OK","","", message!!)
        commonDialog.binding.btnPositive.setOnClickListener {
            commonDialog.dismiss()
        }
        commonDialog.show()
    }

    fun showToast(message: String?) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show()
    }

    fun minutesBetween(startTime: Long, endTime: Long): Long {
        return TimeUnit.MILLISECONDS.toMinutes(abs(endTime - startTime))
    }

    val baseBranchUrl: String
        get() = Constant().shareLink

    fun shareBranchIOLink(
        properties: LinkProperties,
        title: String?,
        description: String?,
        imageUrl: String?,
        isShare: Boolean,
        listener: BranchIOListener?,
    ) {
        properties.setChannel(getBaseActivity()!!.getString(R.string.app_name))
            .setFeature(getBaseActivity()!!.getString(R.string.app_name))
            .addControlParameter("\$ios_url", baseBranchUrl)
            .addControlParameter("\$android_url", baseBranchUrl)
            .addControlParameter("\$ios_deeplink_path", baseBranchUrl)
            .addControlParameter("\$android_deeplink_path", baseBranchUrl)
        val buo = BranchUniversalObject()
            .setCanonicalIdentifier(UUID.randomUUID().toString())
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
        if (title != null) {
            buo.setTitle(title)
                .setContentDescription(description)
                .setContentImageUrl(imageUrl!!)
        }
        buo.generateShortUrl(getBaseActivity()!!, properties
        ) { url, error ->
            if (error == null) {
                listener?.onLinkCreate(url)
                if (isShare) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = Constant().sharePattern
                    if (title == null) {
                        intent.putExtra(Intent.EXTRA_TEXT, "$description")
                    } else {
                        intent.putExtra(Intent.EXTRA_TEXT, url)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    getBaseActivity()!!.startActivity(Intent.createChooser(intent, Constant().share))
                }
            } else {
                Log.e("BASE", "onLinkCreate: Branch error: " + error.message)
                listener?.onLinkCreateError(error)
            }
        }
    }


    fun shareBranchIOLink(properties: LinkProperties?, description: String?) {
        shareBranchIOLink(properties!!, null, description!!, null, true, null)
    }

    fun shareBranchIOLink(
        properties: LinkProperties?,
        description: String?,
        isShare: Boolean,
        listener: BranchIOListener?,
    ) {
        shareBranchIOLink(properties!!, null, description!!, null, isShare, listener)
    }

    fun shareBranchIOLink(properties: LinkProperties?, listener: BranchIOListener?) {
        shareBranchIOLink(properties!!, null, null, null, false, listener)
    }

    fun shareMessageBuilder(type:Int,title: String?,url:String){
        if(type==SHARE_MESSAGE_TYPE.VENUE){
            val shareBody = getBaseActivity()!!.getString(R.string.lbl_venue_share) +" "+ title+ "\n" +url
            shareMessage(shareBody)
        }else if(type==SHARE_MESSAGE_TYPE.EVENT){
            val shareBody = getBaseActivity()!!.getString(R.string.lbl_event_share) +" "+ title+ "\n" +url
            shareMessage(shareBody)
        }
    }

    fun shareMessage(message: String?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = Constant().sharePattern
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
        getBaseActivity()!!.startActivity(Intent.createChooser(sharingIntent, Constant().shareVia))
    }

    fun copyCode(context: Activity?,label:String?,message: String?){
        val clipboard: ClipboardManager =
            context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(Constant().label, label)

        clipboard.setPrimaryClip(clip)
        showToast(message)
    }

    @SuppressLint("SimpleDateFormat")
    fun getAge(dobString: String?): Int {
        var date: Date? = null
        val sdf = SimpleDateFormat(Constant().yyyyMmDd)
        try {
            date = sdf.parse(dobString!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) return 0
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.time = date
        val year = dob[Calendar.YEAR]
        val month = dob[Calendar.MONTH]
        val day = dob[Calendar.DAY_OF_MONTH]
        dob[year, month + 1] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    fun getCurrentCalenderWithStartOfDay(): Calendar {
        return getCurrentCalenderWithStartOfDay(Calendar.getInstance())!!
    }

    fun getCurrentDateWithStartOfDay(): Date? {
        return getCurrentCalenderWithStartOfDay().time
    }

    fun getCurrentCalenderWithStartOfDay(calendar: Calendar): Calendar? {
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.AM_PM] = Calendar.AM
        return calendar
    }


    fun getCopyOfCalender(calendarOriginal: Calendar): Calendar {
        val calendarCopy = Calendar.getInstance()
        calendarCopy.timeInMillis = calendarOriginal.timeInMillis
        return calendarCopy
    }

    fun getGivenDateWithFormat(input: String?, format: String?): Calendar {
        val returnDate = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        try {
            returnDate.time = sdf.parse(input!!)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return getCurrentCalenderWithStartOfDay(returnDate)!!
    }

    fun shareImageUri(uri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        getBaseActivity()!!.startActivity(intent)
    }


    @SuppressLint("SimpleDateFormat")
    fun saveImageAndFileProviderUri(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(getBaseActivity()!!.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
            val mImageName = "MI_$timeStamp.jpg"
            val file = File(imagesFolder, mImageName)
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(getBaseActivity()!!,
                getBaseActivity()!!.applicationContext.packageName + ".provider",
                file)
        } catch (e: IOException) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

}
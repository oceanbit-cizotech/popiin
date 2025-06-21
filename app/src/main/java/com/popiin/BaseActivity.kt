package com.popiin

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.popiin.databinding.DialogLoaderBinding
import com.popiin.dialog.CheckInternetConnectionDialog
import com.popiin.dialog.CommonDialog
import com.popiin.exceptions.NoConnectivityException
import com.popiin.util.CheckNetwork
import com.popiin.util.Common
import com.popiin.util.DateTimePicker
import com.popiin.util.LogHandler
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.regex.Pattern


abstract class BaseActivity<DB : ViewDataBinding> :AppCompatActivity(),BaseListener{
    lateinit var binding: DB
    lateinit var mActivity: BaseActivity<DB>
    lateinit var apiInterface: ApiInterface
    private var progressDialog: Dialog? = null
    private lateinit var dialogLoaderBinding: DialogLoaderBinding
    var checkInternetConnectionDialog: CheckInternetConnectionDialog? = null
    private lateinit var dateTimePickerBuilder: DateTimePicker.DateTimePickerBuilder
    abstract fun getLayout(): Int
    var common: Common = Common.instance!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        binding = DataBindingUtil.setContentView(this, getLayout())

        dialogLoaderBinding = DataBindingUtil.inflate(LayoutInflater.from(getBaseActivity()),
            R.layout.dialog_loader,
            null,
            false)

        val animationDrawable = dialogLoaderBinding.imageView.drawable as AnimationDrawable
        animationDrawable.start()

        Glide.with(mActivity).load(R.drawable.popiin_loader).into(dialogLoaderBinding.imgLoader)

        apiInterface = ApiClient(mActivity).client!!.create(ApiInterface::class.java)
        progressDialog = initProgress(dialogLoaderBinding)
        dateTimePickerBuilder = DateTimePicker.DateTimePickerBuilder(getBaseActivity())
        initViews()

    }

     abstract fun initViews()

     override fun isValidTime(startTime: String, endTime: String): Boolean {
         return true
     }

     override fun isValidDate(startDate: String, endDate: String) {
         println("")
     }

     override fun isValidPhoneNUmber(phone: String): Boolean {
         return true
     }

    override fun isInternetConnect(): Boolean {
        return  CheckNetwork.isInternetAvailable(this)
     }


    override fun showProgress() {
        if (progressDialog != null && !isFinishing) {
//            (dialogLoaderBinding.imgLoader.background as GifAnimationDrawable).start()
            progressDialog!!.show()
        }
    }

    override fun onApiFailure(throwable : Throwable) {
        try {
            hideProgress()

            when (throwable) {
                is NoConnectivityException ->{
                    isInternetConnect()
                }
                is UnknownHostException -> {
                    // Handle case when the host (server) is unreachable
                    showErrorDialog("No Internet Connection", "Please check your internet and try again.")
                }
                is ConnectException -> {
                    // Handle case when connection is refused or server is unreachable
                    showErrorDialog("Server Unreachable", "We are unable to connect to the server. Try again later.")
                }
                is SocketTimeoutException -> {
                    // Handle request timeout
                    showErrorDialog("Request Timed Out", "The server is taking too long to respond. Try again later.")
                }
                is IOException -> {
                    // General network issue
                    showErrorDialog("Network Error", "Something went wrong with the network. Check your internet.")
                }
                else -> {
                    // Other unexpected errors
                    showErrorDialog("Error", throwable.localizedMessage ?: "An unknown error occurred.")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showErrorDialog(title: String,msg:String){
      var  commonDialog = CommonDialog(
            mActivity,
            getString(R.string.lbl_ok),
            "",
            title,
            msg
        )
        commonDialog.show()
        commonDialog.binding.btnPositive.setOnClickListener {
            commonDialog.dismiss()
            mActivity.finish()
        }
    }

    override fun hideProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

     override fun getBaseActivity(): BaseActivity<DB>? {
         return this
     }

     override fun isValidEmailId(email: String?): Boolean {
        val expression = "[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-zA-Z]{2,4}"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = email?.let { pattern.matcher(it) }
        return matcher?.matches() ?: false
    }


     open fun setFragment(fragment: Fragment?, fragmentName: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.frame_layout, fragment!!)
            .commit()
         //            .addToBackStack(fragmentName)
    }

    open fun isValidResponse(response: Response<*>?): Boolean {
        if (response!!.code() == 200) {
            LogHandler.writeLog(mActivity, "isValidResponse onResponse 200 ", "INFO")
            if (response.body()  != null) {
                return true
            } else {
                LogHandler.writeLog(mActivity, "isValidResponse onResponse 200 else parth ", "INFO")
                showToast(getString(R.string.something_went_wrong_try_again))
            }
        } else if (response.code() == 401) {
            LogHandler.writeLog(mActivity, "isValidResponse onResponse 401 ", "INFO")
            showToast(getString(R.string.something_went_wrong_try_again))
        } else if (response.code() == 404) {
            LogHandler.writeLog(mActivity, "isValidResponse onResponse 404 ", "INFO")
            showToast(getString(R.string.something_went_wrong_try_again))
        } else if (response.code() == 500) {
            LogHandler.writeLog(mActivity, "isValidResponse onResponse 500 ", "INFO")
            showToast(getString(R.string.internal_server_error))
        } else {
            LogHandler.writeLog(mActivity!!, "isValidResponse onResponse code "+response.code(), "INFO")
            showToast(getString(R.string.something_went_wrong_try_again))
        }
        return false
    }

    override fun hideKeyBoard(context: Activity?) {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = context.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(context)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
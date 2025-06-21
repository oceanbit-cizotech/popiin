package com.popiin

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.popiin.dialog.CommonDialog
import com.popiin.exceptions.NoConnectivityException
import com.popiin.util.CheckNetwork
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.regex.Pattern


abstract class BaseBottomSheetDialog<DB : ViewDataBinding?> : BottomSheetDialogFragment(), BaseListener {
    var mActivity: BaseActivity<*>? = null
    var binding: DB? = null

    abstract fun getLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setCanceledOnTouchOutside(isDismissOnTouchOutside)
        dialog.setOnShowListener {
//            val bottomSheet: FrameLayout? =
//                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)

            // =               BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            val behavior: BottomSheetBehavior<*> = dialog.behavior
            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isHideable = false // Prevent hiding
            behavior.peekHeight = 0 // Always fully expanded
            behavior.isDraggable = false // Disable dragging (optional)

            if (isDisableHalfExpanded) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    protected abstract fun initViews()
    private val isDisableHalfExpanded: Boolean
        get() = false
    private val isDismissOnTouchOutside: Boolean
        get() = true

    override fun showProgress() {
        mActivity!!.showProgress()
    }

    override fun hideProgress() {
        mActivity!!.hideProgress()
    }

    override fun isValidTime(startTime: String, endTime: String): Boolean {
        return true
    }

    override fun isValidDate(startDate: String, endDate: String) {
        println("")
    }

    override fun isValidPhoneNUmber(phone: String): Boolean {
        return true
    }

    override fun getBaseActivity(): BaseActivity<*>? {
        return mActivity
    }
    override  fun isInternetConnect(): Boolean {
        var isInternet= CheckNetwork.isInternetAvailable(mActivity!!.baseContext)
        if(isInternet){
            return true
        }else {
          val  commonDialog = CommonDialog(
                mActivity!!,
                getString(R.string.lbl_ok),
                "",
                "",
                getString(R.string.noInternetConnection)
            )
            commonDialog!!.show()
            commonDialog!!.binding.btnPositive.setOnClickListener {
                commonDialog!!.dismiss()
                mActivity!!.finish()
            }
            return false
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
                    isInternetConnect()
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
            mActivity!!,
            getString(R.string.lbl_ok),
            "",
            title,
            msg
        )
        commonDialog.show()
        commonDialog.binding.btnPositive.setOnClickListener {
            commonDialog.dismiss()
            mActivity!!.finish()
        }
    }
    fun isValidResponse(response: Response): Boolean {
        if (response.code == 200) {
            if (response.body != null) {
                return true
            } else {
                showToast(mActivity!!.getString(R.string.something_went_wrong_try_again))
            }
        } else if (response.code == 401) {
            showToast(mActivity!!.getString(R.string.something_went_wrong_try_again))
           // openLoginScreenFinishOther(requireActivity())
        } else if (response.code == 404) {
            showToast(mActivity!!.getString(R.string.something_went_wrong_try_again))
        } else if (response.code == 500) {
            showToast(mActivity!!.getString(R.string.internal_server_error))
        } else {
            showToast(mActivity!!.getString(R.string.something_went_wrong_try_again))
        }
        return false
    }

    fun isEmpty(editText: EditText, message: String?, errorMessage: TextView): Boolean {
        return if (editText.text.toString().isEmpty()) {
            errorMessage.visibility = View.VISIBLE
            errorMessage.text = message
            true
        } else {
            false
        }
    }

    override fun isValidEmailId(email: String?): Boolean {
        val expression = "[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-zA-Z]{2,4}"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = email?.let { pattern.matcher(it) }
        return matcher?.matches() ?: false
    }

    override fun hideKeyBoard(context: Activity?) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = context?.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(context)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected open fun setFragment(fragment: Fragment?, fragmentName: String?) {
        val transaction = childFragmentManager.beginTransaction()
        transaction
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.frame_layout, fragment!!)
             .addToBackStack(null)
            .commit()
    }
}
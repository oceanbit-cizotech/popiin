package com.popiin

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.DialogTitle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.popiin.realm.RealmController
import com.popiin.util.Common
import com.popiin.util.DateTimePicker
import com.popiin.req.UpdateLinkReq
import com.popiin.res.CommonRes
import com.popiin.util.PrefManager
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import com.popiin.dialog.CommonDialog
import com.popiin.exceptions.NoConnectivityException
import com.popiin.req.UpdateVenueReq
import com.popiin.util.CheckNetwork
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class BaseFragment<DB : ViewDataBinding> : Fragment(),BaseListener{

    lateinit var binding: DB
    var mActivity: BaseActivity<*>? = null
    lateinit var apiInterface : ApiInterface
    protected abstract fun getLayout(): Int
    var common: Common = Common.instance!!
    private lateinit var dateTimePickerBuilder: DateTimePicker.DateTimePickerBuilder
    var realm: Realm? = null
    var realmController: RealmController? = null
    var  commonDialog:CommonDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as BaseActivity<*>?
        apiInterface = ApiClient(requireContext()).client!!.create(ApiInterface::class.java)  // ✅ Fix
        dateTimePickerBuilder = DateTimePicker.DateTimePickerBuilder(getBaseActivity())
        realmController = RealmController.with(this)
        realm = RealmController.with(this)!!.realm
    }

    abstract fun initViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun isValidTime(startTime: String, endTime: String): Boolean {
        return true
    }

    override fun isValidDate(startDate: String, endDate: String) {
    }

    override fun isValidPhoneNUmber(phone: String): Boolean {
        return true
    }
    override  fun isInternetConnect(): Boolean {
        var isInternet= CheckNetwork.isInternetAvailable(mActivity!!.baseContext)
        if(isInternet){
            return true
        }else {
            commonDialog = CommonDialog(
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
        commonDialog = CommonDialog(
            mActivity!!,
            getString(R.string.lbl_ok),
            "",
            title,
            msg
        )
        commonDialog!!.show()
        commonDialog!!.binding.btnPositive.setOnClickListener {
            commonDialog!!.dismiss()
            mActivity!!.finish()
        }
    }

    override fun showProgress() {
        mActivity!!.showProgress()
    }

    override fun hideProgress() {
        mActivity!!.hideProgress()
    }


    override fun getBaseActivity(): BaseActivity<*>? {
        return mActivity
    }

    override fun isValidEmailId(email: String?): Boolean {
        val expression = "[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-zA-Z]{2,4}"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = email?.let { pattern.matcher(it) }
        return matcher?.matches() ?: false
    }

    open fun setFragmentWithStack(fragment: Fragment?, tag: String?) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,R.anim.fade_in, R.anim.fade_out)
        transaction.replace(R.id.frame_layout, fragment!!)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    open fun setFragmentAdd(fragment: Fragment?, tag: String?) {
        val fragmentManager = mActivity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,R.anim.fade_in, R.anim.fade_out)
        transaction?.add(R.id.frame_layout, fragment!!)
        transaction?.addToBackStack(tag)
        transaction?.commit()
    }

    open fun backPressedWithFragment(tag:String){
        val fragment = mActivity?.supportFragmentManager?.fragments
        var createFragment: Fragment? = null
        fragment?.forEach { it->
            if(it.javaClass.simpleName.equals(tag)) {
                createFragment = it
            }
        }
        val manager = mActivity?.supportFragmentManager
        val trans = manager?.beginTransaction()
        createFragment?.let { trans?.remove(it) }
        trans?.commit()
        manager?.popBackStack()
    }


    open fun setBackPressed() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mActivity!!.supportFragmentManager.popBackStack()
                return@OnKeyListener true
            }
            false
        })
    }

    open fun isValidResponse(response: Response<*>?): Boolean {
        if (response!!.code() == 200) {
            if (response.body() != null) {
                return true
            } else {
                showToast(getString(R.string.something_went_wrong_try_again))
            }
        } else if (response.code() == 401) {
            showToast(getString(R.string.something_went_wrong_try_again))
        } else if (response.code() == 404) {
            showToast(getString(R.string.something_went_wrong_try_again))
        } else if (response.code() == 500) {
            showToast(getString(R.string.internal_server_error))
        } else {
            showToast(getString(R.string.something_went_wrong_try_again))
        }
        return false
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

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun callPostUpdateLinkApi(id: Int, url: String) {
        if(isInternetConnect()) {
            val call: Call<CommonRes?>? = apiInterface.postUpdateLink(
                PrefManager.getAccessToken(),
                UpdateLinkReq("" + id, url)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {

                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                  //  onApiFailure(throwable = t)
                }
            })
        }
    }

    fun callPostVenueUpdateLinkApi(id: Int, url: String) {
        if(isInternetConnect()) {
            val call: Call<CommonRes?>? = apiInterface.postUpdateVenueLink(
                PrefManager.getAccessToken(),
                UpdateVenueReq("" + id, url)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {

                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                      onApiFailure(throwable = t)
                }
            })
        }
    }

}
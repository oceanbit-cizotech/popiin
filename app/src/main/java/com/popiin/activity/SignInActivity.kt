package com.popiin.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.multidex.BuildConfig
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.annotation.Languages
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.ActivitySignInBinding
import com.popiin.listener.InfoDialogListener
import com.popiin.req.LoginReq
import com.popiin.res.LoginRes
import com.popiin.util.PrefManager
import com.popiin.util.LogHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : BaseActivity<ActivitySignInBinding>(),
    CompoundButton.OnCheckedChangeListener {
    lateinit var email : String
    lateinit var password : String

     var tag:String=SignInActivity::class.java.simpleName
    override fun getLayout(): Int {
        return R.layout.activity_sign_in
    }

    override fun initViews() {
        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        binding.chkPassword.setOnCheckedChangeListener(this)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher
        }

        binding.btnLogIn.setOnClickListener {
            if (isValidate()){
                callLoginApi()
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(mActivity,SignUpActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(mActivity, ForgotPasswordActivity::class.java))
            if (!isFinishing) {
                finish()
            }
        }

        binding.ivGoogle.setOnClickListener{
        }

        common.handleErrorView(binding.edtPassword, binding.tvMsgPassword )
        common.handleErrorView(binding.edtEmailAddress, binding.tvMsgEmail)

        if (BuildConfig.DEBUG) {
            binding.edtEmailAddress.text="barri.alexander@gmail.com"
            binding.edtPassword.text="ouQHeJ"
        }

     /*    binding.edtEmailAddress.text="jignesh.sapra@gmail.com"
        binding.edtPassword.text="123456"*/

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isFinishing) {
                    finishAffinity()
                }
            }
        })

       // initSocialLogin()


    }


    private fun isValidate(): Boolean {
        var isValid = true
        email = binding.edtEmailAddress.text.toString().trim()
        password = binding.edtPassword.text.toString().trim()


        if (email.isEmpty()) {
            isValid = false
            binding.tvMsgEmail.visibility = View.VISIBLE
            binding.tvMsgEmail.text = getString(R.string.txt_err_enter_email)
        } else if (!isValidEmailId(email)) {
            isValid = false
            binding.tvMsgEmail.visibility = View.VISIBLE
            binding.tvMsgEmail.text = getString(R.string.txt_err_valid_email)
        }

        if (password.isEmpty()){
            isValid = false
            binding.tvMsgPassword.visibility = View.VISIBLE
            binding.tvMsgPassword.text = getString(R.string.txt_err_enter_password)
        }

        return isValid


    }


    private fun callLoginApi() {
        if (isInternetConnect()) {
            showProgress()

            val loginReq=LoginReq(
                email = binding.edtEmailAddress.getText().toString(),
                password =  binding.edtPassword.getText().toString(),
                device_token = PrefManager.getFirebaseToken()!!,
                lang = Languages.ENGLISH)

            val call: Call<LoginRes> = apiInterface.doLogin(loginReq)
            call.enqueue(object : Callback<LoginRes> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    LogHandler.writeLog(mActivity!!, "${tag} onResponse ", "INFO")
                    if (isValidResponse(response)) {
                        LogHandler.writeLog(mActivity!!, "${tag} isValidResponse isValidResponse true ", "INFO")
                        if (response.body()!!.success) {
                            PrefManager.setAccessToken("Bearer " + response.body()!!.token)
                            PrefManager.setUser(response.body()!!)
                            PrefManager.setEmailVerify(response.body()!!.user!!.status)
                            PrefManager.setUserId("" + response.body()!!.user!!.id)
                            PrefManager.setPassword(binding.edtPassword.getText().toString())
                            PrefManager.setHasEvent(response.body()!!.user!!.hasEvent)
                            PrefManager.setHasVenue(response.body()!!.user!!.hasVenue)
                            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                            startActivity(intent)
                            if (!isFinishing) {
                                finishAffinity()
                            }
                        } else {
                            val commonInfoBottomFragment =
                                CommonInfoBottomFragment(ContextCompat.getDrawable(mActivity,
                                    R.drawable.ic_failed),
                                    getString(R.string.txt_sorry),
                                    response.body()!!.message,
                                    "",
                                    getString(R.string.txt_ok),
                                    ContextCompat.getColor(mActivity, R.color.colorBlack),
                                    ContextCompat.getColor(mActivity, R.color.colorBlack),
                                    true,
                                    infoDialogListener)
                            commonInfoBottomFragment.isCancelable = false
                            commonInfoBottomFragment.show(mActivity.supportFragmentManager, "")

                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    LogHandler.writeLog(mActivity!!, "${tag} onFailure "+t.printStackTrace(), "INFO")
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod()
        } else {
            binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        }

        if (binding.edtPassword.text.isEmpty() || binding.tvMsgPassword.visibility == View.VISIBLE) {
            binding.tvMsgPassword.visibility = View.VISIBLE
        }

        if (binding.edtPassword.getText().toString().isNotEmpty()) {
            binding.edtPassword.setSelection(binding.edtPassword.getText().toString().length)
        }
    }

    var infoDialogListener: InfoDialogListener = object : InfoDialogListener() {

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

    }



 /*   private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                socialLoginCallback?.onGoogleLoginResult(
                    GoogleLoginResult(account.id!!,account.email!!,account.givenName!!,account.givenName!!,account.photoUrl.toString()),
                    true )
            }
        }
    }*/
}
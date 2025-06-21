package com.popiin.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.annotation.Languages
import com.popiin.databinding.ActivitySignUpBinding
import com.popiin.dialog.CommonDialog
import com.popiin.req.SignUpReq
import com.popiin.res.LoginRes
import com.popiin.services.MyNotificationPublisher
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SignUpActivity : BaseActivity<ActivitySignUpBinding>() , CompoundButton.OnCheckedChangeListener {
    private lateinit var name: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confPassword: String
    private lateinit var genderName: String
    private var genderId: Int = 0
    private var isTermChecked = 0
    private var dob: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_sign_up
    }

    override fun initViews() {

        val gender = resources.getStringArray(R.array.Gender)
        val genderStringArray = resources.getStringArray(R.array.GenderValues)
        val genderIntArray = genderStringArray.map { it.toInt() }.toIntArray()
        val adapter = ArrayAdapter(this, R.layout.spin_gender, gender)
        binding.spinGender.adapter = adapter


        val ss = SpannableString(getString(R.string.txt_join_terms))
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mActivity, TermsActivity::class.java))
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(mActivity, R.color.colorBlue)
            }
        }

        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mActivity, PrivacyActivity::class.java))
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(mActivity, R.color.colorBlue)
            }
        }

        ss.setSpan(clickableSpan1, 40, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpan2, 57, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply Bold StyleSpan
      //  ss.setSpan(StyleSpan(Typeface.BOLD), 40, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      //  ss.setSpan(StyleSpan(Typeface.BOLD), 57, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTermsConditionJoin.text = ss
        binding.tvTermsConditionJoin.movementMethod = LinkMovementMethod.getInstance()


        binding.spinGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                genderId =genderIntArray[binding.spinGender.selectedItemPosition]
                genderName = gender[position].toString()
                if (genderId > 0) {
                    binding.tvMsgGender.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.edtDob.setOnClickListener {
            setDobDate()
        }

        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        binding.edtConfPassword.transformationMethod = PasswordTransformationMethod()

        binding.chkPassword.setOnCheckedChangeListener(this)
        binding.chkConfirmPsw.setOnCheckedChangeListener(this)

        binding.tvLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCreateAccount.setOnClickListener {
            if (isValidate()){
                callSignUpApi()
            }
        }

        binding.chkTerms.setOnCheckedChangeListener { _, isChecked ->
            isTermChecked = if (isChecked) {
                1
            } else {
                0
            }
        }


        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        common.handleErrorView(binding.edtFirstname, binding.tvMsgFirstname)
        common.handleErrorView(binding.edtLastname, binding.tvMsgLastname)
        common.handleErrorView(binding.lblEmailAddress.edtName, binding.lblEmailAddress.tvError)
        common.handleErrorView(binding.edtPassword, binding.tvMsgPassword)
        common.handleErrorView(binding.edtConfPassword, binding.tvMsgConfPassword)
        common.handleErrorView(binding.edtDob, binding.tvMsgDob)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(mActivity, SignInActivity::class.java))
                finish()
            }
        })

    }

    private fun setDobDate() {
        val c: Calendar = Calendar.getInstance()
        var dobYear: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        dobYear -= 16

        val dpd = DatePickerDialog(mActivity, { _, year, monthOfYear, dayOfMonth ->
            val dates = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
            dob = common.convertDateInFormat(dates, Constant().yyyyMmDd, Constant().yyyyMmDd)!!
            binding.edtDob.setText(common.convertDateInFormat(dates, Constant().yyyyMmDd, Constant().ddMmmYyyySlash)!!)
            binding.lblEmailAddress.edtName.requestFocus()
        }, dobYear, month, day
        )
        dpd.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dpd.datePicker.maxDate = c.timeInMillis
        dpd.show()
    }


    private fun isValidate(): Boolean {
        var isValid = true
        firstName = binding.edtFirstname.text.toString().trim()
        lastName = binding.edtLastname.text.toString().trim()
        email = binding.lblEmailAddress.edtName.text.toString().trim()
        password = binding.edtPassword.text.toString().trim()
        confPassword = binding.edtConfPassword.text.toString().trim()

        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            name = firstName + "" + lastName
        }


        if (firstName.isEmpty()) {
            isValid = false
            binding.tvMsgFirstname.visibility = View.VISIBLE
            binding.tvMsgFirstname.text = getString(R.string.txt_err_enter_first_name)
        }

        if (lastName.isEmpty()){
            isValid = false
            binding.tvMsgLastname.visibility = View.VISIBLE
            binding.tvMsgLastname.text = getString(R.string.txt_err_enter_last_name)
        }

        if (email.isEmpty()) {
            isValid = false
            binding.lblEmailAddress.tvError.visibility = View.VISIBLE
            binding.lblEmailAddress.tvError.text = getString(R.string.txt_err_enter_email)
        }else if (!isValidEmailId(email)) {
            isValid = false
            binding.lblEmailAddress.tvError.visibility = View.VISIBLE
            binding.lblEmailAddress.tvError.text = getString(R.string.txt_err_valid_email)
        }

        if (password.isEmpty()){
            isValid = false
            binding.tvMsgPassword.visibility = View.VISIBLE
            binding.tvMsgPassword.text = getString(R.string.txt_err_enter_password)

        }

        if (confPassword.isEmpty()){
            isValid = false
            binding.tvMsgConfPassword.visibility = View.VISIBLE
            binding.tvMsgConfPassword.text = getString(R.string.txt_err_enter_conf_password)

        }

        if (genderId == 0){
            isValid = false
            binding.tvMsgGender.visibility = View.VISIBLE
            binding.tvMsgGender.text = getString(R.string.txt_err_select_gender)
        }

        if (dob.isEmpty()){
            isValid = false
            binding.tvMsgDob.visibility = View.VISIBLE
            binding.tvMsgDob.text = getString(R.string.txt_err_enter_dob)
        }

        if (password != confPassword) {
            isValid = false
            binding.tvMsgConfPassword.visibility = View.VISIBLE
            binding.tvMsgConfPassword.text = getString(R.string.txt_err_match_conf_password)
        }
        return isValid

    }

    private fun callSignUpApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<LoginRes> = apiInterface.doRegistration(SignUpReq(dob,email,firstName,genderId,
                Languages.ENGLISH,lastName,""+isTermChecked,name,password))
            call.enqueue(object : Callback<LoginRes> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            val notificationPublisher = MyNotificationPublisher(mActivity)
                            notificationPublisher.sendNotification(
                                resources.getString(R.string.app_name),
                                response.body()!!.message!!
                            )

                            PrefManager.setAccessToken(Constant().bearer + " " + response.body()!!.token)
                            PrefManager.setUser(response.body()!!)
                            PrefManager.setUserId("" + response.body()!!.user!!.id)
                            PrefManager.setPassword(binding.edtPassword.getText().toString())
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            val commonDialog = CommonDialog(
                                mActivity,
                                getString(R.string.lbl_ok), "", "", response.body()!!.message!!
                            )
                            commonDialog.show()
                            commonDialog.binding.btnPositive.setOnClickListener {
                                commonDialog.dismiss()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (binding.chkPassword.isChecked) {
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod()
        } else {
            binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        }

        if (binding.edtPassword.getText().toString().isNotEmpty()) {
            binding.edtPassword.setSelection(binding.edtPassword.getText().toString().length)
        }

        if (binding.chkConfirmPsw.isChecked) {
            binding.edtConfPassword.transformationMethod = HideReturnsTransformationMethod()
        } else {
            binding.edtConfPassword.transformationMethod = PasswordTransformationMethod()
        }

        if (binding.edtPassword.text.isEmpty() && binding.tvMsgPassword.visibility == View.VISIBLE) {
            binding.tvMsgPassword.visibility = View.VISIBLE
        }

        if (binding.edtConfPassword.getText().toString().isNotEmpty()) {
            binding.edtConfPassword.setSelection(
                binding.edtConfPassword.getText().toString().length
            )
        }
    }
}
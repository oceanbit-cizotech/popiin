package com.popiin.activity

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.databinding.FragmentHelpBinding
import com.popiin.dialog.CommonDialog
import com.popiin.req.AddHelpReq
import com.popiin.res.CommonRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HelpFragment : BaseFragment<FragmentHelpBinding>() {
    lateinit var name: String
    lateinit var title: String
    lateinit var email: String
    lateinit var desc: String
    override fun getLayout(): Int {
        return R.layout.fragment_help
    }

    companion object {
        fun newInstance(): HelpFragment {
            return HelpFragment()
        }
    }

    override fun initViews() {
        binding.inclName.edtName.inputType = InputType.TYPE_CLASS_TEXT
        binding.inclEmail.edtName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        binding.inclTitle.edtName.inputType = InputType.TYPE_CLASS_TEXT
        binding.inclDescription.edtText.inputType = InputType.TYPE_CLASS_TEXT

        binding.inclName.edtName.text =PrefManager.getName()
        binding.inclEmail.edtName.text = PrefManager.getUser().user?.email!!

        binding.btnSubmit.setOnClickListener {
            if (isValidate()){
                callAddHelpApi()
            }
        }

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    private fun callAddHelpApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes> = apiInterface.doAddHelp(PrefManager.getAccessToken(),
                AddHelpReq(title,name,email,desc))
            call.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        val commonDialog = CommonDialog(
                            requireActivity(),
                            getString(R.string.lbl_ok), "", "", response.body()!!.msg!!
                        )
                        commonDialog.show()
                        commonDialog.binding.btnPositive.setOnClickListener {
                            commonDialog.dismiss()
                            requireActivity().supportFragmentManager.popBackStack()
                            requireActivity().overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true

        name = binding.inclName.edtName.text.toString()
        email = binding.inclEmail.edtName.text.toString()
        title = binding.inclTitle.edtName.text.toString()
        desc = binding.inclDescription.edtText.text.toString()

        if (name.isEmpty()){
            isValid = false
            binding.inclName.tvError.visibility = View.VISIBLE
            binding.inclName.tvError.text = getString(R.string.txt_err_enter_name)
        }

        if (email.isEmpty()){
            isValid = false
            binding.inclEmail.tvError.visibility = View.VISIBLE
            binding.inclEmail.tvError.text = getString(R.string.txt_err_enter_email)
        }

        if (title.isEmpty()){
            isValid = false
            binding.inclTitle.tvError.visibility = View.VISIBLE
            binding.inclTitle.tvError.text = getString(R.string.txt_err_enter_title)
        }

        if (desc.isEmpty()){
            isValid = false
            binding.inclDescription.tvError.visibility = View.VISIBLE
            binding.inclDescription.tvError.text = getString(R.string.txt_err_desc)
        }



        return isValid
    }
}
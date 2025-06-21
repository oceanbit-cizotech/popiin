package com.popiin.bottomDialogFragment

import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentAddSpecialRequestBinding
import com.popiin.listener.AddSpecialRequiremnetListener
import com.popiin.res.MyBookingRes

class AddSpecialRequestBottomFragment(
    private var specialRequirementListener: AddSpecialRequiremnetListener<MyBookingRes.Data>,
    var item: MyBookingRes.Data,
    var position: Int,
) :
    BaseBottomSheetDialog<BottomFragmentAddSpecialRequestBinding>() {
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_add_special_request
    }

    override fun initViews() {
        binding!!.btnAdd.setOnClickListener {
            if (isValidate()) {
                // call mark special request api
                dialog!!.dismiss()
                specialRequirementListener.onAddClick(
                    item,
                    position,
                    binding!!.edtSpecialRequest.text.toString()
                )
            }
        }

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true

        if (binding!!.edtSpecialRequest.text.toString().isEmpty()) {
            isValid = false
            binding!!.tvError.visibility = View.VISIBLE
            binding!!.tvError.text = getString(R.string.txt_err_special_requirement)

        }
        return isValid
    }
}
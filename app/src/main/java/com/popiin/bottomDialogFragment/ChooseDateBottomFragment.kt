package com.popiin.bottomDialogFragment

import android.app.Dialog
import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentChooseDateBinding
import com.popiin.listener.ChooseDateListener
import com.popiin.util.Constant
import com.popiin.util.DateTimePicker

class ChooseDateBottomFragment(
    private var chooseDateListener: ChooseDateListener,
    private var eventFilterDialog: Dialog,
) : BaseBottomSheetDialog<BottomFragmentChooseDateBinding>() {
    private lateinit var dateTimePickerBuilder: DateTimePicker.DateTimePickerBuilder
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_choose_date
    }

    override fun initViews() {
        dateTimePickerBuilder = DateTimePicker.DateTimePickerBuilder(requireActivity())
        binding!!.btnSearch.setOnClickListener {
            if (isValidate()) {
                dialog!!.dismiss()
                eventFilterDialog.dismiss()
                chooseDateListener.onSearchClick(
                    binding!!.edtFromDate.text.toString(),
                    binding!!.edtToDate.text.toString()
                )
            }
        }

        binding!!.edtFromDate.setOnClickListener {
            dateTimePickerBuilder
                .setPickerType(DateTimePicker.PICKER_TYPE.DATE)
                .setFormat(Constant().ddMmmYyyy)
                .setListener(object : DateTimePicker.DateTimePickerListener() {
                    override fun onDateSelected(
                        year: Int,
                        month: Int,
                        date: Int,
                        dateInString: String?,
                    ) {
                        super.onDateSelected(year, month, date, dateInString)
                        binding!!.edtFromDate.setText(dateInString)
                    }

                })
                .show()
        }
        binding!!.edtToDate.setOnClickListener {
            dateTimePickerBuilder
                .setPickerType(DateTimePicker.PICKER_TYPE.DATE)
                .setFormat(Constant().ddMmmYyyy)
                .setListener(object : DateTimePicker.DateTimePickerListener() {
                    override fun onDateSelected(
                        year: Int,
                        month: Int,
                        date: Int,
                        dateInString: String?,
                    ) {
                        binding!!.edtToDate.setText(dateInString)
                    }
                })
                .show()
        }

        binding!!.imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true

        if (binding!!.edtFromDate.text.toString().isEmpty()) {
            isValid = false
            binding!!.tvErrorFromDate.visibility = View.VISIBLE
        }

        if (binding!!.edtToDate.text.toString().isEmpty()) {
            isValid = false
            binding!!.tvErrorToDate.visibility = View.VISIBLE
        }

        return isValid
    }
}
package com.popiin.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.popiin.listener.AdapterTicketTagListener
import com.popiin.model.TicketBook
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.FragmentActivity
import android.graphics.drawable.ColorDrawable
import androidx.databinding.DataBindingUtil
import com.popiin.R
import android.widget.AdapterView
import android.text.TextWatcher
import android.text.Editable
import android.text.InputFilter
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.popiin.databinding.DialogAddBookingTypeBinding
import com.popiin.util.Common
import com.popiin.util.MinMaxFilter

class AddBookingTypeDialog(
    context: AppCompatActivity,
    private var listener: AdapterTicketTagListener<TicketBook?>,
    var position: Int,
    var ticketBook: TicketBook?,
    private var isUpdate: Boolean,
    rc: Int,
) : AppCompatDialog(context) {
    var binding: DialogAddBookingTypeBinding
    var activity: FragmentActivity
    var common = Common.instance
    var ticket: String? = null
    var ticketType: Array<String>
    var ticketPosition = -1
    private var remainingCapacity = 0

    init {
        activity = context
        remainingCapacity = rc
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_add_booking_type,
            null,
            false
        )
        setContentView(binding.root)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.setDimAmount(0.0f)
        common!!.handleErrorView(binding.edtTicketName.edtName, binding.edtTicketName.tvError)
        common!!.handleErrorView(binding.edtTicketPrice.edtName, binding.edtTicketPrice.tvError)
        common!!.handleErrorView(
            binding.edtTicketQuantity.edtName,
            binding.edtTicketQuantity.tvError
        )

        ticketType = context.resources.getStringArray(R.array.ticketType)
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(activity, R.layout.spin_gender, ticketType)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBookingOption.spinner.adapter = aa
        binding.spBookingOption.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    ticketPosition = position
                    if (position > -1) {
                        binding.spBookingOption.tvError.visibility = View.GONE
                        ticket = ticketType[position]
                        binding.edtTicketName.lblTitle.text = "Enter $ticket name"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.edtTicketQuantity.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnAddBooking.setOnClickListener {
            hideKeyboard()
            if (isValidate) {
                if (isUpdate) {
                    listener.onUpdateTag(
                        TicketBook(
                            binding.edtTicketName.edtName.getText().toString(),
                            binding.edtTicketPrice.edtName.getText().toString(),
                            binding.edtTicketQuantity.edtName.getText().toString(),
                            ticket!!,
                            ticketPosition,
                            true
                        ), position
                    )
                } else {
                    listener.onAddNewTag(
                        TicketBook(
                            binding.edtTicketName.edtName.getText().toString(),
                            binding.edtTicketPrice.edtName.getText().toString(),
                            binding.edtTicketQuantity.edtName.getText().toString(),
                            ticket!!,
                            ticketPosition,
                            true
                        )
                    )
                }
                dismiss()
            }
        }
        if (isUpdate) {
            binding.tvMenu.text = activity.resources.getString(R.string.abt_update_booking_type)
            binding.edtTicketName.edtName.setText(ticketBook!!.name)
            binding.edtTicketPrice.edtName.setText(ticketBook!!.price)
            binding.edtTicketQuantity.edtName.setText(ticketBook!!.quantity)
            binding.spBookingOption.spinner.setSelection(ticketBook!!.position)
            binding.btnAddBooking.setText(activity.resources.getString(R.string.lbl_update))
        }
        binding.edtTicketQuantity.edtName.filters =
            arrayOf<InputFilter>(MinMaxFilter("1", "" + remainingCapacity))
    }

    private val isValidate: Boolean
        get() {
            var isValidate = true
            if (ticketPosition == -1) {
                binding.spBookingOption.tvError.visibility = View.VISIBLE
                binding.spBookingOption.tvError.text =
                    context.getString(R.string.txt_err_select_option)
                isValidate = false
            }
            if (binding.edtTicketName.edtName.getText().toString().isEmpty()) {
                binding.edtTicketName.tvError.visibility = View.VISIBLE
                binding.edtTicketName.tvError.text = context.getString(R.string.other_placeholder)
                isValidate = false
            }
            if (binding.edtTicketPrice.edtName.getText().toString().isEmpty()) {
                binding.edtTicketPrice.tvError.visibility = View.VISIBLE
                binding.edtTicketPrice.tvError.text =
                    activity.resources.getString(R.string.edt_error_add_price)
                isValidate = false
            }

            if (binding.edtTicketPrice.edtName.getText().toString().length>0 && binding.edtTicketPrice.edtName.getText().toString().toInt()>=1 && binding.edtTicketPrice.edtName.getText().toString().toInt()<=4) {
                binding.edtTicketPrice.tvError.visibility = View.VISIBLE
                binding.edtTicketPrice.tvError.text ="Price should be > 5. Or 0."
                  //  activity.resources.getString(R.string.edt_error_add_price)
                isValidate = false
            }
            if (binding.edtTicketQuantity.edtName.getText().toString().isEmpty()) {
                binding.edtTicketQuantity.tvError.visibility = View.VISIBLE
                binding.edtTicketQuantity.tvError.text =
                    activity.resources.getString(R.string.edt_error_add_quantity)
                isValidate = false
            }
            return isValidate
        }

    private fun hideKeyboard() {
        this.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun showKeyboard() {
        this.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}
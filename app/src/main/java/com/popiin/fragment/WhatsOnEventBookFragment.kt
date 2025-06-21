package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.MyBookingsFragment
import com.popiin.activity.PrivacyActivity
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.bottomDialogFragment.SelectNumOfPeopleBottomFragment
import com.popiin.bottomDialogFragment.SelectWhatsonEventBottomFragment
import com.popiin.databinding.ActivityWhatsonEventBookBinding
import com.popiin.databinding.DialogBookNowCardDetailBinding
import com.popiin.dialog.ExpirationDatePickerDialog
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.InfoDialogListener
import com.popiin.model.NumOfPeopleModel
import com.popiin.req.BookWhatsOnReq
import com.popiin.res.EventListRes
import com.popiin.res.WhatsOnBookRes
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.FourDigitCardFormatWatcher
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class WhatsOnEventBookFragment : BaseFragment<ActivityWhatsonEventBookBinding>() {
    private var tickets: ArrayList<EventListRes.Whatsonticket> = ArrayList()
    private var numberPeoples: ArrayList<NumOfPeopleModel> = ArrayList()
    private var numberPeople = 1
    private var ticketId: String? = null
    private var dialog: Dialog? = null
    private var isDepositRequired = 0
    private var expireYearStr = ""
    private var expireMonthStr = ""
    private var cardNumber = ""
    private var cardCcvNo = ""
    private var bookingPosition = 0
    private var maxProgress = 600000
    private var currentProgress = maxProgress
    private var countDownTimer: CountDownTimer? = null
    private var dialogBinding: DialogBookNowCardDetailBinding? = null
    var processFee: Double = 0.0
    var priceBeforeFee: Double = 0.0
    var totalPrice: Double = 0.0
    var price: Double = 0.0
    override fun getLayout(): Int {
        return R.layout.activity_whatson_event_book
    }

    companion object {
        var whatsOnData: EventListRes.Event? = null
        fun newInstance(
            eventItem: EventListRes.Event? = null,
        ): WhatsOnEventBookFragment {
            whatsOnData = eventItem
            return WhatsOnEventBookFragment()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        binding.venueNumPeople.edtText.text=""+numberPeople
        numberPeoples.clear()
        numberPeoples.add(NumOfPeopleModel(Constant().one, false))
        numberPeoples.add(NumOfPeopleModel(Constant().two, false))


        tickets.clear()
        tickets.addAll(whatsOnData!!.whatsonticket!!)

        binding.model = whatsOnData
        binding.bc = common

        if (whatsOnData!!.whatsonticket!!.size > 0) {
            price = whatsOnData!!.whatsonticket!![bookingPosition].price
            binding.venueBookType.edtText.text = whatsOnData!!.whatsonticket!![bookingPosition].ticket_type!!
            binding.tvPriceValue.text =
                common.currencySymbol + common.getDecimalFormatValue(whatsOnData!!.whatsonticket!![bookingPosition].price)
            processFee = (whatsOnData!!.whatsonticket!![bookingPosition].price * common.processingFees) / 100
            totalPrice = whatsOnData!!.whatsonticket!![bookingPosition].price + processFee
            binding.tvProcessFeeValue.text =
                common.currencySymbol + common.getDecimalFormatValue(processFee)
            binding.tvTotalValue.text =
                common.currencySymbol + common.getDecimalFormatValue(totalPrice)
            isDepositRequired = if (whatsOnData!!.whatsonticket!![0].price > 0.0) 1 else 0
            ticketId = "" + whatsOnData!!.whatsonticket!![0].id
        }

        binding.venueBookType.edtText.setOnClickListener {
            showSelectBookTypeFragment()
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.venueNumPeople.edtText.setOnClickListener {

            numberPeoples.clear()
            if(binding.venueBookType.edtText.text.toString().equals("tables")){
                numberPeoples.add(NumOfPeopleModel(Constant().one, false))
            }else{
                numberPeoples.add(NumOfPeopleModel(Constant().one, false))
                numberPeoples.add(NumOfPeopleModel(Constant().two, false))
            }
            showSelectPeoplesFragment()
        }


        binding.btnBookNow.setOnClickListener {
            if (isValidate()) {
                if (isDepositRequired == 1) {
                    if (cardNumber.isNotEmpty() && cardCcvNo.isNotEmpty()) {
                        callBookingWhatsOn(1)
                    } else {
                        openCardDetailPopup()
                    }
                } else {
                    callBookingWhatsOn(0)
                }
            }
        }

        common.handleErrorView(
            binding.venueNumPeopleOther.edtName,
            binding.venueNumPeopleOther.tvError
        )

        startCountDownTimer()
        setSpannableText()
    }

    private fun setSpannableText(){

        val ss = SpannableString(getString(R.string.book_term_condition_cdata1))
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(requireActivity(), PrivacyActivity::class.java))
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(), R.color.colorBlue)
            }
        }
        ss.setSpan(clickableSpan1, 137, 151, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTermsCondition.text = ss
        binding.tvTermsCondition.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun callBookingWhatsOn(type: Int) {
        if (isValidate()) {
            callWhatsOnBookApi(type)
        }
    }

    private fun callWhatsOnBookApi(type: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<WhatsOnBookRes?>?
            if (type == 1) {
                if (dialog!!.isShowing) {
                    dialog!!.hide()
                }
                call = apiInterface.doWhatsTicketBook(
                    PrefManager.getAccessToken(), BookWhatsOnReq(
                        ticketId!!,
                        if (binding.venueNumPeopleOther.root.isVisible) binding.venueNumPeopleOther.edtName.text.toString() else binding.venueNumPeople.edtText.text.toString(),
                        cardCcvNo,
                        expireMonthStr,
                        cardNumber,
                        expireYearStr, processFee
                    )
                )
            } else {
                call = apiInterface.doWhatsTicketBook(
                    PrefManager.getAccessToken(), BookWhatsOnReq(
                        ticketId!!,
                        if (binding.venueNumPeopleOther.root.isVisible) binding.venueNumPeopleOther.edtName.text.toString()
                        else binding.venueNumPeople.edtText.text.toString()
                    )
                )
            }
            call!!.enqueue(object : Callback<WhatsOnBookRes?> {
                override fun onResponse(
                    call: Call<WhatsOnBookRes?>,
                    response: Response<WhatsOnBookRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            showCloseDialog(
                                ContextCompat.getDrawable(mActivity!!, R.drawable.ic_pass_success),
                                getString(R.string.txt_book_successful),
                                response.body()!!.message!!
                            )
                        } else {
                            common.openDialog(mActivity!!, response.body()!!.message)
                            // showCloseDialog(getString(R.string.txt_book_successful),response.body()!!.message!!)
                        }
                        countDownTimer!!.cancel()
                    }
                    clearCardData()
                }

                override fun onFailure(call: Call<WhatsOnBookRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun clearCardData() {
        cardNumber = ""
        cardCcvNo = ""
    }


    private fun isValidate(): Boolean {
        var isValid = true

        if (bookingPosition == -1) {
            isValid = false
            binding.venueBookType.tvError.visibility = View.VISIBLE
            binding.venueBookType.tvError.text = getString(R.string.error_venue_booking_type)
        }

        if (numberPeople == -1) {
            isValid = false
            binding.venueNumPeople.tvError.visibility = View.VISIBLE
            binding.venueNumPeople.tvError.setText(R.string.error_select_num_of_people)
        }
      /*  if (binding.venueNumPeopleOther.root.visibility == View.VISIBLE) {
            if (binding.venueNumPeopleOther.edtName.getText().toString().isEmpty()) {
                isValid = false
                binding.venueNumPeopleOther.tvError.visibility = View.VISIBLE
                binding.venueNumPeopleOther.tvError.setText(R.string.error_ener_num_of_people)
            }
            if (binding.venueNumPeopleOther.edtName.getText().toString() == "0") {
                isValid = false
                binding.venueNumPeopleOther.tvError.visibility = View.VISIBLE
                binding.venueNumPeopleOther.tvError.setText(R.string.error_ener_num_of_people)
            }
        }*/

        return isValid
    }

    private fun openCardDetailPopup() {
        dialog = Dialog(mActivity!!, R.style.Theme_AppCompat_Dialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_book_now_card_detail,
            null,
            false
        )
        dialog!!.setContentView(dialogBinding!!.root)
        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.window!!.statusBarColor = ContextCompat.getColor(mActivity!!, R.color.colorTransparent)
        dialogBinding!!.etCardNumber.addTextChangedListener(FourDigitCardFormatWatcher())
        dialogBinding!!.btnCancel.setOnClickListener { dialog!!.dismiss() }

        dialogBinding!!.etAddress.visibility = View.GONE
        dialogBinding!!.etPostcode.visibility = View.GONE
        dialogBinding!!.tvAddress.visibility = View.GONE
        dialogBinding!!.tvPostCode.visibility = View.GONE

        dialogBinding!!.etEmail.text= PrefManager.getUser()?.user?.email.toString()

        dialogBinding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }

        dialogBinding!!.etCardExpireDate.edtText.setOnClickListener {
            createDialogWithoutDateField { _, year, month, _ ->
                val expireMonth = month + 1
                expireYearStr = year.toString()
                expireMonthStr = expireMonth.toString()
                val date = String.format("%02d/%s", expireMonth, expireYearStr)
                dialogBinding!!.etCardExpireDate.edtText.setText(date)
            }.show()
        }
        dialogBinding!!.btnPayNow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (isValidInput) {
                    cardNumber = dialogBinding!!.etCardNumber.getText().toString()
                    cardCcvNo = dialogBinding!!.etCardCvv.getText().toString()
                    dialog!!.dismiss()
                    callBookingWhatsOn(1)
                }
            }

            private val isValidInput: Boolean
                get() {
                    dialogBinding!!.tvErrorCardName.visibility = View.GONE
                    dialogBinding!!.tvErrorCardNumber.visibility = View.GONE
                    dialogBinding!!.etCardExpireDate.tvError.visibility = View.INVISIBLE
                    dialogBinding!!.tvErrorCvvNumber.visibility = View.INVISIBLE
                    dialogBinding!!.tvErrorAdress.visibility = View.GONE
                    dialogBinding!!.tvErrorPostCode.visibility = View.GONE
                    var isValid = true
                    if (dialogBinding!!.etCardName.getText().toString().isEmpty()) {
                        dialogBinding!!.tvErrorCardName.visibility = View.VISIBLE
                        isValid = false
                    }
                    if (dialogBinding!!.etCardNumber.getText().toString().isEmpty()) {
                        dialogBinding!!.tvErrorCardNumber.visibility = View.VISIBLE
                        isValid = false
                    }
                    if (dialogBinding!!.etCardExpireDate.edtText.text.toString().isEmpty()) {
                        dialogBinding!!.etCardExpireDate.tvError.visibility = View.VISIBLE
                        isValid = false
                    }
                    if (dialogBinding!!.etCardCvv.getText().toString().isEmpty()) {
                        dialogBinding!!.tvErrorCvvNumber.visibility = View.VISIBLE
                        isValid = false
                    } else if (dialogBinding!!.etCardCvv.getText().toString().length < 3) {
                        dialogBinding!!.tvErrorCvvNumber.visibility = View.VISIBLE
                        dialogBinding!!.tvErrorCvvNumber.setText(R.string.please_enter_valid_cvv)
                        isValid = false
                    }

                    return isValid
                }
        })
        if (!mActivity!!.isFinishing) {
            dialog!!.show()
        }
    }

    private fun createDialogWithoutDateField(listener: DatePickerDialog.OnDateSetListener): DatePickerDialog {
        val calendar: Calendar = Calendar.getInstance()
        return ExpirationDatePickerDialog(
            mActivity!!,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }


    private fun startCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
        countDownTimer = object : CountDownTimer(currentProgress.toLong(), 1000) {
            override fun onTick(remainSec: Long) {
                currentProgress = remainSec.toInt()
                binding.csbTimer.progress = currentProgress.toFloat()
                val totalSecond = (remainSec / 1000).toInt()
                val minute = totalSecond / 60
                val second = totalSecond % 60
                val timeToDisplay = String.format("%02d:%02d", minute, second)
                binding.tvTimer.text = timeToDisplay
            }

            override fun onFinish() {
                currentProgress = 0
                binding.csbTimer.progress = currentProgress.toFloat()
                binding.tvTimer.text = getString(R.string.txt_default_book_time)
                showCloseDialog(
                    ContextCompat.getDrawable(mActivity!!, R.drawable.ic_failed),
                    getString(R.string.txt_failed),
                    getString(R.string.txt_booking_time_run_out)
                )
            }
        }.start()
    }

    private fun showSelectBookTypeFragment() {
        val selectBookTypeBottomFragment =
            SelectWhatsonEventBottomFragment(tickets, bookingPosition, bookTypeListener)
        selectBookTypeBottomFragment.isCancelable = false
        mActivity!!.supportFragmentManager.let { selectBookTypeBottomFragment.show(it, "") }
    }


    private fun showSelectPeoplesFragment() {
        val selectNumOfPeopleBottomFragment =
            SelectNumOfPeopleBottomFragment(numberPeoples, numberPeople, numOfPeopleListener)
        selectNumOfPeopleBottomFragment.isCancelable = false
        mActivity!!.supportFragmentManager.let { selectNumOfPeopleBottomFragment.show(it, "") }
    }

    private var bookTypeListener: AdapterItemClickListener<EventListRes.Whatsonticket> =
        object : AdapterItemClickListener<EventListRes.Whatsonticket>() {
            @SuppressLint("SetTextI18n")
            override fun onAdapterItemClick(item: EventListRes.Whatsonticket, position: Int) {
                super.onAdapterItemClick(item, position)
                numberPeople=1
                price = item.price
                priceBeforeFee = (price * numberPeople)
                bookingPosition = position
                ticketId = "" + item.id
                isDepositRequired = if (item.price > 0.0) 1 else 0
                binding.venueBookType.edtText.text = item.ticket_type!!
                binding.tvPriceValue.text = common.currencySymbol + common.getDecimalFormatValue(price)
                processFee = (priceBeforeFee * common.processingFees) / 100
                totalPrice = price + processFee
                binding.tvProcessFeeValue.text = common.currencySymbol + common.getDecimalFormatValue(processFee)
                binding.tvTotalValue.text = common.currencySymbol + common.getDecimalFormatValue(totalPrice)
                binding.venueNumPeople.edtText.text=""+numberPeople
            }
        }

    private var numOfPeopleListener: AdapterItemClickListener<NumOfPeopleModel> =
        object : AdapterItemClickListener<NumOfPeopleModel>() {
            override fun onAdapterItemClick(item: NumOfPeopleModel, position: Int) {
                super.onAdapterItemClick(item, position)
                numberPeople = item.title.toInt()
                priceBeforeFee = (price * numberPeople)
                processFee = (priceBeforeFee * common.processingFees) / 100
                totalPrice = priceBeforeFee + processFee
                binding.tvProcessFeeValue.text = common.currencySymbol + common.getDecimalFormatValue(processFee)
                binding.tvPriceValue.text = common.currencySymbol + common.getDecimalFormatValue(priceBeforeFee)
                binding.tvTotalValue.text = common.currencySymbol + common.getDecimalFormatValue(totalPrice)
                binding.venueNumPeople.edtText.text = item.title
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }

    private fun showCloseDialog(image: Drawable?, message: String, desc: String) {
        showCommonInfoBottomFragment(
            image,
            message,
            desc,
            getString(R.string.lbl_ok),
            infoDialogListener
        )
    }

    private fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        positiveText: String,
        listener: InfoDialogListener,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                "",
                positiveText,
                ContextCompat.getColor(mActivity!!, R.color.colorHeaderText),
                ContextCompat.getColor(mActivity!!, R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        mActivity!!.supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
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

        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
            setFragmentWithStack(
                MyBookingsFragment.newInstance(Constant().whatsOnBooking),
                MyBookingsFragment::class.java.simpleName
            )
        }

    }

}

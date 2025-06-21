package com.popiin.activity

import android.app.DatePickerDialog
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
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.constant.Language
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.EventTicketAdapter
import com.popiin.adapter.FavouriteEventTicketAdapter
import com.popiin.adapter.QuantityBookAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.DialogAvailableOfferBinding
import com.popiin.databinding.DialogBookNowCardDetailBinding
import com.popiin.databinding.FragmentBookNowBinding
import com.popiin.dialog.ExpirationDatePickerDialog
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.AdapterOfferListener
import com.popiin.listener.InfoDialogListener
import com.popiin.model.AvailOfferModel
import com.popiin.req.BookEventReq
import com.popiin.req.ValidatePromoReq
import com.popiin.res.BookEventRes
import com.popiin.res.EditEventRes
import com.popiin.res.EventListRes
import com.popiin.res.FavouriteEventsRes
import com.popiin.res.ValidatePromoRes
import com.popiin.services.MyNotificationPublisher
import com.popiin.util.Constant
import com.popiin.util.FourDigitCardFormatWatcher
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BookNowFragment : BaseFragment<FragmentBookNowBinding>() {
    private lateinit var quantityAdapter: QuantityBookAdapter
    private var quantityList = ArrayList<AvailOfferModel>()
    private var eventTicketAdapter: EventTicketAdapter? = null
    private var favouriteEventTicketAdapter: FavouriteEventTicketAdapter? = null
    private var eventId = 0
    private var ticketId = 0
    private var selectedTicketPrice = 0.0
    private var selectedIndex = 0
    private var bookingType: String? = null
    private var maxProgress = 600000

    //    int maxProgress = 5000;
    private var currentProgress = maxProgress
    override fun getLayout(): Int {
        return R.layout.fragment_book_now
    }

    companion object {
        var event: EventListRes.Event? = null
        var favouriteEvent: FavouriteEventsRes.FavouriteEvent? = null
        fun newInstance(
            eventItem: EventListRes.Event? = null,
            favEventItem: FavouriteEventsRes.FavouriteEvent? = null
        ): BookNowFragment {
            event = eventItem
            favouriteEvent = favEventItem
            return BookNowFragment()
        }
    }

    override fun initViews() {
        binding.username = PrefManager.getUser().user?.firstName
        binding.discount = 0.0

        binding.common = common

        if (quantityList.size == 0) {
            quantityList.add(AvailOfferModel(Constant().one, false))
            quantityList.add(AvailOfferModel(Constant().two, false))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(mActivity)

        if (favouriteEvent != null) {
            binding.eventFavourite = favouriteEvent!!.event
            binding.ticketPrice = favouriteEvent!!.tickets!![0].price
            binding.bookingFee = (favouriteEvent!!.tickets!![0].price * common.processingFees) / 100
            binding.totalPrice = favouriteEvent!!.tickets!![0].price + binding.bookingFee!!
            binding.ticketQuantity = getString(R.string.txt_default_ticket_quantity)
            favouriteEventTicketAdapter = FavouriteEventTicketAdapter(favouriteEvent!!.tickets!!, favouriteTicketsAdapterItemListener)
            binding.recyclerView.adapter = favouriteEventTicketAdapter
            favouriteEventTicketAdapter!!.setSelect(true, 0)
            eventId = favouriteEvent!!.event!!.id
        } else {
            if (event != null) {
                binding.event = event
                binding.ticketPrice = event!!.lowestPrice
                binding.bookingFee = (event!!.lowestPrice * common.processingFees) / 100
                binding.totalPrice = event!!.lowestPrice + binding.bookingFee!!
                binding.ticketQuantity = getString(R.string.txt_default_ticket_quantity)
                eventTicketAdapter = EventTicketAdapter(event!!.tickets!!, ticketsAdapterItemListener)
                binding.recyclerView.adapter = eventTicketAdapter
                eventTicketAdapter!!.setSelect(true, 0)
                eventId = event!!.id
            }
        }
        binding.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.btnApplyCode.setOnClickListener(View.OnClickListener {
            hideKeyBoard(requireActivity())
            if (binding.etPromoCode.getText().toString().isEmpty()) {
                showToast(getString(R.string.txt_err_promo_code))
                return@OnClickListener
            }
            callValidatePromoCode()
        })

        binding.btnDeleteCode.setOnClickListener {
            binding.etPromoCode.setText("")
            binding.discount = 0.0
        }

        binding.btnCancel.setOnClickListener {
            binding.etPromoCode.setText("")
            binding.discount = 0.0
        }

        binding.btnBookNow.setOnClickListener {
            hideKeyBoard(requireActivity())
            var age = 0

            val strAge: String? = if (favouriteEvent != null) {
                favouriteEvent!!.event!!.age
            } else {
                event!!.age
            }

            when (strAge) {
                Constant().sixteen -> {
                    age = 16
                }
                Constant().eighteen -> {
                    age = 18
                }
                Constant().twentyOne -> {
                    age = 21
                }
                Constant().noAgeRestriction -> {
                    age = 0
                }
            }

            if (getAge(PrefManager.getUser()!!.user!!.dob) > age) {
                if ((binding.ticketPrice!!).toInt() == 0) {
                    callBookEventApi(
                        "" + eventId,
                        "" + ticketId,
                        "" + 0,
                        "",
                        "" + expireMonthStr,
                        "" + expireYearStr,
                        "",
                        "",
                        "", "0", (binding.tvQuantity.text.toString()).toInt(),
                        "",""
                    )
                } else {
                    openCardDetailsDialog()
                }
            } else {
                common.openDialog(mActivity!!, resources.getString(R.string.popup_age))
            }
        }

        binding.tvQuantity.setOnClickListener {
            openQuantityDialog()
        }

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
    private var expireYearStr = ""
    private var expireMonthStr = ""

    private fun openCardDetailsDialog() {
        val dialog = PopupWindow(mActivity)

        val binding: DialogBookNowCardDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_book_now_card_detail,
            null,
            false
        )
        binding!!.etEmail.text= PrefManager.getUser()?.user?.email.toString()

        binding.etCardNumber.addTextChangedListener(FourDigitCardFormatWatcher())

        binding.etCardExpireDate.edtText.setOnClickListener {
            createDialogWithoutDateField { _, year, month, _ ->
                val expireMonth = month + 1
                expireYearStr = "" + year
                expireMonthStr = "" + expireMonth
                val date = String.format("%02d/%s", expireMonth, expireYearStr)
                binding.etCardExpireDate.edtText.setText(date)

            }.show()
        }

        binding.btnPayNow.setOnClickListener {
            if (isValidInput(binding)) {
                dialog.dismiss()
                if ((this.binding.ticketPrice!!).toInt() == 0) {
                    callBookEventApi(
                        "" + eventId,
                        "" + ticketId,
                        "" + 0,
                        "" + binding.etCardNumber.getText().toString(),
                        "" + expireMonthStr,
                        "" + expireYearStr,
                        "" + binding.etCardCvv.getText().toString(),
                        "" + binding.etAddress.getText().toString(),
                        "" + binding.etPostcode.getText().toString(),
                        "0",
                        this.binding.tvQuantity.text.toString().toInt(),
                        email = binding.etEmail.getText().toString(),
                        phone_number = binding.etPhone.getText().toString()
                    )
                } else {
                    callBookEventApi(
                        "" + eventId,
                        "" + ticketId,
                        "" + (this.binding.totalPrice!! - this.binding.discount!!),
                        "" + binding.etCardNumber.getText().toString(),
                        "" + expireMonthStr,
                        "" + expireYearStr,
                        "" + binding.etCardCvv.getText().toString(),
                        "" + binding.etAddress.getText().toString(),
                        pinCode = "" + binding.etPostcode.getText().toString(),
                        bookingFees = ""+this.binding.bookingFee!!,
                        quantity = this.binding.tvQuantity.text.toString().toInt(),
                        email = binding.etEmail.getText().toString(),
                        phone_number = binding.etPhone.getText().toString()
                    )
                }
                dialog.dismiss()
            }
        }

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }



        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.isFocusable = true
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
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

    private fun callValidatePromoCode() {
        if (!isInternetConnect()) {
            return
        }
        showProgress()
        val call = apiInterface.getValidatePromo(
            PrefManager.getAccessToken(),
            ValidatePromoReq(binding.etPromoCode.getText().toString())
        )
        call!!.enqueue(object : Callback<ValidatePromoRes?> {
            override fun onResponse(
                call: Call<ValidatePromoRes?>,
                response: Response<ValidatePromoRes?>
            ) {
                if (isValidResponse(response)) {
                    if (response.body()!!.success) {
                        binding.discount = response.body()!!.validatePromoData!!.value
                    }

                    common.openDialog(mActivity!!, response.body()!!.message)
                }
                hideProgress()
            }

            override fun onFailure(call: Call<ValidatePromoRes?>, t: Throwable) {
                onApiFailure(throwable = t )
            }

        })
    }


    private fun isValidInput(dialogBinding: DialogBookNowCardDetailBinding): Boolean {
        dialogBinding.tvErrorCardName.visibility = View.GONE
        dialogBinding.tvErrorCardNumber.visibility = View.GONE
        dialogBinding.etCardExpireDate.tvError.visibility = View.INVISIBLE
        dialogBinding.tvErrorCvvNumber.visibility = View.INVISIBLE
        dialogBinding.tvErrorAdress.visibility = View.GONE
        dialogBinding.tvErrorPostCode.visibility = View.GONE
        dialogBinding.tvErrorEmail.visibility = View.GONE
        dialogBinding.tvErrorPhone.visibility = View.GONE


        var isValid = true
        if (dialogBinding.etCardName.getText().toString().isEmpty()) {
            dialogBinding.tvErrorCardName.visibility = View.VISIBLE
            isValid = false
        }
        if (dialogBinding.etEmail.getText().toString().isEmpty()) {
            dialogBinding.tvErrorEmail.visibility = View.VISIBLE
            isValid = false
        }
        if (dialogBinding.etPhone.getText().toString().isEmpty()) {
            dialogBinding.tvErrorPhone.visibility = View.VISIBLE
            isValid = false
        }
        if (dialogBinding.etCardNumber.getText().toString().isEmpty()) {
            dialogBinding.tvErrorCardNumber.visibility = View.VISIBLE
            isValid = false
        }
        if (dialogBinding.etCardExpireDate.edtText.text.toString().isEmpty()) {
            dialogBinding.etCardExpireDate.tvError.visibility = View.VISIBLE
            dialogBinding.etCardExpireDate.tvError.text = getString(R.string.txt_err_expiry_date)
            isValid = false
        }
        if (dialogBinding.etCardCvv.getText().toString().isEmpty()) {
            dialogBinding.tvErrorCvvNumber.visibility = View.VISIBLE
            isValid = false
        } else if (dialogBinding.etCardCvv.getText().toString().length < 3) {
            dialogBinding.tvErrorCvvNumber.visibility = View.VISIBLE
            dialogBinding.tvErrorCvvNumber.setText(R.string.please_enter_valid_cvv)
            isValid = false
        }

        if (dialogBinding.etAddress.getText().toString().isEmpty()) {
            dialogBinding.tvErrorAdress.visibility = View.VISIBLE
            isValid = false
        }
        if (dialogBinding.etPostcode.getText().toString().isEmpty()) {
            dialogBinding.tvErrorPostCode.visibility = View.VISIBLE
            isValid = false
        }
        return isValid
    }

    private var ticketsAdapterItemListener: AdapterItemClickListener<EventListRes.Tickets?> =
        object : AdapterItemClickListener<EventListRes.Tickets?>() {
            override fun onAdapterItemClick(item: EventListRes.Tickets?, position: Int) {
                super.onAdapterItemClick(item, position)
                ticketId = item!!.id
                bookingType = item.ticket_type
                binding.ticketName = item.name


                if (item.available_quantity > 0) {
                    selectedTicketPrice = item.price
                    binding.ticketQuantity = if(binding.ticketQuantity!!.toInt() > 1) binding.ticketQuantity else getString(R.string.txt_default_ticket_quantity)
                    binding.ticketPrice = item.price * binding.ticketQuantity!!.toInt()

                    binding.btnBookNow.visibility = View.VISIBLE
                    binding.cbAddPromoCode.visibility = View.VISIBLE
                } else {
                    selectedTicketPrice=0.0
                    binding.ticketPrice = 0.0
                    binding.bookingFee = 0.0
                    binding.ticketQuantity = getString(R.string.txt_sold_out)
                    binding.btnBookNow.visibility = View.GONE
                    binding.cbAddPromoCode.visibility = View.GONE
                }

                if (binding.ticketQuantity == getString(R.string.txt_sold_out)){
                    binding.ticketQuantity = "0"
                }
                calculateTicketPrice()

            }

        }

    private var favouriteTicketsAdapterItemListener: AdapterItemClickListener<FavouriteEventsRes.Tickets?> =
        object : AdapterItemClickListener<FavouriteEventsRes.Tickets?>() {
            override fun onAdapterItemClick(item: FavouriteEventsRes.Tickets?, position: Int) {
                super.onAdapterItemClick(item, position)
                ticketId = item!!.id
                bookingType = item.ticket_type
                binding.ticketName = item.name

                if (item.available_quantity > 0) {
                    selectedTicketPrice = item.price
                    binding.ticketQuantity = if (binding.ticketQuantity!!.toInt() > 1) binding.ticketQuantity else getString(
                        R.string.txt_default_ticket_quantity
                    )
                    binding.ticketPrice = item.price * binding.ticketQuantity!!.toInt()
                //    binding.bookingFee = if (item.price == 0.0) 0.0 else (item.price * binding.ticketQuantity!!.toInt() * 11 / 100)
                    binding.btnBookNow.visibility = View.VISIBLE
                    binding.cbAddPromoCode.visibility = View.VISIBLE
                } else {
                    selectedTicketPrice=0.0
                    binding.ticketPrice = 0.0
                    binding.bookingFee = 0.0
                    binding.ticketQuantity = getString(R.string.txt_sold_out)
                    binding.btnBookNow.visibility = View.GONE
                    binding.cbAddPromoCode.visibility = View.GONE
                }

                if (binding.ticketQuantity == getString(R.string.txt_sold_out)) {
                    binding.ticketQuantity = "0"
                }
                calculateTicketPrice()
            }
        }

    private var countDownTimer: CountDownTimer? = null

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
                binding.csbTimer.progress = (currentProgress).toFloat()
                binding.tvTimer.text = getString(R.string.txt_default_book_time)
                showCloseDialog()
            }
        }.start()
    }

    private fun showCloseDialog() {
        showCommonInfoBottomFragment(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_failed),
            getString(
                R.string.txt_failed
            ),
            getString(R.string.txt_booking_time_run_out),
            getString(R.string.lbl_ok),
            infoDialogListener
        )
    }

    private fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        positiveText: String,
        listener: InfoDialogListener
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                "",
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
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
            mActivity!!.supportFragmentManager.popBackStack()
            mActivity!!.supportFragmentManager.popBackStack()
            if (favouriteEvent != null && favouriteEvent!!.event != null){
                setFragmentWithStack(
                    BookingSummaryFragment.newInstance(
                        favouriteEvent,
                        event,
                        bookEventRes
                    ), BookingSummaryFragment::class.java.simpleName
                )
            }else{
                setFragmentWithStack(
                    BookingSummaryFragment.newInstance(
                        null,
                        event,
                        bookEventRes
                    ), BookingSummaryFragment::class.java.simpleName
                )
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }

    private fun openQuantityDialog() {
        val dialog = PopupWindow(mActivity)

        val binding: DialogAvailableOfferBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_available_offer,
            null,
            false
        )


        quantityAdapter = QuantityBookAdapter(quantityList, quantityListener, selectedIndex, dialog)
        binding.rvOffers.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvOffers.adapter = quantityAdapter

        binding.title = getString(R.string.txt_select_quantity)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private var quantityListener: AdapterOfferListener<AvailOfferModel> =
        object : AdapterOfferListener<AvailOfferModel>() {
            override fun onOfferItemClick(item: AvailOfferModel, position: Int) {
                super.onOfferItemClick(item, position)
                selectedIndex = position
                binding.ticketQuantity = item.title
                var ticketQuatity = item.title.toDouble()
                binding.ticketPrice = selectedTicketPrice* ticketQuatity
                calculateTicketPrice()


            }

        }
    private fun calculateTicketPrice(){
        binding.bookingFee = (binding.ticketPrice ?: 0.0) * common.processingFees / 100
        binding.totalPrice = (binding.ticketPrice as? Double ?: 0.0) + (binding.bookingFee ?: 0.0)
    }
var bookEventRes: BookEventRes.Data? =null
    private fun callBookEventApi(
        event_id: String,
        ticket_id: String,
        price: String,
        card_number: String,
        exp_month: String,
        exp_year: String,
        cvc: String,
        address: String,
        pinCode: String,
        bookingFees: String,
        quantity: Int,
        email:String,
        phone_number:String
    ) {
        if (!isInternetConnect()) {
            return
        }
        showProgress()
        val call: Call<BookEventRes?>? = apiInterface.getBookEvent(
            PrefManager.getAccessToken(),
            BookEventReq(
                Language.ENGLISH,
                "" + event_id,
                "" + ticket_id,
                "" + price,
                "" + card_number,
                "" + exp_month,
                "" + exp_year,
                "" + cvc,
                "0",
                "0",
                bookingFees,
                binding.bookingFee!!.toDouble(),
                "" + bookingType,
                "" + binding.discount!!,
                "" + address,
                "" + pinCode, quantity,email,phone_number
            )
        )
        call!!.enqueue(object : Callback<BookEventRes?> {
            override fun onResponse(call: Call<BookEventRes?>, response: Response<BookEventRes?>) {
                hideProgress()
                if (response.body()!!.success) {
                    if (favouriteEvent != null && favouriteEvent!!.event != null){
                        bookEventRes= response.body()!!.data
                        openSuccessDialog(response.body()!!.message.toString())
                     /*  setFragmentWithStack(
                            BookingSummaryFragment.newInstance(
                                favouriteEvent,
                                event,
                                response.body()!!.data
                            ), BookingSummaryFragment::class.java.simpleName
                        )*/

                    }else{

                        bookEventRes= response.body()!!.data
                        openSuccessDialog(response.body()!!.message.toString())
                      /*  mActivity!!.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )*/
                    }
                }else{
                    common.openDialog(mActivity!!, response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<BookEventRes?>, t: Throwable) {
                onApiFailure(throwable = t )
            }

        })
    }


    private fun openSuccessDialog(message: String) {
        val notificationPublisher = MyNotificationPublisher(mActivity!!.baseContext)
        notificationPublisher.sendNotification(
            resources.getString(R.string.app_name),
            message
        )
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pass_success),
                getString(
                    R.string.txt_thank_you
                ),
                message,
                "",
                getString(R.string.txt_done),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                infoDialogListener
            )
        commonInfoBottomFragment.isCancelable = false
        commonInfoBottomFragment.binding?.ivClose?.visibility=View.GONE
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }




}
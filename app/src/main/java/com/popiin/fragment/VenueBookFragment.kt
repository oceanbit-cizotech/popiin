package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.CountDownTimer
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import carbon.widget.EditText
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.MyBookingsFragment
import com.popiin.bottomDialogFragment.*
import com.popiin.databinding.DialogBookNowCardDetailBinding
import com.popiin.databinding.FragmentVenueBookBinding
import com.popiin.dialog.CommonDialog
import com.popiin.dialog.ExpirationDatePickerDialog
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.InfoDialogListener
import com.popiin.listener.SelectDateListener
import com.popiin.model.NumOfPeopleModel
import com.popiin.req.BookVenueReq
import com.popiin.res.FavouriteVenueRes
import com.popiin.res.VenueBookRes
import com.popiin.res.VenueListRes
import com.popiin.util.Constant
import com.popiin.util.DateTimePicker
import com.popiin.util.FourDigitCardFormatWatcher
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class VenueBookFragment : BaseFragment<FragmentVenueBookBinding>() {
    private var favouriteVenue: FavouriteVenueRes.FavouriteVenue? = null
    private var specialRequest: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var startDate: String? = null
    private var strDate: String? = null
    private var endSecTime: String? = null
    private var strTime: String? = null
    private var tickets: ArrayList<VenueListRes.Tickets> = ArrayList()
    private var numberPeoples: ArrayList<NumOfPeopleModel> = ArrayList()
    private var bookingPosition = -1
    private var isDepositRequired = 0
    private var expireYearStr = ""
    private var expireMonthStr = ""
    private var cardNumber = ""
    private var cardCcvNo = ""
    private var ticketId: String? = null
    private var numberPeople = -1
    private var dialog: Dialog? = null
    private var maxProgress = 600000
    private var currentProgress = maxProgress
    private var peopleNumber = ""
    private var countDownTimer: CountDownTimer? = null
    var isVenueBookingDateAvailable : Boolean =false
    var isVenueBookingTimeAvailable : Boolean =false
    var userSelectedTime:String= ""

    override fun getLayout(): Int {
        return R.layout.fragment_venue_book
    }

    companion object {
        var venueEvent: VenueListRes.Venue? = null
        fun newInstance(data: VenueListRes.Venue): VenueBookFragment {
            venueEvent = data
            return VenueBookFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun initViews() {
        binding.username = venueEvent!!.venue_name

        numberPeoples.clear()
        numberPeoples.add(NumOfPeopleModel(Constant().one, false))
        numberPeoples.add(NumOfPeopleModel(Constant().two, false))
        numberPeoples.add(NumOfPeopleModel(Constant().three, false))
        numberPeoples.add(NumOfPeopleModel(Constant().four, false))
        numberPeoples.add(NumOfPeopleModel(Constant().five, false))
        numberPeoples.add(NumOfPeopleModel(Constant().six, false))

        tickets.addAll(venueEvent!!.tickets!!)

        if (favouriteVenue != null) {
            binding.model = favouriteVenue!!.venue
        } else {
            binding.venue = venueEvent
            binding.txtBookingText.text = venueEvent!!.tickets!![0].booking_text
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.venueBookType.edtText.setOnClickListener {
            showSelectBookTypeFragment()
        }

        binding.venueNumPeople.edtText.setOnClickListener {
            showSelectPeoplesFragment()
        }

        binding.venueStartDate.edtText.setOnClickListener {
            showSelectDateFragment(binding.venueStartDate.edtText, strDate)
        }

        binding.venueStartTime.edtText.setOnClickListener {
            showSelectTimeFragment(binding.venueStartTime.edtText, strTime)
        }

        binding.venueEndTime.edtText.setOnClickListener {
            showSelectTimeFragment(binding.venueEndTime.edtText, endSecTime)
        }

        binding.btnBook.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidate()) {
               /* if (isDepositRequired == 1) {
                    if (cardNumber.isNotEmpty() && cardCcvNo.isNotEmpty()) {
                        callBookingVenue(1)
                    } else {
                        openCardDetailPopup()
                    }
                } else {
                    callBookingVenue(0)
                }*/

                callBookingVenue(0)
            }
        }


        startCountDownTimer()

    }

    private fun showSelectBookTypeFragment() {
        val selectBookTypeBottomFragment =
            SelectBookTypeBottomFragment(tickets, bookingPosition, bookTypeListener)
        selectBookTypeBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectBookTypeBottomFragment.show(it, "") }
    }

    private fun showSelectPeoplesFragment() {
        val selectNumOfPeopleBottomFragment =
            SelectNumOfPeopleBottomFragment(numberPeoples, numberPeople, numOfPeopleListener)
        selectNumOfPeopleBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectNumOfPeopleBottomFragment.show(it, "") }
    }

    private fun showSelectDateFragment(edtText: EditText, strDate: String?) {
        val selectDateBottomFragment = SelectDateBottomFragment(edtText, strDate,listener)
        selectDateBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectDateBottomFragment.show(it, "") }
    }

    private fun showSelectTimeFragment(edtText: EditText, time: String?) {
        val selectTimeBottomFragment = SelectTimeBottomFragment(edtText, time,listener)
        selectTimeBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectTimeBottomFragment.show(it, "") }
    }

    private var bookTypeListener: AdapterItemClickListener<VenueListRes.Tickets> =
        object : AdapterItemClickListener<VenueListRes.Tickets>() {
            override fun onAdapterItemClick(item: VenueListRes.Tickets, position: Int) {
                super.onAdapterItemClick(item, position)
                println("bookTypeListener : onAdapterItemClick : " + item.name)
                bookingPosition = position
                isDepositRequired = item.is_deposite_required
                ticketId = "" + item.id
                binding.venueBookType.edtText.text = item.name!!
              //  binding.tvTerms.text = item.booking_text
            }
        }

    private var numOfPeopleListener: AdapterItemClickListener<NumOfPeopleModel> =
        object : AdapterItemClickListener<NumOfPeopleModel>() {
            override fun onAdapterItemClick(item: NumOfPeopleModel, position: Int) {
                super.onAdapterItemClick(item, position)
                println("numOfPeopleListener : onAdapterItemClick : " + item.title)
                if (position == numberPeoples.size - 1) {
                    numberPeople = position
                //    binding.venueNumPeopleOther.root.visibility = View.VISIBLE
                } else {
                    numberPeople = position
                  //  binding.venueNumPeopleOther.root.visibility = View.GONE
                }
                binding.venueNumPeople.edtText.text = item.title
            }
        }

    val listener = object : SelectDateListener() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSelectDate(strDay: String) {
            isVenueBookingDateAvailable =false
            for ((index, timing) in venueEvent!!.timings!!.withIndex()) {
                if (!timing.working_day.isNullOrBlank() && timing.working_day == strDay) {
                    isVenueBookingDateAvailable =true
                    bookingOpenTime=timing.open_time.toString()
                    bookingCloseTime=timing.close_time.toString()
                    break
                }
            }

            if(userSelectedTime.isNotEmpty()){
                isVenueBookingDateAvailable=isWithinTimeRange(bookingOpenTime,bookingCloseTime,userSelectedTime)
            }
            isVenueBookingAvailable(isAvailable = isVenueBookingDateAvailable)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSelectTime(strDay: String) {
            super.onSelectTime(strDay)
            userSelectedTime=strDay
            if(bookingOpenTime.isNotEmpty() && bookingCloseTime.isNotEmpty()){
                isVenueBookingTimeAvailable=isWithinTimeRange(bookingOpenTime,bookingCloseTime,strDay)
            }
            isVenueBookingAvailable(isVenueBookingTimeAvailable)
        }
    }
    var bookingOpenTime : String=""
    var bookingCloseTime : String=""
    @RequiresApi(Build.VERSION_CODES.O)
    private fun callBookingVenue(type: Int) {
        if (isValidate()) {
            callVenueBookApi(type)
        }
    }

    private fun isVenueBookingAvailable(isAvailable : Boolean){
        println("@@@@@.isVenueBookingAvailable  "+isAvailable)

        if(isAvailable){
            binding.btnBook.isEnabled =true
            binding.btnBook.alpha =1f
            binding.txtBookingMessage.visibility=View.GONE
        }else{
            binding.txtBookingMessage.visibility=View.VISIBLE
            binding.btnBook.alpha =0.5f
            binding.btnBook.isEnabled =false
        }
    }
    private fun callVenueBookApi(type: Int) {
        if (isInternetConnect()) {
            showProgress()
            strDate = common.convertDateInFormat(
                binding.venueStartDate.edtText.text.toString(),
                Constant().ddMmmYyyy,
                Constant().yyyyMmDd
            )
            strTime = common.convertDateInFormat(
                binding.venueStartTime.edtText.text.toString(),
                Constant().hhMmA,
                Constant().HHmmss24hrs
            )
            endSecTime = common.convertDateInFormat(
                binding.venueEndTime.edtText.text.toString(),
                Constant().hhMmA,
                Constant().HHmmss24hrs
            )

            val call: Call<VenueBookRes?>?
            if (type == 1) {
                if (dialog!!.isShowing) {
                    dialog!!.hide()
                }
                call = apiInterface.getBookVenue(
                    PrefManager.getAccessToken(), BookVenueReq(
                        cardNumber, cardCcvNo,
                        "$strDate", expireMonthStr, ticketId!!, specialRequest!!,
                        "$strDate $strTime", expireYearStr,
                        binding.venueNumPeople.edtText.text.toString().toInt())
                )
            } else {
                call = apiInterface.getBookVenue(
                    PrefManager.getAccessToken(), BookVenueReq(
                        specialRequest!!,
                        "$strDate", ticketId!!, "$strDate $strTime",
                        binding.venueNumPeople.edtText.text.toString().toInt()
                    )
                )
            }
            call!!.enqueue(object : Callback<VenueBookRes?> {
                override fun onResponse(
                    call: Call<VenueBookRes?>,
                    response: Response<VenueBookRes?>
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            if(venueEvent!!.need_booking_confirmation==1) {
                                openSuccessDialog(
                                    "Reservation received.",
                                    "Your booking is awaiting confirmation, and we’ll keep you updated as soon as we have more information. Thank you!"
                                )
                            }else{
                                openSuccessDialog(
                                    "Booking accepted.",
                                    "Thank you"
                                )
                            }
                        } else {
                            openFailerDialog("Reservation failed.",response.body()!!.message!!);
                        }
                        countDownTimer!!.cancel()
                    } else {
                        openFailerDialog("Reservation failed.",response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<VenueBookRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun openSuccessDialog(title:String,message: String) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pass_success),
                title,
                message,
                "",
                getString(R.string.txt_done),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                infoDialogListener
            )
        commonInfoBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }

    private fun openFailerDialog(title:String,message: String) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pass_success),
                title,
                message,
                "",
                getString(R.string.txt_done),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                cancelListener
            )
        commonInfoBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
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
                MyBookingsFragment.newInstance(Constant().venueBooking),
                MyBookingsFragment::class.simpleName
            )
        }

    }

    var cancelListener: InfoDialogListener = object : InfoDialogListener() {

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

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidate(): Boolean {
        var isValid = true
        startDate = binding.venueStartDate.edtText.getText().toString()
        startTime = binding.venueStartTime.edtText.getText().toString()
        endTime = binding.venueStartTime.edtText.getText().toString()
        specialRequest = binding.venueSpecialRequests.edtName.text.toString()
        if (bookingPosition == -1) {
            isValid = false
            binding.venueBookType.tvError.visibility = View.VISIBLE
            binding.venueBookType.tvError.text = getString(R.string.error_venue_booking_type)
        }
        if (startDate!!.isEmpty()) {
            isValid = false
            binding.venueStartDate.tvError.visibility = View.VISIBLE
            binding.venueStartDate.tvError.text = getString(R.string.txt_err_enter_start_date)
        }
        if (startTime!!.isEmpty()) {
            isValid = false
            binding.venueStartTime.tvError.visibility = View.VISIBLE
            binding.venueStartTime.tvError.text = getString(R.string.txt_err_enter_start_time)
        }

        if (numberPeople == -1) {
            isValid = false
            binding.venueNumPeople.tvError.visibility = View.VISIBLE
            binding.venueNumPeople.tvError.setText(R.string.error_ener_num_of_people)
        }

        strDate = common.convertDateInFormat(
            binding.venueStartDate.edtText.text.toString(),
            Constant().ddMmmYyyy,
            Constant().yyyyMmDd
        )
        strTime = common.convertDateInFormat(
            binding.venueStartTime.edtText.text.toString(),
            Constant().hhMmA,
            Constant().HHmmss24hrs
        )

        if(bookingPosition>=0) {
            if (isWithinRange(
                    startTimeStr = venueEvent?.tickets?.get(bookingPosition)!!.start_time,
                    endTimeStr = venueEvent?.tickets?.get(bookingPosition)!!.end_time,
                    checkTimeStr = strDate + " " + strTime
                )
            ) {
            } else {
                commonDialog = CommonDialog(
                    mActivity!!,
                    getString(R.string.lbl_ok),
                    "",
                    "",
                    "Sorry, reservation is not available at this time. Try again."
                )
                commonDialog!!.show()
                commonDialog!!.binding.btnPositive.setOnClickListener {
                    commonDialog!!.dismiss()
                }
                isValid = false
            }
        }else{

        }

       // venueEvent.tickets[bookingPosition].start_time
       /*if (checkTimings(startTime!!, endTime!!)) {
            isValid = false
            binding.venueStartTime.tvError.visibility = View.VISIBLE
            binding.venueStartTime.tvError.setText(R.string.txt_error_until_time_ends_same_day)
        }*/
        return isValid
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun isWithinRange(
        startTimeStr: String?,
        endTimeStr: String?,
        checkTimeStr: String
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val startTime = LocalDateTime.parse(startTimeStr, formatter)
        val endTime = LocalDateTime.parse(endTimeStr, formatter)
        val checkTime = LocalDateTime.parse(checkTimeStr, formatter)

        return checkTime.isAfter(startTime) && checkTime.isBefore(endTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isWithinTimeRange(
        startTimeStr: String?,
        endTimeStr: String?,
        checkTimeStr: String
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        val startTime = LocalTime.parse(startTimeStr, formatter);
        val endTime = LocalTime.parse(endTimeStr, formatter);
        val checkTime = LocalTime.parse(checkTimeStr, formatter);
        return checkTime.isAfter(startTime) && checkTime.isBefore(endTime)
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
                // showCloseDialog()
            }
        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }


    private fun openCardDetailPopup() {
        dialog = Dialog(mActivity!!, R.style.Theme_AppCompat_Dialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        val dialogBinding: DialogBookNowCardDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_book_now_card_detail,
            null,
            false
        )
        dialog!!.setContentView(dialogBinding.root)
        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.window!!.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorTransparent)
        dialogBinding.etCardNumber.addTextChangedListener(FourDigitCardFormatWatcher())
        dialogBinding.btnCancel.setOnClickListener { dialog!!.dismiss() }

        dialogBinding.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }

        dialogBinding.etCardExpireDate.edtText.setOnClickListener {
            createDialogWithoutDateField { _, year, month, _ ->
                val expireMonth = month + 1
                expireYearStr = year.toString()
                expireMonthStr = expireMonth.toString()
                val date = String.format("%02d/%s", expireMonth, expireYearStr)
                dialogBinding.etCardExpireDate.edtText.setText(date)
            }.show()
        }
        dialogBinding.btnPayNow.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(view: View) {
                hideKeyBoard(requireActivity())
                if (isValidInput) {
                    cardNumber = dialogBinding.etCardNumber.getText().toString()
                    cardCcvNo = dialogBinding.etCardCvv.getText().toString()
                    dialog!!.dismiss()
                    callBookingVenue(1)
                }
            }

            private val isValidInput: Boolean
                get() {
                    dialogBinding.tvErrorCardName.visibility = View.GONE
                    dialogBinding.tvErrorCardNumber.visibility = View.GONE
                    dialogBinding.etCardExpireDate.tvError.visibility = View.INVISIBLE
                    dialogBinding.tvErrorCvvNumber.visibility = View.INVISIBLE
                    dialogBinding.tvErrorAdress.visibility = View.GONE
                    dialogBinding.tvErrorPostCode.visibility = View.GONE
                    var isValid = true
                    if (dialogBinding.etCardName.getText().toString().isEmpty()) {
                        dialogBinding.tvErrorCardName.visibility = View.VISIBLE
                        isValid = false
                    }
                    if (dialogBinding.etCardNumber.getText().toString().isEmpty()) {
                        dialogBinding.tvErrorCardNumber.visibility = View.VISIBLE
                        isValid = false
                    }
                    if (dialogBinding.etCardExpireDate.edtText.text.toString().isEmpty()) {
                        dialogBinding.etCardExpireDate.tvError.visibility = View.VISIBLE
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

    @SuppressLint("SimpleDateFormat")
    private fun checkTimings(startTime: String, endTime: String): Boolean {
        var isValid = false
        val pattern = DateTimePicker.FORMATS.TIME_AP
        val sdf = SimpleDateFormat(pattern)
        try {
            val date1: Date = sdf.parse(startTime)!!
            val date2: Date = sdf.parse(endTime)!!
            isValid = date1.after(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return isValid
    }

}
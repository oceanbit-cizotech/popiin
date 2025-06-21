package com.popiin.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.bottomDialogFragment.SelectDateTimeBottomFragment
import com.popiin.databinding.BottomFragmentSelectDateTimeBinding
import com.popiin.databinding.CalendarDayLayoutBinding
import com.popiin.databinding.DialogCalenderBinding
import com.popiin.databinding.FragmentEditReservationBinding
import com.popiin.listener.InfoDialogListener
import com.popiin.req.CreateVenueTicketsReq
import com.popiin.req.TicketReq
import com.popiin.req.VenueReserveReq
import com.popiin.res.CreateStripeLinkRes
import com.popiin.res.CreateVenueTicketsRes
import com.popiin.res.UpdateVenueTicketRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueReserveRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.suke.widget.SwitchButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


class EditReserveFragment : BaseFragment<FragmentEditReservationBinding>() {
    private var list: ArrayList<VenueListRes.Tickets> = ArrayList()
    private var checkConfirmStatus: Int = -1

    private lateinit var ticketType: Array<String>
    private var ticketTypeSelected = 0
    private var strTicketType = ""
    private var isVenueReservation = false
    private var depositRequired: Int = 1
    override fun getLayout(): Int {
        return R.layout.fragment_edit_reservation
    }

    companion object {
        var item: VenueListRes.Venue? = null
        fun newInstance(venue: VenueListRes.Venue): EditReserveFragment {
            item = venue
            return EditReserveFragment()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        ticketType = resources.getStringArray(R.array.table_type)

        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_item, ticketType)
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.venueBookingOptions.spinnerBookingType.adapter = aa


        binding.btnSubmit.setOnClickListener {
            if (isValidate()) {
                val addTicket: ArrayList<TicketReq> = ArrayList()
                val updateTicket: ArrayList<TicketReq> = ArrayList()
                if (list.size > 0) {
                    for (i in list.indices) {
                        val ticketReq = list[i]
                        if (ticketReq.id > 0) {
                            // update
                            // validation logic
                            setDataForApi(ticketReq)
                            updateTicket.add(getUpdateTicketRequest(list[i]))
                        } else {
                            // validation logic
                            // create
                            setDataForApi(ticketReq)
                            addTicket.add(getTicketRequest(list[i]))
                        }
                    }
                }

                if (addTicket.size > 0) {
                    // call add ticket
                    isVenueReservation = true
                    callAddTicketApi(addTicket)
                }

                if (updateTicket.size > 0) {
                    // call update ticket
                    isVenueReservation = true
                    callUpdateTicketApi(updateTicket)
                }
            }
        }

        binding.venueBookingOptions.spinnerBookingType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {

                    (view as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorHeaderText
                        )
                    )

                    ticketTypeSelected = position
                    strTicketType = ticketType[position]
                    if (position == ticketType.size - 1) {
                        binding.venueBookingOptions.tvError.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.venueDepositRequired.switchButton.setOnCheckedChangeListener(switchListener)

        binding.venueStartDateTime.edtText.setOnClickListener {
            openCalenderDialog(binding.venueStartDateTime.edtText)
        }

        binding.venueEndDateTime.edtText.setOnClickListener {
            openCalenderDialog(binding.venueEndDateTime.edtText)
        }

        binding.tvReservationName.text = item!!.venue_name

        binding.switchVenueConfirm.isChecked = item!!.need_booking_confirmation == 1

        binding.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            checkConfirmStatus = if (isChecked) {
                1
            } else {
                0
            }
            callAutoVenueConfirmApi(checkConfirmStatus)
        }



        if (item!!.tickets!!.size > 0) {
            list.addAll(item!!.tickets!!)
            setReserveData(item!!.tickets!![0])
        } else {
            list.clear()
            list.add(VenueListRes.Tickets())
        }
    }

    private var switchListener: SwitchButton.OnCheckedChangeListener =
        SwitchButton.OnCheckedChangeListener { _, isChecked ->

            if (PrefManager.getUser().user!!.is_stripe_verified ==0) {
                if (isChecked) {
                    showCommonInfoBottomFragment(
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_payment_header),
                        getString(R.string.txt_add_payment_details),
                        getString(R.string.txt_payment_desc),
                        getString(
                            R.string.txt_no_cancel
                        ),
                        getString(R.string.txt_yes_add),
                        true,
                        infoDialogListener
                    )
                    // open add payment dialog
                }
            } else {
                if (isChecked) {
                    depositRequired = 1
                    binding.venueDepositAmount.clAbout.visibility = View.VISIBLE
                } else {
                    depositRequired = 1
                    binding.venueDepositAmount.clAbout.visibility = View.GONE
                }
            }
        }

    private fun setDataForApi(ticketReq: VenueListRes.Tickets) {
        ticketReq.name = strTicketType
        ticketReq.booking_text = binding.edtVenueTerms.text.toString()
        val quantity = binding.edtNumberTables.edtName.text.toString().toInt()
        ticketReq.quantity = quantity
        ticketReq.price = if (binding.venueDepositAmount.edtName.text.toString()
                .isEmpty()
        ) 0.0 else binding.venueDepositAmount.edtName.text.toString().toDouble()
        ticketReq.start_time = common.convertDateInFormat(
            binding.venueStartDateTime.edtText.text.toString(),
            Constant().ddMmYyyyHhMmA,
            Constant().yyyyMmDdHhMmSs
        )
        ticketReq.end_time = common.convertDateInFormat(
            binding.venueEndDateTime.edtText.text.toString(),
            Constant().ddMmYyyyHhMmA,
            Constant().yyyyMmDdHhMmSs
        )
        ticketReq.is_deposite_required = if (binding.venueDepositAmount.clAbout.isVisible) 1 else 0
    }

    private fun setReserveData(tickets: VenueListRes.Tickets) {
        binding.edtNumberTables.edtName.text = "" + tickets.quantity
        binding.edtVenueTerms.text = tickets.booking_text
        binding.venueStartDateTime.edtText.setText(
            common.convertDateInFormat(
                tickets.start_time!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmYyyyHhMmA
            )
        )
        binding.venueEndDateTime.edtText.setText(
            common.convertDateInFormat(
                tickets.end_time!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmYyyyHhMmA
            )
        )

        for (i in ticketType.indices) {
            if (tickets.name.equals(ticketType[i])) {
                binding.venueBookingOptions.spinnerBookingType.setSelection(i, true)
                break
            }
        }

        if (tickets.is_deposite_required == 1) {
            binding.venueDepositRequired.switchButton.isChecked = true
            binding.venueDepositAmount.root.visibility = View.VISIBLE
            binding.venueDepositAmount.edtName.text = ("" + tickets.price.toInt())
        } else {
            binding.venueDepositRequired.switchButton.isChecked = false
            binding.venueDepositAmount.root.visibility = View.GONE
        }

    }

    private fun isValidate(): Boolean {
        var isValid = true

        if (ticketTypeSelected == 0) {
            isValid = false
            binding.venueBookingOptions.tvError.visibility = View.VISIBLE
            binding.venueBookingOptions.tvError.text =
                getString(R.string.txt_err_select_booking_option)
        }

        if (binding.edtNumberTables.edtName.text.toString().isEmpty()) {
            isValid = false
            binding.edtNumberTables.tvError.visibility = View.VISIBLE
            binding.edtNumberTables.tvError.text = getString(R.string.txt_err_enter_num_of_tables)
        }

        if (binding.venueStartDateTime.edtText.text.toString().isEmpty()) {
            isValid = false
            binding.venueStartDateTime.tvError.visibility = View.VISIBLE
            binding.venueStartDateTime.tvError.text = getString(R.string.txt_err_enter_start_date_time)
        }

        if (binding.venueEndDateTime.edtText.text.toString().isEmpty()) {
            isValid = false
            binding.venueEndDateTime.tvError.visibility = View.VISIBLE
            binding.venueEndDateTime.tvError.text = getString(R.string.txt_err_enter_end_date_time)
        }

        if (binding.venueDepositAmount.clAbout.isVisible) {
            if (binding.venueDepositAmount.edtName.text.toString().isEmpty()) {
                isValid = false
                binding.venueDepositAmount.tvError.visibility = View.VISIBLE
                binding.venueDepositAmount.tvError.text =
                    getString(R.string.txt_err_deposit_amount_required)
            }
        }

        return isValid

    }

    private var today: LocalDate? = null
    private var selectedDate: LocalDate? = null
    private var currentTime = ""
    private var eventTime = ""
    private var hour: String = ""
    private var minute: String = ""
    private var amPm: String = ""
    private var startDate: String = ""
    private lateinit var hours: List<String>
    private lateinit var minutes: List<String>
    private lateinit var amPms: List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalenderDialog(edtText: EditText) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogCalenderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_calender, null, false
        )

        val currentMonth = YearMonth.now()
        val currentDate = LocalDate.now()
        today = currentDate
        selectedDate = currentDate

        val monthScroll: (CalendarMonth) -> Unit = { calendarMonth ->
            binding.inclCalender.tvMonthYear.text =
                "${calendarMonth.yearMonth.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${calendarMonth.yearMonth.year}"
        }

        binding.inclCalender.calenderView.setup(
            currentMonth,
            currentMonth.plusMonths(100),
            DayOfWeek.SUNDAY
        )
        binding.inclCalender.calenderView.scrollToMonth(currentMonth)
        binding.inclCalender.calenderView.scrollToDate(currentDate)
        binding.inclCalender.calenderView.monthScrollListener = monthScroll

        val cal = Calendar.getInstance()
        val timeHour = cal.get(Calendar.HOUR_OF_DAY)
        val timeMinute = cal.get(Calendar.MINUTE)
        val timeSeconds = cal.get(Calendar.SECOND)

        val time = "$timeHour:$timeMinute:$timeSeconds"
        currentTime = common.convertDateInFormat(time, Constant().HHmmss24hrs, Constant().hhMmA)!!

        binding.tvCalTime.text = currentTime

        configureBinders(binding)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        var combinedDate: String
        var formattedDate: String?

        binding.btnSelectDate.setOnClickListener {
            dialog.dismiss()
            if (startDate.isEmpty() && eventTime.isEmpty()) {
                combinedDate = "$today $currentTime"
            } else if (startDate.isEmpty()) {
                combinedDate = "$today $eventTime"
            } else if (eventTime.isEmpty()) {
                combinedDate = "$startDate $currentTime"
            } else {
                combinedDate = "$startDate $eventTime"
            }

            formattedDate = common.convertDateInFormat(
                combinedDate,
                Constant().yyyyMmDdHhMmA,
                Constant().ddMmYyyyHhMmA
            )
            edtText.setText(formattedDate)
        }

        binding.inclCalender.ivCalLeft.setOnClickListener {
            binding.inclCalender.ivCalLeft.isEnabled = false
            Handler(Looper.myLooper()!!).postDelayed({
                val leftMonth = binding.inclCalender.calenderView.findFirstVisibleMonth()
                val leftYearMonth = leftMonth!!.yearMonth

                if (leftYearMonth != currentMonth) {
                    binding.inclCalender.calenderView.smoothScrollToMonth(leftYearMonth.previousMonth)
                }

                binding.inclCalender.tvMonthYear.text =
                    "${leftMonth.yearMonth.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${leftMonth.yearMonth.year}"
                binding.inclCalender.ivCalLeft.isEnabled = true
            }, 200)
        }

        binding.inclCalender.ivCalRight.setOnClickListener {
            binding.inclCalender.ivCalRight.isEnabled = false
            Handler(Looper.myLooper()!!).postDelayed({
                val rightMonth = binding.inclCalender.calenderView.findFirstVisibleMonth()
                val rightYearMonth = rightMonth!!.yearMonth

                binding.inclCalender.calenderView.smoothScrollToMonth(rightYearMonth.nextMonth)

                binding.inclCalender.tvMonthYear.text =
                    "${rightMonth.yearMonth.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${rightMonth.yearMonth.year}"
                binding.inclCalender.ivCalRight.isEnabled = true
            }, 200)
        }

        binding.tvCalTime.setOnClickListener {
            binding.clTimeDialog.visibility =
                if (binding.clTimeDialog.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        hours = resources.getStringArray(R.array.hour).toList()
        minutes = resources.getStringArray(R.array.minutes).toList()
        amPms = resources.getStringArray(R.array.am_pm).toList()

        binding.inclTime.wpHours.data = hours.toMutableList()
        binding.inclTime.wpMinutes.data = minutes.toMutableList()
        binding.inclTime.wpAmPm.data = amPms.toMutableList()

        // ✅ Pre-select current time if EditText is empty
        if (edtText.text.isNullOrBlank()) {
            val parts = currentTime.split(":", " ")
            if (parts.size >= 3) {
                hour = parts[0]
                minute = parts[1]
                amPm = parts[2]

                binding.inclTime.wpHours.selectedItemPosition = hours.indexOf(hour)
                binding.inclTime.wpMinutes.selectedItemPosition = minutes.indexOf(minute)
                binding.inclTime.wpAmPm.selectedItemPosition = amPms.indexOf(amPm)

                eventTime = "$hour:$minute $amPm"
                binding.tvCalTime.text = eventTime
            }
        }

        binding.inclTime.wpHours.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                hour = data.toString()
                binding.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }
            override fun onWheelScrolled(offset: Int) {}
            override fun onWheelSelected(position: Int) {}
            override fun onWheelScrollStateChanged(state: Int) {}
        })

        binding.inclTime.wpMinutes.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                minute = data.toString()
                binding.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }
            override fun onWheelScrolled(offset: Int) {}
            override fun onWheelSelected(position: Int) {}
            override fun onWheelScrollStateChanged(state: Int) {}
        })

        binding.inclTime.wpAmPm.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                amPm = data.toString()
                binding.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }
            override fun onWheelScrolled(offset: Int) {}
            override fun onWheelSelected(position: Int) {}
            override fun onWheelScrollStateChanged(state: Int) {}
        })

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureBinders(binding: DialogCalenderBinding?) {
        val calendarView = binding!!.inclCalender.calenderView

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).exFourDayText

            init {
                textView.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate == day.date) {
                            selectedDate = day.date
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(oldDate) }
                        }
                        // set date text here
                        startDate = selectedDate.toString()
                    }
                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                if (container.day.position == DayPosition.OutDate || container.day.position == DayPosition.InDate) {
                    container.textView.setTextColor(
                        ContextCompat.getColor(requireActivity(), R.color.colorGrey)
                    )
                }

                if (data.position == DayPosition.MonthDate) {
                    when (data.date) {
                        selectedDate -> {
                            textView.setTextColor(
                                ContextCompat.getColor(requireActivity(), R.color.colorLightBlue)
                            )
                            textView.background =
                                ContextCompat.getDrawable(requireActivity(), R.drawable.bg_calender_day)
                        }
                        today -> {
                            textView.setTextColor(
                                ContextCompat.getColor(requireActivity(), R.color.colorWhite)
                            )
                            textView.background =
                                ContextCompat.getDrawable(requireActivity(), R.drawable.bg_calender_today)
                        }
                        else -> {
                            textView.setTextColor(
                                ContextCompat.getColor(requireActivity(), R.color.colorBlack)
                            )
                            textView.background = null
                        }
                    }
                }

                if (container.day.date.isBefore(LocalDate.now())) {
                    container.textView.setOnClickListener(null)
                    container.textView.setTextColor(
                        ContextCompat.getColor(requireActivity(), R.color.colorGrey)
                    )
                }
            }
        }

        calendarView.scrollToDate(LocalDate.now())
    }


    private fun callAutoVenueConfirmApi(checkConfirm: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueReserveRes?>? = apiInterface.doAutoVenueConfirm(
                PrefManager.getAccessToken(),
                VenueReserveReq(checkConfirm, item!!.id)
            )
            call!!.enqueue(object : Callback<VenueReserveRes?> {
                override fun onResponse(
                    call: Call<VenueReserveRes?>,
                    response: Response<VenueReserveRes?>,
                ) {
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueReserveRes?>, t: Throwable) {
                    onApiFailure(throwable = t )
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    private fun getUpdateTicketRequest(tickets: VenueListRes.Tickets): TicketReq {
        return TicketReq(
            tickets.is_deposite_required,
            tickets.quantity,
            tickets.end_time!!, tickets.start_time!!, tickets.price.toInt(),
            tickets.name!!, tickets.id, tickets.booking_text
        )
    }

    private fun getTicketRequest(tickets: VenueListRes.Tickets): TicketReq {
        return TicketReq(
            tickets.is_deposite_required,
            tickets.available_quantity,
            tickets.quantity,
            tickets.end_time!!,
            tickets.start_time!!,
            tickets.price.toInt(),
            tickets.name!!,
            tickets.booking_text
        )
    }

    private fun callUpdateTicketApi(updateTicket: ArrayList<TicketReq>) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doUpdateVenueTickets(
                PrefManager.getAccessToken(),
                CreateVenueTicketsReq(item!!.id, updateTicket)
            )
            call!!.enqueue(object : Callback<UpdateVenueTicketRes?> {
                override fun onResponse(
                    call: Call<UpdateVenueTicketRes?>,
                    response: Response<UpdateVenueTicketRes?>
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            showCommonInfoBottomFragment(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.ic_pass_success
                                ),
                                getString(R.string.txt_venue_reservation_title),
                                response.body()!!.message!!,
                                "",
                                getString(R.string.txt_done),
                                false,
                                infoDialogListener
                            )
                        } else {
                            showToast(response.body()!!.message)
//                            CommonDialogBuilder(
//                                "",
//                                response.body()!!.message,
//                                getActivity().getResources().getString(R.string.lbl_ok)
//                            )
//                                .setListner(object : CommonDialogListner() {
//                                    fun onPositiveButtonClick(item: Any?) {
//                                        mActivity.supportFragmentManager.popBackStack()
//                                    }
//                                })
//                                .show(mActivity)
                        }
                    } else {
                        common.openDialog(
                            requireActivity(), response.body()!!.message
                        )
                    }
                }

                override fun onFailure(call: Call<UpdateVenueTicketRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    private fun callAddTicketApi(addTicket: ArrayList<TicketReq>) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doCreateVenueTickets(
                PrefManager.getAccessToken(),
                CreateVenueTicketsReq(addTicket, item!!.id)
            )
            call!!.enqueue(object : Callback<CreateVenueTicketsRes?> {
                override fun onResponse(
                    call: Call<CreateVenueTicketsRes?>,
                    response: Response<CreateVenueTicketsRes?>
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.isSuccess) {
                            showCommonInfoBottomFragment(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.ic_pass_success
                                ),
                                getString(R.string.txt_venue_reservation_title),
                                response.body()!!.message!!,
                                "",
                                getString(R.string.txt_done),
                                false,
                                infoDialogListener
                            )
                        }
                    } else {
                        showToast(response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<CreateVenueTicketsRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        isPayment: Boolean,
        listener: InfoDialogListener
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                if (isPayment) ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorSecondaryText
                ) else ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                isPayment,
                listener
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
            if (isVenueReservation) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                getCreateStripeLink(true)
            }

        }

    }

    private fun getCreateStripeLink(isLoaderDisplay: Boolean) {
        if (isLoaderDisplay) {
            showProgress()
        }
        if (!isInternetConnect()) {
            return
        }

        val call: Call<CreateStripeLinkRes?>? = apiInterface.createStripeLink(
            PrefManager.getAccessToken(),
        )
        call!!.enqueue(object : Callback<CreateStripeLinkRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CreateStripeLinkRes?>,
                response: Response<CreateStripeLinkRes?>,
            ) {
                if (isValidResponse(response)) {
                    if (response.body()?.success == true) {
                        var url = response.body()!!.data.url
                        val i = Intent(Intent.ACTION_VIEW)
                        i.setData(Uri.parse(url))
                        startActivity(i)
                        hideProgress()

                    }else{
                        hideProgress()
                    }
                }

            }

            override fun onFailure(call: Call<CreateStripeLinkRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }


}
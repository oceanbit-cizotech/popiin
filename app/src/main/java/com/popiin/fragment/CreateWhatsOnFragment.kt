package com.popiin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.WhatsonReserveBookingInfoAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.*
import com.popiin.listener.AdapterWhatsReserveItemListener
import com.popiin.listener.DialogListener
import com.popiin.model.WhatsReserveTicketModel
import com.popiin.req.VenueDetailsReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueOfferRes
import com.popiin.res.VenueWhatsListRes
import com.popiin.util.AttachmentHelper
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.popiin.views.ImageVideoView
import com.popiin.views.ImageVideoView.OnViewClickListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.popiin.flowlayout.FlowLayout
import com.popiin.flowlayout.TagAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList


class CreateWhatsOnFragment : BaseFragment<FragmentWhatsOnDetailBinding>() {
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private lateinit var hours: List<String>
    private lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    private var mInflater: LayoutInflater? = null
    private var hour: String? = ""
    private var minute: String? = ""
    private var amPm: String? = ""

    // private val requestImage = 100
    private lateinit var venueMusicAdapter: com.popiin.flowlayout.TagAdapter<*>
    private lateinit var entertainmentAdapter: com.popiin.flowlayout.TagAdapter<*>
    private lateinit var venueMusic: Array<String>
    private lateinit var entertainment: Array<String>
    private var offers: ArrayList<VenueListRes.Offerslive> = ArrayList()
    private var strMusic = ""
    private var reqMusic = ""
    private var whatsOnId: String? = null
    private var strEntertainment = ""
    private var reqEntertainment = ""
    private val permissionAll = 1
    private val addImages: ArrayList<ImageVideoView> = ArrayList()
    private val whatsOnReserveBookList: ArrayList<VenueWhatsListRes.Whatsonticket> = ArrayList()
    private val addTicket: ArrayList<WhatsReserveTicketModel> = ArrayList()
    private val updateTicket: ArrayList<WhatsReserveTicketModel> = ArrayList()
    private var counter = -1
    private val requestPdf = 101
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private var imageTypes = ""
    private var isBasicInfoVerified = false
    private var isWhatsOnBooking = true
    private var isOtherDetailVerified = true
    private var isBookingInfoVerified = true
    private lateinit var whatsReserveBookAdapter: WhatsonReserveBookingInfoAdapter

    override fun getLayout(): Int {
        return R.layout.fragment_whats_on_detail
    }

    companion object {
        var whatsOnData: VenueWhatsListRes.Data? = null
        var selectedDate: String? = null
        var venueId: String? = null
        fun newInstance(
            item: VenueWhatsListRes.Data? = null,
            selectDate: String? = null,
            venuesId: String? = null,
        ): CreateWhatsOnFragment {
            whatsOnData = item
            selectedDate = selectDate
            venueId = venuesId
            return CreateWhatsOnFragment()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        mInflater = LayoutInflater.from(requireActivity())
        venueMusic = resources.getStringArray(R.array.venue_music)
        entertainment = resources.getStringArray(R.array.entertainment)

        setUiWithExpandCollapse()

        whatsReserveBookAdapter = WhatsonReserveBookingInfoAdapter(whatsOnReserveBookList, whatsListener)
        binding.inclWhatsBookingInfo.rvWhatsBookingInfo.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.inclWhatsBookingInfo.rvWhatsBookingInfo.adapter = whatsReserveBookAdapter

        whatsOnReserveBookList.clear()

        if (whatsOnData != null) {
            //all data set
            binding.btnPreview.alpha = 1f
            binding.btnPreview.isEnabled = true
            setWhatsOnData()
        } else {
            // only date set
            binding.inclWhatsBasicInfo.inclStartDate.edtText.setText(
                common.convertDateInFormat(
                    selectedDate,
                    Constant().yyyyMmDd,
                    Constant().ddMmmYyyy
                )
            )
            binding.inclOtherDetails.uiVerified.visibility = View.GONE
            binding.inclBookingInfo.uiVerified.visibility = View.GONE
            binding.btnPreview.alpha = 0.5f
            binding.btnPreview.isEnabled = false

            whatsOnReserveBookList.add(VenueWhatsListRes.Whatsonticket())

        }

        binding.inclWhatsBookingInfo.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if(isValidateTicketBookingInfo()) {
                binding.inclWhatsBookingInfo.root.visibility = View.GONE
                if (isWhatsOnBooking && whatsOnReserveBookList.size > 0) {
                    var ticketReq: VenueWhatsListRes.Whatsonticket? = null
                    addTicket.clear()
                    updateTicket.clear()
                    for (i in 0 until whatsOnReserveBookList.size) {
                        ticketReq = whatsOnReserveBookList[i]
                        if (ticketReq.quantity > 0 && ticketReq.price !=null && ticketReq.ticket_type != null && ticketReq.ticket_type!!.isNotEmpty()) {
                            if (ticketReq.id > 0) {
                                updateTicket.add(updateTicketReq(ticketReq))
                            } else {
                                // add ticket
                                addTicket.add(addTicketReq(ticketReq))

                            }
                        }
                    }
                }

            }
        }

        binding.inclWhatsBookingInfo.btnAdd.setOnClickListener {
            hideKeyBoard(requireActivity())
            isWhatsOnBooking = true
            whatsOnReserveBookList.add(VenueWhatsListRes.Whatsonticket())
            whatsReserveBookAdapter.notifyDataSetChanged()
        }

        binding.inclWhatsBasicInfo.inclStartDate.edtText.setOnClickListener {
            openCalenderDialog(binding.inclWhatsBasicInfo.inclStartDate.edtText)
        }

        binding.inclWhatsBasicInfo.inclEndDate.edtText.setOnClickListener {
            openCalenderDialog(binding.inclWhatsBasicInfo.inclEndDate.edtText)
        }

        binding.inclVenueOtherDetails.inclMusic.edtText.setOnClickListener {
            openMusicDialog()
        }

        binding.inclVenueOtherDetails.inclEventEntertainment.edtText.setOnClickListener {
            openEntertainmentDialog()
        }

        if (!hasPermissions(requireActivity(), *permissions)) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, permissionAll)
        }

        binding.inclWhatsBasicInfo.inclStartTime.edtText.setOnClickListener {
            // open timepicker dialog
            if (binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString().isEmpty()) {
                hour = null
                minute = null
                amPm = null
            } else {
                hour = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().hh
                )
                minute = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().mm
                )
                amPm = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().a
                )
            }
            openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                    super.onSelectedTime(hours, minutes, ampms)
                    val startTime = "$hours:$minutes$ampms"
                    binding.inclWhatsBasicInfo.inclStartTime.edtText.setText(startTime)
                }
            })
        }

        binding.inclWhatsBasicInfo.inclEndTime.edtText.setOnClickListener {
            // open timepicker dialog
            if (binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString().isEmpty()) {
                hour = null
                minute = null
                amPm = null
            } else {
                hour = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().hh
                )
                minute = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().mm
                )
                amPm = common.convertDateInFormat(
                    binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().a
                )
            }
            openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                    super.onSelectedTime(hours, minutes, ampms)
                    val endTime = "$hours:$minutes$ampms"
                    binding.inclWhatsBasicInfo.inclEndTime.edtText.setText(endTime)
                }
            })
        }



        binding.inclWhatsBasicInfo.imgAddImage.setOnClickListener {
            imageTypes = CONSTANT.IMAGE
            imageGetterPopup()
        }

        binding.inclWhatsBasicInfo.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateBasicInfo()) {
                binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
                binding.inclWhatsBasicInfo.root.visibility = View.GONE
                binding.btnPreview.alpha = 1f
                binding.btnPreview.isEnabled = true
                isBasicInfoVerified = true

            }
        }


        binding.inclVenueOtherDetails.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            binding.inclVenueOtherDetails.root.visibility = View.GONE
        }



        binding.btnPreview.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isBasicInfoVerified && isOtherDetailVerified && isBookingInfoVerified) {
                callVenueOfferApi(venueId)
            }
        }

        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclTitle.edtName,
            binding.inclWhatsBasicInfo.inclTitle.tvError
        )
        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclDescription.edtText,
            binding.inclWhatsBasicInfo.inclDescription.tvError
        )
        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclStartDate.edtText,
            binding.inclWhatsBasicInfo.inclStartDate.tvError
        )
        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclEndDate.edtText,
            binding.inclWhatsBasicInfo.inclEndDate.tvError
        )
        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclStartTime.edtText,
            binding.inclWhatsBasicInfo.inclStartTime.tvError
        )
        common.handleErrorView(
            binding.inclWhatsBasicInfo.inclEndTime.edtText,
            binding.inclWhatsBasicInfo.inclEndTime.tvError
        )
    }

    private fun addTicketReq(ticket: VenueWhatsListRes.Whatsonticket): WhatsReserveTicketModel {
        return WhatsReserveTicketModel(ticket.ticket_type!!,
            ticket.price!!,
            ticket.quantity,
            ticket.venue_id)
    }

    private fun updateTicketReq(ticket: VenueWhatsListRes.Whatsonticket): WhatsReserveTicketModel {
        return WhatsReserveTicketModel(ticket.ticket_type!!,
            ticket.price!!,
            ticket.quantity,
            ticket.venue_id,
            ticket.whatson_id,
            ticket.id)
    }

    private var whatsListener: AdapterWhatsReserveItemListener<VenueWhatsListRes.Whatsonticket> =
        object : AdapterWhatsReserveItemListener<VenueWhatsListRes.Whatsonticket>() {
            override fun onBookingType(position: Int, bookingType: String?) {
                super.onBookingType(position, bookingType)
                whatsOnReserveBookList[position].ticket_type = bookingType
            }

            override fun onPrice(
                item: VenueWhatsListRes.Whatsonticket,
                position: Int,
                price: String,
            ) {
                super.onPrice(item, position, price)
                whatsOnReserveBookList[position].price = price.toFloat()
            }

            override fun onQuantity(
                item: VenueWhatsListRes.Whatsonticket,
                position: Int,
                quantity: String,
            ) {
                super.onQuantity(item, position, quantity)
                whatsOnReserveBookList[position].quantity = quantity.toInt()
            }

            override fun onDeleteClick(item: VenueWhatsListRes.Whatsonticket, position: Int) {
                super.onDeleteClick(item, position)
                selectedWhatsOnTicketPosition = position
                openDeleteWhatsOnDialog(item.venue_id, item.id)
            }
        }

    var selectedWhatsOnTicketPosition = -1
    @SuppressLint("NotifyDataSetChanged")
    private fun openDeleteWhatsOnDialog(venueId: Int, id: Int) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )


        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.tvPassSuccess.text = getString(R.string.txt_do_you_want_to)
        binding.tvSuccess.text = getString(R.string.txt_remove_reservation)
        binding.ivPassSuccess.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_sure_delete
            )
        )
        binding.ivClose.visibility = View.VISIBLE
        binding.btnNo.visibility = View.VISIBLE
        binding.btnNo.text = getString(R.string.txt_no)
        binding.btnDone.text = getString(R.string.txt_yes_remove)


        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (id != 0) {
                callDeleteWhatsOn(id, venueId)
            } else {
                whatsOnReserveBookList.removeAt(selectedWhatsOnTicketPosition)
                whatsReserveBookAdapter.notifyDataSetChanged()
            }

        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun openDeleteSuccessDialog(msg: String?) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )


        binding.ivClose.setOnClickListener {
            dialog.dismiss()
            var ticketId=whatsOnReserveBookList[selectedWhatsOnTicketPosition];
            common.createWhatsOnModel.addTickets.removeIf { it.id == ticketId.id }
            common.createWhatsOnModel.updateTickets.removeIf { it.id == ticketId.id }
            whatsOnReserveBookList.removeAt(selectedWhatsOnTicketPosition)
            whatsReserveBookAdapter.notifyDataSetChanged()
        }

        binding.btnNo.visibility = View.GONE
        binding.ivPassSuccess.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_pass_success
            )
        )
        binding.ivClose.visibility = View.GONE
        binding.tvPassSuccess.text = getString(R.string.txt_reservation_removed)
        binding.tvSuccess.text = msg


        binding.btnDone.setOnClickListener {
            dialog.dismiss()

            var ticketId=whatsOnReserveBookList[selectedWhatsOnTicketPosition];
            common.createWhatsOnModel.addTickets.removeIf { it.id == ticketId.id }
            common.createWhatsOnModel.updateTickets.removeIf { it.id == ticketId.id }
            whatsOnReserveBookList.removeAt(selectedWhatsOnTicketPosition)
            whatsReserveBookAdapter.notifyDataSetChanged()

        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun callDeleteWhatsOn(id: Int, venueId: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? = apiInterface.deleteWhatsOnReservation(
                PrefManager.getAccessToken(),
                id, venueId
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
//                            whatsOnReserveBookList.removeAt(selectedWhatsOnTicketPosition)
//                            whatsReserveBookAdapter.notifyDataSetChanged()
                            openDeleteSuccessDialog(response.body()!!.msg)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.msg)
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

    lateinit var venuesOfferRes: VenueOfferRes

    private fun callVenueOfferApi(venueId: String?) {
        if (isInternetConnect()) {
            showProgress()
            if (offers.size > 0) {
                offers.clear()
            }

            val call: Call<VenueOfferRes?>? = apiInterface.getVenueOfferList(
                PrefManager.getAccessToken(),
                VenueDetailsReq(venueId!!)
            )
            call!!.enqueue(object : Callback<VenueOfferRes?> {
                override fun onResponse(
                    call: Call<VenueOfferRes?>,
                    response: Response<VenueOfferRes?>,
                ) {
                    if (isValidResponse(response)) {
                        venuesOfferRes = response.body()!!
                        if (response.body()!!.success) {
                            offers.addAll(response.body()!!.data!!.offers!!)
                            setWhatsOnModelData(venuesOfferRes)
                        }

                    }

                    hideProgress()
                }

                override fun onFailure(call: Call<VenueOfferRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun setWhatsOnModelData(venuesOfferRes: VenueOfferRes) {
        common.createWhatsOnModel.title = binding.inclWhatsBasicInfo.inclTitle.edtName.text.toString()
        common.createWhatsOnModel.description = binding.inclWhatsBasicInfo.inclDescription.edtText.text.toString()
        common.createWhatsOnModel.startDate = binding.inclWhatsBasicInfo.inclStartDate.edtText.text.toString()
        common.createWhatsOnModel.endDate = binding.inclWhatsBasicInfo.inclEndDate.edtText.text.toString()
        common.createWhatsOnModel.startTime = binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString()
        common.createWhatsOnModel.endTime = binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString()
        val images = arrayOfNulls<String>(addImages.size)
        for (i in 0 until addImages.size) {
            images[i] = addImages[i].imageUrl
        }
        common.createWhatsOnModel.whatsOnImages = images
        common.createWhatsOnModel.dressCode = binding.inclVenueOtherDetails.inclDressCode.edtText.text.toString()
        common.createWhatsOnModel.entryPolicy = binding.inclVenueOtherDetails.inclEntryPolicy.edtText.text.toString()
        common.createWhatsOnModel.otherInfo = binding.inclVenueOtherDetails.inclOtherInformation.edtText.text.toString()
        common.createWhatsOnModel.venuesOfferRes = venuesOfferRes
        common.createWhatsOnModel.banner= venuesOfferRes.data!!.venue_banner_image!!

        if (addTicket.size > 0) {
            common.createWhatsOnModel.addTickets = ArrayList(addTicket)
            addTicket.clear()
        }

        if (updateTicket.size > 0) {
            common.createWhatsOnModel.updateTickets.clear()
            common.createWhatsOnModel.updateTickets = ArrayList(updateTicket)
            updateTicket.clear()
        }


        if (whatsOnId != null) {
            setFragmentAdd(
                WhatsOnPreviewFragment.newInstance(whatsOnId!!),
                WhatsOnPreviewFragment::class.java.simpleName
            )
        } else {
            setFragmentAdd(
                WhatsOnPreviewFragment.newInstance(),
                WhatsOnPreviewFragment::class.java.simpleName
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalenderDialog(edtText: EditText) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogWhatsOnCalenderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_whats_on_calender, null, false
        )

        val currentMonth = YearMonth.now()

        val monthScroll: Function1<CalendarMonth, Unit> = { calendarMonth: CalendarMonth ->
            binding.inclCalender.tvMonthYear.text =
                String.format(calendarMonth.yearMonth.month.toString() + " " + calendarMonth.yearMonth.year.toString())
                    .lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        binding.inclCalender.calenderView.setup(
            currentMonth,
            currentMonth.plusMonths(100),
            DayOfWeek.SUNDAY
        )
        binding.inclCalender.calenderView.scrollToMonth(currentMonth)
        binding.inclCalender.calenderView.monthScrollListener = monthScroll

        configureBinders(binding)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }


        binding.btnSelectDate.setOnClickListener {
            dialog.dismiss()
            if (date.isEmpty()) {
                edtText.setText(
                    common.convertDateInFormat(
                        today.toString(),
                        Constant().yyyyMmDd, Constant().ddMmmYyyy
                    )
                )
            } else {
                edtText.setText(
                    common.convertDateInFormat(
                        date,
                        Constant().yyyyMmDd,
                        Constant().ddMmmYyyy
                    )
                )
            }
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
                    String.format(leftMonth.yearMonth.month.toString() + " " + leftMonth.yearMonth.year.toString())
                        .lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

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
                    String.format(rightMonth.yearMonth.month.toString() + " " + rightMonth.yearMonth.year.toString())
                        .lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                binding.inclCalender.ivCalRight.isEnabled = true

            }, 200)
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


    private var selectDate: LocalDate? = null
    var date = ""
    var today: LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    private fun configureBinders(binding: DialogWhatsOnCalenderBinding) {
        val calendarView = binding.inclCalender.calenderView

        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).exFourDayText

            init {
                textView.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectDate == day.date) {
                            selectDate = day.date
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectDate
                            selectDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(oldDate) }
                        }

                        date = selectDate.toString()

                    }

                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun create(view: View) = DayViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                Log.e(TAG, "bind: data: $data")
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                if (container.day.position == DayPosition.OutDate || container.day.position == DayPosition.InDate) {
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(), R.color.colorGrey
                        )
                    )
                }

                if (data.position == DayPosition.MonthDate) {
                    when (data.date) {
                        selectDate -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorLightBlue
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.bg_calender_day
                                )
                        }
                        today -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorWhite
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.bg_calender_today
                                )
                        }
                        else -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorBlack
                                )
                            )
                            textView.background = null
                        }
                    }
                }

                if (container.day.date.isBefore(LocalDate.now())) {
                    container.textView.setOnClickListener { @Suppress("UNUSED_EXPRESSION") null }
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(), R.color.colorGrey
                        )
                    )
                }

            }
        }

    }
    private fun isValidateTicketBookingInfo():Boolean{
        var isValid = true
        for (whatsonticket in whatsOnReserveBookList) {
            if(whatsonticket.ticket_type!=null && whatsonticket.ticket_type!!.isNotEmpty()){
                if(whatsonticket.price==null){
                    isValid=false
                    whatsReserveBookAdapter.notifyDataSetChanged()
                    return isValid
                    break
                }

                if(whatsonticket.quantity==0){
                    isValid=false
                    whatsReserveBookAdapter.notifyDataSetChanged()
                    return isValid
                    break
                }
            }

        }
        whatsReserveBookAdapter.notifyDataSetChanged()
        return isValid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidateBasicInfo(): Boolean {
        var isValid = true

        val title = binding.inclWhatsBasicInfo.inclTitle.edtName.text.toString()
        val description = binding.inclWhatsBasicInfo.inclDescription.edtText.text.toString()
        val startDate = binding.inclWhatsBasicInfo.inclStartDate.edtText.text.toString()
        val endDate = binding.inclWhatsBasicInfo.inclEndDate.edtText.text.toString()
        val startTime = binding.inclWhatsBasicInfo.inclStartTime.edtText.text.toString()
        val endTime = binding.inclWhatsBasicInfo.inclEndTime.edtText.text.toString()

        if (title.isEmpty()) {
            binding.inclWhatsBasicInfo.inclTitle.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclTitle.tvError.text =
                getString(R.string.txt_err_enter_title)
            isValid = false
        }

        if (description.isEmpty()) {
            binding.inclWhatsBasicInfo.inclDescription.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclDescription.tvError.text =
                getString(R.string.txt_err_description)
            isValid = false
        }

        if (startDate.isEmpty()) {
            binding.inclWhatsBasicInfo.inclStartDate.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclStartDate.tvError.text =
                getString(R.string.txt_err_enter_start_date)
            isValid = false
        }

        if (endDate.isEmpty()) {
            binding.inclWhatsBasicInfo.inclEndDate.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclEndDate.tvError.text =
                getString(R.string.txt_err_enter_end_date)
            isValid = false
        }

        if(!common.isValidDateRange(startDate = startDate, endDate = endDate, patternDate = Constant().ddMmmYyyy)){
            showToast("Start date must be before the end date.")
            isValid = false
        }else if(!common.isValidUserTime(userTime = startDate+" "+startTime, userTimeFormat = Constant().ddMmmYyyy+" "+Constant().hhMmA) ){
            showToast("The entered date and time are in the future.")
        }else if(!(common.isValidDateRange(startDate = startTime, endDate = endTime, patternDate = Constant().hhMmA))){
            showToast("Start time must be before the end time.")
            isValid = false
        }

        if (startTime.isEmpty()) {
            binding.inclWhatsBasicInfo.inclStartTime.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclStartTime.tvError.text =
                getString(R.string.txt_err_enter_start_time)
            isValid = false
        }

        if (endTime.isEmpty()) {
            binding.inclWhatsBasicInfo.inclEndTime.tvError.visibility = View.VISIBLE
            binding.inclWhatsBasicInfo.inclEndTime.tvError.text =
                getString(R.string.txt_err_enter_end_time)
            isValid = false
        }

        for (index in addImages.indices) {
            if(addImages[index].imageUrl!=null && addImages[index].imageUrl!!.isEmpty()) {
                showToast(resources.getString(R.string.txt_err_venues_image_uploading))
                isValid = false
                break
            }
        }

        return isValid
    }

    private fun setWhatsOnData() {
        binding.inclWhatsBasicInfo.inclTitle.edtName.text = whatsOnData!!.title!!
        binding.inclWhatsBasicInfo.inclDescription.edtText.text = whatsOnData!!.description!!

        whatsOnId = "" + whatsOnData!!.id

        whatsOnReserveBookList.addAll(whatsOnData!!.whatsonticket!!)

        binding.inclWhatsBasicInfo.inclStartDate.edtText.setText(
            common.convertDateInFormat(
                whatsOnData!!.what_datetime!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmmYyyy
            )
        )
        binding.inclWhatsBasicInfo.inclEndDate.edtText.setText(
            common.convertDateInFormat(
                whatsOnData!!.end_time!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmmYyyy
            )
        )

        binding.inclWhatsBasicInfo.inclStartTime.edtText.setText(
            common.convertDateInFormat(
                whatsOnData!!.what_datetime,
                Constant().yyyyMmDdHhMmSs,
                Constant().hhMmA
            )
        )
        binding.inclWhatsBasicInfo.inclEndTime.edtText.setText(
            common.convertDateInFormat(
                whatsOnData!!.end_time,
                Constant().yyyyMmDdHhMmSs,
                Constant().hhMmA
            )
        )

        for (i in 0 until whatsOnData!!.whatsonimages!!.size) {
            displayImage(whatsOnData!!.whatsonimages!![i].image_url)
        }

        if (whatsOnData!!.music != null) {
            common.createWhatsOnModel.music=whatsOnData!!.music!!

            binding.inclVenueOtherDetails.inclMusic.edtText.text =
                whatsOnData!!.music!!.replace(
                    CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                ).replace(CONSTANT.SEPRATEOR, ", ")
        }

        if (whatsOnData!!.entertainment != null) {
            common.createWhatsOnModel.entertainment=whatsOnData!!.entertainment!!
            binding.inclVenueOtherDetails.inclEventEntertainment.edtText.text =
                whatsOnData!!.entertainment!!.replace(
                    CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                ).replace(CONSTANT.SEPRATEOR, ", ")
        }

        binding.inclVenueOtherDetails.inclDressCode.edtText.text = whatsOnData!!.whatson_dress_code ?: ""
        binding.inclVenueOtherDetails.inclEntryPolicy.edtText.text = whatsOnData!!.whatson_entry_policy ?: ""
        binding.inclVenueOtherDetails.inclOtherInformation.edtText.text = whatsOnData!!.other_information ?: ""

        binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
        binding.inclBookingInfo.uiVerified.visibility = View.VISIBLE
        binding.inclOtherDetails.uiVerified.visibility = View.VISIBLE

        isBasicInfoVerified = true

        binding.inclWhatsBasicInfo.inclStartDate.edtText.isEnabled=false
        binding.inclWhatsBasicInfo.inclEndDate.edtText.isEnabled=false
        binding.inclWhatsBasicInfo.inclStartTime.edtText.isEnabled=false
        binding.inclWhatsBasicInfo.inclEndTime.edtText.isEnabled=false
    }

    private fun openEntertainmentDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogEntertainmentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_entertainment, null, false
        )


        binding.inclEventEntertainment.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (whatsOnData != null) {
                    if( whatsOnData!!.entertainment!=null){
                        setEntertainmentData(binding, whatsOnData!!.entertainment!!)
                    }else{
                        setEntertainmentData(binding, common.createWhatsOnModel.entertainment)
                    }
                } else {
                    setEntertainmentData(binding, common.createWhatsOnModel.entertainment)
                }
                binding.inclEventEntertainment.tegview.visibility = View.VISIBLE
                binding.inclEventEntertainment.tvSave.visibility = View.VISIBLE
            } else {
                binding.inclEventEntertainment.tegview.visibility = View.GONE
                binding.inclEventEntertainment.tvSave.visibility = View.GONE
                binding.inclEventEntertainment.inclOther.root.visibility = View.GONE
                binding.inclEventEntertainment.inclOther.edtText.setText("")
            }
        }


        binding.btnDone.alpha = 0.5f
        binding.btnDone.isEnabled = false

        binding.inclEventEntertainment.tvSave.setOnClickListener {
            if (!binding.inclEventEntertainment.tegview.selectedList.iterator().hasNext()) {
                binding.inclEventEntertainment.tvErrorTags.visibility = View.VISIBLE
                binding.inclEventEntertainment.tvErrorTags.text =
                    getString(R.string.txt_select_one_music_type)
            } else if (binding.inclEventEntertainment.inclOther.root.visibility == View.VISIBLE && binding.inclEventEntertainment.inclOther.edtText.text.isEmpty()) {
                binding.inclEventEntertainment.inclOther.tvError.visibility = View.VISIBLE
                binding.inclEventEntertainment.inclOther.tvError.text =
                    getString(R.string.txt_err_enter_details)
            } else {
                showToast(getString(R.string.txt_entertainment_saved))
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true


            }
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (common.checkVenueTypeValidate(
                    binding.inclEventEntertainment.tegview.selectedList.iterator(),
                    entertainment,
                    binding.inclEventEntertainment.inclOther.edtText,
                    binding.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclEventEntertainment.tegview.selectedList.iterator(),
                    entertainment,
                    binding.inclEventEntertainment.inclOther.edtText,
                    Constant().entertainment
                )
            }
        }

        common.createEventModel.eventEntertainmentNamr =
            binding.inclEventEntertainment.inclOther.edtText.text.toString()

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.inclEventEntertainment.tegview.adapter =
            object : com.popiin.flowlayout.TagAdapter<String?>(entertainment) {
                override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                    val tv = mInflater!!.inflate(
                        R.layout.tv, binding.inclEventEntertainment.tegview, false
                    ) as TextView
                    tv.text = s
                    return tv
                }

                override fun onSelected(position: Int, view: View) {
                    super.onSelected(position, view)
                    if (entertainment.size - 1 == position) {
                        binding.inclEventEntertainment.inclOther.edtText.setText("")
                        binding.inclEventEntertainment.inclOther.root.visibility = View.VISIBLE
                    }
                }

                override fun unSelected(position: Int, view: View) {
                    super.unSelected(position, view)
                    if (entertainment.size - 1 == position) {
                        binding.inclEventEntertainment.inclOther.root.visibility = View.GONE
                        binding.inclEventEntertainment.inclOther.edtText.setText("")
                    }
                }
            }.also { entertainmentAdapter = it }


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

    private fun openMusicDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogEntertainmentTypeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_entertainment_type, null, false
        )

        val displaymetrics = DisplayMetrics()

        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding.clPass.height = (height * 0.35).toInt()
        binding.clPass.requestLayout()

        binding.title = getString(R.string.txt_plain_music)

        binding.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.clPass.height = height
                binding.clPass.requestLayout()
                if (whatsOnData != null &&  whatsOnData!!.music!=null) {
                    setMusicData(binding, height, whatsOnData!!.music!!)
                } else {
                    setMusicData(binding, height, common.createWhatsOnModel.music)
                }

                binding.tegview.visibility = View.VISIBLE
                binding.tvSave.visibility = View.VISIBLE
                binding.tvVenueMusicTitle.visibility = View.VISIBLE
                binding.edtVenueMusicName.visibility = View.VISIBLE
            } else {
                binding.clPass.height = (height * 0.35).toInt()
                binding.clPass.requestLayout()
                binding.tegview.visibility = View.GONE
                binding.tvSave.visibility = View.GONE
                binding.tvVenueMusicTitle.visibility = View.GONE
                binding.edtVenueMusicName.visibility = View.GONE
                binding.inclOther.root.visibility = View.GONE
                binding.inclOther.edtText.setText("")
            }
        }

        binding.btnDone.alpha = 0.5f
        binding.btnDone.isEnabled = false

        binding.tvSave.setOnClickListener {
            if (!binding.tegview.selectedList.iterator().hasNext()) {
                binding.tvErrorTags.visibility = View.VISIBLE
                binding.tvErrorTags.text = getString(R.string.txt_select_one_music_type)
            } else if (binding.inclOther.root.visibility == View.VISIBLE && binding.inclOther.edtText.text.isEmpty()) {
                binding.inclOther.tvError.visibility = View.VISIBLE
                binding.inclOther.tvError.text = getString(R.string.txt_err_enter_details)
            } else {
                showToast(getString(R.string.txt_music_saved))
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true

                common.createWhatsOnModel.djLineUpName = binding.edtVenueMusicName.text.toString()
            }
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (common.checkVenueTypeValidate(
                    binding.tegview.selectedList.iterator(),
                    venueMusic,
                    binding.inclOther.edtText,
                    binding.tvError
                )
            ) {
                setVenueTypeData(
                    binding.tegview.selectedList.iterator(),
                    venueMusic,
                    binding.inclOther.edtText,
                    Constant().music
                )
            }

            common.createWhatsOnModel.djLineUpName = binding.edtVenueMusicName.text.toString()

        }



        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(venueMusic) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv, binding.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                if (venueMusic.size - 1 == position) {
                    binding.inclOther.edtText.setText("")
                    binding.inclOther.root.visibility = View.VISIBLE
                }

            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (venueMusic.size - 1 == position) {
                    binding.inclOther.root.visibility = View.GONE
                    binding.inclOther.edtText.setText("")
                }

            }
        }.also { venueMusicAdapter = it }


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


    private fun setMusicData(
        binding: DialogEntertainmentTypeBinding,
        height: Int,
        musicData: String,
    ) {
        val music: MutableSet<Int> = HashSet()

        binding.clPass.height = height
        binding.clPass.requestLayout()

        if (musicData.isNotEmpty()) {
            binding.switchVenueConfirm.isChecked = true
            binding.tegview.visibility = View.VISIBLE
            binding.tvSave.visibility = View.VISIBLE
            binding.tvVenueMusicTitle.visibility = View.VISIBLE
            binding.edtVenueMusicName.visibility = View.VISIBLE



            for (i in venueMusic.indices) {
                if (musicData.contains(venueMusic[i])) {
                    music.add(i)
                }
            }

            println("setMusicData : music $music")

            @Suppress("DEPRECATION")
            venueMusicAdapter.setSelectedList(music)
            venueMusicAdapter.notifyDataChanged()

//            if (musicData.contains(Constant().OTHER)) {
//                binding.inclOther.root.visibility = View.VISIBLE
//                binding.inclOther.edtText.setText(common.createWhatsOnModel.musicOther)
//            }

            if (musicData.contains(Constant().otherConst)) {
                val other: List<String> = musicData.split(Constant().otherConst)
                binding.inclOther.root.visibility = View.VISIBLE
                binding.inclOther.edtText.setText(
                    other[1].replace(CONSTANT.SEPRATEOR_OTHER, "")
                )
            }

            if (common.createWhatsOnModel.djLineUpName.isNotEmpty()) {
                binding.edtVenueMusicName.text = common.createWhatsOnModel.djLineUpName
            } else {
                if (whatsOnData != null) {
                    if (whatsOnData!!.whatson_djline_up != null) {
                        binding.edtVenueMusicName.text = whatsOnData!!.whatson_djline_up!!
                    }
                }

            }

        }
    }


    private fun setEntertainmentData(
        binding: DialogEntertainmentBinding,
        entertainmentData: String,
    ) {
        val entertainments: MutableSet<Int> = HashSet()

        if (entertainmentData.isNotEmpty()) {
            binding.inclEventEntertainment.tegview.visibility = View.VISIBLE
            binding.inclEventEntertainment.tvSave.visibility = View.VISIBLE

            for (i in entertainment.indices) {
                if (entertainmentData.contains(entertainment[i])) {
                    entertainments.add(i)
                }
            }

            @Suppress("DEPRECATION")
            entertainmentAdapter.setSelectedList(entertainments)
            entertainmentAdapter.notifyDataChanged()

            if (entertainmentData.contains(Constant().otherConst)) {
                val other: List<String> = entertainmentData.split(Constant().otherConst)
                binding.inclEventEntertainment.inclOther.root.visibility = View.VISIBLE
                binding.inclEventEntertainment.inclOther.edtText.setText(
                    other[1].replace(CONSTANT.SEPRATEOR_OTHER, "")
                )
            }
        }
    }

    private fun setVenueTypeData(
        iterator: Iterator<Int>?, types: Array<String>, edtName: EditText, position: String,
    ) {
        var strRequest = ""
        var strDisplay = ""
        val list: ArrayList<Int>
        if (iterator != null) {
            var index: Int
            list = ArrayList()
            while (iterator.hasNext()) {
                index = iterator.next()
                list.add(index)
            }
            list.sort()
            for (i in list.indices) {
                strDisplay += types[list[i]]
                strRequest += types[list[i]]
                if (i == list.size - 1) {
//                    strRequest += CONSTANT.SEPARATOR_OTHER
                } else {
                    strDisplay = "$strDisplay, "
                    strRequest += CONSTANT.SEPRATEOR
                }
            }
            if (edtName.text.toString().isNotEmpty()) {
                strRequest += CONSTANT.SEPRATEOR_OTHER
                strDisplay = strDisplay.replace(Constant().otherConst, "")
                strDisplay += edtName.text.toString()
                strRequest += edtName.text.toString()
            }
        }
        if (position == Constant().music) {
            strMusic = strDisplay
            reqMusic = strRequest
            binding.inclVenueOtherDetails.inclMusic.edtText.setText(strMusic)
            common.createWhatsOnModel.music = reqMusic
            common.createWhatsOnModel.musicOther = edtName.text.toString()
        } else if (position == Constant().entertainment) {
            strEntertainment = strDisplay
            reqEntertainment = strRequest
            binding.inclVenueOtherDetails.inclEventEntertainment.edtText.setText(strEntertainment)
            common.createWhatsOnModel.entertainment = reqEntertainment
            common.createWhatsOnModel.entertainmentOther = edtName.text.toString()
        }
    }

    private fun openTimePickerDialog(
        hour: String?, minute: String?, amPm: String?, listener: DialogListener,
    ) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogTimePickerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_time_picker, null, false
        )


        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        hours = resources.getStringArray(R.array.hour).toList()
        minutes = resources.getStringArray(R.array.minutes).toList()
        amPms = resources.getStringArray(R.array.am_pm).toList()

        binding.inclTimePicker.wpHours.data = hours.toMutableList()
        binding.inclTimePicker.wpMinutes.data = minutes.toMutableList()
        binding.inclTimePicker.wpAmPm.data = amPms.toMutableList()


        if (hour != null && minute != null && amPm != null) {
            for (i in hours.indices) {
                if (hour == hours[i]) {
                    binding.inclTimePicker.wpHours.setSelectedItemPosition(i, false)
                    break
                }
            }

            for (i in minutes.indices) {
                if (minute == minutes[i]) {
                    binding.inclTimePicker.wpMinutes.setSelectedItemPosition(i, false)
                    break
                }
            }

            for (i in amPms.indices) {
                if (amPm == amPms[i]) {
                    binding.inclTimePicker.wpAmPm.setSelectedItemPosition(i, false)
                    break
                }
            }
        }


        binding.btnSelectTime.setOnClickListener {
            dialog.dismiss()

            println("openTimePickerDialog : " + binding.inclTimePicker.wpHours.data[binding.inclTimePicker.wpHours.currentItemPosition])
            println("openTimePickerDialog : " + binding.inclTimePicker.wpMinutes.data[binding.inclTimePicker.wpMinutes.currentItemPosition])
            println("openTimePickerDialog : " + binding.inclTimePicker.wpAmPm.data[binding.inclTimePicker.wpAmPm.currentItemPosition])

            listener.onSelectedTime(
                "" + binding.inclTimePicker.wpHours.data[binding.inclTimePicker.wpHours.currentItemPosition],
                "" + binding.inclTimePicker.wpMinutes.data[binding.inclTimePicker.wpMinutes.currentItemPosition],
                "" + binding.inclTimePicker.wpAmPm.data[binding.inclTimePicker.wpAmPm.currentItemPosition]
            )
        }

        binding.inclTimePicker.wpHours.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

        })

        binding.inclTimePicker.wpMinutes.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

        })


        binding.inclTimePicker.wpAmPm.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

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

    private fun addView(Image: String?, ImageType: String) {
        counter++
        val imageVideoView = ImageVideoView(activity, onViewClickListener, ImageType + counter)
        addImages.add(imageVideoView)
        binding.inclWhatsBasicInfo.llEventImage.addView(
            addImages[addImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        if (addImages.size > 1) {
            for (i in 0 until addImages.size) {
                addImages[i].imgClose!!.visibility = View.VISIBLE
            }
        } else {
            addImages[0].imgClose!!.visibility = View.INVISIBLE
        }
        if (addImages.size == 5) {
            binding.inclWhatsBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclWhatsBasicInfo.llEventImage.requestLayout()
        imageVideoView.uploadVideo(Image!!)
    }


    private fun displayImage(url: String?) {
        counter++
        addImages.add(ImageVideoView(activity, onViewClickListener, "IMAGE$counter", url!!))
        binding.inclWhatsBasicInfo.llEventImage.addView(
            addImages[addImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        for (i in 0 until addImages.size) {
            addImages[i].imgClose!!.visibility = View.VISIBLE
        }
        if (addImages.size == 5) {
            binding.inclWhatsBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclWhatsBasicInfo.llEventImage.requestLayout()
    }

    private var onViewClickListener: OnViewClickListener = object : OnViewClickListener() {
        override fun onClose(parent: View?, child: View?, Ids: String?) {
            if (parent!!.parent != null) {
                binding.inclWhatsBasicInfo.llEventImage.removeView(parent)
                addImages.remove(parent)
                binding.inclWhatsBasicInfo.llEventImage.requestLayout()
            }
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun imageGetterPopup() {
        val dialog = PopupWindow(requireActivity())
        val inflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.image_getter_dialog, null)
        val llClose: LinearLayout = v.findViewById<View>(R.id.ll_close) as LinearLayout
        val tvGallery: TextView = v.findViewById<View>(R.id.tv_gallery) as TextView
        val tvCamera: TextView = v.findViewById<View>(R.id.tv_camera) as TextView
        val tvCancel: TextView = v.findViewById<View>(R.id.tv_cancel) as TextView
        val tvPdf: TextView = v.findViewById<View>(R.id.tv_pdf) as TextView
        tvPdf.visibility = View.VISIBLE
        tvPdf.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_GET_CONTENT)
            pickPhoto.type = Constant().applicationPdf
            pickPhoto.addCategory(Intent.CATEGORY_OPENABLE)
            pickPhoto.putExtra(Intent.EXTRA_TITLE, Constant().invoicePdf)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pickPhoto.putExtra(
                    DocumentsContract.EXTRA_INITIAL_URI,
                    Environment.getExternalStorageDirectory().absolutePath
                )
            }
             startActivityForResult(Intent.createChooser(pickPhoto, Constant().selectPicture),
                requestPdf)
            dialog.dismiss()
        }
        llClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvCamera.setOnClickListener {
            ImagePicker.Companion.with(this@CreateWhatsOnFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        tvGallery.setOnClickListener {
            ImagePicker.Companion.with(this@CreateWhatsOnFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .galleryOnly()
                .provider(ImageProvider.GALLERY)
                .start(imagePickerGalleryRequest)
            dialog.dismiss()
        }
        dialog.contentView = v
        dialog.width = RecyclerView.LayoutParams.MATCH_PARENT
        dialog.height = RecyclerView.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
    }

    private var selectedImagePath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                imagePickerCameraRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    if (imageTypes.equals(CONSTANT.IMAGE, ignoreCase = true)) {
                        addView(selectedImagePath, CONSTANT.IMAGE)
                    }
                }
                imagePickerGalleryRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    if (imageTypes.equals(CONSTANT.IMAGE, ignoreCase = true)) {
                        addView(selectedImagePath, CONSTANT.IMAGE)
                    }
                }
                requestPdf -> {
                    try {
                        val file: File = AttachmentHelper.getFileFromUri(requireActivity(), data!!.data!!)
                        selectedPdfFile = file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    addView(selectedPdfFile, CONSTANT.MENUIMAGE)
                }
            }

        }

    }


    //private var selectedVideoPath = ""
    private var selectedPdfFile = ""


    private fun setUiWithExpandCollapse() {
        binding.inclBasicInfo.root.setOnClickListener {
            handleUiWithDropDown(
                binding.inclWhatsBasicInfo.root,
                binding.inclVenueOtherDetails.root,
                binding.inclWhatsBookingInfo.root,
                binding.inclBasicInfo.chkDropDown,
                binding.inclOtherDetails.chkDropDown,
                binding.inclBookingInfo.chkDropDown
            )
        }

        binding.inclBookingInfo.root.setOnClickListener {
                handleUiWithDropDown(
                    binding.inclWhatsBookingInfo.root,
                    binding.inclVenueOtherDetails.root,
                    binding.inclWhatsBasicInfo.root,
                    binding.inclBookingInfo.chkDropDown,
                    binding.inclOtherDetails.chkDropDown,
                    binding.inclBasicInfo.chkDropDown
                )
        }


        binding.inclOtherDetails.root.setOnClickListener {
            handleUiWithDropDown(
                binding.inclVenueOtherDetails.root,
                binding.inclWhatsBasicInfo.root,
                binding.inclWhatsBookingInfo.root,
                binding.inclOtherDetails.chkDropDown,
                binding.inclBasicInfo.chkDropDown,
                binding.inclBookingInfo.chkDropDown
            )
        }
    }

    private fun handleUiWithDropDown(
        rootView: View,
        rootView1: View,
        rootView2: View,
        checkBox: CheckBox,
        checkBox1: CheckBox,
        checkBox2: CheckBox,
    ) {
        if (rootView.isShown) {
            rootView.visibility = View.GONE
            checkBox.isChecked = false
        } else {
            rootView.visibility = View.VISIBLE
            checkBox.isChecked = true
        }
        rootView1.visibility = View.GONE
        checkBox1.isChecked = false
        rootView2.visibility = View.GONE
        checkBox2.isChecked = false
    }
}
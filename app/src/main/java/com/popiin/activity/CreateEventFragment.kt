package com.popiin.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.*
import com.popiin.R
import com.popiin.adapter.AddBookingTagAdapter
import com.popiin.adapter.BookingTagAdapter
import com.popiin.adapter.EventImageAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.*
import com.popiin.dialog.AddBookingTypeDialog
import com.popiin.dialog.CommonDialog
import com.popiin.flowlayout.FlowLayout
import com.popiin.flowlayout.TagAdapter
import com.popiin.fragment.SearchAddressFragment
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.AdapterTicketTagListener
import com.popiin.listener.AddressSelectionListener
import com.popiin.listener.InfoDialogListener
import com.popiin.model.CreateEventModel
import com.popiin.model.TicketBook
import com.popiin.res.EventListRes
import com.popiin.util.Constant
import com.popiin.util.MinMaxFilter
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.popiin.views.ImageVideoView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.popiin.res.CreateStripeLinkRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList


class CreateEventFragment : BaseFragment<FragmentCreateEventBinding>(), AddressSelectionListener {
    private var selectedDate: LocalDate? = null
    @RequiresApi(Build.VERSION_CODES.O)
    private var today = LocalDate.now()
    private var currentTime = ""
    private var eventTime = ""
    private lateinit var hours: List<String>
    private lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    var latitude: String = ""
    var longitude: String = ""
    var hour: String = ""
    var minute: String = ""
    var amPm: String = ""
    var startDate: String = ""
    lateinit var tv: TextView
    lateinit var ages: Array<String>
    private val perMissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private val maxCapacity = 100000
    private val permissionAll = 1
    lateinit var bookingTypeDialog: AddBookingTypeDialog
    private var ageSelected = 0
    private var strAge = ""
    private var strCategory = ""
    private var reqCategory = ""

    private lateinit var venueMusicAdapter: com.popiin.flowlayout.TagAdapter<*>
    private lateinit var entertainmentAdapter: com.popiin.flowlayout.TagAdapter<*>
    private lateinit var eventTagsAdapter: com.popiin.flowlayout.TagAdapter<*>
    private var mInflater: LayoutInflater? = null
    private lateinit var venueMusic: Array<String>

    lateinit var entertainment: Array<String>
    lateinit var eventTags: Array<String>
    private var strMusic = ""
    private var reqMusic = ""
    private var strEntertainment = ""
    private var reqEntertainment = ""
    private var isFirstTime = false
    private var addBookingTagAdapter: AddBookingTagAdapter? = null
    private var bookingTagAdapter: BookingTagAdapter? = null
    override fun getLayout(): Int {
        return R.layout.fragment_create_event
    }

    companion object {
        var isEditEvent = false
        fun newInstance(
            isEdit: Boolean,
        ): CreateEventFragment {
            isEditEvent = isEdit
            return CreateEventFragment()
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            if (isEditEvent || common.createEventModel.isBasicInfoVerified || common.createEventModel.isAddressVerfied ||
                common.createEventModel.isEventTimeVerified || common.createEventModel.isOtherInfoVerified
            ) {
                showExitPopup()
            } else {
                mActivity!!.supportFragmentManager.popBackStack()
            }
        }

        ages = resources.getStringArray(R.array.age)

        setUiWithExpandCollapse()

        common.handleErrorView(
            binding.inclEventBasicInfo.inclEventName.edtName,
            binding.inclEventBasicInfo.inclEventName.tvError
        )
        common.handleErrorView(
            binding.inclEventBasicInfo.inclDescription.edtText,
            binding.inclEventBasicInfo.inclDescription.tvError
        )
        common.handleErrorView(binding.inclVenueAddress.inclVenueAddress.edtName,
            binding.inclVenueAddress.inclVenueAddress.tvError)
        common.handleErrorView(binding.inclVenueAddress.inclVenueCity.edtName,
            binding.inclVenueAddress.inclVenueCity.tvError)
        common.handleErrorView(binding.inclVenueAddress.inclVenuePincode.edtName,
            binding.inclVenueAddress.inclVenuePincode.tvError)

        binding.inclEventTime.inclStartDateTime.edtText.setOnClickListener {
            openCalenderDialog(binding.inclEventTime.inclStartDateTime.edtText)
        }

        binding.inclEventBasicInfo.ivInfo.setOnClickListener {
            common.openDialog(mActivity!!, getString(R.string.txt_event_private_info))
        }


        binding.inclEventBasicInfo.switchEventConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                common.createEventModel.is_public = 0
            } else {
                common.createEventModel.is_public = 1
            }
        }


        binding.inclEventTime.inclEndDateTime.edtText.setOnClickListener {
            openCalenderDialog(binding.inclEventTime.inclEndDateTime.edtText)
        }


        setEventButtonEnabled()


        binding.btnCreateEvent.setOnClickListener {
            setFragmentWithStack(
                EventSummaryFragment.newInstance(
                    isEditEvent
                ), EventSummaryFragment::class.java.simpleName)

        }


        mInflater = LayoutInflater.from(mActivity)
        venueMusic = resources.getStringArray(R.array.venue_music)
        entertainment = resources.getStringArray(R.array.entertainment)
        eventTags = resources.getStringArray(R.array.tags)

        if (!hasPermissions(mActivity, *perMissions)) {
            ActivityCompat.requestPermissions(mActivity!!, perMissions, permissionAll)
        }

        binding.inclEventBasicInfo.imgAddImage.setOnClickListener {
            false.imageGetterPopup()
        }

        binding.inclEventOtherDetails.inclDressCode.ivInfo.setOnClickListener {
            common.openDialog(mActivity!!, getString(R.string.txt_dress_code_info))
        }

        binding.inclEventOtherDetails.inclEntryPolicy.ivInfo.setOnClickListener {
            common.openDialog(mActivity!!, getString(R.string.txt_entry_policy_info))
        }

        binding.inclEventOtherDetails.eventEntryType.ivInfo.setOnClickListener {
            common.openDialog(mActivity!!, getString(R.string.txt_entry_type_info))
        }

        binding.inclEventOtherDetails.inclTags.ivInfo.setOnClickListener {
            common.openDialog(mActivity!!, getString(R.string.txt_tags_info))
        }


        binding.inclEventOtherDetails.inclMusic.edtText.setOnClickListener {
            openMusicDialog()
        }

        binding.inclEventOtherDetails.inclEventEntertainment.edtText.setOnClickListener {
            openEntertainmentDialog()
        }

        binding.inclEventOtherDetails.inclEventTags.edtText.setOnClickListener {
            openTagsDialog()
        }

        binding.inclEventBasicInfo.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateBasicInfo()) {
                common.createEventModel.eventName =
                    binding.inclEventBasicInfo.inclEventName.edtName.text.toString()
                common.createEventModel.venueName =
                    binding.inclEventBasicInfo.inclVenueName.edtName.text.toString()
                common.createEventModel.eventType =
                    binding.inclEventBasicInfo.inclEventType.edtName.text.toString()
                common.createEventModel.description =
                    binding.inclEventBasicInfo.inclDescription.edtText.text.toString()
                val images = arrayOfNulls<String>(addImages.size)
                for (i in 0 until addImages.size) {
                    images[i] = addImages[i].imageUrl
                }

                if (strAge == Constant().otherConst) {
                    strAge = binding.inclEventBasicInfo.eventAgeOther.edtName.getText().toString()
                }

                common.createEventModel.age = strAge
                common.createEventModel.imagePath = images

                common.createEventModel.isBasicInfoVerified = true
                binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
                binding.inclBasicInfo.chkDropDown.isChecked = false
                binding.inclEventBasicInfo.root.visibility = View.GONE
                setEventButtonEnabled()
            } else {
                common.createEventModel.isBasicInfoVerified = false
                binding.inclBasicInfo.uiVerified.visibility = View.GONE
                binding.inclBasicInfo.chkDropDown.isChecked = true
                binding.inclEventBasicInfo.root.visibility = View.VISIBLE
                setEventButtonEnabled()
            }
        }

        binding.inclEventTime.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidEventTime()) {
                common.createEventModel.startDateTime = binding.inclEventTime.inclStartDateTime.edtText.text.toString()
                common.createEventModel.endDateTime = binding.inclEventTime.inclEndDateTime.edtText.text.toString()

                common.createEventModel.isEventTimeVerified = true
                binding.inclTime.uiVerified.visibility = View.VISIBLE
                binding.inclTime.chkDropDown.isChecked = false
                binding.inclEventTime.root.visibility = View.GONE
                setEventButtonEnabled()
            } else {
                common.createEventModel.isEventTimeVerified = false
                binding.inclTime.uiVerified.visibility = View.GONE
                binding.inclTime.chkDropDown.isChecked = true
                binding.inclEventTime.root.visibility = View.VISIBLE
                setEventButtonEnabled()
            }
        }

        binding.inclVenueAddress.inclVenueAddress.edtName.isFocusable = false
        binding.inclVenueAddress.inclVenueAddress.edtName.isFocusableInTouchMode = false
        binding.inclVenueAddress.inclVenueAddress.edtName.setSingleLine(true)
        binding.inclVenueAddress.inclVenueAddress.edtName.ellipsize = TextUtils.TruncateAt.END

        binding.inclVenueAddress.inclVenueAddress.edtName.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val searchFragment = SearchAddressFragment.newInstance(2)
            searchFragment.addressListener = this
            transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            transaction.add(R.id.frame_layout, searchFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        binding.inclVenueAddress.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidAddress()) {
                showToast(getString(R.string.txt_address_saved))
                common.createEventModel.isAddressVerfied = true
                binding.inclAddress.uiVerified.visibility = View.VISIBLE
                binding.inclAddress.chkDropDown.isChecked = false
                binding.inclVenueAddress.root.visibility = View.GONE
                setEventButtonEnabled()
            } else {
                common.createEventModel.isAddressVerfied = false
                binding.inclAddress.uiVerified.visibility = View.GONE
                binding.inclAddress.chkDropDown.isChecked = true
                binding.inclVenueAddress.root.visibility = View.VISIBLE
                setEventButtonEnabled()
            }
        }

        binding.inclEventOtherDetails.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidEventOtherDetails()) {
                common.createEventModel.dressCode = binding.inclEventOtherDetails.inclDressCode.edtText.text.toString()
                common.createEventModel.entryPolicy = binding.inclEventOtherDetails.inclEntryPolicy.edtText.text.toString()
                common.createEventModel.lastEntryPolicy = binding.inclEventOtherDetails.edtLastEntry.text.toString()
                common.createEventModel.totalCapacity = totalCapacity
                common.createEventModel.otherInfo = binding.inclEventOtherDetails.inclOtherInformation.edtText.text.toString()

                common.createEventModel.isOtherInfoVerified = true
                binding.inclOtherDetails.uiVerified.visibility = View.VISIBLE
                binding.inclOtherDetails.chkDropDown.isChecked = false
                binding.inclEventOtherDetails.root.visibility = View.GONE
                setEventButtonEnabled()
            } else {
                common.createEventModel.isOtherInfoVerified = false
                binding.inclOtherDetails.uiVerified.visibility = View.GONE
                binding.inclOtherDetails.chkDropDown.isChecked = true
                binding.inclEventOtherDetails.root.visibility = View.VISIBLE
                setEventButtonEnabled()
            }
        }

        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(mActivity!!, R.layout.spinner_item, ages)
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.inclEventBasicInfo.inclAge.img.visibility = View.GONE
        binding.inclEventBasicInfo.inclAge.spinner.adapter = aa

        binding.inclEventBasicInfo.inclAge.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    binding.inclEventBasicInfo.inclAge.tvError.visibility = View.GONE
                    ageSelected = position
                    strAge = ages[position]

                    (view as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorHeaderText
                        )
                    )

                    isFirstTime = true
                    if (position == ages.size - 1) {
                        if (position > 0) {
                            binding.inclEventBasicInfo.eventAgeOther.edtName.requestFocus()
                        }
                        binding.inclEventBasicInfo.eventAgeOther.root.visibility = View.VISIBLE
                        binding.inclEventBasicInfo.eventAgeOther.edtName.hint =
                            getString(R.string.txt_enter_age)
                        binding.inclEventBasicInfo.eventAgeOther.tvError.visibility = View.GONE
                    } else {
                        binding.inclEventBasicInfo.eventAgeOther.root.visibility = View.GONE
                        if (position > 0 && isFirstTime) {
                            binding.inclEventBasicInfo.inclVenueName.edtName.requestFocus()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.inclEventOtherDetails.rvTicketTags.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        bookingTagAdapter = BookingTagAdapter(common.createEventModel.ticketBooks, ticketTagListener)
        binding.inclEventOtherDetails.rvTicketTags.adapter = bookingTagAdapter

        binding.inclEventOtherDetails.rvTicketDetails.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        addBookingTagAdapter = AddBookingTagAdapter(common.createEventModel.ticketBooks, ticketTagListener, true)
        binding.inclEventOtherDetails.rvTicketDetails.adapter = addBookingTagAdapter

        binding.inclEventOtherDetails.imgAddBookingTicket.setOnClickListener {
            binding.inclEventOtherDetails.imgAddBookingTicket.requestFocus()

            if (remainingCapacityAvailable()) {
                bookingTypeDialog = AddBookingTypeDialog(
                    mActivity!!, ticketTagListener, -1, null, false, remainingCapacity
                )
                bookingTypeDialog.show()
            } else {
                val commonDialog = CommonDialog(
                    mActivity!!,
                    getString(R.string.lbl_ok),
                    "",
                    "",
                    getString(R.string.txt_dialog_venue_capacity)
                )
                commonDialog.show()
                commonDialog.binding.btnPositive.setOnClickListener {
                    commonDialog.dismiss()
                }
            }
        }

        binding.inclEventOtherDetails.tvRemainingCapecity.text =
            resources.getString(R.string.lbl_remaining_capacity) + ": 0"

        binding.inclEventOtherDetails.inclTotalCapacity.edtName.filters = arrayOf<InputFilter>(
            MinMaxFilter(Constant().one, "" + maxCapacity)
        )

        binding.inclEventOtherDetails.inclTotalCapacity.edtName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    totalCapacity = s.toString().toInt()
                    if (totalCapacity <= maxCapacity) {
                        setRemainCapacity()
                    } else {
                        val commonDialog = CommonDialog(
                            mActivity!!,
                            getString(R.string.lbl_ok),
                            "",
                            "",
                            "Maximum limit $maxCapacity."
                        )
                        commonDialog.show()
                        commonDialog.binding.btnPositive.setOnClickListener {commonDialog.dismiss()}
                    }
                } else {
                    totalCapacity = 0
                    binding.inclEventOtherDetails.tvRemainingCapecity.text = resources.getString(R.string.lbl_remaining_capacity) + ": " + totalCapacity
                }
            }

            override fun afterTextChanged(s: Editable) {
                remainingCapacityAvailable()
                var capacity = 0
                for (i in common.createEventModel.ticketBooks.indices) {
                    capacity += common.createEventModel.ticketBooks[i].quantity.toInt()
                }
                if (remainingCapacity < 0) {
                    binding.inclEventOtherDetails.inclTotalCapacity.edtName.setText(capacity.toString())
                    binding.inclEventOtherDetails.inclTotalCapacity.edtName.setSelection(binding.inclEventOtherDetails.inclTotalCapacity.edtName.getText().toString().length)
                }
            }
        })

        if (PrefManager.getUser().user?.is_stripe_verified== 0) {
                showCommonInfoBottomFragment(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_payment_info),
                    getString(R.string.txt_add_payment_details),
                    getString(R.string.msg_stripe),
                    getString(R.string.lbl_not_now),
                    getString(R.string.txt_countinue),
                    stripeDialogListener
                )
            }

    }

    var stripeDialogListener: InfoDialogListener = object : InfoDialogListener() {

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            getCreateStripeLink(true)
            bottomSheetDialog.dismiss()
        }

    }
    private fun getCreateStripeLink(isLoaderDisplay: Boolean) {
        if(isInternetConnect()) {
            if (isLoaderDisplay) {
                showProgress()
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

                        } else {
                            hideProgress()
                        }
                    }
                    hideProgress()

                }

                override fun onFailure(call: Call<CreateStripeLinkRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun showExitPopup() {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.tvPassSuccess.text = getString(R.string.txt_sure_to_delete)
        binding.tvSuccess.text = getString(R.string.txt_you_want_to_exit)

        binding.ivPassSuccess.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
            R.drawable.ic_sure_delete))
        binding.btnNo.visibility = View.VISIBLE

        binding.btnNo.setText(getString(R.string.txt_no))
        binding.btnDone.setText(getString(R.string.txt_yes_exit))

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            requireActivity().supportFragmentManager.popBackStack()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


    private fun setEventButtonEnabled() {
        if (common.createEventModel.isEventTimeVerified && common.createEventModel.isBasicInfoVerified && common.createEventModel.isOtherInfoVerified && common.createEventModel.isAddressVerfied) {
            binding.btnCreateEvent.isEnabled = true
            binding.btnCreateEvent.alpha = 1f
        } else {
            binding.btnCreateEvent.isEnabled = false
            binding.btnCreateEvent.alpha = 0.5f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalenderDialog(edtText: EditText) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogCalenderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_calender, null, false
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

        val cal = Calendar.getInstance()
        val timeHour = cal.get(Calendar.HOUR_OF_DAY)
        val timeMinute = cal.get(Calendar.MINUTE)
        val timeSeconds = cal.get(Calendar.SECOND)
        val amOrPm  = cal.get(Calendar.AM_PM)
        var selectedPosition=0;

        hour=timeHour.toString();
        minute=timeMinute.toString();
        if(amOrPm==Calendar.AM){
            amPm=getString(R.string.txt_am)
            selectedPosition=0
        }else{
            amPm=getString(R.string.txt_pm)
            selectedPosition=1
        }
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
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else if (startDate.isEmpty()) {
                combinedDate = "$today $eventTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else if (eventTime.isEmpty()) {
                combinedDate = "$startDate $currentTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else {
                combinedDate = "$startDate $eventTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
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


        binding.tvCalTime.setOnClickListener {
            if (binding.clTimeDialog.visibility == View.GONE) {
                binding.clTimeDialog.visibility = View.VISIBLE
                binding.inclTime.wpAmPm.setSelectedItemPosition(selectedPosition)
            } else {
                binding.clTimeDialog.visibility = View.GONE
            }
        }

        hours = resources.getStringArray(R.array.hour).toList()
        minutes = resources.getStringArray(R.array.minutes).toList()
        amPms = resources.getStringArray(R.array.am_pm).toList()

        binding.inclTime.wpHours.data = hours.toMutableList()
        binding.inclTime.wpMinutes.data = minutes.toMutableList()
        binding.inclTime.wpAmPm.data = amPms.toMutableList()

        binding.inclTime.wpAmPm.setSelectedItemPosition(selectedPosition)

        binding.inclTime.wpHours.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                hour = data.toString()
                binding.tvCalTime.text = "$hour:$minute$amPm"
                eventTime = "$hour:$minute$amPm"
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

        binding.inclTime.wpMinutes.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                minute = data.toString()
                binding.tvCalTime.text = "$hour:$minute$amPm"
                eventTime = "$hour:$minute$amPm"
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


        binding.inclTime.wpAmPm.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                amPm = data.toString()
                binding.tvCalTime.text = "$hour:$minute$amPm"
                eventTime = "$hour:$minute$amPm"
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


    private fun configureBinders(binding: DialogCalenderBinding) {
        val calendarView = binding.inclCalender.calenderView

        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
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

                    }

                    // set date text here

                    startDate = selectedDate.toString()

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
                            mActivity!!, R.color.colorGrey
                        )
                    )
                }

                if (data.position == DayPosition.MonthDate) {
                    when (data.date) {
                        selectedDate -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mActivity!!, R.color.colorLightBlue
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(mActivity!!, R.drawable.bg_calender_day)
                        }
                        today -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mActivity!!, R.color.colorWhite
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(mActivity!!, R.drawable.bg_calender_today)
                        }
                        else -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    mActivity!!, R.color.colorBlack
                                )
                            )
                            textView.background = null
                        }
                    }
                }

                if (container.day.date.isBefore(LocalDate.now())) {
                    container.textView.setOnClickListener(null)
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            mActivity!!, R.color.colorGrey
                        )
                    )
                }

            }
        }

    }

    private var entryPosition = 0
    private var ticketTagListener: AdapterTicketTagListener<TicketBook?> =
        object : AdapterTicketTagListener<TicketBook?>() {

            override fun onItemClick(ticketBook: TicketBook?, position: Int) {
                super.onItemClick(ticketBook, position)

                if (!ticketBook!!.isNew) {
                    return
                }
                remainingCapacityAvailable()
                remainingCapacity += ticketBook.quantity.toInt()
                bookingTypeDialog = AddBookingTypeDialog(
                    mActivity!!, this, position, ticketBook, true, remainingCapacity
                )
                bookingTypeDialog.show()

            }

            override fun onItemDeleteClick(ticketBook: TicketBook?, position: Int) {
                super.onItemDeleteClick(ticketBook, position)
                entryPosition = position

                showCommonInfoBottomFragment(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_sure_delete),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_want_to_delete_booking_type),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_delete),
                    infoDialogListener
                )
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onAddNewTag(ticketBook: TicketBook?) {
                super.onAddNewTag(ticketBook)
                common.createEventModel.ticketBooks.add(ticketBook!!)
                addBookingTagAdapter!!.notifyDataSetChanged()
                bookingTagAdapter!!.notifyDataSetChanged()
                setRemainCapacity()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onUpdateTag(ticketBook: TicketBook?, position: Int) {
                super.onUpdateTag(ticketBook, position)
                common.createEventModel.ticketBooks[position].name = ticketBook!!.name
                common.createEventModel.ticketBooks[position].price = ticketBook.price
                common.createEventModel.ticketBooks[position].quantity = ticketBook.quantity
                addBookingTagAdapter!!.notifyDataSetChanged()
                bookingTagAdapter!!.notifyDataSetChanged()
                setRemainCapacity()

            }
        }

    fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                negativeText,
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

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
            common.createEventModel.ticketBooks.removeAt(entryPosition)
            addBookingTagAdapter!!.notifyDataSetChanged()
            bookingTagAdapter!!.notifyDataSetChanged()
            setRemainCapacity()
        }

    }


    private fun remainingCapacityAvailable(): Boolean {
        remainingCapacity = totalCapacity
        for (i in common.createEventModel.ticketBooks.indices) {
            remainingCapacity -= common.createEventModel.ticketBooks[i].quantity.toInt()
        }
        return remainingCapacity > 0
    }

    @SuppressLint("SetTextI18n")
    fun setRemainCapacity() {
        var temp = totalCapacity
        for (i in common.createEventModel.ticketBooks.indices) {
            temp -= common.createEventModel.ticketBooks[i].quantity.toInt()
        }
        binding.inclEventOtherDetails.tvRemainingCapecity.text =
            resources.getString(R.string.lbl_remaining_capacity) + ": " + temp
        binding.inclEventOtherDetails.tvTickerError.visibility = View.GONE
    }

    var totalCapacity = 0
    var remainingCapacity = 0

    private fun isValidEventOtherDetails(): Boolean {
        var isValid = true

        if (binding.inclEventOtherDetails.inclTotalCapacity.edtName.text.toString().isEmpty()) {
            binding.inclEventOtherDetails.tvCapacityError.visibility = View.VISIBLE
            binding.inclEventOtherDetails.tvCapacityError.text =
                getString(R.string.txt_err_enter_total_capacity)
            isValid = false
        }

        if (common.createEventModel.ticketBooks.size == 0) {
            binding.inclEventOtherDetails.tvTickerError.visibility = View.VISIBLE
            binding.inclEventOtherDetails.tvTickerError.text =
                getString(R.string.txt_err_entry_type)
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

    private fun isValidAddress(): Boolean {
        var isValid = true

        if (binding.inclVenueAddress.inclVenueAddress.edtName.text.toString().isEmpty()) {
            isValid = false
            binding.inclVenueAddress.inclVenueAddress.tvError.visibility = View.VISIBLE
            binding.inclVenueAddress.inclVenueAddress.tvError.text =
                getString(R.string.txt_err_enter_event_address)
        }

        if (binding.inclVenueAddress.inclVenueCity.edtName.text.toString().isEmpty()) {
            isValid = false
            binding.inclVenueAddress.inclVenueCity.tvError.visibility = View.VISIBLE
            binding.inclVenueAddress.inclVenueCity.tvError.text =
                getString(R.string.txt_err_enter_city_name)
        }

        if (binding.inclVenueAddress.inclVenuePincode.edtName.text.toString().isEmpty()) {
            isValid = false
            binding.inclVenueAddress.inclVenuePincode.tvError.visibility = View.VISIBLE
            binding.inclVenueAddress.inclVenuePincode.tvError.text =
                getString(R.string.txt_err_enter_correct_post_code)
        }

        return isValid
    }

    private fun openEntertainmentDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogEntertainmentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_entertainment, null, false
        )

        binding.inclEventEntertainment.viewBottom.visibility = View.GONE

        binding.inclEventEntertainment.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setEntertainmentData(binding, common.createEventModel.entertainment)
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

            setVenueTypeData(
                binding.inclEventEntertainment.tegview.selectedList.iterator(),
                entertainment,
                binding.inclEventEntertainment.inclOther.edtText,
                Constant().entertainment
            )
        }

        common.createEventModel.eventEntertainmentNamr = binding.inclEventEntertainment.inclOther.edtText.text.toString()

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
                        binding.inclEventEntertainment.inclOther.root.visibility = View.GONE
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


    private fun openTagsDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogTagsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_tags, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {

            if (!binding.tegview.selectedList.iterator().hasNext()) {
                binding.tvErrorTags.visibility = View.VISIBLE
                binding.tvErrorTags.text =
                    getString(R.string.txt_select_one_tag)
            } else if (common.checkVenueTypeValidate(binding.tegview.selectedList.iterator(),
                    eventTags,
                    binding.inclOther.edtText,
                    binding.inclOther.tvError)
            ) {
                dialog.dismiss()
                setVenueTypeData(
                    binding.tegview.selectedList.iterator(),
                    eventTags,
                    binding.inclOther.edtText,
                    Constant().tags
                )
            }

        }

        binding.tegview.adapter =
            object : com.popiin.flowlayout.TagAdapter<String?>(eventTags) {
                override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                    val tv = mInflater!!.inflate(
                        R.layout.tv, binding.tegview, false
                    ) as TextView
                    tv.text = s
                    return tv
                }

                override fun onSelected(position: Int, view: View) {
                    super.onSelected(position, view)
                    if (eventTags.size - 1 == position) {
                        binding.inclOther.edtText.setText("")
                        binding.inclOther.root.visibility = View.VISIBLE
                        if (binding.tvErrorTags.visibility == View.VISIBLE) {
                            binding.tvErrorTags.visibility = View.GONE
                        }
                    }
                }

                override fun unSelected(position: Int, view: View) {
                    super.unSelected(position, view)
                    if (eventTags.size - 1 == position) {
                        binding.inclOther.root.visibility = View.GONE
                        binding.inclOther.edtText.setText("")
                    }
                }
            }.also { eventTagsAdapter = it }

        setTagsData(binding, common.createEventModel.category)



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

        val displayMetrics = DisplayMetrics()

        mActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels

        binding.clPass.height = (height * 0.35).toInt()
        binding.clPass.requestLayout()

        binding.title = getString(R.string.txt_plain_music)
        binding.tvMenu.text = getString(R.string.txt_select_music)
        binding.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclOther.lblTitle.textSize = 15f
        val typeface: Typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_regular)!!
        binding.inclOther.lblTitle.typeface = typeface

        binding.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.clPass.height = height
                binding.clPass.requestLayout()
                setMusicData(binding, height, common.createEventModel.music)
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
                common.createEventModel.eventMusicName = binding.edtVenueMusicName.text.toString()
            }
        }

        binding.btnDone.setOnClickListener {

            setVenueTypeData(
                binding.tegview.selectedList.iterator(),
                venueMusic,
                binding.inclOther.edtText,
                Constant().music
            )
            dialog.dismiss()
        }

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(venueMusic) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                tv = mInflater!!.inflate(
                    R.layout.tv, binding.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                if (venueMusic.size - 1 == position) {
                    binding.inclOther.edtText.setText("")
                    binding.inclOther.root.visibility = View.GONE
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

            @Suppress("DEPRECATION")
            venueMusicAdapter.setSelectedList(music)
            venueMusicAdapter.notifyDataChanged()

            if (musicData.contains(Constant().otherConst)) {
                val other: List<String> = musicData.split(Constant().otherConst)
                binding.inclOther.root.visibility = View.VISIBLE
                binding.inclOther.edtText.setText(
                    other[1].replace(CONSTANT.SEPRATEOR_OTHER, "")
                )
            }

            if (common.createEventModel.eventMusicName.isNotEmpty()) {
                binding.edtVenueMusicName.text = common.createEventModel.eventMusicName
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
                binding.inclEventEntertainment.inclOther.edtText.setText(other[1].replace(CONSTANT.SEPRATEOR_OTHER, "")
                )
            }

        }
    }

    private fun setTagsData(
        binding: DialogTagsBinding,
        tagsData: String,
    ) {
        val entertainments: MutableSet<Int> = HashSet()

        if (tagsData.isNotEmpty()) {
            binding.tegview.visibility = View.VISIBLE

            for (i in eventTags.indices) {
                if (tagsData.contains(eventTags[i])) {
                    entertainments.add(i)
                }
            }

            @Suppress("DEPRECATION")
            eventTagsAdapter.setSelectedList(entertainments)
            eventTagsAdapter.notifyDataChanged()


            if (tagsData.contains(Constant().otherConst)) {
                val other: List<String> = tagsData.split(Constant().otherConst)
                binding.inclOther.root.visibility = View.VISIBLE
                binding.inclOther.edtText.setText(
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
        val list: java.util.ArrayList<Int>
        if (iterator != null) {
            var index: Int
            list = java.util.ArrayList()
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
            //music
            strMusic = strDisplay
            reqMusic = strRequest
            binding.inclEventOtherDetails.inclMusic.edtText.setText(strMusic)
            common.createEventModel.music = reqMusic
            common.createEventModel.musicOther = edtName.text.toString()
        } else if (position == Constant().entertainment) {
            strEntertainment = strDisplay
            reqEntertainment = strRequest
            binding.inclEventOtherDetails.inclEventEntertainment.edtText.setText(strEntertainment)
            common.createEventModel.entertainment = reqEntertainment
            common.createEventModel.eventEntertainmentNamr = edtName.text.toString()
        } else if (position == Constant().tags) {
            strCategory = strDisplay
            reqCategory = strRequest
            binding.inclEventOtherDetails.inclEventTags.edtText.setText(strCategory)
            common.createEventModel.category = reqCategory
        }
    }


    private fun isValidEventTime(): Boolean {
        var isValid = true

        if (binding.inclEventTime.inclStartDateTime.edtText.text.toString().isEmpty()) {
            binding.inclEventTime.inclStartDateTime.tvError.visibility = View.VISIBLE
            binding.inclEventTime.inclStartDateTime.tvError.text =
                getString(R.string.txt_err_event_start_date)
            isValid = false
        }

        if (binding.inclEventTime.inclEndDateTime.edtText.text.toString().isEmpty()) {
            binding.inclEventTime.inclEndDateTime.tvError.visibility = View.VISIBLE
            binding.inclEventTime.inclEndDateTime.tvError.text =
                getString(R.string.txt_err_event_end_date)
            isValid = false
        }

        val startDate = common.convertStringToDate(
            binding.inclEventTime.inclStartDateTime.edtText.text.toString(),
            Constant().ddMmYyyyHhMmA
        )
        val endDate = common.convertStringToDate(
            binding.inclEventTime.inclEndDateTime.edtText.text.toString(),
            Constant().ddMmYyyyHhMmA
        )


        if (endDate.before(startDate)) {
            binding.inclEventTime.inclStartDateTime.tvError.visibility = View.VISIBLE
            binding.inclEventTime.inclStartDateTime.tvError.text =
                getString(R.string.txt_err_start_date)
            isValid = false
        }


        return isValid
    }


    private fun isValidateBasicInfo(): Boolean {
        var isValid = true
        val eventName = binding.inclEventBasicInfo.inclEventName.edtName.getText().toString()
        val description = binding.inclEventBasicInfo.inclDescription.edtText.getText().toString()

        if (eventName.isEmpty()) {
            binding.inclEventBasicInfo.inclEventName.tvError.visibility = View.VISIBLE
            binding.inclEventBasicInfo.inclEventName.tvError.text =
                resources.getString(R.string.txt_err_event_name)
            isValid = false
        }

        if (description.isEmpty()) {
            binding.inclEventBasicInfo.inclDescription.tvError.visibility = View.VISIBLE
            binding.inclEventBasicInfo.inclDescription.tvError.text =
                resources.getString(R.string.txt_err_event_description)
            isValid = false
        }

        if (ageSelected == 0) {
            binding.inclEventBasicInfo.inclAge.tvError.visibility = View.VISIBLE
            binding.inclEventBasicInfo.inclAge.tvError.text =
                resources.getString(R.string.txt_err_age)
            isValid = false
        } else if (ageSelected == ages.size - 1 && binding.inclEventBasicInfo.eventAgeOther.edtName.getText()
                .toString().isEmpty()
        ) {
            binding.inclEventBasicInfo.eventAgeOther.tvError.visibility = View.VISIBLE
            binding.inclEventBasicInfo.eventAgeOther.tvError.text =
                resources.getString(R.string.txt_err_enter_age)
            isValid = false
        } else if (strAge == Constant().otherConst) {
            strAge = binding.inclEventBasicInfo.eventAgeOther.edtName.getText().toString()
        }

        if (addImages.size == 0) {
            binding.inclEventBasicInfo.tvImageError.visibility = View.VISIBLE
            binding.inclEventBasicInfo.tvImageError.text =
                resources.getString(R.string.txt_err_events_upload_image)
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


    private fun setUiWithExpandCollapse() {
        binding.inclBasicInfo.root.setOnClickListener {
            handleUiWithDropDown(binding.inclEventBasicInfo.root, binding.inclBasicInfo.chkDropDown)
        }

        binding.inclAddress.root.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
            } else {
                handleUiWithDropDown(binding.inclVenueAddress.root, binding.inclAddress.chkDropDown)
            }
        }


        binding.inclTime.root.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
            } else if (!common.createEventModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_info))
            } else {
                handleUiWithDropDown(binding.inclEventTime.root, binding.inclTime.chkDropDown)

            }
        }

        binding.inclOtherDetails.root.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
            } else if (!common.createEventModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_info))
            } else if (!common.createEventModel.isEventTimeVerified) {
                showToast(getString(R.string.txt_event_date_time_section))
            } else {
                handleUiWithDropDown(binding.inclEventOtherDetails.root,
                    binding.inclOtherDetails.chkDropDown)
            }
        }

        binding.inclBasicInfo.chkDropDown.setOnClickListener {
            handleUiWithDropDown(binding.inclEventBasicInfo.root, binding.inclBasicInfo.chkDropDown)
        }

        binding.inclAddress.chkDropDown.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
                binding.inclAddress.chkDropDown.isChecked = false
            } else {
                handleUiWithDropDown(binding.inclVenueAddress.root, binding.inclAddress.chkDropDown)
            }
        }



        binding.inclTime.chkDropDown.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
                binding.inclTime.chkDropDown.isChecked = false
            } else if (!common.createEventModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_info))
                binding.inclTime.chkDropDown.isChecked = false
            } else {
                handleUiWithDropDown(binding.inclEventTime.root, binding.inclTime.chkDropDown)

            }
        }

        binding.inclOtherDetails.chkDropDown.setOnClickListener {
            if (!common.createEventModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info))
                binding.inclOtherDetails.chkDropDown.isChecked = false
            } else if (!common.createEventModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_info))
                binding.inclOtherDetails.chkDropDown.isChecked = false
            } else if (!common.createEventModel.isEventTimeVerified) {
                showToast(getString(R.string.txt_event_date_time_section))
                binding.inclOtherDetails.chkDropDown.isChecked = false
            } else {
                handleUiWithDropDown(binding.inclEventOtherDetails.root,
                    binding.inclOtherDetails.chkDropDown)
            }
        }

    }

    private fun handleUiWithDropDown(rootView: View, checkBox: CheckBox) {
        if (rootView.isShown) {
            rootView.visibility = View.GONE
            checkBox.isChecked = false
        } else {
            rootView.visibility = View.VISIBLE
            checkBox.isChecked = true
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

    private fun Boolean.imageGetterPopup() {
        val dialog = PopupWindow(requireActivity())

        val binding: ImageGetterDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.image_getter_dialog, null, false
        )

        if (this) {
            binding.tvPdf.visibility = View.VISIBLE
        }

        binding.llClose.setOnClickListener { dialog.dismiss() }


        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.tvCamera.setOnClickListener {
            ImagePicker.Companion.with(this@CreateEventFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            ImagePicker.Companion.with(this@CreateEventFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .galleryOnly()
                .provider(ImageProvider.GALLERY)
                .start(imagePickerGalleryRequest)
            dialog.dismiss()
        }

        dialog.contentView = binding.root
        dialog.isClippingEnabled = false
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private var selectedImagePath: String? = null



    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == imagePickerCameraRequest) {
                selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                addView(selectedImagePath, CONSTANT.IMAGE)
            } else if (requestCode == imagePickerGalleryRequest) {
                selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                addView(selectedImagePath, CONSTANT.IMAGE)

            }

        }

    }
    private var counter = -1
    private val addImages = ArrayList<ImageVideoView>()
    private fun addView(image: String?, ImageType: String) {
        counter++
        val imageVideoView = ImageVideoView(mActivity, onViewClickListener, ImageType + counter)
        addImages.add(imageVideoView)
        binding.inclEventBasicInfo.llEventImage.addView(addImages[addImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        if (addImages.size > 1) {
            for (i in addImages.indices) {
                addImages[i].imgClose!!.visibility = View.VISIBLE
            }
        } else {
            addImages[0].imgClose!!.visibility = View.INVISIBLE
        }
        if (addImages.size == 5) {
            binding.inclEventBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclEventBasicInfo.llEventImage.requestLayout()
        imageVideoView.uploadVideo(image!!)
    }

    private var onViewClickListener = object : ImageVideoView.OnViewClickListener() {
        override fun onClose(parent: View?, child: View?, Ids: String?) {
            if (parent!!.parent != null) {
                    binding.inclEventBasicInfo.llEventImage.removeView(parent)
                    addImages.remove(parent)
                    binding.inclEventBasicInfo.llEventImage.requestLayout()
                 binding.inclEventBasicInfo.imgAddImage.visibility=View.VISIBLE
            }
        }

    }
    private fun displayImage(url: String?) {
        counter++
        addImages.add(
            ImageVideoView(
                mActivity, onViewClickListener, CONSTANT.IMAGE + counter,
                url!!
            )
        )
        binding.inclEventBasicInfo.llEventImage.addView(
            addImages[addImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        for (i in addImages.indices) {
            addImages[i].imgClose!!.visibility = View.VISIBLE
        }
        if (addImages.size == 5) {
            binding.inclEventBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclEventBasicInfo.llEventImage.requestLayout()
    }

    override fun onResume() {
        super.onResume()
        if(common.isSetEventData){
            setData()
            common.isSetEventData=false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setData() {
        if (common.createEventModel.eventName.isNotEmpty()) {

            if (isEditEvent) {
                binding.btnCreateEvent.alpha = 1f
                binding.btnCreateEvent.isEnabled = true
                binding.btnCreateEvent.text = getString(R.string.txt_update_event)

                binding.inclEventTime.inclStartDateTime.edtText.isEnabled=false
                binding.inclEventTime.inclStartDateTime.edtText.alpha=0.7f
                binding.inclEventTime.inclEndDateTime.edtText.isEnabled=false
                binding.inclEventTime.inclEndDateTime.edtText.alpha=0.7f

            }

            binding.inclEventBasicInfo.inclEventName.edtName.setText(common.createEventModel.eventName)

            binding.inclEventBasicInfo.inclVenueName.edtName.setText(common.createEventModel.venueName)

            if (common.createEventModel.isBasicInfoVerified) {
                binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
            }

            if (common.createEventModel.isAddressVerfied) {
                binding.inclAddress.uiVerified.visibility = View.VISIBLE
            }

            if (common.createEventModel.isEventTimeVerified) {
                binding.inclTime.uiVerified.visibility = View.VISIBLE
            }

            if (common.createEventModel.isOtherInfoVerified) {
                binding.inclOtherDetails.uiVerified.visibility = View.VISIBLE
            }


            binding.inclEventBasicInfo.inclEventType.edtName.setText(common.createEventModel.eventType)
            binding.inclEventBasicInfo.inclDescription.edtText.text =
                common.createEventModel.description

            for (i in ages.indices) {
                if (common.createEventModel.age == ages[i]) {
                    binding.inclEventBasicInfo.inclAge.spinner.setSelection(i, true)
                    break
                }
            }


            if (common.createEventModel.imagePath != null && common.createEventModel.imagePath!!.size>0) {
                binding.inclEventBasicInfo.llEventImage.removeAllViews()
                addImages.clear()
                binding.inclEventBasicInfo.llEventImage.requestLayout()
                for (i in 0 until common.createEventModel.imagePath!!.size) {
                    displayImage(common.createEventModel.imagePath!![i])
                }
            }

            if (common.createEventModel.ticketBooks.size > 0) {
                addBookingTagAdapter!!.notifyDataSetChanged()
                bookingTagAdapter!!.notifyDataSetChanged()
            }


            binding.inclVenueAddress.inclVenueAddress.edtName.setText(common.createEventModel.address)
            binding.inclVenueAddress.inclVenueCity.edtName.setText(common.createEventModel.city)
            binding.inclVenueAddress.inclVenuePincode.edtName.setText(common.createEventModel.postCode)


            binding.inclEventTime.inclStartDateTime.edtText.setText(common.createEventModel.startDateTime)
            binding.inclEventTime.inclEndDateTime.edtText.setText(common.createEventModel.endDateTime)



            if (common.createEventModel.music.isNotEmpty()) {
                binding.inclEventOtherDetails.inclMusic.edtText.text =
                    common.createEventModel.music.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ").replace(CONSTANT.SEPRATEOR, ", ")
            }

            if (common.createEventModel.entertainment.isNotEmpty()) {
                binding.inclEventOtherDetails.inclEventEntertainment.edtText.text =
                    common.createEventModel.entertainment.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ").replace(CONSTANT.SEPRATEOR, ", ")
            }

            if (common.createEventModel.category.isNotEmpty()) {
                binding.inclEventOtherDetails.inclEventTags.edtText.text =
                    common.createEventModel.category.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ").replace(CONSTANT.SEPRATEOR, ", ")
            }



            binding.inclEventOtherDetails.inclDressCode.edtText.text =
                common.createEventModel.dressCode
            binding.inclEventOtherDetails.inclEntryPolicy.edtText.text =
                common.createEventModel.entryPolicy
            binding.inclEventOtherDetails.edtLastEntry.text =
                common.createEventModel.lastEntryPolicy
            binding.inclEventOtherDetails.inclTotalCapacity.edtName.setText(common.createEventModel.totalCapacity.toString())
            binding.inclEventOtherDetails.inclOtherInformation.edtText.text =
                common.createEventModel.otherInfo

            binding.inclEventBasicInfo.switchEventConfirm.isChecked =
                common.createEventModel.is_public == 0

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAddressSelected(address: String) {
        binding.inclVenueAddress.inclVenueAddress.edtName.text = common.createEventModel.address
        binding.inclVenueAddress.inclVenueCity.edtName.text = common.createEventModel.city
        binding.inclVenueAddress.inclVenuePincode.edtName.text = common.createEventModel.postCode
        initViews()
    }

    override fun onStart() {
        super.onStart()
    }

}
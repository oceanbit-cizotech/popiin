package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.RedeemOfferAdapter
import com.popiin.adapter.WeekDayAdapter
import com.popiin.databinding.DialogAvailableOfferBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.DialogTimePickerBinding
import com.popiin.databinding.FragmentAddOffersBinding
import com.popiin.listener.AdapterOfferListener
import com.popiin.listener.DialogListener
import com.popiin.model.AvailOfferModel
import com.popiin.req.CreateVenueOfferReq
import com.popiin.req.UpdateVenueOfferReq
import com.popiin.res.CreateOfferRes
import com.popiin.res.VenueListRes.Offerslive
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddOffersFragment : BaseFragment<FragmentAddOffersBinding>() {
    private lateinit var weekDayAdapter: WeekDayAdapter
    private lateinit var redeemOfferAdapter: RedeemOfferAdapter
    private var weekDayList = ArrayList<AvailOfferModel>()
    private var redeemOfferList = ArrayList<AvailOfferModel>()
    lateinit var hours: List<String>
    lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    private var hour: String? = ""
    private var minute: String? = ""
    private var amPm: String? = ""
    private var selectedIndex = -1
    private var selectedRedeemIndex = -1

    override fun getLayout(): Int {
        return R.layout.fragment_add_offers
    }

    companion object {
        var availOfferModel: AvailOfferModel? = null
        var offers: Offerslive? = null
        lateinit var venuesId: String
        fun newInstance(item: AvailOfferModel, venueId: String): AddOffersFragment {
            availOfferModel = item
            venuesId = venueId
            return AddOffersFragment()
        }

        fun newInstanceOffer(item: Offerslive, venueId: String): AddOffersFragment {
            offers = item
            venuesId = venueId
            return AddOffersFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            offers = null
            availOfferModel = null
            mActivity!!.supportFragmentManager.popBackStack()
        }

        if (offers != null) {
            setData(offers!!)
        } else {
            binding.inclAddDetails.edtText.setText(availOfferModel!!.title)
            binding.topHeader.title = getString(R.string.txt_add_offer)
        }


        binding.inclDay.edtText.setOnClickListener {
            // open weekdays dialog
            openWeekDayDialog()
        }

        common.handleErrorView(binding.inclAddDetails.edtText, binding.inclAddDetails.tvError)
        common.handleErrorView(binding.inclDay.edtText, binding.inclDay.tvError)
        common.handleErrorView(binding.inclOfferStarts.edtText, binding.inclOfferStarts.tvError)
        common.handleErrorView(binding.inclOfferEnds.edtText, binding.inclOfferEnds.tvError)
        common.handleErrorView(binding.inclRedeemOffer.edtText, binding.inclRedeemOffer.tvError)

        binding.btnAddOffer.setOnClickListener {
            if (isValidate()) {
                hideKeyBoard(requireActivity())
                // call add offer api
                if (offers != null) {
                    callUpdateOfferApi(offers!!)
                } else {
                    callCreateOfferApi()
                }
            }

        }

        binding.inclOfferStarts.edtText.setOnClickListener {
            // open timepicker dialog
            if (binding.inclOfferStarts.edtText.text.toString().isEmpty()) {
                hour = null
                minute = null
                amPm = null
            } else {
                hour = common.convertDateInFormat(
                    binding.inclOfferStarts.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().hh
                )
                minute = common.convertDateInFormat(
                    binding.inclOfferStarts.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().mm
                )
                amPm = common.convertDateInFormat(
                    binding.inclOfferStarts.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().a
                )
            }
            openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                    super.onSelectedTime(hours, minutes, ampms)
                    val startTime = "$hours:$minutes$ampms"
                    binding.inclOfferStarts.edtText.setText(startTime)
                }
            })
        }

        binding.inclOfferEnds.edtText.setOnClickListener {
            // open timepicker dialog
            if (binding.inclOfferEnds.edtText.text.toString().isEmpty()) {
                hour = null
                minute = null
                amPm = null
            } else {
                hour = common.convertDateInFormat(
                    binding.inclOfferEnds.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().hh
                )
                minute = common.convertDateInFormat(
                    binding.inclOfferEnds.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().mm
                )
                amPm = common.convertDateInFormat(
                    binding.inclOfferEnds.edtText.text.toString(),
                    Constant().hhMmA,
                    Constant().a
                )
            }
            openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                    super.onSelectedTime(hours, minutes, ampms)
                    val endTime = "$hours:$minutes$ampms"
                    binding.inclOfferEnds.edtText.setText(endTime)
                }
            })
        }

        binding.inclRedeemOffer.edtText.setOnClickListener {
            // open redeem offer dialog
            openRedeemOfferDialog()
        }

        if (weekDayList.size == 0) {
            weekDayList.add(AvailOfferModel(Constant().monday, false))
            weekDayList.add(AvailOfferModel(Constant().tuesday, false))
            weekDayList.add(AvailOfferModel(Constant().wednesday, false))
            weekDayList.add(AvailOfferModel(Constant().thurdsay, false))
            weekDayList.add(AvailOfferModel(Constant().friday, false))
            weekDayList.add(AvailOfferModel(Constant().saturday, false))
            weekDayList.add(AvailOfferModel(Constant().sunday, false))
        }

        if (redeemOfferList.size == 0) {
            redeemOfferList.add(AvailOfferModel(Constant().noRestrictions, false))
            redeemOfferList.add(AvailOfferModel(Constant().one, false))
            redeemOfferList.add(AvailOfferModel(Constant().two, false))
            redeemOfferList.add(AvailOfferModel(Constant().three, false))
            redeemOfferList.add(AvailOfferModel(Constant().four, false))
            redeemOfferList.add(AvailOfferModel(Constant().five, false))
        }
    }

    private fun setData(offerLive: Offerslive) {
        binding.inclAddDetails.edtText.setText(offerLive.title)

        binding.topHeader.title = getString(R.string.txt_update_offers)
        binding.btnAddOffer.setText(getString(R.string.txt_update_camel))

        binding.inclOfferStarts.edtText.setText(
            common.convertDateInFormat(offerLive.open_time, Constant().HHmmss24hrs, Constant().hhMmA)
        )
        binding.inclOfferEnds.edtText.setText(
            common.convertDateInFormat(offerLive.close_time, Constant().HHmmss24hrs, Constant().hhMmA)
        )

        binding.inclDay.edtText.setText(offerLive.working_day)
        if(offerLive.redeem_allowed_time==0){
            binding.inclRedeemOffer.edtText.setText(Constant().noRestrictions)
        }else{
            binding.inclRedeemOffer.edtText.setText("${offerLive.redeem_allowed_time}")
        }

    }


    private fun isValidate(): Boolean {
        var isValid = true

        val offerDetails = binding.inclAddDetails.edtText.text.toString()
        val offerDay = binding.inclDay.edtText.text.toString()
        val offerStarts = binding.inclOfferStarts.edtText.text.toString()
        val offerEnds = binding.inclOfferEnds.edtText.text.toString()
        val offerRedeem = binding.inclRedeemOffer.edtText.text.toString()

        if (offerDetails.isEmpty()) {
            binding.inclAddDetails.tvError.visibility = View.VISIBLE
            binding.inclAddDetails.tvError.text = getString(R.string.txt_err_enter_offer_details)
            isValid = false
        }

        if (offerDay.isEmpty()) {
            binding.inclDay.tvError.visibility = View.VISIBLE
            binding.inclDay.tvError.text = getString(R.string.txt_err_offer_day)
            isValid = false
        }

        if (offerStarts.isEmpty()) {
            binding.inclOfferStarts.tvError.visibility = View.VISIBLE
            binding.inclOfferStarts.tvError.text = getString(R.string.txt_err_offer_start_time)
            isValid = false
        }

        if (offerEnds.isEmpty()) {
            binding.inclOfferEnds.tvError.visibility = View.VISIBLE
            binding.inclOfferEnds.tvError.text = getString(R.string.txt_err_offer_end_time)
            isValid = false
        }

        if (offers != null) {
            if (!checkStartEndTime(
                    common.convertDateInFormat(
                        offers!!.open_time,
                        Constant().HHmmss24hrs,
                        Constant().hhMmA
                    )!!,
                    common.convertDateInFormat(
                        offers!!.close_time,
                        Constant().HHmmss24hrs,
                        Constant().hhMmA
                    )!!
                )
            ) {
                binding.inclOfferEnds.tvError.visibility = View.VISIBLE
                binding.inclOfferEnds.tvError.text = getString(R.string.txt_err_min_time)
                isValid = false
            }
        } else {
            if (!checkStartEndTime(offerStarts, offerEnds)) {
                binding.inclOfferEnds.tvError.visibility = View.VISIBLE
                binding.inclOfferEnds.tvError.text = getString(R.string.txt_err_min_time)
                isValid = false
            }
        }


        if (offerRedeem.isEmpty()) {
            binding.inclRedeemOffer.tvError.visibility = View.VISIBLE
            binding.inclRedeemOffer.tvError.text = getString(R.string.txt_err_select_option)
            isValid = false
        }

        return isValid

    }

    @SuppressLint("SimpleDateFormat")
    private fun checkStartEndTime(offerStarts: String, offerEnds: String): Boolean {
        var isValid = true

        val sdf = SimpleDateFormat(Constant().hhMmA)

        try {
            val date1: Date = sdf.parse(offerStarts) as Date
            val date2: Date = sdf.parse(offerEnds) as Date

            isValid = if (date1.before(date2)) {
                val mills = date2.time - date1.time
                //                val hours = (mills / (1000 * 60 * 60)).toInt()
                val minis = TimeUnit.MILLISECONDS.toMinutes(mills)

                println("checkTimings : diff $minis")

                minis >= 5
            } else {
                false
            }


        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return isValid
    }

    private fun openRedeemOfferDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogAvailableOfferBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_available_offer,
            null,
            false
        )

        binding.title = "Redeem offer"

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if (offers != null) {
            for (i in 0 until redeemOfferList.size) {
                if (("" + offers!!.redeem_allowed_time) == redeemOfferList[i].title) {
                    selectedRedeemIndex = i
                    break
                }
            }
        }


        redeemOfferAdapter =
            RedeemOfferAdapter(redeemOfferList, redeemOfferListener, selectedRedeemIndex, dialog)
        binding.rvOffers.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvOffers.adapter = redeemOfferAdapter


        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


    private fun openWeekDayDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogAvailableOfferBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_available_offer,
            null,
            false
        )

        binding.title = getString(R.string.txt_select_day)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if (offers != null) {
            for (i in 0 until weekDayList.size) {
                if (offers!!.working_day == weekDayList[i].title) {
                    selectedIndex = i
                    break
                }
            }
        }

        weekDayAdapter = WeekDayAdapter(weekDayList, weekDayListener, selectedIndex, dialog)
        binding.rvOffers.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvOffers.adapter = weekDayAdapter


        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private var weekDayListener: AdapterOfferListener<AvailOfferModel> =
        object : AdapterOfferListener<AvailOfferModel>() {
            override fun onOfferItemClick(item: AvailOfferModel, position: Int) {
                super.onOfferItemClick(item, position)
                binding.inclDay.edtText.setText(item.title)
                selectedIndex = position
            }
        }


    private var redeemOfferListener: AdapterOfferListener<AvailOfferModel> =
        object : AdapterOfferListener<AvailOfferModel>() {
            override fun onOfferItemClick(item: AvailOfferModel, position: Int) {
                super.onOfferItemClick(item, position)
                binding.inclRedeemOffer.edtText.setText(item.title)
                selectedRedeemIndex = position
            }
        }

    private fun openTimePickerDialog(
        hour: String?, minute: String?, amPm: String?, listener: DialogListener
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

    private fun callCreateOfferApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateOfferRes?>? = apiInterface.doCreateVenueoffer(
                PrefManager.getAccessToken(),
                CreateVenueOfferReq(
                    binding.inclAddDetails.edtText.text.toString(),
                    binding.inclDay.edtText.text.toString(),
                    common.convertDateInFormat(
                        binding.inclOfferStarts.edtText.text.toString(),
                        Constant().hhMmA,
                        Constant().hhMM
                    )!!,
                    common.convertDateInFormat(
                        binding.inclOfferEnds.edtText.text.toString(),
                        Constant().hhMmA,
                        Constant().hhMM
                    )!!,
                    binding.inclOfferStarts.edtText.text.toString(),
                    binding.inclOfferEnds.edtText.text.toString(),
                    venuesId,
                    if(binding.inclRedeemOffer.edtText.text.toString().equals(Constant().noRestrictions)) "0" else binding.inclRedeemOffer.edtText.text.toString(),
                    1
                )
            )
            call!!.enqueue(object : Callback<CreateOfferRes?> {
                override fun onResponse(
                    call: Call<CreateOfferRes?>,
                    response: Response<CreateOfferRes?>
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.isSuccess) {
                            offers = response.body()!!.offers
                            binding.btnAddOffer.setText(getString(R.string.txt_update_offer))
                            binding.topHeader.title = getString(R.string.txt_update_details)
                            openOfferAddDialog(
                                getString(R.string.txt_offer_added),
                                response.body()!!.message!!
                            )
                            // common.openDialog(requireActivity(), "Your offers have been added. Remember to preview and submit.")
                        } else {
                             common.openDialog(requireActivity(), response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateOfferRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    private fun callUpdateOfferApi(offers: Offerslive) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doVenueofferUpdate(
                PrefManager.getAccessToken(),
                UpdateVenueOfferReq(
                    ""+offers.id,
                    binding.inclAddDetails.edtText.text.toString(),
                    binding.inclDay.edtText.text.toString(),
                    common.convertDateInFormat(
                        binding.inclOfferStarts.edtText.text.toString(),
                        Constant().hhMmA,
                        Constant().hhMM
                    )!!,
                    common.convertDateInFormat(
                        binding.inclOfferEnds.edtText.text.toString(),
                        Constant().hhMmA,
                        Constant().hhMM
                    )!!,
                    binding.inclOfferStarts.edtText.text.toString(),
                    binding.inclOfferEnds.edtText.text.toString(),
                    venuesId,
                    if(binding.inclRedeemOffer.edtText.text.toString().equals(Constant().noRestrictions)) "0" else binding.inclRedeemOffer.edtText.text.toString(),
                    1
                )
            )
            call!!.enqueue(object : Callback<CreateOfferRes?> {
                override fun onResponse(
                    call: Call<CreateOfferRes?>,
                    response: Response<CreateOfferRes?>
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.isSuccess) {
                            openOfferAddDialog(getString(R.string.txt_offer_added),response.body()!!.message)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateOfferRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun openOfferAddDialog(title: String, message: String?) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.ivClose.visibility = View.GONE

        binding.tvPassSuccess.text = title
        binding.tvSuccess.text = message

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            mActivity?.supportFragmentManager?.popBackStack()
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

}
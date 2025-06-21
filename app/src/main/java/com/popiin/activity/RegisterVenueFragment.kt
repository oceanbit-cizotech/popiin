package com.popiin.activity
import android.provider.OpenableColumns
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.*
import com.popiin.adapter.DaysAdapter
import com.popiin.adapter.OfferAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.*
import com.popiin.fragment.SearchAddressFragment
import com.popiin.listener.AddressSelectionListener
import com.popiin.listener.DayTimeChangeListener
import com.popiin.listener.DialogListener
import com.popiin.listener.OfferListener
import com.popiin.model.*
import com.popiin.res.VenueListRes
import com.popiin.util.AttachmentHelper
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.views.ImageVideoView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.popiin.flowlayout.FlowLayout
import com.popiin.flowlayout.TagAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class RegisterVenueFragment : BaseFragment<FragmentRegisterVenueBinding>(),
    AddressSelectionListener {

    private var daysAdapter: DaysAdapter? = null
    private val timingModels = ArrayList<TimingModel>()
    private lateinit var ages: Array<String>
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val requestImage = 100
    private val requestPdf = 101
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private val permissionAll = 1
    private var reservationEnabled = -1
    private var outDoorEnabled = -1
    private var freeWifi = -1
    private var ageSelected = 0
    private var strAge = ""
    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var hours: List<String>
    private lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    private var hour: String? = ""
    private var minute: String? = ""
    private var amPm: String? = ""
    private var imageTypes = ""
    private lateinit var venueMusic: Array<String>
    private var venueTypeData: ArrayList<String> = ArrayList()
    private var venueReserveBinding: DialogVenueReservationBinding? = null
    private var counter = -1
    private var counterMenuImage = -1
    private val addImages = ArrayList<ImageVideoView>()
    lateinit var entertainment: Array<String>
    private lateinit var entertainmentAdapter: com.popiin.flowlayout.TagAdapter<*>
    private val menuImages = ArrayList<ImageVideoView>()
    private var venueOffers = ArrayList<VenueOffer>()
    private var reqVenueTypeCategory: ArrayList<VenueTypeCategory>? = ArrayList()
    private lateinit var typeBars: Array<String>
    private lateinit var typeRestaurant: Array<String>
    private lateinit var typeHotel: Array<String>
    private lateinit var typeNightclub: Array<String>
    private lateinit var typePub: Array<String>
    private lateinit var typeCafe: Array<String>
    private lateinit var typeVenueCategory: Array<String>
    private var venueCategory: ArrayList<String> = ArrayList()
    private var resVenueType: ArrayList<String>? = ArrayList()
    private var adapterBars: com.popiin.flowlayout.TagAdapter<*>? = null
    private var adapterRestaurant: com.popiin.flowlayout.TagAdapter<*>? = null
    private var adapterHotel: com.popiin.flowlayout.TagAdapter<*>? = null
    private var adapterNightclub: com.popiin.flowlayout.TagAdapter<*>? = null
    private var adapterPub: com.popiin.flowlayout.TagAdapter<*>? = null
    private var adapterCafe: com.popiin.flowlayout.TagAdapter<*>? = null
    private var venueMusicAdapter: com.popiin.flowlayout.TagAdapter<*>? = null
    private var mInflater: LayoutInflater? = null
    private var strDisBar: String? = ""
    private var strDisPub: String? = ""
    private var strDisHotel: String? = ""
    private var strDisRestaurant: String? = ""
    private var strDisNightClub: String? = ""
    private var strCafe: String? = ""
    private var strOther: String? = ""
    private var code = 10

    override fun onAddressSelected(address: String) {
        binding.inclVenueAddress.inclVenueAddress.edtName.text = common.registerVenueModel.address
        binding.inclVenueAddress.inclVenueCity.edtName.text = common.registerVenueModel.city
        binding.inclVenueAddress.inclVenuePincode.edtName.text = common.createEventModel.postCode
        initViews()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_register_venue
    }

    companion object {
        var isEditVenue = false
        var venueModel: VenueListRes.Venue? = null
        fun newInstance(item: VenueListRes.Venue?, isEdit: Boolean): RegisterVenueFragment {
            venueModel = item
            isEditVenue = isEdit
            return RegisterVenueFragment()
        }
    }

    override fun initViews() {
        venueTypeData.clear()
        binding.topHeader.ivBack.setOnClickListener {
            if (isEditVenue || common.registerVenueModel.isBasicInfoVerified || common.registerVenueModel.isAddressVerfied || common.registerVenueModel.isOpenCloseVerified || common.registerVenueModel.isOtherInfoVerified) {
                showExitPopup()
            } else {
                mActivity!!.supportFragmentManager.popBackStack()
            }
        }

        ages = resources.getStringArray(R.array.age)
        entertainment = resources.getStringArray(R.array.entertainment)

        mInflater = LayoutInflater.from(mActivity)

        setVenueButtonEnabled()
        if (venueModel != null) {

            val images = arrayOfNulls<String>(venueModel!!.images!!.size)
            for (i in 0 until venueModel!!.images!!.size) {
                images[i] = venueModel!!.images!![i].image
            }

            val menuImages = arrayOfNulls<String>(venueModel!!.menus!!.size)
            for (i in 0 until venueModel!!.menus!!.size) {
                menuImages[i] = venueModel!!.menus!![i].image
            }

            val offerData = arrayOfNulls<String>(venueModel!!.offers!!.size)
            if (venueModel!!.offers != null && venueModel!!.offers!!.size > 0) {
                for (i in 0 until venueModel!!.offers!!.size) {
                    offerData[i] += venueModel!!.offers!![i].title + ", "
                }
            }


            for (i in 0 until venueModel!!.venuetypes!!.size) {
                venueTypeData.add(venueModel!!.venuetypes!![i].venue_type!!)
            }


            common.registerVenueModel = RegisterVenueModel(
                venueName = venueModel!!.venue_name!!,
                age = venueModel!!.venue_age_group!!,
               description =  venueModel!!.venue_description!!,
                venueImages=images,
                venueMenuImages=menuImages,
                address=if (common.registerVenueModel.address.isNotEmpty()) common.registerVenueModel.address else venueModel!!.venue_address!!,
                city = venueModel!!.venue_city!!,
                postal_code = venueModel!!.venue_postal_code!!,
                timingList=venueModel!!.timings!!.map { timeData ->
                    TimingModel(
                        day = timeData.working_day!!,
                        open_time = timeData.open_time,
                        close_time = timeData.close_time,
                        is_24hours = timeData.is_24hours,
                        isClear = true
                    )
                } as ArrayList<TimingModel>,
                (if (venueModel!!.entertainment == null) "" else venueModel!!.entertainment)!!,
                "",
                venueModel!!.venue_type,
                venueTypeData,
                venueModel!!.venuecategories!!
                    .distinctBy { it.venue_type to it.category_name }  // Removes duplicates
                    .map { venueCategories ->
                        VenueTypeCategory(
                            type = venueCategories.venue_type!!,
                            category_name = venueCategories.category_name!!
                        )
                    } as ArrayList<VenueTypeCategory>,
                venueModel!!.venue_dress_code!!,
                venueModel!!.venue_door_policy!!,
                venueModel!!.venue_last_entry!!,
                offerData,
                venueModel!!.reservation_enabled == 1,
                venueModel!!.is_outdoor_area == 1,
                (if (venueModel!!.venue_other_information == null) "" else venueModel!!.venue_other_information)!!,
                isBasicInfoVerified = true,
                isAddressVerfied = true,
                isOpenCloseVerified = true,
                isOtherInfoVerified = true,
                latitude = venueModel!!.venue_latitude!!,
                longitude = venueModel!!.venue_longitude!!,
                venueModel!!.free_wifi == 1,
                (if (venueModel!!.venue_music == null) "" else venueModel!!.venue_music)!!,
                (if (venueModel!!.venue_djline == null) "" else venueModel!!.venue_djline)!!
            )
        }

        binding.btnRegisterVenue.setOnClickListener {
            if (isEditVenue) {
                setFragmentAdd(
                    VenueSummaryFragment.newInstance(
                        venueModel,
                        true
                    ), VenueSummaryFragment::class.java.simpleName
                )

            } else {
                setFragmentAdd(
                    VenueSummaryFragment.newInstance(
                        venueModel,
                        false
                    ), VenueSummaryFragment::class.java.simpleName
                )
            }
        }

        common.handleErrorView(
            binding.inclVenueBasicInfo.inclVenueName.edtName,
            binding.inclVenueBasicInfo.inclVenueName.tvError
        )
        common.handleErrorView(
            binding.inclVenueBasicInfo.inclDescription.edtText,
            binding.inclVenueBasicInfo.inclDescription.tvError
        )
        common.handleErrorView(
            binding.inclVenueAddress.inclVenueAddress.edtName,
            binding.inclVenueAddress.inclVenueAddress.tvError
        )
        common.handleErrorView(
            binding.inclVenueAddress.inclVenueCity.edtName,
            binding.inclVenueAddress.inclVenueCity.tvError
        )
        common.handleErrorView(
            binding.inclVenueAddress.inclVenuePincode.edtName,
            binding.inclVenueAddress.inclVenuePincode.tvError
        )

        typeBars = resources.getStringArray(R.array.type_bar)
        typePub = resources.getStringArray(R.array.type_pub)
        typeHotel = resources.getStringArray(R.array.type_hotel)
        typeRestaurant = resources.getStringArray(R.array.type_restaurant)
        typeNightclub = resources.getStringArray(R.array.type_nightclub)
        typeCafe = resources.getStringArray(R.array.type_cafe)
        typeVenueCategory = resources.getStringArray(R.array.venue_category)
        venueCategory.clear()
        venueCategory = typeVenueCategory.toCollection(ArrayList())
        venueMusic = resources.getStringArray(R.array.venue_music)

        binding.inclVenueOtherDetails.inclVenueType.edtText.setOnClickListener {
            openVenueTypeDialog()
        }

        binding.inclVenueOtherDetails.inclVenueMusic.edtText.setOnClickListener {
            openMusicDialog()
        }

        binding.inclVenueOtherDetails.inclVenueEntertainment.edtText.setOnClickListener {
            openEntertainmentDialog()
        }

        binding.inclVenueOtherDetails.inclVenueOffer.edtText.setOnClickListener {
            openOfferDialog()
        }

        binding.inclVenueOtherDetails.inclReservation.edtText.setOnClickListener {
            openReservationDialog()
        }

        binding.inclVenueOtherDetails.inclOutdoorArea.edtText.setOnClickListener {
            openOutdoorDialog()
        }

        binding.inclVenueOtherDetails.inclFreeWifi.edtText.setOnClickListener {
            openWifiDialog()
        }


        val weekName = resources.getStringArray(R.array.week_names)

        if(timingModels.size>0){
            timingModels.clear()
        }
        for (i in weekName.indices) {
            timingModels.add(TimingModel(weekName[i], "", "", 0, true))
        }

        binding.inclVenueOpenClose.rvDays.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        daysAdapter = DaysAdapter(timingModels, timeChangeListener)
        binding.inclVenueOpenClose.rvDays.adapter = daysAdapter

        setUiWithExpandCollapse()

        if (!hasPermissions(mActivity, *permissions)) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, permissionAll)
        }

        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_item, ages)
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.inclVenueBasicInfo.inclAge.spinner.adapter = aa



        binding.inclVenueBasicInfo.inclAge.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                binding.inclVenueBasicInfo.inclAge.spinner.clearFocus()
                binding.inclVenueBasicInfo.inclAge.spinner.requestFocus()

                (view as TextView).setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorHeaderText
                    )
                )

                ageSelected = position
                strAge = ages[position]
                if (ageSelected == ages.size - 1) {
                    binding.inclVenueBasicInfo.venueAgeOther.root.visibility = View.VISIBLE
                    binding.inclVenueBasicInfo.venueAgeOther.edtName.hint =
                        getString(R.string.txt_enter_age)
                } else {
                    binding.inclVenueBasicInfo.venueAgeOther.root.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.inclVenueBasicInfo.imgAddImage.setOnClickListener {
            imageTypes = CONSTANT.IMAGE
            imageGetterPopup(false)
        }

        binding.inclVenueBasicInfo.imgMenuImage.setOnClickListener {
            imageTypes = CONSTANT.MENUIMAGE
            imageGetterPopup(true)
        }


        binding.inclVenueBasicInfo.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateBasicInfo()) {
                showToast(getString(R.string.txt_basic_info_saved))
                common.registerVenueModel.venueName =
                    binding.inclVenueBasicInfo.inclVenueName.edtName.text.toString()
                common.registerVenueModel.description =
                    binding.inclVenueBasicInfo.inclDescription.edtText.text.toString()

                val images = arrayOfNulls<String>(addImages.size)
                for (i in 0 until addImages.size) {
                    images[i] = addImages[i].imageUrl
                }

                if (strAge == Constant().otherConst) {
                    strAge = binding.inclVenueBasicInfo.venueAgeOther.edtName.getText().toString()
                }

                val menuImage = arrayOfNulls<String>(menuImages.size)
                for (i in 0 until menuImages.size) {
                    menuImage[i] = menuImages[i].imageUrl
                }

                common.registerVenueModel.age = strAge
                common.registerVenueModel.venueImages = images
                common.registerVenueModel.venueMenuImages = menuImage

                common.registerVenueModel.isBasicInfoVerified = true
                binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
                binding.inclBasicInfo.chkDropDown.isChecked = false
                binding.inclVenueBasicInfo.root.visibility = View.GONE
                setVenueButtonEnabled()
            } else {
                common.registerVenueModel.isBasicInfoVerified = false
                binding.inclBasicInfo.uiVerified.visibility = View.GONE
                binding.inclBasicInfo.chkDropDown.isChecked = true
                binding.inclVenueBasicInfo.root.visibility = View.VISIBLE
                setVenueButtonEnabled()
            }
        }

        binding.inclVenueAddress.inclVenueAddress.edtName.isFocusable = false
        binding.inclVenueAddress.inclVenueAddress.edtName.isFocusableInTouchMode = false
        binding.inclVenueAddress.inclVenueAddress.edtName.setSingleLine(true)
        binding.inclVenueAddress.inclVenueAddress.edtName.ellipsize = TextUtils.TruncateAt.END

        binding.inclVenueAddress.inclVenueAddress.edtName.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val searchFragment = SearchAddressFragment.newInstance(1)
            searchFragment.addressListener = this
            transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            transaction.add(R.id.frame_layout, searchFragment)
            transaction.addToBackStack(SearchAddressFragment::class.java.simpleName)
            transaction.commit()
        }

        binding.inclVenueAddress.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateAddress()) {
//                callApiForLatLong()
                showToast(getString(R.string.txt_address_saved))
                common.registerVenueModel.isAddressVerfied = true
                binding.inclAddress.uiVerified.visibility = View.VISIBLE
                binding.inclAddress.chkDropDown.isChecked = false
                binding.inclVenueAddress.root.visibility = View.GONE
                setVenueButtonEnabled()
            } else {
                common.registerVenueModel.isAddressVerfied = false
                binding.inclAddress.uiVerified.visibility = View.GONE
                binding.inclAddress.chkDropDown.isChecked = true
                binding.inclVenueAddress.root.visibility = View.VISIBLE
                setVenueButtonEnabled()
            }
        }


        binding.inclVenueOpenClose.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateOpenClose()) {
                showToast(getString(R.string.txt_opening_hours_saved))
                val tempTiming: ArrayList<TimingModel> = ArrayList()
                for (i in timingModels.indices) {
                    if (timingModels[i].open_time!!.isNotEmpty() && timingModels[i].close_time!!.isNotEmpty()) {
                        tempTiming.add(timingModels[i])
                    }
                }
                common.registerVenueModel.timingList = tempTiming

                common.registerVenueModel.isOpenCloseVerified = true
                binding.inclOpenCloseHours.uiVerified.visibility = View.VISIBLE
                binding.inclOpenCloseHours.chkDropDown.isChecked = false
                binding.inclVenueOpenClose.root.visibility = View.GONE
                setVenueButtonEnabled()
            } else {
                common.registerVenueModel.isOpenCloseVerified = false
                binding.inclOpenCloseHours.uiVerified.visibility = View.GONE
                binding.inclOpenCloseHours.chkDropDown.isChecked = true
                binding.inclVenueOpenClose.root.visibility = View.VISIBLE
                setVenueButtonEnabled()
            }

        }


        binding.inclVenueOtherDetails.tvSave.setOnClickListener {
            hideKeyBoard(requireActivity())
            if (isValidateOtherDetails()) {
                showToast(getString(R.string.txt_other_details_saved))
                common.registerVenueModel.dress_code =
                    binding.inclVenueOtherDetails.inclDressCode.edtText.text.toString()
                common.registerVenueModel.entry_policy =
                    binding.inclVenueOtherDetails.inclEntryPolicy.edtText.text.toString()
                common.registerVenueModel.offers =venueOffers.map { it.title }.toTypedArray()
                common.registerVenueModel.last_entry =
                    binding.inclVenueOtherDetails.edtLastEntry.text.toString()
                common.registerVenueModel.other_info =
                    binding.inclVenueOtherDetails.inclVenueOther.edtText.text.toString()

                common.registerVenueModel.isOtherInfoVerified = true
                binding.inclOtherDetails.uiVerified.visibility = View.VISIBLE
                binding.inclOtherDetails.chkDropDown.isChecked = false
                binding.inclVenueOtherDetails.root.visibility = View.GONE
                setVenueButtonEnabled()
            } else {
                common.registerVenueModel.isOtherInfoVerified = false
                binding.inclOtherDetails.uiVerified.visibility = View.GONE
                binding.inclOtherDetails.chkDropDown.isChecked = true
                binding.inclVenueOtherDetails.root.visibility = View.VISIBLE
                setVenueButtonEnabled()
            }
        }
        setData()

    }


    private fun showExitPopup() {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.tvPassSuccess.text = getString(R.string.txt_alert)
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
            mActivity!!.supportFragmentManager.popBackStack()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun setVenueButtonEnabled() {
        if (common.registerVenueModel.isOpenCloseVerified && common.registerVenueModel.isBasicInfoVerified && common.registerVenueModel.isOtherInfoVerified && common.registerVenueModel.isAddressVerfied) {
            binding.btnRegisterVenue.isEnabled = true
            binding.btnRegisterVenue.alpha = 1f
        } else {
            binding.btnRegisterVenue.isEnabled = false
            binding.btnRegisterVenue.alpha = 0.5f
        }
    }

    private fun openOutdoorDialog() {
        val dialog = PopupWindow(mActivity)
        venueReserveBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_venue_reservation,
            null, false
        )

        venueReserveBinding!!.title = getString(R.string.txt_select_outdoor_area)
        setReserveUi(outDoorEnabled)

        venueReserveBinding!!.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        venueReserveBinding!!.btnDone.setOnClickListener {
            dialog.dismiss()
            common.registerVenueModel.outDoorEnabled = outDoorEnabled == 1
            this.binding.inclVenueOtherDetails.inclOutdoorArea.edtText.setText(
                if (outDoorEnabled == 1) getString(
                    R.string.txt_yes
                ) else getString(R.string.txt_no)
            )
        }

        venueReserveBinding!!.inclYes.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                outDoorEnabled = 1
            }

            setReserveUi(outDoorEnabled)

        }

        venueReserveBinding!!.inclNo.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                outDoorEnabled = 0
            }
            setReserveUi(outDoorEnabled)
        }

        venueReserveBinding!!.inclYes.root.setOnClickListener {
            outDoorEnabled = 1
            setReserveUi(outDoorEnabled)
        }

        venueReserveBinding!!.inclNo.root.setOnClickListener {
            outDoorEnabled = 0
            setReserveUi(outDoorEnabled)
        }


        dialog.animationStyle = R.style.animation
        dialog.contentView = venueReserveBinding!!.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(venueReserveBinding!!.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun openWifiDialog() {
        val dialog = PopupWindow(mActivity)
        venueReserveBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_venue_reservation,
            null, false
        )

        venueReserveBinding!!.title = getString(R.string.txt_select_wifi)

        setReserveUi(freeWifi)

        venueReserveBinding!!.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        venueReserveBinding!!.btnDone.setOnClickListener {
            dialog.dismiss()
            common.registerVenueModel.free_wifi = freeWifi == 1
            this.binding.inclVenueOtherDetails.inclFreeWifi.edtText.setText(
                if (freeWifi == 1) getString(
                    R.string.txt_yes
                ) else getString(R.string.txt_no)
            )
        }

        venueReserveBinding!!.inclYes.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                freeWifi = 1
            }

            setReserveUi(freeWifi)

        }

        venueReserveBinding!!.inclNo.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                freeWifi = 0
            }
            setReserveUi(freeWifi)
        }

        venueReserveBinding!!.inclYes.root.setOnClickListener {
            freeWifi = 1
            setReserveUi(freeWifi)
        }

        venueReserveBinding!!.inclNo.root.setOnClickListener {
            freeWifi = 0
            setReserveUi(freeWifi)
        }


        dialog.animationStyle = R.style.animation
        dialog.contentView = venueReserveBinding!!.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(venueReserveBinding!!.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun openReservationDialog() {
        val dialog = PopupWindow(mActivity)
        venueReserveBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_venue_reservation,
            null, false
        )

        setReserveUi(reservationEnabled)
        venueReserveBinding!!.title = getString(R.string.txt_select_reservation)

        venueReserveBinding!!.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        venueReserveBinding!!.btnDone.setOnClickListener {
            dialog.dismiss()
            common.registerVenueModel.reservationEnabled = reservationEnabled == 1
            this.binding.inclVenueOtherDetails.inclReservation.edtText.setText(
                if (reservationEnabled == 1) getString(
                    R.string.txt_yes
                ) else getString(R.string.txt_no)
            )
        }

        venueReserveBinding!!.inclYes.root.setOnClickListener {
            reservationEnabled = 1
            setReserveUi(reservationEnabled)
        }

        venueReserveBinding!!.inclNo.root.setOnClickListener {
            reservationEnabled = 0
            setReserveUi(reservationEnabled)
        }

        venueReserveBinding!!.inclYes.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                reservationEnabled = 1
            }

            setReserveUi(reservationEnabled)

        }

        venueReserveBinding!!.inclNo.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                reservationEnabled = 0
            }
            setReserveUi(reservationEnabled)
        }


        dialog.animationStyle = R.style.animation
        dialog.contentView = venueReserveBinding!!.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(venueReserveBinding!!.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()

    }

    private fun setReserveUi(reservationEnable: Int) {
        when (reservationEnable) {
            0 -> {
                venueReserveBinding!!.inclNo.isSelected = true
                venueReserveBinding!!.inclYes.isSelected = false
                venueReserveBinding!!.inclYes.checkbox.isChecked = false
                venueReserveBinding!!.inclNo.checkbox.isChecked = true
            }
            1 -> {
                venueReserveBinding!!.inclYes.isSelected = true
                venueReserveBinding!!.inclNo.isSelected = false
                venueReserveBinding!!.inclNo.checkbox.isChecked = false
                venueReserveBinding!!.inclYes.checkbox.isChecked = true
            }
        }
    }


    private var venueAdapter: OfferAdapter? = null

    val venueOfferListener: OfferListener<VenueOffer?> =
        object : OfferListener<VenueOffer?>() {
            override fun onCloseClick(item: VenueOffer?, position: Int) {
                super.onCloseClick(item, position)
                venueOffers.removeAt(position)
                venueAdapter!!.notifyDataSetChanged()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    var offersString = ""
    private fun openOfferDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogVenueOffersBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_venue_offers, null, false)
        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        venueOffers.clear()

        if(venueModel!=null && venueModel?.offers!!.size>0) {
            venueOffers = ArrayList(
                venueModel?.offers!!.map { offer ->
                    VenueOffer(
                        title = offer.title!!,
                    )
                }
            )
        }
        val layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true)
        venueAdapter = OfferAdapter(venueOffers, venueOfferListener)
        binding.rvOffer.layoutManager = layoutManager
        binding.rvOffer.adapter = venueAdapter


        binding.btnDone.setOnClickListener {
            dialog.dismiss()

            venueModel?.let { model ->
                if (venueOffers.isNotEmpty()) {
                    model.offers = ArrayList() // Re-initialize to avoid potential null issues
                    venueOffers.forEach { offer ->
                        model.offers.add(VenueListRes.Offer(offer.title))
                    }
                }
            }

            var offersString = ""
            if(venueOffers.size>0) {
                if (venueOffers.size > 1) {
                    offersString = venueOffers[0].title + " + " + (venueOffers.size - 1) + " Other"
                } else if (venueOffers.size == 1) {
                    offersString = venueOffers[0].title
                }
            }

            this.binding.inclVenueOtherDetails.inclVenueOffer.edtText.setText(offersString)
            common.registerVenueModel.offers = venueOffers.map { it.title }.toTypedArray()
        }

        binding.tvPlusOffer.setOnClickListener {
            venueOffers.add(VenueOffer(""))
            venueAdapter!!.notifyDataSetChanged()
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
                if (venueModel != null) {
                    setMusicData(binding, height, venueModel!!.venue_music!!)
                } else {
                    setMusicData(binding, height, common.registerVenueModel.music)
                }

                binding.tegview.visibility = View.VISIBLE
                binding.tvSave.visibility = View.VISIBLE
                binding.tvVenueMusicTitle.visibility = View.VISIBLE
                binding.edtVenueMusicName.visibility = View.VISIBLE
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

                common.registerVenueModel.venueMusicDjLine =
                    binding.edtVenueMusicName.text.toString()
            }
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            setEntertainTypeData(
                binding.tegview.selectedList.iterator(),
                venueMusic,
                binding.inclOther.edtText,
                Constant().music
            )
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

    private fun openEntertainmentDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogEntertainmentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_entertainment, null, false
        )

        binding.inclEventEntertainment.viewBottom.visibility = View.GONE


        binding.inclEventEntertainment.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (venueModel != null) {
                    setEntertainmentData(binding, venueModel!!.entertainment!!)
                } else {
                    setEntertainmentData(binding, common.registerVenueModel.entertainment)
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

            setEntertainTypeData(
                binding.inclEventEntertainment.tegview.selectedList.iterator(),
                entertainment,
                binding.inclEventEntertainment.inclOther.edtText,
                Constant().entertainment
            )
        }

        common.registerVenueModel.venueEntertainmentName =
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
            venueMusicAdapter!!.setSelectedList(music)
            venueMusicAdapter!!.notifyDataChanged()

            if (musicData.contains(Constant().otherConst)) {
                val other: List<String> = musicData.split(Constant().otherConst)
                binding.inclOther.root.visibility = View.VISIBLE
                binding.inclOther.edtText.setText(other[1].replace(CONSTANT.SEPRATEOR_OTHER, ""))
            }

            if (venueModel != null) {
                if (venueModel!!.venue_djline != null) {
                    binding.edtVenueMusicName.text = venueModel!!.venue_djline!!
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


    private fun isValidateOtherDetails(): Boolean {
        var isValid = true

        if (binding.inclVenueOtherDetails.inclVenueType.edtText.text.toString().isEmpty()) {
            binding.inclVenueOtherDetails.inclVenueType.tvError.visibility = View.VISIBLE
            binding.inclVenueOtherDetails.inclVenueType.tvError.text =
                getString(R.string.txt_alert_incomplete_section)
            isValid = false
        }

//            if (binding.inclVenueOtherDetails.inclDressCode.edtText.text.toString().isEmpty()) {
//                binding.inclVenueOtherDetails.inclDressCode.tvError.visibility = View.VISIBLE
//                binding.inclVenueOtherDetails.inclDressCode.tvError.text =
//                    getString(R.string.txt_err_venues_dress_code)
//                isValid = false
//            }
//
//            if (binding.inclVenueOtherDetails.inclEntryPolicy.edtText.text.toString().isEmpty()) {
//                binding.inclVenueOtherDetails.inclEntryPolicy.tvError.visibility = View.VISIBLE
//                binding.inclVenueOtherDetails.inclEntryPolicy.tvError.text =
//                    getString(R.string.txt_err_venues_entry_policy)
//                isValid = false
//            }


        return isValid
    }


    private fun openVenueTypeDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogVenueTypeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_venue_type, null, false
        )

        val displayMetrics = DisplayMetrics()

        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels

        binding.clPass.height = (height * 0.80).toInt()
        binding.clPass.requestLayout()

        setTextSizeAndColor(binding)



        binding.ivClose.setOnClickListener {
            dialog.dismiss()
            if (common.registerVenueModel.venueType == null && common.registerVenueModel.venueTypeCategory.size == 0) {
                resVenueType!!.clear()
                reqVenueTypeCategory!!.clear()
            }
        }

        if (common.registerVenueModel.venueTypes.size > 0 && common.registerVenueModel.venueTypeCategory.size > 0) {
            binding.btnDone.alpha = 1f
            binding.btnDone.isEnabled = false
        } else {
            binding.btnDone.alpha = 0.5f
            binding.btnDone.isEnabled = true
        }


        binding.btnDone.setOnClickListener {
            if (isValidVenueType(binding)) {
                dialog.dismiss()
                var venueTypeString = ""
                for (i in 0 until resVenueType!!.size) {
                    venueTypeString += if (i == resVenueType!!.size - 1) {
                        resVenueType!![i]
                    } else {
                        resVenueType!![i] + ", "
                    }
                }

                this.binding.inclVenueOtherDetails.inclVenueType.edtText.setText(venueTypeString)
                common.registerVenueModel.venueTypes = resVenueType!!
                common.registerVenueModel.venueTypeCategory = reqVenueTypeCategory!!
            }
        }

        binding.inclVenueBar.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().barConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_bar_saved))
            }

        }

        binding.inclVenuePub.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().pubConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_pub_saved))
            }
        }

        binding.inclVenueHotel.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().hotelConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_hotel_saved))
            }
        }

        binding.inclVenueRestaurant.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().restaurantConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_restaurant_saved))
            }
        }

        binding.inclVenueNightclub.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().nightClub)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_night_club_saved))
            }
        }

        binding.inclVenueCafe.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().cafeConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_cafe_saved))
            }
        }

        binding.inclVenueOthers.tvSave.setOnClickListener {
            if (isValidSelectedVenueType(binding, Constant().otherConst)) {
                binding.btnDone.alpha = 1f
                binding.btnDone.isEnabled = true
                showToast(getString(R.string.txt_other_saved))
            }
        }


        binding.inclVenueBar.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueBar.tegview.visibility = View.VISIBLE
                binding.inclVenueBar.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().barConst) {
                        setVenueData(
                            binding,
                            binding.inclVenueBar,
                            height,
                            adapterBars,
                            common.registerVenueModel.venueTypeCategory,
                            typeBars
                        )
                    }
                }
            } else {
                binding.inclVenueBar.tegview.visibility = View.GONE
                binding.inclVenueBar.tvSave.visibility = View.GONE
                binding.inclVenueBar.inclOther.root.visibility = View.GONE
                binding.inclVenueBar.inclOther.edtText.setText("")
            }
        }

        binding.inclVenuePub.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenuePub.tegview.visibility = View.VISIBLE
                binding.inclVenuePub.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().pubConst) {
                        setVenueData(
                            binding,
                            binding.inclVenuePub,
                            height,
                            adapterPub,
                            common.registerVenueModel.venueTypeCategory,
                            typePub
                        )
                    }
                }

            } else {
                binding.inclVenuePub.tegview.visibility = View.GONE
                binding.inclVenuePub.tvSave.visibility = View.GONE
                binding.inclVenuePub.inclOther.root.visibility = View.GONE
                binding.inclVenuePub.inclOther.edtText.setText("")
            }
        }

        binding.inclVenueHotel.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueHotel.tegview.visibility = View.VISIBLE
                binding.inclVenueHotel.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().hotelConst) {
                        setVenueData(
                            binding,
                            binding.inclVenueHotel,
                            height,
                            adapterHotel,
                            common.registerVenueModel.venueTypeCategory,
                            typeHotel
                        )
                    }
                }

            } else {
                binding.inclVenueHotel.tegview.visibility = View.GONE
                binding.inclVenueHotel.tvSave.visibility = View.GONE
                binding.inclVenueHotel.inclOther.root.visibility = View.GONE
                binding.inclVenueHotel.inclOther.edtText.setText("")
            }
        }

        binding.inclVenueRestaurant.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueRestaurant.tegview.visibility = View.VISIBLE
                binding.inclVenueRestaurant.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().restaurantConst) {
                        setVenueData(
                            binding,
                            binding.inclVenueRestaurant,
                            height,
                            adapterRestaurant,
                            common.registerVenueModel.venueTypeCategory,
                            typeRestaurant
                        )
                    }
                }

            } else {
                binding.inclVenueRestaurant.tegview.visibility = View.GONE
                binding.inclVenueRestaurant.tvSave.visibility = View.GONE
                binding.inclVenueRestaurant.inclOther.root.visibility = View.GONE
                binding.inclVenueRestaurant.inclOther.edtText.setText("")
            }
        }

        binding.inclVenueNightclub.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueNightclub.tegview.visibility = View.VISIBLE
                binding.inclVenueNightclub.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().nightClub) {
                        setVenueData(
                            binding,
                            binding.inclVenueNightclub,
                            height,
                            adapterNightclub,
                            common.registerVenueModel.venueTypeCategory,
                            typeNightclub
                        )
                    }
                }

            } else {
                binding.inclVenueNightclub.tegview.visibility = View.GONE
                binding.inclVenueNightclub.tvSave.visibility = View.GONE
                binding.inclVenueNightclub.inclOther.root.visibility = View.GONE
                binding.inclVenueNightclub.inclOther.edtText.setText("")
            }
        }

        binding.inclVenueCafe.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueCafe.tegview.visibility = View.VISIBLE
                binding.inclVenueCafe.tvSave.visibility = View.VISIBLE
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().cafeConst) {
                        setVenueData(
                            binding,
                            binding.inclVenueCafe,
                            height,
                            adapterCafe,
                            common.registerVenueModel.venueTypeCategory,
                            typeCafe
                        )
                    }
                }

            } else {
                binding.inclVenueCafe.tegview.visibility = View.GONE
                binding.inclVenueCafe.tvSave.visibility = View.GONE
                binding.inclVenueCafe.inclOther.root.visibility = View.GONE
                binding.inclVenueCafe.inclOther.edtText.setText("")
            }
        }

        binding.inclVenueOthers.switchVenueConfirm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.inclVenueOthers.inclOther.root.visibility = View.VISIBLE
                binding.inclVenueOthers.tvSave.visibility = View.VISIBLE
                var musicData = ""
                for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
                    if (common.registerVenueModel.venueTypeCategory[i].type == Constant().otherConst) {
                        musicData += common.registerVenueModel.venueTypeCategory[i].category_name.replace(
                            CONSTANT.SEPRATEOR,
                            ","
                        )
                    }
                }
                binding.inclVenueOthers.inclOther.edtText.setText(musicData)
            } else {
                binding.inclVenueOthers.inclOther.root.visibility = View.GONE
                binding.inclVenueOthers.tvSave.visibility = View.GONE
            }
        }

        binding.inclVenueBar.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(typeBars) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv,
                    binding.inclVenueBar.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                binding.inclVenueBar.tvErrorTags.visibility = View.GONE
                if (typeBars.size - 1 == position) {
                    binding.inclVenueBar.inclOther.root.visibility = View.GONE
                    binding.inclVenueBar.inclOther.edtText.text=""
                }
            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (typeBars.size - 1 == position) {
                    binding.inclVenueBar.inclOther.root.visibility = View.GONE
                    binding.inclVenueBar.inclOther.edtText.setText("")

                    if (otherBar.isNotEmpty()) {
                        otherBar = ""
                    }
                }
            }
        }.also { adapterBars = it }

        binding.inclVenuePub.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(typePub) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv,
                    binding.inclVenuePub.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                binding.inclVenuePub.tvErrorTags.visibility = View.GONE
                if (typePub.size - 1 == position) {
                    binding.inclVenuePub.inclOther.root.visibility = View.GONE
                    binding.inclVenuePub.inclOther.edtText.setText("")
                }
            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (typePub.size - 1 == position) {
                    binding.inclVenuePub.inclOther.root.visibility = View.GONE
                    binding.inclVenuePub.inclOther.edtText.setText("")

                    if (otherPub.isNotEmpty()) {
                        otherPub = ""
                    }
                }
            }
        }.also { adapterPub = it }

        binding.inclVenueHotel.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(typeHotel) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv,
                    binding.inclVenueHotel.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                binding.inclVenueHotel.tvErrorTags.visibility = View.GONE
                if (typeHotel.size - 1 == position) {
                    binding.inclVenueHotel.inclOther.root.visibility = View.GONE
                    binding.inclVenueHotel.inclOther.edtText.setText("")
                }
            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (typeHotel.size - 1 == position) {
                    binding.inclVenueHotel.inclOther.root.visibility = View.GONE
                    binding.inclVenueHotel.inclOther.edtText.setText("")

                    if (otherHotel.isNotEmpty()) {
                        otherHotel = ""
                    }
                }
            }
        }.also { adapterHotel = it }

        binding.inclVenueRestaurant.tegview.adapter = object :TagAdapter<String?>(typeRestaurant) {
            override fun getView(parent: FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv,
                    binding.inclVenueRestaurant.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                binding.inclVenueRestaurant.tvErrorTags.visibility = View.GONE
                if (typeRestaurant.size - 1 == position) {
                    binding.inclVenueRestaurant.inclOther.root.visibility = View.GONE
                    binding.inclVenueRestaurant.inclOther.edtText.setText("")
                }
            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (typeRestaurant.size - 1 == position) {
                    binding.inclVenueRestaurant.inclOther.root.visibility = View.GONE
                    binding.inclVenueRestaurant.inclOther.edtText.setText("")

                    if (otherRestaurant.isNotEmpty()) {
                        otherRestaurant = ""
                    }
                }
            }
        }.also { adapterRestaurant = it }

        binding.inclVenueNightclub.tegview.adapter = object :TagAdapter<String?>(typeNightclub) {
                override fun getView(parent: FlowLayout, position: Int, s: String?): View {
                    val tv = mInflater!!.inflate(
                        R.layout.tv,
                        binding.inclVenueNightclub.tegview, false
                    ) as TextView
                    tv.text = s
                    return tv
                }

                override fun onSelected(position: Int, view: View) {
                    super.onSelected(position, view)
                    binding.inclVenueNightclub.tvErrorTags.visibility = View.GONE
                    if (typeNightclub.size - 1 == position) {
                        binding.inclVenueNightclub.inclOther.root.visibility = View.GONE
                        binding.inclVenueNightclub.inclOther.edtText.setText("")
                    }
                }

                override fun unSelected(position: Int, view: View) {
                    super.unSelected(position, view)
                    if (typeNightclub.size - 1 == position) {
                        binding.inclVenueNightclub.inclOther.root.visibility = View.GONE
                        binding.inclVenueNightclub.inclOther.edtText.setText("")

                        if (otherNightclub.isNotEmpty()) {
                            otherNightclub = ""
                        }
                    }
                }
            }.also { adapterNightclub = it }

        binding.inclVenueCafe.tegview.adapter = object : com.popiin.flowlayout.TagAdapter<String?>(typeCafe) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout, position: Int, s: String?): View {
                val tv = mInflater!!.inflate(
                    R.layout.tv,
                    binding.inclVenueCafe.tegview, false
                ) as TextView
                tv.text = s
                return tv
            }

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)
                binding.inclVenueCafe.tvErrorTags.visibility = View.GONE
                if (typeCafe.size - 1 == position) {
                    binding.inclVenueCafe.inclOther.root.visibility = View.GONE
                    binding.inclVenueCafe.inclOther.edtText.setText("")
                }
            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)
                if (typeCafe.size - 1 == position) {
                    binding.inclVenueCafe.inclOther.root.visibility = View.GONE
                    binding.inclVenueCafe.inclOther.edtText.setText("")

                    if (otherCafe.isNotEmpty()) {
                        otherCafe = ""
                    }
                }
            }
        }.also { adapterCafe = it }


        for (i in 0 until common.registerVenueModel.venueTypeCategory.size) {
            if (common.registerVenueModel.venueTypeCategory[i].type == Constant().barConst) {
                binding.inclVenueBar.tegview.visibility = View.VISIBLE
                binding.inclVenueBar.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenueBar,
                    height,
                    adapterBars,
                    common.registerVenueModel.venueTypeCategory,
                    typeBars
                )
                isValidSelectedVenueType(binding, Constant().barConst)
                binding.btnDone.isEnabled = true

            }else  if (common.registerVenueModel.venueTypeCategory[i].type == Constant().pubConst) {
                binding.inclVenuePub.tegview.visibility = View.VISIBLE
                binding.inclVenuePub.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenuePub,
                    height,
                    adapterPub,
                    common.registerVenueModel.venueTypeCategory,
                    typePub
                )
                isValidSelectedVenueType(binding, Constant().pubConst)
                binding.btnDone.isEnabled = true

            }else  if (common.registerVenueModel.venueTypeCategory[i].type == Constant().hotelConst) {
                binding.inclVenueHotel.tegview.visibility = View.VISIBLE
                binding.inclVenueHotel.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenueHotel,
                    height,
                    adapterHotel,
                    common.registerVenueModel.venueTypeCategory,
                    typeHotel
                )
                isValidSelectedVenueType(binding, Constant().hotelConst)
                binding.btnDone.isEnabled = true
            }else  if (common.registerVenueModel.venueTypeCategory[i].type == Constant().restaurantConst) {
                binding.inclVenueRestaurant.tegview.visibility = View.VISIBLE
                binding.inclVenueRestaurant.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenueRestaurant,
                    height,
                    adapterRestaurant,
                    common.registerVenueModel.venueTypeCategory,
                    typeRestaurant
                )
                isValidSelectedVenueType(binding, Constant().restaurantConst)
                binding.btnDone.isEnabled = true
            }else  if (common.registerVenueModel.venueTypeCategory[i].type == Constant().nightClub) {
                binding.inclVenueNightclub.tegview.visibility = View.VISIBLE
                binding.inclVenueNightclub.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenueNightclub,
                    height,
                    adapterNightclub,
                    common.registerVenueModel.venueTypeCategory,
                    typeNightclub
                )
                isValidSelectedVenueType(binding, Constant().nightClub)
                binding.btnDone.isEnabled = true

            }else if (common.registerVenueModel.venueTypeCategory[i].type == Constant().cafeConst) {
                binding.inclVenueCafe.tegview.visibility = View.VISIBLE
                binding.inclVenueCafe.tvSave.visibility = View.VISIBLE
                setVenueData(
                    binding,
                    binding.inclVenueCafe,
                    height,
                    adapterCafe,
                    common.registerVenueModel.venueTypeCategory,
                    typeCafe
                )
                isValidSelectedVenueType(binding, Constant().cafeConst)
                binding.btnDone.isEnabled = true
            }else  if (common.registerVenueModel.venueTypeCategory[i].type == Constant().otherConst) {
                binding.inclVenueOthers.tegview.visibility = View.VISIBLE
                binding.inclVenueOthers.tvSave.visibility = View.VISIBLE
                var musicData = ""
                musicData += common.registerVenueModel.venueTypeCategory[i].category_name.replace(
                    CONSTANT.SEPRATEOR,
                    ","
                )
                binding.inclVenueOthers.inclOther.edtText.setText(musicData)
                isValidSelectedVenueType(binding, Constant().otherConst)
                binding.btnDone.isEnabled = true
            }
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

    private fun setTextSizeAndColor(binding: DialogVenueTypeBinding) {
        binding.inclVenueBar.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenueBar.inclOther.lblTitle.textSize = 15f
        binding.inclVenuePub.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenuePub.inclOther.lblTitle.textSize = 15f
        binding.inclVenueHotel.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenueHotel.inclOther.lblTitle.textSize = 15f
        binding.inclVenueRestaurant.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenueRestaurant.inclOther.lblTitle.textSize = 15f
        binding.inclVenueNightclub.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenueNightclub.inclOther.lblTitle.textSize = 15f
        binding.inclVenueCafe.inclOther.lblTitle.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )
        binding.inclVenueCafe.inclOther.lblTitle.textSize = 15f
        binding.inclVenueOthers.inclOther.lblTitle.visibility = View.GONE
    }

    private fun setVenueData(
        dldBinding: DialogVenueTypeBinding,
        binding: IncludeOtherVenueTypeBinding,
        height: Int,
        adapter: com.popiin.flowlayout.TagAdapter<*>?,
        venueTypeCategory: ArrayList<VenueTypeCategory>,
        dataType: Array<String>,
    ) {
        val music: MutableSet<Int> = HashSet()

        dldBinding.clPass.height = height
        dldBinding.clPass.requestLayout()

        var musicData = ""

        println("setVenueData : venueTypeCategory $venueTypeCategory")


        for (i in 0 until venueTypeCategory.size) {
            if (adapter == adapterBars && venueTypeCategory[i].type == Constant().barConst) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            } else if (adapter == adapterPub && venueTypeCategory[i].type == Constant().pubConst) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            } else if (adapter == adapterHotel && venueTypeCategory[i].type == Constant().hotelConst) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            } else if (adapter == adapterRestaurant && venueTypeCategory[i].type == Constant().restaurantConst) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            } else if (adapter == adapterNightclub && venueTypeCategory[i].type == Constant().nightClub) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            } else if (adapter == adapterCafe && venueTypeCategory[i].type == Constant().cafeConst) {
                musicData += venueTypeCategory[i].category_name.replace(CONSTANT.SEPRATEOR, ",")
            }

        }


        if (musicData.isNotEmpty()) {
            binding.switchVenueConfirm.isChecked = true
            binding.tegview.visibility = View.VISIBLE
            binding.tvSave.visibility = View.VISIBLE

            println("setVenueData : musicData : $musicData")

            for (i in dataType.indices) {
                if (musicData.contains(dataType[i])) {
                    music.add(i)
                }
            }

            println("setVenueData : music : $music")

            @Suppress("DEPRECATION")
            adapter!!.setSelectedList(music)
            adapter.notifyDataChanged()


            if (musicData.contains(Constant().otherConst)) {
                val other: List<String> = musicData.split(Constant().otherConst)
                if (adapter == adapterBars && dldBinding.inclVenueBar.switchVenueConfirm.isChecked) {
                    if (otherBar.isNotEmpty()) {
                        dldBinding.inclVenueBar.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueBar.inclOther.edtText.setText(otherBar)
                    } else if (adapter == adapterBars) {
                        dldBinding.inclVenueBar.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueBar.inclOther.edtText.setText(
                            other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "").replace(",", "")
                        )
                    } else {
                        dldBinding.inclVenueBar.inclOther.root.visibility = View.GONE
                    }
                }
                if (adapter == adapterPub && dldBinding.inclVenuePub.switchVenueConfirm.isChecked) {
                    if (otherPub.isNotEmpty()) {
                        dldBinding.inclVenuePub.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenuePub.inclOther.edtText.setText(otherPub)
                    } else if (adapter == adapterPub) {
                        dldBinding.inclVenuePub.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenuePub.inclOther.edtText.setText(
                            other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "").replace(",", "")
                        )
                    } else {
                        dldBinding.inclVenuePub.inclOther.root.visibility = View.GONE
                    }
                }

                if (adapter == adapterHotel && dldBinding.inclVenueHotel.switchVenueConfirm.isChecked) {
                    if (otherHotel.isNotEmpty()) {
                        dldBinding.inclVenueHotel.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueHotel.inclOther.edtText.setText(otherHotel)
                    } else if (adapter == adapterHotel) {
                        dldBinding.inclVenueHotel.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueHotel.inclOther.edtText.setText(other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "").replace(",", ""))
                    } else {
                        dldBinding.inclVenueHotel.inclOther.root.visibility = View.GONE
                    }
                }

                if (adapter == adapterRestaurant && dldBinding.inclVenueRestaurant.switchVenueConfirm.isChecked) {
                    if (otherRestaurant.isNotEmpty()) {
                        dldBinding.inclVenueRestaurant.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueRestaurant.inclOther.edtText.setText(otherRestaurant)
                    } else if (adapter == adapterRestaurant) {
                        dldBinding.inclVenueRestaurant.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueRestaurant.inclOther.edtText.setText(
                            other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "")
                        )
                    } else {
                        dldBinding.inclVenueRestaurant.inclOther.root.visibility = View.GONE
                    }
                }

                if (adapter == adapterNightclub && dldBinding.inclVenueNightclub.switchVenueConfirm.isChecked) {
                    if (otherNightclub.isNotEmpty()) {
                        dldBinding.inclVenueNightclub.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueNightclub.inclOther.edtText.setText(otherNightclub)
                    } else if (adapter == adapterNightclub) {
                        dldBinding.inclVenueNightclub.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueNightclub.inclOther.edtText.setText(
                            other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "").replace(",", "")
                        )
                    } else {
                        dldBinding.inclVenueNightclub.inclOther.root.visibility = View.GONE
                    }
                }

                if (adapter == adapterCafe && dldBinding.inclVenueCafe.switchVenueConfirm.isChecked) {
                    if (otherCafe.isNotEmpty()) {
                        dldBinding.inclVenueCafe.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueCafe.inclOther.edtText.setText(otherCafe)
                    } else if (adapter == adapterCafe) {
                        dldBinding.inclVenueCafe.inclOther.root.visibility = View.VISIBLE
                        dldBinding.inclVenueCafe.inclOther.edtText.setText(
                            other[1].replace(CONSTANT.SEPRATEOR_OTHER + ",", "").replace(",", "")
                        )
                    } else {
                        dldBinding.inclVenueCafe.inclOther.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun isValidSelectedVenueType(
        binding: DialogVenueTypeBinding,
        index: String,
    ): Boolean {
        var isValid = true
        val barChecked = binding.inclVenueBar.switchVenueConfirm.isChecked
        val pubChecked = binding.inclVenuePub.switchVenueConfirm.isChecked
        val hotelChecked = binding.inclVenueHotel.switchVenueConfirm.isChecked
        val restaurantChecked = binding.inclVenueRestaurant.switchVenueConfirm.isChecked
        val nightClubChecked = binding.inclVenueNightclub.switchVenueConfirm.isChecked
        val cafeChecked = binding.inclVenueCafe.switchVenueConfirm.isChecked
        val othersChecked = binding.inclVenueOthers.switchVenueConfirm.isChecked



        if (othersChecked && index == Constant().otherConst) {
            if (binding.inclVenueOthers.inclOther.edtText.getText().toString().isNotEmpty()) {
                if (!hasString(resVenueType!!, Constant().otherConst)) {
                    resVenueType!!.add(Constant().otherConst)
                }
                checkVenueType(binding, index)
            } else if (binding.inclVenueOthers.inclOther.root.visibility == View.VISIBLE && binding.inclVenueOthers.inclOther.edtText.text.isEmpty()) {
                isValid = false
                binding.inclVenueOthers.inclOther.tvError.visibility = View.VISIBLE
                binding.inclVenueOthers.inclOther.tvError.text =
                    getString(R.string.txt_err_enter_details)
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = resources.getString(R.string.txt_err_venue_other_type)
                isValid = false
            }
        } else {

            if (barChecked && index == Constant().barConst) {
                // Bar
                if (!binding.inclVenueBar.tegview.selectedList.iterator().hasNext()) {
                    isValid = false
                    binding.inclVenueBar.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenueBar.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)

                } else if (binding.inclVenueBar.inclOther.root.visibility == View.VISIBLE && binding.inclVenueBar.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenueBar.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenueBar.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().barConst)) {
                        resVenueType!!.add(Constant().barConst)
                    }
                    checkVenueType(binding, index)
                }
            } else if (pubChecked && index == Constant().pubConst) {
                //pub
                if (!binding.inclVenuePub.tegview.selectedList.iterator().hasNext()) {
                    isValid = false
                    binding.inclVenuePub.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenuePub.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)
                } else if (binding.inclVenuePub.inclOther.root.visibility == View.VISIBLE && binding.inclVenuePub.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenuePub.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenuePub.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().pubConst)) {
                        resVenueType!!.add(Constant().pubConst)
                    }
                    checkVenueType(binding, index)
                }
            } else if (hotelChecked && index == Constant().hotelConst) {
                //hotel
                if (!binding.inclVenueHotel.tegview.selectedList.iterator().hasNext()) {
                    isValid = false
                    binding.inclVenueHotel.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenueHotel.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)
                } else if (binding.inclVenueHotel.inclOther.root.visibility == View.VISIBLE && binding.inclVenueHotel.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenueHotel.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenueHotel.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().hotelConst)) {
                        resVenueType!!.add(Constant().hotelConst)
                    }
                    checkVenueType(binding, index)
                }
            } else if (restaurantChecked && index == Constant().restaurantConst) {
                //restaurant
                if (!binding.inclVenueRestaurant.tegview.selectedList.iterator().hasNext()) {
                    isValid = false
                    binding.inclVenueRestaurant.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenueRestaurant.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)
                } else if (binding.inclVenueRestaurant.inclOther.root.visibility == View.VISIBLE && binding.inclVenueRestaurant.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenueRestaurant.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenueRestaurant.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().restaurantConst)) {
                        resVenueType!!.add(Constant().restaurantConst)
                    }
                    checkVenueType(binding, index)
                }
            } else if (nightClubChecked && index == Constant().nightClub) {
                //nightclub

                if (!binding.inclVenueNightclub.tegview.selectedList.iterator()
                        .hasNext()
                ) {
                    isValid = false
                    binding.inclVenueNightclub.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenueNightclub.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)
                } else if (binding.inclVenueNightclub.inclOther.root.visibility == View.VISIBLE && binding.inclVenueNightclub.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenueNightclub.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenueNightclub.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().nightClub)) {
                        resVenueType!!.add(Constant().nightClub)
                    }
                    checkVenueType(binding, index)
                }
            } else if (cafeChecked && index == Constant().cafeConst) {
                //cafe
                if (!binding.inclVenueCafe.tegview.selectedList.iterator().hasNext()) {
                    isValid = false
                    binding.inclVenueCafe.tvErrorTags.visibility = View.VISIBLE
                    binding.inclVenueCafe.tvErrorTags.text =
                        getString(R.string.txt_please_set_cateogry)
                } else if (binding.inclVenueCafe.inclOther.root.visibility == View.VISIBLE && binding.inclVenueCafe.inclOther.edtText.text.isEmpty()) {
                    isValid = false
                    binding.inclVenueCafe.inclOther.tvError.visibility = View.VISIBLE
                    binding.inclVenueCafe.inclOther.tvError.text =
                        getString(R.string.txt_err_enter_details)
                } else {
                    if (!hasString(resVenueType!!, Constant().cafeConst)) {
                        resVenueType!!.add(Constant().cafeConst)
                    }
                    checkVenueType(binding, index)
                }
            }


        }


        return isValid
    }

    private fun hasString(list: ArrayList<String>?, index: String): Boolean {
        var isPresent = false
        for (i in list!!.indices) {
            if (list[i] == index) {
                isPresent = true
            }
        }
        return isPresent
    }

    private fun hasVenueCategory(list: ArrayList<VenueTypeCategory>?, index: String): Boolean {
        var isPresent = false
        for (i in list!!.indices) {
            if (list[i].type == index) {
                isPresent = true
            }
        }
        return isPresent
    }

    private fun isValidVenueType(binding: DialogVenueTypeBinding): Boolean {
        var isValid = true

        val barChecked = binding.inclVenueBar.switchVenueConfirm.isChecked
        val pubChecked = binding.inclVenuePub.switchVenueConfirm.isChecked
        val hotelChecked = binding.inclVenueHotel.switchVenueConfirm.isChecked
        val restaurantChecked = binding.inclVenueRestaurant.switchVenueConfirm.isChecked
        val nightClubChecked = binding.inclVenueNightclub.switchVenueConfirm.isChecked
        val cafeChecked = binding.inclVenueCafe.switchVenueConfirm.isChecked
        val othersChecked = binding.inclVenueOthers.switchVenueConfirm.isChecked

        if (!barChecked && !pubChecked && !hotelChecked && !restaurantChecked && !nightClubChecked && !cafeChecked && !othersChecked) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = getString(R.string.txt_err_select_venue_type)
            isValid = false
        }




        return isValid
    }

    private var strMusic = ""
    private var reqMusic = ""
    private var strEntertainment = ""
    private var reqEntertainment = ""
    private var isValidateOther = true

    private fun checkVenueType(binding: DialogVenueTypeBinding, position: String) {
        if (position == Constant().barConst) {
            // Bar
            if (common.checkVenueTypeValidate(
                    binding.inclVenueBar.tegview.selectedList.iterator(),
                    typeBars,
                    binding.inclVenueBar.inclOther.edtText,
                    binding.inclVenueBar.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenueBar.tegview.selectedList.iterator(),
                    typeBars,
                    binding.inclVenueBar.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().pubConst) {
            //pub
            if (common.checkVenueTypeValidate(
                    binding.inclVenuePub.tegview.selectedList.iterator(),
                    typePub,
                    binding.inclVenuePub.inclOther.edtText,
                    binding.inclVenuePub.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenuePub.tegview.selectedList.iterator(),
                    typePub,
                    binding.inclVenuePub.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().hotelConst) {
            //hotel
            if (common.checkVenueTypeValidate(
                    binding.inclVenueHotel.tegview.selectedList
                        .iterator(),
                    typeHotel,
                    binding.inclVenueHotel.inclOther.edtText,
                    binding.inclVenueHotel.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenueHotel.tegview.selectedList.iterator(),
                    typeHotel,
                    binding.inclVenueHotel.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().restaurantConst) {
            //restaurant
            if (common.checkVenueTypeValidate(
                    binding.inclVenueRestaurant.tegview.selectedList
                        .iterator(),
                    typeRestaurant,
                    binding.inclVenueRestaurant.inclOther.edtText,
                    binding.inclVenueRestaurant.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenueRestaurant.tegview.selectedList.iterator(),
                    typeRestaurant,
                    binding.inclVenueRestaurant.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().nightClub) {
            //nightclub
            if (common.checkVenueTypeValidate(
                    binding.inclVenueNightclub.tegview.selectedList
                        .iterator(),
                    typeNightclub,
                    binding.inclVenueNightclub.inclOther.edtText,
                    binding.inclVenueNightclub.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenueNightclub.tegview.selectedList.iterator(),
                    typeNightclub,
                    binding.inclVenueNightclub.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().cafeConst) {
            //nightclub
            if (common.checkVenueTypeValidate(
                    binding.inclVenueCafe.tegview.selectedList.iterator(),
                    typeCafe,
                    binding.inclVenueCafe.inclOther.edtText,
                    binding.inclVenueCafe.inclOther.tvError
                )
            ) {
                setVenueTypeData(
                    binding.inclVenueCafe.tegview.selectedList.iterator(),
                    typeCafe,
                    binding.inclVenueCafe.inclOther.edtText,
                    position
                )
            } else {
                isValidateOther = false
            }
        } else if (position == Constant().otherConst) {
            strOther = binding.inclVenueOthers.inclOther.edtText.text.toString()
            if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[6])) {
                reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[6], strOther!!))
            }
        }
    }

    private var otherBar: String = ""
    private var otherHotel: String = ""
    private var otherPub: String = ""
    private var otherRestaurant: String = ""
    private var otherNightclub: String = ""
    private var otherCafe: String = ""
    private var otherVenueEntertain: String = ""
    private var otherVenueMusic: String = ""

    private fun setVenueTypeData(
        iterator: Iterator<Int>?,
        types: Array<String>,
        edtName: EditText,
        position: String,
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
                if (i == types.size - 1) {
                    // strRequest += CONSTANT.SEPRATEOR_OTHER
                } else {
                    strDisplay = "$strDisplay,"
                    strRequest += CONSTANT.SEPRATEOR
                }
            }
            if (edtName.text.toString().isNotEmpty()) {
                strDisplay = strDisplay + "" + edtName.text.toString()
                strRequest = strRequest + "" + edtName.text.toString()
            }
        }
        strDisplay = strDisplay.replace(Constant().otherConst, "")

        when (position) {
            Constant().barConst -> {
                // Bar
                strDisBar = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[0])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[0], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().barConst) {
                            reqVenueTypeCategory!![i] =
                                VenueTypeCategory(venueCategory[0], strRequest)
                        }
                    }
                }

                if (edtName.text.toString().isNotEmpty()) {
                    otherBar = edtName.text.toString()
                }

            }
            Constant().pubConst -> {
                //pub
                strDisPub = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[1])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[1], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().pubConst) {
                            reqVenueTypeCategory!![i] = VenueTypeCategory(venueCategory[1], strRequest)
                        }
                    }

                }
                if (edtName.text.toString().isNotEmpty()) {
                    otherPub = edtName.text.toString()
                }
            }
            Constant().hotelConst -> {
                //hotel
                strDisHotel = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[2])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[2], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().hotelConst) {
                            reqVenueTypeCategory!![i] =
                                VenueTypeCategory(venueCategory[2], strRequest)
                        }
                    }

                }
                if (edtName.text.toString().isNotEmpty()) {
                    otherHotel = edtName.text.toString()
                }
            }
            Constant().restaurantConst -> {
                //restaurant
                strDisRestaurant = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[3])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[3], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().restaurantConst) {
                            reqVenueTypeCategory!![i] =
                                VenueTypeCategory(venueCategory[3], strRequest)
                        }
                    }

                }
                if (edtName.text.toString().isNotEmpty()) {
                    otherRestaurant = edtName.text.toString()
                }
            }
            Constant().nightClub -> {
                //nightclub
                strDisNightClub = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[4])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[4], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().nightClub) {
                            reqVenueTypeCategory!![i] =
                                VenueTypeCategory(venueCategory[4], strRequest)
                        }
                    }

                }
                if (edtName.text.toString().isNotEmpty()) {
                    otherNightclub = edtName.text.toString()
                }
            }
            Constant().cafeConst -> {
                //cafe
                strCafe = strDisplay
                if (!hasVenueCategory(reqVenueTypeCategory, venueCategory[5])) {
                    reqVenueTypeCategory!!.add(VenueTypeCategory(venueCategory[5], strRequest))
                } else {
                    for (i in 0 until reqVenueTypeCategory!!.size) {
                        if (reqVenueTypeCategory!![i].type == Constant().cafeConst) {
                            reqVenueTypeCategory!![i] =
                                VenueTypeCategory(venueCategory[5], strRequest)
                        }
                    }

                }
                if (edtName.text.toString().isNotEmpty()) {
                    otherCafe = edtName.text.toString()
                }
            }
        } /*else if (position == 502) {
            //nightclub
            strDisVenueTypes = strDisplay
        }*/

        println("setVenueTypeData : resVenueType $resVenueType")
        println("setVenueTypeData : resVenueTypeCategory ${reqVenueTypeCategory.toString()}")
    }

    private fun setEntertainTypeData(
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


            println("setVenueTypeData : strDisplay $strDisplay")
            println("setVenueTypeData : strRequest $strRequest")
        }
        if (position == Constant().music) {
            //music
            strMusic = strDisplay
            reqMusic = strRequest
            binding.inclVenueOtherDetails.inclVenueMusic.edtText.setText(strMusic)

            //eventOtherMusic = edtName.text.toString()
            common.registerVenueModel.music = reqMusic
            common.registerVenueModel.venueMusicName = edtName.text.toString()
        } else if (position == Constant().entertainment) {
            strEntertainment = strDisplay
            reqEntertainment = strRequest
            binding.inclVenueOtherDetails.inclVenueEntertainment.edtText.setText(strEntertainment)
            // eventOtherEntertain = edtName.text.toString()
            common.registerVenueModel.entertainment = reqEntertainment
            common.registerVenueModel.venueEntertainmentName = edtName.text.toString()
        }
    }


    private  fun addView(image: String?, ImageType: String) {
        counter++
        val imageVideoView = ImageVideoView(mActivity, onViewClickListener, ImageType + counter)
        addImages.add(imageVideoView)
        binding.inclVenueBasicInfo.llEventImage.addView(
            addImages[addImages.size - 1],
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
            binding.inclVenueBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclVenueBasicInfo.llEventImage.requestLayout()
        imageVideoView.uploadVideo(image!!)
    }

    private fun addViewMenuImage(image: String?, ImageType: String) {
        counterMenuImage++
        val imageVideoView =
            ImageVideoView(mActivity, onViewClickListener, ImageType + counterMenuImage)
        menuImages.add(imageVideoView)
        binding.inclVenueBasicInfo.llMenuImage.addView(
            menuImages[menuImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        if (menuImages.size > 1) {
            for (i in menuImages.indices) {
                menuImages[i].imgClose!!.visibility = View.VISIBLE
            }
        } else {
            menuImages[0].imgClose!!.visibility = View.INVISIBLE
        }
        if (menuImages.size == 3) {
            binding.inclVenueBasicInfo.imgMenuImage.visibility = View.GONE
        }
        binding.inclVenueBasicInfo.llMenuImage.requestLayout()
        imageVideoView.uploadVideo(image!!)
    }

    private fun displayMenuImage(url: String?) {
        counterMenuImage++
        menuImages.add(
            ImageVideoView(
                mActivity,
                onViewClickListener,
                CONSTANT.MENUIMAGE + counterMenuImage,
                url!!
            )
        )
        binding.inclVenueBasicInfo.llMenuImage.addView(
            menuImages[menuImages.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        for (i in menuImages.indices) {
            menuImages[i].imgClose!!.visibility = View.VISIBLE
        }
        if (menuImages.size == 3) {
            binding.inclVenueBasicInfo.llMenuImage.visibility = View.GONE
        }
        binding.inclVenueBasicInfo.llMenuImage.requestLayout()
    }

    private fun displayImage(url: String?) {
        counter++
        addImages.add(
            ImageVideoView(
                mActivity, onViewClickListener, CONSTANT.IMAGE + counter,
                url!!
            )
        )
        binding.inclVenueBasicInfo.llEventImage.addView(
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
            binding.inclVenueBasicInfo.imgAddImage.visibility = View.GONE
        }
        binding.inclVenueBasicInfo.llEventImage.requestLayout()
    }


    private var onViewClickListener = object : ImageVideoView.OnViewClickListener() {
        override fun onClose(parent: View?, child: View?, Ids: String?) {
            if (parent!!.parent != null) {
                if (Ids!!.contains(CONSTANT.MENUIMAGE)) {
                    binding.inclVenueBasicInfo.llMenuImage.removeView(parent)
                    menuImages.remove(parent)
                    binding.inclVenueBasicInfo.llMenuImage.requestLayout()
                } else {
                    binding.inclVenueBasicInfo.llEventImage.removeView(parent)
                    addImages.remove(parent)
                    binding.inclVenueBasicInfo.llEventImage.requestLayout()
                }
            }
        }

        override fun onImageClick(parent: ImageVideoView?, child: View?, Ids: String?) {
            var urlOne: URL?
            var urlTwo: URL?
            if (!parent!!.imageUrl.toString().lowercase(Locale.getDefault())
                    .contains(Constant().pdf)
            ) {
                return
            }
            for (i in menuImages.indices) {
                try {
                    urlOne = URL(parent.imageUrl)
                    urlTwo = URL(menuImages[i].imageUrl)
                    if (urlOne == urlTwo) {
                        val browserIntent =
                            Intent(getBaseActivity(), WebViewActivity::class.java).putExtra(
                                Constant().path,
                                menuImages[i].imageUrl
                            )
                        startActivity(browserIntent)
                        break
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun imageGetterPopup(isPdfOption: Boolean) {
        val dialog = PopupWindow(requireActivity())

        val binding: ImageGetterDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.image_getter_dialog, null, false
        )

        if (isPdfOption) {
            binding.tvPdf.visibility = View.VISIBLE
        }

        binding.llClose.setOnClickListener { dialog.dismiss() }


        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.tvCamera.setOnClickListener {
           ImagePicker.Companion.with(this@RegisterVenueFragment)
               .crop()
               .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
               .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
           ImagePicker.Companion.with(this@RegisterVenueFragment)
                .crop()
               .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
               .galleryOnly()
                .provider(ImageProvider.GALLERY)
                .start(imagePickerGalleryRequest)
            dialog.dismiss()

        }

        binding.tvPdf.setOnClickListener {
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
            startActivityForResult(
                Intent.createChooser(pickPhoto, Constant().selectPicture),
                requestPdf
            )
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

    private fun isValidateBasicInfo(): Boolean {
        var isValid = true
        binding.inclVenueBasicInfo.inclAge.tvError.visibility = View.GONE

        val venueName = binding.inclVenueBasicInfo.inclVenueName.edtName.text.toString()
        val description = binding.inclVenueBasicInfo.inclDescription.edtText.text.toString()
        binding.inclVenueBasicInfo.tvImageError.visibility = View.GONE


        if (venueName.isEmpty()) {
            binding.inclVenueBasicInfo.inclVenueName.tvError.visibility = View.VISIBLE
            binding.inclVenueBasicInfo.inclVenueName.tvError.text =
                getString(R.string.txt_err_venue_name)
            isValid = false
        }

        if (description.isEmpty()) {
            binding.inclVenueBasicInfo.inclDescription.tvError.visibility = View.VISIBLE
            binding.inclVenueBasicInfo.inclDescription.tvError.text =
                getString(R.string.txt_err_description)
            isValid = false
        }

        if (ageSelected == 0) {
            binding.inclVenueBasicInfo.inclAge.tvError.visibility = View.VISIBLE
            binding.inclVenueBasicInfo.inclAge.tvError.text = getString(R.string.txt_err_age)
            isValid = false
        } else if (ageSelected == ages.size - 1 && binding.inclVenueBasicInfo.venueAgeOther.edtName.getText()
                .toString().isEmpty()
        ) {
            binding.inclVenueBasicInfo.venueAgeOther.tvError.visibility = View.VISIBLE
            binding.inclVenueBasicInfo.venueAgeOther.tvError.text =
                resources.getString(R.string.txt_err_venues_description)
            isValid = false
        }

        if (addImages.size == 0) {
            binding.inclVenueBasicInfo.tvImageError.visibility = View.VISIBLE
            binding.inclVenueBasicInfo.tvImageError.text = resources.getString(R.string.txt_err_venues_upload_image)
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

    private fun isValidateAddress(): Boolean {
        var isValid = true

        val address = binding.inclVenueAddress.inclVenueAddress.edtName.text.toString()
        val city = binding.inclVenueAddress.inclVenueCity.edtName.text.toString()
        val postalCode = binding.inclVenueAddress.inclVenuePincode.edtName.text.toString()

        if (address.isEmpty()) {
            binding.inclVenueAddress.inclVenueAddress.tvError.visibility = View.VISIBLE
            binding.inclVenueAddress.inclVenueAddress.tvError.text =
                resources.getString(R.string.txt_err_venues_address)
            isValid = false
        }

        if (city.isEmpty()) {
            binding.inclVenueAddress.inclVenueCity.tvError.visibility = View.VISIBLE
            binding.inclVenueAddress.inclVenueCity.tvError.text =
                resources.getString(R.string.txt_err_venues_city)
            isValid = false
        }

        /* if (postalCode.isEmpty()) {
             binding.inclVenueAddress.inclVenuePincode.tvError.visibility = View.VISIBLE
             binding.inclVenueAddress.inclVenuePincode.tvError.text =
                 resources.getString(R.string.txt_err_venues_postcode)
             isValid = false
         }*/

        return isValid
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun isValidateOpenClose(): Boolean {
        var isValid = true


        for (i in timingModels.indices) {
            if (!timingModels[i].isClear && (timingModels[i].open_time!!.isEmpty() || timingModels[i].close_time!!.isEmpty())) {
                isValid = false
                daysAdapter!!.notifyDataSetChanged()
                break
            }
        }

        var isTimeSelected = false
        for (timingModel in timingModels) {
            if (timingModel.open_time!!.isNotEmpty() || timingModel.close_time!!.isNotEmpty()) {
                isTimeSelected = true
            }
        }
        if (!isTimeSelected) {
            isValid = false
            common.openErrorDialog(requireActivity(),
                childFragmentManager,
                null,
                "",
                getString(R.string.txt_plese_select_opening_hours))
        }

        return isValid
    }


    private var selectedImagePath: String? = null
    private var selectedPdfFile = ""



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                imagePickerCameraRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    if (imageTypes.equals(CONSTANT.IMAGE, ignoreCase = true)) {
                        addView(selectedImagePath, CONSTANT.IMAGE)
                    } else {
                        addViewMenuImage(selectedImagePath, CONSTANT.MENUIMAGE)
                    }
                }
                imagePickerGalleryRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    if (imageTypes.equals(CONSTANT.IMAGE, ignoreCase = true)) {
                        addView(selectedImagePath, CONSTANT.IMAGE)
                    } else {
                        addViewMenuImage(selectedImagePath, CONSTANT.MENUIMAGE)
                    }
                }
                requestPdf -> {
                    try {
                        val file: File =
                            AttachmentHelper.getFileFromUri(requireActivity(), data!!.data!!)

                        selectedPdfFile = file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (common.getFileSize(mActivity!!.baseContext,data!!.data!!) <= 5 * 1024 * 1024) { // 5MB in bytes
                        addViewMenuImage(selectedPdfFile, CONSTANT.MENUIMAGE)
                    } else {
                        selectedPdfFile=""
                        Toast.makeText(mActivity, "Image size exceeds 5MB", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }




    private fun setUiWithExpandCollapse() {
        binding.inclBasicInfo.root.setOnClickListener {
            handleUiWithDropDown(binding.inclVenueBasicInfo.root, binding.inclBasicInfo.chkDropDown)
        }

        binding.inclAddress.root.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else {
                handleUiWithDropDown(binding.inclVenueAddress.root, binding.inclAddress.chkDropDown)
            }

        }

        binding.inclOpenCloseHours.root.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else if (!common.registerVenueModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_section))
            } else {
                handleUiWithDropDown(binding.inclVenueOpenClose.root,
                    binding.inclOpenCloseHours.chkDropDown)
            }

        }

        binding.inclOtherDetails.root.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else if (!common.registerVenueModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_section))
            } else if (!common.registerVenueModel.isOpenCloseVerified) {
                showToast(getString(R.string.txt_please_add_opening_hours))
            } else {
                handleUiWithDropDown(binding.inclVenueOtherDetails.root,
                    binding.inclOtherDetails.chkDropDown)
            }
        }

        binding.inclBasicInfo.chkDropDown.setOnClickListener {
            handleUiWithDropDown(binding.inclVenueBasicInfo.root, binding.inclBasicInfo.chkDropDown)
        }

        binding.inclAddress.chkDropDown.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else {
                handleUiWithDropDown(binding.inclVenueAddress.root, binding.inclAddress.chkDropDown)
            }

        }

        binding.inclOpenCloseHours.chkDropDown.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else if (!common.registerVenueModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_section))
            } else {
                handleUiWithDropDown(binding.inclVenueOpenClose.root,
                    binding.inclOpenCloseHours.chkDropDown)
            }

        }

        binding.inclOtherDetails.chkDropDown.setOnClickListener {
            if (!common.registerVenueModel.isBasicInfoVerified) {
                showToast(getString(R.string.txt_event_please_add_basic_info_section))
            } else if (!common.registerVenueModel.isAddressVerfied) {
                showToast(getString(R.string.txt_event_address_section))
            } else {
                handleUiWithDropDown(binding.inclVenueOtherDetails.root,
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


    private var timeChangeListener: DayTimeChangeListener<TimingModel?> =
        object : DayTimeChangeListener<TimingModel?>() {
            override fun onAddOpenTime(daysModel: TimingModel, position: Int) {
                super.onAddOpenTime(daysModel, position)

                if (daysModel.open_time == null) {
                    hour = null
                    minute = null
                    amPm = null
                } else {
                    hour =
                        common.convertDateInFormat(
                            daysModel.open_time,
                            Constant().hhMmA,
                            Constant().hh
                        )
                    minute =
                        common.convertDateInFormat(
                            daysModel.open_time,
                            Constant().hhMmA,
                            Constant().mm
                        )
                    amPm =
                        common.convertDateInFormat(
                            daysModel.open_time,
                            Constant().hhMmA,
                            Constant().a
                        )
                }

                openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                        super.onSelectedTime(hours, minutes, ampms)
                        timingModels[position].open_time = "$hours:$minutes$ampms"
                        timingModels[position].isClear = false
                        binding.inclVenueOpenClose.tvDaysError.visibility = View.GONE
                        daysAdapter!!.notifyDataSetChanged()
                    }
                })
            }

            override fun onAddCloseTime(daysModel: TimingModel, position: Int) {
                super.onAddCloseTime(daysModel, position)
                if (daysModel.close_time == null) {
                    hour = null
                    minute = null
                    amPm = null
                } else {
                    hour =
                        common.convertDateInFormat(
                            daysModel.close_time,
                            Constant().hhMmA,
                            Constant().hh
                        )
                    minute =
                        common.convertDateInFormat(
                            daysModel.close_time,
                            Constant().hhMmA,
                            Constant().mm
                        )
                    amPm =
                        common.convertDateInFormat(
                            daysModel.close_time,
                            Constant().hhMmA,
                            Constant().a
                        )
                }

                openTimePickerDialog(hour, minute, amPm, object : DialogListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onSelectedTime(hours: String, minutes: String, ampms: String) {
                        super.onSelectedTime(hours, minutes, ampms)
                        timingModels[position].close_time = "$hours:$minutes$ampms"
                        timingModels[position].isClear = false
                        binding.inclVenueOpenClose.tvDaysError.visibility = View.GONE
                        daysAdapter!!.notifyDataSetChanged()
                    }
                })
            }

            override fun onClearTime(isClear: Boolean, position: Int) {
                super.onClearTime(isClear, position)
                timingModels[position].isClear = true
                timingModels[position].open_time = ""
                timingModels[position].close_time = ""
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


    var offerString = ""

    @SuppressLint("NotifyDataSetChanged")
    private fun setData() {
        if (venueModel != null) {

            if (isEditVenue) {
                binding.btnRegisterVenue.alpha = 1f
                binding.btnRegisterVenue.isEnabled = true
            }

            binding.inclVenueBasicInfo.inclVenueName.edtName.setText(common.registerVenueModel.venueName)
            for (i in ages.indices) {
                if (common.registerVenueModel.age == ages[i]) {
                    binding.inclVenueBasicInfo.inclAge.spinner.setSelection(i, true)
                    break
                }
            }
            binding.inclVenueBasicInfo.inclDescription.edtText.setText(common.registerVenueModel.description)


            if (common.registerVenueModel.isBasicInfoVerified) {
                binding.inclBasicInfo.uiVerified.visibility = View.VISIBLE
            }

            if (common.registerVenueModel.isAddressVerfied) {
                binding.inclAddress.uiVerified.visibility = View.VISIBLE
            }

            if (common.registerVenueModel.isOpenCloseVerified) {
                binding.inclOpenCloseHours.uiVerified.visibility = View.VISIBLE
            }

            if (common.registerVenueModel.isOtherInfoVerified) {
                binding.inclOtherDetails.uiVerified.visibility = View.VISIBLE
            }

            reservationEnabled = if (common.registerVenueModel.reservationEnabled) 1 else 0
            outDoorEnabled = if (common.registerVenueModel.outDoorEnabled) 1 else 0
            freeWifi = if (common.registerVenueModel.free_wifi) 1 else 0

            if (common.registerVenueModel.reservationEnabled) {
                binding.inclVenueOtherDetails.inclReservation.edtText.text =
                    getString(R.string.txt_yes)
            } else {
                binding.inclVenueOtherDetails.inclReservation.edtText.text =
                    getString(R.string.txt_no)
            }

            if (common.registerVenueModel.outDoorEnabled) {
                binding.inclVenueOtherDetails.inclOutdoorArea.edtText.text =
                    getString(R.string.txt_yes)
            } else {
                binding.inclVenueOtherDetails.inclOutdoorArea.edtText.text =
                    getString(R.string.txt_no)
            }

            if (common.registerVenueModel.free_wifi) {
                binding.inclVenueOtherDetails.inclFreeWifi.edtText.text =
                    getString(R.string.txt_yes)
            } else {
                binding.inclVenueOtherDetails.inclFreeWifi.edtText.text = getString(R.string.txt_no)
            }

            if (common.registerVenueModel.venueImages != null && common.registerVenueModel.venueImages!!.isNotEmpty()) {

                binding.inclVenueBasicInfo.llEventImage.removeAllViews()
                addImages.clear()
                binding.inclVenueBasicInfo.llEventImage.requestLayout()

                for (i in 0 until common.registerVenueModel.venueImages!!.size) {
                    displayImage(common.registerVenueModel.venueImages!![i])
                }
            }

            if (common.registerVenueModel.venueMenuImages != null && common.registerVenueModel.venueMenuImages!!.isNotEmpty()) {
                for (i in 0 until common.registerVenueModel.venueMenuImages!!.size) {
                    displayMenuImage(common.registerVenueModel.venueMenuImages!![i])
                }
            }

            if (venueModel!!.venue_music != null) {
                binding.inclVenueOtherDetails.inclVenueMusic.edtText.text =
                    venueModel!!.venue_music!!.replace(
                        CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", "
                    ).replace(CONSTANT.SEPRATEOR, ", ")
            }

            if (venueModel!!.entertainment != null) {
                binding.inclVenueOtherDetails.inclVenueEntertainment.edtText.text =
                    venueModel!!.entertainment!!.replace(
                        CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", "
                    ).replace(CONSTANT.SEPRATEOR, ", ")
            }



            for (i in 0 until venueModel!!.timings!!.size) {
                for (j in i until timingModels.size) {
                    if (venueModel!!.timings!![i].working_day.equals(
                            timingModels[j].day,
                            ignoreCase = true
                        )
                    ) {
                        timingModels[j].open_time = common.convertDateInFormat(
                            venueModel!!.timings!![i].open_time,
                            Constant().HHmmss24hrs,
                            Constant().hhMmA
                        )
                        timingModels[j].close_time = common.convertDateInFormat(
                            venueModel!!.timings!![i].close_time,
                            Constant().HHmmss24hrs,
                            Constant().hhMmA
                        )
                        if (venueModel!!.timings!![i].open_time!!.isNotEmpty() && venueModel!!.timings!![i].close_time!!.isNotEmpty()
                        ) {
                            timingModels[j].isClear = false
                        }

                        daysAdapter!!.notifyDataSetChanged()
                    }
                }
            }


            var venueTypeString = ""
            for (i in 0 until venueModel!!.venuetypes!!.size) {
                venueTypeString += if (i == venueModel!!.venuetypes!!.size - 1) {
                    venueModel!!.venuetypes!![i].venue_type
                } else {
                    venueModel!!.venuetypes!![i].venue_type + ", "
                }
            }

            binding.inclVenueOtherDetails.inclVenueType.edtText.setText(venueTypeString)


            if (venueModel!!.offers!!.size > 0) {
                for (i in 0 until venueModel!!.offers!!.size) {
                    offerString = if (venueModel!!.offers!!.size > 1) {
                        venueModel!!.offers!![i].title + " + " + i + " Other"
                    } else {
                        venueModel!!.offers!![i].title!!
                    }
                }
            }

            binding.inclVenueOtherDetails.inclVenueOffer.edtText.setText(offerString.replace("null", ""))

            binding.inclVenueAddress.inclVenueAddress.edtName.text =
                common.registerVenueModel.address
            binding.inclVenueAddress.inclVenueCity.edtName.text = common.registerVenueModel.city
            binding.inclVenueAddress.inclVenuePincode.edtName.text =
                common.registerVenueModel.postal_code
            binding.inclVenueOtherDetails.inclDressCode.edtText.text =
                common.registerVenueModel.dress_code
            binding.inclVenueOtherDetails.inclEntryPolicy.edtText.text =
                common.registerVenueModel.entry_policy
            binding.inclVenueOtherDetails.edtLastEntry.text =
                common.registerVenueModel.last_entry
            binding.inclVenueOtherDetails.inclVenueOther.edtText.text =
                common.registerVenueModel.other_info

        }
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

}
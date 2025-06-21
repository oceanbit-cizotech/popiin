package com.popiin.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.annotation.Languages
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.ActivityEditProfileBinding
import com.popiin.databinding.ImagePickerDialogBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.InfoDialogListener
import com.popiin.res.CommonRes
import com.popiin.res.LoginRes
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.popiin.SplashActivity
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(),
    CompoundButton.OnCheckedChangeListener {
    private var selectedImagePath: String? = ""
    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var permissionAll = 1
    private var imagePickerCameraRequest = 101
    private var imagePickerGalleryRequest = 102
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var genderName: String
    private var genderId: Int = 0
    override fun getLayout(): Int {
        return R.layout.activity_edit_profile
    }

    override fun initViews() {
        if (!hasPermissions(mActivity, permissions)) {
            ActivityCompat.requestPermissions(mActivity, permissions, permissionAll)
        }
        binding.topHeader.ivBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.ivCamera.setOnClickListener {
            openImagePopup()
        }

        binding.btnUpdateChanges.setOnClickListener {
            if (isValidate()) {
                callUpdateProfileApi()
            }
        }

        binding.btnDeleteAccount.setOnClickListener {
            showCommonInfoBottomFragment(
                ContextCompat.getDrawable(mActivity, R.drawable.ic_sure_delete),
                getString(R.string.txt_sure_to_delete),
                getString(R.string.txt_you_want_to_delete_account),
                getString(R.string.txt_no),
                getString(R.string.txt_yes_delete),
                infoDialogListener
            )
        }

        val gender = resources.getStringArray(R.array.Gender)
        val genderStringArray = resources.getStringArray(R.array.GenderValues)
        val genderIntArray = genderStringArray.map { it.toInt() }.toIntArray()
        val adapter = ArrayAdapter(this, R.layout.spin_gender, gender)
        binding.inclGender.spinGender.adapter = adapter

        binding.inclGender.spinGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                genderId =genderIntArray[binding.inclGender.spinGender.selectedItemPosition]
                genderName = gender[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.inclGender.spinGender.setSelection(getGenderPosition(PrefManager.getUser()!!.user!!.gender.toString()))


        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        binding.edtConfPassword.transformationMethod = PasswordTransformationMethod()

        binding.chkPassword.setOnCheckedChangeListener(this)
        binding.chkConfirmPsw.setOnCheckedChangeListener(this)

        setUserDetails()

        common.handleErrorView(binding.inclFirstName.edtName, binding.tvMsgFirstname)
        common.handleErrorView(binding.inclLastName.edtName, binding.tvMsgLastname)
        common.handleErrorView(binding.inclPostalCode.edtName, binding.tvMsgPostalCode)
        common.handleErrorView(binding.inclAddress.edtText, binding.tvMsgAddress)
    }
    fun getGenderPosition( value: String): Int {
        val genderArray = resources.getStringArray(R.array.GenderValues)
        return genderArray.indexOf(value)
    }
    private fun showCommonInfoBottomFragment(
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
                ContextCompat.getColor(mActivity, R.color.colorHeaderText),
                ContextCompat.getColor(mActivity, R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        mActivity.supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
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
            callDeleteAccountApi()
        }

    }


    private fun isValidate(): Boolean {
        var isValid = true

        firstName = binding.inclFirstName.edtName.text.toString().trim()
        lastName = binding.inclLastName.edtName.text.toString().trim()

        if (firstName.isEmpty()) {
            binding.tvMsgFirstname.visibility = View.VISIBLE
            binding.tvMsgFirstname.text = getString(R.string.txt_err_enter_first_name)
            isValid = false
        }

        if (lastName.isEmpty()) {
            binding.tvMsgLastname.visibility = View.VISIBLE
            binding.tvMsgLastname.text = getString(R.string.txt_err_enter_last_name)
            isValid = false
        }

        if (binding.inclEmail.edtName.text.toString().trim().isEmpty()) {
            binding.inclEmail.tvError.visibility = View.VISIBLE
            binding.inclEmail.tvError.text = getString(R.string.txt_err_enter_email)
            isValid = false
        }

        if (binding.edtConfPassword.text.toString().trim().isNotEmpty() && binding.edtConfPassword.text.toString().trim() != binding.edtPassword.text.toString().trim()) {
            binding.tvMsgConfPassword.visibility = View.VISIBLE
            binding.tvMsgConfPassword.text = getString(R.string.txt_err_match_conf_password)
            isValid = false
        }

        return isValid
    }

    private fun openImagePopup() {
        val dialog = PopupWindow(mActivity)
        val binding: ImagePickerDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.image_picker_dialog, null, false
        )

        binding.tvCamera.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .galleryOnly()
                .provider(ImageProvider.GALLERY)
                .start(imagePickerGalleryRequest)
            dialog.dismiss()
        }

        binding.tvCancel.setOnClickListener {
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

    private fun setUserDetails() {
        binding.inclFirstName.edtName.text = PrefManager.getUser().user?.firstName!!
        binding.inclLastName.edtName.text = PrefManager.getUser().user?.lastName!!

        binding.inclEmail.edtName.text = PrefManager.getUser().user?.email!!
      //  binding.edtPassword.text = PrefManager.getPassword()!!
        binding.inclDob.edtText.text =
            common.convertDateInFormat(
                PrefManager.getUser().user?.dob,
                Constant().yyyyMmDd,
                Constant().ddMmmYyyySlash
            )!!

        common.loadImageFromUrl(binding.civProfile, PrefManager.getUser().user?.profilePic)

        val addresses: List<Address>?
        val geocoder = Geocoder(mActivity, Locale.getDefault())
        try {
            @Suppress("DEPRECATION")
            addresses = geocoder.getFromLocation(
                PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val state: String = addresses!![0].adminArea
            val country: String = addresses[0].countryName
            val postalCode: String = addresses[0].postalCode
            binding.inclAddress.edtText.text = "$state, $country"
            binding.inclPostalCode.edtName.text = postalCode
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun callUpdateProfileApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<LoginRes>
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            builder.addFormDataPart(Constant().firstName, firstName)
            builder.addFormDataPart(Constant().lastName, lastName)
            builder.addFormDataPart(
                Constant().email,
                "" + binding.inclEmail.edtName.text.toString()
            )
            builder.addFormDataPart(Constant().gender, "" + genderId)
            if (binding.inclDob.edtText.text.toString().isNotEmpty()) {
                builder.addFormDataPart(
                    Constant().dob,
                    common.convertDateInFormat(
                        binding.inclDob.edtText.text.toString(),
                        Constant().ddMmmYyyySlash,
                        Constant().yyyyMmDd
                    )!!
                )
            }
            builder.addFormDataPart(Constant().password, binding.edtPassword.getText().toString())
            builder.addFormDataPart(Constant().lang, Languages.ENGLISH)
            if (selectedImagePath!!.isNotEmpty()) {
                val profilePicture = File(selectedImagePath!!)
                builder.addFormDataPart(
                    Constant().avatar,
                    profilePicture.name,
                    profilePicture.asRequestBody(Constant().multipartFormData.toMediaTypeOrNull())
                )
            }
            call = apiInterface.doUpdateProfile(PrefManager.getAccessToken(), builder.build())
            call!!.enqueue(object : Callback<LoginRes> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            PrefManager.setUser(response.body()!!)
                            PrefManager.setPassword(binding.edtPassword.getText().toString())
                            val commonDialog = CommonDialog(
                                mActivity,
                                getString(R.string.lbl_ok),
                                "",
                                "",
                                response.body()!!.message!!
                            )
                            commonDialog.binding.btnPositive.setOnClickListener {
                                commonDialog.dismiss()
                                finish()
                            }
                            commonDialog.show()
                        } else {
                            common.openDialog(mActivity, response.body()!!.message)
                        }
                    }
                }

                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun fetchDataWithCallback(file: File, callback: (File) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = compressImage(file)
            callback(result)
        }
    }

    private suspend fun compressImage(file: File):File{
        return Compressor.compress(getBaseActivity()!!.baseContext, file) {
            quality(15)
            format(Bitmap.CompressFormat.JPEG) // Specify the format
            size(2_097_152) // Set maximum size in bytes (2 MB in this example)
        }
    }


    private fun hasPermissions(context: Context, vararg permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission.toString()
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == imagePickerCameraRequest) {
                selectedImagePath = data!!.data!!.path
                common.loadImageFromUrl(binding.civProfile, selectedImagePath)
                val profilePicture = File(selectedImagePath!!)
                fetchDataWithCallback(profilePicture){ result ->
                    selectedImagePath=result.path
                }
            } else if (requestCode == imagePickerGalleryRequest) {
                selectedImagePath = PathUtil().getPath(mActivity, data!!.data!!)
                common.loadImageFromUrl(binding.civProfile, selectedImagePath)
                val profilePicture = File(selectedImagePath!!)
                fetchDataWithCallback(profilePicture){ result ->
                    selectedImagePath=result.path
                }
            }
        }

    }



    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (binding.chkPassword.isChecked) {
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod()
        } else {
            binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        }

        if (binding.edtPassword.getText().toString().isNotEmpty()) {
            binding.edtPassword.setSelection(binding.edtPassword.getText().toString().length)
        }

        if (binding.chkConfirmPsw.isChecked) {
            binding.edtConfPassword.transformationMethod = HideReturnsTransformationMethod()
        } else {
            binding.edtConfPassword.transformationMethod = PasswordTransformationMethod()
        }

        if (binding.edtConfPassword.getText().toString().isNotEmpty()) {
            binding.edtConfPassword.setSelection(
                binding.edtConfPassword.getText().toString().length
            )
        }
    }

    private fun callDeleteAccountApi() {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doDeleteAccount(PrefManager.getAccessToken())
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            PrefManager.setAccessToken("")
                            startActivity(Intent(mActivity, SplashActivity::class.java))
                            finish()
                        } else {
                            common.openDialog(mActivity, response.body()!!.msg)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }


}
package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.MyVenueFragment
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentVenueVerificationBinding
import com.popiin.databinding.ImageGetterDialogBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.InfoDialogListener
import com.popiin.res.SubmitDocRes
import com.popiin.res.VenueListRes
import com.popiin.util.AttachmentHelper
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Locale

class VenueVerificationFragment : BaseFragment<FragmentVenueVerificationBinding>() {
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var companyName = ""
    private var address = ""
    private var city = ""
    private var postCode = ""
    private val requestPdf = 101
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private var selectedPersonalDocPath: String = ""
    private var selectedVenueDocPath: String = ""
    private var selectedPdfFile = ""
    private var docType = 0

    companion object {
        var venueModel: VenueListRes.Venue? = null
        var isEditMode = false
        fun newInstance(item: VenueListRes.Venue?, editMode: Boolean): VenueVerificationFragment {
            venueModel = item
            isEditMode = editMode
            return VenueVerificationFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_venue_verification
    }

    override fun initViews() {
        if (isEditMode) {
            setVenueData(venueModel)
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.tvSave.setOnClickListener {
            if (isValidData()) {
                callSubmitDocumentApi()
            }else{
                val commonDialog = CommonDialog(
                    requireActivity(),
                    getString(R.string.lbl_ok), "", "", getString(R.string.msg_please_agree)
                )
                commonDialog.show()
                commonDialog.binding.btnPositive.setOnClickListener {
                    commonDialog.dismiss()
                }
            }
        }

        binding.tvPersonalDocLink.setOnClickListener {
            docType = 1
            imageGetterPopup(true)
        }

        binding.tvVenueDocLink.setOnClickListener {
            docType = 2
            imageGetterPopup(true)
        }

        binding.inclAddress.edtText.text = venueModel!!.venue_address!!
        binding.edtCity.text = venueModel!!.venue_city!!
        binding.edtPostcode.text = venueModel!!.venue_postal_code!!

        common.handleErrorView(binding.edtFirstName, binding.tvErrorFirstName)
        common.handleErrorView(binding.edtLastName, binding.tvErrorLastName)
        common.handleErrorView(binding.edtEmail, binding.tvErrorEmail)
        common.handleErrorView(binding.edtPhone, binding.tvErrorPhone)
        common.handleErrorView(binding.edtCompanyName, binding.tvErrorCompanyName)
        common.handleErrorView(binding.inclAddress.edtText, binding.inclAddress.tvError)
        common.handleErrorView(binding.inclComments.edtText, binding.inclComments.tvError)
        common.handleErrorView(binding.edtCity, binding.tvErrorCity)
        common.handleErrorView(binding.edtPostcode, binding.tvErrorPostcode)
    }


    private fun setVenueData(venueModel: VenueListRes.Venue?) {
        binding.edtFirstName.text = venueModel!!.document!!.firstName!!
        binding.edtLastName.text = venueModel.document!!.lastName!!
        binding.edtEmail.text = venueModel.document.email!!.trim()
        binding.edtCompanyName.text = venueModel.document.companyName!!
        binding.edtCity.text = venueModel.document.city!!
        binding.edtPostcode.text = venueModel.document.postcode.toString()
        binding.inclComments.edtText.text = venueModel.document.comments ?: ""
        binding.inclAddress.edtText.text = venueModel.document.address!!
        setImageData(venueModel.document.personalDoc, venueModel.document.venueDoc)

    }

    private fun setImageData(personalDoc: String?, venueDoc: String?) {
        if (personalDoc!!.contains("pdf")) {
            Glide.with(requireActivity())
                .load(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pdf))
                .into(binding.ivPresonalDoc)
        } else {
            Common.instance!!.loadImageFromUrl(binding.ivPresonalDoc, personalDoc)
        }

        if (venueDoc!!.contains("pdf")) {
            Glide.with(requireActivity())
                .load(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pdf))
                .into(binding.ivVenueDoc)
        } else {
            Common.instance!!.loadImageFromUrl(binding.ivVenueDoc, venueDoc)
        }


    }

    private fun callSubmitDocumentApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<SubmitDocRes?>?
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)

            builder.addFormDataPart(Constant().venueId, "" + venueModel!!.id)
            builder.addFormDataPart(
                Constant().comments,
                binding.inclComments.edtText.text.toString()
            )
            builder.addFormDataPart(Constant().firstName, firstName)
            builder.addFormDataPart(Constant().lastName, lastName)
            builder.addFormDataPart(Constant().email, email)
            builder.addFormDataPart(Constant().companyName, companyName)
            builder.addFormDataPart(Constant().city, city)
            builder.addFormDataPart(Constant().addressVerify, address)
            builder.addFormDataPart(Constant().postcode, postCode)



            if (selectedPersonalDocPath.isNotEmpty()) {
                val profilePicture = File(selectedPersonalDocPath)
                builder.addFormDataPart(
                    Constant().personalDoc,
                    profilePicture.name,
                    profilePicture.asRequestBody(Constant().multipartFormData.toMediaTypeOrNull())
                )
            }

            if ( selectedVenueDocPath.isNotEmpty()) {
                val profilePicture = File(selectedVenueDocPath)
                builder.addFormDataPart(
                    Constant().venueDoc,
                    profilePicture.name,
                    profilePicture.asRequestBody(Constant().multipartFormData.toMediaTypeOrNull())
                )
            }

            call = apiInterface.submitDocument(PrefManager.getAccessToken(), builder.build())
            call!!.enqueue(object : Callback<SubmitDocRes?> {
                override fun onResponse(
                    call: Call<SubmitDocRes?>,
                    response: Response<SubmitDocRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        openDialog(
                            requireActivity(), childFragmentManager,
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.ic_pass_success
                            ),
                            getString(R.string.txt_success), response.body()!!.message,
                            getString(R.string.txt_done)
                        )
                    } else {
                        openDialog(
                            requireActivity(), childFragmentManager,
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_failed),
                            getString(R.string.txt_failed), response.body()!!.message,
                            getString(R.string.txt_ok)
                        )
                    }

                }

                override fun onFailure(call: Call<SubmitDocRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }

    private fun isValidData(): Boolean {
        var isValid = true
        firstName = binding.edtFirstName.text.toString()
        lastName = binding.edtLastName.text.toString()
        email = binding.edtEmail.text.toString()
        companyName = binding.edtCompanyName.text.toString()
        address = binding.inclAddress.edtText.text.toString()
        city = binding.edtCity.text.toString()
        postCode = binding.edtPostcode.text.toString()

        if (firstName.isEmpty()) {
            isValid = false
            binding.tvErrorFirstName.visibility = View.VISIBLE
            binding.tvErrorFirstName.text = getString(R.string.txt_err_enter_first_name)
        }

        if (lastName.isEmpty()) {
            isValid = false
            binding.tvErrorLastName.visibility = View.VISIBLE
            binding.tvErrorLastName.text = getString(R.string.txt_err_enter_last_name)
        }

        if (email.isEmpty()) {
            isValid = false
            binding.tvErrorEmail.visibility = View.VISIBLE
            binding.tvErrorEmail.text = getString(R.string.txt_err_venue_verify_email)
        }

        if (!isValidEmailId(email)) {
            isValid = false
            binding.tvErrorEmail.visibility = View.VISIBLE
            binding.tvErrorEmail.text = getString(R.string.txt_err_venue_verify_email)
        }

        if (binding.edtPhone.text.toString().isEmpty()) {
            isValid = false
            binding.tvErrorPhone.visibility = View.VISIBLE
            binding.tvErrorPhone.text = getString(R.string.txt_err_phone_number)
        }

      /*  if (binding.edtPhone.text.toString().length<6) {
            isValid = false
            binding.tvErrorPhone.visibility = View.VISIBLE
            binding.tvErrorPhone.text = getString(R.string.txt_err_phone_number)
        }*/

        if (companyName.isEmpty()) {
            isValid = false
            binding.tvErrorCompanyName.visibility = View.VISIBLE
            binding.tvErrorCompanyName.text = getString(R.string.txt_err_enter_company_name)
        }

        if (address.isEmpty()) {
            isValid = false
            binding.inclAddress.tvError.visibility = View.VISIBLE
            binding.inclAddress.tvError.text = getString(R.string.txt_err_enter_address)
        }

        if (city.isEmpty()) {
            isValid = false
            binding.tvErrorCity.visibility = View.VISIBLE
            binding.tvErrorCity.text = getString(R.string.apm_error_city)
        }

        if (postCode.isEmpty()) {
            isValid = false
            binding.tvErrorPostcode.visibility = View.VISIBLE
            binding.tvErrorPostcode.text = getString(R.string.txt_err_venues_postcode)
        }

        if (venueModel == null && selectedPersonalDocPath.isEmpty()) {
            isValid = false
            binding.tvErrorPersonalDoc.visibility = View.VISIBLE
            binding.tvErrorPersonalDoc.text =
                getString(R.string.txt_please_upload_personal_document)
        }

        if (venueModel == null && selectedVenueDocPath.isEmpty()) {
            isValid = false
            binding.tvErrorVenueDoc.visibility = View.VISIBLE
            binding.tvErrorVenueDoc.text = getString(R.string.txt_please_upload_venue_document)
        }

        if(!binding.chkTerms.isChecked){
            isValid = false
        }
        return isValid
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
            ImagePicker.Companion.with(this@VenueVerificationFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            ImagePicker.Companion.with(this@VenueVerificationFragment)
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

    private var selectedImagePath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                imagePickerCameraRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    addViewMenuImage(selectedImagePath)
                }

                imagePickerGalleryRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    addViewMenuImage(selectedImagePath)
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
                        addViewMenuImage(selectedPdfFile)
                    } else {
                        selectedPdfFile=""
                        Toast.makeText(mActivity, "Image size exceeds 5MB", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    private fun setImageWithView(ivDoc: ImageView, selectedImagePath: String?) {
        if (selectedImagePath!!.lowercase(Locale.getDefault()).contains("pdf")) {
            Glide.with(this).load(R.drawable.ic_pdf).into(ivDoc)
        } else {
            Glide.with(this).load(selectedImagePath).into(ivDoc)
        }
    }

    private fun addViewMenuImage(selectedImagePath: String?) {
        if (docType == 1) {
            selectedPersonalDocPath = selectedImagePath!!
            binding.tvErrorPersonalDoc.visibility = View.GONE
            setImageWithView(binding.ivPresonalDoc, selectedPersonalDocPath)
        } else if (docType == 2) {
            selectedVenueDocPath = selectedImagePath!!
            binding.tvErrorVenueDoc.visibility = View.GONE
            setImageWithView(binding.ivVenueDoc, selectedVenueDocPath)
        }

    }

    fun openDialog(
        context: Context,
        manager: FragmentManager,
        image: Drawable?,
        title: String,
        desc: String,
        positiveText: String,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                title, desc, "", positiveText,
                ContextCompat.getColor(context, R.color.colorBlack),
                ContextCompat.getColor(context, R.color.colorBlack), true, infoDialogListener
            )
        commonInfoBottomFragment.isCancelable = false
        commonInfoBottomFragment.show(manager, "")
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
            mActivity!!.supportFragmentManager.popBackStack()
        }

    }

}
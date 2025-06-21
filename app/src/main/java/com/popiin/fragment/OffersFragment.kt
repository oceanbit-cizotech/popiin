package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.WebViewActivity
import com.popiin.adapter.AvailOfferAdapter
import com.popiin.adapter.OfferVenueAdapter
import com.popiin.adapter.VenueSavedOfferAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.*
import com.popiin.dialog.CommonDialog
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.listener.AdapterOfferListener
import com.popiin.model.AvailOfferModel
import com.popiin.req.EventReq
import com.popiin.req.VenueDetailsReq
import com.popiin.req.VenueOfferUpdateSubReq
import com.popiin.req.VenueofferDeleteReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueListRes.Offerslive
import com.popiin.res.VenueOfferRes
import com.popiin.util.AttachmentHelper
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.popiin.views.ImageVideoView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class OffersFragment : BaseFragment<FragmentOffersBinding>() {
    private var pageNo = defaultPage
    private var venues = ArrayList<VenueListRes.Venue>()
    private var offers: ArrayList<Offerslive> = ArrayList()
    lateinit var venueSavedOfferAdapter: VenueSavedOfferAdapter
    private var offerList = ArrayList<AvailOfferModel>()
    lateinit var venueOfferBinding: DialogChooseVenueOfferBinding
    private var imageTypes = ""
    private var venueId = ""
    private var selectedVenues: VenueListRes.Venue? = null
    lateinit var venueOfferAdapter: OfferVenueAdapter
    private lateinit var availOfferAdapter: AvailOfferAdapter

    //private val requestImage = 100
    private val requestPdf = 101
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private var counterMenuImage = -1
    private val images = ArrayList<ImageVideoView>()
    private var isLoading = true
    private var strSearch = ""
    private var selectedIndex = 0
    private var selectedOfferIndex = -1
    override fun getLayout(): Int {
        return R.layout.fragment_offers
    }

    companion object {
        fun newInstance(): OffersFragment {
            return OffersFragment()
        }
    }

    override fun initViews() {

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnPreview.setOnClickListener {
            hideKeyBoard(requireActivity())
            ("" + venuesOfferRes!!.data!!.id).callVenueOfferUpdateSubs()
        }

        venueOfferBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_choose_venue_offer, null, false)

        venueOfferAdapter = OfferVenueAdapter(venues,listener,selectedIndex)

        venueSavedOfferAdapter = VenueSavedOfferAdapter(offers,savedOfferListener)
        binding.rvSavedOffers.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvSavedOffers.adapter = venueSavedOfferAdapter


        binding.inclChooseVenue.edtText.setOnClickListener {
            // open venue list dialog
            openVenueOfferDialog()
        }


        binding.inclSelectOffer.edtText.setOnClickListener {
            // open venue list dialog
            openOffersDialog()
        }

        binding.imgAddImage.setOnClickListener {
            imageTypes = CONSTANT.IMAGE
            true.imageGetterPopup()
        }


        callVenuesApi()


        if (offerList.size == 0) {
            offerList.add(AvailOfferModel(Constant().venueOfferOne, false))
            offerList.add(AvailOfferModel(Constant().venueOfferTwo, false))
            offerList.add(AvailOfferModel(Constant().venueOfferThree, false))
            offerList.add(AvailOfferModel(Constant().venueOfferFour, false))
            offerList.add(AvailOfferModel(Constant().venueOfferFive,false))
            offerList.add(AvailOfferModel(Constant().venueOfferSix,false))
            offerList.add(AvailOfferModel(Constant().venueOfferSeven,false))
        }
    }


    private fun openOffersDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogAvailableOfferBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_available_offer,
            null,
            false
        )

        binding.title = getString(R.string.txt_available_offers)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        availOfferAdapter = AvailOfferAdapter(offerList,offerListener,selectedOfferIndex,dialog)
        binding.rvOffers.layoutManager = LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false)
        binding.rvOffers.adapter = availOfferAdapter


        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.isFocusable = true
        dialog.isOutsideTouchable = true
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    var listener : AdapterMyVenuesListener<VenueListRes.Venue> = object : AdapterMyVenuesListener<VenueListRes.Venue>() {
        override fun onEventClick(item: VenueListRes.Venue, position: Int) {
            super.onEventClick(item, position)
            binding.inclChooseVenue.edtText.setText(item.venue_name)
            venueId = ""+item.id
            selectedVenues = venues[position]
            callVenueOfferListApi(venueId)
        }

        override fun onEventSelected(item: VenueListRes.Venue, position: Int) {
            super.onEventSelected(item, position)
            venueOfferBinding.btnDone.setOnClickListener {
                dialog.dismiss()
                selectedIndex = position
                onEventClick(item, position)
            }
        }
    }

    private var offerListener: AdapterOfferListener<AvailOfferModel> =
        object : AdapterOfferListener<AvailOfferModel>() {
            override fun onOfferItemClick(item: AvailOfferModel, position: Int) {
                super.onOfferItemClick(item, position)
                setFragmentWithStack(
                    AddOffersFragment.newInstance(item, venueId),
                    AddOffersFragment::class.java.simpleName
                )
                selectedOfferIndex = position
            }

        }

    private var savedOfferListener: AdapterOfferListener<Offerslive> =
        object : AdapterOfferListener<Offerslive>() {
            override fun onSavedOfferClick(item: Offerslive, position: Int) {
                super.onSavedOfferClick(item, position)
                setFragmentWithStack(
                    AddOffersFragment.newInstanceOffer(item, venueId),
                    AddOffersFragment::class.java.simpleName
                )
            }

            override fun onSavedOfferDeleteClick(item: Offerslive, position: Int) {
                super.onSavedOfferDeleteClick(item, position)
                val commonDialog = CommonDialog(
                    requireActivity(),
                    getString(R.string.txt_yes),
                    getString(R.string.txt_no),
                    "",
                    getString(R.string.txt_delete_offer_message)
                )
                commonDialog.show()
            commonDialog.binding.btnPositive.setOnClickListener {
                commonDialog.dismiss()
                item.id.toString().callDeleteOfferApi(position)
            }
            commonDialog.binding.btnNegative.setOnClickListener {
                commonDialog.dismiss()
            }

        }
    }

    private fun String.callDeleteOfferApi(position: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.doVenueofferDelete(
                PrefManager.getAccessToken(),
                VenueofferDeleteReq(this)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            offers.removeAt(position)
                            venueSavedOfferAdapter.notifyDataSetChanged()
                        }
                    }
                    if (offers.size == 0) {
                        binding.tvSavedOffers.visibility = View.VISIBLE
                    } else {
                        binding.tvSavedOffers.visibility = View.GONE
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    var dialog: PopupWindow = PopupWindow()
    private fun openVenueOfferDialog() {
        dialog = PopupWindow(mActivity)

        venueOfferBinding.title = getString(R.string.txt_select_venue)

        venueOfferBinding.rvVenues.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        venueOfferBinding.rvVenues.adapter = venueOfferAdapter

        venueOfferBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        venueOfferBinding.btnDone.setOnClickListener {
            dialog.dismiss()
        }

        dialog.animationStyle = R.style.animation
        dialog.contentView = venueOfferBinding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.isFocusable = true
        dialog.isOutsideTouchable = true
        dialog.showAtLocation(venueOfferBinding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


    private fun callVenuesApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueListRes?>? =
                apiInterface.doGetVenuesList(
                    PrefManager.getAccessToken(),
                    EventReq(pageLimit, strSearch, pageNo)
                )
            call!!.enqueue(object : Callback<VenueListRes?> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (pageNo == defaultPage) {
                            venues.clear()
                        }
                        if (response.body()!!.success) {
                            venues.clear()
                            venues.addAll(response.body()!!.venues)
                            pageNo++
                            isLoading = true
                        }

                        venueOfferAdapter.notifyDataSetChanged()

                        binding.inclChooseVenue.edtText.setText(venues[selectedIndex].venue_name)
                        venueId = ""+venues[selectedIndex].id
                        selectedVenues = venues[selectedIndex]
                        if (selectedOfferIndex >= 0){
                            binding.inclSelectOffer.edtText.setText(offerList[selectedOfferIndex].title)
                        }


                        callVenueOfferListApi(venueId)
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    var venuesOfferRes : VenueOfferRes? = null
    private fun callVenueOfferListApi(venueId: String) {
        if (isInternetConnect()) {
            showProgress()
            if (offers.size > 0) {
                offers.clear()
            }
            counterMenuImage = -1
            images.clear()
            binding.llEventImage.removeAllViews()
            val call: Call<VenueOfferRes?>? = apiInterface.getVenueOfferList(
                PrefManager.getAccessToken(),
                VenueDetailsReq(venueId)
            )
            call!!.enqueue(object : Callback<VenueOfferRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueOfferRes?>,
                    response: Response<VenueOfferRes?>
                ) {
                    if (isValidResponse(response)) {
                        venuesOfferRes = response.body()
                        if (response.body()!!.success) {
                            offers.addAll(response.body()!!.data!!.offers!!)
                            venueSavedOfferAdapter.notifyDataSetChanged()
                        }

                        if (offers.size == 0) {
                            binding.tvNoOffers.visibility = View.VISIBLE
                        } else {
                            binding.tvNoOffers.visibility = View.GONE
                        }
                    }

                    hideProgress()
                    counterMenuImage = -1
                    for (i in 0 until response.body()!!.data!!.menus!!.size) {
                        displayImage(response.body()!!.data!!.menus!![i].image)
                    }
                }

                override fun onFailure(call: Call<VenueOfferRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun displayImage(url: String?) {
        counterMenuImage++
        images.add(
            ImageVideoView(
                activity, onViewClickListener, CONSTANT.IMAGE + counterMenuImage,
                url!!
            )
        )
        binding.llEventImage.addView(
            images[images.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        for (i in 0 until images.size) {
            images[i].imgClose!!.visibility = View.VISIBLE
        }
        if (images.size == 5) {
            binding.imgAddImage.visibility = View.GONE
        }
        binding.llEventImage.requestLayout()
    }

    private fun String.callVenueOfferUpdateSubs() {
        if (isInternetConnect()) {
            showProgress()
            val imageList = arrayOfNulls<String>(images.size)
            for (i in images.indices) {
                imageList[i] = images[i].imageUrl
            }
            val call: Call<VenueOfferRes?>? = apiInterface.doVenueOfferUpdateSub(
                PrefManager.getAccessToken(),
                VenueOfferUpdateSubReq(this, imageList)
            )
            call!!.enqueue(object : Callback<VenueOfferRes?> {
                override fun onResponse(
                    call: Call<VenueOfferRes?>,
                    response: Response<VenueOfferRes?>
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            setFragmentWithStack(OfferPreviewFragment.newInstance(response.body()!!.data,
                                selectedVenues), OfferPreviewFragment::class.java.simpleName)
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


    // private var pictureUri: Uri? = null
    private var selectedImagePath: String? = null

    // private var bm: Bitmap? = null
    // private var compressedImage: File? = null
    private var selectedPdfFile = ""
    // private var selectedVideoPath = ""

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
            ImagePicker.Companion.with(this@OffersFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            ImagePicker.Companion.with(this@OffersFragment)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                imagePickerCameraRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    addViewMenuImage(selectedImagePath, CONSTANT.MENUIMAGE)
                }
                imagePickerGalleryRequest -> {
                    selectedImagePath = PathUtil().getPath(requireActivity(), data!!.data!!)
                    addViewMenuImage(selectedImagePath, CONSTANT.MENUIMAGE)
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

    private fun addViewMenuImage(Image: String?, ImageType: String) {
        counterMenuImage++
        val imageVideoView =
            ImageVideoView(mActivity, onViewClickListener, ImageType + counterMenuImage)
        images.add(imageVideoView)
        binding.llEventImage.addView(
            images[images.size - 1],
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        if (images.size > 1) {
            for (i in images.indices) {
                images[i].imgClose!!.visibility = View.VISIBLE
            }
        } else {
            images[0].imgClose!!.visibility = View.INVISIBLE
        }
        if (images.size == 3) {
            binding.imgAddImage.visibility = View.GONE
        }
        binding.llEventImage.requestLayout()
        imageVideoView.uploadVideo(Image!!)
    }

    private var onViewClickListener = object : ImageVideoView.OnViewClickListener() {
        override fun onClose(parent: View?, child: View?, Ids: String?) {
            if (parent!!.parent != null) {
                if (Ids!!.contains(CONSTANT.IMAGE)) {
                    binding.llEventImage.removeView(parent)
                    images.remove(parent)
                    binding.llEventImage.requestLayout()
                }
            }
        }

        override fun onImageClick(parent: ImageVideoView?, child: View?, Ids: String?) {
            var urlOne: URL
            var urlTwo: URL
            if (!parent!!.imageUrl.toString().lowercase(Locale.getDefault())
                    .contains(Constant().pdf)
            ) {
                return
            }
            for (i in images.indices) {
                try {
                    urlOne = URL(parent.imageUrl)
                    urlTwo = URL(images[i].imageUrl)
                    if (urlOne == urlTwo) {
                        val browserIntent =
                            Intent(getBaseActivity(), WebViewActivity::class.java).putExtra(
                                Constant().path,
                                images[i].imageUrl
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
}
package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
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
import com.popiin.req.VenueofferDeleteReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueListRes.Offerslive
import com.popiin.util.AttachmentHelper
import com.popiin.util.Constant
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.popiin.views.ImageVideoView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.popiin.activity.BookNowFragment.Companion.event
import com.popiin.activity.BookNowFragment.Companion.favouriteEvent
import com.popiin.activity.BookingSummaryFragment
import com.popiin.adapter.SelectTrendAdapter
import com.popiin.adapter.VenueTrendsAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.listener.AdapterVenueTrendsListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.CreateTrendReq
import com.popiin.req.DeleteTrendReq
import com.popiin.req.VenueDetailsReq
import com.popiin.res.CreateTrendRes
import com.popiin.res.Trend
import com.popiin.res.TrendRes
import com.popiin.res.VenueTrend
import com.popiin.res.VenueTrendRes
import com.popiin.services.MyNotificationPublisher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class TrendingFragment : BaseFragment<FragmentTrendingsBinding>() {
    private var pageNo = defaultPage
    private var venues = ArrayList<VenueListRes.Venue>()
    lateinit var venueTrendsAdapter: VenueTrendsAdapter
    private var trendList=ArrayList<Trend>()
    private var venueTrendList=ArrayList<VenueTrend>()
    lateinit var venueOfferBinding: DialogChooseVenueOfferBinding
    private var imageTypes = ""
    private var venueId = ""
    private var selectedVenues: VenueListRes.Venue? = null
    lateinit var venueListAdapter: OfferVenueAdapter
    private lateinit var selectTrendAdapter: SelectTrendAdapter
    private var imagePickerCameraRequest = 102
    private var imagePickerGalleryRequest = 103
    private var counterMenuImage = -1
    private val images = ArrayList<ImageVideoView>()
    private var isLoading = true
    private var strSearch = ""
    private var selectedIndex = 0
    override fun getLayout(): Int {
        return R.layout.fragment_trendings
    }
    companion object {
        fun newInstance(): TrendingFragment {
            return TrendingFragment()
        }
    }
    override fun initViews() {

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if(isValidate()){
                 callCreateTrendAPi(venueId = venueId.toInt(), trendId = selectedTrend, bannerImage = images.get(0).imageUrl!!,binding.inclDescription.edtText.text.toString())
            }
        }

        venueOfferBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_choose_venue_offer, null, false)

        venueListAdapter = OfferVenueAdapter(venues,listener,selectedIndex)

        venueTrendsAdapter = VenueTrendsAdapter(venueTrendList,adapterVenueTrendsListener)
        binding.rvSavedVenueTrends.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvSavedVenueTrends.adapter = venueTrendsAdapter

        binding.inclChooseVenue.edtText.setOnClickListener {
            // open venue list dialog
            openVenueOfferDialog()
        }
        binding.inclSelectTrend.edtText.setOnClickListener {
            // open venue list dialog
            openTrendDialog()
        }
        binding.imgAddImage.setOnClickListener {
            imageTypes = CONSTANT.IMAGE
            true.imageGetterPopup()
        }
        callVenuesApi()
        callTrendListAPi()

        binding.btnCancel.setOnClickListener {
            binding.btnCancel.visibility=View.GONE
            binding.btnUpdate.visibility=View.GONE
            binding.btnSave.visibility=View.GONE
            resetData()
        }
        binding.btnUpdate.setOnClickListener {
            if(isValidate()){
                callUpdateTrendAPi(
                    venueId = venueId.toInt(),
                    trendId = selectedTrend,
                    bannerImage = images.get(0).imageUrl!!,
                    summary = binding.inclDescription.edtText.text.toString(),
                )
            }
        }
    }

    private fun resetData(){
        selectedTrend=-1
        binding.inclSelectTrend.edtText.text=""
        binding.inclDescription.edtText.text=""
        binding.llEventImage.removeAllViews()
        images.clear()
        binding.llEventImage.requestLayout()
        binding.btnUpdate.visibility=View.GONE
        binding.btnCancel.visibility=View.GONE
        binding.btnSave.visibility=View.VISIBLE

    }
    private fun isValidate():Boolean{
        var isValid=true
        binding.inclSelectTrend.tvError.text=""
        if(selectedTrend==-1){
            isValid=false
            binding.inclSelectTrend.tvError.visibility=View.VISIBLE
            binding.inclSelectTrend.tvError.text= getString(R.string.please_select_trends)
        }else if (images.size == 0) {
                binding.tvImageError.visibility = View.VISIBLE
                binding.tvImageError.text = resources.getString(R.string.txt_err_events_upload_image)
                isValid = false
        }

        for (index in images.indices) {
            if(images[index].imageUrl!=null && images[index].imageUrl!!.isEmpty()) {
                showToast(resources.getString(R.string.txt_err_venues_image_uploading))
                isValid = false
                break
            }
        }

        return isValid
    }

    private fun openTrendDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogSelectTrendBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_select_trend,
            null,
            false
        )

        binding.title = getString(R.string.txt_select_trends)
        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        selectTrendAdapter = SelectTrendAdapter(trendList,trendListener,dialog)
        binding.rvOffers.layoutManager = LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false)
        binding.rvOffers.adapter = selectTrendAdapter

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
            callListVenueTrendAPi(venueId)
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
    private var venueTrend:VenueTrend?=null
    private var venueUpdatePosition=0
   private var selectedTrend:Int=-1;
    private var trendListener: AdapterOfferListener<Trend> =
        object : AdapterOfferListener<Trend>() {
            override fun onOfferItemClick(item: Trend, position: Int) {
                super.onOfferItemClick(item, position)
                selectedTrend=item.id
                binding.inclSelectTrend.edtText.text=item.title
            }
        }

    var adapterVenueTrendsListener : AdapterVenueTrendsListener<VenueTrend> =
        object : AdapterVenueTrendsListener<VenueTrend>(){
            override fun onDeleteVenueTrendsClick(item: VenueTrend, position: Int) {
                super.onDeleteVenueTrendsClick(item, position)
                item.id.openTrendDeleteDialog(position)
            }

            override fun onEditVenueTrendsClick(item: VenueTrend, position: Int) {
                super.onEditVenueTrendsClick(item, position)
                venueTrend=item
                venueUpdatePosition=position
                displayImage(item.banner_image)
                binding.inclDescription.edtText.text=item.summary
                val result = trendList.firstOrNull { it.id == item.trend_id }
                selectedTrend=result!!.id
                binding.inclSelectTrend.edtText.text=result.title
                val venueResult = venues.firstOrNull { it.id == item.venue_id }
                binding.inclChooseVenue.edtText.text= venueResult?.venue_name!!
                venueId=venueResult.id.toString()

                binding.btnSave.visibility=View.GONE
                binding.btnCancel.visibility=View.VISIBLE
                binding.btnUpdate.visibility=View.VISIBLE

            }

            override fun onSelectVenueTrendsClick(item: VenueTrend, position: Int) {
                super.onSelectVenueTrendsClick(item, position)
            }
        }


/*
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
*/

    var dialog: PopupWindow = PopupWindow()
    private fun openVenueOfferDialog() {
        dialog = PopupWindow(mActivity)
        venueOfferBinding.title = getString(R.string.txt_select_venue)
        venueOfferBinding.rvVenues.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        venueOfferBinding.rvVenues.adapter = venueListAdapter

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
                        venueListAdapter.notifyDataSetChanged()
                        if(venues.size>0) {
                            binding.inclChooseVenue.edtText.setText(venues[selectedIndex].venue_name)
                            venueId = "" + venues[selectedIndex].id
                            selectedVenues = venues[selectedIndex]
                            callListVenueTrendAPi(venueId = venueId)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
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
            ImagePicker.Companion.with(this@TrendingFragment)
                .crop()
                .compress(5*1024)         //Final image size will be less than 1 MB(Optional)
                .cameraOnly()
                .provider(ImageProvider.CAMERA)
                .start(imagePickerCameraRequest)
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            ImagePicker.Companion.with(this@TrendingFragment)
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

    private fun callTrendListAPi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<TrendRes> =
                apiInterface.getListOfTrend(
                    PrefManager.getAccessToken(),
                )
            call.enqueue(object : Callback<TrendRes>{
                override fun onResponse(call: Call<TrendRes>, response: Response<TrendRes>) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success){
                            trendList.clear()
                            trendList.addAll(response.body()!!.data)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<TrendRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })

        }
    }

    private fun callListVenueTrendAPi(venueId:String) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueTrendRes> =
                apiInterface.getListVenueTrend(
                    PrefManager.getAccessToken(),
                    VenueDetailsReq(venueId,null,null)
                )
            call.enqueue(object : Callback<VenueTrendRes>{
                override fun onResponse(call: Call<VenueTrendRes>, response: Response<VenueTrendRes>) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success){
                            venueTrendList.clear()
                            venueTrendList.addAll(response.body()!!.venueTrend)
                            venueTrendsAdapter.notifyDataSetChanged()
                        }else{
                            venueTrendList.clear()
                            venueTrendsAdapter.notifyDataSetChanged()
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueTrendRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })

        }
    }

    private fun callCreateTrendAPi(venueId:Int,trendId:Int,bannerImage:String,summary:String?) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateTrendRes> =
                apiInterface.doCreateTrend(
                    PrefManager.getAccessToken(),
                    CreateTrendReq(venue_id = venueId, trend_id = trendId, banner_image = bannerImage, summary =summary,id=0 )
                )
            call.enqueue(object : Callback<CreateTrendRes>{
                override fun onResponse(call: Call<CreateTrendRes>, response: Response<CreateTrendRes>) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success){
                            resetData()
                            venueTrendList.add(response.body()!!.trend)
                            venueTrendsAdapter.notifyDataSetChanged()
                            openSuccessDialog(response.body()!!.message, getString(R.string.txt_trends_added))
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateTrendRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })

        }
    }

    private fun callUpdateTrendAPi(venueId:Int,trendId:Int,bannerImage:String,summary:String?) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateTrendRes> =
                apiInterface.doUpdateTrend(
                    PrefManager.getAccessToken(),
                    CreateTrendReq(
                        venue_id = venueId,
                        trend_id = trendId,
                        banner_image = bannerImage,
                        summary =summary,
                        id = venueTrend?.id!!
                    )
                )
            call.enqueue(object : Callback<CreateTrendRes>{
                override fun onResponse(call: Call<CreateTrendRes>, response: Response<CreateTrendRes>) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success){
                            resetData()
                            val index = venueTrendList.indexOfFirst { it.id == response.body()!!.trend.id }
                            if (index != -1) {
                                venueTrendList[index] = response.body()!!.trend  // replace with the new object
                                resetData()
                            }
                            venueTrendsAdapter.notifyDataSetChanged()
                            openSuccessDialog(response.body()!!.message, getString(R.string.txt_trends_updated))
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateTrendRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })

        }
    }

    private fun callDeleteTrendAPi(venueOfferId:Int,position:Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CreateTrendRes> =
                apiInterface.doDeleteTrend(
                    PrefManager.getAccessToken(),
                    DeleteTrendReq(id = venueOfferId)
                )
            call.enqueue(object : Callback<CreateTrendRes>{
                override fun onResponse(call: Call<CreateTrendRes>, response: Response<CreateTrendRes>) {
                    if (isValidResponse(response)) {
                        if(response.body()!!.success){
                            venueTrendList.removeAt(position)
                            venueTrendsAdapter.notifyDataSetChanged()
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateTrendRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })

        }
    }

    private fun openSuccessDialog(message: String,title:String) {
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
        commonInfoBottomFragment.binding?.ivClose?.visibility=View.GONE
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
        }

    }

    private fun Int.openTrendDeleteDialog(position: Int) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if (position == -1 && this == 0) {
            binding.btnNo.visibility = View.GONE
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_pass_success
                )
            )
            binding.ivClose.visibility = View.GONE
            binding.tvPassSuccess.text = getString(R.string.txt_venue_removed)
            binding.tvSuccess.text = getString(R.string.txt_venue_has_deleteed)
        } else {
            binding.tvPassSuccess.text = getString(R.string.txt_sure_to_delete)
            binding.tvSuccess.text = getString(R.string.txt_delete_trending_message)
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_sure_delete
                )
            )
            binding.ivClose.visibility = View.VISIBLE
            binding.btnNo.visibility = View.VISIBLE
            binding.btnNo.text = getString(R.string.txt_no)
            binding.btnDone.text = getString(R.string.txt_yes_delete)
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (position != -1 && this != 0) {
                callDeleteTrendAPi(this,position)
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



}
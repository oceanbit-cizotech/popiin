package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.StoryListAdapter
import com.popiin.bottomDialogFragment.AddStoryCaptionBottomFragment
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.bottomDialogFragment.SelectVenueBottomFragment
import com.popiin.databinding.FragmentStoryBinding
import com.popiin.databinding.ImagePickerDialogBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.listener.AddSpecialRequiremnetListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.AddStoryReq
import com.popiin.req.EventReq
import com.popiin.res.*
import com.popiin.util.PathUtil
import com.popiin.util.PrefManager
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.gowtham.library.utils.TrimmerUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryFragment : BaseFragment<FragmentStoryBinding>() {
    var venues = ArrayList<VenueListRes.Venue>()
    var selectedVenues = 0
    var venueId = 0
    var storyId = 0
    var storyPosition = 0
    var isVideoUpload = false
    var isDeleteStory = false
    var storyList: ArrayList<VenueStoryListRes> = ArrayList()
    lateinit var storyListAdapter: StoryListAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_story
    }

    companion object {
        fun newInstance(): StoryFragment {
            return StoryFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.inclVenueType.edtText.setTextColor(ContextCompat.getColor(requireActivity(),
            R.color.colorHeaderText))

        binding.inclVenueType.edtText.setOnClickListener {
            showSelectVenueBottomFragment()
        }

        binding.btnAddStory.setOnClickListener {
            openImagePopup()
        }

        storyListAdapter = StoryListAdapter(storyList, storyListener)
        binding.rvStories.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvStories.adapter = storyListAdapter

        callMyVenuesApi()

    }

    private fun openImagePopup() {
        val dialog = PopupWindow(mActivity)
        val binding: ImagePickerDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.image_picker_dialog, null, false
        )

        binding.tvCamera.setOnClickListener {
            dialog.dismiss()
            try {
                val intent = Intent("android.media.action.VIDEO_CAPTURE")
                intent.putExtra("android.intent.extra.durationLimit", 30)
                takeOrSelectVideoResultLauncher.launch(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        binding.tvGallery.setOnClickListener {
            dialog.dismiss()
            try {
                val intent = Intent()
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

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

    private var storyListener: AdapterItemClickListener<VenueStoryListRes> =
        object : AdapterItemClickListener<VenueStoryListRes>() {
            override fun onAdapterDeleteClick(item: VenueStoryListRes, position: Int) {
                super.onAdapterDeleteClick(item, position)
                isDeleteStory = true
                storyId = item.data!![position].id
                storyPosition = position
                openSuccessDialog(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_sure_delete)!!,
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_delete_story),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_delete))
            }

            override fun onAdapterItemClick(item: VenueStoryListRes, position: Int) {
                super.onAdapterItemClick(item, position)
          /*      startActivity(Intent(requireActivity(), StoryPlayerActivity::class.java).putExtra(
                    Constant().item,
                    item).putExtra(Constant().position, position))
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
          */  }
        }

    private fun showSelectVenueBottomFragment() {
        val selectVenueBottomFragment = SelectVenueBottomFragment(venues, listener, selectVenue)
        selectVenueBottomFragment.isCancelable = false
        selectVenueBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectVenueBottomFragment.show(it, "") }
    }

    private fun showStoryCaptionBottomFragment(dialogTitle: String, hint: String) {
        val addStoryCaptionBottomFragment =
            AddStoryCaptionBottomFragment(dialogTitle, dialogTitle, hint, captionListener)
        addStoryCaptionBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let {
            addStoryCaptionBottomFragment.show(
                it,
                ""
            )
        }
    }

    private var captionListener: AddSpecialRequiremnetListener<String> =
        object : AddSpecialRequiremnetListener<String>() {
            override fun onAddCaptionClick(specialRequirement: String) {
                super.onAddCaptionClick(specialRequirement)
                callAddVenueStoryApi(videoUrl, videoId, specialRequirement)
            }
        }

    var selectVenue: Int = 0
    var listener: AdapterMyVenuesListener<VenueListRes.Venue> =
        object : AdapterMyVenuesListener<VenueListRes.Venue>() {
            override fun onEventSelected(item: VenueListRes.Venue, position: Int) {
                super.onEventSelected(item, position)
                venues[position]
                selectVenue = position
                venues[position].isSelected = true
                venueId = venues[position].id
                binding.inclVenueType.edtText.setText(venues[position].venue_name)
                isVideoUpload = false
                callVenueStoryListApi(venues[position].id, "")
            }
        }

    private fun callMyVenuesApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueListRes?>? =
                apiInterface.doGetVenuesList(
                    PrefManager.getAccessToken(),
                    EventReq(100, "", 0)
                )
            call!!.enqueue(object : Callback<VenueListRes?> {

                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            venues.clear()
                            venues.addAll(response.body()!!.venues)
                            venues[selectedVenues].isSelected = true
                            binding.inclVenueType.edtText.setText(venues[selectedVenues].venue_name)
                            venueId = venues[selectedVenues].id
                            isVideoUpload = false
                            callVenueStoryListApi(venues[selectedVenues].id, "")
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

    var videoUrl: String = ""
    var videoId: String = ""
    private var videoTrimResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))

            val fileName = PathUtil().getPathFromURI(requireActivity(), uri)

            MediaManager.get().upload(fileName).callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // your code here
                    showProgress()
                    println("videoTrimResultLauncher : onStart")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    println("videoTrimResultLauncher : onProgress")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                    // your code here
                    println("videoTrimResultLauncher : onSuccess ${resultData!!["url"].toString()}")
                    println("videoTrimResultLauncher : onSuccess ${resultData["public_id"].toString()}")
                    hideProgress()
                    videoUrl = resultData["url"].toString()
                    videoId = resultData["public_id"].toString()
                    showStoryCaptionBottomFragment(getString(R.string.txt_add_caption),
                        getString(R.string.other_placeholder))
//                    callAddVenueStoryApi(videoUrl, videoId)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    // your code here
                    println("videoTrimResultLauncher : onError ${error!!.description}")
                    println("videoTrimResultLauncher : onError ${error.code}")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    // your code here
                    println("videoTrimResultLauncher : onReschedule $error")
                }
            })
                .option("resource_type", "video")
                .dispatch()


        } else LogMessage.v("videoTrimResultLauncher data is null")
    }

    private fun callAddVenueStoryApi(videoUrl: String, videoId: String, storyText: String) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<AddVenueStoryRes?>? =
                apiInterface.addVenueStory(PrefManager.getAccessToken(),
                    AddStoryReq(videoId, venueId, videoUrl, storyText))
            call!!.enqueue(object : Callback<AddVenueStoryRes?> {
                override fun onResponse(
                    call: Call<AddVenueStoryRes?>,
                    response: Response<AddVenueStoryRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        isVideoUpload = true
                        callVenueStoryListApi(venueId, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<AddVenueStoryRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }

    private var takeOrSelectVideoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK &&
            result.data != null
        ) {
            val data = result.data
            //check video duration if needed
            if (data!!.data != null) {
                println("takeOrSelectVideoResultLauncher : Video path:: " + data.data)
                openTrimActivity(data.data.toString())
            } else {
                showToast("video url is null")
            }
        } else LogMessage.v("takeVideoResultLauncher data is null")
    }

    private fun openTrimActivity(path: String) {

     /*   val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)

        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 720
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 1280
        retriever.release() // Always release retriever to prevent memory leaks
*/
        val wAndH = TrimmerUtils.getVideoWidthHeight(mActivity, Uri.parse(path))
        var width = wAndH[0]
        val height = wAndH[1]


        val compressOption = if (width > 800) {
            width /= 2
            width /= 2
            CompressOption(30, "4M", width, height)
        } else {
            CompressOption(30, "3M", width, height)
        }

        TrimVideo.activity(path)
            .setAccurateCut(true)
            .setTrimType(TrimType.FIXED_DURATION)
            .setFixedDuration(30)
            .setCompressOption(compressOption)
            .start(requireActivity(), videoTrimResultLauncher)

      /*  TrimVideo.activity(path)
            .setAccurateCut(true)
            .setTrimType(TrimType.FIXED_DURATION)
            .setFixedDuration(30)
            .start(requireActivity(), videoTrimResultLauncher)*/
    }

    private fun openSuccessDialog(
        image: Drawable,
        title: String,
        message: String,
        negativeText: String,
        positiveText: String,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                title,
                message,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                infoDialogListener
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
            if (isDeleteStory) {
                callDeleteVenueStoryApi()
            }

        }

    }

    private fun callDeleteVenueStoryApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? =
                apiInterface.deleteVenueStory(PrefManager.getAccessToken(), storyId)
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    hideProgress()
                    if (response.body()!!.status) {
                        storyList.removeAt(storyPosition)
                        storyListAdapter.notifyDataSetChanged()
                        isVideoUpload = false
                        callVenueStoryListApi(venueId, "")
                    }
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }


    private fun callVenueStoryListApi(
        @Suppress("SameParameterValue") venueId: Int,
        message: String,
    ) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueStoryListRes?>? =
                apiInterface.getStoryList(PrefManager.getAccessToken(), venueId)
            call!!.enqueue(object : Callback<VenueStoryListRes?> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueStoryListRes?>,
                    response: Response<VenueStoryListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        storyList.clear()
                        for (i in 0 until response.body()!!.data!!.size) {
                            storyList.add(response.body()!!)
                        }

                        storyListAdapter.notifyDataSetChanged()

                        if (isVideoUpload) {
                            isDeleteStory = false
                            openSuccessDialog(
                                ContextCompat.getDrawable(requireActivity(),
                                    R.drawable.ic_pass_success)!!,
                                getString(R.string.txt_story_uploaded),
                                message,
                                "",
                                getString(R.string.txt_done))
                        }

                        if (storyList.size == 0) {
                            binding.llNoData.visibility = View.VISIBLE
                        } else {
                            binding.llNoData.visibility = View.GONE
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueStoryListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

}
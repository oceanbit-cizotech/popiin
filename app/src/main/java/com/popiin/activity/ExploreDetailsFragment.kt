package com.popiin.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.popiin.APIClientMap
import com.popiin.ApiInterface
import com.popiin.BaseMapFragment
import com.popiin.R
import com.popiin.adapter.*
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.*
import com.popiin.dialog.DirectionsDialog
import com.popiin.fragment.ActivatedOfferQrCodeFragment
import com.popiin.fragment.ImagePreviewFragment
import com.popiin.fragment.VenueBookFragment
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.ImageOnclick
import com.popiin.listener.OfferListener
import com.popiin.model.*
import com.popiin.realm.VenuesFavorites
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import com.popiin.res.GoogleDirectionRes
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import com.popiin.listener.FavoriteListener
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.collections.ArrayList


class ExploreDetailsFragment : BaseMapFragment<FragmentExploreDetailsBinding>() {
    private lateinit var exploreDetailImgAdapter: ExploreDetailImgAdapter
    private lateinit var openCloseAdapter: ExploreOpenCloseAdapter
    private lateinit var venueTrendsListAdapter: VenueTrendsListAdapter
    private var openCloseList: ArrayList<ExploreOpenCloseModel> = ArrayList()
    private var exploreDetailImgList: ArrayList<String> = ArrayList()
    private var exploreDetailReserveList: ArrayList<ExploreDetailReserveModel> = ArrayList()
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var isTicketEmpty = false
    private var mMap: GoogleMap? = null
    private lateinit var whatsOnAdapter: WhatsOnDialogAdapter
    private var whatsOnList: ArrayList<VenueListRes.Whatson> = ArrayList()
    private lateinit var listener: AdapterItemClickListener<VenueListRes.Whatson>
    private lateinit var amensMainAdapter: AmenAdapter
    private lateinit var venueOfferAdapter: VenueOfferAdapter
    private var amensMainList: ArrayList<AmensModel> = ArrayList()
    private var subAmenList: ArrayList<AmensSubModel> = ArrayList()
    private var subAge: ArrayList<AmensSubModel> = ArrayList()
    private var venueTypeArray: ArrayList<AmensSubModel> = ArrayList()
    private var subOffer: ArrayList<String> = ArrayList()
    private var amensSubMusicList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubEntertainList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubBarList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubPubList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubHotelList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubRestaurantList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubNightClubList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubCafeList: ArrayList<AmensSubModel> = ArrayList()
    private var amensSubOtherList: ArrayList<AmensSubModel> = ArrayList()

    var favorites : FavoriteListener?=null

    override fun getLayout(): Int {
        return R.layout.fragment_explore_details
    }

    companion object {
        var exploreItem: VenueListRes.Venue? = null
        fun newInstance(item: VenueListRes.Venue?): ExploreDetailsFragment {
            exploreItem = item
            return ExploreDetailsFragment()
        }
    }
    var c: Common = Common.instance!!


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {

        binding.isFavorite= exploreItem!!.isFavorite
        if (exploreItem!!.images!!.isNotEmpty() && exploreItem!!.images!![0].image!!.isNotEmpty()) {
            common.loadImageFromUrl(binding.ivImage, exploreItem!!.images!![0].image)
        } else if (exploreItem!!.venue_banner_image!!.isNotEmpty()) {
            common.loadImageFromUrl(binding.ivImage, exploreItem!!.venue_banner_image)
        } else {
         //   common.loadImageFromUrl(binding.ivImage, CONSTANT.POPIINIMAGE)
        }

        if (exploreItem!!.whatsons!=null && exploreItem!!.whatsons!!.size > 0) {
            var isWhatson =false
            for (watson in exploreItem!!.whatsons!!) {
                isWhatson = c.dateInBitween(watson.what_datetime.toString(), watson.end_time.toString())
                if(isWhatson){
                    break
                }
            }
            if(isWhatson){
                binding.viewWhatsOnLine.visibility=View.VISIBLE
                binding.inclWhatsOn.root.visibility = View.VISIBLE
            }else{
                binding.viewWhatsOnLine.visibility=View.GONE
                binding.inclWhatsOn.root.visibility = View.GONE
            }

        } else {
            binding.inclWhatsOn.root.visibility = View.GONE
        }

        if (exploreItem!!.menus!!.isEmpty()) {
            binding.inclMenu.root.visibility = View.GONE
        } else {
            binding.inclMenu.root.visibility = View.VISIBLE
        }

        if (exploreItem!!.tickets!!.size > 0) {
            binding.btnBookNow.visibility = View.VISIBLE
        } else {
            binding.btnBookNow.visibility = View.GONE
        }



        //setTextViewSelectionWithColor(binding.tvWalk, binding.tvBus, binding.tvCar)

        binding.tvWalk.setOnClickListener {
            setTextViewSelectionWithColor(binding.tvWalk, binding.tvBus, binding.tvCar)
            openDirectionDialog()
        }

        binding.tvBus.setOnClickListener {
            setTextViewSelectionWithColor(binding.tvBus, binding.tvWalk, binding.tvCar)
            openDirectionDialog()
        }

        binding.tvCar.setOnClickListener {
            setTextViewSelectionWithColor(binding.tvCar, binding.tvBus, binding.tvWalk)
            openDirectionDialog()
        }

        binding.btnBookNow.setOnClickListener {
            setFragmentWithStack(
                VenueBookFragment.newInstance(exploreItem!!),
                VenueBookFragment::class.java.simpleName
            )
        }

        exploreDetailReserveList.clear()
        for (i in 0 until exploreItem!!.tickets!!.size) {
            isTicketEmpty = true
            exploreDetailReserveList.add(ExploreDetailReserveModel(exploreItem!!.tickets!![i].name!!))
        }

        if (exploreDetailReserveList.size == 0) {
            binding.inclReservation.root.visibility = View.GONE
        } else {
            binding.inclReservation.root.visibility = View.VISIBLE
        }

        binding.inclTrending.root.visibility = View.GONE
        if (exploreItem?.trending!!.isEmpty()) {
            binding.inclTrending.root.visibility = View.GONE
        }else {
            binding.inclTrending.root.visibility = View.VISIBLE
        }

        if (exploreItem!!.distance != null && exploreItem!!.distance != 0.0) {
            binding.tvLocation.text = common.getOneDecimalFormatValueWithMiles(exploreItem!!.distance)
            binding.tvDistance.text = Constant().distance + " " + common.getOneDecimalFormatValueWithMiles(exploreItem!!.distance)

        } else {
            val distance = c.calculateDistanceInMiles(
                PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude,
                exploreItem!!.venue_latitude!!.toDouble(),
                exploreItem!!.venue_longitude!!.toDouble()
            )
            binding.tvLocation.text = common.getOneDecimalFormatValueWithMiles(distance)
            binding.tvDistance.text = Constant().distance + " " + common.getOneDecimalFormatValueWithMiles(distance)

        }


        binding.tvVenueName.text = exploreItem!!.venue_name
        val openCloseText = common.getOpenCloseText(exploreItem!!)
        val topThreeVenueType: List<String?> = common.getTopThreeVenueTypeList(exploreItem!!)

        if (openCloseText.equals(common.closedText)) {
            binding.tvHoursMayVary.visibility = View.GONE
        } else {
            binding.tvHoursMayVary.visibility = View.VISIBLE
            binding.tvHoursMayVary.text = getString(R.string.txt_open_now)
        }

        // Offer Venue Logic.
        var isOfferLiveVisible=false
        if(exploreItem!!.offerslive!=null) {
            for (item in exploreItem!!.offerslive!!) {
                if (c.isOfferAvailable(
                        item.working_day!!,
                        item.open_time!!,
                        item.close_time!!
                    ) == true
                ) {
                    isOfferLiveVisible = true
                    break
                }
            }

            if (isOfferLiveVisible && binding.tvHoursMayVary.visibility == View.VISIBLE) {
                binding.inclOffers.root.visibility = View.VISIBLE
            } else {
                binding.inclOffers.root.visibility = View.GONE
            }
        }else{
            binding.inclOffers.root.visibility = View.GONE
        }


        var venueType = ""

        if (topThreeVenueType.isEmpty()) {
            venueType = getString(R.string.txt_not_available)
        } else {
            for (venueTypeString in topThreeVenueType) {
                venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
            }
        }

        latitude = (exploreItem!!.venue_latitude)!!.toDouble()
        longitude = (exploreItem!!.venue_longitude)!!.toDouble()

        binding.category = venueType.replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, "")
        binding.address = exploreItem!!.venue_address
        binding.description = exploreItem!!.venue_description

        if (exploreItem!!.venue_description!!.length > 120) {
            binding.tvReadMore.visibility = View.VISIBLE
            binding.tvDescriptionDesc.maxLines = 2
            binding.tvReadMore.setText(R.string.txt_read_more)
        } else if (binding.tvDescriptionDesc.maxLines <= 2 && exploreItem!!.venue_description!!.length < 120) {
            binding.tvReadMore.visibility = View.GONE
        }

        exploreDetailImgAdapter =
            ExploreDetailImgAdapter(exploreDetailImgList, imageOnclickListener)

        binding.rvExploreImages.layoutManager = GridLayoutManager(mActivity, 5)
        binding.rvExploreImages.adapter = exploreDetailImgAdapter

        exploreDetailImgList.clear()
        for (i in 0 until exploreItem!!.images!!.size) {
            exploreDetailImgList.add(exploreItem!!.images!![i].image!!)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.tvReadMore.setOnClickListener {
            if (binding.tvDescriptionDesc.maxLines == 2) {
                binding.tvDescriptionDesc.maxLines = 120
                binding.tvReadMore.setText(R.string.txt_hide)
            } else {
                binding.tvDescriptionDesc.maxLines = 2
                binding.tvReadMore.setText(R.string.txt_read_more)
            }
        }

        binding.inclAmenFeature.root.setOnClickListener {
            openAmenFeatureBottomSheetDialog()
        }

        binding.inclMenu.root.setOnClickListener {
            openMenuBottomSheetDialog()
        }

        binding.inclReservation.root.setOnClickListener {
            openReservationBottomSheetDialog()
        }

        binding.inclTrending.root.setOnClickListener {
            openTrendingBottomSheetDialog()
        }

        binding.inclOffers.root.setOnClickListener {
            openOffersBottomSheetDialog()
        }

        binding.inclWhatsOn.root.setOnClickListener {
            openWhatOnBottomSheetDialog()
        }

        binding.inclOpenCloseHour.root.setOnClickListener {
            openCloseTimeBottomSheetDialog()
        }

        binding.ivShare.setOnClickListener {

            if(exploreItem!!.venue_share_link!=null && exploreItem!!.venue_share_link!!.isNotEmpty()){
                shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = exploreItem!!.venue_name, url =exploreItem!!.venue_share_link!! )
            }else {
                val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
                shareBranchIOLink(properties, object : BranchIOListener() {
                    override fun onLinkCreate(url: String?) {
                        super.onLinkCreate(url)
                        shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = exploreItem!!.venue_name, url =url!! )
                        callPostVenueUpdateLinkApi(exploreItem!!.id, url!!)
                    }
                })
            }
        }

        binding.isFavorite = exploreItem!!.isFavorite

        if (exploreItem!!.venue_other_information != null && exploreItem!!.venue_other_information!!.isNotEmpty()) {
            binding.tvOtherInfo.visibility = View.VISIBLE
            binding.tvOtherInfoData.visibility = View.VISIBLE
            binding.tvOtherInfoData.text = exploreItem!!.venue_other_information
            binding.viewWhatsOnLine.visibility=View.VISIBLE
            binding.viewOtherInfo.visibility = View.VISIBLE

        } else {
            binding.inclWhatsOn.root.visibility = View.GONE
            binding.tvOtherInfo.visibility = View.GONE
            binding.tvOtherInfoData.visibility = View.GONE
            binding.viewOtherInfo.visibility = View.GONE

        }

        binding.cbLike.setOnClickListener {
            if (exploreItem!!.isFavorite == 0) {
                setFavoriteData(exploreItem!!)
                callEventFavourite(exploreItem!!, 1)
                favorites?.onStatusUpdates(exploreItem!!.id.toInt(), 1,SHARE_MESSAGE_TYPE.VENUE)
            } else {
                setFavoriteData(exploreItem!!)
                callEventFavourite(exploreItem!!, 0)
                favorites?.onStatusUpdates(exploreItem!!.id.toInt(), 0,SHARE_MESSAGE_TYPE.VENUE)
            }
        }

        moveToLocation()
        val origin =
            "" + PrefManager.lastLocation!!.latitude + "," + PrefManager.lastLocation!!.longitude
        val destination = "" + exploreItem!!.venue_latitude + "," + exploreItem!!.venue_longitude
        getDirectionList(origin, destination, Constant().driving)
        getDirectionList(origin, destination, Constant().walking)
        getDirectionList(origin, destination, Constant().transit)

        if(PrefManager.config?.trendingFlag==0){
            binding.inclTrending.root.visibility = View.GONE
        }
    }

    var imageOnclickListener: ImageOnclick = object : ImageOnclick() {
        override fun onImageSelected(position: Int) {
            super.onImageSelected(position)
            exploreDetailImgList
            setFragmentAdd(
                ImagePreviewFragment.newInstance(exploreDetailImgList,position),
                ImagePreviewFragment::class.java.simpleName
            )
        }
    }

    private fun setTextViewSelectionWithColor(tvOne: TextView, tvTwo: TextView, tvThree: TextView) {
        if (!tvOne.isSelected) {
            tvOne.isSelected = true
            tvOne.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlue))
            tvTwo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorHeaderText))
            tvThree.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorHeaderText))
            tvOne.setDrawableColor(R.color.colorBlue)
            tvTwo.setDrawableColor(R.color.colorHeaderText)
            tvThree.setDrawableColor(R.color.colorHeaderText)
            tvTwo.isSelected = false
            tvThree.isSelected = false
        }
    }

    private fun TextView.setDrawableColor(@ColorRes color: Int) {
        compoundDrawables.filterNotNull().forEach {
            it.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, color),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun setFeaturesTags(binding: DialogAmenFeatureBinding) {

        subAmenList.clear()
        if (exploreItem!!.free_wifi == 1) {
            subAmenList.add(AmensSubModel(ContextCompat.getDrawable(requireActivity(),
                R.drawable.ic_wifi_venue), "WiFi available"))
        }

        if (exploreItem!!.is_outdoor_area == 1) {
            subAmenList.add(AmensSubModel(ContextCompat.getDrawable(requireActivity(),
                R.drawable.ic_blue_outdoor), "OutDoor area"))
        }

        subAge.clear()
        if (exploreItem!!.venue_age_group != null && exploreItem!!.venue_age_group!!.isNotEmpty()) {
            subAge.add(AmensSubModel(null, exploreItem!!.venue_age_group!!))
        }


        subOffer.clear()
        if (exploreItem!!.offers != null) {
            for (i in 0 until exploreItem!!.offers!!.size) {
                subOffer.add(exploreItem!!.offers[i].title!!.replace("null",""))
            }
        }

        if (subOffer.size == 0) {
            binding.tvVenueOffers.visibility = View.GONE
            binding.rvOffers.visibility = View.GONE
            binding.offerView.visibility = View.GONE
        } else {
            binding.tvVenueOffers.visibility = View.VISIBLE
            binding.rvOffers.visibility = View.VISIBLE
        }

        if (exploreItem!!.venue_dress_code != null) {
            if (exploreItem!!.venue_dress_code!!.isNotEmpty()) {
                binding.inclDressCode.root.visibility = View.VISIBLE
                binding.inclDressCode.desc = exploreItem!!.venue_dress_code!!
            } else {
                binding.inclDressCode.root.visibility = View.GONE
            }
        }

        if (exploreItem!!.venue_door_policy != null) {
            if (exploreItem!!.venue_door_policy!!.trim().isNotEmpty()) {
                binding.inclEntryPolicy.root.visibility = View.VISIBLE
                binding.inclEntryPolicy.desc = exploreItem!!.venue_door_policy!!
            } else {
                binding.inclEntryPolicy.root.visibility = View.GONE
            }
        }

        if (exploreItem!!.venue_last_entry != null) {
            if (exploreItem!!.venue_last_entry!!.trim().isNotEmpty()) {
                binding.inclLastEntryPolicy.root.visibility = View.VISIBLE
                binding.inclLastEntryPolicy.desc = exploreItem!!.venue_last_entry!!
            } else {
                binding.inclLastEntryPolicy.root.visibility = View.GONE
            }
        }


        if (exploreItem!!.venue_other_information != null) {
            if (exploreItem!!.venue_other_information!!.isNotEmpty()) {
                binding.inclOtherInfo.root.visibility = View.VISIBLE
                binding.inclOtherInfo.desc = exploreItem!!.venue_other_information!!
            } else {
                binding.inclOtherInfo.root.visibility = View.GONE
            }
        } else {
            binding.inclOtherInfo.root.visibility = View.GONE
        }

        if (binding.inclDressCode.root.visibility == View.GONE && binding.inclEntryPolicy.root.visibility == View.GONE &&
            binding.inclLastEntryPolicy.root.visibility == View.GONE && binding.inclOtherInfo.root.visibility == View.GONE
        ) {
            binding.tvVenueEntertainment.visibility = View.GONE
            binding.offerView.visibility = View.GONE
        }

        amensSubMusicList.clear()
        if (exploreItem!!.venue_music != null && exploreItem!!.venue_music!!.isNotEmpty()) {
            val musicData = exploreItem!!.venue_music!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            val musicList = musicData.split(",")
            for (element in musicList) {
                amensSubMusicList.add(
                    AmensSubModel(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_music
                        )!!, element
                    )
                )
            }
        }


        amensSubEntertainList.clear()
        if (exploreItem!!.entertainment != null && exploreItem!!.entertainment!!.isNotEmpty()) {
            val musicData = exploreItem!!.entertainment!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            val musicList = musicData.split(",")
            for (element in musicList) {
                amensSubEntertainList.add(
                    AmensSubModel(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_entertainment_venue
                        )!!, element
                    )
                )
            }
        }

        amensSubBarList.clear()
        setAmentiesDataWithTags(Constant().barConst,
            amensSubBarList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bar_glass_lemon)!!)

        amensSubPubList.clear()
        setAmentiesDataWithTags(Constant().pubConst,
            amensSubPubList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pub_table)!!)

        amensSubHotelList.clear()
        setAmentiesDataWithTags(Constant().hotelConst,
            amensSubHotelList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_music)!!)

        amensSubRestaurantList.clear()
        setAmentiesDataWithTags(Constant().restaurantConst,
            amensSubRestaurantList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_restro_reserve)!!)

        amensSubNightClubList.clear()
        setAmentiesDataWithTags(Constant().nightClub,
            amensSubNightClubList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_night_club_ball)!!)

        amensSubOtherList.clear()
        setAmentiesDataWithTags(Constant().otherConst,
            amensSubOtherList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_music)!!)

        amensSubCafeList.clear()
        setAmentiesDataWithTags(Constant().cafeRoman,
            amensSubCafeList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_coffee)!!)

        val topThreeVenueType: List<String?> = c.getTopThreeVenueTypeList(exploreItem!!)
        var venueType = ""

        if (!topThreeVenueType.isEmpty()) {
            for (venueTypeString in topThreeVenueType) {
                venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
            }
        }

        venueTypeArray.clear()
        if(venueType.length>0){
            venueTypeArray.add(AmensSubModel(null,venueType))
        }
        amensMainList.clear()
        if (subAmenList.size > 0) {
            amensMainList.add(AmensModel("Amenities", subAmenList))
        }
        if(venueTypeArray.size>0) {
            amensMainList.add(AmensModel("Venue", venueTypeArray))
        }

        if (subAge.size > 0) {
            amensMainList.add(AmensModel("Age", subAge))
        }

        if (amensSubMusicList.size > 0) {
            amensMainList.add(AmensModel(Constant().music, amensSubMusicList))
        }
        if (amensSubEntertainList.size > 0) {
            amensMainList.add(AmensModel(Constant().entertainment, amensSubEntertainList))
        }
        if (amensSubBarList.size > 0) {
            amensMainList.add(AmensModel(Constant().barConst, amensSubBarList))
        }
        if (amensSubPubList.size > 0) {
            amensMainList.add(AmensModel(Constant().pubConst, amensSubPubList))
        }
        if (amensSubHotelList.size > 0) {
            amensMainList.add(AmensModel(Constant().hotelConst, amensSubHotelList))
        }
        if (amensSubRestaurantList.size > 0) {
            amensMainList.add(AmensModel(Constant().restaurantConst, amensSubRestaurantList))
        }
        if (amensSubNightClubList.size > 0) {
            amensMainList.add(AmensModel(Constant().nightClub, amensSubNightClubList))
        }
        if (amensSubCafeList.size > 0) {
            amensMainList.add(AmensModel(Constant().cafeRoman, amensSubCafeList))
        }
        if (amensSubOtherList.size > 0) {
            amensMainList.add(AmensModel(Constant().otherConst, amensSubOtherList))
        }

        amensMainAdapter = AmenAdapter(amensMainList)
        binding.rvAmentites.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvAmentites.adapter = amensMainAdapter

        venueOfferAdapter = VenueOfferAdapter(subOffer)
        binding.rvOffers.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvOffers.adapter = venueOfferAdapter

        val displaymetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        mActivity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding.clAmenties.visibility = View.VISIBLE

        if (amensMainList.size in 1..2) {
            binding.clPass.height = (height * 0.55).toInt()
        } else if (amensMainList.size in 3..6) {
            binding.clPass.height = (height * 0.80).toInt()
        } else if (amensMainList.size > 6) {
            binding.clPass.height = (height * 0.80).toInt()
        } else {
            binding.clAmenties.visibility = View.GONE
            binding.clPass.height = (height * 0.45).toInt()
        }


        binding.clPass.requestLayout()

    }

    fun setAmentiesDataWithTags(
        venueType: String,
        amensSubList: ArrayList<AmensSubModel>,
        image: Drawable?,
    ) {
        if (exploreItem!!.venuecategories != null && exploreItem!!.venuecategories!!.isNotEmpty()) {
            for (i in 0 until exploreItem!!.venuecategories!!.size) {
                if (exploreItem!!.venuecategories!![i].venue_type.equals(venueType, true)) {
                    val barData = exploreItem!!.venuecategories!![i].category_name!!.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ").replace(CONSTANT.SEPRATEOR, ", ")
                    val barList = barData.split(",")
                    for (i in 0 until barList.size - 1) {
                        amensSubList.add(AmensSubModel(image, barList[i]))
                    }
                }
            }
        }
    }


    private fun openAmenFeatureBottomSheetDialog() {

        val binding: DialogAmenFeatureBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_amen_feature, null, false
        )
        val dialog = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        setFeaturesTags(binding)


        dialog.animationStyle = R.style.animation
        dialog.isFocusable = true
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.BOTTOM, 0, 0)
        dialog.contentView.bringToFront()

    }


    private var imgList: ArrayList<String> = ArrayList()
    private var pdfList: ArrayList<String> = ArrayList()
    private fun openMenuBottomSheetDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogMenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_menu, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        pdfList.clear()
        imgList.clear()

        if (exploreItem!!.menus!!.isNotEmpty()) {
            for (menu in exploreItem!!.menus!!) {
                if (menu.image!!.contains(Constant().pdfCaps)) {
                    pdfList.add(menu.image)
                } else {
                    imgList.add(menu.image)
                }
            }
        }


        if (imgList.size > 0) {
            binding.inclBarMenuImg.root.visibility = View.VISIBLE
        } else {
            binding.inclBarMenuImg.root.visibility = View.GONE
        }

        if (pdfList.size > 0) {
            binding.inclBarMenuPdf.root.visibility = View.VISIBLE
        } else {
            binding.inclBarMenuPdf.root.visibility = View.GONE
        }

        if(imgList.size>0 || pdfList.size>0){
            binding.tvNoImage.visibility = View.GONE
            binding.ivGreyMenu.visibility = View.VISIBLE
            binding.viewMenu.visibility=View.VISIBLE
        }else{
            binding.tvNoImage.visibility = View.VISIBLE
            binding.ivGreyMenu.visibility = View.GONE
            binding.viewMenu.visibility=View.GONE
        }

        binding.inclBarMenuImg.root.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    mActivity,
                    MenuImgActivity::class.java
                ).putExtra(Constant().imageList, imgList)
            )
        }

        binding.inclBarMenuPdf.root.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    mActivity,
                    MenuImgActivity::class.java
                ).putExtra(Constant().imageList, pdfList)
            )
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

    private fun openReservationBottomSheetDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogReservationsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_reservations, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val reserveAdapter =
            ExploreDetailReserveAdapter(
                exploreDetailReserveList,
                reserveListener,
                dialog,
                isTicketEmpty
            )
        binding.rvReservations.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvReservations.adapter = reserveAdapter

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

    private var reserveListener: AdapterItemClickListener<ExploreDetailReserveModel> =
        object : AdapterItemClickListener<ExploreDetailReserveModel>() {
            override fun onAdapterItemClick(item: ExploreDetailReserveModel, position: Int) {
                super.onAdapterItemClick(item, position)
                setFragmentWithStack(
                    VenueBookFragment.newInstance(exploreItem!!),
                    VenueBookFragment::class.java.simpleName
                )
            }
        }

    private fun openOffersBottomSheetDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogOffersBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_offers, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val offerListener: OfferListener<VenueListRes.Offerslive> = object : OfferListener<VenueListRes.Offerslive>() {
            override fun onCloseClick(item: VenueListRes.Offerslive, position: Int) {
                super.onCloseClick(item, position)
            }

            override fun onItemClick(item: VenueListRes.Offerslive, position: Int) {
                super.onItemClick(item, position)
                setFragmentAdd(
                    ActivatedOfferQrCodeFragment.newInstance(item),
                    ActivatedOfferQrCodeFragment::class.java.simpleName
                )
                dialog.dismiss()
            }

        }
         val offerslive: ArrayList<VenueListRes.Offerslive> = arrayListOf()

        for (item in exploreItem?.offerslive!!) {
            if (c.isOfferAvailable(item.working_day!!,item.open_time!!,item.close_time!!) == true) {
                offerslive.add(item)
            }
        }

        val liveOfferAdapter = VenueLiveOffersAdapter(offerslive,offerListener)
        binding.rvOffers.layoutManager = LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false)
        binding.rvOffers.adapter = liveOfferAdapter

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }



        private fun getDirectionList(origin: String, destination: String, mode: String) {
        if (isInternetConnect()) {
            val apiInterface: ApiInterface =
                APIClientMap().directionsClient!!.create(ApiInterface::class.java)
            val call: Call<GoogleDirectionRes?>? =
                apiInterface.getDirections(origin,
                    destination,
                    mode,
                    getString(R.string.google_maps_key))
            call!!.enqueue(object : Callback<GoogleDirectionRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<GoogleDirectionRes?>,
                    response: Response<GoogleDirectionRes?>,
                ) {
                    hideProgress()
                    if (response.body() != null && response.body()!!.routes!!.isNotEmpty()) {
                        when (mode) {
                            Constant().driving -> {
                                binding.tvCar.text =
                                    response.body()!!.routes?.get(0)!!.legs?.get(0)!!.duration!!.text
                            }
                            Constant().walking -> {
                                binding.tvWalk.text =
                                    response.body()!!.routes?.get(0)!!.legs?.get(0)!!.duration!!.text
                            }
                            Constant().transit -> {
                                binding.tvBus.text =
                                    response.body()!!.routes?.get(0)!!.legs?.get(0)!!.duration!!.text
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GoogleDirectionRes?>, t: Throwable) {
                   onApiFailure(throwable = t)
                }
            })

        }
    }

     var whatsonDialog: PopupWindow? =null
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openWhatOnBottomSheetDialog() {
        whatsonDialog = PopupWindow(mActivity)
        val binding: DialogWhatsOnBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_whats_on,
            null,
            false
        )

        binding.ivClose.setOnClickListener {
            whatsonDialog?.dismiss()
        }

        listener = object : AdapterItemClickListener<VenueListRes.Whatson>() {
            override fun onAdapterItemClick(item: VenueListRes.Whatson, position: Int) {
                super.onAdapterItemClick(item, position)
                openWhatDetailOnBottomSheetDialog(item)
            }
        }

        val today = LocalDate.now()

        val displaymetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        mActivity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        if (exploreItem!!.whatsons!!.size > 0) {
            whatsOnList.clear()
            for (i in 0 until exploreItem!!.whatsons!!.size) {

                val startDate = common.convertDateInFormat(
                    exploreItem!!.whatsons!![i].what_datetime,
                    Constant().yyyyMmDdHhMmSs,
                    Constant().yyyyMmDd
                )
                val endDate = common.convertDateInFormat(
                    exploreItem!!.whatsons!![i].end_time,
                    Constant().yyyyMmDdHhMmSs,
                    Constant().yyyyMmDd
                )
                val time1: LocalDate = LocalDate.parse(startDate)
                val time2: LocalDate = LocalDate.parse(endDate)

                if (time1 == today || time1.isBefore(today) && time2 == today || time2.isAfter(today)) {
                    whatsOnList.add(exploreItem!!.whatsons!![i])
                }

                if (whatsOnList.size > 1) {
                    binding.clPass.height = (height * 0.90).toInt()
                    binding.clPass.requestLayout()
                } else if (whatsOnList.size > 0) {
                    binding.clPass.height = (height * 0.55).toInt()
                    binding.clPass.requestLayout()
                } else {
                    binding.clPass.height = (height * 0.15).toInt()
                    binding.clPass.requestLayout()
                }

            }

            whatsOnAdapter = WhatsOnDialogAdapter(whatsOnList, listener)
            binding.rvWhatsOn.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            binding.rvWhatsOn.adapter = whatsOnAdapter

        } else {
            binding.clPass.height = (height * 0.15).toInt()
            binding.clPass.requestLayout()
        }


        whatsonDialog?.animationStyle = R.style.animation
        whatsonDialog?.contentView = binding.root
        whatsonDialog?.width = ViewGroup.LayoutParams.MATCH_PARENT
        whatsonDialog?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        whatsonDialog?.height = ViewGroup.LayoutParams.MATCH_PARENT
        whatsonDialog?.isClippingEnabled = false
        whatsonDialog?.isFocusable = true
        whatsonDialog?.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        whatsonDialog?.contentView?.bringToFront()
    }

    var whatDetailOnBottomSheetDialog:PopupWindow? = null
    private fun openWhatDetailOnBottomSheetDialog(item: VenueListRes.Whatson) {
        whatDetailOnBottomSheetDialog = PopupWindow(mActivity)
        val binding: DialogWhatsOnDetailBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(mActivity),
                R.layout.dialog_whats_on_detail,
                null,
                false
            )

        binding.ivClose.setOnClickListener {
            whatDetailOnBottomSheetDialog?.dismiss()
        }

        binding.tvWhatsOnName.text = item.title



        binding.btnBookNow.setOnClickListener {
            val intent=Intent(requireActivity(), WhatsOnBookActivity::class.java).putExtra(Constant().exploreItem, exploreItem).putExtra(Constant().item, item)
            resultLauncher.launch(intent)
        }

        val displaymetrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        mActivity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding.clPass.height = (height * 0.85).toInt()
        binding.clPass.requestLayout()

        val list: ArrayList<String> = ArrayList()

        list.clear()
        for (i in 0 until item.whatsonimages!!.size) {
            list.add(item.whatsonimages[i].image_url!!)
        }

        val imageViewAdapter = WhatsOnPreviewImageAdapter(list){

        }
        binding.rvEventImages.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEventImages.adapter = imageViewAdapter
        binding.rvEventImages.onFlingListener = null

        binding.arIndicator.attachTo(binding.rvEventImages, true)
        binding.arIndicator.numberOfIndicators = if (list.size > 1) list.size else 0

        binding.inclName.desc = item.title
        binding.inclDesc.desc = item.description

        binding.inclStartDate.desc = common.convertDateInFormat(
            item.what_datetime,
            Constant().yyyyMmDdHhMmSs,
            Constant().eeeDdMmmYyyySpace
        )
        binding.inclEndDate.desc = common.convertDateInFormat(
            item.end_time,
            Constant().yyyyMmDdHhMmSs,
            Constant().eeeDdMmmYyyySpace
        )
        binding.inclStartTime.desc = common.convertDateInFormat(
            item.what_datetime,
            Constant().yyyyMmDdHhMmSs,
            Constant().hhMmA
        )
        binding.inclEndTime.desc = common.convertDateInFormat(
            item.end_time,
            Constant().yyyyMmDdHhMmSs,
            Constant().hhMmA
        )
        if (item.music != null) {
            binding.inclEventMusic.root.visibility = View.VISIBLE
            binding.inclEventMusic.desc =
                item.music
                    .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", ")
                    .replace(CONSTANT.SEPRATEOR, ", ")
                    .ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventMusic.root.visibility = View.GONE
        }


        if (item.entertainment != null) {
            binding.inclEventEntertainment.root.visibility = View.VISIBLE
            binding.inclEventEntertainment.desc = item.entertainment
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
                .ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventEntertainment.root.visibility = View.GONE
        }

        if (item.other_music != null) {
            binding.inclEventDjLineUp.root.visibility = View.VISIBLE
            binding.inclEventDjLineUp.desc = item.other_music.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventDjLineUp.root.visibility = View.GONE
        }

        if (item.whatson_dress_code != null) {
            binding.inclEventDressCode.root.visibility = View.VISIBLE
            binding.inclEventDressCode.desc =
                item.whatson_dress_code.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventDressCode.root.visibility = View.GONE
        }
        if (item.whatson_entry_policy != null) {
            binding.inclEventEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventEntryPolicy.desc =
                item.whatson_entry_policy.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventEntryPolicy.root.visibility = View.GONE
        }

        if (item.other_information != null) {
            binding.inclEventOtherInfo.root.visibility = View.VISIBLE
            binding.inclEventOtherInfo.desc =
                item.other_information.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventOtherInfo.root.visibility = View.GONE
        }


        whatDetailOnBottomSheetDialog?.animationStyle = R.style.animation
        whatDetailOnBottomSheetDialog?.contentView = binding.root
        whatDetailOnBottomSheetDialog?.width = ViewGroup.LayoutParams.MATCH_PARENT
        whatDetailOnBottomSheetDialog?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        whatDetailOnBottomSheetDialog?.height = ViewGroup.LayoutParams.MATCH_PARENT
        whatDetailOnBottomSheetDialog?.isClippingEnabled = false
        whatDetailOnBottomSheetDialog?.isFocusable = true
        whatDetailOnBottomSheetDialog?.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        whatDetailOnBottomSheetDialog?.contentView?.bringToFront()
    }

    private fun openCloseTimeBottomSheetDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogOpenCloseBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_open_close, null, false
        )

        openCloseList.clear()
        if (exploreItem!!.timings!!.isNotEmpty()) {
            for (i in 0 until exploreItem!!.timings!!.size) {
                openCloseList.add(
                    ExploreOpenCloseModel(
                        exploreItem!!.timings!![i].working_day!!,
                        common.convertDateInFormat(
                            exploreItem!!.timings!![i].open_time,
                            Constant().HHmmss24hrs,
                            Constant().hhMmA
                        ) + " - " +
                                common.convertDateInFormat(
                                    exploreItem!!.timings!![i].close_time,
                                    Constant().HHmmss24hrs,
                                    Constant().hhMmA
                                )
                    )
                )
            }

        }

        openCloseAdapter = ExploreOpenCloseAdapter(openCloseList)
        binding.rvOpenClose.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvOpenClose.adapter = openCloseAdapter

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
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

    private fun openTrendingBottomSheetDialog() {
        val dialog = PopupWindow(mActivity)
        val binding:DialogTrendingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_trending, null, false
        )

        venueTrendsListAdapter = VenueTrendsListAdapter(exploreItem?.trending!!)
        binding.rvTrending.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvTrending.adapter = venueTrendsListAdapter

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
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



    private fun moveToLocation() {
        if (activity != null && isAdded) {
            MapsInitializer.initialize(requireActivity())
        }
    }


    private fun setUpMap(mMap: GoogleMap, latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        setMapStyle()
        setDirectionFromLocation(mMap,
            latLng,
            LatLng(PrefManager.lastLocation!!.latitude, (PrefManager.lastLocation!!.longitude)))

    }


    private fun setDirectionFromLocation(mMap: GoogleMap, destination: LatLng, source: LatLng) {
        mMap.isBuildingsEnabled = false
        var markerOptions: MarkerOptions?
        GoogleDirection.withServerKey(requireActivity().resources.getString(R.string.google_maps_key))
            .from(source)
            .to(destination)
            .avoid(AvoidType.FERRIES)
            .avoid(AvoidType.HIGHWAYS)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {
                    if (direction!!.isOK) {
                        val lineOptions = PolylineOptions()
                        markerOptions = MarkerOptions()
                        markerOptions!!.position(direction.routeList[0].overviewPolyline.pointList[0])
                        markerOptions!!.title("")
                        markerOptions!!.flat(true)
                        markerOptions!!.infoWindowAnchor(0.5f, 0.2f)
                        markerOptions!!.anchor(0.5f, 0.5f)
                        markerOptions!!.icon(Common.instance!!.bitmapFromVector(requireActivity(),
                            R.drawable.ic_current_point))
                        mMap.addMarker(markerOptions!!)
                        markerOptions = MarkerOptions()
                        markerOptions!!.position(
                            direction.routeList[0].overviewPolyline.pointList[direction.routeList[0].overviewPolyline.pointList.size - 1]
                        )
                        markerOptions!!.title("")
                        markerOptions!!.flat(true)
                        markerOptions!!.infoWindowAnchor(0.5f, 0.5f)
                        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_point))
                        mMap.addMarker(markerOptions!!)
                        lineOptions.addAll(direction.routeList[0].overviewPolyline.pointList)
                        lineOptions.width(8f)
                        lineOptions.color(Color.BLUE)
                        lineOptions.geodesic(true)
                        mMap.addPolyline(lineOptions)
                        val bounds = LatLngBounds.Builder()
                            .include(source)
                            .include(destination).build()
                        val displaySize = Point()
                        @Suppress("DEPRECATION")
                        requireActivity().windowManager.defaultDisplay.getSize(displaySize)


                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                (binding.exploreMap.width * 0.85).toInt(),
                                (binding.exploreMap.height * 0.85).toInt(),
                                100,
                            )
                        )


                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
    }

    private fun setFavoriteData(item: VenueListRes.Venue) {
        val userId: Int = PrefManager.getUser().user!!.id
        if (realmController!!.isFavorites(userId, item.id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(VenuesFavorites::class.java).equalTo(Constant().idConst, item.id)
                        .findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavorites(userId, item.id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(VenuesFavorites::class.java).equalTo(Constant().idConst, item.id)
                        .findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = VenuesFavorites(userId, item.id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }

    private fun callEventFavourite(item: VenueListRes.Venue, status: Int) {
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(PrefManager.getAccessToken(),
             MarkFavouriteReq("" + item.id, 1, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                if (isValidResponse(response)) {
                    item.isFavorite = status
                }
            }
            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    private fun openDirectionDialog() {
        val directionDialog = DirectionsDialog(
            mActivity!!,
            LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude),
            LatLng(exploreItem!!.venue_latitude!!.toDouble(),
                exploreItem!!.venue_longitude!!.toDouble())
        )
        directionDialog.show()
    }

    override val mapView: MapView
        get() = binding.exploreMap

    override fun onGoogleMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        setUpMap(mMap!!, latitude, longitude)
    }

    private fun setMapStyle() {
        try {
            val styleJson = loadJsonFromAssets("mapstyle.json")
            val success = mMap!!.setMapStyle(MapStyleOptions(styleJson))

        } catch (e: Exception) {
            Log.e("MapsActivity", "Error loading map style.", e)
        }
    }

    private fun loadJsonFromAssets(filename: String): String {
        return mActivity!!.assets.open(filename).bufferedReader().use { it.readText() }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if(whatsonDialog!=null && whatsonDialog?.isShowing == true){
                whatsonDialog?.dismiss()
            }

            if(whatDetailOnBottomSheetDialog!=null && whatDetailOnBottomSheetDialog?.isShowing == true){
                whatDetailOnBottomSheetDialog?.dismiss()
            }

            val data = result.data?.getStringExtra("screen")
            if(data.equals("mybooking")){
                setFragmentWithStack(
                    MyBookingsFragment.newInstance(Constant().whatsOnBooking),
                    MyBookingsFragment::class.java.simpleName
                )
            }
        }
    }
}
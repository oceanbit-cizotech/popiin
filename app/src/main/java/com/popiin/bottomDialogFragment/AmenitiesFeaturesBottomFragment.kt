package com.popiin.bottomDialogFragment

import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.AmenAdapter
import com.popiin.adapter.VenueOfferAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.DialogAmenFeatureBinding
import com.popiin.model.AmensModel
import com.popiin.model.AmensSubModel
import com.popiin.res.VenueListRes
import com.popiin.util.Constant

class AmenitiesFeaturesBottomFragment(var exploreItem: VenueListRes.Venue? = null) :
    BaseBottomSheetDialog<DialogAmenFeatureBinding>() {
    var bottomSheetDialog = this

    override fun getLayout(): Int {
        return R.layout.dialog_amen_feature
    }


    private lateinit var venueOfferAdapter: VenueOfferAdapter
    private var amensMainList: ArrayList<AmensModel> = ArrayList()
    private var subAmenList: ArrayList<AmensSubModel> = ArrayList()
    private var subAge: ArrayList<AmensSubModel> = ArrayList()
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
    private lateinit var amensMainAdapter: AmenAdapter

    override fun initViews() {

        if (exploreItem == null) {
            Log.e("AmenitiesFeatures", "exploreItem is null")
            return
        }

        subAmenList.clear()
        if (exploreItem!!.free_wifi == 1) {
            subAmenList.add(
                AmensSubModel(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_wifi_venue
                    ), "WiFi available"
                )
            )
        }

        if (exploreItem!!.is_outdoor_area == 1) {
            subAmenList.add(
                AmensSubModel(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_blue_outdoor
                    ), "OutDoor area"
                )
            )
        }

        subAge.clear()
        if (exploreItem!!.venue_age_group != null && exploreItem!!.venue_age_group!!.isNotEmpty()) {
            subAge.add(AmensSubModel(null, exploreItem!!.venue_age_group!!))
        }


        subOffer.clear()
        if (exploreItem!!.offers != null) {
            for (i in 0 until exploreItem!!.offers!!.size) {
                subOffer.add(exploreItem!!.offers!![i].title!!)
            }
        }


        if (subOffer.size == 0) {
            binding!!.tvVenueOffers.visibility = View.GONE
            binding!!.rvOffers.visibility = View.GONE
            binding!!.offerView.visibility = View.GONE
        } else {
            binding!!.tvVenueOffers.visibility = View.VISIBLE
            binding!!.rvOffers.visibility = View.VISIBLE
        }

        if (exploreItem!!.venue_dress_code != null) {
            if (exploreItem!!.venue_dress_code!!.isNotEmpty()) {
                binding!!.inclDressCode.root.visibility = View.VISIBLE
                binding!!.inclDressCode.desc = exploreItem!!.venue_dress_code!!
            } else {
                binding!!.inclDressCode.root.visibility = View.GONE
            }
        }

        if (exploreItem!!.venue_last_entry != null) {
            if (exploreItem!!.venue_door_policy!!.trim().isNotEmpty()) {
                binding!!.inclEntryPolicy.root.visibility = View.VISIBLE
                binding!!.inclEntryPolicy.desc = exploreItem!!.venue_door_policy!!
            } else {
                binding!!.inclEntryPolicy.root.visibility = View.GONE
            }
        }

        if (exploreItem!!.venue_last_entry != null) {
            if (exploreItem!!.venue_last_entry!!.trim().isNotEmpty()) {
                binding!!.inclLastEntryPolicy.root.visibility = View.VISIBLE
                binding!!.inclLastEntryPolicy.desc = exploreItem!!.venue_last_entry!!
            } else {
                binding!!.inclLastEntryPolicy.root.visibility = View.GONE
            }
        }


        if (exploreItem!!.venue_other_information != null) {
            if (exploreItem!!.venue_other_information!!.isNotEmpty()) {
                binding!!.inclOtherInfo.root.visibility = View.VISIBLE
                binding!!.inclOtherInfo.desc = exploreItem!!.venue_other_information!!
            } else {
                binding!!.inclOtherInfo.root.visibility = View.GONE
            }
        } else {
            binding!!.inclOtherInfo.root.visibility = View.GONE
        }

        if (binding!!.inclDressCode.root.visibility == View.GONE && binding!!.inclEntryPolicy.root.visibility == View.GONE &&
            binding!!.inclLastEntryPolicy.root.visibility == View.GONE && binding!!.inclOtherInfo.root.visibility == View.GONE
        ) {
            binding!!.tvVenueEntertainment.visibility = View.GONE
            binding!!.offerView.visibility = View.GONE
        }

        amensSubMusicList.clear()
        if (exploreItem!!.venue_music != null && exploreItem!!.venue_music!!.isNotEmpty()) {
            val musicData = exploreItem!!.venue_music!!
                .replace(
                    CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                )
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
                .replace(
                    CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                )
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
        setAmentiesDataWithTags(
            Constant().barConst,
            amensSubBarList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bar_glass_lemon)!!
        )

        amensSubPubList.clear()
        setAmentiesDataWithTags(
            Constant().pubConst,
            amensSubPubList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pub_table)!!
        )

        amensSubHotelList.clear()
        setAmentiesDataWithTags(
            Constant().hotelConst,
            amensSubHotelList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_music)!!
        )

        amensSubRestaurantList.clear()
        setAmentiesDataWithTags(
            Constant().restaurantConst,
            amensSubRestaurantList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_restro_reserve)!!
        )

        amensSubNightClubList.clear()
        setAmentiesDataWithTags(
            Constant().nightClub,
            amensSubNightClubList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_night_club_ball)!!
        )

        amensSubOtherList.clear()
        setAmentiesDataWithTags(
            Constant().otherConst,
            amensSubOtherList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_music)!!
        )

        amensSubCafeList.clear()
        setAmentiesDataWithTags(
            Constant().cafeRoman,
            amensSubCafeList,
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_coffee)!!
        )

        amensMainList.clear()
        if (subAmenList.size > 0) {
            amensMainList.add(AmensModel("Amenities", subAmenList))
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
        // amensMainList.add(AmensModel("Food", amensSubFoodList))

        amensMainAdapter = AmenAdapter(amensMainList)
        binding!!.rvAmentites.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvAmentites.adapter = amensMainAdapter

        venueOfferAdapter = VenueOfferAdapter(subOffer)
        binding!!.rvOffers.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvOffers.adapter = venueOfferAdapter

        val displaymetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding!!.clAmenties.visibility = View.VISIBLE

        if (amensMainList.size in 1..2) {
            binding!!.clPass.height = (height * 0.65).toInt()
        } else if (amensMainList.size in 3..6) {
            binding!!.clPass.height = (height * 0.85).toInt()
        } else if (amensMainList.size > 6) {
            binding!!.clPass.height = (height * 0.95).toInt()
        } else {
            binding!!.clAmenties.visibility = View.GONE
            binding!!.clPass.height = (height * 0.45).toInt()
        }

        binding!!.clPass.requestLayout()

    }

    fun setAmentiesDataWithTags(
        venueType: String,
        amensSubList: ArrayList<AmensSubModel>,
        image: Drawable?,
    ) {
        if (exploreItem!!.venuecategories != null && exploreItem!!.venuecategories!!.isNotEmpty()) {
            for (i in 0 until exploreItem!!.venuecategories!!.size) {
                if (exploreItem!!.venuecategories!![i].venue_type.equals(
                        venueType,
                        true
                    )
                ) {
                    val barData = exploreItem!!.venuecategories!![i].category_name!!
                        .replace(
                            CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                            ", "
                        ).replace(CONSTANT.SEPRATEOR, ", ")
                    val barList = barData.split(",")
                    for (element in barList) {
                        amensSubList.add(
                            AmensSubModel(
                                image, element
                            )
                        )
                    }
                }
            }
        }
    }


}
package com.popiin.bottomDialogFragment

import android.app.Dialog
import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.annotation.FILTER_MAP
import com.popiin.databinding.BottomFragmentSelectMapFilterBinding
import com.popiin.databinding.IncludeFilterBinding
import com.popiin.listener.MapFilterListener
import com.popiin.util.PrefManager

class SelectMapFilterBottomFragment(var listener: MapFilterListener) :
    BaseBottomSheetDialog<BottomFragmentSelectMapFilterBinding>() {
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_map_filter
    }

    override fun initViews() {
        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }

        if (PrefManager.getSelected() == FILTER_MAP.NONE) {
            PrefManager.setSelected(0)
        }


        initFilters(binding, dialog)
    }

    private fun initFilters(binding: BottomFragmentSelectMapFilterBinding?, dialog: Dialog?) {
        binding!!.selectedFilter = PrefManager.getSelected()
        setSelectedFilter(binding, PrefManager.getSelected())
        binding.llFilter.setOnClickListener(null)
        binding.root.setOnClickListener(null)

        setFilterClickListener(binding, binding.filterBar.root, FILTER_MAP.BAR, dialog)
        setFilterClickListener(binding, binding.filterNightclub.root, FILTER_MAP.NIGHTCLUB, dialog)
        setFilterClickListener(binding, binding.filterPub.root, FILTER_MAP.PUB, dialog)
        setFilterClickListener(binding, binding.filterRestaurant.root, FILTER_MAP.RESTAURANT, dialog)
        setFilterClickListener(binding, binding.filterCafe.root, FILTER_MAP.CAFE, dialog)
        setFilterClickListener(binding, binding.filterFavorite.root, FILTER_MAP.OFFER, dialog)
        setFilterClickListener(binding, binding.filterTrending.root, FILTER_MAP.TRENDING, dialog)
        setFilterClickListener(binding, binding.filterMostPopular.root, FILTER_MAP.MOST_POPULAR, dialog)

        setSelectedFilter(binding, binding!!.selectedFilter)


        if (PrefManager.config?.trendingFlag==0) {
            binding.filterTrending.root.visibility=View.GONE
        }
        if (PrefManager.config?.popularFlag==0) {
            binding.filterMostPopular.root.visibility=View.GONE
        }
    }

    private fun setFilterClickListener(
        binding: BottomFragmentSelectMapFilterBinding?,
        filterRoot: View,
        filter: Int,
        dialog: Dialog?
    ) {
        filterRoot.setOnClickListener {
            println("setFilterClickListener : FILTER $filter")

            binding!!.selectedFilter = filter
            PrefManager.setSelected(filter)
            setSelectedFilter(binding, filter)
        }

        binding!!.tvApply.setOnClickListener {
            dialog!!.dismiss()
            listener.onFilterItemClick(binding, binding.selectedFilter)
        }

        binding.tvReset.setOnClickListener {
            dialog!!.dismiss()
            PrefManager.setSelected(FILTER_MAP.NONE)
            listener.onFilterItemClick(binding, FILTER_MAP.NONE)
        }

    }

    private fun setSelectedFilter(binding: BottomFragmentSelectMapFilterBinding?, filter: Int) {
        when (filter) {
            FILTER_MAP.BAR -> {
                setSelectedItem(
                    binding!!.filterBar,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterNightclub,
                    binding.filterRestaurant,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.PUB -> {
                setSelectedItem(
                    binding!!.filterPub,
                    binding.filterBar,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterNightclub,
                    binding.filterRestaurant,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.CAFE -> {
                setSelectedItem(
                    binding!!.filterCafe,
                    binding.filterPub,
                    binding.filterBar,
                    binding.filterFavorite,
                    binding.filterNightclub,
                    binding.filterRestaurant,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.RESTAURANT -> {
                setSelectedItem(
                    binding!!.filterRestaurant,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterNightclub,
                    binding.filterBar,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.OFFER -> {
                setSelectedItem(
                    binding!!.filterFavorite,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterBar,
                    binding.filterNightclub,
                    binding.filterRestaurant,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.NIGHTCLUB -> {
                setSelectedItem(
                    binding!!.filterNightclub,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterBar,
                    binding.filterRestaurant,
                    binding.filterTrending,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.TRENDING -> {
                setSelectedItem(
                    binding!!.filterTrending,
                    binding.filterNightclub,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterBar,
                    binding.filterRestaurant,
                    binding.filterMostPopular
                )
            }
            FILTER_MAP.MOST_POPULAR -> {
                setSelectedItem(
                    binding!!.filterMostPopular,
                    binding.filterNightclub,
                    binding.filterPub,
                    binding.filterCafe,
                    binding.filterFavorite,
                    binding.filterBar,
                    binding.filterRestaurant,
                    binding.filterTrending,
                )
            }
        }

    }

    private fun setSelectedItem(
        filterOne: IncludeFilterBinding,
        filterTwo: IncludeFilterBinding,
        filterThree: IncludeFilterBinding,
        filterFour: IncludeFilterBinding,
        filterFive: IncludeFilterBinding,
        filterSix: IncludeFilterBinding,
        filterSeven: IncludeFilterBinding,
        filterEight: IncludeFilterBinding
    ) {
        filterOne.isSelected = true
        filterTwo.isSelected = false
        filterThree.isSelected = false
        filterFour.isSelected = false
        filterFive.isSelected = false
        filterSix.isSelected = false
        filterSeven.isSelected = false
        filterEight.isSelected = false
    }


}
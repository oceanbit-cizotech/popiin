package com.popiin.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.ZoomImageViewPagerAdapter
import com.popiin.databinding.FragmentImagePreviewBinding
import com.popiin.util.Constant

class ImagePreviewFragment : BaseFragment<FragmentImagePreviewBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_image_preview
    }

    override fun initViews() {
        binding.ivBack.setOnClickListener {
            mActivity?.supportFragmentManager?.popBackStack()
        }

        val adapter = ZoomImageViewPagerAdapter(list)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(currentPosition,true)


        binding.tvCurrentPosition.text = Constant().one
        binding.tvTotalSize.text = list.size.toString()

        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                binding.tvCurrentPosition.text = (position + 1).toString()
                binding.tvTotalSize.text = list.size.toString()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    companion object {
        var currentPosition = 0
        var list: List<String> = emptyList()
        fun newInstance(listImages: List<String>, position : Int ): ImagePreviewFragment {
            list = listImages
            currentPosition =  position
            return ImagePreviewFragment()
        }
    }
}

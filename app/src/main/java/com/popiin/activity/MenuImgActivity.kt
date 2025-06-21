package com.popiin.activity

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.adapter.MenuImgAdapter
import com.popiin.databinding.ActivityMenuImgBinding
import com.popiin.util.Constant


class MenuImgActivity : BaseActivity<ActivityMenuImgBinding>() {
    var imageList: ArrayList<String> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.activity_menu_img
    }

    override fun initViews() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }


        @Suppress("DEPRECATION")
        imageList = intent.getSerializableExtra(Constant().imageList) as ArrayList<String>


        binding.tvCurrentPosition.text = Constant().one
        binding.tvTotalSize.text = imageList.size.toString()

        drawPageSelectionIndicators(0)

        val menuImgAdapter = MenuImgAdapter(imageList)
        binding.vpImages.adapter = menuImgAdapter

        binding.vpImages.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                binding.tvCurrentPosition.text = (position + 1).toString()
                binding.tvTotalSize.text = imageList.size.toString()
                drawPageSelectionIndicators(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawPageSelectionIndicators(mPosition: Int) {
        binding.viewPagerCountDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(imageList.size)
        for (i in 0 until imageList.size) {
            dots[i] = ImageView(mActivity)
            if (i == mPosition) dots[i]?.setImageDrawable(ContextCompat.getDrawable(mActivity,
                R.drawable.ic_page_selected))
            else dots[i]?.setImageDrawable(ContextCompat.getDrawable(mActivity,
                R.drawable.ic_page_unselected))
            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 0, 10, 0)
            binding.viewPagerCountDots.addView(dots[i], params)
        }
    }
}
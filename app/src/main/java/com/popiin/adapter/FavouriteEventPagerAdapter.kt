package com.popiin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.popiin.R
import com.popiin.listener.AdapterEventListner
import com.popiin.res.FavouriteEventsRes

class FavouriteEventPagerAdapter(
    var context: Context,
    var images: ArrayList<FavouriteEventsRes.Images>?,
    var event: FavouriteEventsRes.FavouriteEvent,
    var adapterEventListener: AdapterEventListner<FavouriteEventsRes.FavouriteEvent?>,
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return images!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }


    override fun instantiateItem( container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.adapter_event_pager, null)
        val imageView: ImageView = view.findViewById(R.id.img_image)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(container.context).load(images!![position].image).into(imageView)
        imageView.setOnClickListener {
            adapterEventListener.onItemClick(event, position)
        }
        val viewPager = container as ViewPager
        viewPager.addView(view, 0)
        return view
    }

    override fun destroyItem( container: ViewGroup, position: Int,  `object`: Any) {
        val viewPager: ViewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}
package com.popiin.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.jvm.JvmOverloads
import androidx.collection.SimpleArrayMap
import com.popiin.views.IndexAdapter
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.popiin.views.ZSideBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.popiin.R
import com.popiin.views.Indexable
import java.lang.IllegalArgumentException

class ZSideBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val indexMap = SimpleArrayMap<Int, String?>()
    private var recyclerView: RecyclerView? = null
    private var choose = 0 // 选中
    private var oldChoose = -1
    private val paint = Paint()
    private var offsetY = 0
    private var singleHeight = 0
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as ViewGroup).clipChildren = false
    }

    fun setupWithRecycler(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        val adapter = recyclerView.adapter
            ?: throw IllegalArgumentException("recyclerView do not set adapter")
        require(adapter is IndexAdapter) { "recyclerView adapter not implement IndexAdapter" }
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                initIndex(adapter)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                initIndex(adapter)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                initIndex(adapter)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                initIndex(adapter)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                initIndex(adapter)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                initIndex(adapter)
            }
        })
        initIndex(adapter)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        oldChoose = choose
        val c = ((y - offsetY) / singleHeight).toInt()
        when (action) {
            MotionEvent.ACTION_UP -> {
                setBackgroundColor(0x00000000)
                // choose = -1;
                invalidate()
            }
            else -> {
                setBackgroundColor(0x00000000)
                if (oldChoose != c) {
                    if (c >= 0 && c < indexMap.size()) {
                        val position = indexMap.keyAt(c)
                        recyclerView!!.layoutManager!!.scrollToPosition(position)
                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (indexMap.isEmpty) return
        val height = height
        val width = width
        singleHeight = height / indexMap.size()
        val dp12 = dip2px(20f)
        val dp24 = dip2px(24f)
        singleHeight = if (singleHeight > dp24) dp24 else singleHeight
        offsetY = (height - singleHeight * indexMap.size()) / 2
        for (i in 0 until indexMap.size()) {
            paint.isAntiAlias = true
            paint.textSize = dp12.toFloat()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                paint.typeface = resources.getFont(R.font.inter_bold)
            }
            val colorId = if (i == choose) R.color.colorBlue else R.color.colorBlack
            paint.color = ContextCompat.getColor(context, colorId)
            val xPos = width / 2 - paint.measureText(indexMap[indexMap.keyAt(i)]) / 2
            val yPos = offsetY + singleHeight * (i + 0.2f)
            canvas.drawText(indexMap[indexMap.keyAt(i)]!!, xPos, yPos, paint)
        }
    }

    private fun initIndex(adapter: RecyclerView.Adapter<*>) {
        indexMap.clear()
        for (i in 0 until adapter.itemCount) {
            val item = (adapter as IndexAdapter).getItem(i)
            if (i == 0) {
                indexMap.put(i, item!!.index)
            } else {
                val preItem = (adapter as IndexAdapter).getItem(i - 1)
                if (preItem!!.index != item!!.index) {
                    indexMap.put(i, item.index)
                }
            }
        }
        invalidate()
    }

    companion object {
        private fun dip2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}
package com.tsai.shakeit

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout

class ScalableTabLayout : TabLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tabLayout = getChildAt(0) as ViewGroup
        val childCount = tabLayout.childCount
        if (childCount > 0) {
            val widthPixels = MeasureSpec.getSize(widthMeasureSpec)

            // Every Tab's width = screen width / Tab's amount
            val tabMinWidth = widthPixels / childCount

            /**
             * If remainderPixel != 0 means tab's width can't fill the screen so loop plus 1 pixel
             * until remainderPixel = 0
             */
            var remainderPixels = widthPixels % childCount
            tabLayout.forEach {
                if (remainderPixels > 0) {
                    it.minimumWidth = tabMinWidth + 1
                    remainderPixels--
                } else {
                    it.minimumWidth = tabMinWidth
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

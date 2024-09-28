/**
 * Инструменты для использования индикатора с ViewPager
 *
 * @author aa.prischep
 */
package ru.tensor.sbis.design.view_ext.indicator

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

/**@SelfDocumented*/
fun SbisIndicator.attachToViewPager(viewPager: ViewPager) {
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) = Unit
        override fun onPageSelected(position: Int) {
            selectedPosition = position to 0f
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            selectedPosition = position to positionOffset
        }
    })
}

/**@SelfDocumented*/
fun SbisIndicator.attachToViewPager(viewPager: ViewPager2) {
    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(p0: Int) = Unit
        override fun onPageSelected(position: Int) {
            selectedPosition = position to 0f
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            selectedPosition = position to positionOffset
        }
    })
}
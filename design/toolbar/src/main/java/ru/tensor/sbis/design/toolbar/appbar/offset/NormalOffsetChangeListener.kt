package ru.tensor.sbis.design.toolbar.appbar.offset

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * Реализация [AppBarLayout.OnOffsetChangedListener], которая инкапсулирует логику нормализации offset
 *
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
internal class NormalOffsetChangeListener(
    private val observers: List<NormalOffsetObserver>,
    private val getMaxOffset: () -> Int
) : AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        var absOffset = offset.absoluteValue
        val offsetLimit = getMaxOffset()

        absOffset = min(absOffset, offsetLimit)

        val normalOffset = 1F - (
            offsetLimit.toFloat()
                .takeUnless { it == 0F }
                ?.let { absOffset.div(it) }
                ?: 0F
            )
        for (observer in observers) {
            observer.onOffsetChanged(normalOffset)
        }
    }
}
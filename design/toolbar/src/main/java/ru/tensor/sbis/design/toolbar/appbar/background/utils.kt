/**
 * Инструменты для работы с фоном в SbisAppBarLayout
 *
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
package ru.tensor.sbis.design.toolbar.appbar.background

import android.view.View
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.DraweeView

/**
 * Определение стратегии подстановки фона по целевой [view]
 */
internal fun resolveBackgroundStrategy(
    view: View,
    onAspectRatioChanged: AspectRatioChangeListener?
): BackgroundStrategy = when (view) {
    is DraweeView<*> -> {
        require(view.hierarchy is GenericDraweeHierarchy) {
            "Unsupported DraweeHierarchy ${view.hierarchy::class}"
        }
        @Suppress("UNCHECKED_CAST")
        DraweeViewBackgroundStrategy(
            view as DraweeView<GenericDraweeHierarchy>,
            backgroundAspectRatioChangedCallback = onAspectRatioChanged
        )
    }

    else -> DefaultBackgroundStrategy(view)
}
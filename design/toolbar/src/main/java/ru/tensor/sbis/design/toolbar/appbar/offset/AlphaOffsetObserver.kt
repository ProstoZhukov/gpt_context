package ru.tensor.sbis.design.toolbar.appbar.offset

import android.view.View
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

internal const val MAX_ALPHA = 1f
internal const val MIN_ALPHA = 0f
internal const val MAX_POSITION_LIMIT = 0.70
internal const val MIN_POSITION_LIMIT = MAX_ALPHA - MAX_POSITION_LIMIT

/**
 * Реализация [AppBarLayout.OnOffsetChangedListener],
 * которая показывает или скрывает элменты, плавно изменяя их прозрачность
 *
 * @param views пары, которые говорят, что если данное `view to true`, то оно отображается на раскрытом
 * [SbisAppBarLayout] и соответственно на свернутом [SbisAppBarLayout] не отображается
 *
 * @author ma.kolpakov
 * @since 12/27/2019
 */
class AlphaOffsetObserver(
    vararg val views: Pair<View, Boolean>
) : NormalOffsetObserver {

    override fun onOffsetChanged(position: Float) {
        for ((view, isVisible) in views) {
            view.alpha = if (isVisible) calculateAlpha(position) else calculateAlpha(MAX_ALPHA - position)
        }
    }

    private fun calculateAlpha(position: Float) =
        when {
            position > MAX_POSITION_LIMIT -> MAX_ALPHA
            position < MIN_POSITION_LIMIT -> MIN_ALPHA
            else -> position
        }
}
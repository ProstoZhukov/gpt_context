/**
 * Набор общих утилит для работы с шапкой.
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.topNavigation.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import ru.tensor.sbis.design.custom_view_tools.utils.layout

/**
 * Получить видимость nullable view.
 */
internal inline var View?.isVisibleNullable
    get() = this?.isVisible ?: false
    set(value) {
        this?.visibility = if (value) View.VISIBLE else View.GONE
    }

/**
 * Узнать, не видима ли nullable view.
 */
internal inline val View?.isNotVisibleNullable
    get() = !isVisibleNullable

/**
 * Получить ширину view вместе с margin на основе текущей видимости.
 */
internal fun <T : View> T?.getWidthWithMargins(internalWidth: (T) -> Int) = if (this.isVisibleNullable) {
    this?.run { internalWidth(this) + paddingStart + paddingEnd + marginStart + marginEnd } ?: 0
} else {
    0
}

/**
 * Расположить View в родителе и вернуть высоту.
 */
internal fun View.layoutContentAndGetHeight(left: Int, top: Int): Int {
    val parentMinHeight = (parent as? View)?.minimumHeight ?: 0
    if (parentMinHeight > measuredHeight) {
        layout(left, ((parentMinHeight - measuredHeight) / 2) + top)
    } else {
        layout(left, top)
    }
    return measuredHeight
}

/**
 * Измерить View и вернуть высоту.
 */
internal fun View.measureAndGetHeight(mainContentWidthSpec: Int, mainContentHeightSpec: Int): Int {
    measure(mainContentWidthSpec, mainContentHeightSpec)
    return measuredHeight
}

/** @SelfDocumented */
internal fun ViewGroup.safeAddView(view: View) {
    (view.parent as? ViewGroup)?.removeView(view)
    addView(view)
}

/** @SelfDocumented */
internal fun ViewGroup.safeAddView(view: View, params: ViewGroup.LayoutParams) {
    (view.parent as? ViewGroup)?.removeView(view)
    addView(view, params)
}
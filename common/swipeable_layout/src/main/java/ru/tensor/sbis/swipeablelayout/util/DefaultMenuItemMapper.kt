package ru.tensor.sbis.swipeablelayout.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.ColorfulMenuItemIcon
import ru.tensor.sbis.swipeablelayout.DefaultMenuItem
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeMenuItemVm

/** @SelfDocumented */
internal fun DefaultMenuItem.toItemVm(context: Context) = SwipeMenuItemVm(
    id,
    clickAction,
    null,
    colorfulItemIcon?.getBackgroundColor(context),
    getSwipeIconColor(iconColor, context),
    getIcon(colorfulItemIcon, icon, context),
    null,
    label.orEmpty(),
    !isLongLabel,
    hasLabel,
    isClickPostponedUntilMenuClosed,
    colorfulItemIcon != null,
    autotestsText
)

/** @SelfDocumented */
@ColorInt
internal fun ColorfulMenuItemIcon.getBackgroundColor(context: Context): Int? {
    context.withStyledAttributes(attrs = intArrayOf(colorAttr)) {
        return getColor(0, Color.TRANSPARENT).takeUnless { it == Color.TRANSPARENT }
    }
    return null
}

/** @SelfDocumented */
internal fun ColorfulMenuItemIcon.getIcon(context: Context): String {
    context.withStyledAttributes(attrs = intArrayOf(iconAttr)) {
        return checkNotNullSafe(getString(0)) {
            "Icon ${this@getIcon} value is not specified in SwipeableLayout theme"
        } ?: ""
    }
    return ""
}

/**
 * Использует цвет иконки пункта свайп-меню из атрибута [R.attr.SwipeableLayout_iconTextColor]. Если атрибут не
 * задан, то используется [customColor]
 */
@ColorInt
internal fun getSwipeIconColor(@ColorRes customColor: Int, context: Context): Int {
    context.withStyledAttributes(attrs = intArrayOf(R.attr.SwipeableLayout_iconTextColor)) {
        return getColor(0, Color.MAGENTA).takeUnless { it == Color.MAGENTA } ?: ContextCompat.getColor(
            context,
            customColor
        )
    }
    return Color.MAGENTA
}

/**
 * Использует иконку пункта свайп-меню, указанную в атрибуте из [icon], а если это невозможно, то [legacyIcon].
 */
private fun getIcon(icon: ColorfulMenuItemIcon?, legacyIcon: String, context: Context): String {
    return icon?.getIcon(context)?.takeUnless { it.isEmpty() } ?: legacyIcon
}

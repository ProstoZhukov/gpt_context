package ru.tensor.sbis.swipeablelayout.api.menu

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.swipeable_layout.R

/**
 * Стиль пункта свайп-меню.
 * Определяет какой из предусмотренных цветов фона использовать.
 *
 * @author us.bessonov
 */
enum class SwipeItemStyle(@AttrRes private val backgroundColor: Int) {
    ORANGE(R.attr.SwipeableLayout_itemBackgroundOrange),
    BLUE(R.attr.SwipeableLayout_itemBackgroundBlue),
    GREEN(R.attr.SwipeableLayout_itemBackgroundGreen),
    RED(R.attr.SwipeableLayout_itemBackgroundRed),
    GREY(R.attr.SwipeableLayout_itemBackgroundGrey),
    PINK(R.attr.SwipeableLayout_itemBackgroundPink);

    /** @SelfDocumented */
    @ColorInt
    internal fun getBackgroundColor(context: Context) = context.getDataFromAttrOrNull(backgroundColor)!!
}
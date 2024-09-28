package ru.tensor.sbis.swipeablelayout.api.menu

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.swipeable_layout.R

/**
 * Стиль пункта свайп-меню с текстом.
 * Определяет используемый цвет фона.
 *
 * @author us.bessonov
 */
enum class SwipeTextItemStyle(@AttrRes private val backgroundColor: Int) {
    /**
     * Используется для обычных операций
     */
    DEFAULT(R.attr.SwipeableLayout_textItemBackgroundDefault),

    /**
     * Используется для операции удаления и прочих, требующих повышенного внимания
     */
    DANGER(R.attr.SwipeableLayout_textItemBackgroundDanger);

    /** @SelfDocumented */
    @ColorInt
    internal fun getBackgroundColor(context: Context) = context.getDataFromAttrOrNull(backgroundColor)!!
}
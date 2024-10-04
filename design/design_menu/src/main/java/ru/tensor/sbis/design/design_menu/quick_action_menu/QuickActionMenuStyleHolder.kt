package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.design_menu.R
import ru.tensor.sbis.design.design_menu.view.shadow.MenuShadowStyleHolder
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * StyleHolder для компонента меню быстрых действий.
 *
 * @author ra.geraskin
 */
internal class QuickActionMenuStyleHolder : MenuShadowStyleHolder {

    /** Максимальный размер текста. */
    @Dimension
    var maxTextSize = 0
        private set

    /** Отступ картинки. */
    @Dimension
    var imageOffset = 0
        private set

    /** Размер иконки. */
    @Dimension
    var iconSize = 0
        private set

    /** Цвет иконки. */
    @ColorInt
    var iconColor: Int = Color.MAGENTA
        private set

    /** Цвет текста. */
    @ColorInt
    var textColor: Int = Color.MAGENTA
        private set

    /** Цвет фона элемента. */
    @ColorInt
    var itemBackgroundColor: Int = Color.MAGENTA
        private set

    /** Цвет фона элемента при нажатии. */
    @ColorInt
    var itemBackgroundPressedColor: Int = Color.MAGENTA
        private set

    /** Максимальная ширина элемента меню. */
    @Dimension
    var maxItemWidth = 0
        private set

    /** Минимальная ширина элемента меню. */
    @Dimension
    var minItemWidth = 0
        private set

    /** Вертикальный и горизонтальный отступ между элементами. */
    @Dimension
    var quickActionMenuItemOffset = 0
        private set

    /** Внутренние отступы в строке меню */
    @Dimension
    var quickActionMenuItemInnerOffset = 0
        private set

    /** Скругление краёв элементов меню. */
    @Dimension
    var quickActionMenuCornerRadius = 0
        private set

    /** Высота элемента меню. */
    @Dimension
    var quickActionMenuItemHeight = 0
        private set

    /** Цвет затенения на границах списка меню быстрых действий. */
    @ColorInt
    override var shadowColor = 0
        private set

    /** Высота затенения на границах списка меню быстрых действий. */
    @ColorInt
    override var shadowHeight = 0
        private set

    /** Прочитать значения из стилевых атрибутов и записать в соответствующие поля. */
    fun loadStyle(
        context: Context,
        @DimenRes itemWidthRes: Int? = null
    ) = context.apply {

        Offset.M.getDimenPx(context).let { offsetM ->
            imageOffset = offsetM
            quickActionMenuItemOffset = offsetM
            quickActionMenuItemInnerOffset = offsetM
        }

        shadowColor = StyleColor.UNACCENTED.getAdaptiveBackgroundColor(context)
        shadowHeight = InlineHeight.X5S.getDimenPx(context)

        maxItemWidth = itemWidthRes?.let { widthRes -> resources.getDimensionPixelSize(widthRes) }
            ?: resources.getDimensionPixelSize(R.dimen.quick_action_menu_width)
        minItemWidth = maxItemWidth
        quickActionMenuItemHeight = InlineHeight.S.getDimenPx(context)
        quickActionMenuCornerRadius = quickActionMenuItemHeight / 2

        maxTextSize = FontSize.XL.getScaleOnDimenPx(context)
        iconSize = IconSize.X3L.getScaleOnDimenPx(context)

        iconColor = IconColor.DEFAULT.getValue(context)
        textColor = TextColor.DEFAULT.getValue(context)

        itemBackgroundColor = BackgroundColor.DEFAULT.getValue(context)
        itemBackgroundPressedColor = BackgroundColor.ACTIVE.getValue(context)
    }
}
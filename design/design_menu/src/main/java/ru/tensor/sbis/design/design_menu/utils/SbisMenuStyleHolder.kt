package ru.tensor.sbis.design.design_menu.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.view.shadow.MenuShadowStyleHolder
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.design_menu.viewholders.MenuAdapter
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel

/**
 * Класс содержащий ресурсы для ViewHolder-ов [MenuAdapter].
 *
 * @author ra.geraskin
 */
internal class SbisMenuStyleHolder private constructor() : MenuShadowStyleHolder {

    /** Размер иконки. */
    @Dimension
    var iconSize: Float = 0f
        private set

    /** Цвет иконки. */
    @ColorInt
    var iconColor: Int = Color.MAGENTA
        private set

    /** Цвет фона элемента. */
    @ColorInt
    var itemBackgroundColor: Int = Color.MAGENTA
        private set

    /** Цвет фона элемента при нажатии. */
    @ColorInt
    var itemBackgroundPressedColor: Int = Color.MAGENTA
        private set

    @Dimension
    var itemMinHeight: Int = 0
        private set

    /** Цвет тени, отображаемой на верхней и нижней границе списка элементов меню при отображении в шторке. */
    @ColorInt
    override var shadowColor: Int = 0
        private set

    /** Высота тени, отображаемой на верхней и нижней границе списка элементов меню при отображении в шторке. */
    @ColorInt
    override var shadowHeight: Int = 0
        private set

    /** Размер текста заголовка. */
    @Dimension
    var titleSize: Float = 0f
        private set

    /** Цвет текста заголовка. */
    @ColorInt
    var titleColor: Int = Color.MAGENTA
        private set

    /** Максимальное число строк текста в заголовке. */
    var titleMaxLines: Int = 0
        private set

    /** Размер текста комментария. */
    @Dimension
    var commentSize: Float = 0f
        private set

    /** Цвет текста комментария. */
    @ColorInt
    var commentColor: Int = Color.MAGENTA
        private set

    /** Максимальное количество строк текста комментария. */
    var commentMaxLines: Int = 0
        private set

    /** Размер маркера выделения. */
    @Dimension
    var markerSize: Float = 0f
        private set

    /** Вид иконки. */
    var menuSelectionStyle: MenuSelectionStyle = MenuSelectionStyle.CHECKBOX
        private set

    /** Максимальная ширина элемента меню. */
    @Dimension
    var maxItemWidth = 0
        private set

    /** Минимальная ширина элемента меню. */
    @Dimension
    var minItemWidth = 0
        private set

    /** Отступ дочерних элементов во вложенном меню. */
    @Dimension
    var hierarchyOffset = 0
        private set

    /** Цвет иконки стрелки элемента меню, открывающего подменю. */
    @ColorInt
    var subMenuArrowIconColor: Int = Color.MAGENTA
        private set

    /** Размер иконки стрелки элемента меню, открывающего подменю. */
    @Dimension
    var subMenuArrowIconSize: Float = 0f
        private set

    /** Иконка стрелки элемента меню, открывающего подменю. */
    var subMenuArrowIcon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_MarkCRightLight
        private set

    /** Высота элемента разделителя. */
    @Dimension
    var dividerHeight: Int = 0
        private set

    /** @SelfDocumented */
    @Dimension
    var dividerMarginTop: Int = 0
        private set

    /** @SelfDocumented */
    @Dimension
    var dividerMarginBottom: Int = 0
        private set

    companion object {

        /**
         * Создать [SbisMenuStyleHolder] для меню, отображающегося в контейнере.
         */
        fun createStyleHolderForContainer(
            context: Context,
            selectionStyle: MenuSelectionStyle,
            @DimenRes maxWidthRes: Int? = null
        ) = SbisMenuStyleHolder().apply {
            menuSelectionStyle = selectionStyle

            maxItemWidth = (
                maxWidthRes?.let {
                    getCustomDimension(context.resources, it)
                } ?: context.getDimenPx(R.attr.popupMaxWidthMenuPopup)
                ).coerceAtMost(context.resources.displayMetrics.widthPixels - (Offset.M.getDimenPx(context) * 2))

            if (maxWidthRes != null) minItemWidth = maxItemWidth

            iconSize = IconSize.X3L.getDimen(context)
            titleSize = FontSize.XL.getScaleOnDimen(context)
            markerSize = IconSize.X2S.getDimen(context)
            commentSize = FontSize.XS.getScaleOnDimen(context)
            subMenuArrowIconSize = IconSize.X3L.getDimen(context)

            iconColor = StyleColor.SECONDARY.getIconColor(context)
            titleColor = TextColor.DEFAULT.getValue(context)
            commentColor = StyleColor.UNACCENTED.getTextColor(context)
            subMenuArrowIconColor = StyleColor.SECONDARY.getIconColor(context)

            itemBackgroundColor = BackgroundColor.STICKY.getValue(context)
            itemBackgroundPressedColor = BackgroundColor.ACTIVE.getValue(context)

            hierarchyOffset = Offset.XL.getDimenPx(context)

            shadowColor = OtherColor.SHADOW.getValue(context)
            shadowHeight = InlineHeight.X8S.getDimenPx(context)

            itemMinHeight = InlineHeight.S.getDimenPx(context)

            titleMaxLines = 2
            commentMaxLines = 2

            dividerHeight = InlineHeight.X5S.getDimenPx(context)
            dividerMarginTop = Offset.XS.getDimenPx(context)
            dividerMarginBottom = Offset.X3S.getDimenPx(context)
        }

        /**
         * Создать [SbisMenuStyleHolder] для меню, отображающегося в [MovablePanel] (шторке).
         */
        fun createStyleHolderForPanel(context: Context, selectionStyle: MenuSelectionStyle) =
            createStyleHolderForContainer(context, selectionStyle).apply {
                itemBackgroundColor = BackgroundColor.STACK.getValue(context)
                maxItemWidth = ViewGroup.LayoutParams.MATCH_PARENT
                minItemWidth = ViewGroup.LayoutParams.MATCH_PARENT
            }

        /**
         * Получить значение из ресурса, даже если ресурс ссылается на другой ресурс.
         */
        private fun getCustomDimension(resources: Resources, @DimenRes customContentDimensionRes: Int): Int? {
            if (customContentDimensionRes == ResourcesCompat.ID_NULL) return null
            val typedValue = TypedValue()
            resources.getValue(customContentDimensionRes, typedValue, true)
            return if (typedValue.resourceId == R.dimen.match_parent) Int.MAX_VALUE
            else resources.getDimensionPixelSize(customContentDimensionRes)
        }
    }

}
package ru.tensor.sbis.design.design_menu.model

import android.content.Context
import androidx.annotation.AttrRes
import com.mikepenz.iconics.typeface.IIcon
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Стиль отображения маркера при единичном выборе.
 *
 * @author ra.geraskin
 */
enum class MenuSelectionStyle(

    /**
     * Цвет иконки маркера.
     */
    @AttrRes
    private val iconColor: Int,

    /**
     * Иконка маркера.
     */
    private val icon: IIcon,

    /**
     * Отступы для иконки. Необходимы для соответствия отступов с макетом.
     */
    private val padding: Int

) {

    /** Галкой. */
    CHECKBOX(
        iconColor = R.attr.iconColor,
        icon = SbisMobileIcon.Icon.smi_checked,
        padding = 0
    ),

    /** Цветной точкой. */
    MARKER(
        iconColor = R.attr.markerColor,
        icon = SbisMobileIcon.Icon.smi_markerCircle,
        padding = 5
    );

    /**
     * @SelfDocumented
     */
    internal fun getIconColor(context: Context) = context.getThemeColorInt(iconColor)

    /**
     * @SelfDocumented
     */
    internal fun getIcon() = icon

    /**
     * @SelfDocumented
     */
    internal fun getTextLayoutPadding(context: Context) = context.dpToPx(padding).let {
        TextLayout.TextLayoutPadding(it, it, it, it)
    }
}
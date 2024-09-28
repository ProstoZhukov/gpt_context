package ru.tensor.sbis.design.tab_panel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Ресурсы для [TabPanelItemView]
 *
 * @author ai.abramenko
 */
internal class TabPanelStyleHolder(private val context: Context) {

    @Dimension
    val size = context.resources.getDimension(R.dimen.design_tab_panel_item_size)

    @Px
    val sizePx = context.resources.getDimensionPixelSize(R.dimen.design_tab_panel_item_size)

    @Dimension
    val heightPx = context.resources.getDimensionPixelSize(R.dimen.design_tab_panel_height)

    @Dimension
    val textBottomMargin = Offset.XS.getDimen(context)

    @Dimension
    val iconBottomMargin = Offset.X3S.getDimen(context)

    val textLayout: TextLayout = TextLayout {
        paint.typeface = TypefaceManager.getRobotoMediumFont(context)
        paint.textSize = FontSize.X2S.getScaleOffDimenPx(context).toFloat()
        paint.color = TextColor.DEFAULT.getValue(context)
        includeFontPad = false
        ellipsize = TextUtils.TruncateAt.END
        maxLines = 1
        maxWidth = sizePx
    }

    val iconPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        textSize = IconSize.X4L.getDimen(context)
    }

    val iconColors: ColorStateList = AppCompatResources.getColorStateList(
        context,
        R.color.tab_panel_icon_color
    )

    val iconBackgroundDrawable: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.tab_panel_icon_circle)
}
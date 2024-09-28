package ru.tensor.sbis.richtext.converter.cfg.style

import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.R

/**
 * Набор стилей для отрисовки декорированных ссылок.
 *
 * @param context темизированный контекст
 *
 * @author am.boldinov
 */
class DecoratedLinkStyle(private val context: SbisThemedContext) {

    val small by lazy(LazyThreadSafetyMode.NONE) { Small() }

    val medium by lazy(LazyThreadSafetyMode.NONE) { Medium() }

    inner class Small {
        val textSize = FontSize.M.getScaleOffDimen(context)
        val textTypeface = TypefaceManager.getRobotoRegularFont(context)
        val textColor = StyleColor.SECONDARY.getTextColor(context)

        val imageMarginRight = Offset.X2S.getDimenPx(context)

        val maxWidth = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_inline_max_width)
    }

    inner class Medium {
        val titleSize = FontSize.M.getScaleOffDimen(context)
        val titleTypeface = TypefaceManager.getRobotoRegularFont(context)
        val titleColor = StyleColor.SECONDARY.getTextColor(context)

        val subtitleSize = FontSize.XS.getScaleOffDimen(context)
        val subtitleTypeface = TypefaceManager.getRobotoRegularFont(context)
        val subtitleColor = TextColor.DEFAULT.getValue(context)

        val detailsSize = FontSize.X3S.getScaleOffDimen(context)
        val detailsTypeface = TypefaceManager.getRobotoRegularFont(context)
        val detailsColor = TextColor.LABEL.getValue(context)

        val additionalSize = FontSize.X3S.getScaleOffDimen(context)
        val additionalTypeface = TypefaceManager.getRobotoRegularFont(context)
        val additionalColor = TextColor.LABEL.getValue(context)

        val backgroundColor =
            StyleColor.SECONDARY.getSameBackgroundColor(context).withOpacity(0.5f).let { first ->
                BackgroundColor.DEFAULT.getValue(context).withOpacity(0.5f).let { second ->
                    ColorUtils.blendARGB(first, second, 0.5f)
                } // 50% opacity
            }
        val backgroundCornerRadius = BorderRadius.X3S.getDimen(context)
        val backgroundStrokeWidth = BorderThickness.S.getDimenPx(context)
        val backgroundStrokeColor = IconColor.LABEL.getValue(context).withOpacity(0.22f) // 22% opacity

        val padding = Offset.XS.getDimenPx(context)
        val verticalMargin = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_vertical_margin)
        val imageMarginRight = Offset.XS.getDimenPx(context)
        val titleMarginHorizontal = Offset.X2S.getDimenPx(context)

        val minWidth = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_min_width)
        val maxWidth = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_max_width)
    }
}

/**
 * Набор стилей для отрисовки изображений внутри декорированных ссылок.
 *
 * @param context темизированный контекст
 */
internal class DecoratedImageStyle(private val context: SbisThemedContext) {
    val placeholderBackgroundColor = BackgroundColor.ACTIVE.getValue(context).withOpacity(0.8f) // 80% opacity
    val placeholderColor = IconColor.LABEL.getValue(context).withOpacity(0.7f) // 70% opacity
    val placeholderIcon = SbisMobileIcon.Icon.smi_link

    val small by lazy(LazyThreadSafetyMode.NONE) { Small() }

    val medium by lazy(LazyThreadSafetyMode.NONE) { Medium() }

    inner class Small {
        val size = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_inline_image_side_size)
        val cornerRadius = context.resources.getDimension(R.dimen.richtext_decorated_link_inline_image_corner_radius)
    }

    inner class Medium {
        val size = context.resources.getDimensionPixelSize(R.dimen.richtext_decorated_link_image_side_size)
        val cornerRadius = BorderRadius.X3S.getDimen(context)
    }
}

private fun Int.withOpacity(opacity: Float): Int {
    return ColorUtils.setAlphaComponent(this, (255 * opacity).toInt())
}
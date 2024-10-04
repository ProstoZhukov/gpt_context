package ru.tensor.sbis.design.logo.utils

import android.content.Context
import android.graphics.Paint
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.logo.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Класс, содержащий ресурсные константы.
 *
 * @author da.zolotarev
 */
internal class SbisLogoStyleHolder(context: Context) {
    /** @SelfDocumented */
    @Px
    val viewHeight = InlineHeight.X2S.getDimenPx(context)

    /** @SelfDocumented */
    @Px
    val brandIconHeightPage = IconSize.X5L.getDimenPx(context)

    /** @SelfDocumented */
    @Px
    val brandIconHeightNavigation = IconSize.X4L.getDimenPx(context)

    /** @SelfDocumented */
    @Px
    val brandIconBackgroundCircleRadius = InlineHeight.X2S.getDimenPx(context) / 2

    /** @SelfDocumented */
    @Px
    val defaultIconTextOffset = context.resources.getDimensionPixelSize(R.dimen.sbis_logo_view_default_icon_text_offset)

    /** @SelfDocumented */
    @Px
    val navigationIconTextOffset = Offset.ST.getDimenPx(context)

    /** @SelfDocumented */
    @Dimension
    val logoTextSize = FontSize.X4L.getScaleOffDimen(context)

    /** @SelfDocumented */
    val logoTextTypeFace = ResourcesCompat.getFont(context, RDesign.font.maison_neue_extended_book)

    /** @SelfDocumented */
    val circleIconPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getThemeColorInt(RDesign.attr.navigationLogoFill)
    }

    /** @SelfDocumented */
    val demiFontSpan = ResourcesCompat.getFont(context, RDesign.font.maison_neue_extended_demi)
}
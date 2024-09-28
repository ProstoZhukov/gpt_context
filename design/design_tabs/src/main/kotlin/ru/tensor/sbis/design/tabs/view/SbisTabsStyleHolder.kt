package ru.tensor.sbis.design.tabs.view

import android.content.Context
import android.graphics.Paint
import androidx.annotation.Px
import ru.tensor.sbis.design.tabs.R
import ru.tensor.sbis.design.tabs.api.SbisTabsStyle
import ru.tensor.sbis.design.tabs.util.SbisTabInternalStyle
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.MarkerColor
import ru.tensor.sbis.design.theme.global_variables.SeparatorColor

/**
 * Держатель ресурсов панелт вкладок.
 * @author da.zolotarev
 */
internal class SbisTabsStyleHolder(
    /** Высота панели. */
    @Px
    val maxPanelHeight: Int,

    /** Расстояние до baseline текста вкладок (от верха). */
    val tabTextBaseline: Int,
    /** Ширина подчеркивания главной вкладки. */
    @Px
    val mainTabMarkerWidth: Int,
    /** Толщина подчеркивания вкладок. */
    @Px
    val markerThick: Int,
    /** Радиус закругления подчеркивания вкладок. */
    val markerCornerRadius: Float,
    /** @SelfDocumented */
    var markerPaint: Paint,
    /** Цвет нижней разделительной линии. */
    var borderPaint: Paint,
    /** Толщина нижней разделительной линии.  */
    var borderHeight: Float,
    /** Ширина затенения при скролле.  */
    var fadeEdgeWidth: Int
) {

    /**
     * Установить стиль [SbisTabsStyleHolder].
     */
    fun setStyle(context: Context, style: SbisTabInternalStyle, customStyle: SbisTabsStyle) {
        markerPaint = Paint().apply {
            color = customStyle.customMarkerColor?.getColor(context) ?: style.getMarkerColor(context)
        }
    }

    companion object Factory {
        /** @SelfDocumented */
        fun create(context: Context): SbisTabsStyleHolder {
            return SbisTabsStyleHolder(
                maxPanelHeight = context.resources.getDimensionPixelSize(R.dimen.sbis_tabs_view_item_height),
                tabTextBaseline = context.resources.getDimensionPixelSize(R.dimen.sbis_tabs_view_tab_baseline),
                mainTabMarkerWidth =
                context.resources.getDimensionPixelOffset(R.dimen.sbis_tabs_view_main_tab_marker_width),
                markerThick = BorderThickness.L.getDimenPx(context),
                markerCornerRadius = BorderRadius.X2S.getDimen(context),
                markerPaint = Paint().apply { color = MarkerColor.DEFAULT.getValue(context) },
                borderPaint = Paint().apply { color = SeparatorColor.DEFAULT.getValue(context) },
                borderHeight = BorderThickness.S.getDimen(context),
                fadeEdgeWidth = context.resources.getDimensionPixelSize(R.dimen.sbis_tabs_view_fade_edge)
            )
        }
    }
}

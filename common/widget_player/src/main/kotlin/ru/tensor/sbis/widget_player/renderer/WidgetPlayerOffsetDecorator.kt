package ru.tensor.sbis.widget_player.renderer

import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.WidgetPlayer
import ru.tensor.sbis.widget_player.api.WidgetPlayerOffsetApi
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.id
import ru.tensor.sbis.design.R as RDesign

/**
 * @author am.boldinov
 */
internal class WidgetPlayerOffsetDecorator(
    private val player: WidgetPlayer,
    private val navigationOffset: DimenRes = DimenRes.id(RDesign.dimen.tab_navigation_menu_horizontal_height),
    private val floatingOffset: DimenRes = DimenRes.id(RDesign.dimen.floating_panel_height)
) : WidgetPlayerOffsetApi {

    private val context get() = player.context

    init {
        player.clipToPadding = false
        player.clipChildren = false
    }

    override var navigationPanelPadding: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateBottomPadding()
            }
        }

    override var floatingPanelPadding: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateBottomPadding()
            }
        }

    private fun updateBottomPadding() {
        var padding = 0
        if (navigationPanelPadding) {
            padding += navigationOffset.getValuePx(context)
        }
        if (floatingPanelPadding) {
            padding += floatingOffset.getValuePx(context)
        }
        player.updatePadding(bottom = padding)
    }
}
package ru.tensor.sbis.design.tab_panel

import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout

/**
 * Контроллер для управления состоянием и внешним видом [TabPanelItemView]
 *
 * @author ai.abramenko
 */
internal class TabPanelItemController(
    private val view: View,
    private val styleHolder: TabPanelStyleHolder
) {

    @ColorInt
    var iconColor = Color.BLACK
        private set

    @Dimension
    var iconWidth = 0F
        private set

    /**
     * Текст элемента панели вкладок для специальных возможностей и UI тестов.
     */
    val accessibilityText: String
        get() = "$icon|$title"

    var icon = ""
        set(value) {
            if (field != value) {
                field = value
                iconWidth = styleHolder.iconPaint.measureText(icon)
                view.invalidate()
            }
        }

    var title = ""
        set(value) {
            if (field != value) {
                field = value
                styleHolder.textLayout.configure {
                    text = value
                }
                view.safeRequestLayout()
            }
        }

    fun applyItem(item: TabPanelItem) {
        icon = item.icon.character.toString()
        title = view.context.getString(item.title)
    }

    fun drawableStateChanged(): Boolean {
        var changed = false
        val newIconColor = styleHolder.iconColors.getColorForState(view.drawableState, Color.BLACK)
        if (newIconColor != iconColor) {
            changed = true
            iconColor = newIconColor
        }
        return changed
    }
}
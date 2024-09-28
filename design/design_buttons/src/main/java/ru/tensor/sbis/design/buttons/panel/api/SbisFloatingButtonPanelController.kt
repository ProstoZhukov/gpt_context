package ru.tensor.sbis.design.buttons.api

import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.SbisFloatingButtonPanel
import ru.tensor.sbis.design.buttons.base.models.offset.SbisViewOffsetBehaviorImpl
import ru.tensor.sbis.design.buttons.base.models.offset.SbisViewOffsetBehavior
import ru.tensor.sbis.design.theme.HorizontalAlignment

/**
 * Контроллер для управления состоянием и внешним видом плавающего контейнера [SbisFloatingButtonPanel].
 *
 * @author ma.kolpakov
 */
internal class SbisFloatingButtonPanelController(
    private val viewOffsetBehavior: SbisViewOffsetBehaviorImpl = SbisViewOffsetBehaviorImpl()
) : SbisFloatingButtonPanelApi,
    SbisViewOffsetBehavior by viewOffsetBehavior {

    private lateinit var panel: View

    override var buttons: List<AbstractSbisButton<*, *>> = emptyList()

    override var align = HorizontalAlignment.RIGHT
        set(value) {
            if (field != value) {
                field = value
                panel.requestLayout()
            }
        }

    fun attach(panel: View, attrs: AttributeSet?) {
        this.panel = panel

        panel.context.withStyledAttributes(attrs, R.styleable.SbisFloatingButtonPanel) {
            align = loadEnum(
                R.styleable.SbisFloatingButtonPanel_SbisFloatingButtonPanel_content_align,
                align,
                *HorizontalAlignment.values()
            )
        }

        viewOffsetBehavior.initSbisView(panel)
    }
}

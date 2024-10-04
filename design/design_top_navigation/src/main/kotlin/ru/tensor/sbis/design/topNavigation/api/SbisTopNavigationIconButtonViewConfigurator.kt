package ru.tensor.sbis.design.topNavigation.api

import android.widget.FrameLayout
import androidx.core.view.isVisible
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter

/**
 * Настройки кнопки, переданной в [SbisTopNavigationActionItem.IconButton].
 *
 * @author da.zolotarev
 */
class SbisTopNavigationIconButtonViewConfigurator(
    private val container: FrameLayout,
    private val button: SbisRoundButton
) {

    /** @SelfDocumented */
    var isVisible: Boolean = container.isVisible
        set(value) {
            field = value
            container.isVisible = value
        }

    /** @SelfDocumented */
    var counter: Int = button.counter?.counter ?: 0
        set(value) {
            field = value
            button.counter = SbisButtonCounter(value)
        }
}
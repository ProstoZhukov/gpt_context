package ru.tensor.sbis.main_screen.widget.view.fabcontainer

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.indices
import androidx.core.view.isVisible
import androidx.core.view.size
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisFloatingButtonPanel
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.NavigationButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.main_screen.R

/**
 * Создаёт реализацию [FabContainer] для кнопок, добавляемых в контейнер динамически.
 *
 * @param containerId Идентификатор view контейнера кнопок.
 * @param configureMainFab Позволяет кастомизировать конфигурацию основной кнопки (+). Последующие параметры
 * аналогичны и применяются к соответствующим кнопкам.
 *
 * @author us.bessonov
 */
fun createLazyFabContainer(
    @IdRes containerId: Int,
    configureMainFab: SbisRoundButton.() -> Unit = { },
    configureTodayExtraFab: SbisRoundButton.() -> Unit = { },
    configureExtraFab: SbisRoundButton.() -> Unit = { },
    configureExtraFab2: SbisRoundButton.() -> Unit = { },
    configureExtraFab3: SbisRoundButton.() -> Unit = { },
    configureExtraFab4: SbisRoundButton.() -> Unit = { }
): FabContainer = FabContainerImpl(
    getContainerView = { it.findViewById(containerId) ?: SbisFloatingButtonPanel(it) },
    createMainFab = {
        addButton(it, order = 60, configureMainFab, icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_navBarPlus))
            /*
            Для совместимости с текущим кодом, у кнопки (+) выставляется типовой id, но на него полагаться не следует.
            Используйте `BottomBarProviderExt.mainFab` для доступа к кнопке.
            */
            .apply { id = R.id.fab }
    },
    createTodayExtraFab = {
        addButton(it, order = 20, configureTodayExtraFab, style = UnaccentedButtonStyle)
            .apply { id = R.id.today_extra_fab } // Для автотестов.
    },
    createExtraFab = { addButton(it, order = 10, configureExtraFab, style = UnaccentedButtonStyle) },
    createExtraFab2 = {
        addButton(
            it,
            order = 30,
            configureExtraFab2,
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_cameraBlack)
        )
    },
    createExtraFab3 = {
        addButton(
            it,
            order = 40,
            configureExtraFab3,
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_entry)
        ).apply { id = R.id.fab2 } // Для автотестов.
    },
    createExtraFab4 = {
        addButton(
            it,
            order = 50,
            configureExtraFab4,
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_out),
            style = PrimaryButtonStyle
        ).apply { id = R.id.fab3 } // Для автотестов.
    }
)

private fun addButton(
    container: ViewGroup,
    order: Int,
    configure: SbisRoundButton.() -> Unit,
    icon: SbisButtonIcon? = null,
    style: SbisButtonStyle? = NavigationButtonStyle
) = SbisRoundButton(container.context).apply {
    isVisible = false
    icon?.let { this.icon = it }
    style?.let { this.style = it }
    configure()
    setTag(ORDER_TAG, order)
    val addIndex = container.indices
        .find { container.getChildAt(it).getOrder() > order }
        ?: container.size
    container.addView(this, addIndex)
}

private fun View.getOrder() = (getTag(ORDER_TAG) as Int?) ?: 0

private val ORDER_TAG = R.id.lazy_fab_container_order




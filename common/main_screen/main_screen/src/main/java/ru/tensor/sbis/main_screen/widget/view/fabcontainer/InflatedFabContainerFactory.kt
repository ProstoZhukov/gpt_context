package ru.tensor.sbis.main_screen.widget.view.fabcontainer

import android.view.ViewGroup
import androidx.annotation.IdRes
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.main_screen.widget.view.SbisRoundButtonBottomBarProvider


/**
 * Создаёт [FabContainer] для кнопок, добавленных в макет.
 *
 * @author us.bessonov
 */
@Deprecated("Будет удалено по https://online.sbis.ru/opendoc.html?guid=40cbe6fa-f3ad-428c-8073-55872c2179bf&client=3")
internal fun createInflatedFabContainer(
    @IdRes containerId: Int,
    @IdRes mainFabId: Int,
    @IdRes extraFabId: Int? = null,
    @IdRes extraFab2Id: Int? = null,
    @IdRes extraFab3Id: Int? = null,
    @IdRes extraFab4Id: Int? = null,
    todayButtonConfig: SbisRoundButtonBottomBarProvider.TodayButtonConfig? = null
) = FabContainerImpl(
    getContainerView = { it.findViewById(containerId) },
    createMainFab = { it.findButton(mainFabId) },
    createTodayExtraFab = { container -> todayButtonConfig?.let { container.findButton(it.todayButtonId) } },
    createExtraFab = { getButton(it, extraFabId) },
    createExtraFab2 = { getButton(it, extraFab2Id) },
    createExtraFab3 = { getButton(it, extraFab3Id) },
    createExtraFab4 = { getButton(it, extraFab4Id) }
)

private fun getButton(container: ViewGroup, @IdRes viewId: Int?): SbisRoundButton? =
    viewId?.let { container.findButton(it) }

private fun ViewGroup.findButton(@IdRes viewId: Int) = rootView.findViewById<SbisRoundButton>(viewId)
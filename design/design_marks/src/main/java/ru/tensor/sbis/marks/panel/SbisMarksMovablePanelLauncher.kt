package ru.tensor.sbis.marks.panel

import androidx.fragment.app.FragmentManager
import android.os.Bundle
import android.view.ViewGroup
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_dialogs.movablepanel.PanelWidth
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.panel.api.SbisMarksMovablePanelLauncherApi
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment

/**
 * Класс реализующий открытие списка пометок в шторке.
 *
 * ВАЖНО! Для получения результата выбора пользователя требуется воспользоваться FragmentResultApi.
 * ключ: [FRAGMENT_RESULT_KEY]
 *
 * @property items          список моделей пометок
 * @property componentType  тип списка пометок
 *
 * @author ra.geraskin
 */

class SbisMarksMovablePanelLauncher(
    override var items: List<SbisMarksElement> = emptyList(),
    override var componentType: SbisMarksComponentType = SbisMarksComponentType.COLOR_STYLE
) : SbisMarksMovablePanelLauncherApi {

    /** @SelfDocumented */
    override fun showMarksPanel(fragmentManager: FragmentManager, containerView: ViewGroup) {
        if (items.isEmpty()) return

        // Передача в панельку копии списка айтемов, для предотвращения изменений в исходном списке.
        val newItems = items.map { it.copyElement() }

        val containerMovableFragment = ContainerMovableFragment.Builder()
            .setContentCreator(MovablePanelContentCreator(newItems, componentType))
            .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
            .setPanelWidthForLandscape(PanelWidth.CENTER_HALF)
            .instant(true)
            .build()

        fragmentManager.beginTransaction()
            .add(containerView.id, containerMovableFragment, MARKS_COMPONENT_FRAGMENT_TAG)
            .addToBackStack(MARKS_COMPONENT_FRAGMENT_TAG)
            .commit()
    }

    companion object {

        /** Тэг открытия [ContainerMovableFragment] фрагмента */
        internal val MARKS_COMPONENT_FRAGMENT_TAG =
            SbisMarksMovablePanelLauncher::class.java.canonicalName!! + ".marks_component_fragment_tag"

        /** Ключ передачи данных с помощью FragmentResulApi между фрагментами прикладного экрана (demo) и шторки */
        val FRAGMENT_RESULT_KEY =
            SbisMarksMovablePanelLauncher::class.java.canonicalName!! + ".result_request_key"

        /** Ключ под которым сохраняется список моделей в [Bundle]  */
        val ITEMS_STATE = SbisMarksMovablePanelLauncher::class.java.canonicalName!! + ".items_state"

        /** Ключ под которым сохраняется значение типа списка пометок в [Bundle]  */
        val COMPONENT_TYPE_STATE = SbisMarksMovablePanelLauncher::class.java.canonicalName!! + ".component_type_state"
    }

}
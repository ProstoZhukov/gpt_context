package ru.tensor.sbis.marks.panel.api

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement

/**
 * Api класса отвечающего за запуск списка пометок в шторке.
 *
 * @author ra.geraskin
 */

interface SbisMarksMovablePanelLauncherApi {

    /**
     * Список моделей пометок.
     */
    var items: List<SbisMarksElement>

    /**
     * Тип списка пометок.
     */
    var componentType: SbisMarksComponentType

    /** @SelfDocumented */
    fun showMarksPanel(fragmentManager: FragmentManager, containerView: ViewGroup)

}
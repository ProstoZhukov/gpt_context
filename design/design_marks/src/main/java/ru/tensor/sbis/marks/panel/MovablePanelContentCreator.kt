package ru.tensor.sbis.marks.panel

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement

/**
 * Класс реализующий функционал создания фрагмента, отображающего в себе список пометок.
 *
 * @property items          список моделей пометок
 * @property componentType  тип списка пометок
 *
 * @author ra.geraskin
 */

@Parcelize
internal class MovablePanelContentCreator(
    private var items: List<SbisMarksElement> = emptyList(),
    private var componentType: SbisMarksComponentType = SbisMarksComponentType.COLOR
) : ContentCreatorParcelable {

    /** @SelfDocumented */
    override fun createFragment(): Fragment = MarksMovablePanelFragment(
        items,
        componentType
    )
}
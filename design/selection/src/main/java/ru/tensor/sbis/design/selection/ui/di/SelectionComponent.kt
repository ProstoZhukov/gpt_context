package ru.tensor.sbis.design.selection.ui.di

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonListener

/**
 * Интерфейс dagger компонента для бизнес логики выбора и поиска
 *
 * @author ma.kolpakov
 */
internal interface SelectionComponent<SELECTION_VM> {

    val selectionVm: SELECTION_VM

    /**
     * Параметры, которые определены для компонента в целом (для корневого фрагмента)
     *
     * @see isHierarchicalData
     * @see selectionLimit
     * @see counterFormat
     * @see multiCustomisation
     * @see singleCustomisation
     */
    val arguments: Bundle
    val selectorCustomisation: SelectorCustomisation
    val fixedButtonListener: FixedButtonListener<Any, FragmentActivity>?

    val metaFactory: ItemMetaFactory
}
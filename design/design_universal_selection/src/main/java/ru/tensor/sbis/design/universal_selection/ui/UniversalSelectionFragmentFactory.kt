package ru.tensor.sbis.design.universal_selection.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.design.universal_selection.domain.UniversalSelectionDependenciesProvider
import ru.tensor.sbis.design.universal_selection.R
import ru.tensor.sbis.design_selection.SelectionFragmentFactory

/**
 * Фабрика для создания фрагмента универсального выбора.
 *
 * @author vv.chekurda
 */
internal object UniversalSelectionFragmentFactory {

    /**
     * Создать фрагмент универсального выбора.
     *
     * @param config конфигурация универсального выбора.
     */
    fun createFragment(config: UniversalSelectionConfig): Fragment =
        SelectionFragmentFactory.createSelectionFragment(
            config = config,
            dependenciesProvider = UniversalSelectionDependenciesProvider(),
            themeAttr = R.attr.universalSelectionTheme,
            defTheme = R.style.UniversalSelectionDefaultTheme
        )
}
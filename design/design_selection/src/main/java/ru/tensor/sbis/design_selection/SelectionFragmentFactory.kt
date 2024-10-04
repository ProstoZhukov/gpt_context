package ru.tensor.sbis.design_selection

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.ui.main.SelectionFragment
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory

/**
 * Фабрика для создания фрагмента компонента выбора.
 *
 * @author vv.chekurda
 */
object SelectionFragmentFactory {

    /**
     * Создать инстанс фрагмента компонента выбора.
     *
     * @param config конфигурация компонента.
     * @param dependenciesProvider поставщик зависимостей компонента.
     */
    fun <ITEM : SelectionItem, CONFIG : SelectionConfig> createSelectionFragment(
        config: CONFIG,
        dependenciesProvider: SelectionDependenciesFactory.Provider<ITEM, CONFIG>,
        @AttrRes themeAttr: Int = R.attr.selectionTheme,
        @StyleRes defTheme: Int = R.style.SelectionTheme
    ): Fragment = SelectionFragment.newInstance(
        config = config,
        dependenciesProvider = dependenciesProvider,
        themeAttr = themeAttr,
        defTheme = defTheme
    )
}
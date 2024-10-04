package ru.tensor.sbis.design.tabs.api

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Служебное API компонента вкладок.
 *
 * @author us.bessonov
 */
internal interface SbisTabsViewApiInternal {

    /**
     * Событие изменения выбранной вкладки.
     */
    val tabSelectionFlow: Flow<NavxIdDecl>
}
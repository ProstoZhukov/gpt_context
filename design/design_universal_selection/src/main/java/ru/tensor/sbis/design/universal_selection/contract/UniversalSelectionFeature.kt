package ru.tensor.sbis.design.universal_selection.contract

import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionProvider
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultDelegate

/**
 * Фичи модуля компонента универсального выбора.
 *
 * @author vv.chekurda
 */
interface UniversalSelectionFeature :
    UniversalSelectionProvider,
    UniversalSelectionResultDelegate.Provider
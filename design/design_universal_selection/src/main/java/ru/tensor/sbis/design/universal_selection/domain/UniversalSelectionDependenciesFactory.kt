package ru.tensor.sbis.design.universal_selection.domain

import android.content.Context
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.ui.di.screen.UniversalSelectionComponent
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener

/**
 * Реализация фабрики зависимостей для компонента универсального выбора.
 *
 * @see SelectionDependenciesFactory
 *
 * @property component di-компонент универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionDependenciesFactory(
    private val component: UniversalSelectionComponent
) : SelectionDependenciesFactory<UniversalItem> {

    override fun getSelectionControllerProvider(
        appContext: Context
    ): SelectionControllerWrapper.Provider<*, *, *, *, *, *, UniversalItem> =
        component.controllerProvider

    override fun getFilterFactory(appContext: Context): SelectionFilterFactory<*, *> =
        component.filterFactory

    override fun getSelectionResultListener(appContext: Context): SelectionResultListener<UniversalItem, *> =
        component.resultListener

    override fun getSelectionCustomization(appContext: Context): SelectionCustomization<UniversalItem> =
        component.customization
}
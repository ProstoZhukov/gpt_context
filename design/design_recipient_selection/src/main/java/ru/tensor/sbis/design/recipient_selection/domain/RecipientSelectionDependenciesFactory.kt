package ru.tensor.sbis.design.recipient_selection.domain

import android.content.Context
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.ui.di.screen.RecipientSelectionComponent
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract

/**
 * Реализация фабрики зависимостей для компонента выбора получателей.
 *
 * @see SelectionDependenciesFactory
 *
 * @property component di-компонент выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionDependenciesFactory(
    private val component: RecipientSelectionComponent
) : SelectionDependenciesFactory<RecipientItem> {

    override fun getSelectionControllerProvider(
        appContext: Context
    ): SelectionControllerWrapper.Provider<*, *, *, *, *, *, RecipientItem> =
        component.controllerProvider

    override fun getFilterFactory(appContext: Context): SelectionFilterFactory<*, *> =
        component.filterFactory

    override fun getSelectionResultListener(appContext: Context): SelectionResultListener<RecipientItem, *> =
        component.resultListener

    override fun getHeaderButtonContract(appContext: Context): HeaderButtonContract<RecipientItem, *>? =
        component.headerButtonContract

    override fun getSelectionCustomization(appContext: Context): SelectionCustomization<RecipientItem> =
        component.selectionCustomization
}
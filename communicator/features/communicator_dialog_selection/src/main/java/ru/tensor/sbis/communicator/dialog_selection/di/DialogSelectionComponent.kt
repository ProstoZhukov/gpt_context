package ru.tensor.sbis.communicator.dialog_selection.di

import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionSearchFilter
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
import javax.inject.Scope

/**
 * Компонент экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
@DialogSelectionDIScope
@Component(
    dependencies = [CommunicatorCommonComponent::class],
    modules = [DialogSelectionModule::class]
)
internal interface DialogSelectionComponent {
    val resultManager: MultiSelectionResultManager<DialogSelectionResult>

    val serviceWrapper: ServiceWrapper<DialogSelectionServiceResult, DialogSelectionSearchFilter>
    val resultHelper: ResultHelper<Int, DialogSelectionServiceResult>
    val filterFactory: FilterFactory<SelectorItemModel, DialogSelectionSearchFilter, Int>
    val listMapper: ListMapper<DialogSelectionServiceResult, SelectorItemModel>
    val multiSelectionLoader: MultiSelectionLoader<SelectorItemModel>
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DialogSelectionDIScope
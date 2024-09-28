package ru.tensor.sbis.communicator.dialog_selection.data.factory

import android.content.Context
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionSearchFilter
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.communicator.dialog_selection.data.factory.filter.DialogSelectionFilterFactory
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.DialogSelectionServiceWrapper
import ru.tensor.sbis.communicator.dialog_selection.di.getDialogSelectionComponent
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * Реализация сериализуемой фабрики для экрана выбора диалога/участников
 * @see [DialogSelectionServiceWrapper]
 * @see [DialogSelectionLoader]
 * @see [DialogSelectionFilterFactory]
 * @see [DialogSelectionMapper]
 * @see [DialogSelectionResultHelper]
 *
 * @author vv.chekurda
 */
internal class DialogSelectionDependencyFactory
    : ListDependenciesFactory<DialogSelectionServiceResult, SelectorItemModel, DialogSelectionSearchFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<DialogSelectionServiceResult, DialogSelectionSearchFilter> =
        appContext.getDialogSelectionComponent().serviceWrapper

    override fun getSelectionLoader(appContext: Context): MultiSelectionLoader<SelectorItemModel> =
        appContext.getDialogSelectionComponent().multiSelectionLoader

    override fun getFilterFactory(appContext: Context): FilterFactory<SelectorItemModel, DialogSelectionSearchFilter, Int> =
        appContext.getDialogSelectionComponent().filterFactory

    override fun getMapperFunction(appContext: Context): ListMapper<DialogSelectionServiceResult, SelectorItemModel> =
        appContext.getDialogSelectionComponent().listMapper

    override fun getResultHelper(appContext: Context): ResultHelper<Int, DialogSelectionServiceResult> =
        appContext.getDialogSelectionComponent().resultHelper
}
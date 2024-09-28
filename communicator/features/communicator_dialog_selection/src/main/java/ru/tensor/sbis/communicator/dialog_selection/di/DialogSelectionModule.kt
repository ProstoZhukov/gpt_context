package ru.tensor.sbis.communicator.dialog_selection.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewMode
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionSearchFilter
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.communicator.dialog_selection.data.factory.DialogSelectionLoader
import ru.tensor.sbis.communicator.dialog_selection.data.factory.DialogSelectionMapper
import ru.tensor.sbis.communicator.dialog_selection.data.factory.DialogSelectionResultHelper
import ru.tensor.sbis.communicator.dialog_selection.data.factory.filter.DialogSelectionFilterFactory
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.DialogSelectionServiceWrapper
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients.RecipientsServiceWrapper
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients.RecipientsServiceWrapperImpl
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme.ThemeServiceWrapper
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme.ThemeServiceWrapperImpl
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * Модуль для экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
@Module(includes = [DialogSelectionModule.BindsDIModule::class])
internal class DialogSelectionModule {

    @Provides
    @DialogSelectionDIScope
    fun provideListMapper(
        resourceProvider: ResourceProvider,
        dependency: CommunicatorCommonDependency
    ): ListMapper<DialogSelectionServiceResult, SelectorItemModel> =
        DialogSelectionMapper(
            resourceProvider,
            dependency.createAttachmentRegisterModelMapper(AttachmentsViewMode.REGISTRY)
        )

    @Suppress("unused")
    @Module
    interface BindsDIModule {

        @Binds
        @DialogSelectionDIScope
        fun asThemeServiceWrapper(impl: ThemeServiceWrapperImpl): ThemeServiceWrapper

        @Binds
        @DialogSelectionDIScope
        fun asRecipientsServiceWrapper(impl: RecipientsServiceWrapperImpl): RecipientsServiceWrapper

        @Binds
        @DialogSelectionDIScope
        fun asServiceWrapper(impl: DialogSelectionServiceWrapper): ServiceWrapper<DialogSelectionServiceResult, DialogSelectionSearchFilter>

        @Binds
        @DialogSelectionDIScope
        fun asFilterFactory(impl: DialogSelectionFilterFactory): FilterFactory<SelectorItemModel, DialogSelectionSearchFilter, Int>

        @Binds
        @DialogSelectionDIScope
        fun asResultHelper(impl: DialogSelectionResultHelper): ResultHelper<Int, DialogSelectionServiceResult>

        @Binds
        @DialogSelectionDIScope
        fun asMultiSelectionLoader(impl: DialogSelectionLoader): MultiSelectionLoader<SelectorItemModel>
    }
}
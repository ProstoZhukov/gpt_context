package ru.tensor.sbis.recipient_selection.profile.di.profile_component

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.di.PerActivity
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionDependency
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionNewModule.BindsDIModule
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.RecipientSelectionResultHelper
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.RecipientSelectionServiceWrapper
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi.MultiRecipientSelectionLoader
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi.MultiSelectionFilterFactory
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi.MultiSelectionMapper
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.repost.ContactSelectionForRepostLoader
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.single.SingleRecipientSelectionLoader
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.single.SingleSelectionFilterFactory
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.single.SingleSelectionMapper
import ru.tensor.sbis.recipient_selection.profile.di.CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST
import ru.tensor.sbis.recipient_selection.profile.interactor.RecipientSelectionInteractor
import ru.tensor.sbis.recipient_selection.profile.interactor.RecipientSelectionInteractorImpl
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import javax.inject.Named

/**
 * Модуль зависимостей выбора получателей
 */
@Module(includes = [BindsDIModule::class])
internal class RecipientSelectionNewModule {

    @Provides
    @PerActivity
    fun provideEmployeeProfileControllerProvider(
        dependency: RecipientSelectionDependency
    ): DependencyProvider<EmployeeProfileControllerWrapper> =
        dependency.employeeProfileControllerWrapper

    @Provides
    @PerActivity
    @Named(MULTI_SELECTION_FILTER_FACTORY)
    fun provideMultiSelectionFilterFactory(
        filter: RecipientsSearchFilter
    ): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> =
        MultiSelectionFilterFactory(filter)

    @Provides
    @PerActivity
    @Named(SINGLE_SELECTION_FILTER_FACTORY)
    fun provideSingleSelectionFilterFactory(
        filter: RecipientsSearchFilter
    ): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> =
        SingleSelectionFilterFactory(filter)

    @Provides
    @PerActivity
    @Named(MULTI_SELECTION_MAPPER)
    fun provideMultiSelectionMapper(
        profileAndContactMapper: ProfileAndContactItemMapper,
        folderAndGroupMapper: FolderAndGroupItemMapper,
    ): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> =
        MultiSelectionMapper(profileAndContactMapper, folderAndGroupMapper)

    @Provides
    @PerActivity
    @Named(SINGLE_SELECTION_MAPPER)
    fun provideSingleSelectionMapper(
        profileAndContactMapper: ProfileAndContactItemMapper
    ): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> =
        SingleSelectionMapper(profileAndContactMapper)

    @Provides
    @PerActivity
    @Named(CONTACT_SELECTION_FOR_REPOST_LOADER)
    fun provideContactSelectionForRepostLoader(
        @Named(CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST) contactSelectionResultManager: RecipientSelectionResultManager,
        employeeProfileService: DependencyProvider<EmployeeProfileControllerWrapper>
    ): MultiSelectionLoader<RecipientSelectorItemModel> =
        ContactSelectionForRepostLoader(contactSelectionResultManager, employeeProfileService)

    @Module
    interface BindsDIModule {

        @Binds
        @PerActivity
        fun asRecipientSelectionInteractor(impl: RecipientSelectionInteractorImpl): RecipientSelectionInteractor

        @Binds
        @PerActivity
        fun asServiceWrapper(impl: RecipientSelectionServiceWrapper): ServiceWrapper<ProfilesFoldersResult, RecipientsSearchFilter>

        @Binds
        @PerActivity
        fun asResultHelper(impl: RecipientSelectionResultHelper): ResultHelper<Int, ProfilesFoldersResult>

        @Binds
        @PerActivity
        fun asMultiSelectionLoader(impl: MultiRecipientSelectionLoader): MultiSelectionLoader<RecipientSelectorItemModel>

        @Binds
        @PerActivity
        fun asSingleSelectionLoader(impl: SingleRecipientSelectionLoader): SingleSelectionLoader<DefaultPersonSelectorItemModel>
    }
}

internal const val MULTI_SELECTION_FILTER_FACTORY = "MULTI_SELECTION_FILTER_FACTORY"
internal const val SINGLE_SELECTION_FILTER_FACTORY = "SINGLE_SELECTION_FILTER_FACTORY"
internal const val MULTI_SELECTION_MAPPER = "MULTI_SELECTION_MAPPER"
internal const val SINGLE_SELECTION_MAPPER = "SINGLE_SELECTION_MAPPER"
internal const val CONTACT_SELECTION_FOR_REPOST_LOADER = "CONTACT_SELECTION_FOR_REPOST_LOADER"
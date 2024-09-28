package ru.tensor.sbis.recipient_selection.profile.di.profile_component

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.util.di.PerActivity
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionSingletonComponent
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.interactor.RecipientSelectionInteractor
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import javax.inject.Named

/**
 * DI Компонент выбора получателей
 */
@PerActivity
@Component(
    modules = [RecipientSelectionNewModule::class],
    dependencies = [RecipientSelectionSingletonComponent::class]
)
internal abstract class RecipientSelectionComponent {

    internal abstract fun getProfileAndContactItemMapper(): ProfileAndContactItemMapper
    internal abstract fun getFolderAndGroupItemMapper(): FolderAndGroupItemMapper
    internal abstract fun getRecipientSelectionInteractor(): RecipientSelectionInteractor
    internal abstract fun getRecipientSelectionResultManager(): RecipientSelectionResultManager

    internal abstract fun getRecipientSelectionServiceWrapper(): ServiceWrapper<ProfilesFoldersResult, RecipientsSearchFilter>
    internal abstract fun getRecipientSelectionResultHelper(): ResultHelper<Int, ProfilesFoldersResult>

    @Named(MULTI_SELECTION_FILTER_FACTORY)
    internal abstract fun getMultiSelectionFilterFactory(): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int>
    @Named(MULTI_SELECTION_MAPPER)
    internal abstract fun getMultiSelectionMapper(): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel>
    internal abstract fun getMultiRecipientSelectionLoader(): MultiSelectionLoader<RecipientSelectorItemModel>
    @Named(CONTACT_SELECTION_FOR_REPOST_LOADER)
    internal abstract fun getContactSelectionForRepostLoader(): MultiSelectionLoader<RecipientSelectorItemModel>

    @Named(SINGLE_SELECTION_FILTER_FACTORY)
    internal abstract fun getSingleSelectionFilterFactory(): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int>
    @Named(SINGLE_SELECTION_MAPPER)
    internal abstract fun getSingleSelectionMapper(): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel>
    internal abstract fun getSingleRecipientSelectionLoader(): SingleSelectionLoader<DefaultPersonSelectorItemModel>

    @Component.Builder
    interface Builder {

        fun recipientSelectionSingletonComponent(component: RecipientSelectionSingletonComponent): Builder

        @BindsInstance
        fun recipientsFilter(filter: RecipientsSearchFilter): Builder

        fun build(): RecipientSelectionComponent
    }
}
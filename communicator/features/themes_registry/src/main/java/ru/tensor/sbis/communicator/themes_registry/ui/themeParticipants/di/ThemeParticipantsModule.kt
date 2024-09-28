package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapperImpl
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListCommand
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepository
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepositoryImpl
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper.ThemeParticipantListItemListMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper.ThemeParticipantListItemMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper.ThemeParticipantsListMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper.ThemeParticipantsMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.communicator.generated.ThemeParticipantListItem as ControllerThemeParticipantListItem
import ru.tensor.sbis.communicator.generated.ThemeParticipant as ControllerThemeParticipant

/** @SelfDocumented */
@Suppress("unused")
@Module
internal class ThemeParticipantsModule {

    @Provides
    internal fun provideFilter(): ThemeParticipantsListFilter = ThemeParticipantsListFilter()

    @Provides
    internal fun provideRepository(
        manager: DependencyProvider<ThemeParticipantsController>,
        loginInterface: LoginInterface
    ): ThemeParticipantsRepository =
        ThemeParticipantsRepositoryImpl(manager, loginInterface)

    @Provides
    internal fun provideThemeParticipantsCommandWrapper(
        repository: ThemeParticipantsRepository,
        listCommand: BaseListObservableCommand<PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>
    ): ThemeParticipantsCommandWrapper =
        ThemeParticipantsCommandWrapperImpl(repository, listCommand)

    @Provides
    internal fun provideMapper(
        context: Context
    ): BaseModelMapper<ControllerThemeParticipant, ThemeParticipant> =
        ThemeParticipantsMapper(context)

    @Provides
    internal fun provideListItemMapper(
        context: Context
    ): BaseModelMapper<ControllerThemeParticipantListItem, ThemeParticipantListItem> =
        ThemeParticipantListItemMapper(context)

    @Provides
    internal fun provideListMapper(context: Context): BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ThemeParticipant>> =
        ThemeParticipantsListMapper(context)

    @Provides
    internal fun provideListItemListMapper(context: Context): BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ThemeParticipantListItem>> =
        ThemeParticipantListItemListMapper(context)

    @Provides
    internal fun provideListCommand(
        repository: ThemeParticipantsRepository,
        mapper: BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ThemeParticipantListItem>>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): BaseListObservableCommand<PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback> =
        ThemeParticipantsListCommand(repository, mapper, activityStatusSubscriptionsInitializer)
}

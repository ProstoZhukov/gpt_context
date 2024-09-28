package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepository
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.communicator.generated.ThemeParticipantListItem as ControllerThemeParticipantListItem

/** @SelfDocumented */
internal interface ThemeParticipantsComponent {

    /** @SelfDocumented */
    fun getThemeParticipantsListFilter(): ThemeParticipantsListFilter
    /** @SelfDocumented */
    fun getThemeParticipantsController(): DependencyProvider<ThemeParticipantsController>
    /** @SelfDocumented */
    fun getThemeParticipantsRepository(): ThemeParticipantsRepository
    /** @SelfDocumented */
    fun getThemeParticipantsCommandWrapper(): ThemeParticipantsCommandWrapper
    /** @SelfDocumented */
    fun getThemeParticipantsMapper(): BaseModelMapper<ControllerThemeParticipantListItem, ThemeParticipantListItem>
    /** @SelfDocumented */
    fun getThemeParticipantsListMapper(): BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ThemeParticipantListItem>>
    /** @SelfDocumented */
    fun getThemeParticipantsListCommand(): BaseListObservableCommand<PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>

    val activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
}

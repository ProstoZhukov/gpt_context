package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/** @SelfDocumented */
internal class ThemeParticipantsCommandWrapperImpl(
    private val repository: ThemeParticipantsRepository,
    override val listCommand: BaseListObservableCommand<PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>
) :
    ThemeParticipantsCommandWrapper,
    BaseInteractor() {

    override fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable =
        Completable.fromCallable { repository.addParticipants(themeUuid, participantsUuid) }
            .compose(completableBackgroundSchedulers)

    override fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable =
        Completable.fromCallable { repository.removeParticipants(themeUuid, participantsUuid) }
            .compose(completableBackgroundSchedulers)

    override fun getCurrentUserUUID(): Single<UUID> =
        Single.fromCallable<UUID> { repository.getCurrentUserUUID() }
            .compose(getSingleBackgroundSchedulers())
}

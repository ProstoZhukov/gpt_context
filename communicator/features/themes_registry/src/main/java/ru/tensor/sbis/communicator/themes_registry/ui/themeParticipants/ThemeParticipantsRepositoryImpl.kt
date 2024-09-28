package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*

/** @SelfDocumented */
internal class ThemeParticipantsRepositoryImpl(
    private val controller: DependencyProvider<ThemeParticipantsController>,
    private val loginInterface: LoginInterface
) : ThemeParticipantsRepository {

    override fun list(filter: ThemeParticipantsFilter): ListResultOfThemeParticipantListItemMapOfStringString =
        controller.get().list(filter)

    override fun refresh(filter: ThemeParticipantsFilter): ListResultOfThemeParticipantListItemMapOfStringString =
        controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedThemeParticipantsControllerCallback): Subscription =
        controller.get().dataRefreshed().subscribe(callback)

    override fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>) =
        controller.get().addParticipants(themeUuid, participantsUuid)

    override fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>) =
        controller.get().removeParticipants(themeUuid, participantsUuid)

    override fun getCurrentUserUUID(): UUID =
        loginInterface.getCurrentAccount()!!.personId.let(UUIDUtils::fromString)
}

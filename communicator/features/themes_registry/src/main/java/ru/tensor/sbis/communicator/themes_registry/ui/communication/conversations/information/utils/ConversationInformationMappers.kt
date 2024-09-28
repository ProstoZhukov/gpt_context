package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.generated.Conversation
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view.ConversationInformationParticipantViewData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import ru.tensor.sbis.profiles.generated.Gender
import ru.tensor.sbis.profiles.generated.Person
import ru.tensor.sbis.profiles.generated.PersonName

/**
 * Расширения, необходимые для получения данных для отображения экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
/** @SelfDcoumented */
internal fun Conversation.getPhotoData(): List<PhotoData> {
    val participantList = when {
        participants.isNotEmpty() -> participants
        isNew && !receivers.isNullOrEmpty() -> receivers
        else -> emptyList()
    }
    return if (photoUrl.isNotEmpty()) {
        listOf(PersonData(null, photoUrl, null))
    } else {
        participantList?.toPhotoDataList() ?: emptyList()
    }
}

private fun List<Person>.toPhotoDataList(): List<PhotoData> =
    map {
        PersonData(
            it.uuid,
            it.photoUrl,
            it.photoDecoration?.let { stubData ->
                InitialsStubData(
                    stubData.initials,
                    stubData.backgroundColorHex
                )
            }
        )
    }

/** @SelfDcoumented */
internal fun EmployeeProfile.getParticipantViewData(): ConversationInformationParticipantViewData = ConversationInformationParticipantViewData(
    PersonData(
        person.uuid,
        person.photoUrl,
        person.photoDecoration?.let { stubData ->
            InitialsStubData(stubData.initials, stubData.backgroundColorHex)
        }
    ),
    person.name.formatName(PersonNameTemplate.SURNAME_NAME_PATRONYMIC),
    position ?: StringUtils.EMPTY,
    person.gender,
    null
)


/** @SelfDcoumented */
internal fun ContactVM.getParticipantViewData(): ConversationInformationParticipantViewData =
    ConversationInformationParticipantViewData(
        photoData = PersonData(
            uuid = uuid,
            photoUrl = rawPhoto,
            initialsStubData = initialsStubData
        ),
        fullName = getRenderedName(ru.tensor.sbis.persons.util.PersonNameTemplate.SURNAME_NAME_PATRONYMIC),
        position = data1 ?: StringUtils.EMPTY,
        gender = gender?.let {
            when (it) {
                ru.tensor.sbis.person_decl.profile.model.Gender.UNKNOWN -> Gender.UNKNOWN
                ru.tensor.sbis.person_decl.profile.model.Gender.MALE -> Gender.MALE
                ru.tensor.sbis.person_decl.profile.model.Gender.FEMALE ->  Gender.FEMALE
            }
        } ?: Gender.UNKNOWN,
        profileActivityStatus = null
    )

private fun PersonName.formatName(template: PersonNameTemplate) = template.format(last, first, patronymic)

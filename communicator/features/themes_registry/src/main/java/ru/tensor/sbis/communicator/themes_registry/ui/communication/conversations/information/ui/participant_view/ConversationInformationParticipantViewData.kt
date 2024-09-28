package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.profiles.generated.Gender

/**
 * Данные для отображения вью единственного участника на экране информации диалога/канала.
 *
 * @param photoData данные фото.
 * @param fullName ФИО.
 * @param position должность.
 * @param gender пол.
 * @param profileActivityStatus статус активности.
 *
 * @author dv.baranov
 */
@Parcelize
internal data class ConversationInformationParticipantViewData(
    val photoData: PhotoData,
    val fullName: String,
    val position: String,
    val gender: Gender,
    val profileActivityStatus: ProfileActivityStatus?
) : Parcelable
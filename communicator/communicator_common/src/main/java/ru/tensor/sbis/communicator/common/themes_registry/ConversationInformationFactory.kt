package ru.tensor.sbis.communicator.common.themes_registry

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания фрагмента информации о диалоге/канале.
 *
 * @author dv.baranov
 */
interface ConversationInformationFactory : Feature {

    /** @SelfDocumented */
    fun createConversationInformationFragment(
        conversationUuid: UUID,
        subtitle: String,
        isNewDialog: Boolean,
        isChat: Boolean,
        conversationName: String,
        permissions: Permissions,
        photoData: List<PhotoData>,
        isGroupConversation: Boolean,
        singleParticipant: ContactVM?
    ): Fragment
}
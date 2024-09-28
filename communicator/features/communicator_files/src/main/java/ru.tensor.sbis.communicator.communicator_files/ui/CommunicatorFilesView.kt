package ru.tensor.sbis.communicator.communicator_files.ui

import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilesFilter
import java.util.UUID

/**
 * @author da.zhukov
 */
internal interface CommunicatorFilesView : MviView<CommunicatorFilesView.Model, CommunicatorFilesView.Event> {

    /** @SelfDocumented */
    sealed interface Event {
        data class SelectedFilter(val filterTypes: List<ConversationInformationFilesFilter>) : Event
        data class EnterSearchQuery(val query: String) : Event
        data class FolderClick(
            val id: UUID? = null,
            val title: String = StringUtils.EMPTY
        ) : Event
        data class FolderSelected(val folderId: UUID) : Event
        object BackButtonClick : Event
    }

    /** @SelfDocumented */
    data class Model(
        val currentFolderViewIsVisible: Boolean = false,
        val folderTitle: String = StringUtils.EMPTY
    )
}
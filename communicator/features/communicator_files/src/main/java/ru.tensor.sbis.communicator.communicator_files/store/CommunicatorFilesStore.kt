package ru.tensor.sbis.communicator.communicator_files.store

import android.os.Parcelable
import android.view.View
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileAction
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import java.util.UUID

/**
 * Стор файлов переписки.
 *
 * @author da.zhukov
 */
internal interface CommunicatorFilesStore :
    Store<CommunicatorFilesStore.Intent, CommunicatorFilesStore.State, CommunicatorFilesStore.Label> {

    /**
     * Намерения файлов переписки.
     */
    sealed interface Intent {
        data class InitialLoading(
            val query: String? = null,
            val folderTitle: String = StringUtils.EMPTY,
            val currentFolderViewIsVisible: Boolean = true
        ) : Intent
        data class ChangeCurrentFolder(val id: UUID?, val title: String = StringUtils.EMPTY) : Intent
        data class SearchQuery(val query: String) : Intent
        data class UpdateFilter(val filterTypes: List<ConversationInformationFilter>) : Intent
        data class ShowActionList(val view: View, val actionData: CommunicatorFileActionData) : Intent
        data class ShowFile(val actionData: CommunicatorFileActionData) : Intent
        data class MoveToFolder(val folderId: UUID) : Intent
        data class OnFileActionClick(val action: CommunicatorFileAction, val actionData: CommunicatorFileActionData) : Intent
        object BackButtonClick : Intent
        data class ConfigurationChanged(val quantityOfViews: Int) : Intent
        data class AddFiles(val selectedFiles: List<SbisPickedItem>, val compressImages: Boolean) : Intent
        data class CreateFolder(val folderName: String) : Intent
    }

    /**
     * События файлов переписки.
     */
    sealed interface Label {
        object BackButtonClick : Label
        data class ShowActionList(
            val view: View,
            val actions: List<CommunicatorFileAction>,
            val actionData: CommunicatorFileActionData
        ) : Label
        data class ShowFile(val themeUuid: UUID, val folderUuid: UUID?, val actionData: CommunicatorFileActionData) : Label
        data class CopyLink(val link: String) : Label
        data class GoToMessage(val messageId: UUID) : Label
        data class ShowFolderSelection(val currentFolderId: UUID?) : Label
        object ShowFileSuccessMovedToFolder : Label
    }
    /**
     * Состояние стора файлов переписки.
     */
    @Parcelize
    data class State(
        val currentFolderViewIsVisible: Boolean = false,
        val folderTitle: String = StringUtils.EMPTY
     ) : Parcelable
}
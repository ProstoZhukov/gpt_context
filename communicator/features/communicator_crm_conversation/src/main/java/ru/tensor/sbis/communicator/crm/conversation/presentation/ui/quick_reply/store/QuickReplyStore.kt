package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import java.util.UUID

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для быстрых ответов.
 *
 * @author dv.baranov
 */
internal interface QuickReplyStore : Store<QuickReplyStore.Intent, QuickReplyStore.State, QuickReplyStore.Label> {

    /** @SelfDocumented */
    sealed interface Intent {

        /** Намерение изменения поисковой строки. */
        data class EnterSearchQuery(val query: String) : Intent

        /** Намерение изменения папки. */
        data class FolderChanged(
            val folderTitle: String,
            val uuid: UUID?,
        ) : Intent

        /** Намерение клика по кнопке открепить/закрепить быстрый ответ. */
        data class OnPinClick(val uuid: UUID, val isPinned: Boolean) : Intent

        /** @SelfDocumented */
        object NoAction : Intent
    }

    /** @SelfDocumented */
    sealed interface Label

    /** @SelfDocumented */
    @Parcelize
    data class State(
        val searchText: String = StringUtils.EMPTY,
        val folderTitle: String = StringUtils.EMPTY,
    ) : Parcelable
}

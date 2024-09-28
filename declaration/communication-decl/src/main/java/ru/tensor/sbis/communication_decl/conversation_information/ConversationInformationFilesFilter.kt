package ru.tensor.sbis.communication_decl.conversation_information

import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communication_decl.R

/**
 * Типы фильтров для раздела вложений на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
@Parcelize
sealed class ConversationInformationFilesFilter(
    @StringRes override val caption: Int
) : ConversationInformationFilter {

    /** Медиа. */
    object MediaFiles : ConversationInformationFilesFilter(
        R.string.communication_decl_conversation_information_filter_media
    )

    /** Документы. */
    object Documents : ConversationInformationFilesFilter(
        R.string.communication_decl_conversation_information_filter_docs
    )

    override fun getAllFilters(): List<ConversationInformationFilesFilter> = allFilters

    companion object {

        /** @SelfDocumented */
        val allFilters = listOf(MediaFiles, Documents)
    }
}
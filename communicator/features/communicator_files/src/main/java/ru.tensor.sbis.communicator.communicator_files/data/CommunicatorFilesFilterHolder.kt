package ru.tensor.sbis.communicator.communicator_files.data

import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilesFilter
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentsFileFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import java.util.UUID

/**
 * Холдер фильтра файлов переписки.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesFilterHolder(
    private val filter: ThemeAttachmentFilter
): () -> ThemeAttachmentFilter {

    override fun invoke(): ThemeAttachmentFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setQuery(query: String) {
        filter.searchQuery = query
    }

    /** @SelfDocumented */
    fun setFolderId(id: UUID? = null) {
        filter.folderId = id
    }

    /** @SelfDocumented */
    fun setFilters(filters: List<ConversationInformationFilter>) {
        filter.filters = filters.toThemeAttachmentsFileFilters()
    }

    private fun List<ConversationInformationFilter>.toThemeAttachmentsFileFilters(): ArrayList<ThemeAttachmentsFileFilter> {
        return mutableListOf<ThemeAttachmentsFileFilter>().apply {
            this@toThemeAttachmentsFileFilters.forEach {
                when (it) {
                    ConversationInformationFilesFilter.MediaFiles -> add(ThemeAttachmentsFileFilter.MULTIMEDIA)
                    ConversationInformationFilesFilter.Documents -> add(ThemeAttachmentsFileFilter.DOCUMENTS)
                }
            }
        }.asArrayList()
    }
}
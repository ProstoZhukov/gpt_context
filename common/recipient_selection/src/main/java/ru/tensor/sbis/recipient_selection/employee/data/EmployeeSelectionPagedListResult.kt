package ru.tensor.sbis.recipient_selection.employee.data

import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.generated.CommandStatus

/**
 * Обёртка над PagedListResult<MultiSelectionItem> для добавления родительской папки (ParentFolderData)
 */
internal class EmployeeSelectionPagedListResult<DATA>(
        data: List<DATA>,
        status: CommandStatus,
        hasMore: Boolean,
        isFullyCached: Boolean,
        val parentFolder: ParentFolderData
) : PagedListResult<DATA>(data, status, hasMore, isFullyCached)
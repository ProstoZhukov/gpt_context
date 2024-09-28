package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import java.io.Serializable
import java.util.*

/**
 * CRUD-фильтр для участников диалога/чата
 */
internal class ThemeParticipantsListFilter : Serializable, ListFilter() {

    /** Uuid диалога/чата. */
    var theme: UUID = UUIDUtils.NIL_UUID

    /** Uuid текущей папки. */
    var folderUUID: UUID? = null

    override fun queryBuilder(): Builder<*, *> =
        ThemeParticipantsFilterBuilder(theme, folderUUID)
            .searchQuery(mSearchQuery)

    private class ThemeParticipantsFilterBuilder(private var theme: UUID, private var folderUUID: UUID?) :
        AnchorPositionQueryBuilder<Any, ThemeParticipantsFilter>() {

        override fun build(): ThemeParticipantsFilter {
            val fromUuid = mAnchorModel?.let {
                (it as? ThemeParticipantListItem.ThemeParticipant)?.employeeProfile?.uuid
            }
            val searchQuery = if (mSearchQuery.isNullOrEmpty()) null else mSearchQuery

            return ThemeParticipantsFilter(theme, fromUuid, mDirection!!, mItemsCount, mDirection == QueryDirection.TO_NEWER, searchQuery, folderUUID)
        }
    }
}

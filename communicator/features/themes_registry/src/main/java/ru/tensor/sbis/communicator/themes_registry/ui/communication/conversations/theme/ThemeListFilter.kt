package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.communicator.generated.ChatFilter
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.ConversationType
import ru.tensor.sbis.communicator.generated.DialogFilter
import ru.tensor.sbis.communicator.generated.ThemeFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.filters.ConversationFilterConfiguration
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.persons.ConversationRegistryItem
import java.util.*

internal typealias DeclaredDialogType = DialogType
internal typealias DeclaredChatType = ru.tensor.sbis.communicator.declaration.model.ChatType

/**
 * CRUD-фильтр для реестра диалогов/чатов.
 *
 * @author rv.krohalev
 */
internal class ThemeListFilter : ListFilter() {
    var filterConfiguration: ConversationFilterConfiguration? = null
    var conversationType: ConversationType = ConversationType.DIALOG
    var personUuid: UUID? = null
    var minItemsCount: Int = 0
    var limitItems: Int? = null

    override fun queryBuilder(): Builder<*, *> = ThemeListFilterBuilder(this).searchQuery(mSearchQuery)

    /** @SelfDocumented */
    fun getSearchQuery(): String? = mSearchQuery

    internal class ThemeListFilterBuilder(
        private val container: ThemeListFilter
    ) : AnchorPositionQueryBuilder<ConversationRegistryItem, ThemeFilter>() {

        override fun build(): ThemeFilter {
            val folderUuid = UUIDUtils.fromString(container.filterConfiguration?.folderUuid)?.takeIf {
                !UUIDUtils.isNilUuid(it)
            }

            // TODO для SabyGet выставить в ChatType.SABY
            val chatType = ChatType.UNKNOWN
            val conversationModel = mAnchorModel as? ConversationModel

            return ThemeFilter(
                null,
                container.personUuid,
                container.conversationType,
                folderUuid,
                container.filterConfiguration?.dialogType.asControllerDialogFilter(),
                container.filterConfiguration?.chatType.asControllerChatFilter(),
                chatType,
                mSearchQuery ?: "",
                mDirection!!,
                conversationModel?.timestamp ?: 0,
                conversationModel?.favoriteTimestamp ?: 0,
                conversationModel?.messageUuid,
                calcItemsCountForRequest(),
                mInclusive,
                arrayListOf(),
                mFromPullRefresh,
                null
            )
        }

        /**
         * Метод вычисляет количество элементов для запроса с контроллера.
         *
         * При шаринге запрашиваем всегда ограниченное количество диалогов [limitItems].
         * Для первой запрашиваемой порции диалогов запрашивать [minItemsCount] элементов.
         * Во всех остальных случаях запрашивать [mItemsCount] элементов, вычисленное общим компонентом.
         * @return количество item'ов
         */
        private fun calcItemsCountForRequest(): Int {
            return container.limitItems
                ?: if (container.conversationType == ConversationType.DIALOG && !mSearchQuery.isNullOrEmpty() &&
                    mDirection == QueryDirection.TO_OLDER && (mAnchorModel as? ConversationModel)?.messageUuid == null
                ) {
                    container.minItemsCount
                } else {
                    mItemsCount
                }
        }
    }

    /**
     * Возвращает true, если чат скрытый или диалог перемещён в архив.
     */
    fun isConversationHiddenOrArchived(): Boolean {
        return (filterConfiguration!!.chatType == DeclaredChatType.HIDDEN && conversationType == ConversationType.CHAT) ||
            (filterConfiguration!!.dialogType == DeclaredDialogType.DELETED && conversationType == ConversationType.DIALOG)
    }
}

private fun DeclaredDialogType?.asControllerDialogFilter() = when (this) {
    null, DeclaredDialogType.ALL  -> DialogFilter.ALL
    DeclaredDialogType.INCOMING   -> DialogFilter.INCOMING
    DeclaredDialogType.UNANSWERED -> DialogFilter.UNANSWERED
    DeclaredDialogType.UNREAD     -> DialogFilter.UNREAD
    DeclaredDialogType.DELETED    -> DialogFilter.DELETED
}

internal fun DeclaredChatType?.asControllerChatFilter() = when (this) {
    null, DeclaredChatType.ALL -> ChatFilter.ALL
    DeclaredChatType.UNREAD    -> ChatFilter.UNREAD
    DeclaredChatType.HIDDEN    -> ChatFilter.HIDDEN
}

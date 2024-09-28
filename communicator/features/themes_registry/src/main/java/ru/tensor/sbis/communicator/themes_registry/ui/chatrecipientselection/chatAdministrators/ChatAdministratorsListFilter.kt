package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.common.util.UUIDUtils
import java.io.Serializable
import java.util.*

/**
 * CRUD-фильтр для экрана "Администраторы чата"
 */
internal class ChatAdministratorsListFilter : Serializable, ListFilter() {

    var theme: UUID = UUIDUtils.NIL_UUID

    override fun queryBuilder(): Builder<*, *> =
            ChatAdministratorsFilterBuilder(theme)
                    .searchQuery(mSearchQuery)

    private class ChatAdministratorsFilterBuilder(private var theme: UUID) : AnchorPositionQueryBuilder<Any, ChatAdministratorsFilter>() {

        override fun build(): ChatAdministratorsFilter =
                ChatAdministratorsFilter(theme, mFromPosition, mItemsCount)
    }
}
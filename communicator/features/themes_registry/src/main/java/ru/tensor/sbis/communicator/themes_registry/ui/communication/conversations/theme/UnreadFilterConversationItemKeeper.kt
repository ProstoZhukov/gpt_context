package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import io.reactivex.Observable
import org.jetbrains.annotations.NotNull
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * Обёртка над командой фильтрации, хранящая элементы в списке непрочитанных до момента ручного обновления.
 * Использует для своей работы специальные возможности контроллера.
 *
 * @author rv.krohalev
 */
internal class UnreadFilterConversationItemKeeper(
    val themeListCommand: @NotNull ThemeListCommand
) : BaseListObservableCommand<PagedListResult<ConversationRegistryItem>, ThemeFilter, DataRefreshedThemeControllerCallback> by themeListCommand {
    private val cache: MutableSet<UUID> = LinkedHashSet()

    /** @SelfDocumented */
    fun notifyItemMightLeave(id: UUID) {
        cache += id
    }

    /** @SelfDocumented */
    fun doNotKeep(ids: Collection<UUID>) {
        cache.removeAll(ids)
    }

    override fun refresh(filter: ThemeFilter): Observable<PagedListResult<ConversationRegistryItem>> {
        if (filter.isUnread()) {
            filter.additionalIds = cache.asArrayList()
        }
        return themeListCommand.refresh(filter)
    }

    override fun list(filter: ThemeFilter): Observable<PagedListResult<ConversationRegistryItem>> {
        cache.clear()
        return themeListCommand.list(filter)
    }

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedThemeControllerCallback): Observable<Subscription> =
        themeListCommand.subscribeDataRefreshedEvent(callback)
}

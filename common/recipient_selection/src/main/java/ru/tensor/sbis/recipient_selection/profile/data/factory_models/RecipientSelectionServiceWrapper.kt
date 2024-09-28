package ru.tensor.sbis.recipient_selection.profile.data.factory_models

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.generated.GetStatus
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import javax.inject.Inject

/**
 * Реализация обертки над микросервисом [RecipientsController] для получения списков получателей/групп сотрудников.
 * Контроллер не крудовый, поэтому на list и refresh зовется один и тот же метод с разными фильтрами.
 * Первый вызов метода отдает результаты из кэша, если с запроса результатов придет меньше, чем запрашивалось,
 * то в следующем вызове необходимо передать excludeList с ранее полученными UUID, в этом сценарии запрос с контроллера будет в облако
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionServiceWrapper(
    controllerLazy: Lazy<RecipientsController>
) : ServiceWrapper<ProfilesFoldersResult, RecipientsSearchFilter> {

    private val controller by controllerLazy

    @Inject constructor(): this(lazy { RecipientsController.instance() })

    // контроллер без CRUD фасада и не работает c DataRefreshCallback
    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    @Synchronized
    override fun list(filter: RecipientsSearchFilter): ProfilesFoldersResult =
        controller.getRecipients(filter)
            .let {
                val diffCount = filter.count - it.size
                if (it.hasMore && diffCount > 0) getMoreRecipients(filter, it) else it
            }

    private fun getMoreRecipients(
        filter: RecipientsSearchFilter,
        cacheResult: ProfilesFoldersResult
    ): ProfilesFoldersResult {
        val excludeList = filter.excludeList
            .plus(cacheResult.uuidList.map { it.toString() })

        val loadMoreFilter = filter.copy(
            excludeList = excludeList,
            count = filter.count - cacheResult.size
        )

        return controller.getRecipients(loadMoreFilter, cacheResult)
    }

    override fun refresh(filter: RecipientsSearchFilter, params: Map<String, String>): ProfilesFoldersResult =
        controller.getRecipients(filter)

    /**
     * Получить список получателей, в результате содержатся списки получателей и папок(отделов/групп) сотрудников по запросу.
     */
    private fun RecipientsController.getRecipients(
        filter: RecipientsSearchFilter,
        cacheResult: ProfilesFoldersResult? = null
    ): ProfilesFoldersResult = filter.run {
        val excludeUuidList = excludeList.map { UUIDUtils.fromString(it) }.asArrayList()
        val result = when {
            // новый чат
            isNewConversation && isChat ->
                getRecipientsList(searchString, excludeUuidList, count, false, filter.containsWorkingGroups)
            // новый диалог или обсуждение по задаче
            isNewConversation || conversationType == ConversationType.DOCUMENT_CONVERSATION ->
                getDialogRecipientsList(searchString, dialogUuid, documentUuid, excludeUuidList, count)
            // релевантные получатели по существующему чату
            isChat && dialogUuid != null ->
                getChatRecipientsList(searchString, dialogUuid, documentUuid, onlyParticipants, excludeParticipants, excludeUuidList, count)
            // релевантные получатели по существующему диалогу
            dialogUuid != null ->
                getDialogRecipientsList(searchString, dialogUuid, documentUuid, excludeUuidList, count)
            /*
            Остальные сценарии. Например:
            - выбор получателей для доступа к документу (не переписка)
             */
            else ->
                getRecipientsList(searchString, excludeUuidList, count, false, filter.containsWorkingGroups)
        }
        ProfilesFoldersResult(result).mergeWith(cacheResult)
    }

    /**
     * Объединить текущие результаты с переданными.
     * Необходим для получения единого списка по результатам двух запросов в кэш и облако.
     */
    private fun ProfilesFoldersResult.mergeWith(another: ProfilesFoldersResult?) =
        if (status == GetStatus.SUCCES_CLOUD || status == GetStatus.SUCCES_LOCAL_CACHE) {
            another?.let {
                profiles = it.profiles.plus(profiles).asArrayList()
                folders = it.folders.plus(folders).asArrayList()
            }
            this
        } else another ?: this
}
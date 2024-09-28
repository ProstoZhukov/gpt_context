package ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.dialog_selection.data.RecipientsFilter
import ru.tensor.sbis.communicator.dialog_selection.data.mapper.size
import ru.tensor.sbis.communicator.dialog_selection.data.mapper.uuidList
import ru.tensor.sbis.communicator.generated.GetStatus
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.communicator.generated.ProfilesFoldersResult
import javax.inject.Inject

/**
 * Результат контроллера получателей
 */
internal typealias RecipientsServiceResult = ProfilesFoldersResult

/**
 * Реализация обертки контроллера получателей для экрана выбора диалога/участников
 * @property controller провайдер контроллера получателей [RecipientsController]
 *
 * @author vv.chekurda
 */
internal class RecipientsServiceWrapperImpl @Inject constructor(
    private val controller: DependencyProvider<RecipientsController>
) : RecipientsServiceWrapper {

    private var lastResult: RecipientsServiceResult? = null

    @Synchronized
    override fun list(filter: RecipientsFilter): RecipientsServiceResult =
        getRecipients(filter).let {
            val diffCount = filter.count - it.size
            if (it.hasMore && diffCount > 0) getMoreRecipients(filter, it) else it
        }.also {
            lastResult = it
        }

    private fun getMoreRecipients(filter: RecipientsFilter, cacheResult: ProfilesFoldersResult): ProfilesFoldersResult {
        val excludeList = filter.excludeList
            .plus(cacheResult.uuidList.map { it.toString() })

        val loadMoreFilter = filter.copy(
            excludeList = excludeList,
            count = filter.count - cacheResult.size
        )

        return getRecipients(loadMoreFilter, cacheResult)
    }

    override fun refresh(filter: RecipientsFilter): RecipientsServiceResult =
        lastResult ?: RecipientsServiceResult()

    private fun getRecipients(
        filter: RecipientsFilter,
        cacheResult: RecipientsServiceResult? = null
    ): RecipientsServiceResult = filter.run {
        val excludeUuidList = excludeList.map { UUIDUtils.fromString(it) }.asArrayList()
        controller.get()
            .getDialogRecipientsList(searchString, null, null, excludeUuidList, count)
            .mergeWith(cacheResult)
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
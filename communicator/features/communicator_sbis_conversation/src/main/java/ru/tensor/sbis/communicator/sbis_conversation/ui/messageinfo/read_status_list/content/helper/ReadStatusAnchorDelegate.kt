package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.helper

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.AnchorReadStatus

/**
 * Делегат для создания модели якоря из метадаты результата контроллера на экране информации о сообщении
 *
 * @author vv.chekurda
 */
internal interface ReadStatusAnchorDelegate {

    companion object {
        private const val ANCHOR_UUID_KEY = "person_uuid"
        private const val ANCHOR_READ_KEY = "datetimeRead"
        private const val ANCHOR_SEND_KEY = "datetimeSend"
    }

    /**
     * Создать якорь для следующей страницы
     *
     * @param meta метадата результата запроса страницы
     * @return модель якоря для загрузки следующей страницы
     */
    fun createNextPageAnchor(meta: HashMap<String, String>?): AnchorReadStatus =
        meta?.let {
            AnchorReadStatus(
                UUIDUtils.fromString(it[ANCHOR_UUID_KEY]),
                it[ANCHOR_READ_KEY]?.toLongOrNull(),
                it[ANCHOR_SEND_KEY]?.toLongOrNull()
            )
        } ?: AnchorReadStatus()

    /**
     * Якорь для предыдущей страницы, контроллер с односторонней пагинацией, поэтому - stub якорь
     */
    fun createPreviousPageAnchor(): AnchorReadStatus = AnchorReadStatus()
}
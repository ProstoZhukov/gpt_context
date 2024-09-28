package ru.tensor.sbis.design.cloud_view.utils.thread

import ru.tensor.sbis.design.cloud_view.thread.data.ThreadData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Вспомогательная реализация для реализации костыля вытягивания облачков исходящих сообщений до ширины тредов
 * во имя красоты.
 *
 * Модели сообщений никак логически не связаны с сервисными сообщениями тредов,
 * поэтому данный класс является связующим звеном,
 * чтобы иметь возможность быстро проверить и найти для сообщения его дочерний тред.
 *
 * Иной некостыльный способ только переписывать всю облачнгую и контроллерную БЛ ради этой мелкой
 * интерфейсной доработки.
 *
 * @author vv.chekurda
 */
object CloudThreadHelper {

    private val outcomeThreads = ConcurrentHashMap<UUID, ThreadData>()

    /**
     * Получить данные по дочернему треду для сообщения [outcomeMessageUuid].
     */
    fun getChildThreadData(outcomeMessageUuid: UUID): ThreadData? =
        outcomeThreads[outcomeMessageUuid]

    /**
     * Добавить информация об исходящем треде [threadData] для исходящего сообщения [outcomeParentMessageUuid].
     */
    fun addOutcomeThread(outcomeParentMessageUuid: UUID, threadData: ThreadData) {
        outcomeThreads[outcomeParentMessageUuid] = threadData
    }
}
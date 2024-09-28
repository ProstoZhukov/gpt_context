package ru.tensor.sbis.communicator.base.conversation.presentation.crud

import android.content.Context
import ru.tensor.sbis.common.util.DeviceConfigurationUtils

/**
 * Вспомогательная реализация с настройками размера списка сообщений.
 *
 * @author vv.chekurda
 */
object ConversationListSizeSettings {

    private lateinit var appContext: Context

    /**
     * Признак планшета.
     */
    val isTablet: Boolean
        get() = DeviceConfigurationUtils.isTablet(appContext)

    /**
     * Размер одной страницы.
     */
    val pageSize = 5

    /**
     * Количество элементов до ближайшей границы списка для триггера пагинации.
     */
    val paginationReserveCount = 10

    /**
     * Размер списка коллекции.
     */
    val listSize: Int
        get() = if (isTablet) {
            CONVERSATION_TABLET_LIST_SIZE
        } else {
            CONVERSATION_PHONE_LIST_SIZE
        }

    fun init(appContext: Context) {
        this.appContext = appContext
    }
}

private const val CONVERSATION_PHONE_LIST_SIZE = 45
private const val CONVERSATION_TABLET_LIST_SIZE = 60
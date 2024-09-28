package ru.tensor.sbis.design.message_view.contact

import android.content.Context
import ru.tensor.sbis.design.message_view.mapper.MessageViewDataMapper

/**
 * Фабрика для создания [MessageViewDataMapper].
 *
 * @author vv.chekurda
 */
interface MessageViewDataMapperFactory {

    /**
     * Создать [MessageViewDataMapper].
     *
     * @param context контекст
     * @param isChat true, если чат
     * @param isCrmMessageForOperator true, если crm оператора
     * @param optimizeConvert true, если необходимо оптимизировать конвертацию богатого текста на уровне маппинга.
     * В этом случае конвертация будет происходить только для видимых ячеек при биндинге.
     */
    fun createMessageViewDataMapper(
        context: Context,
        isChat: Boolean = false,
        isCrmMessageForOperator: Boolean = false,
        optimizeConvert: Boolean = false
    ): MessageViewDataMapper
}
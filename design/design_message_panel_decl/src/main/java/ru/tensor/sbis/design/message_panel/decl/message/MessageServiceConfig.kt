package ru.tensor.sbis.design.message_panel.decl.message

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Модель аггрегирует реализации для консистентности параметризующих типов в рамках одной конфигурации
 *
 * @author ma.kolpakov
 */
data class MessageServiceConfig<MESSAGE, RESULT>(
    val service: MessageService<MESSAGE, RESULT>,
    val serviceHelper: MessageServiceHelper<MESSAGE, RESULT>
) : Feature

package ru.tensor.sbis.design.message_panel.decl.draft

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Модель аггрегирует реализации для консистентности параметризующих типов в рамках одной конфигурации
 *
 * @author ma.kolpakov
 */
data class MessageDraftServiceConfig<DRAFT>(
    val service: MessageDraftService<DRAFT>,
    val serviceHelper: MessageDraftServiceHelper<DRAFT>
) : Feature

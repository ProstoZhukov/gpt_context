package ru.tensor.sbis.design.message_panel.decl.recipients

import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Модель аггрегирует реализации для консистентности параметризующих типов в рамках одной конфигурации
 *
 * @author ma.kolpakov
 */
data class RecipientServiceConfig<RECIPIENT : IContactVM>(
    val service: RecipientService<RECIPIENT>,
    val serviceHelper: RecipientServiceHelper<RECIPIENT>
) : Feature

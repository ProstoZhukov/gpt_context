package ru.tensor.sbis.edo.additional_fields.decl.service

import kotlinx.coroutines.Dispatchers
import java.util.UUID

/**
 * Поставщик идентификаторов полей, которые можно редактировать в режиме просмотра
 * Как правило, это обязательные этапные доп поля
 *
 * Стандартная реализация расположена в модуле edo_additional_fields_integration репозитория android-edo
 */
interface AdditionalFieldsEditableInViewModeIdsProvider {

    /**
     * Получить список идентификаторов полей, редактируемых в режиме просмотра
     *
     * Переключение корутиновских [Dispatchers] должно быть в реализации
     */
    suspend fun getEditableInViewModeIds(): List<UUID>
}
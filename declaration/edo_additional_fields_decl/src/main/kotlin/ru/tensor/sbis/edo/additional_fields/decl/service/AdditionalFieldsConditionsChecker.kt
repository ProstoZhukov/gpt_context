package ru.tensor.sbis.edo.additional_fields.decl.service

import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemModel
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemsModels
import ru.tensor.sbis.edo.additional_fields.decl.model.raw_data.FieldRawData

/**
 * Интерфейс для проверки условий, завязанных на доп поля
 *
 * Например, это проверка условий видимости
 * Наличие условия видимости на доп поле определяется непустым свойством [FieldRawData.visibilityCondition]
 * у элемента [AdditionalItemModel.Field]
 *
 * Стандартная реализация расположена в модуле edo_additional_fields_integration репозитория android-edo
 *
 * @author sa.nikitin
 */
interface AdditionalFieldsConditionsChecker {

    /**
     * Нужно ли проверять условия, завязанные на доп поля
     * Если вернуть true, то будет вызван [checkConditions]
     */
    fun isNeedCheckConditions(): Boolean

    /**
     * Проверить условия, завязанные на доп поля
     * Параметр [models] определяет доп поля с текущими значениями
     *
     * Вернуть модели доп полей
     * При этом их состав может быть изменен, например, удалены скрытые условиями видимости
     * Если составь не изменился, то следует вернуть [models]
     *
     * Переключение корутиновских [Dispatchers] должно быть в реализации
     */
    @Throws(Exception::class)
    suspend fun checkConditions(models: AdditionalItemsModels): AdditionalItemsModels
}
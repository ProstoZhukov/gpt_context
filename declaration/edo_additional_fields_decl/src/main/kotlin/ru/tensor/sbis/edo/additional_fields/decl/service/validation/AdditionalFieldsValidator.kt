package ru.tensor.sbis.edo.additional_fields.decl.service.validation

import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemModel
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemsModels
import ru.tensor.sbis.edo.additional_fields.decl.model.raw_data.FieldRawData

/**
 * Интерфейс для проверки значений доп полей
 * Проверять заполненность обязательных полей и выполнение условий на значения
 *
 * Обязательность доп поля определяется свойством [FieldRawData.isRequired]
 * у элемента [AdditionalItemModel.Field]
 * Наличие условия на значение доп поля определяется непустым свойством [FieldRawData.valueValidationCondition]
 * у элемента [AdditionalItemModel.Field]
 * Также может быть проверка на соответствие маски доп поля, см. [AdditionalItemModel.Field.Masked]
 *
 * Стандартная реализация расположена в модуле edo_additional_fields_integration репозитория android-edo
 *
 * @author sa.nikitin
 */
interface AdditionalFieldsValidator {

    /**
     * Результат проверки значений доп полей
     *
     * @author sa.nikitin
     */
    sealed interface Result {

        /**
         * Проверка прервана исключением
         */
        class CheckFailed(val exception: Exception) : Result

        /**
         * Ошибка при проверке полей
         *
         * @property validationFailures Ошибки проверки, сгруппированные по тексту ошибки
         */
        class Error(val validationFailures: List<AdditionalFieldsValidationFailure>) : Result

        /** @SelfDocumented */
        object Success : Result
    }

    /**
     * Проверить значения доп полей
     * Параметр [models] определяет доп поля с текущими значениями
     *
     * Вернуть результат проверки
     *
     * Переключение корутиновских [Dispatchers] должно быть в реализации
     */
    suspend fun validate(models: AdditionalItemsModels): Result
}
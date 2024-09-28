package ru.tensor.sbis.edo.additional_fields.decl.service.validation

import ru.tensor.sbis.edo.additional_fields.decl.AdditionalFieldsComponent
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemsModels
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Результат получения моделей доп полей
 *
 * @author sa.nikitin
 */
sealed interface AdditionalFieldsGetModelsResult {

    /**
     * Валидация моделей прошла успешно
     *
     * [models] будут null, если получение моделей выполняется до вызова [AdditionalFieldsComponent.setConfig]
     */
    class Success(val models: AdditionalItemsModels?) : AdditionalFieldsGetModelsResult

    /**
     * Валидация моделей не прошла
     * Ошибки будут автоматически отображены на проблемных доп полях
     *
     * На прикладной стороне нужно прокрутить к первому полю с ошибкой [ValidationFail.itemToScroll]
     * [ValidationFail.itemToScroll] будет null, если не удалось провести валидацию из-за отсутствия сети, например
     * Иные действия делать не нужно, в том числе отображение общего сообщения об ошибке через тост, например
     */
    class ValidationFail(val itemToScroll: AnyItem?) : AdditionalFieldsGetModelsResult
}
package ru.tensor.sbis.edo.additional_fields.decl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemsModels
import ru.tensor.sbis.edo.additional_fields.decl.service.validation.AdditionalFieldsGetModelsResult
import ru.tensor.sbis.edo.additional_fields.decl.service.validation.AutoValidationFail
import ru.tensor.sbis.list.view.binding.BindingItem

typealias SbisListItems = List<BindingItem<*>>
typealias SbisListItemsStateFlow = StateFlow<SbisListItems>
typealias SbisListItemsFlow = Flow<SbisListItems>

/**
 * Компонент доп полей документа
 *
 * Создать компонент можно с помощью [AdditionalFieldsComponentFactory]
 *
 * Для использования выполните шаги:
 *  1. Установите конфиг, см. [setConfig]
 *  2. Установите режим работы, см. [setMode]
 *  3. Установите модели доп полей, см. [setModels]
 *  4. Подпишитесь на готовый к отображению список элементов, см. [items]
 *
 * Для получения полей после редактирования используйте [getModels]
 *
 * @author sa.nikitin
 */
interface AdditionalFieldsComponent {

    /**
     * Flow из List<BindingItem<*>>, где каждый элемент представляет собой доп поле.
     * Следует подписаться и обновлять SbisList на каждый новый список
     */
    val items: SbisListItemsFlow

    /**
     * События изменения любого из доп полей
     *
     * На прикладной стороне нужно использовать для отображения кнопки сохранения,
     * если в режиме просмотра были отредактированы некоторые поля,
     * см. [AdditionalFieldsComponentMode.View.isSomeEditable]
     * Иные действия делать не нужно
     *
     * Этим механизмом обрабатывается ситуация с редактированием обязательных этапных доп полей
     */
    val fieldValueChangedEvents: Flow<Unit>

    /**
     * Ошибки автоматической валидации
     *
     * На прикладной стороне нужно прокрутить к первому полю с ошибкой [AutoValidationFail.itemToScroll]
     * Иные действия делать не нужно
     *
     * Этим механизмом обрабатывается ситуация с отображением ошибок
     * при попытке выполнить переход через ДЗЗ с незаполненными доп полями
     */
    val autoValidationFails: Flow<AutoValidationFail>

    /**
     * Установить конфиг компонента
     */
    fun setConfig(config: AdditionalFieldsComponentConfig)

    /**
     * Установить режим работы компонента
     */
    fun setMode(mode: AdditionalFieldsComponentMode)

    /**
     * Установить модели доп полей
     */
    fun setModels(models: () -> AdditionalItemsModels)

    /**
     * Получить модели доп полей в виде [AdditionalFieldsGetModelsResult]
     * Не выдает сразу [AdditionalItemsModels], т.к. требуется валидация полей на корректность
     *
     * @param showValidationErrorMessage Отображать ли ошибку валидации полей
     *
     * @see getModelsWithoutValidate
     */
    suspend fun getModels(showValidationErrorMessage: Boolean = true): AdditionalFieldsGetModelsResult

    /**
     * Получить модели без проведения валидации
     */
    fun getModelsWithoutValidate(): AdditionalItemsModels?
}
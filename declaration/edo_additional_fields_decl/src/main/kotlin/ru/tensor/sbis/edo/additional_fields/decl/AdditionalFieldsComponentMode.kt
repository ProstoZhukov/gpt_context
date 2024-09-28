package ru.tensor.sbis.edo.additional_fields.decl

import ru.tensor.sbis.edo.additional_fields.decl.service.AdditionalFieldsEditableInViewModeIdsProvider
import ru.tensor.sbis.edo.additional_fields.decl.service.AdditionalFieldsService

/**
 * Режим работы компонента доп полей
 *
 * @author sa.nikitin
 */
sealed interface AdditionalFieldsComponentMode {

    /**
     * Режим просмотра
     * Доп поля не редактируются
     *
     * Для включения редактирования некоторых доп полей в режиме просмотра, нужно передать [isSomeEditable] true.
     * При этом список редактируемых доп полей будет поставлен с помощью [AdditionalFieldsEditableInViewModeIdsProvider]
     * Его реализация передается через [AdditionalFieldsService.editableInViewModeIdsProvider],
     * а [AdditionalFieldsService] передается в компонент через [AdditionalFieldsComponentConfig.service]
     * Для отображения кнопки сохранения, нужно подписаться на [AdditionalFieldsComponent.fieldValueChangedEvents]
     *
     * @property collapsedFieldsCount   Количество отображаемых непустых или обязательных доп полей в свернутом режиме
     *                                  Если передать null, то будут отображены все непустые или обязательные
     *
     * @property isSomeEditable         Редактируемы ли некоторые доп поля, например, обязательные этапные
     *
     * @property showEmptyGroups        Отображать ли пустые группы доп полей
     *
     * @author sa.nikitin
     */
    data class View(
        val collapsedFieldsCount: Int? = 7,
        val isSomeEditable: Boolean = true,
        val showEmptyGroups: Boolean = false
    ) : AdditionalFieldsComponentMode

    /**
     * Режим изменения
     * Все доп поля редактируются
     *
     * @author sa.nikitin
     */
    class Edit : AdditionalFieldsComponentMode {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int = javaClass.hashCode()

        override fun toString(): String = "AdditionalFieldsComponentMode.Edit"
    }
}
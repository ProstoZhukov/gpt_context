package ru.tensor.sbis.design.radio_group.control.models

import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupViewApi

/**
 * Модель радиокнопки.
 *
 * [id] идентификатор радиокнопки.
 * Используется для указания выбранной радиокнопки и отслеживания изменения выбранной радиокнопки.
 *
 * [content] контент радиокнопки. Может быть либо стандартный текст, либо прикладная view.
 *
 * [readOnly] состояние доступности радиокнопки.
 * Для отключения всех элементов группы используется [SbisRadioGroupViewApi.readOnly].
 *
 * [children] дочерние элементы радиокнопки. Используется для построения иерархического списка.
 *
 * [parentId] id родительской радиокнопки.
 * Может использоваться для проверки выбранного состояния родителя.
 * Устанавливается компонентом при построении списка радиокнопок.
 */
class SbisRadioGroupItem(
    val id: String,
    val content: SbisRadioGroupContent,
    val readOnly: Boolean = false,
    val children: List<SbisRadioGroupItem> = emptyList()
) {
    var parentId: String? = null
        internal set
}

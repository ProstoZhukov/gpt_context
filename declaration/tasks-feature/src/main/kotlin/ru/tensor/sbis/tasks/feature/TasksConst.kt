package ru.tensor.sbis.tasks.feature

/**
 * Общие константы модулей задач.
 *
 * @author aa.sviridov
 */
object TasksConst {

    /**
     * Тип документа задачи (по-умолчанию).
     */
    const val DEFAULT_DOC_TYPE = "СлужЗап"

    /**
     * Тип документа проект.
     */
    const val PROJECT_DOC_TYPE = "Проект"

    /**
     * Ключ для вызова мастера создания задачи, либо карточки задачи, основанных на ЭДО "движке".
     */
    const val KEY_EDO_BASED_TASK_CARD = "EdoBased"

    /**
     * Ключ модуля, к которому принадлежит карточка опубликованной инструкции.
     */
    const val INSTRUCTIONS_MODULE_KEY = "Instructions"
}
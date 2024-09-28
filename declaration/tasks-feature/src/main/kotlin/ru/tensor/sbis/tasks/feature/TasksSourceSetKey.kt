package ru.tensor.sbis.tasks.feature

import kotlinx.parcelize.Parcelize

/**
 * Ключ набора источников для карточки документа по-умолчанию - задач.
 *
 * @author aa.sviridov
 */
@Parcelize
object TasksSourceSetKey: SourceSetKey() {

    override val moduleKey
        get() = "Tasks"
}
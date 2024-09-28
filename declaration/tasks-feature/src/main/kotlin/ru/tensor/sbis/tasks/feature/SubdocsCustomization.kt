package ru.tensor.sbis.tasks.feature

import androidx.annotation.PluralsRes

/**
 * Дополнительные параметры кастомизации для компонента поддокументов.
 * @property headerPlural идентификатор ресурса множественного для форматирования окончаний заголовка, например
 * "5 подзадач" или "1 подзадача", 0 - по-умолчанию, "5 поддокументов" или "1 поддокумент", null - нет элемента
 * заголовка.
 *
 * @author aa.sviridov
 */
class SubdocsCustomization(
    @PluralsRes val headerPlural: Int? = 0,
)
package ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model

import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util.ELLIPSIS_ID

/**
 * Модель элемента хлебных крошек
 *
 * @property title текст элемента
 * @property id идентификатор, уникальный для элемента в иерархии. Значение [ELLIPSIS_ID] не допускается
 * (зарезервировано)
 * @property highlights список подсвечиваемых областей в [title], не выходящих за пределы строки
 *
 * @author us.bessonov
 */
data class BreadCrumb(
    val title: String,
    val id: String,
    val highlights: List<IntRange> = emptyList()
) {
    /**
     * Позволяет создавать элемент хлебных крошек с числовым идентификатором. Стоит учесть, что значение будет
     * неявно преобразовано к строковому (при обработке нажатий может потребоваться обратное преобразование)
     */
    constructor(
        title: String,
        id: Long,
        highlights: List<IntRange> = emptyList()
    ) : this(title, id.toString(), highlights)
}
/**
 * Файл содержит инструмент для отображения хлебных крошек в BreadCrumbsView в превью макета
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util

import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.BreadCrumbsView
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb

/**
 * Отображает список демонстрационных хлебных крошек
 */
internal fun BreadCrumbsView.showPreview() {
    setItems(previewItems)
}

private val previewItems = listOf(
    "Документация компаний",
    "Здравица ОАО",
    "Отчеты",
    "Отчеты бухгалтерии",
    "Производственные активы"
).mapIndexed { i, it ->
    BreadCrumb(it, i.toString())
}
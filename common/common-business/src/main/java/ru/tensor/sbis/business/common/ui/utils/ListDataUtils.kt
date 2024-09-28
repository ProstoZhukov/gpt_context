package ru.tensor.sbis.business.common.ui.utils

import androidx.databinding.BaseObservable
import ru.tensor.sbis.business.common.data.FolderStructureItemVmProvider
import ru.tensor.sbis.business.common.ui.viewmodel.BreadCrumbsVm
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb

/**
 * @return все диапазоны вхождения подстроки [substring] в [text], без учета регистра.
 */
fun findAllSubstringOccurrenceRangesCaseInsensitive(text: String, substring: String): List<IntRange> =
    Regex("(?i)$substring")
        .findAll(text)
        .map(MatchResult::range)
        .toList()

/**
 * Формирует список с добавлением [BreadCrumbsVm] на основе списка элементов иерерхической структуры.
 * Предполагается, что в исходном списке группам листовых элементов предшествуют папки, составляющие путь, на основе которого формируется [BreadCrumbsVm].
 */
fun List<FolderStructureItemVmProvider>.toVmListWithBreadCrumbs(): List<BaseObservable> {
    val result = mutableListOf<BaseObservable>()
    val path = mutableListOf<FolderStructureItemVmProvider>()
    forEachIndexed { i, item ->
        if (item.isEmptyFolder) {
            result.add(item.toBaseObservableVM())
        } else {
            if (item.isFolder) {
                // предотвращаем формирование хлебных крошек когда несвязанные (вероятно пустые) папки следую друг за другом
                if (path.isNotEmpty()) {
                    val lastFolderInPath = path.lastOrNull()?.id
                    if (lastFolderInPath != item.parentId) {
                        result.add(createBreadCrumb(path))
                        path.clear()
                    }
                }
                /** родитель есть но отсутствует в пути `path` */
                if (path.isEmpty() && item.hasParent) {
                    val parentItem = find { it.id == item.parentId }
                    if (parentItem != null) {
                        result.add(createBreadCrumb(listOf(parentItem, item)))
                        return@forEachIndexed
                    }
                    val parentBreadcrumb =
                        result.find { it is BreadCrumbsVm && it.parentId == item.parentId } as? BreadCrumbsVm
                    if (parentBreadcrumb != null) {
                        parentBreadcrumb.copy(
                            items = parentBreadcrumb.items + BreadCrumb(item.name, item.id, item.highlightedNameRanges)
                        ).let(result::add)
                        return@forEachIndexed
                    }
                } else {
                    item.also(path::add)
                }
            }
            if (path.isNotEmpty() && (item.isNotFolder || i == lastIndex)) {
                result.add(createBreadCrumb(path))
                path.clear()
            }
            if (item.isNotFolder) {
                result.add(item.toBaseObservableVM())
            }
        }
    }
    return result
}

private fun createBreadCrumb(path: List<FolderStructureItemVmProvider>): BreadCrumbsVm =
    path.map {
        BreadCrumb(it.name, it.id, it.highlightedNameRanges)
    }.let(::BreadCrumbsVm)

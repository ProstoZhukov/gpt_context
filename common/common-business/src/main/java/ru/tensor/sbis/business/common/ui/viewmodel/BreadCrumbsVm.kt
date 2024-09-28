package ru.tensor.sbis.business.common.ui.viewmodel

import androidx.databinding.BaseObservable
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Текст с диапазонами индексов подсвеченных символов.
 *
 * @property text текст
 * @property highlightedRanges диапазоны индексов подсвеченных символов
 */
data class TextWithHighlights(
    val text: String = "",
    val highlightedRanges: List<IntRange> = emptyList()
)

/**
 * Вью-модель хлебных крошек.
 *
 * @property items хлебные крошки
 * @property clickAction действие по клику на элемент
 */
data class BreadCrumbsVm(
    val items: List<BreadCrumb>,
    @Transient
    var clickAction: () -> Unit = {}
) : BaseObservable(),
    ComparableItem<BreadCrumbsVm> {

    /** Идентификатор родителя. */
    val parentId = items.lastOrNull()?.id.orEmpty()

    override fun areTheSame(otherItem: BreadCrumbsVm) =
        areItemsTheSame(otherItem, this)

    companion object {
        val areItemsTheSame = { old: BreadCrumbsVm, new: BreadCrumbsVm -> old.items == new.items }
    }
}
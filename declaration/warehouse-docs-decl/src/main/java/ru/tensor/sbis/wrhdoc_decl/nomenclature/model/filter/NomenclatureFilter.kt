package ru.tensor.sbis.wrhdoc_decl.nomenclature.model.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *  Фильтр наименований документа.
 *
 *  @property filterOption Критерии выборки
 *  @property sortOption Сортировка
 *  @property childSortOption Сортировка вторичная
 *
 *  @author as.mozgolin
 */
@Parcelize
data class NomenclatureFilter(
    val filterOption: FilterOptions? = null,
    val sortOption: SortOptions? = null,
    val childSortOption: SortOptions? = null,
): Parcelable

package ru.tensor.sbis.wrhdoc_decl.nomenclature.model.filter

import androidx.annotation.StringRes
import ru.tensor.sbis.wrhdoc_decl.R

/**
 *  Опции сортировки наименований документа.
 *
 *  @author as.mozgolin
 */
enum class SortOptions(@StringRes val titleId: Int) {
    IN_ORDER_ON_ADDITION(R.string.wrhdoc_nom_in_order_on_addition_title),
    ALPHABETICALLY_ASC(R.string.wrhdoc_nom_alphabetically_asc_title),
    ALPHABETICALLY_DESC(R.string.wrhdoc_nom_alphabetically_desc_title),
    BY_NUMBER_OF_DEVIATIONS(R.string.wrhdoc_nom_by_number_of_deviations_title),
    BY_SUM_OF_DEVIATIONS(R.string.wrhdoc_nom_by_sum_of_deviations_title),
    BY_NUMBER_OF_DEVIATIONS_ASCENDING(R.string.wrhdoc_nom_by_sum_of_deviations_asc_title),
    BY_NUMBER_OF_DEVIATIONS_DESCENDING(R.string.wrhdoc_nom_by_sum_of_deviations_desc_title),
    BY_SUM_OF_DEVIATIONS_ASCENDING(R.string.wrhdoc_nom_by_sum_of_deviations_asc_title),
    BY_SUM_OF_DEVIATIONS_DESCENDING(R.string.wrhdoc_nom_by_sum_of_deviations_desc_title);
}
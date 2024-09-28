package ru.tensor.sbis.wrhdoc_decl.nomenclature.model.filter

import androidx.annotation.StringRes
import ru.tensor.sbis.wrhdoc_decl.R

/**
 *  Опции фильтра наименований документа.
 *
 *  @author as.mozgolin
 */
enum class FilterOptions(@StringRes val titleId: Int) {
    CONFIRMED(R.string.wrhdoc_nom_filter_confirmed),
    NOT_CONFIRMED(R.string.wrhdoc_nom_filter_not_confirmed),

    ALL_NOMENCLATURE(R.string.wrhdoc_nom_without_filters_title),
    NON_ZERO_BALANCES(R.string.wrhdoc_nom_non_zero_balances_title),
    ZERO_BALANCES(R.string.wrhdoc_nom_zero_balances_title),
    NEGATIVE_BALANCES(R.string.wrhdoc_nom_negative_balances_title),
    UNFILLED_BALANCES(R.string.wrhdoc_nom_unfilled_balances_title),
    WITH_DEVIATIONS(R.string.wrhdoc_nom_with_deviations_balances_title),
    DEVIATION_NEGATIVE(R.string.wrhdoc_nom_deviation_negative_title),
    DEVIATION_POSITIVE(R.string.wrhdoc_nom_deviation_positive_title),
}
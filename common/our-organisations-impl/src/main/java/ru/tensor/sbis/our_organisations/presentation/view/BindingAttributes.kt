package ru.tensor.sbis.our_organisations.presentation.view

import android.content.Context
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.base_components.adapter.universal.ItemClickHandler
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.our_organisations.presentation.highlight_helper.SubstringSearchRangeBuilder
import ru.tensor.sbis.our_organisations.presentation.highlight_helper.WordsSearchRangeBuilder
import ru.tensor.sbis.our_organisations.presentation.highlight_helper.findRanges
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM

/**
 * Адаптеры для присвоения цвета текста имени организации в зависимости
 * от статуса ликвидации.
 *
 * @param isEliminated ликвидирована ли организация.
 *
 * @author mv.ilin
 */
@BindingAdapter("bind_color_organisation_name")
internal fun SbisTextView.setColorOrganisationName(isEliminated: Boolean) {
    setTextColor(getColorOrganisationName(isEliminated, context))
}

/**
 * Адаптеры для подсветки поискового текста
 *
 * @param text текст
 * @param searchText поисковой текст
 *
 * @author mv.ilin
 */
@BindingAdapter(value = ["set_text", "set_search_text"], requireAll = true)
internal fun SbisTextView.setSearchText(text: String, searchText: String?) {
    if (searchText.isNullOrEmpty()) {
        setTextWithHighlightRanges(text, null)
        return
    }
    val findRanges =
        text.findRanges(
            searchText = searchText,
            ignoreCase = true,
            searchRangeBuilders = arrayOf(SubstringSearchRangeBuilder(), WordsSearchRangeBuilder())
        )

    setTextWithHighlightRanges(text, findRanges.toList())
}

/**
 * Адаптеры для скрытия/показа маркера, только в при единичном выборе
 *
 * @author mv.ilin
 */
@BindingAdapter("selected", "is_multiple_choice")
internal fun View.setVisibilityMark(selected: Boolean, isMultipleChoice: Boolean) {
    if (isMultipleChoice) isVisible = false
    else isInvisible = !selected
}

/**
 * Адаптеры для отслеживания нажатия на организацию
 *
 * @author mv.ilin
 */
@Suppress("UNCHECKED_CAST")
@BindingAdapter("setClickListener", "viewModel")
internal fun <T : UniversalBindingItem> View.setClickListener(action: ItemClickHandler<T>, viewModel: OrganisationVM) {
    setOnClickListener {
        action.onItemClick(viewModel as T)
    }
}

private fun getColorOrganisationName(isEliminated: Boolean, context: Context): Int {
    val textColor = if (isEliminated) TextColor.READ_ONLY else TextColor.DEFAULT
    return textColor.getValue(context)
}

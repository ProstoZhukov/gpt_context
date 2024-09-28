package ru.tensor.sbis.business.common.ui.bind_adapter

import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import io.reactivex.subjects.Subject
import ru.tensor.sbis.business.common.ui.utils.ViewActionObservable
import ru.tensor.sbis.business.common.ui.utils.isTrue
import ru.tensor.sbis.business.common.ui.utils.toFalse
import ru.tensor.sbis.design.view.input.searchinput.DEFAULT_SEARCH_QUERY
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.business.theme.R as RBusinessTheme
import ru.tensor.sbis.design.R as RDesign

/**
 * Data Binding Адаптеры используемые для макетов Строки поиска с фильтром
 * Ссылка на стандарт:
 * @see <a href="http://axure.tensor.ru/MobileStandart8/#p=строка_поиска">Строка поиска с фильтром</a>
 *
 * @author as.chadov
 *
 * @see BindingAdapter
 */
@BindingAdapter("rxCancelSearch")
internal fun SearchInput.setCancelSearchAction(
    cancelSearchChannel: Subject<Any>?
) = cancelSearchChannel?.let { cancelSearchObservable().subscribe(it) }

@BindingAdapter(
    value = ["rxSearchFieldEditor", "hideKeyboardOnSearchClick"],
    requireAll = true
)
internal fun SearchInput.setSearchFieldEditorActions(
    searchFieldEditorChannel: Subject<Int>?,
    hideKeyboardOnSearchClick: Boolean
) = searchFieldEditorChannel?.let {
    searchFieldEditorActionsObservable().doOnNext { keyId ->
        if (hideKeyboardOnSearchClick && keyId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard()
        }
    }.subscribe(it)
}

@BindingAdapter("rxSearchQueryChanged")
internal fun SearchInput.setSearchQueryChangedAction(
    searchQueryChangedChannel: Subject<String>?
) = searchQueryChangedChannel?.let { searchQueryChangedObservable().subscribe(it) }

@BindingAdapter("rxSearchFocusChanged")
internal fun SearchInput.setSearchFocusChangedAction(
    searchFocusChangedChannel: Subject<Boolean>?
) = searchFocusChangedChannel?.let { searchFocusChangeObservable().subscribe(it) }

@BindingAdapter("filterButtonClickAction")
internal fun SearchInput.filterButtonClickAction(action: Subject<Any>) =
    filterClickObservable()
        .throttleFirst(FILTER_CLICK_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)
        .subscribe(action)

@BindingAdapter("clearText")
internal fun SearchInput.clearText(
    resetEvent: ObservableBoolean
) {
    if (resetEvent.isTrue) {
        setSearchText(DEFAULT_SEARCH_QUERY)
        resetEvent.toFalse
    }
}

@BindingAdapter("updateCurrentFiltersText")
internal fun SearchInput.updateCurrentFiltersText(
    currentFilters: List<String>?
) = setSelectedFilters(currentFilters.orEmpty())

@BindingAdapter("viewActionChannel")
internal fun SearchInput.performViewAction(
    action: ViewActionObservable<SearchInput>?
) = action?.perform(this)

/**
 * При скролле устанавливаем для [SearchInput] фон без разделителя и тень через [defaultElevation],
 * когда же [SearchInput] прижата к верху устанавливаем фон по-умолчанию для [SearchInput]
 */
@BindingAdapter("elevationFilterSearch")
internal fun SearchInput.setElevationFilterSearch(
    isElevated: Boolean
) {
    val typedValue = TypedValue()
    elevation = if (isElevated) {
        if (context.theme.resolveAttribute(RBusinessTheme.attr.search_panel_background_simple, typedValue, true)) {
            setBackgroundResource(typedValue.resourceId)
        }
        val defaultElevation = resources.getDimensionPixelSize(RDesign.dimen.elevation_high).toFloat()
        defaultElevation
    } else {
        if (context.theme.resolveAttribute(RBusinessTheme.attr.search_panel_background, typedValue, true)) {
            setBackgroundResource(typedValue.resourceId)
        }
        0f
    }
}

/**
 * Показываем определенный слой drawable (напр. business_simple_search_filter_background_dark) в зависимости от переданного level
 * необходимо для разных фонов в темной теме
 */
@BindingAdapter("backgroundLevel")
internal fun SearchInput.setBackgroundLevel(
    level: Int
) {
    if (this.background is LayerDrawable) {
        val layerDrawable: LayerDrawable = this.background as LayerDrawable
        val background = layerDrawable.findDrawableByLayerId(RBusinessTheme.id.backgroundDrawable)
        if (background != null) {
            background.level = level
        }
    }
}

@BindingAdapter(
    value = ["visibleFilterSearch", "isAnimatedFilterSearch"],
    requireAll = true
)
internal fun SearchInput.setVisibleFilterSearch(
    isVisible: Boolean,
    isAnimated: Boolean
) {
    if (isAnimated) {
        if (isVisible) {
            visibility = View.VISIBLE
            animate().translationY(0F)
                .interpolator = DecelerateInterpolator(FILTER_FACTOR_INTERPOLATOR)
        } else {
            animate().translationY(-height.toFloat())
                .interpolator = AccelerateInterpolator(FILTER_FACTOR_INTERPOLATOR)
        }
    } else {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    if (isVisible.not()) {
        clearFocus()
    }
}


private const val FILTER_CLICK_WINDOW_DELAY_MILL_SEC = 200L
private const val FILTER_FACTOR_INTERPOLATOR = 2F
package ru.tensor.sbis.business.common.ui.utils

import android.view.View
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.business.common.ui.base.contract.MoneyTopNavigationState
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.view.input.searchinput.SearchColorType

/** @SelfDocumented */
@BindingAdapter("navigationContent")
internal fun SbisTopNavigationView.setContent(
    state: MoneyTopNavigationState
) {
    @Suppress("SENSELESS_COMPARISON")
    if (state == null) return
    if (!state.isTopNavigationVisible) {
        visibility = View.GONE
        return
    }
    val newContent = state.contentCreator?.invoke()
    if (newContent != null) {
        content = newContent
    }
    searchInput?.let {
        it.setSearchColor(SearchColorType.BASE)
        state.searchHint?.let { hint -> it.setSearchHint(hint) }
        it.cancelSearchObservable().subscribe { searchInput?.setSearchText("") }
        it.searchQueryChangedObservable().subscribe { query -> state.searchAction.invoke(query) }
    }

    smallTitleMaxLines = state.smallTitleMaxLines
    if (content is SbisTopNavigationContent.SmallTitle) {
        subtitleView?.maxLines = state.smallSubtitleMaxLines
    }
    showBackButton = state.showBackButton
    if (showBackButton) {
        backBtn?.setOnClickListener {
            state.backButtonAction()
        }
    }
    state.rightItemsCreator?.let { create ->
        rightActions = context.create(rightActions.toMutableList())
    }
    state.leftCustomView?.let { create ->
        leftCustomView = context.create()
    }
}
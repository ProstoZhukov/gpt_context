package ru.tensor.sbis.business.common.ui.base.contract

import android.content.Context
import android.view.View
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationActionItem
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent

/**
 * Контракт шапки для модулей `money_impl`, `payment_document_impl`, `payment_request_impl`, `payment_bank_impl`, `payment_draft_impl`.
 *
 * @author aa.kobeleva
 */
interface MoneyTopNavigationContract {
    val state: StateFlow<MoneyTopNavigationState>
}

/** @SelfDocumented */
data class MoneyTopNavigationState(
    val isTopNavigationVisible: Boolean = true,
    val contentCreator: (() -> SbisTopNavigationContent?)? = null,
    val rightItemsCreator: (Context.(MutableList<SbisTopNavigationActionItem>) -> MutableList<SbisTopNavigationActionItem>)? = null,
    val smallTitleMaxLines: Int = DEFAULT_MAX_LINES,
    val smallSubtitleMaxLines: Int = DEFAULT_MAX_LINES,
    val showBackButton: Boolean = true,
    val backButtonAction: () -> Unit = {},
    val searchHint: String? = null,
    val searchAction: (String) -> Unit = {},
    val leftCustomView: (Context.() -> View)? = null
) {
    companion object {
        const val DEFAULT_MAX_LINES = 1
    }
}
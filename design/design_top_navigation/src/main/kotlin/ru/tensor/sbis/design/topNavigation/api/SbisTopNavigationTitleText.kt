package ru.tensor.sbis.design.topNavigation.api

import ru.tensor.sbis.design.view.input.base.BaseInputView

/**
 * Информация о тексте в заголовке.
 *
 * @author da.zolotarev
 */
class SbisTopNavigationTitleText {
    internal var inputView: BaseInputView? = null

    /** @SelfDocumented */
    fun getTextWidth(text: CharSequence) = inputView?.getValueWidth(text) ?: 0

    /** @SelfDocumented */
    fun getTextValue() = inputView?.value ?: ""
}
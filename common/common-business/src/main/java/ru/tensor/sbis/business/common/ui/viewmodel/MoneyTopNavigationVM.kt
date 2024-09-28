package ru.tensor.sbis.business.common.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.base.contract.MoneyTopNavigationContract
import ru.tensor.sbis.business.common.ui.base.contract.MoneyTopNavigationState
import ru.tensor.sbis.business.common.ui.base.router.BaseRouter
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationActionItem
import ru.tensor.sbis.design.utils.extentions.setRightPadding
import ru.tensor.sbis.design.utils.extentions.setTopPadding

/**
 * Шапка денег для модулей `business_money`, `payment_document_impl`, `payment_request_impl`.
 *
 */
abstract class MoneyTopNavigationVM(
    private val router: BaseRouter
) : BaseViewModel(),
    MoneyTopNavigationContract {

    protected val internalState: MutableStateFlow<MoneyTopNavigationState> = MutableStateFlow(
        MoneyTopNavigationState(showBackButton = true, backButtonAction = router::goBack)
    )
    override val state: StateFlow<MoneyTopNavigationState> = internalState

    /**
     * Добавить элементы шапки с сокращением по фактору (тыс, млн, млрд)
     *
     * @param totalSum сумма к отображению, уже сокращенная по фактору
     * @param units фактор, по которому была сокращена сумма
     * @param hasAccessToWalletAndDocuments true, если есть доступ к ДДС
     * @param action действие по клику на сумму и фактор
     */
    fun addBalance(
        totalSum: String,
        units: String,
        hasAccessToWalletAndDocuments: Boolean = false,
        action: (() -> Unit)? = null
    ) {
        internalState.value = state.value.copy(
            rightItemsCreator = {
                val totalSumItem = SbisTopNavigationActionItem.CustomView(
                    SbisTextView(this).apply {
                        id = R.id.top_navigation_total_sum
                        text = PlatformSbisString.Value(totalSum).getCharSequence(context)
                        typeface = TypefaceManager.getRobotoMediumFont(context)
                        textSize = FontSize.X5L.getScaleOffDimen(context)
                        setTextColor(TextColor.DEFAULT.getValue(context))
                        setRightPadding(Offset.X2S.getDimenPx(context))
                        if (hasAccessToWalletAndDocuments) {
                            setOnClickListener { action?.invoke() }
                        }
                    }
                )
                val unitsItem = SbisTopNavigationActionItem.CustomView(
                    SbisTextView(this).apply {
                        id = R.id.top_navigation_units
                        text = PlatformSbisString.Value(units).getCharSequence(context)
                        typeface = TypefaceManager.getRobotoRegularFont(context)
                        textSize = FontSize.XL.getScaleOffDimen(context)
                        setTextColor(TextColor.DEFAULT.getValue(context))
                        setTopPadding(Offset.ST.getDimenPx(context))
                        if (hasAccessToWalletAndDocuments) {
                            setOnClickListener { action?.invoke() }
                        }
                    }
                )
                if (it.isNotEmpty()) {
                    mutableListOf(totalSumItem, unitsItem, it.last())
                } else {
                    mutableListOf(totalSumItem, unitsItem)
                }
            }
        )
    }
}

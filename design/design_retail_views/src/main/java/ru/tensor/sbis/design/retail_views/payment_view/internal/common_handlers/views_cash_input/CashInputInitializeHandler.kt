package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.IncludeViewsInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import java.math.BigDecimal

/**
 * Набор параметров, необходимых для инициализации [CashInputInitializeHandler].
 *
 * @param totalCheckAmountInitial кол-во средств, которые требуется внести покупателю.
 */
data class CashInputInitializeParams(
    val totalCheckAmountInitial: BigDecimal
)

/** Общая реализация объекта для инициализации элементов управления "Ввод денежных средств". */
internal class CashInputInitializeHandler(
    private val initialParams: CashInputInitializeParams,
    private val actionListenerApi: CashInputActionListenerApi,
    private val setDataApi: CashInputSetDataApi
) : IncludeViewsInitializeApi {

    override fun initialize() {
        /* Выполняем подписку на события изменения кол-ва внесенных средств. */
        actionListenerApi.setOnAmountChangedAction(setDataApi.amountChangeListener)

        /* Выполняем инициализацию блока ввода денежных средств. */
        setDataApi.setTotalCheckValue(
            totalCheckValue = initialParams.totalCheckAmountInitial
        )
    }
}
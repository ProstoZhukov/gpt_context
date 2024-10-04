package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment

import android.view.View
import android.view.View.MeasureSpec
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.double_button.DoubleButtonApi
import ru.tensor.sbis.design.retail_views.double_button.DoubleButtonApi.Mode
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.IncludeViewsInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners.MixPaymentActionListenerApi
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Набор параметров, необходимых для инициализации [MixPaymentInitializeHandler].
 *
 * @param viewForFocusReceiver специальная [View] для установки фокуса, при выключении смешанного режима оплаты.
 * @param isMixPaymentModeEnabled доступно ли переключение режимов смешанной оплаты.
 * @param isMixPaymentModeInitialValue текущее состояние смешанной оплаты - активна или нет.
 */
data class MixPaymentInitializeParams(
    val viewForFocusReceiver: View? = null,
    val isMixPaymentModeEnabled: Boolean = true,
    val isMixPaymentModeInitialValue: Boolean = false
)

/** Общая реализация объекта для инициализации элементов управления "переключение режима оплаты". */
internal class MixPaymentInitializeHandler(
    private val initialParams: MixPaymentInitializeParams,
    private val safetyApi: MixPaymentAccessSafetyApi,
    private val actionListenerApi: MixPaymentActionListenerApi,
    private val viewAccessApi: MixPaymentAccessDangerousApi,
    private val doubleButtonsWithMixPaymentSupport: List<DoubleButtonApi> = emptyList(),
    private val calculateAvailableSpace: ((disableMixButton: View) -> Int)? = null
) : IncludeViewsInitializeApi {

    override fun initialize() {
        /* Устанавливаем иконку на кнопку "Включить смешанную оплату". */
        viewAccessApi.enableMixButton.icon = SbisButtonDrawableIcon(
            iconRes = R.drawable.retail_views_ic_mix_payment,
            size = SbisButtonIconSize.X7L
        )

        /* Настройка доступности переключения режимов смешанной оплаты. */
        initialParams.isMixPaymentModeEnabled.let { isEnabled ->
            safetyApi.setEnableMixButtonEnableState(isEnabled)
            safetyApi.setDisableMixButtonEnableState(isEnabled)
        }

        /* Настройка состояний кнопок переключения режимов смешанной оплаты. */
        setupMixPaymentMode(isMixPayment = initialParams.isMixPaymentModeInitialValue)

        /* Кнопка включения смешанного режима оплаты. */
        actionListenerApi.setEnableMixButtonClickListener {
            setupMixPaymentMode(isMixPayment = true)
            callExtraActionIfAvailable(isMixPayment = true)
        }

        /* Кнопка выключения смешанного режима оплаты. */
        actionListenerApi.setDisableMixButtonClickListener {
            setupMixPaymentMode(isMixPayment = false)
            callExtraActionIfAvailable(isMixPayment = false)

            /* Переназначаем фокус в указанную View, чтобы не было проблем с неявным вводом в скрытое поле. */
            initialParams.viewForFocusReceiver?.requestFocus()
        }

        /* Определяем показывать ли сокращенный текст в зависимости от доступного места. */
        calculateAvailableSpace?.let(::adjustDisableMixButtonText)
    }

    private fun List<DoubleButtonApi>.setupDoubleButtons(isMixPayment: Boolean) {
        forEach { doubleButton ->
            doubleButton.viewPropertiesApi.changeDoubleButtonModeTo(
                if (!isMixPayment) Mode.Button
                else {
                    /* isLocked = false, т.к. кнопки со смешанной оплатой должны уметь трансформироваться. */
                    Mode.Editing(isLocked = false)
                }
            )
        }
    }

    private fun setupMixPaymentMode(isMixPayment: Boolean) {
        /* Переключаем состояние двойных кнопок. */
        doubleButtonsWithMixPaymentSupport.setupDoubleButtons(isMixPayment)

        /* Переключение видимости кнопок. */
        safetyApi.setEnableMixButtonVisibility(isVisible = !isMixPayment)
        safetyApi.setDisableMixButtonVisibility(isVisible = isMixPayment)
    }

    private fun callExtraActionIfAvailable(isMixPayment: Boolean) {
        /* Дополнительное действие при смене режима. */
        actionListenerApi.onMixPaymentClickExtraAction?.invoke(isMixPayment)
    }

    /** Определяем какой текст отобразить в зависимости от доступного места. */
    private fun adjustDisableMixButtonText(calculateAvailableSpace: (View) -> Int) {
        val parent = viewAccessApi.disableMixButton.parent as View

        parent.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val availableSpace = calculateAvailableSpace(parent)
            val wrapContentWidth = viewAccessApi.disableMixButton.calculateWrapContentWidth()

            val title = if (wrapContentWidth > availableSpace) {
                R.string.retail_views_payment_mix_btn_title_short
            } else {
                R.string.retail_views_payment_mix_btn_title
            }.let { viewAccessApi.disableMixButton.context.getString(it) }

            if (viewAccessApi.disableMixButton.model.title?.text != title) {
                viewAccessApi.disableMixButton.setTitle(title)
            }
        }
    }

    /** Рассчитываем сколько будет занимать кнопка. */
    private fun SbisButton.calculateWrapContentWidth(): Int {
        val textWidth = measureText(context.getString(R.string.retail_views_payment_mix_btn_title))
        val iconSize = model.icon?.size?.globalVar?.getDimen(context) ?: 0f

        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        return (textWidth + innerSpacing + (sidePadding * 2) + iconSize).roundToInt()
    }

    companion object {

        /** Рассчитываем доступное место для кнопки смешанной оплаты в зависимости от кнопки оплаты картой. */
        fun getCalculateAvailableSpaceAction(payCardButton: DoubleButtonApi): ((View) -> Int)? {
            val isLandscape = payCardButton.dangerousApi.doubleButtonRoot
                .resources.getBoolean(RDesign.bool.is_landscape)

            return if (!isLandscape) null
            else {
                { disabledMixButton ->
                    (payCardButton.dangerousApi.doubleButtonRoot.right - disabledMixButton.left)
                        .coerceAtLeast(0)
                }
            }
        }
    }
}
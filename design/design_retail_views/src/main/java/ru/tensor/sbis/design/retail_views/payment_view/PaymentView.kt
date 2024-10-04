package ru.tensor.sbis.design.retail_views.payment_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.PaymentViewMode
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.AdvanceDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.AdvanceDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.DebtCreditDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.DebtCreditDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.DepositWithdrawalDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.DepositWithdrawalDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.PaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.PaymentDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.RefundPaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.RefundPaymentDelegateApi
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.RetailColor
import kotlin.properties.Delegates

/** View предоставляющая интерфейс окна оплаты. */
class PaymentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_payment_view_theme,
    @StyleRes defStyleRes: Int = R.style.RetailViewsPaymentViewStyle_Light
) : ConstraintLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes),
    PaymentViewApi {

    companion object {
        private const val NO_VALUE = -1
    }

    /** Публичное API [PaymentView]. */
    override val api: PaymentViewApi.Accessor by lazy {
        object : PaymentViewApi.Accessor {

            private var advanceModeDelegateOrNull: AdvanceDelegateApi? = null
            private var debtCreditModeDelegateOrNull: DebtCreditDelegateApi? = null
            private var depositWithDrawModeDelegateOrNull: DepositWithdrawalDelegateApi? = null
            private var paymentModeDelegateOrNull: PaymentDelegateApi? = null
            private var returnModeDelegateOrNull: RefundPaymentDelegateApi? = null

            override val advanceApi: AdvanceDelegateApi
                get() = getDelegateOrThrow(PaymentViewMode.Advance) {
                    advanceModeDelegateOrNull ?: AdvanceDelegate.createDefault().also { advanceModeDelegateOrNull = it }
                }

            override val debtCreditApi: DebtCreditDelegateApi
                get() = getDelegateOrThrow(PaymentViewMode.DebtCredit) {
                    debtCreditModeDelegateOrNull
                        ?: DebtCreditDelegate.createDefault().also { debtCreditModeDelegateOrNull = it }
                }

            override val depositWithDrawApi: DepositWithdrawalDelegateApi
                get() = getDelegateOrThrow(PaymentViewMode.DepositWithDraw) {
                    depositWithDrawModeDelegateOrNull
                        ?: DepositWithdrawalDelegate.createDefault().also { depositWithDrawModeDelegateOrNull = it }
                }

            override val paymentApi: PaymentDelegateApi
                get() = getDelegateOrThrow(PaymentViewMode.Payment) {
                    paymentModeDelegateOrNull ?: PaymentDelegate.createDefault().also { paymentModeDelegateOrNull = it }
                }

            override val refundPaymentApi: RefundPaymentDelegateApi
                get() = getDelegateOrThrow(PaymentViewMode.Refund) {
                    returnModeDelegateOrNull
                        ?: RefundPaymentDelegate.createDefault().also { returnModeDelegateOrNull = it }
                }

            private inline fun <reified T> getDelegateOrThrow(
                mainDelegateMode: PaymentViewMode,
                getterBlock: () -> T
            ): T {
                if (mainDelegateMode != paymentMode) {
                    throw IllegalStateException(
                        "Попытка использовать ${T::class.java.simpleName} делегат, однако " +
                            "текущий режим работы '$paymentMode'. Перед использованием ${T::class.java.simpleName}" +
                            "убедитесь, что ${PaymentView::class.java.simpleName} переведена в режим $mainDelegateMode."
                    )
                }

                return getterBlock()
            }
        }
    }

    /** Получить/установить текущий режим работы окна оплаты. ВАЖНО: работает единожды! */
    override var paymentMode: PaymentViewMode? by Delegates.observable(
        initialValue = null,
        onChange = { _, oldValue, newValue ->
            /* Инициализация проводится только один раз. Изменение режима в рантайме - не поддерживается! */
            if (oldValue == null && newValue != null) {
                /* Инициализируем соответствующий делегат. */
                initializeAllNecessaryDelegateByMode(newValue)
            }
        }
    )

    /*
     * Важно! Блок 'init' должен находиться ниже, чем объявления Delegates.notNull(),
     * в противном случае получим NPE при обращении к полю.
     */
    init {
        initAttrs(attrs, defStyleAttr, defStyleRes)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.RetailViewsPaymentViewAttrs, defStyleAttr, defStyleRes
        )

        try {
            typedArray.getInt(
                R.styleable.RetailViewsPaymentViewAttrs_retail_views_payment_view_mode, NO_VALUE
            ).toPaymentMode()?.let { initialPaymentMode ->
                /* Инициализируем PaymentMode, если значение было передано из '.xml'. */
                paymentMode = initialPaymentMode
            }
        } finally {
            typedArray.recycle()
        }
    }

    /* Инициализация делегатов, которые отвечают за работу изначального режима оплаты. */
    private fun initializeAllNecessaryDelegateByMode(initialPaymentMode: PaymentViewMode) {
        when (initialPaymentMode) {
            PaymentViewMode.Advance -> api.advanceApi
            PaymentViewMode.DebtCredit -> api.debtCreditApi
            PaymentViewMode.DepositWithDraw -> api.depositWithDrawApi
            PaymentViewMode.Payment -> api.paymentApi
            PaymentViewMode.Refund -> api.refundPaymentApi
        }.inflateAndInitializeDelegate()
    }

    private fun BaseRenderApi<*, *>.inflateAndInitializeDelegate() {
        renderApiHandler
            .inflateViewDelegateBinding(LayoutInflater.from(context), this@PaymentView)
            .let(::initializeInflatedRootView)
    }

    /* Первоначальная настройка родительской ViewGroup. Значения берутся из стиля 'RetailViewsPaymentRootViewStyle'. */
    private fun initializeInflatedRootView(inflatedView: ViewDataBinding) {
        /* Запрещаем обрезать потомков. */
        clipChildren = false
        /* Запрещаем обрезать тени у кнопок оплаты. */
        clipToPadding = false

        /* Выставляем отступы со всех сторон [PaymentView]. */
        val horizontalPadding = Offset.S.getDimenPx(inflatedView.root.context)
        val verticalPadding = Offset.X2S.getDimenPx(inflatedView.root.context)
        setPadding(
            /* left = */ horizontalPadding,
            /* top = */ verticalPadding,
            /* right = */ horizontalPadding,
            /* bottom = */ verticalPadding
        )

        /* Устанавливаем основной цвет панели. */
        setBackgroundColor(RetailColor.KEYBOARD_PANEL_BACKGROUND.getValue(context))

        /* Делаем контейнер фокусируемым, чтобы корректно отрабатывало очищение фокуса у view [View.clearFocus]. */
        isFocusable = true
        isFocusableInTouchMode = true
    }

    private fun Int.toPaymentMode(): PaymentViewMode? =
        PaymentViewMode.byIndex(this)
}
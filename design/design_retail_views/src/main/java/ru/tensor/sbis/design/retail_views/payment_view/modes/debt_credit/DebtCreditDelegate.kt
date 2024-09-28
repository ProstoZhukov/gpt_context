package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_models.utils.ZERO_MONEY_VALUE
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsDebtCreditLayoutBinding
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.IncludeViewsInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.initializeCursorPositionAfterDraw
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.CashInputInitializeHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.CashInputInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety.ExtraViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety.ExtraViewsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners.ExtraViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners.ExtraViewsActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners.MixPaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners.MixPaymentActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety.PaymentButtonsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety.PaymentButtonsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners.PaymentButtonsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners.PaymentButtonsActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegate.InputViewWrapper
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditViewAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.dangerous.DebtCreditViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.inner_types.DebtCreditInnerMode
import kotlin.properties.Delegates

/** Управляет поведением вьюшек, связанных с режимом отображения "Оплата кредита (Долги)". */
internal class DebtCreditDelegate private constructor() : DebtCreditDelegateApi {

    companion object {
        /** Дефолтный метод для получения объекта [DebtCreditDelegateApi]. */
        fun createDefault(): DebtCreditDelegateApi = DebtCreditDelegate()
    }

    private val keyboardHelper by lazy { CustomNumericKeyboardHelper() }

    private val context: Context
        get() = viewAccessApi.paymentView.context

    private val defaultInputFocusView by lazy {
        viewAccessApi.moneyInputField.editableView
    }

    private val banknotesApi: BanknotesDelegateApi.Handler by lazy {
        BanknotesDelegate.defaultCreator(
            context = context,
            targetAncestor = viewAccessApi.paymentView,
            keyboardView = viewAccessApi.keyboardView,
            banknotesViewBinding = viewAccessApi.banknotesView,
            fieldsForBanknotesInput = listOf(
                InputViewWrapper(
                    inputField = viewAccessApi.moneyInputField.editableView,
                    paymentMethod = PaymentMethod.CASH,
                    needOverrideInputFieldHint = false
                ),
                InputViewWrapper(
                    inputField = viewAccessApi.payCardButton.dangerousApi.editableView,
                    paymentMethod = PaymentMethod.CARD,
                    autoSizeConfig = InputViewWrapper.AutoSizeConfig()
                ),
                InputViewWrapper(
                    inputField = viewAccessApi.checkDoubleButton.dangerousApi.editableView,
                    autoSizeConfig = InputViewWrapper.AutoSizeConfig()
                )
            )
        ).banknotesApi.apply {
            /* Если нет фокуса, то нажатие на банкноты автоматически установит фокус в это поле. */
            inputViewForAutoFocus = defaultInputFocusView
        }
    }

    /* Инициализируется вместе с RenderApi. */
    internal var viewAccessApi: DebtCreditViewAccessDangerousApi.Handler by Delegates.notNull()

    override val renderApiHandler: DebtCreditRenderApi.Handler by lazy { RenderApiHandler() }

    override val setDataApi: DebtCreditSetDataApi.Handler by lazy {
        SetDataApiHandler(
            banknotesApi = banknotesApi,
            actionListenerApi = actionListenerApi,
            viewSafetyApi = viewSafetyApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val actionListenerApi: DebtCreditActionListenerApi.Handler by lazy {
        ActionListenerApiHandler(
            banknotesApi = banknotesApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val viewSafetyApi: DebtCreditViewAccessSafetyApi.Handler by lazy {
        SafetyViewAccessApiHandler(
            keyboardHelper = keyboardHelper,
            viewAccessApi = viewAccessApi
        )
    }

    /* Всегда пустой конструктор, для инициализации используйте метод 'doAfterInflate(initializeApi)'. */
    internal inner class RenderApiHandler : DebtCreditRenderApi.Handler {

        override var viewDelegateBinding: RetailViewsDebtCreditLayoutBinding by Delegates.notNull()

        @SuppressLint("UnsafeOptInUsageWarning")
        override fun inflateViewDelegateBinding(layoutInflater: LayoutInflater, rootView: ViewGroup) =
            RetailViewsDebtCreditLayoutBinding.inflate(layoutInflater, rootView, true)
                .also { binding ->
                    /* После инфлейта инициализируем поле с доступом к View элементам. */
                    viewDelegateBinding = binding

                    /* Выполняем инициализацию Dangerous Api. */
                    DangerousViewAccessApiHandler(viewDelegateBinding)
                        .also { dangerousApi ->
                            viewAccessApi = dangerousApi

                            /* Действие после инициализации View делегата. */
                            doAfterInflate(
                                initializeApi = DebtCreditInitializeApi(
                                    keyboardHelper = keyboardHelper,
                                    mixPaymentInitializeParams = MixPaymentInitializeParams(
                                        viewForFocusReceiver = defaultInputFocusView
                                    ),
                                    setDataApi = setDataApi,
                                    actionListenerApi = actionListenerApi,
                                    viewSafetyApi = viewSafetyApi
                                ).also { initializeApi ->
                                    initializeApi.banknotesApi = banknotesApi
                                    initializeApi.viewAccessApi = viewAccessApi
                                }
                            )
                        }
                }

        override fun doAfterInflate(initializeApi: DebtCreditInitializeApi) {
            /* Выполнение базовой настройки делегата. */
            super.doAfterInflate(initializeApi)

            /* Настройка Banknotes Delegate. */
            initializeApi.banknotesApi.initialize(initializeApi.keyboardHelper)
        }

        override fun getFieldsForLockSoftKeyboard(initializeApi: DebtCreditInitializeApi): List<EditText> =
            initializeApi.viewAccessApi.allInputFields

        override fun getIncludeViewsInitializeApi(initializeApi: DebtCreditInitializeApi): List<IncludeViewsInitializeApi> =
            listOf(
                MixPaymentInitializeHandler(
                    initialParams = initializeApi.mixPaymentInitializeParams,
                    safetyApi = initializeApi.viewSafetyApi,
                    actionListenerApi = initializeApi.actionListenerApi,
                    viewAccessApi = initializeApi.viewAccessApi,
                    doubleButtonsWithMixPaymentSupport = listOf(
                        initializeApi.viewAccessApi.payCardButton
                    ),
                    calculateAvailableSpace = MixPaymentInitializeHandler.getCalculateAvailableSpaceAction(
                        payCardButton = initializeApi.viewAccessApi.payCardButton
                    )
                ),
                CashInputInitializeHandler(
                    initialParams = CashInputInitializeParams(
                        totalCheckAmountInitial = ZERO_MONEY_VALUE
                    ),
                    actionListenerApi = initializeApi.actionListenerApi,
                    setDataApi = initializeApi.setDataApi
                )
            )
    }

    internal class SetDataApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val actionListenerApi: DebtCreditActionListenerApi.Handler,
        private val viewSafetyApi: DebtCreditViewAccessSafetyApi.Handler,
        private val viewAccessApi: DebtCreditViewAccessDangerousApi.Handler
    ) : DebtCreditSetDataApi.Handler,
        CreditInfoSetDataApi by CreditInfoSetDataHandler(viewAccessApi),
        ToolbarViewsSetDataApi by ToolbarViewsSetDataHandler(viewAccessApi),
        PaymentTypeSetDataApi by PaymentTypeSetDataHandler(actionListenerApi),
        PaymentButtonsSetDataApi by PaymentButtonsSetDataHandler(viewAccessApi),
        CashInputSetDataApi by CashInputSetDataHandler(banknotesApi, viewSafetyApi, viewAccessApi) {

        private val context: Context
            get() = viewAccessApi.paymentView.context

        override fun setPaymentType(paymentInnerMode: DebtCreditInnerMode) {
            viewAccessApi.paymentTypeButton.setTitle(context.getString(paymentInnerMode.stringResId))

            /* Устанавливаем фокус на нужное поле в зависимости от типа погашения кредита. */
            when (paymentInnerMode) {
                DebtCreditInnerMode.FULL_REPAYMENT ->
                    viewAccessApi.moneyInputField.editableView

                DebtCreditInnerMode.CREDIT_PAYMENT ->
                    viewAccessApi.checkDoubleButton.dangerousApi.editableView
            }.initializeCursorPositionAfterDraw()
        }

        override fun setCreditPaymentFullRepayment(isFullRepayment: Boolean) {
            with(viewSafetyApi) {
                setCheckButtonInvisible(!isFullRepayment)
                setCheckDoubleButtonInvisible(isFullRepayment)
                setDebtTextInvisible(isFullRepayment)
            }
        }
    }

    internal class ActionListenerApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val viewAccessApi: DebtCreditViewAccessDangerousApi.Handler
    ) : DebtCreditActionListenerApi.Handler,
        CashInputActionListenerApi by CashInputActionListenerHandler(banknotesApi),
        ExtraViewsActionListenerApi by ExtraViewsActionListenerHandler(viewAccessApi),
        PaymentTypeActionListenerApi by PaymentTypeActionListenerHandler(viewAccessApi),
        ToolbarViewsActionListenerApi by ToolbarViewsActionListenerHandler(viewAccessApi),
        PaymentButtonsActionListenerApi by PaymentButtonsActionListenerHandler(viewAccessApi),
        MixPaymentActionListenerApi by MixPaymentActionListenerHandler(
            viewAccessApi = viewAccessApi,
            initialOnMixPaymentClickExtraAction = { mixPaymentMode ->
                if (mixPaymentMode) {
                    /* При переключении смешанной оплаты, требуется дозаполнять поле "Картой". */
                    banknotesApi.autoSetupRemainingAmountForInputField(
                        inputField = viewAccessApi.payCardButton.dangerousApi.editableView
                    )
                }
            }) {
        /* Блок для прикладной логики. */
    }

    internal class SafetyViewAccessApiHandler(
        private val keyboardHelper: CustomNumericKeyboardHelper,
        private val viewAccessApi: DebtCreditViewAccessDangerousApi.Handler
    ) : DebtCreditViewAccessSafetyApi.Handler,
        ExtraViewsAccessSafetyApi by ExtraViewsAccessSafetyHandler(viewAccessApi),
        MixPaymentAccessSafetyApi by MixPaymentAccessSafetyHandler(viewAccessApi),
        CreditInfoAccessSafetyApi by CreditInfoAccessSafetyHandler(viewAccessApi),
        ToolbarViewsAccessSafetyApi by ToolbarViewsAccessSafetyHandler(viewAccessApi),
        PaymentButtonsAccessSafetyApi by PaymentButtonsAccessSafetyHandler(viewAccessApi),
        AllInputFieldsAccessSafetyApi by AllInputFieldsAccessSafetyHandler(viewAccessApi),
        CashInputAccessSafetyApi by CashInputAccessSafetyHandler(keyboardHelper, viewAccessApi) {
        /* Блок для прикладной логики. */
    }

    @DangerousApi
    private class DangerousViewAccessApiHandler(
        private val viewDelegateBinding: RetailViewsDebtCreditLayoutBinding
    ) : DebtCreditViewAccessDangerousApi.Handler,
        ExtraViewsAccessDangerousApi by ExtraViewsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        CreditInfoAccessDangerousApi by CreditInfoAccessDangerousHandler(viewDelegateBinding.root.rootView),
        CashInputAccessDangerousApi by CashInputAccessDangerousHandler(viewDelegateBinding.includeCashInput),
        MixPaymentAccessDangerousApi by MixPaymentAccessDangerousHandler(viewDelegateBinding.includeMixButtons),
        PaymentButtonsAccessDangerousApi by PaymentButtonsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        ToolbarViewsAccessDangerousApi by ToolbarViewsAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons),
        PaymentTypeAccessDangerousApi by PaymentTypeAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons) {

        override val paymentView: PaymentView
            get() = viewDelegateBinding.root.parent as PaymentView

        override val allInputFields: List<EditText>
            get() = listOf(
                moneyInputField.editableView,
                payCardButton.dangerousApi.editableView,
                checkDoubleButton.dangerousApi.editableView
            )
    }
}
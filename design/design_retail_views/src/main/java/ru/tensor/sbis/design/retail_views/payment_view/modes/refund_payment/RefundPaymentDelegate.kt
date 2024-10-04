package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_models.utils.ZERO_MONEY_VALUE
import ru.tensor.sbis.design.retail_models.utils.isZero
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsRefundPaymentLayoutBinding
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
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataHandler
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
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety.PaymentTypeAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety.PaymentTypeAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous.TaxationInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous.TaxationInfoAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety.TaxationInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety.TaxationInfoAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners.TaxationInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners.TaxationInfoActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data.TaxationInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data.TaxationInfoSetDataHandler
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
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentViewAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.dangerous.RefundPaymentViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.inner_types.RefundPaymentInnerMode
import ru.tensor.sbis.design.retail_views.utils.intAmountFormat
import java.math.BigDecimal
import kotlin.properties.Delegates

/** Управляет поведением вьюшек, связанных с режимом отображения "Возврат". */
internal class RefundPaymentDelegate private constructor() : RefundPaymentDelegateApi {

    companion object {
        /** Дефолтный метод для получения объекта [RefundPaymentDelegateApi]. */
        fun createDefault(): RefundPaymentDelegateApi = RefundPaymentDelegate()
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
    internal var viewAccessApi: RefundPaymentViewAccessDangerousApi.Handler by Delegates.notNull()

    override val renderApiHandler: RefundPaymentRenderApi.Handler by lazy { RenderApiHandler() }

    override val setDataApi: RefundPaymentSetDataApi.Handler by lazy {
        SetDataApiHandler(
            banknotesApi = banknotesApi,
            actionListenerApi = actionListenerApi,
            viewAccessApi = viewAccessApi,
            viewSafetyApi = viewSafetyApi
        )
    }

    override val actionListenerApi: RefundPaymentActionListenerApi.Handler by lazy {
        ActionListenerApiHandler(
            banknotesApi = banknotesApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val viewSafetyApi: RefundPaymentViewAccessSafetyApi.Handler by lazy {
        SafetyViewAccessApiHandler(
            keyboardHelper = keyboardHelper,
            viewAccessApi = viewAccessApi
        )
    }

    /* Всегда пустой конструктор, для инициализации используйте метод 'doAfterInflate(initializeApi)'. */
    internal inner class RenderApiHandler : RefundPaymentRenderApi.Handler {

        override var viewDelegateBinding: RetailViewsRefundPaymentLayoutBinding by Delegates.notNull()

        @SuppressLint("UnsafeOptInUsageWarning")
        override fun inflateViewDelegateBinding(layoutInflater: LayoutInflater, rootView: ViewGroup) =
            RetailViewsRefundPaymentLayoutBinding.inflate(layoutInflater, rootView, true)
                .also { binding ->
                    /* После инфлейта инициализируем поле с доступом к View элементам. */
                    viewDelegateBinding = binding

                    /* Выполняем инициализацию Dangerous Api. */
                    DangerousViewAccessApiHandler(binding).let { dangerousApi ->
                        viewAccessApi = dangerousApi

                        /* Действие после инициализации View делегата. */
                        doAfterInflate(
                            initializeApi = RefundPaymentInitializeApi(
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

        override fun doAfterInflate(initializeApi: RefundPaymentInitializeApi) {
            /* Выполнение базовой настройки делегата. */
            super.doAfterInflate(initializeApi)

            /* Настройка Banknotes Delegate. */
            initializeApi.banknotesApi.initialize(initializeApi.keyboardHelper)

            /* Настройка поля ввода денег. */
            initializeApi.viewAccessApi.moneyInputField.editableView.initializeCursorPositionAfterDraw()
        }

        override fun getFieldsForLockSoftKeyboard(initializeApi: RefundPaymentInitializeApi): List<EditText> =
            initializeApi.viewAccessApi.allInputFields

        override fun getIncludeViewsInitializeApi(initializeApi: RefundPaymentInitializeApi): List<IncludeViewsInitializeApi> =
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
                        /* FIXME: totalCheckAmountInitial - необходимо инициализировать валидным значением "Сколько нужно оплатить". */
                        totalCheckAmountInitial = ZERO_MONEY_VALUE
                    ),
                    actionListenerApi = actionListenerApi,
                    setDataApi = initializeApi.setDataApi
                )
            )
    }

    internal class SetDataApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val actionListenerApi: RefundPaymentActionListenerApi.Handler,
        private val viewSafetyApi: RefundPaymentViewAccessSafetyApi.Handler,
        private val viewAccessApi: RefundPaymentViewAccessDangerousApi.Handler
    ) : RefundPaymentSetDataApi.Handler,
        ToolbarViewsSetDataApi by ToolbarViewsSetDataHandler(viewAccessApi),
        PaymentTypeSetDataApi by PaymentTypeSetDataHandler(actionListenerApi),
        PaymentButtonsSetDataApi by PaymentButtonsSetDataHandler(viewAccessApi),
        CommentInfoSetDataApi by CommentInfoSetDataHandler(viewSafetyApi, viewAccessApi),
        CashInputSetDataApi by CashInputSetDataHandler(banknotesApi, viewSafetyApi, viewAccessApi),
        TaxationInfoSetDataApi by TaxationInfoSetDataHandler(viewSafetyApi, viewAccessApi, actionListenerApi) {

        /* Поддержка API для использования в DataBinding. */
        companion object {
            /* Nothing. */
        }

        private val context: Context
            get() = viewAccessApi.paymentView.context

        /** Если двойная кнопка 'Картой' находится в режиме редактирования, то это смешанная оплата. */
        private val isMixPaymentMode: Boolean
            get() = viewAccessApi.payCardButton.viewPropertiesApi.isEditMode

        override fun setPaymentType(paymentInnerMode: RefundPaymentInnerMode) {
            viewAccessApi.paymentTypeButton.setTitle(context.getString(paymentInnerMode.stringResId))
            viewSafetyApi.configureRefundScreenConstraints(paymentInnerMode)

            when (paymentInnerMode) {
                /* Режимы "Расчет" - включает кнопку оплаты картой и переключение режимов. */
                RefundPaymentInnerMode.REFUND -> {
                    viewSafetyApi.setCardPaymentButtonVisibility(true)
                    viewSafetyApi.setCurrentMixPaymentMode(isMixPaymentMode)

                    viewSafetyApi.setPaymentTypeButtonEnabled(true)
                }

                /* Режимы "Нефискальный" - скрывает кнопку оплаты картой и переключение режимов. */
                RefundPaymentInnerMode.REFUND_NONFISCAL -> {
                    viewSafetyApi.setCardPaymentButtonVisibility(false)
                    viewSafetyApi.setEnableMixButtonVisibility(false)
                    viewSafetyApi.setDisableMixButtonVisibility(false)
                }

                /* Режимы возврата частичной оплаты должны быть заблокированы. */
                RefundPaymentInnerMode.REFUND_ADVANCE,
                RefundPaymentInnerMode.REFUND_PRE_PAYMENT,
                RefundPaymentInnerMode.REFUND_CREDIT,
                RefundPaymentInnerMode.REFUND_FULL_PRE_PAYMENT -> {
                    viewSafetyApi.setPayCardDoubleButtonEnabled(true)
                    viewSafetyApi.setPaymentTypeButtonEnabled(false)
                }
            }
        }

        override fun setCheckButtonTextValue(amount: BigDecimal) {
            if (amount.isZero()) {
                val text = viewAccessApi.checkButton.context.getString(R.string.retail_views_payment_type_payment_text)
                viewAccessApi.checkButton.setTitle(text)
            } else {
                viewAccessApi.checkButton.setTitle(intAmountFormat.format(amount))
            }
        }
    }

    internal class ActionListenerApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val viewAccessApi: RefundPaymentViewAccessDangerousApi.Handler
    ) : RefundPaymentActionListenerApi.Handler,
        CashInputActionListenerApi by CashInputActionListenerHandler(banknotesApi),
        CommentInfoActionListenerApi by CommentInfoActionListenerHandler(viewAccessApi),
        PaymentTypeActionListenerApi by PaymentTypeActionListenerHandler(viewAccessApi),
        TaxationInfoActionListenerApi by TaxationInfoActionListenerHandler(viewAccessApi),
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
        private val viewAccessApi: RefundPaymentViewAccessDangerousApi.Handler
    ) : RefundPaymentViewAccessSafetyApi.Handler,
        MixPaymentAccessSafetyApi by MixPaymentAccessSafetyHandler(viewAccessApi),
        CommentInfoAccessSafetyApi by CommentInfoAccessSafetyHandler(viewAccessApi),
        PaymentTypeAccessSafetyApi by PaymentTypeAccessSafetyHandler(viewAccessApi),
        TaxationInfoAccessSafetyApi by TaxationInfoAccessSafetyHandler(viewAccessApi),
        ToolbarViewsAccessSafetyApi by ToolbarViewsAccessSafetyHandler(viewAccessApi),
        PaymentButtonsAccessSafetyApi by PaymentButtonsAccessSafetyHandler(viewAccessApi),
        AllInputFieldsAccessSafetyApi by AllInputFieldsAccessSafetyHandler(viewAccessApi),
        CashInputAccessSafetyApi by CashInputAccessSafetyHandler(keyboardHelper, viewAccessApi) {

        /* Поддержка API для использования в DataBinding. */
        companion object {
            /* Nothing. */
        }

        private val context: Context
            get() = viewAccessApi.paymentView.context

        private fun Context.isPortraitOrientation(): Boolean =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        private val originalConstraint by lazy {
            ConstraintSet().apply {
                clone(viewAccessApi.paymentView)
            }
        }

        private val changedConstraint by lazy {
            ConstraintSet().apply {
                clone(viewAccessApi.paymentView)

                /* Привязываем кнопку "Комментарий" к началу кнопки "Вернуть". */
                clear(viewAccessApi.commentButton.id, START)
                connect(viewAccessApi.commentButton.id, END, viewAccessApi.checkButton.id, START)
                connect(viewAccessApi.commentButton.id, BOTTOM, viewAccessApi.checkButton.id, BOTTOM, 0)
            }
        }

        override fun configureRefundScreenConstraints(refundPaymentInnerMode: RefundPaymentInnerMode) {
            /* Для портретной верстки нужно изменить привязку. */
            if (context.isPortraitOrientation()) {
                when (refundPaymentInnerMode) {
                    RefundPaymentInnerMode.REFUND -> originalConstraint
                    RefundPaymentInnerMode.REFUND_NONFISCAL -> changedConstraint
                    else -> originalConstraint
                }.applyTo(viewAccessApi.paymentView)
            }
        }

        override fun setCheckButtonVisibility(isVisible: Boolean) {
            viewAccessApi.checkButton.isVisible = isVisible
            if (isVisible) viewAccessApi.moneyInputField.editableView.initializeCursorPositionAfterDraw()
        }

        override fun setCheckDoubleButtonVisibility(isVisible: Boolean) {
            viewAccessApi.checkDoubleButton.viewPropertiesApi.isVisible = isVisible
            if (isVisible) viewAccessApi.checkDoubleButton.dangerousApi.editableView.initializeCursorPositionAfterDraw()
        }
    }

    @DangerousApi
    private class DangerousViewAccessApiHandler(
        private val viewDelegateBinding: RetailViewsRefundPaymentLayoutBinding
    ) : RefundPaymentViewAccessDangerousApi.Handler,
        CashInputAccessDangerousApi by CashInputAccessDangerousHandler(viewDelegateBinding.includeCashInput),
        CommentInfoAccessDangerousApi by CommentInfoAccessDangerousHandler(viewDelegateBinding.root.rootView),
        MixPaymentAccessDangerousApi by MixPaymentAccessDangerousHandler(viewDelegateBinding.includeMixButtons),
        PaymentButtonsAccessDangerousApi by PaymentButtonsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        ToolbarViewsAccessDangerousApi by ToolbarViewsAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons),
        PaymentTypeAccessDangerousApi by PaymentTypeAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons),
        TaxationInfoAccessDangerousApi by TaxationInfoAccessDangerousHandler(
            taxationViewStub = viewDelegateBinding.retailViewsTaxationSystemContentStub.viewStub!!
        ) {

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
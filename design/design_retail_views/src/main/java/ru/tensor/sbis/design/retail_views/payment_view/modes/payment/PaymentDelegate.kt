package ru.tensor.sbis.design.retail_views.payment_view.modes.payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenu
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_models.utils.ZERO_MONEY_VALUE
import ru.tensor.sbis.design.retail_models.utils.isZero
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.common.DynamicButtonsLayout
import ru.tensor.sbis.design.retail_views.common.setDynamicButtonsActionId
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsPaymentExtraButtonBinding
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsPaymentLayoutBinding
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.IncludeViewsInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.initializeCursorPositionAfterDraw
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.tryFindFragmentManager
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
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous.DiscountViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous.DiscountViewsAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety.DiscountViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety.DiscountViewsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.action_listeners.DiscountViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.action_listeners.DiscountViewsActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.set_data.DiscountViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.set_data.DiscountViewsSetDataHandler
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
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentViewAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.dangerous.PaymentViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.inner_types.PaymentInnerMode
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration
import ru.tensor.sbis.design.retail_views.popup_menu.config.MenuItemWrapper
import ru.tensor.sbis.design.retail_views.popup_menu.config.transformToDefaultItem
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import java.math.BigDecimal
import kotlin.properties.Delegates
import ru.tensor.sbis.design.R as RDesign

/** Управляет поведением вьюшек, связанных с режимом отображения для Оплаты. */
internal class PaymentDelegate private constructor() : PaymentDelegateApi {

    companion object {
        /** Дефолтный метод для получения объекта [PaymentDelegateApi]. */
        fun createDefault(): PaymentDelegateApi = PaymentDelegate()
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
                ),
                InputViewWrapper(
                    inputField = viewAccessApi.bonusButton.bonusCountInput,
                    fieldInputType = CustomNumericKeyboardHelper.FieldType.NUMBER_GROUPING
                )
            )
        ).banknotesApi.apply {
            /* Если нет фокуса, то нажатие на банкноты автоматически установит фокус в это поле. */
            inputViewForAutoFocus = defaultInputFocusView
        }
    }

    /* Инициализируется вместе с RenderApi. */
    internal var viewAccessApi: PaymentViewAccessDangerousApi.Handler by Delegates.notNull()

    override val renderApiHandler: PaymentRenderApi.Handler by lazy { RenderApiHandler() }

    override val setDataApi: PaymentSetDataApi.Handler by lazy {
        SetDataApiHandler(
            banknotesApi = banknotesApi,
            actionListenerApi = actionListenerApi,
            viewSafetyApi = viewSafetyApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val actionListenerApi: PaymentActionListenerApi.Handler by lazy {
        ActionListenerApiHandler(
            banknotesApi = banknotesApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val viewSafetyApi: PaymentViewAccessSafetyApi.Handler by lazy {
        val paymentButtonsAccessSafetyHandler = PaymentButtonsAccessSafetyHandler(viewAccessApi)
        SafetyViewAccessApiHandler(
            keyboardHelper = keyboardHelper,
            viewAccessApi = viewAccessApi,
            paymentButtonsAccessSafetyHandler = paymentButtonsAccessSafetyHandler
        )
    }

    /* Всегда пустой конструктор, для инициализации используйте метод 'doAfterInflate(initializeApi)'. */
    internal inner class RenderApiHandler : PaymentRenderApi.Handler {

        override var viewDelegateBinding: RetailViewsPaymentLayoutBinding by Delegates.notNull()

        @SuppressLint("UnsafeOptInUsageWarning")
        override fun inflateViewDelegateBinding(layoutInflater: LayoutInflater, rootView: ViewGroup) =
            RetailViewsPaymentLayoutBinding.inflate(layoutInflater, rootView, true)
                .also { binding ->
                    /* После инфлейта инициализируем поле с доступом к View элементам. */
                    viewDelegateBinding = binding

                    /* Выполняем инициализацию Dangerous Api. */
                    DangerousViewAccessApiHandler(viewDelegateBinding)
                        .also { dangerousApi ->
                            viewAccessApi = dangerousApi

                            /* Действие после инициализации View делегата. */
                            doAfterInflate(
                                initializeApi = PaymentInitializeApi(
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

        override fun doAfterInflate(initializeApi: PaymentInitializeApi) {
            /* Выполнение базовой настройки делегата. */
            super.doAfterInflate(initializeApi)

            /* Настройка Banknotes Delegate. */
            initializeApi.banknotesApi.initialize(initializeApi.keyboardHelper)

            /* Настройка поля ввода денег. */
            initializeApi.viewAccessApi.moneyInputField.editableView.initializeCursorPositionAfterDraw()

            /* Устанавливаем стандартное диалоговое окно на нажатие кнопки с доп. действиями. */
            initializeApi.viewAccessApi.dynamicButtonsLayout.setOnHiddenActionsButtonListener(::showMoreMenuPopup)

            /* По дефолту в контекстном меню отображаются все типы оплат. */
            initializeApi.setDataApi.allowedPaymentTypes = PaymentInnerMode.values().asList()
        }

        override fun getFieldsForLockSoftKeyboard(initializeApi: PaymentInitializeApi): List<EditText> =
            initializeApi.viewAccessApi.allInputFields

        override fun getIncludeViewsInitializeApi(initializeApi: PaymentInitializeApi): List<IncludeViewsInitializeApi> =
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
                    actionListenerApi = actionListenerApi,
                    setDataApi = initializeApi.setDataApi
                )
            )

        private fun showMoreMenuPopup(anchorView: View, actionIds: List<String>) {
            val items = actionIds
                .map(ExtraMenuItem::valueOf)
                .map { item ->
                    MenuItemWrapper(
                        titleResId = item.titleResId,
                        action = { onExtraMenuItemClick(item) },
                        imageIcon = item.imageIcon
                    )
                }

            SbisMenu(children = items.map { it.transformToDefaultItem() })
                .showMenu(
                    viewAccessApi.paymentView.tryFindFragmentManager(),
                    anchor = anchorView,
                    dimType = DimType.SHADOW,
                    customWidth = R.dimen.retail_views_context_menu_width
                )
        }

        private fun onExtraMenuItemClick(item: ExtraMenuItem) {
            when (item) {
                ExtraMenuItem.QR_CODE -> viewAccessApi.qrCodeButton
                ExtraMenuItem.SEND_INVOICE -> viewAccessApi.sendButton
                ExtraMenuItem.COMMENT -> viewAccessApi.commentButton
            }.performClick()
        }
    }

    internal class SetDataApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val actionListenerApi: PaymentActionListenerApi.Handler,
        private val viewSafetyApi: PaymentViewAccessSafetyApi.Handler,
        private val viewAccessApi: PaymentViewAccessDangerousApi.Handler
    ) : PaymentSetDataApi.Handler,
        CreditInfoSetDataApi by CreditInfoSetDataHandler(viewAccessApi),
        ToolbarViewsSetDataApi by ToolbarViewsSetDataHandler(viewAccessApi),
        PaymentTypeSetDataApi by PaymentTypeSetDataHandler(actionListenerApi),
        PaymentButtonsSetDataApi by PaymentButtonsSetDataHandler(viewAccessApi),
        CommentInfoSetDataApi by CommentInfoSetDataHandler(viewSafetyApi, viewAccessApi),
        DiscountViewsSetDataApi by DiscountViewsSetDataHandler(viewSafetyApi, viewAccessApi),
        CashInputSetDataApi by CashInputSetDataHandler(banknotesApi, viewSafetyApi, viewAccessApi),
        TaxationInfoSetDataApi by TaxationInfoSetDataHandler(viewSafetyApi, viewAccessApi, actionListenerApi) {

        private var paymentMode: PaymentInnerMode? = null
            set(value) {
                field = value
                reconfigurePaymentTypeMenu()
            }

        private val isLandscape: Boolean
            get() = viewAccessApi.paymentView.context.resources.getBoolean(RDesign.bool.is_landscape)

        override var allowedPaymentTypes: List<PaymentInnerMode> = emptyList()
            set(value) {
                field = value
                reconfigurePaymentTypeMenu()
            }

        override fun setPaymentType(paymentInnerMode: PaymentInnerMode): Unit = with(viewAccessApi) {
            paymentMode = paymentInnerMode

            paymentTypeButton.apply {
                val textColorState = ColorStateList.valueOf(StyleColor.UNACCENTED.getTextColor(context))

                model = SbisButtonModel(
                    title = SbisButtonTitle(
                        text = context.getString(paymentInnerMode.stringResId),
                        style = SbisButtonTitleStyle.create(textColorState, textColorState, textColorState)
                    )
                )
            }

            /* Обновляем видимость кнопок оплаты в зависимости от типа оплаты. */
            val maxButtons = when (paymentInnerMode) {
                PaymentInnerMode.PREPAYMENT -> 2
                else -> if (isLandscape) 2 else 3
            }
            dynamicButtonsLayout.setMaxChildren(maxButtons)

            /* Устанавливаем фокус на нужное поле в зависимости от типа оплаты. */
            when (paymentInnerMode) {
                PaymentInnerMode.PAYMENT,
                PaymentInnerMode.NONFISCAL -> viewAccessApi.moneyInputField.editableView

                PaymentInnerMode.PREPAYMENT,
                PaymentInnerMode.CREDIT -> viewAccessApi.checkDoubleButton.dangerousApi.editableView
            }.initializeCursorPositionAfterDraw()
        }

        override fun setCheckButtonTextValue(amount: BigDecimal) {
            if (amount.isZero()) {
                val text = viewAccessApi.checkButton.context.getString(R.string.retail_views_payment_type_payment_text)
                viewAccessApi.checkButton.setTitle(text)
            } else {
                viewAccessApi.checkButton.setTitle(amountFormat.format(amount))
            }
        }

        private fun reconfigurePaymentTypeMenu() {
            configurePaymentTypeMenu(
                fragmentManager = viewAccessApi.paymentView.tryFindFragmentManager(),
                configuration = PopupMenuConfiguration(
                    menuItems = allowedPaymentTypes
                        .map { paymentType ->
                            MenuItemWrapper(
                                titleResId = paymentType.stringResId,
                                isSelected = this.paymentMode == paymentType,
                                action = { actionListenerApi.onPaymentTypeClick?.invoke(paymentType) }
                            )
                        }
                )
            )
        }
    }

    internal class ActionListenerApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val viewAccessApi: PaymentViewAccessDangerousApi.Handler
    ) : PaymentActionListenerApi.Handler,
        CashInputActionListenerApi by CashInputActionListenerHandler(banknotesApi),
        ExtraViewsActionListenerApi by ExtraViewsActionListenerHandler(viewAccessApi),
        MixPaymentActionListenerApi by MixPaymentActionListenerHandler(viewAccessApi),
        CommentInfoActionListenerApi by CommentInfoActionListenerHandler(viewAccessApi),
        PaymentTypeActionListenerApi by PaymentTypeActionListenerHandler(viewAccessApi),
        ToolbarViewsActionListenerApi by ToolbarViewsActionListenerHandler(viewAccessApi),
        TaxationInfoActionListenerApi by TaxationInfoActionListenerHandler(viewAccessApi),
        PaymentButtonsActionListenerApi by PaymentButtonsActionListenerHandler(viewAccessApi),
        DiscountViewsActionListenerApi by DiscountViewsActionListenerHandler(viewAccessApi) {

        override var onPaymentTypeClick: ((PaymentInnerMode) -> Unit)? = null

        override var onMixPaymentClickExtraAction: ((mixPaymentMode: Boolean) -> Unit)? = null
            set(value) {
                field = { mixPaymentMode ->
                    value?.invoke(mixPaymentMode)

                    if (mixPaymentMode) {
                        /* При переключении смешанной оплаты, требуется дозаполнять поле "Картой". */
                        banknotesApi.autoSetupRemainingAmountForInputField(
                            inputField = viewAccessApi.payCardButton.dangerousApi.editableView
                        )
                    }
                }
            }
    }

    internal class SafetyViewAccessApiHandler(
        private val keyboardHelper: CustomNumericKeyboardHelper,
        private val viewAccessApi: PaymentViewAccessDangerousApi.Handler,
        private val paymentButtonsAccessSafetyHandler: PaymentButtonsAccessSafetyHandler
    ) : PaymentViewAccessSafetyApi.Handler,
        ExtraViewsAccessSafetyApi by ExtraViewsAccessSafetyHandler(viewAccessApi),
        CreditInfoAccessSafetyApi by CreditInfoAccessSafetyHandler(viewAccessApi),
        MixPaymentAccessSafetyApi by MixPaymentAccessSafetyHandler(viewAccessApi),
        PaymentTypeAccessSafetyApi by PaymentTypeAccessSafetyHandler(viewAccessApi),
        CommentInfoAccessSafetyApi by CommentInfoAccessSafetyHandler(viewAccessApi),
        TaxationInfoAccessSafetyApi by TaxationInfoAccessSafetyHandler(viewAccessApi),
        ToolbarViewsAccessSafetyApi by ToolbarViewsAccessSafetyHandler(viewAccessApi),
        DiscountViewsAccessSafetyApi by DiscountViewsAccessSafetyHandler(viewAccessApi),
        PaymentButtonsAccessSafetyApi by paymentButtonsAccessSafetyHandler,
        AllInputFieldsAccessSafetyApi by AllInputFieldsAccessSafetyHandler(viewAccessApi),
        CashInputAccessSafetyApi by CashInputAccessSafetyHandler(keyboardHelper, viewAccessApi) {

        override fun setManualInputEnable(isEnabled: Boolean) =
            with(viewAccessApi) {
                setKeyboardEnabled(isEnabled)
                setBanknotesEnabled(isEnabled)
                setMoneyInputEnabled(isEnabled)
                setClientButtonEnabled(isEnabled)
                setDiscountButtonEnabled(isEnabled)
                setTaxationSystemInfoEnabled(isEnabled)
            }

        override fun overrideHints(override: Boolean) {
            keyboardHelper.overrideHints = override
        }

        override fun setMoreButtonVisibility(isVisible: Boolean) {
            viewAccessApi.moreButton.isVisible = isVisible
            viewAccessApi.dynamicButtonsLayout.setHiddenActionsButtonAlwaysVisible(isVisible)
        }

        override fun setCardPaymentButtonVisibility(isVisible: Boolean) {
            paymentButtonsAccessSafetyHandler.setCardPaymentButtonVisibility(isVisible)

            val isLandscape = viewAccessApi.paymentView.resources.getBoolean(RDesign.bool.is_landscape)
            if (!isLandscape) {
                updateButtonsLayout(isPayCardVisible = isVisible, isLandscape = false)
            }
        }

        /**
         * Меняем направление дополнительных кнопок оплаты в зависимости от видимости кнопки "Оплата картой".
         *
         *      - если кнопка "Оплата картой" видима.
         *      [▩][🖅][🖉] ->
         *      [PayCard button] [Check button]
         *
         *      - если кнопки "Оплата картой" скрыта.
         *      <- [▩][🖅][🖉] [Check button]
         *
         * @param [isLandscape] - доп. параметр, на случай, если данный метод потребуется
         * вызывать и для альбомной ориентации. Меняет точку привязки для [DynamicButtonsLayout],
         * т.к. портретная и альбомная верстки имеют различный набор элементов разметки.
         */
        private fun updateButtonsLayout(isPayCardVisible: Boolean, isLandscape: Boolean) {
            val constraintSet = ConstraintSet()

            constraintSet.clone(viewAccessApi.paymentView)

            if (isPayCardVisible) {
                constraintSet.connect(
                    R.id.retail_views_buttons,
                    ConstraintSet.START,
                    R.id.retail_views_card_payment_double_button,
                    ConstraintSet.START
                )
                constraintSet.clear(R.id.retail_views_buttons, ConstraintSet.END)
                constraintSet.connect(
                    R.id.retail_views_buttons,
                    ConstraintSet.BOTTOM,
                    R.id.retail_views_card_payment_double_button,
                    ConstraintSet.TOP
                )
            } else {
                constraintSet.clear(R.id.retail_views_buttons, ConstraintSet.START)
                constraintSet.connect(
                    R.id.retail_views_buttons,
                    ConstraintSet.END,
                    if (isLandscape) R.id.retail_views_card_payment_double_button
                    else R.id.retail_views_barrier_payment_button,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.retail_views_buttons,
                    ConstraintSet.BOTTOM,
                    R.id.retail_views_card_payment_double_button,
                    ConstraintSet.TOP
                )
            }

            constraintSet.applyTo(viewAccessApi.paymentView)
        }

        /**
         * Сконфигурировать меню с дополнительными опциями.
         * Если элемент в списке всего один, то добавляем его в виде кнопки.
         * В остальных случаях список с опциями сливается со списком динамических кнопок (см. [DynamicButtonsLayout]).
         */
        override fun configureMoreMenu(configuration: PopupMenuConfiguration) {
            val externalMenuItems = configuration.menuItems
            var singleExternalItem: Pair<String, MenuItemWrapper>? = null

            when {
                // если в списке больше одной опции показываем кнопку "Еще" для показа опций
                externalMenuItems.size > 1 -> {
                    setMoreButtonVisibility(true)
                }

                // если опция только одна добавляем ее в виде кнопки
                externalMenuItems.size == 1 -> {
                    val externalMenuItem = externalMenuItems.first()
                    val actionId = addExtraButton(viewAccessApi.dynamicButtonsLayout, externalMenuItem)
                    singleExternalItem = actionId to externalMenuItem
                }
            }

            viewAccessApi.dynamicButtonsLayout.setOnHiddenActionsButtonListener { anchorView, actionIds ->
                val moreMenuItems = buildMoreMenuItems(actionIds, singleExternalItem, externalMenuItems)

                SbisMenu(children = moreMenuItems.map { it.transformToDefaultItem() })
                    .showMenu(
                        viewAccessApi.paymentView.tryFindFragmentManager(),
                        anchor = anchorView,
                        dimType = DimType.SHADOW,
                        customWidth = configuration.displayOptions.width
                    )
            }
        }

        private fun addExtraButton(container: ViewGroup, item: MenuItemWrapper): String {
            val actionId = container.context.getString(item.titleResId)
            val button = RetailViewsPaymentExtraButtonBinding.inflate(
                LayoutInflater.from(container.context),
                container, false
            ).root.apply {
                item.imageIcon?.let { icon = SbisButtonTextIcon(it) }
                setOnClickListener { item.action() }
                setDynamicButtonsActionId(context.getString(item.titleResId))
            }

            container.addView(button)

            return actionId
        }

        private fun buildMoreMenuItems(
            actionIds: List<String>,
            singleExternalItem: Pair<String, MenuItemWrapper>?,
            externalMenuItems: List<MenuItemWrapper>
        ): List<MenuItemWrapper> {
            val extraItems = actionIds.mapNotNull { actionId ->
                val extraMenuItem = ExtraMenuItem.values().firstOrNull { it.name == actionId }

                when {
                    extraMenuItem != null ->
                        MenuItemWrapper(
                            titleResId = extraMenuItem.titleResId,
                            action = { onExtraMenuItemClick(extraMenuItem) },
                            imageIcon = extraMenuItem.imageIcon
                        )

                    singleExternalItem != null && singleExternalItem.first == actionId ->
                        MenuItemWrapper(
                            titleResId = singleExternalItem.second.titleResId,
                            action = { singleExternalItem.second.action() },
                            imageIcon = singleExternalItem.second.imageIcon
                        )

                    else -> null
                }
            }

            return if (externalMenuItems.size > 1) {
                extraItems + externalMenuItems
            } else {
                extraItems
            }
        }

        private fun onExtraMenuItemClick(item: ExtraMenuItem) {
            when (item) {
                ExtraMenuItem.QR_CODE -> viewAccessApi.qrCodeButton
                ExtraMenuItem.SEND_INVOICE -> viewAccessApi.sendButton
                ExtraMenuItem.COMMENT -> viewAccessApi.commentButton
            }.performClick()
        }
    }

    @DangerousApi
    private class DangerousViewAccessApiHandler(
        private val viewDelegateBinding: RetailViewsPaymentLayoutBinding
    ) : PaymentViewAccessDangerousApi.Handler,
        CreditInfoAccessDangerousApi by CreditInfoAccessDangerousHandler(viewDelegateBinding.root.rootView),
        ExtraViewsAccessDangerousApi by ExtraViewsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        CashInputAccessDangerousApi by CashInputAccessDangerousHandler(viewDelegateBinding.includeCashInput),
        CommentInfoAccessDangerousApi by CommentInfoAccessDangerousHandler(viewDelegateBinding.root.rootView),
        MixPaymentAccessDangerousApi by MixPaymentAccessDangerousHandler(viewDelegateBinding.includeMixButtons),
        DiscountViewsAccessDangerousApi by DiscountViewsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        PaymentButtonsAccessDangerousApi by PaymentButtonsAccessDangerousHandler(viewDelegateBinding.root.rootView),
        PaymentTypeAccessDangerousApi by PaymentTypeAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons),
        ToolbarViewsAccessDangerousApi by ToolbarViewsAccessDangerousHandler(viewDelegateBinding.includeToolbarButtons),
        TaxationInfoAccessDangerousApi by TaxationInfoAccessDangerousHandler(
            taxationViewStub = viewDelegateBinding.retailViewsTaxationSystemContentStub.viewStub!!
        ) {

        override val paymentView: PaymentView
            get() = viewDelegateBinding.root.parent as PaymentView

        override val paymentTypeButton: SbisButton
            get() = viewDelegateBinding.includeToolbarButtons.findViewById(R.id.retail_views_payment_type_button)

        override val dynamicButtonsLayout: DynamicButtonsLayout
            get() = viewDelegateBinding.retailViewsButtons

        override val allInputFields: List<EditText>
            get() = listOf(
                moneyInputField.editableView,
                bonusButton.bonusCountInput,
                payCardButton.dangerousApi.editableView,
                checkDoubleButton.dangerousApi.editableView
            )
    }

    /** Элементы контекстного меню. */
    enum class ExtraMenuItem(
        @StringRes val titleResId: Int,
        val imageIcon: SbisMobileIcon.Icon
    ) {
        /** Элемент QR-кода для контекстного меню. */
        QR_CODE(
            titleResId = R.string.payment_qr_code_button_text,
            imageIcon = SbisMobileIcon.Icon.smi_QRCode
        ),

        /** Элемент отправки счета для контекстного меню. */
        SEND_INVOICE(
            titleResId = R.string.payment_send_invoice_button_text,
            imageIcon = SbisMobileIcon.Icon.smi_SwipeUnload
        ),

        /** Элемент комментария для контекстного меню. */
        COMMENT(
            titleResId = R.string.payment_comment_button_text,
            imageIcon = SbisMobileIcon.Icon.smi_EditComment
        )
    }
}
package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsDepositLayoutBinding
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.initializeCursorPositionAfterDraw
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerHandler
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegate.InputViewWrapper
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalViewAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.dangerous.DepositWithdrawalViewAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import kotlin.properties.Delegates

/** Управляет поведением вьюшек, связанных с режимом отображения "Внести/Изъять". */
internal class DepositWithdrawalDelegate private constructor() : DepositWithdrawalDelegateApi {

    companion object {
        /** Дефолтный метод для получения объекта [DepositWithdrawalDelegateApi]. */
        fun createDefault(): DepositWithdrawalDelegateApi = DepositWithdrawalDelegate()
    }

    private val keyboardHelper by lazy { CustomNumericKeyboardHelper() }

    private val context: Context
        get() = viewAccessApi.paymentView.context

    private val banknotesApi: BanknotesDelegateApi.Handler by lazy {
        BanknotesDelegate.defaultCreator(
            context = context,
            targetAncestor = viewAccessApi.paymentView,
            keyboardView = viewAccessApi.keyboardView,
            banknotesViewBinding = viewAccessApi.banknotesView,
            fieldsForBanknotesInput = listOf(
                InputViewWrapper(viewAccessApi.moneyInputField.editableView, PaymentMethod.CASH)
            )
        ).banknotesApi.apply {
            /* Если нет фокуса, то нажатие на банкноты автоматически установит фокус в это поле. */
            inputViewForAutoFocus = viewAccessApi.moneyInputField.editableView
        }
    }

    /* Инициализируется вместе с RenderApi. */
    internal var viewAccessApi: DepositWithdrawalViewAccessDangerousApi.Handler by Delegates.notNull()

    override val renderApiHandler: DepositWithdrawalRenderApi.Handler by lazy { RenderApiHandler() }

    override val setDataApi: DepositWithdrawalSetDataApi.Handler by lazy {
        SetDataApiHandler(
            viewSafetyApi = viewSafetyApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val actionListenerApi: DepositWithdrawalActionListenerApi.Handler by lazy {
        ActionListenerApiHandler(
            banknotesApi = banknotesApi,
            viewAccessApi = viewAccessApi
        )
    }

    override val viewSafetyApi: DepositWithdrawalViewAccessSafetyApi.Handler by lazy {
        SafetyViewAccessApiHandler(
            keyboardHelper = keyboardHelper,
            viewAccessApi = viewAccessApi
        )
    }

    /* Всегда пустой конструктор, для инициализации используйте метод 'doAfterInflate(initializeApi)'. */
    internal inner class RenderApiHandler : DepositWithdrawalRenderApi.Handler {

        override var viewDelegateBinding: RetailViewsDepositLayoutBinding by Delegates.notNull()

        @SuppressLint("UnsafeOptInUsageWarning")
        override fun inflateViewDelegateBinding(layoutInflater: LayoutInflater, rootView: ViewGroup) =
            RetailViewsDepositLayoutBinding.inflate(layoutInflater, rootView, true)
                .also { binding ->
                    /* После инфлейта инициализируем поле с доступом к View элементам. */
                    viewDelegateBinding = binding

                    /* Выполняем инициализацию Dangerous Api. */
                    DangerousViewAccessApiHandler(binding).let { dangerousApi ->
                        viewAccessApi = dangerousApi

                        /* Действие после инициализации View делегата. */
                        doAfterInflate(
                            initializeApi = DepositWithdrawalInitializeApi(
                                keyboardHelper = keyboardHelper
                            ).also { initializeApi ->
                                initializeApi.banknotesApi = banknotesApi
                                initializeApi.viewAccessApi = viewAccessApi
                            }
                        )
                    }
                }

        override fun doAfterInflate(initializeApi: DepositWithdrawalInitializeApi) {
            /* Выполнение базовой настройки делегата. */
            super.doAfterInflate(initializeApi)

            /* Настройка Banknotes Delegate. */
            initializeApi.banknotesApi.initialize(initializeApi.keyboardHelper)

            /* Настройка поля ввода денег. */
            initializeApi.viewAccessApi.moneyInputField.editableView.initializeCursorPositionAfterDraw()
        }

        override fun getFieldsForLockSoftKeyboard(initializeApi: DepositWithdrawalInitializeApi): List<EditText> =
            initializeApi.viewAccessApi.allInputFields
    }

    internal class SetDataApiHandler(
        private val viewSafetyApi: DepositWithdrawalViewAccessSafetyApi.Handler,
        private val viewAccessApi: DepositWithdrawalViewAccessDangerousApi.Handler
    ) : DepositWithdrawalSetDataApi.Handler,
        CommentInfoSetDataApi by CommentInfoSetDataHandler(viewSafetyApi, viewAccessApi) {

        /* Поддержка API для использования в DataBinding. */
        companion object {

            /** @see DepositWithdrawalSetDataApi.Handler.setCommentTextValue */
            @JvmStatic
            @BindingAdapter("setupReasonTextValue")
            fun PaymentView.setupReasonTextValue(reasonText: String) {
                api.depositWithDrawApi.setDataApi
                    .setCommentTextValue(reasonText)
            }
        }
    }

    internal class ActionListenerApiHandler(
        private val banknotesApi: BanknotesDelegateApi.Handler,
        private val viewAccessApi: DepositWithdrawalViewAccessDangerousApi.Handler
    ) : DepositWithdrawalActionListenerApi.Handler,
        CashInputActionListenerApi by CashInputActionListenerHandler(banknotesApi),
        CommentInfoActionListenerApi by CommentInfoActionListenerHandler(viewAccessApi),
        ToolbarViewsActionListenerApi by ToolbarViewsActionListenerHandler(viewAccessApi) {

        /* Поддержка API для использования в DataBinding. */
        companion object {
            /* Nothing. */
        }

        override fun setDepositClickListener(action: () -> Unit) {
            viewAccessApi.depositButton.preventDoubleClickListener(LONG_CLICK_DELAY) {
                action.invoke()
            }
        }

        override fun setWithdrawalClickListener(action: () -> Unit) {
            viewAccessApi.withdrawalButton.preventDoubleClickListener(LONG_CLICK_DELAY) {
                action.invoke()
            }
        }
    }

    internal class SafetyViewAccessApiHandler(
        private val keyboardHelper: CustomNumericKeyboardHelper,
        private val viewAccessApi: DepositWithdrawalViewAccessDangerousApi.Handler
    ) : DepositWithdrawalViewAccessSafetyApi.Handler,
        CommentInfoAccessSafetyApi by CommentInfoAccessSafetyHandler(viewAccessApi),
        ToolbarViewsAccessSafetyApi by ToolbarViewsAccessSafetyHandler(viewAccessApi),
        AllInputFieldsAccessSafetyApi by AllInputFieldsAccessSafetyHandler(viewAccessApi),
        CashInputAccessSafetyApi by CashInputAccessSafetyHandler(keyboardHelper, viewAccessApi) {

        /* Поддержка API для использования в DataBinding. */
        companion object {

            /** @see DepositWithdrawalViewAccessSafetyApi.Handler.setDepositButtonVisible */
            @JvmStatic
            @BindingAdapter("setupDepositButtonVisible")
            fun PaymentView.setupDepositButtonVisible(isVisible: Boolean) {
                api.depositWithDrawApi.viewSafetyApi
                    .setDepositButtonVisible(isVisible)
            }

            /** @see DepositWithdrawalViewAccessSafetyApi.Handler.setWithdrawalButtonVisible */
            @JvmStatic
            @BindingAdapter("setupWithdrawalButtonVisible")
            fun PaymentView.setupWithdrawalButtonVisible(isVisible: Boolean) {
                api.depositWithDrawApi.viewSafetyApi
                    .setWithdrawalButtonVisible(isVisible)
            }
        }

        override fun setDepositButtonVisible(isVisible: Boolean) {
            viewAccessApi.depositButton.isVisible = isVisible
        }

        override fun setWithdrawalButtonVisible(isVisible: Boolean) {
            viewAccessApi.withdrawalButton.isVisible = isVisible
        }
    }

    @DangerousApi
    private class DangerousViewAccessApiHandler(
        private val viewDelegateBinding: RetailViewsDepositLayoutBinding
    ) : DepositWithdrawalViewAccessDangerousApi.Handler,
        CashInputAccessDangerousApi by CashInputAccessDangerousHandler(viewDelegateBinding.includeCashInput),
        CommentInfoAccessDangerousApi by CommentInfoAccessDangerousHandler(viewDelegateBinding.root.rootView),
        ToolbarViewsAccessDangerousApi by ToolbarViewsAccessDangerousHandler(viewDelegateBinding.root.rootView) {

        override val paymentView: PaymentView
            get() = viewDelegateBinding.root.parent as PaymentView

        override val depositButton: SbisButton
            get() = viewDelegateBinding.retailViewsDepositButton

        override val withdrawalButton: SbisButton
            get() = viewDelegateBinding.retailViewsWithdrawalButton

        override val allInputFields: List<EditText>
            get() = listOf(moneyInputField.editableView)
    }
}
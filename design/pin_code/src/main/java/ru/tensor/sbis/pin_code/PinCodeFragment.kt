package ru.tensor.sbis.pin_code

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.findViewModelHierarchical
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.DangerButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.confirmation_dialog.BaseContentProvider
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonHandler
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonOrientation
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.design.container.CONTAINER_DEFAULT_TAG
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.createParcelableFragmentContainer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.header.createHeader
import ru.tensor.sbis.design.header.data.HeaderTitleSettings
import ru.tensor.sbis.design.header.data.LeftCustomContent
import ru.tensor.sbis.design.header.data.RightCustomContent
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import ru.tensor.sbis.modalwindows.movable_container.CustomClosePanelAction
import ru.tensor.sbis.pin_code.databinding.PinCodeAccessCodeInputViewBinding
import ru.tensor.sbis.pin_code.databinding.PinCodeBubbleLimitedInputViewBinding
import ru.tensor.sbis.pin_code.databinding.PinCodeFragmentBinding
import ru.tensor.sbis.pin_code.databinding.PinCodePasswordInputViewBinding
import ru.tensor.sbis.pin_code.decl.ConfirmationType
import ru.tensor.sbis.pin_code.decl.PinCodeAnchor
import ru.tensor.sbis.pin_code.decl.PinCodeConfiguration
import ru.tensor.sbis.pin_code.decl.PinCodePeriod
import ru.tensor.sbis.pin_code.decl.PinCodeSuccessResult
import ru.tensor.sbis.pin_code.decl.PinCodeTransportType
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl
import ru.tensor.sbis.pin_code.util.createPeriodPickerMenu
import ru.tensor.sbis.pin_code.util.runVibration
import ru.tensor.sbis.pin_code.view.CodeInputViewType
import ru.tensor.sbis.pin_code.view.CodeInputViewType.ACCESS_CODE_INPUT_VIEW
import ru.tensor.sbis.pin_code.view.CodeInputViewType.DEFAULT_INPUT_VIEW
import ru.tensor.sbis.pin_code.view.CodeInputViewType.LIMITED_INPUT_VIEW
import timber.log.Timber
import ru.tensor.sbis.design.theme.HorizontalAlignment as DesignHorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment as DesignVerticalAlignment

/**
 * Фрагмент ввода пин-кода
 *
 * @author mb.kruglova
 */
internal class PinCodeFragment :
    Fragment(),
    Content,
    AdjustResizeHelper.KeyboardEventListener,
    ConfirmationButtonHandler {

    private val feature: PinCodeFeatureImpl<*>? by lazy { findViewModelHierarchical() }
    private val pinCodeViewModel: PinCodeViewModel<*> by viewModels { PinCodeVmFactory(this, feature) }
    private lateinit var binding: PinCodeFragmentBinding
    private var codeEditTextBinding: PinCodeBubbleLimitedInputViewBinding? = null
    private var passwordInputViewBinding: PinCodePasswordInputViewBinding? = null
    private var accessCodeInputViewBinding: PinCodeAccessCodeInputViewBinding? = null
    private var pinCodeInput: View? = null
    private var acceptButton: SbisRoundButton? = null
    private lateinit var codeType: CodeInputViewType
    private var isTablet = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PinCodeFragmentBinding.inflate(applyTheme(inflater)).also {
            it.viewModel = pinCodeViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        initPinCodeHeader()

        initPinCodeInput()

        isTablet = DeviceConfigurationUtils.isTablet(requireContext())

        if (!isTablet) {
            (parentFragment as DialogFragment).requireDialog().window!!
                .setSoftInputMode(
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        SOFT_INPUT_ADJUST_RESIZE
                    } else {
                        SOFT_INPUT_ADJUST_NOTHING
                    }
                )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCustomButtonLink()

        pinCodeViewModel.confirmBtnEnabled.observe(viewLifecycleOwner) {
            acceptButton?.isEnabled = it
            acceptButton?.setVisibility(pinCodeViewModel.confirmBtnVisible.value == true, it)
        }

        pinCodeViewModel.confirmBtnVisible.observe(viewLifecycleOwner) {
            acceptButton?.setVisibility(it, pinCodeViewModel.confirmBtnEnabled.value == true)
        }

        pinCodeViewModel.closeEvent.observe(
            viewLifecycleOwner
        ) {
            if (isTablet) {
                (parentFragment as DialogFragment).dismiss()
            } else {
                (parentFragment as Container.Closeable).closeContainer()
            }
        }

        pinCodeViewModel.retrySuccessEvent.observe(
            viewLifecycleOwner
        ) {
            showKeyboard() // восстанавливаем фокус т.к. поле ввода скрывалось
        }

        pinCodeViewModel.confirmationErrorEvent.observe(viewLifecycleOwner) { isEventHandled ->
            if (isEventHandled) {
                requireActivity().runVibration()
                clearPinCodeInputView()
            }
        }

        pinCodeViewModel.errorEvent.observe(viewLifecycleOwner) {
            showToastWithError(it)
            clearPinCodeInputView()
        }

        pinCodeViewModel.longErrorEvent.observe(viewLifecycleOwner) {
            showDialogFragmentWithError(it)
            clearPinCodeInputView()
        }

        pinCodeViewModel.showPeriodPickerEvent.observe(viewLifecycleOwner, ::showPeriodPicker)

        pinCodeViewModel.period.observe(viewLifecycleOwner) {
            onPeriodChanged(it)
            showKeyboard()
        }
    }

    private fun setupCustomButtonLink() = with(binding) {
        if (pinCodeViewModel.customLinkButtonTitle.isNotEmpty()) {
            pinCodeCustomLinkButton.apply {
                isVisible = true
                text = pinCodeViewModel.customLinkButtonTitle
                setOnClickListener {
                    feature?.notifyCustomLinkButtonClicked()
                }
            }
        }
    }

    override fun onButtonClick(tag: String?, id: String, sbisContainer: SbisContainerImpl) {
        sbisContainer.getViewModel().closeContainer()
    }

    private fun clearPinCodeInputView() {
        codeEditTextBinding?.pinCodeEditText?.text?.clear()
        passwordInputViewBinding?.pinCodePasswordInputView?.value = ""
    }

    private fun initPinCodeHeader() {
        val headerView = createHeader(
            requireContext(),
            HeaderTitleSettings.TextTitle(pinCodeViewModel.headerTitle),
            leftCustomContent = LeftCustomContent.Content {
                SbisTextView(it).apply {
                    setTextColor(StyleColor.PRIMARY.getIconColor(context))
                    typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                    text = pinCodeViewModel.headerIcon
                    textSize = IconSize.X3L.getDimen(context)
                    includeFontPadding = false

                    val offsetWithIcon = Offset.S.getDimenPx(context)
                    val offsetWithoutIcon = Offset.M.getDimenPx(context)
                    if (text.isNullOrEmpty()) {
                        setPadding(offsetWithoutIcon, 0, 0, 0)
                    } else {
                        setPadding(offsetWithIcon, 0, offsetWithIcon, 0)
                    }
                }
            },
            rightCustomContent = RightCustomContent.Content(contentIsResponsibleForEndPadding = true) {
                acceptButton = SbisRoundButton(it).apply {
                    size = SbisRoundButtonSize.S
                    type = SbisRoundButtonType.Filled
                    style = SuccessButtonStyle
                    icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_checked)
                    isEnabled = false
                    visibility = View.GONE
                    val offset = Offset.M.getDimenPx(context)
                    setPadding(offset, 0, offset, 0)
                    tag = acceptButtonTag
                }
                acceptButton?.setOnClickListener { pinCodeViewModel.confirmClickAction() }
                acceptButton!!
            }
        )

        binding.pinCodeLayout.addView(headerView, 0)
    }

    @SuppressLint("ResourceType")
    private fun initPinCodeInput() {
        codeType = when {
            pinCodeViewModel.isPhoneCode -> ACCESS_CODE_INPUT_VIEW
            pinCodeViewModel.isDefaultField -> DEFAULT_INPUT_VIEW
            else -> LIMITED_INPUT_VIEW
        }

        val margin = requireContext().resources.getDimensionPixelSize(
            if (codeType == LIMITED_INPUT_VIEW) { // marginHorizontal уменьшен до 44 из-за внутренних отсупов EditText
                R.dimen.pin_code_bubble_view_margin_horizontal
            } else {
                R.dimen.pin_code_input_view_margin_horizontal
            }
        )

        binding.layoutInputView.setHorizontalMargin(margin, margin)

        binding.layoutStub.viewStub?.apply {
            layoutResource = codeType.layoutRes
            setOnInflateListener { _, inflated ->
                setViewStubBinding(inflated)
            }

            inflate()
        }
    }

    private fun setViewStubBinding(inflated: View) {
        when (codeType) {
            LIMITED_INPUT_VIEW -> {
                codeEditTextBinding = PinCodeBubbleLimitedInputViewBinding.bind(inflated).also {
                    it.viewModel = pinCodeViewModel
                    it.lifecycleOwner = viewLifecycleOwner
                }
                codeEditTextBinding?.pinCodeEditText?.activateInput()
            }

            DEFAULT_INPUT_VIEW -> {
                passwordInputViewBinding = PinCodePasswordInputViewBinding.bind(inflated).also {
                    it.viewModel = pinCodeViewModel
                    it.lifecycleOwner = viewLifecycleOwner
                }
                setPasswordInputView()
            }

            ACCESS_CODE_INPUT_VIEW -> {
                accessCodeInputViewBinding = PinCodeAccessCodeInputViewBinding.bind(inflated).also {
                    it.viewModel = pinCodeViewModel
                    it.lifecycleOwner = viewLifecycleOwner

                    it.pinCodeAccessCodeInputView.title = ""
                    it.pinCodeAccessCodeInputView.gravity = Gravity.CENTER_HORIZONTAL
                    it.pinCodeAccessCodeInputView.maxLengthReachedListener = { code ->
                        val imm = accessCodeInputViewBinding?.pinCodeAccessCodeInputView?.context?.getSystemService(
                            Context.INPUT_METHOD_SERVICE
                        ) as InputMethodManager

                        imm.hideSoftInputFromWindow(
                            accessCodeInputViewBinding?.pinCodeAccessCodeInputView?.windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS
                        )

                        pinCodeViewModel.digits.value = code
                        pinCodeViewModel.codeInputConfirmAction()
                    }
                }
            }
        }

        pinCodeInput = codeEditTextBinding?.pinCodeEditText
            ?: passwordInputViewBinding?.pinCodePasswordInputView
            ?: accessCodeInputViewBinding?.pinCodeAccessCodeInputView
    }

    private fun setPasswordInputView() {
        passwordInputViewBinding?.pinCodePasswordInputView?.apply {
            inputType = if (pinCodeViewModel.isNumericKeyboard) {
                TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
            } else {
                TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
            }

            isSecureMode = pinCodeViewModel.isMaskedCode
        }
    }

    override fun onResume() {
        // при восстановлении активности после вытеснения ориентация останется портретной для смартфона
        setRequestOrientationSafe(SCREEN_ORIENTATION_PORTRAIT)
        super.onResume()
        showKeyboard()
    }

    override fun onPause() {
        super.onPause()
        setRequestOrientationSafe(pinCodeViewModel.initialScreenOrientation)
    }

    override fun onDestroyView() {
        hideKeyboard()
        closePinCodeFragment()
        super.onDestroyView()
        binding.unbind()
        dialogFragment = null
    }

    private fun closePinCodeFragment() {
        if (isUserClose && (!pinCodeViewModel.isCodeInputComplete || pinCodeViewModel.closeEvent.value == false)) {
            feature?.isConfirmationFlow = false
            feature?.cancelPinCodeEntering?.value = Unit
            feature?.notifyCancellation(pinCodeViewModel.onCancel)
        }
    }

    override fun onBackPressed() = false

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setRequestOrientationSafe(requestOrientation: Int) {
        if (isTablet) {
            return
        }

        // На Android 8 у прозрачных activity нельзя устанавливать orientation.
        try {
            requireActivity().requestedOrientation = requestOrientation
        } catch (e: IllegalStateException) {
            Timber.tag(pinCodeTag).e(e)
        }
    }

    /**
     * Ищет родительский [ViewModelStoreOwner] чтобы получить вью-модель фичи с корректным скоупом.
     */
    private fun findHostOwner(): ViewModelStoreOwner =
        requireParentFragment().run { parentFragment?.parentFragment ?: requireActivity() }

    private fun applyTheme(inflater: LayoutInflater) = inflater.cloneInContext(
        ThemeContextBuilder(
            requireContext(),
            R.attr.pin_code_theme
        ).build()
    )

    private fun View.setVisibility(isVisible: Boolean, isEnabled: Boolean) {
        this.visibility = if (isVisible && isEnabled) View.VISIBLE else View.GONE
    }

    /**
     * Отображать клавиатуру.
     */
    private fun showKeyboard() {
        pinCodeInput?.let {
            it.postDelayed({
                it.requestFocus()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    it.windowInsetsController?.show(WindowInsetsCompat.Type.ime())
                } else {
                    val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(it, SHOW_IMPLICIT)
                }
            }, delayMillis)
        }
    }

    // TODO Сделать рефакторинг фрагмента пин-кода, чтобы убрать все зависимости от контейнеров,
    //  в которые фрагмент пинкода помещают.
    //  https://online.sbis.ru/opendoc.html?guid=263cb57d-f74d-4a40-a718-35627d9b7b0e&client=3
    override fun onCloseContent() {
        hideKeyboard()
    }

    /**
     * Скрывать клавиатуру.
     */
    private fun hideKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(
                dialogFragment?.dialog?.window ?: requireActivity().window,
                requireView()
            ).hide(WindowInsetsCompat.Type.ime())
        } else {
            KeyboardUtils.hideKeyboard(requireView())
        }
    }

    private fun showToastWithError(error: String?) {
        if (!error.isNullOrBlank()) {
            SbisPopupNotification.pushToast(requireContext(), error)
        }
        showKeyboard() // восстанавливаем фокус т.к. поле ввода скрывалось
    }

    /** Отобразить ошибку в диалоговом окне. */
    private fun showDialogFragmentWithError(error: String?) {
        if (error.isNullOrBlank()) return

        val dialog = ConfirmationDialog(
            contentProvider = BaseContentProvider(
                resources.getString(R.string.pin_code_error_title),
                error
            ),
            buttons = {
                listOf(
                    ButtonModel(
                        ConfirmationButtonId.CANCEL,
                        R.string.pin_code_error_confirmation_button,
                        DangerButtonStyle
                    )
                )
            },
            style = ConfirmationDialogStyle.ERROR,
            buttonOrientation = ConfirmationButtonOrientation.HORIZONTAL,
            showMarker = true
        )

        dialog.setOnDismissListener {
            pinCodeViewModel.closeOnError()
        }

        dialog.show(childFragmentManager)
    }

    private fun showPeriodPicker(currentPeriod: PinCodePeriod) {
        val menu = createPeriodPickerMenu(pinCodeViewModel, resources, currentPeriod)
        passwordInputViewBinding?.pinCodePasswordInputView?.clearFocus()
        menu.addCloseListener { showKeyboard() }
        menu.showMenuWithLocators(
            parentFragmentManager,
            verticalLocator = ScreenVerticalLocator(VerticalAlignment.CENTER),
            horizontalLocator = ScreenHorizontalLocator(HorizontalAlignment.CENTER)
        )
    }

    private fun onPeriodChanged(period: PinCodePeriod?) {
        feature?.notifyPeriodChanged(period)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        binding.root.updatePadding(bottom = keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        binding.root.updatePadding(bottom = 0)
        return true
    }

    @Parcelize
    private class Creator(
        private val headerIcon: String,
        private val header: String,
        private val comment: String,
        private val codeLength: Int,
        private val confirmationType: ConfirmationType,
        private val isDefaultField: Boolean,
        private val isMaskedCode: Boolean,
        private val isPhoneCode: Boolean,
        private val isNumericKeyboard: Boolean,
        private val inputHint: String,
        private val closeBtnVisible: Boolean,
        private val transportType: PinCodeTransportType,
        private val initialScreenOrientation: Int,
        private val hasPeriod: Boolean,
        private val customLinkButtonTitle: String,
        private val onCancel: (() -> Unit)?,
        private val onResult: ((PinCodeSuccessResult<*>) -> Unit)?
    ) : ContentCreatorParcelable {

        private lateinit var fragment: PinCodeFragment

        override fun createFragment(): Fragment {
            fragment = PinCodeFragment().apply {
                arguments = bundleOf(
                    ARG_HEADER to header,
                    ARG_DESCRIPTION to comment,
                    ARG_CODE_LENGTH to codeLength,
                    ARG_IS_DEFAULT_FIELD to isDefaultField,
                    ARG_CONFIRMATION_TYPE to confirmationType,
                    ARG_IS_MASKED_CODE to isMaskedCode,
                    ARG_IS_PHONE_CODE to isPhoneCode,
                    ARG_INPUT_HINT to inputHint,
                    ARG_CLOSE_BTN_VISIBLE to closeBtnVisible,
                    ARG_HEADER_ICON to headerIcon,
                    ARG_TRANSPORT_TYPE to transportType,
                    ARG_IS_NUMERIC_KEYBOARD to isNumericKeyboard,
                    ARG_INITIAL_SCREEN_ORIENTATION to initialScreenOrientation,
                    ARG_HAS_PERIOD to hasPeriod,
                    ARG_CUSTOM_LINK_BUTTON_TITLE to customLinkButtonTitle,
                    ARG_ON_CANCEL to onCancel,
                    ARG_ON_RESULT to onResult
                )
            }
            return fragment
        }

        fun getFragment(): PinCodeFragment = fragment
    }

    @Parcelize
    private class ParcelableFragmentContentCreator(
        private val headerIcon: String,
        private val header: String,
        private val comment: String,
        private val codeLength: Int,
        private val confirmationType: ConfirmationType,
        private val isDefaultField: Boolean,
        private val isMaskedCode: Boolean,
        private val isPhoneCode: Boolean,
        private val isNumericKeyboard: Boolean,
        private val inputHint: String,
        private val closeBtnVisible: Boolean,
        private val transportType: PinCodeTransportType,
        private val initialScreenOrientation: Int,
        private val hasPeriod: Boolean,
        private val customLinkButtonTitle: String,
        private val onCancel: (() -> Unit)?,
        private val onResult: ((PinCodeSuccessResult<*>) -> Unit)?
    ) :
        ContentCreator<FragmentContent>, Parcelable {
        override fun createContent(): FragmentContent {
            return object : FragmentContent {

                private lateinit var fragment: PinCodeFragment

                override fun getFragment(containerFragment: SbisContainerImpl): Fragment {
                    fragment = PinCodeFragment().apply {
                        arguments = bundleOf(
                            ARG_HEADER to header,
                            ARG_DESCRIPTION to comment,
                            ARG_CODE_LENGTH to codeLength,
                            ARG_IS_DEFAULT_FIELD to isDefaultField,
                            ARG_CONFIRMATION_TYPE to confirmationType,
                            ARG_IS_MASKED_CODE to isMaskedCode,
                            ARG_IS_PHONE_CODE to isPhoneCode,
                            ARG_INPUT_HINT to inputHint,
                            ARG_CLOSE_BTN_VISIBLE to closeBtnVisible,
                            ARG_HEADER_ICON to headerIcon,
                            ARG_TRANSPORT_TYPE to transportType,
                            ARG_IS_NUMERIC_KEYBOARD to isNumericKeyboard,
                            ARG_INITIAL_SCREEN_ORIENTATION to initialScreenOrientation,
                            ARG_HAS_PERIOD to hasPeriod,
                            ARG_CUSTOM_LINK_BUTTON_TITLE to customLinkButtonTitle,
                            ARG_ON_CANCEL to onCancel,
                            ARG_ON_RESULT to onResult
                        )
                    }

                    initContent(containerFragment)

                    return fragment
                }

                override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

                override fun customWidth(): Int = R.dimen.pin_code_tablet_container_width

                override fun useDefaultHorizontalOffset() = false

                private fun initContent(containerFragment: SbisContainerImpl) {
                    val containerViewModel = containerFragment.getViewModel()
                    containerFragment.viewLifecycleOwner.lifecycleScope.launch {
                        containerFragment.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            containerViewModel.onCancelContainer.collect {
                                isUserClose = true
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private var pinCodeTag = "PIN_CODE_INPUT_FRAGMENT"
        private const val acceptButtonTag = "PIN_CODE_ACCEPT_BUTTON"

        /**
         * Является ли закрытие окна пинкода сделанным пользователем закрытием
         * путем нажатия на область вне контейнера или свайпам вниз(для шторки).
         */
        internal var isUserClose = false

        /**
         * Время задержки отображения клавиатуры (эмпирически полученная величина).
         * Задержка нужна для того, чтобы клавиатура получила фокус текущего фрагмента,
         * в противном случае, она не отобразится либо отобразится под фрагментом.
         */
        internal const val delayMillis = 400L

        private var dialogFragment: DialogFragment? = null

        /**
         * Отобразить компонент с заданной конфигурацией.
         * @param activity [AppCompatActivity] в котором будет отображаться фрагмент с вводом пин-кода.
         * @param fragmentManager Фрагмент менеджер для размещения фрагмент с вводом пин-кода.
         * @param config конфигурация для отображения компонента ввода пин-кода.
         * @param popoverAnchor якорь [PinCodeAnchor] вью к которому должна быть прикреплена форма ввода пин-кода.
         * Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить
         * по центру.
         */
        internal fun create(
            activity: FragmentActivity,
            fragmentManager: FragmentManager,
            config: PinCodeConfiguration,
            popoverAnchor: PinCodeAnchor? = null,
            onCancel: (() -> Unit)? = null,
            onResult: ((PinCodeSuccessResult<*>) -> Unit)? = null
        ) = config.run {
            val isTablet = DeviceConfigurationUtils.isTablet(activity)
            isUserClose = false

            if (isTablet) pinCodeTag = CONTAINER_DEFAULT_TAG
            dismiss(fragmentManager)

            if (isTablet) {
                val container = createParcelableFragmentContainer(
                    ParcelableFragmentContentCreator(
                        header = prepareStr(activity, header),
                        comment = if (description.isEmpty() && descriptionRes != ID_NULL) {
                            prepareStr(activity, descriptionRes)
                        } else {
                            description
                        },
                        codeLength = codeLength,
                        confirmationType = confirmationType,
                        isDefaultField = isDefaultField,
                        isMaskedCode = isMaskedCode,
                        isPhoneCode = isPhoneCode,
                        inputHint = prepareStr(activity, inputHint),
                        closeBtnVisible = false,
                        headerIcon = headerIcon?.character?.toString() ?: "",
                        transportType = transportType,
                        isNumericKeyboard = isNumericKeyboard,
                        initialScreenOrientation = activity.requestedOrientation,
                        hasPeriod = hasPeriod,
                        customLinkButtonTitle = prepareStr(activity, customLinkButtonTitle),
                        onCancel = onCancel,
                        onResult = onResult
                    )
                )

                container.dimType = DimType.SOLID
                container.isCloseOnTouchOutside = true

                container.show(
                    fragmentManager,
                    createHorizontalLocator(popoverAnchor),
                    createVerticalLocator(popoverAnchor)
                )

                dialogFragment = container as DialogFragment
            } else {
                val creator = Creator(
                    header = prepareStr(activity, header),
                    comment = if (description.isEmpty() && descriptionRes != ID_NULL) {
                        prepareStr(activity, descriptionRes)
                    } else {
                        description
                    },
                    codeLength = codeLength,
                    confirmationType = confirmationType,
                    isDefaultField = isDefaultField,
                    isMaskedCode = isMaskedCode,
                    isPhoneCode = isPhoneCode,
                    inputHint = prepareStr(activity, inputHint),
                    closeBtnVisible = true,
                    headerIcon = headerIcon?.character?.toString() ?: "",
                    transportType = transportType,
                    isNumericKeyboard = isNumericKeyboard,
                    initialScreenOrientation = activity.requestedOrientation,
                    hasPeriod = hasPeriod,
                    customLinkButtonTitle = prepareStr(activity, customLinkButtonTitle),
                    onCancel = onCancel,
                    onResult = onResult
                )

                dialogFragment =
                    ContainerMovableDialogFragment.Builder()
                        .instant(true)
                        .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                        .setAutoCloseable(true)
                        .setContentCreator(creator)
                        .setContainerBackgroundColor(BackgroundColor.DEFAULT.getValue(activity))
                        .setCustomClosePanelAction(CustomClosePanelAction { isUserClose = true })
                        .setIgnoreLock(true)
                        .hideKeyboardOnStart(false)
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        .build()

                fragmentManager.beginTransaction()
                    .add(dialogFragment as ContainerMovableDialogFragment, pinCodeTag)
                    .commit()
            }
        }

        /**
         * Закрыть фрагмент шторки.
         */
        internal fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(pinCodeTag) as? DialogFragment)?.dismiss()
        }

        /**
         * Проверить, фрагмент шторки отображается или нет.
         */
        internal fun isShown(fragmentManager: FragmentManager): Boolean {
            return (fragmentManager.findFragmentByTag(pinCodeTag) as? DialogFragment)?.isResumed ?: false
        }

        private fun prepareStr(context: Context, @StringRes res: Int) =
            if (res != ID_NULL) {
                context.getString(res)
            } else {
                ""
            }

        private fun createVerticalLocator(anchor: PinCodeAnchor?): VerticalLocator {
            return if (anchor != null) {
                TagAnchorVerticalLocator(
                    anchorLocator = AnchorVerticalLocator(anchor.verticalAlignment.toContainerVerticalAlignment()),
                    anchorTag = anchor.viewTag
                )
            } else {
                ScreenVerticalLocator()
            }
        }

        private fun createHorizontalLocator(anchor: PinCodeAnchor?): HorizontalLocator {
            return if (anchor != null) {
                TagAnchorHorizontalLocator(
                    anchorLocator = AnchorHorizontalLocator(
                        anchor.horizontalAlignment.toContainerHorizontalAlignment()
                    ),
                    anchorTag = anchor.viewTag
                )
            } else {
                ScreenHorizontalLocator()
            }
        }

        private fun DesignVerticalAlignment?.toContainerVerticalAlignment() = when (this) {
            DesignVerticalAlignment.TOP -> VerticalAlignment.TOP
            DesignVerticalAlignment.BOTTOM -> VerticalAlignment.BOTTOM
            else -> VerticalAlignment.CENTER
        }

        private fun DesignHorizontalAlignment?.toContainerHorizontalAlignment() = when (this) {
            DesignHorizontalAlignment.LEFT -> HorizontalAlignment.LEFT
            DesignHorizontalAlignment.RIGHT -> HorizontalAlignment.RIGHT
            else -> HorizontalAlignment.CENTER
        }
    }
}

internal const val ARG_HEADER = "HEADER"
internal const val ARG_HEADER_ICON = "HEADER_ICON"
internal const val ARG_DESCRIPTION = "DESCRIPTION"
internal const val ARG_CODE_LENGTH = "CODE_LENGTH"
internal const val ARG_IS_NUMERIC_KEYBOARD = "IS_NUMERIC_KEYBOARD"
internal const val ARG_CONFIRMATION_TYPE = "CONFIRMATION_TYPE"
internal const val ARG_IS_DEFAULT_FIELD = "IS_DEFAULT_FIELD"
internal const val ARG_IS_MASKED_CODE = "IS_MASKED_CODE"
internal const val ARG_IS_PHONE_CODE = "IS_PHONE_CODE"
internal const val ARG_INPUT_HINT = "INPUT_HINT"
internal const val ARG_CLOSE_BTN_VISIBLE = "CLOSE_BTN_VISIBLE"
internal const val ARG_THEME = "THEME"
internal const val ARG_TRANSPORT_TYPE = "TRANSPORT_TYPE"
internal const val ARG_INITIAL_SCREEN_ORIENTATION = "INITIAL_SCREEN_ORIENTATION"
internal const val ARG_HAS_PERIOD = "HAS_PERIOD"
internal const val ARG_ON_CANCEL = "ON_CANCEL"
internal const val ARG_ON_RESULT = "ON_RESULT"
internal const val ARG_CUSTOM_LINK_BUTTON_TITLE = "CUSTOM_LINK_BUTTON_TITLE"
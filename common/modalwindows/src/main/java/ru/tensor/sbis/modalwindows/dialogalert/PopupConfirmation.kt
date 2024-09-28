package ru.tensor.sbis.modalwindows.dialogalert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.text.TextInputView
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.common.R as RCommon

const val DIALOG_REQUEST_CODE_ARG = "DIALOG_REQUEST_CODE_ARG"
const val DIALOG_CONTENT_TYPE_ARG = "DIALOG_CONTENT_TYPE_ARG"
const val DIALOG_MESSAGE_ARG = "DIALOG_MESSAGE_ARG"
const val DIALOG_EDIT_TEXT_INITIAL_TEXT_ARG = "DIALOG_EDIT_TEXT_INITIAL_TEXT_ARG"
const val DIALOG_EDIT_TEXT_HINT_ARG = "DIALOG_EDIT_TEXT_HINT_ARG"
const val DIALOG_EDIT_TEXT_INPUT_TYPE_ARG = "DIALOG_EDIT_TEXT_INPUT_TYPE_ARG"
const val DIALOG_EDIT_TEXT_INPUT_MAX_LENGTH_ARG = "DIALOG_EDIT_TEXT_INPUT_MAX_LENGTH_ARG"
const val DIALOG_EDIT_TEXT_MUST_CHANGE_INITIAL_TEXT_ARG = "DIALOG_EDIT_TEXT_MUST_CHANGE_INITIAL_TEXT_ARG"
const val DIALOG_EDIT_TEXT_CAN_NOT_BE_BLANK_ARG = "DIALOG_EDIT_TEXT_CAN_NOT_BE_BLANK_ARG"
const val DIALOG_EDIT_TEXT_IS_CLEAR_VISIBLE = "DIALOG_EDIT_TEXT_IS_CLEAR_VISIBLE"
const val DIALOG_EDIT_TEXT_FILTERS = "DIALOG_EDIT_TEXT_FILTERS"
const val DIALOG_EDIT_TEXT_CUSTOM_THEME_RES = "DIALOG_EDIT_TEXT_CUSTOM_THEME_RES"
const val DIALOG_LIST_ARG = "DIALOG_LIST_ARG"
const val DIALOG_BUTTON_LIST = "DIALOG_BUTTON_LIST"
const val DIALOG_SHOWING_PROGRESS_BAR_ON_INPUT = "DIALOG_SHOWING_PROGRESS_BAR_ON_INPUT"
const val DIALOG_CURRENT_TEXT = "DIALOG_CURRENT_TEXT"

/**
 * Диалог подтверждения
 */
open class PopupConfirmation : BaseAlertDialogFragment(), DialogItemClickListener {

    @Suppress("MemberVisibilityCanBePrivate")
    var contentType = AlertContentType.NONE
    var requestCode: Int = REQUEST_CODE_DEFAULT_VALUE
    private var savedText: String? = null

    private var textInput: TextInputView? = null

    /**@SelfDocumented*/
    interface DialogDismissListener {

        /**@SelfDocumented*/
        fun onDismiss(requestCode: Int) = Unit
    }

    /**@SelfDocumented*/
    interface DialogCancelListener {

        /**@SelfDocumented*/
        fun onCancel(requestCode: Int) = Unit
    }

    /**
     * Обработчик событий нажатия на кнопку подтверждения или отмены
     */
    interface DialogYesNoWithTextListener {

        /**
         * Вызывается при нажатии кнопки подтверждения
         *
         * @param text текст, введённый пользователем (для диалогов типа [AlertContentType.EDIT_TEXT])
         */
        fun onYes(requestCode: Int, text: String?)

        /**
         * Вызывается при нажатии кнопки отмены.
         * Реализация не обязательна, поскольку часто не требуется
         *
         * @param text текст, введённый пользователем (для диалогов типа [AlertContentType.EDIT_TEXT])
         */
        fun onNo(requestCode: Int, text: String?) = Unit
    }

    /**@SelfDocumented*/
    interface DialogItemClickListener {

        //ignore by default
        fun onItemClicked(requestCode: Int, position: Int) = Unit

        //ignore by default
        fun onItemClicked(requestCode: Int, itemValue: String?) = Unit

        //ignore by default
        fun onItemClicked(requestCode: Int, button: Button) = Unit

        fun onYes(requestCode: Int) = Unit
    }

    /**
     * ViewModel кнопки диалога.
     *
     * @param text текст кнопки.
     * @param actionId идентификатор действия кнопки.
     * @param style стиль кнопки.
     *
     * @author aa.mezencev
     */
    @Parcelize
    data class Button(
        val text: String,
        val actionId: String? = null,
        @StyleRes
        val style: Int? = null,
        @IdRes
        val buttonId: Int? = null
    ) : Parcelable

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        requestCode = arguments?.getInt(
            DIALOG_REQUEST_CODE_ARG,
            REQUEST_CODE_DEFAULT_VALUE
        )
            ?: REQUEST_CODE_DEFAULT_VALUE
        contentType = arguments?.getSerializable(DIALOG_CONTENT_TYPE_ARG) as? AlertContentType
            ?: AlertContentType.NONE
        savedText = savedInstanceState?.getString(DIALOG_CURRENT_TEXT)
        val dialog = super.onCreateDialog(savedInstanceState)
        if (savedInstanceState?.getBoolean(DIALOG_SHOWING_PROGRESS_BAR_ON_INPUT) == true) {
            showInputProgress()
        }
        return if (contentType == AlertContentType.EDIT_TEXT) {
            dialog.apply {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
        } else {
            dialog
        }
    }

    override fun addContent(container: View) {
        when (contentType) {
            AlertContentType.NONE -> {
            }
            AlertContentType.MESSAGE -> {
                AlertDialogCreator.addMessage(
                    requireActivity(),
                    container,
                    hasTitle,
                    requireArguments().getCharSequence(DIALOG_MESSAGE_ARG)!!
                )
            }
            AlertContentType.EDIT_TEXT -> {
                val arguments = requireArguments()
                val initialEditTextContent = arguments.getString(DIALOG_EDIT_TEXT_INITIAL_TEXT_ARG)
                val editTextHint = arguments.getString(DIALOG_EDIT_TEXT_HINT_ARG)
                val editTextInputType = arguments.getInt(DIALOG_EDIT_TEXT_INPUT_TYPE_ARG, InputType.TYPE_CLASS_TEXT)
                val message = arguments.getCharSequence(DIALOG_MESSAGE_ARG)
                val maxLength = arguments.getInt(DIALOG_EDIT_TEXT_INPUT_MAX_LENGTH_ARG)
                val mustChangeInitialText = arguments.getBoolean(DIALOG_EDIT_TEXT_MUST_CHANGE_INITIAL_TEXT_ARG)
                val isInputTextCanNotBeBlank = arguments.getBoolean(DIALOG_EDIT_TEXT_CAN_NOT_BE_BLANK_ARG)
                val isClearVisible = arguments.getBoolean(DIALOG_EDIT_TEXT_IS_CLEAR_VISIBLE)
                val editTextCustomTheme = arguments.getInt(DIALOG_EDIT_TEXT_CUSTOM_THEME_RES, 0)
                val needInputTextRestrictions = mustChangeInitialText || isInputTextCanNotBeBlank

                @Suppress("UNCHECKED_CAST") val filters =
                    (arguments.getSerializable(DIALOG_EDIT_TEXT_FILTERS) as ArrayList<InputFilter>?)?.toMutableList()
                        ?: ArrayList()
                if (maxLength > 0) {
                    filters.add(InputFilter.LengthFilter(maxLength))
                }
                val startInputText = savedText ?: initialEditTextContent
                AlertDialogCreator.addEditText(
                    requireActivity(),
                    container,
                    hasTitle,
                    startInputText,
                    editTextHint,
                    editTextInputType,
                    message,
                    filters,
                    editTextCustomTheme,
                    canBeBlank = !isInputTextCanNotBeBlank,
                    isClearVisible = isClearVisible
                )
                textInput = contentView.findViewById(R.id.modalwindows_alert_text_input_view)
                if (needInputTextRestrictions) {
                    addInputTextRestrictions(
                        initialText = initialEditTextContent,
                        mustBeChange = mustChangeInitialText,
                        canNotBeBlank = isInputTextCanNotBeBlank
                    )
                }
            }
            AlertContentType.LIST -> {
                val items = arguments?.getCharSequenceArrayList(DIALOG_LIST_ARG)
                AlertDialogCreator.addList(
                    requireActivity(),
                    container,
                    hasTitle,
                    items!!,
                    this
                )
            }
            AlertContentType.BUTTONS_LIST -> {
                val items = arguments?.getParcelableArrayList<Button>(DIALOG_BUTTON_LIST)
                val message = arguments?.getString(DIALOG_MESSAGE_ARG)

                AlertDialogCreator.addButtonsList(
                    requireActivity(),
                    container,
                    hasTitle,
                    items!!,
                    message,
                    this
                )
            }
        }
    }

    /**
     * Добавление ограничений на вводимый текст для блокировки кнопки подтверждения
     *
     * @param initialText   изначальный текст в поле ввода при показе диалога (опционально)
     * @param mustBeChange  true, если вводимый текст должен быть отличным от первоначального
     * @param canNotBeBlank true, если вводимый текст не должен быть пустым или содержать только пробелы
     */
    private fun addInputTextRestrictions(
        initialText: String?,
        mustBeChange: Boolean,
        canNotBeBlank: Boolean
    ) {
        checkNotNullSafe(textInput)
        val positiveButton = contentView.findViewById<View>(R.id.modalwindows_positive_button)

        if (mustBeChange) {
            positiveButton.isEnabled = textInput?.value != initialText
        }
        textInput?.onValueChanged = { _, value ->
            positiveButton.isEnabled = !(mustBeChange && initialText == value) && !(canNotBeBlank && value.isBlank())
        }
    }

    @Suppress("DEPRECATION")
    override fun hasListener(): Boolean {
        return when (contentType) {
            AlertContentType.EDIT_TEXT -> {
                getListener<DialogYesNoWithTextListener>() != null
            }
            AlertContentType.LIST -> {
                getListener<DialogItemClickListener>() != null
            }
            AlertContentType.BUTTONS_LIST -> {
                getListener<DialogItemClickListener>() != null
            }
            AlertContentType.MESSAGE, AlertContentType.NONE -> {
                getListener<DialogYesNoWithTextListener>() != null
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onPositiveButtonClick() {
        if (processEvents) {
            val text = if (contentType == AlertContentType.EDIT_TEXT) {
                getInputTextAndHideKeyboard()
            } else {
                null
            }
            when (contentType) {
                AlertContentType.LIST -> getListener<DialogItemClickListener>()?.onYes(requestCode)
                else -> {
                    getListener<DialogYesNoWithTextListener>()?.onYes(requestCode, text)
                }
            }
        }
        dismissAllowingStateLoss()
    }

    override fun onNegativeButtonClick() {
        if (processEvents) {
            val text = if (contentType == AlertContentType.EDIT_TEXT) {
                getInputTextAndHideKeyboard()
            } else {
                null
            }
            getListener<DialogYesNoWithTextListener>()?.onNo(requestCode, text)
        }
        dismissAllowingStateLoss()
    }

    override fun onItemClicked(position: Int) {
        if (processEvents) {
            getListener<DialogItemClickListener>()?.onItemClicked(
                requestCode,
                position
            )

            when (contentType) {
                AlertContentType.LIST -> {
                    getListener<DialogItemClickListener>()?.onItemClicked(
                        requestCode,
                        arguments?.getCharSequenceArrayList(DIALOG_LIST_ARG)?.get(position)?.toString()
                    )
                }
                AlertContentType.BUTTONS_LIST -> {
                    val button = arguments?.getParcelableArrayList<Button>(DIALOG_BUTTON_LIST)?.get(position)

                    button.let { getListener<DialogItemClickListener>()?.onItemClicked(requestCode, it?.text) }
                    button?.let { getListener<DialogItemClickListener>()?.onItemClicked(requestCode, it) }
                }
                AlertContentType.NONE,
                AlertContentType.MESSAGE,
                AlertContentType.EDIT_TEXT -> Unit
            }
        }
        dismissAllowingStateLoss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (contentType == AlertContentType.EDIT_TEXT) {
            checkNotNullSafe(textInput)
            outState.putBoolean(DIALOG_SHOWING_PROGRESS_BAR_ON_INPUT, textInput?.isProgressVisible ?: false)
            outState.putString(DIALOG_CURRENT_TEXT, textInput?.value?.toString())
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        getListener<DialogDismissListener>()?.onDismiss(requestCode)
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        getListener<DialogCancelListener>()?.onCancel(requestCode)
        super.onCancel(dialog)
    }

    /**
     * Показать ошибку под полем ввода
     * @param errorText текст ошибки для отображения
     */
    @Suppress("unused")
    fun showInputError(errorText: String?) {
        if (contentType == AlertContentType.EDIT_TEXT) {
            val errorMessage = if (errorText.isNullOrEmpty()) {
                getString(RCommon.string.operation_error_message)
            } else {
                errorText
            }
            textInput?.validationStatus = ValidationStatus.Error(errorMessage)
        }
    }

    /**
     * Скрыть ошибку поля ввода
     */
    @Suppress("unused")
    fun hideInputError() {
        if (contentType == AlertContentType.EDIT_TEXT) {
            textInput?.validationStatus = ValidationStatus.Default("")
        }
    }

    /**
     * Показать прогресс с блокировкой поля ввода. Полезно во время длительной операции
     */
    fun showInputProgress() {
        setProgressBarVisible(true)
    }

    /**
     * Скрыть прогрес рядом с полем ввода
     */
    @Suppress("unused")
    fun hideInputProgress() {
        setProgressBarVisible(false)
    }

    private fun setProgressBarVisible(progressVisible: Boolean) {
        if (contentType == AlertContentType.EDIT_TEXT) {
            textInput?.isProgressVisible = progressVisible
            textInput?.isEnabled = !progressVisible
        }
    }

    protected fun getInputTextAndHideKeyboard(): String? {
        checkNotNullSafe(textInput)
        KeyboardUtils.hideKeyboard(contentView)
        return textInput?.value?.toString()
    }

    companion object {

        private const val REQUEST_CODE_DEFAULT_VALUE = -1

        /**
         * Создание диалога без контента. Кнопки, заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestPositiveButton], [BaseAlertDialogFragment.requestNegativeButton],
         * [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         */
        @JvmStatic
        fun newSimpleInstance(requestCode: Int): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.NONE)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                }
            }
        }

        /**
         * Вывод текстового сообщения в качетве контента. Кнопки, заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestPositiveButton], [BaseAlertDialogFragment.requestNegativeButton],
         * [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         * @param message отображаемый текст
         */
        @JvmStatic
        fun newMessageInstance(
            requestCode: Int,
            message: CharSequence
        ): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.MESSAGE)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                    putCharSequence(DIALOG_MESSAGE_ARG, message)
                }
            }
        }

        /**
         * Вывод одиночного поля ввода в качестве контента. Кнопки, заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestPositiveButton], [BaseAlertDialogFragment.requestNegativeButton],
         * [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         * @param initialText текст, выводимый в поле ввода при показе диалога
         * @param hint текст подсказки
         * @param maxLength Int максимальное количество символов для поля ввода
         * @param mustChangeInitialText boolean блокировка позитивной кнопки, если не изменился начальный текст
         * @param canNotBeBlank  boolean блокировка позитивной кнопки, если текст пустой или содержит толькой пробелы
         * @param filters список [SerializableInputFilter] которые применяются на поле ввода
         * @param customEditTextTheme @Deprecated.
         * @param isClearVisible true если должна быть кнопка очистки, иначе false.
         * Будет переделан по [задаче](https://online.sbis.ru/opendoc.html?guid=19996b18-124f-4855-aaea-51bdc0f4fc58&client=3).
         * Кастомная тема для поля ввода внутри диалога.
         */
        @JvmStatic
        fun newEditTextInstance(
            requestCode: Int,
            initialText: String?,
            hint: String?,
            inputType: Int = InputType.TYPE_CLASS_TEXT,
            message: CharSequence? = null,
            maxLength: Int = 0,
            mustChangeInitialText: Boolean = false,
            canNotBeBlank: Boolean = false,
            filters: ArrayList<SerializableInputFilter>? = null,
            @StyleRes customEditTextTheme: Int = 0,
            isClearVisible: Boolean = false
        ): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.EDIT_TEXT)
                    putString(DIALOG_EDIT_TEXT_INITIAL_TEXT_ARG, initialText)
                    putString(DIALOG_EDIT_TEXT_HINT_ARG, hint)
                    putInt(DIALOG_EDIT_TEXT_INPUT_TYPE_ARG, inputType)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                    putInt(DIALOG_EDIT_TEXT_INPUT_MAX_LENGTH_ARG, maxLength)
                    putBoolean(DIALOG_EDIT_TEXT_MUST_CHANGE_INITIAL_TEXT_ARG, mustChangeInitialText)
                    putBoolean(DIALOG_EDIT_TEXT_CAN_NOT_BE_BLANK_ARG, canNotBeBlank)
                    putBoolean(DIALOG_EDIT_TEXT_IS_CLEAR_VISIBLE, isClearVisible)
                    putSerializable(DIALOG_EDIT_TEXT_CUSTOM_THEME_RES, customEditTextTheme)
                    if (message != null) {
                        putCharSequence(DIALOG_MESSAGE_ARG, message)
                    }
                    if (filters != null) {
                        putSerializable(DIALOG_EDIT_TEXT_FILTERS, filters)
                    }
                }
            }
        }

        /**
         * Вывод простого списка в качестве контента. Кнопки, заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestPositiveButton], [BaseAlertDialogFragment.requestNegativeButton],
         * [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         * @param content список строковых значений
         */
        @JvmStatic
        fun newListInstance(
            requestCode: Int,
            content: List<CharSequence>
        ): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.LIST)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                    putCharSequenceArrayList(DIALOG_LIST_ARG, ArrayList(content))
                }
            }
        }

        /**
         * Вывод до 5 кнопок в диалоге. Заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         * @param content список строковых значений
         * @param buttonsStyle позиция удаляющей кнопки
         */
        @JvmStatic
        fun newButtonsListInstance(
            requestCode: Int,
            content: List<String>,
            message: String? = null,
            buttonsStyle: HashMap<String, Int>? = null,
            buttonsId: Map<String, Int>? = null
        ): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.BUTTONS_LIST)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                    putParcelableArrayList(
                        DIALOG_BUTTON_LIST,
                        content.mapTo(ArrayList()) {
                            Button(
                                text = it,
                                style = buttonsStyle?.get(it),
                                buttonId = buttonsId?.get(it)
                            )
                        }
                    )
                    if (message != null) {
                        putCharSequence(DIALOG_MESSAGE_ARG, message)
                    }
                }
            }
        }

        /**
         * Вывод до 5 кнопок в диалоге. Заголовок и количество строк в заголовке могут быть
         * добавлены через [BaseAlertDialogFragment.requestTitle] и [BaseAlertDialogFragment.requestTitleMaxLines]
         *
         * @param requestCode код, используемый при совместном использовании нескольких инстансов
         * диалога в одном окне для обработки результата
         * @param message сообщение диалога.
         * @param buttons список кнопок.
         */
        @JvmStatic
        fun newButtonsListInstance(
            message: String? = null,
            requestCode: Int,
            buttons: List<Button>
        ): PopupConfirmation {
            return PopupConfirmation().apply {
                getOrCreateArguments().apply {
                    putSerializable(DIALOG_CONTENT_TYPE_ARG, AlertContentType.BUTTONS_LIST)
                    putInt(DIALOG_REQUEST_CODE_ARG, requestCode)
                    putParcelableArrayList(DIALOG_BUTTON_LIST, ArrayList(buttons))
                    if (message != null) {
                        putCharSequence(DIALOG_MESSAGE_ARG, message)
                    }
                }
            }
        }
    }
}
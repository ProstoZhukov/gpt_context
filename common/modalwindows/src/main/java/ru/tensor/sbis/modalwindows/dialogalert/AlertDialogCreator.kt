package ru.tensor.sbis.modalwindows.dialogalert

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.view.input.text.TextInputView
import ru.tensor.sbis.modalwindows.R

/**
 * Класс для создания стандартных диалогов различных типов
 */
@Suppress("unused")
object AlertDialogCreator {

    /**
     * Константы отступов для разных макетов из спецификации.
     */
    private const val DIALOG_MESSAGE_MARGIN_TOP = 8F
    private const val DIALOG_EDITTEXT_MARGIN_TOP = 12F
    private const val DIALOG_LIST_MARGIN_TOP = 16F
    private const val DIALOG_CONTENT_WITHOUT_TITLE_TOP_MARGIN = 18F
    private const val DIALOG_BUTTONS_LIST_MAX_SIZE = 5
    private const val HORIZONTAL_DIALOG_BUTTONS_LIST_MAX_SIZE = 2

    const val TITLE_MAX_LINES_NOT_DEFINED = -1

    //region  message
    fun createMessageDialog(
        activity: Activity,
        title: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null,
        message: CharSequence,
        titleMaxLines: Int = TITLE_MAX_LINES_NOT_DEFINED
    ): AlertDialog {
        val container = createContainer(
            activity,
            title != null,
            positiveButtonText,
            negativeButtonText,
            positiveButtonForRemoval,
            onPositiveButtonClick,
            onNegativeButtonClick
        )
        addMessage(
            activity,
            container,
            title != null,
            message
        )
        val builder = AlertDialog.Builder(activity)
        title?.let { addTitle(container, it, titleMaxLines) }
        builder.setView(container)
        return builder.create()
    }

    fun addMessage(
        activity: Activity,
        root: View,
        hasTitle: Boolean,
        message: CharSequence
    ) {
        val container = root.findViewById<FrameLayout>(R.id.modalwindows_alert_content_container)
        activity.layoutInflater.let {
            it.inflate(
                R.layout.modalwindows_dialog_alert_message_layout,
                container,
                true
            )
            initMessageView(activity, hasTitle, container, message)
        }
    }
    //end region

    //region editText
    /**
     * Создание диалога с полем ввода. Клавиатура показывается автоматически
     */
    fun createEditTextDialog(
        activity: Activity,
        title: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null,
        initialText: String? = null,
        hint: String? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        message: CharSequence? = null,
        titleMaxLines: Int = TITLE_MAX_LINES_NOT_DEFINED,
        isClearVisible: Boolean = false
    ): AlertDialog {

        val root = createContainer(
            activity,
            title != null,
            positiveButtonText,
            negativeButtonText,
            positiveButtonForRemoval,
            onPositiveButtonClick,
            onNegativeButtonClick
        )
        addEditText(
            activity,
            root,
            title != null,
            initialText,
            hint,
            inputType,
            message,
            isClearVisible = isClearVisible
        )
        val builder = AlertDialog.Builder(activity)
        title?.let { addTitle(root, it, titleMaxLines) }
        builder.setView(root)
        return builder.create().apply {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    /**@SelfDocumented*/
    fun addEditText(
        activity: Activity,
        root: View,
        hasTitle: Boolean,
        initialEditTextContent: String?,
        hint: String?,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        message: CharSequence?,
        filters: List<InputFilter>? = null,
        @StyleRes customEditTextTheme: Int = 0,
        canBeBlank: Boolean = false,
        isClearVisible: Boolean = false
    ) {
        val container = root.findViewById<FrameLayout>(R.id.modalwindows_alert_content_container)
        activity.layoutInflater.let {
            it.cloneInContext(ContextThemeWrapper(it.context, customEditTextTheme)).inflate(
                R.layout.modalwindows_dialog_alert_text_input_layout,
                container,
                true
            )
            val textInputView = container.findViewById<TextInputView>(R.id.modalwindows_alert_text_input_view)
            textInputView.isClearVisible = isClearVisible
            textInputView.isFocusable = true
            textInputView.isFocusableInTouchMode = true
            textInputView.placeholder = hint ?: ""
            textInputView.apply {
                filters?.let { list ->
                    this.filters = list.toTypedArray()
                }
                value = initialEditTextContent ?: ""
                setSelection(textInputView.value.length)
                this.inputType = inputType
                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        post {
                            popUpKeyboard(
                                { windowInsetsController?.show(WindowInsetsCompat.Type.ime()) },
                                { KeyboardUtils.showKeyboard(v) }
                            )
                        }
                    } else {
                        popUpKeyboard(
                            { windowInsetsController?.hide(WindowInsetsCompat.Type.ime()) },
                            { KeyboardUtils.hideKeyboard(v) }
                        )
                    }
                }

                requestFocus()
            }
            if (!message.isNullOrEmpty()) {
                initMessageView(activity, hasTitle, container, message)
            }
            updateTopMargin(
                activity,
                hasTitle,
                textInputView,
                DIALOG_EDITTEXT_MARGIN_TOP
            )
            val positiveBtn = root.findViewById<Button>(R.id.modalwindows_positive_button)
            positiveBtn.isEnabled = textInputView.value.isNotBlank() || canBeBlank
            textInputView.onValueChanged = { _, value ->
                positiveBtn.isEnabled = value.isNotBlank() || canBeBlank
            }
        }
    }
    //end region

    //region list
    /**@SelfDocumented*/
    fun createListDialog(
        activity: Activity,
        title: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null,
        data: List<String>,
        dialogItemClickListener: DialogItemClickListener,
        titleMaxLines: Int = TITLE_MAX_LINES_NOT_DEFINED
    ): AlertDialog {

        val root = createContainer(
            activity,
            title != null,
            positiveButtonText,
            negativeButtonText,
            positiveButtonForRemoval,
            onPositiveButtonClick,
            onNegativeButtonClick
        )
        addList(
            activity,
            root,
            title != null,
            data,
            dialogItemClickListener
        )
        val builder = AlertDialog.Builder(activity)
        title?.let { addTitle(root, it, titleMaxLines) }
        builder.setView(root)
        return builder.create()
    }

    /**@SelfDocumented*/
    fun addList(
        activity: Activity,
        root: View,
        hasTitle: Boolean,
        data: List<CharSequence>,
        dialogItemClickListener: DialogItemClickListener
    ) {
        val container = root.findViewById<FrameLayout>(R.id.modalwindows_alert_content_container)
        activity.layoutInflater.let {
            it.inflate(
                R.layout.modalwindows_dialog_alert_recycler_view,
                container,
                true
            )
            val recyclerView = container.findViewById<RecyclerView>(R.id.modalwindows_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = SimpleStringAdapter(
                data,
                dialogItemClickListener
            )
            updateTopMargin(
                activity,
                hasTitle,
                recyclerView,
                DIALOG_LIST_MARGIN_TOP
            )
        }
    }

    /**
     * Добавление в диалог до 5 кнопок
     */
    fun addButtonsList(
        activity: Activity,
        root: View,
        hasTitle: Boolean,
        data: List<String>,
        message: String?,
        dialogItemClickListener: DialogItemClickListener,
        buttonsStyle: HashMap<String, Int>? = null
    ) = data
        .map { PopupConfirmation.Button(text = it, style = buttonsStyle?.get(it)) }
        .let { buttons -> addButtonsList(activity, root, hasTitle, buttons, message, dialogItemClickListener) }

    /**
     * Добавление в диалог до 5 кнопок
     */
    fun addButtonsList(
        activity: Activity,
        root: View,
        hasTitle: Boolean,
        data: List<PopupConfirmation.Button>,
        message: String?,
        dialogItemClickListener: DialogItemClickListener
    ) {
        require(data.size <= DIALOG_BUTTONS_LIST_MAX_SIZE) {
            "A maximum of $DIALOG_BUTTONS_LIST_MAX_SIZE buttons can be placed on a alert dialog"
        }

        val container = root.findViewById<FrameLayout>(R.id.modalwindows_alert_content_container)
        activity.layoutInflater.let {
            val dialogAlertButtonsView = it.inflate(
                R.layout.modalwindows_dialog_alert_buttons_view,
                container,
                false
            ) as LinearLayout

            if (!message.isNullOrEmpty()) {
                initMessageView(activity, hasTitle, dialogAlertButtonsView, message)
            }

            val parentNegativePositiveButtonsPanel = root.findViewById<ButtonBarLayout>(R.id.modalwindows_button_panel)
            parentNegativePositiveButtonsPanel.visibility = View.GONE

            val buttonListPanel =
                dialogAlertButtonsView.findViewById<ButtonBarLayout>(R.id.modalwindows_button_list_panel)

            data.forEachIndexed { index, buttonViewModel ->
                val button = Button(
                    ContextThemeWrapper(
                        root.context,
                        buttonViewModel.style ?: R.style.ModalWindowsAlertDialogButton
                    ),
                    null,
                    0
                )
                button.text = buttonViewModel.text
                button.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                button.ellipsize = TextUtils.TruncateAt.END
                button.maxLines = 1
                button.setOnClickListener { dialogItemClickListener.onItemClicked(index) }
                buttonViewModel.buttonId?.let { button.id = it }

                buttonListPanel.addView(button)
            }

            updateTopMargin(
                activity,
                hasTitle,
                buttonListPanel,
                DIALOG_LIST_MARGIN_TOP
            )

            if (data.size > HORIZONTAL_DIALOG_BUTTONS_LIST_MAX_SIZE) {
                buttonListPanel.orientation = LinearLayout.VERTICAL
            }

            container.addView(dialogAlertButtonsView)
        }
    }
    //end region

    private fun updateTopMargin(
        context: Context,
        hasTitle: Boolean,
        target: View,
        value: Float
    ) {
        if (hasTitle) {
            ((target.parent as? ViewGroup)?.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    value,
                    context.resources.displayMetrics
                )
                    .toInt()
        }
    }

    /**
     * Создание простого диалога, не имеющего содержимого помимо заголовка и кнопок
     */
    fun createSimpleDialog(
        activity: Activity,
        title: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null,
        titleMaxLines: Int = TITLE_MAX_LINES_NOT_DEFINED
    ): AlertDialog {

        val root = createContainer(
            activity,
            title != null,
            positiveButtonText,
            negativeButtonText,
            positiveButtonForRemoval,
            onPositiveButtonClick,
            onNegativeButtonClick
        )

        val builder = AlertDialog.Builder(activity)
        title?.let { addTitle(root, it, titleMaxLines) }
        builder.setView(root)
        return builder.create()
    }

    /**
     * Создание контейнера для содержимого диалога и добавление в него кнопок (при необходимости)
     */
    @SuppressLint("InflateParams")
    fun createContainer(
        activity: Activity,
        hasTitle: Boolean,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null
    ): View {
        val contentView = activity.layoutInflater.inflate(
            R.layout.modalwindows_sbis_alert_dialog,
            null
        ) as ViewGroup
        setupButtons(
            contentView,
            positiveButtonText,
            negativeButtonText,
            positiveButtonForRemoval,
            onPositiveButtonClick,
            onNegativeButtonClick
        )
        if (!hasTitle) {
            (contentView.findViewById<View>(R.id.modalwindows_alert_content_container).layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DIALOG_CONTENT_WITHOUT_TITLE_TOP_MARGIN,
                    activity.resources.displayMetrics
                )
                    .toInt()
        }
        return contentView
    }

    /**
     * Инициализация кнопок, отображаемых под содержимым
     */
    private fun setupButtons(
        contentView: View,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveButtonForRemoval: Boolean = false,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null
    ) {
        if (positiveButtonText != null || negativeButtonText != null) {
            contentView.findViewById<View>(R.id.modalwindows_button_panel).visibility = View.VISIBLE
        }

        positiveButtonText?.let {
            val positiveButton = contentView.findViewById<Button>(R.id.modalwindows_positive_button)
            positiveButton.text = it
            positiveButton.visibility = View.VISIBLE
            positiveButton.setOnClickListener { onPositiveButtonClick?.invoke() }
            if (positiveButtonForRemoval) {
                positiveButton.setTextColor(
                    ContextCompat.getColorStateList(
                        contentView.context,
                        R.color.modal_windows_alert_remove_button_text
                    )
                )
            }
        }

        negativeButtonText?.let {
            val negativeButton = contentView.findViewById<Button>(R.id.modalwindows_negative_button)
            negativeButton.text = it
            negativeButton.visibility = View.VISIBLE
            negativeButton.setOnClickListener { onNegativeButtonClick?.invoke() }
        }
    }

    private fun initMessageView(
        activity: Activity,
        hasTitle: Boolean,
        container: ViewGroup,
        message: CharSequence
    ) {
        val messageView = container.findViewById<TextView>(R.id.modalwindows_message)
        messageView.visibility = View.VISIBLE
        messageView.movementMethod = LinkMovementMethod.getInstance()
        messageView.text = message
        updateTopMargin(
            activity,
            hasTitle,
            messageView,
            DIALOG_MESSAGE_MARGIN_TOP
        )
    }

    /**@SelfDocumented*/
    fun addTitle(
        root: View,
        title: CharSequence,
        titleMaxLines: Int
    ) {
        val titleView = root.findViewById<SbisTextView>(R.id.modalwindows_alert_title)
        titleView.visibility = View.VISIBLE
        titleView.text = title
        if (titleMaxLines != TITLE_MAX_LINES_NOT_DEFINED) {
            titleView.maxLines = titleMaxLines
            titleView.ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun popUpKeyboard(newVersionPopUp: () -> Unit, oldVersionPopUp: () -> Unit, ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            newVersionPopUp()
        } else {
            oldVersionPopUp()
        }
    }
}
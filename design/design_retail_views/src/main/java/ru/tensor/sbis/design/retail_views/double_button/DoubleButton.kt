package ru.tensor.sbis.design.retail_views.double_button

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.mikepenz.iconics.typeface.IIcon
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.common.text_watcher.DecimalTextWatcher
import ru.tensor.sbis.design.retail_views.databinding.DoubleButtonBinding
import ru.tensor.sbis.design.retail_views.double_button.DoubleButtonApi.Mode
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.retail_views.utils.kopeckCursorOffset
import ru.tensor.sbis.design.theme.global_variables.Elevation
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/**
 * View-компонент "Двойная кнопка".
 *
 * Макет: http://axure.tensor.ru/carry_sheme/%D0%BF%D0%B0%D0%BD%D0%B5%D0%BB%D1%8C_%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D1%8B.html#OnLoadVariable=Presto2-1600&CSUM=1
 */
@SuppressLint("CustomViewStyleable")
class DoubleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_double_button_style,
    @StyleRes defStyleRes: Int = R.style.RetailViewsDoubleButtonBaseStyle_Pink
) : ConstraintLayout(
    run {
        /*
         * Темы у двойных кнопок динамические (на одном экране может быть много кнопок с разными темами),
         * поэтому атрибут 'retail_views_double_button_style' может быть задан в разметке у самой кнопки,
         * после чего мы динамически ее применяем в компоненте.
         */
        val attrValues = context.obtainStyledAttributes(
            attrs, R.styleable.RetailViewsDoubleButtonAttrs, defStyleAttr, defStyleRes
        )

        val buttonStyleId = attrValues.getResourceId(
            R.styleable.RetailViewsDoubleButtonAttrs_retail_views_double_button_style, THEME_ID_NOT_AVAILABLE
        )

        attrValues.recycle()

        if (buttonStyleId == THEME_ID_NOT_AVAILABLE) {
            /*
             * Если не нашли точечное определение темы, то пытаемся прочитать его из темы модуля,
             * если и там не нашли, то применяем дефолтный стиль.
             */
            context.applyStyle(defStyleAttr, defStyleRes)
        } else {
            /* Применяем тему, которую установили точечно в .xml */
            ContextThemeWrapper(context, buttonStyleId)
        }
    },
    attrs,
    defStyleAttr,
    defStyleRes
), DoubleButtonApi {

    companion object {
        /* Константа для определения состояния, когда тема в .xml не была установлена. */
        private const val THEME_ID_NOT_AVAILABLE = -1

        /* Текст, для сброса введенного значения в поле вводы "двойной" кнопки. */
        private const val DROP_DOUBLE_BTN_VALUE_TEXT = ""

        /* Константы для корректного сохранения состояния двойной кнопки. */
        private const val SPARSE_STATE_KEY = "SPARSE_STATE_KEY"
        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
    }

    override val viewPropertiesApi: DoubleButtonApi.ViewPropertiesApi by lazy {
        object : DoubleButtonApi.ViewPropertiesApi {
            override var isVisible: Boolean = true
                set(value) {
                    field = value
                    this@DoubleButton.isVisible = value
                }

            override var isInvisible: Boolean = false
                set(value) {
                    field = value
                    this@DoubleButton.isInvisible = value
                }

            override var isEnabled: Boolean = true
                set(value) {
                    field = value
                    this@DoubleButton.isEnabled = value
                }

            override val isEditMode: Boolean
                get() = currentViewMode is Mode.Editing

            override val isEditModeLocked: Boolean
                get() = currentViewMode.let { viewModeLocal ->
                    viewModeLocal is Mode.Editing && !viewModeLocal.isLocked
                }

            override val currentButtonMode: Mode
                get() = currentViewMode

            override val currentInputValue: String
                get() = binding.editField.text.toString()

            override fun setIcon(icon: IIcon) {
                binding.txtBtnIcon.text = icon.character.toString()
            }

            override fun setInputTextValue(value: String) {
                binding.editField.setText(value)
            }

            override fun changeDoubleButtonModeTo(mode: Mode) {
                currentViewMode = mode
            }
        }
    }

    override val actionApi: DoubleButtonApi.ActionApi by lazy {
        object : DoubleButtonApi.ActionApi {
            override var onMoneyChangedAction: ((moneyValue: String?) -> Unit)? = null
            override var onIconClickAction: ((buttonMode: Mode) -> Unit)? = null
            override var onFullButtonClickAction: (() -> Unit)? = null
        }
    }

    override val dangerousApi: DoubleButtonApi.DangerousApi by lazy {
        object : DoubleButtonApi.DangerousApi {
            override val doubleButtonRoot: View
                get() = binding.root

            override val editableView: EditText
                get() = binding.editField
        }
    }

    private val binding: DoubleButtonBinding =
        DoubleButtonBinding.inflate(LayoutInflater.from(getContext()), this)

    /* Текущий режим работы "двойной" кнопки. */
    private var currentViewMode: Mode = Mode.Button
        set(value) {
            field = value
            changeViewModeTo(field)
        }

    /* Нужно ли показывать системную клавиатуру при получении фокуса. */
    private var needUseSoftKeyboard: Boolean = true

    /* Доступен ли режим с полем для ввода значения. */
    private val isEditFieldModeAllowed: Boolean
        get() = viewPropertiesApi.isEditMode

    private val moneyFieldChangeListener by lazy {
        /* По аналогии с режимом 'Money' в RetailInputView. */
        val moneyMaxLength = 10
        object : DecimalTextWatcher(binding.editField, amountFormat, kopeckCursorOffset, moneyMaxLength) {
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(charSequence, start, before, count)

                actionApi.onMoneyChangedAction?.invoke(charSequence?.toString())
            }
        }
    }

    init {
        if (isInEditMode) {
            inflate(getContext(), R.layout.double_button, this)
        } else {
            initViewAttrs(attrs, defStyleAttr, defStyleRes)
            initViews()
        }
    }

    // Отключаем стандартное сохранение состояния, будем сохранять его вручную.
    // onSaveInstanceState() будет возвращать состояние родителя, избегая дочерних view.
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }

    // Сохраняем состояние вручную, чтобы избежать проблем/конфликтов из-за одинаковых id-шников на одном экране.
    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
        putSparseParcelableArray(SPARSE_STATE_KEY, saveChildViewStates())
    }

    private fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
        val childViewStates = SparseArray<Parcelable>()
        children.forEach { child -> child.saveHierarchyState(childViewStates) }
        return childViewStates
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var stateCopy = state
        if (stateCopy is Bundle) {
            val childrenState = stateCopy.getSparseParcelableArray<Parcelable>(SPARSE_STATE_KEY)
            childrenState?.let { restoreChildViewStates(it) }
            stateCopy = stateCopy.getParcelable(SUPER_STATE_KEY)
        }
        super.onRestoreInstanceState(stateCopy)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        children.forEach {
            // оставляем возможность ввода значения в случае отключения
            if (it.id != R.id.edit_field) {
                it.isEnabled = enabled
            }
        }
    }

    private fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
        children.forEach { child -> child.restoreHierarchyState(childViewStates) }
    }

    private fun initViewAttrs(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val attrValues = context.obtainStyledAttributes(
            attrs, R.styleable.RetailViewsDoubleButtonAttrs, defStyleAttr, defStyleRes
        )

        /* Инициализируем режим работы "двойной" кнопки. */
        attrValues.getInt(
            R.styleable.RetailViewsDoubleButtonAttrs_retail_views_double_button_initial_mode, -1
        ).toDoubleButtonMode().let { initialDoubleButtonMode ->
            /* Инициализируем режим работы "двойной" кнопки. */
            currentViewMode = initialDoubleButtonMode
        }

        /* Нужно ли показывать системную клавиатуру при получении фокуса. */
        needUseSoftKeyboard = attrValues.getBoolean(
            R.styleable.RetailViewsDoubleButtonAttrs_retail_views_double_button_use_soft_keyboard, true
        ).also { value -> binding.editField.showSoftInputOnFocus = value }

        /* Установка текста и Hint'a второй кнопки. */
        attrValues.getString(R.styleable.RetailViewsDoubleButtonAttrs_retail_views_double_button_title)
            ?.let { text ->
                binding.txtBtnTitle.text = text
                binding.editField.hint = text
            }

        attrValues.recycle()
    }

    private fun initViews() {
        /* Настройка всей кнопки. */
        setupRootViewButton()

        /* Настройка кнопки с иконкой. */
        setupIconButton()

        /* Настройка поля ввода денег. */
        setupMoneyField()

        /* Установка видимости элементов. */
        updateViewsVisibility()
    }

    private fun setupRootViewButton() {
        isFocusable = true
        isClickable = true

        elevation = Elevation.M.getDimen(context)
        background = ContextCompat.getDrawable(context, R.drawable.retail_views_double_button_root_background)

        preventDoubleClickListener(LONG_CLICK_DELAY) {
            actionApi.onFullButtonClickAction?.invoke()
        }
    }

    private fun setupIconButton(isDefaultEnabled: Boolean = true) = with(binding) {
        /* Кнопка с иконкой работает, если это ее единственный режим, либо НЕ активен режим редактирования. */
        if (isDefaultEnabled || !isEditFieldModeAllowed) {
            txtBtnIcon.preventDoubleClickListener(LONG_CLICK_DELAY) {
                /*
                 * Если кнопка не в 'двойном режиме', то нажатие на
                 * 'иконку' равносильно нажатию на кнопку с текстом.
                 *
                 * https://online.sbis.ru/opendoc.html?guid=06ebcdb8-ce63-4e74-ab38-ef507d265794&client=3
                 */
                if (isEditFieldModeAllowed) {
                    actionApi.onIconClickAction?.invoke(currentViewMode)
                } else {
                    actionApi.onFullButtonClickAction?.invoke()
                }
            }
        } else {
            txtBtnIcon.setOnClickListener(null)
        }
    }

    private fun setupMoneyField() = with(binding.editField) {
        /*
            Нет простого способа проверить, установили мы TextChangeListener или нет,
            поэтому сначала пытаемся его удалить, а затем вновь добавить.
        */
        removeTextChangedListener(moneyFieldChangeListener)
        addTextChangedListener(moneyFieldChangeListener)

        /* Обработка кнопки 'Done' на софтверной клавиатуре. */
        setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardIfAllowed(view)

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    private fun updateViewsVisibility() = with(binding) {
        /* Скрываем кнопку с названием, если активирован режим "Только поле ввода". */
        txtBtnTitle.isVisible = !isEditFieldModeAllowed
        editField.isVisible = !txtBtnTitle.isVisible
    }

    private fun changeViewModeTo(newMode: Mode) {
        /* При переключении режимов, дропаем прошлое состояние кнопки. */
        binding.editField.setText(DROP_DOUBLE_BTN_VALUE_TEXT)

        /* Установка видимости элементов. */
        updateViewsVisibility()

        /* Кнопка 'Иконка' меняет свое поведение в режиме 'заблокированной' двойной кнопки. */
        setupIconButton(isDefaultEnabled = (newMode as? Mode.Editing)?.isLocked ?: false)

        /* Переключаем цвет подложки. */
        isActivated = newMode is Mode.Editing

        if (isVisible) {
            when (newMode) {
                is Mode.Button -> {
                    /* Скрываем клавиатуру. */
                    if (binding.editField.isFocused) {
                        hideKeyboardIfAllowed(binding.editField)
                        binding.editField.clearFocus()
                    }
                }

                is Mode.Editing -> {
                    binding.editField.run {
                        requestFocus()

                        /* Поднимаем клавиатуру. */
                        showKeyboardIfAllowed(this)
                    }
                }
            }
        }
    }

    private fun showKeyboardIfAllowed(view: View) {
        if (needUseSoftKeyboard) KeyboardUtils.showKeyboard(view)
    }

    private fun hideKeyboardIfAllowed(view: View) {
        view.clearFocus()
        if (needUseSoftKeyboard) KeyboardUtils.hideKeyboard(view)
    }

    private fun Int.toDoubleButtonMode(): Mode =
        when (this) {
            0 -> Mode.Button
            1 -> Mode.Editing(isLocked = false)
            2 -> Mode.Editing(isLocked = true)

            /* Если ничего не указали, то запускаемся в режиме обычной кнопки. */
            else -> Mode.Button
        }
}

/** Внутренний адаптер для поддержки логики установки иконки через .xml. */
@BindingAdapter("retail_views_double_button_icon")
internal fun DoubleButton.setupIcon(icon: IIcon) {
    viewPropertiesApi.setIcon(icon)
}
package ru.tensor.sbis.design.retail_views.numberic_keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginStart
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.NumericKeyboardBinding
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.OnInflateNumericKeyboardListener
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.retail_views.utils.isOutOfHierarchyBoundsHorizontally

/**
 * Кастомизируемая View представляющая собой цифровую клавиатуру для пользовательского ввода.
 *
 * Построена на новом дизайне окна оплаты в Рознице, Курьере. Меняет свой стиль в зависимости
 * от приложения в которое встроена.
 */
class NumericKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_numeric_keyboard_theme_customized,
    @StyleRes defStyleRes: Int = R.style.RetailViewsNumericKeyboardCustomizedTheme_RetailLight
) : ConstraintLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs) {

    private lateinit var keys: Array<SbisRoundButton>

    private val binding: NumericKeyboardBinding =
        NumericKeyboardBinding.inflate(LayoutInflater.from(getContext()), this)

    private var onInflateNumericKeyboardListener: OnInflateNumericKeyboardListener? = null

    init {
        initAttrs(attrs, defStyleAttr, defStyleRes)
        initViews()
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) {
        val attrValues = context.obtainStyledAttributes(
            attrs,
            R.styleable.RetailViewsNumericKeyboardCustomizedAttrs,
            defStyleAttr,
            defStyleRes
        )

        /* Настраиваем дефолтное состояние кнопок клавиатуры. */
        setupDefaultBottomButtons()

        /* Настраиваем текст в левой нижней кнопке [для использования вне DataBinding'a]. */
        attrValues.getString(
            R.styleable.RetailViewsNumericKeyboardCustomizedAttrs_retail_views_numeric_keyboard_extra_left_btn_title
        )?.let { title -> setupLeftButtonIcon(title) }

        /* Настраиваем иконку в левой нижней кнопке [для использования вне DataBinding'a]. */
        attrValues.getString(
            R.styleable.RetailViewsNumericKeyboardCustomizedAttrs_retail_views_numeric_keyboard_extra_left_btn_icon
        )?.let { iconText -> setupLeftButtonIcon(iconText) }

        /* Настраиваем текст в правой нижней кнопке [для использования вне DataBinding'a]. */
        attrValues.getString(
            R.styleable.RetailViewsNumericKeyboardCustomizedAttrs_retail_views_numeric_keyboard_extra_right_btn_title
        )?.let { title -> setupRightButtonIcon(title) }

        /* Настраиваем иконку в правой нижней кнопке [для использования вне DataBinding'a]. */
        attrValues.getString(
            R.styleable.RetailViewsNumericKeyboardCustomizedAttrs_retail_views_numeric_keyboard_extra_right_btn_icon
        )?.let { iconText -> setupRightButtonIcon(iconText) }

        attrValues.recycle()
    }

    /** Подписка на нажатие кнопок клавиатуры. */
    var numericClickAction: ((String) -> Unit)? = null

    /** Подписка на нажатие нижней правой кнопки клавиатуры. */
    var bottomRightButtonClickAction: (() -> Unit)? = null

    /** Подписка на нажатие кнопки "Сбросить". */
    var resetClickAction: (() -> Unit)? = null

    /** Установить текст [iconText] в правую нижнюю кнопку клавиатуры. */
    fun setupRightButtonIcon(iconText: String?) {
        binding.retailViewsBottomExtraRightButton.icon = createTextIcon(iconText)
    }

    /** Установить иконку [icon]/[iconText] в правую нижнюю кнопку клавиатуры. */
    fun setupRightButtonIcon(icon: SbisMobileIcon.Icon? = null, iconText: String? = null) {
        (icon?.character?.toString() ?: iconText)?.let { buttonIcon ->
            binding.retailViewsBottomExtraRightButton.icon = createTextIcon(buttonIcon)
        }
    }

    /** Установить иконку [iconText] в левую нижнюю кнопку клавиатуры. */
    fun setupLeftButtonIcon(iconText: String?) {
        binding.retailViewsBottomExtraLeftButton.icon = createTextIcon(iconText)
    }

    /** Установить иконку [icon]/[iconText] в левую нижнюю кнопку клавиатуры. */
    fun setupLeftButtonIcon(icon: SbisMobileIcon.Icon? = null, iconText: String? = null) {
        (icon?.character?.toString() ?: iconText)?.let { buttonIcon ->
            binding.retailViewsBottomExtraLeftButton.icon = createTextIcon(buttonIcon)
        }
    }

    /** Установка слушателя [OnInflateNumericKeyboardListener]. */
    fun setOnInflateNumericKeyboardListener(listener: OnInflateNumericKeyboardListener?) {
        onInflateNumericKeyboardListener = listener
    }

    /** Определить, видима ли клавиатура полностью по горизонтали. */
    fun isNotCompletelyVisibleHorizontal(): Boolean {
        /*
         * Вызываем measure и смотрим по measureHeight, потому что этот метод могут вызвать сразу
         * после изменения размера кнопок клавиатуры, до того как это изменение отрисовалось,
         * а значит обычный height вернет старый размер.
         *
         * @unknown_author
         */
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return isOutOfHierarchyBoundsHorizontally() || canScrollHorizontally()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        keys.forEach { it.isEnabled = enabled }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        doOnLayout {
            /* Сообщаем UI, что виртуальная клавиатура успешно отрисована. */
            onInflateNumericKeyboardListener?.onKeyboardInflated()

            /* Подчищаем за прикладниками, чтобы не было утечек. */
            onInflateNumericKeyboardListener = null
        }
    }

    private fun initViews() {
        /* Скрываем Android клавиатуру. */
        hideKeyboard()

        /* Запоминаем кнопки виртуальной клавиатуры, для дальнейшей работы. */
        keys = with(binding) {
            arrayOf(
                retailViewsKey0,
                retailViewsKey1,
                retailViewsKey2,
                retailViewsKey3,
                retailViewsKey4,
                retailViewsKey5,
                retailViewsKey6,
                retailViewsKey7,
                retailViewsKey8,
                retailViewsKey9,
                retailViewsBottomExtraRightButton,
                retailViewsBottomExtraLeftButton
            )
        }

        /* Настраиваем кнопки виртуальной клавиатуры. */
        keys.forEach { key ->
            /* Исключаем кнопки особого действия. */
            if (key != binding.retailViewsBottomExtraRightButton && key != binding.retailViewsBottomExtraLeftButton) {
                key.setOnClickListener {
                    numericClickAction?.invoke((key.icon as SbisButtonTextIcon).icon.toString())
                }
            }
        }

        /* Отдельно настраиваем доп. кнопки виртуальной клавиатуры. */
        binding.retailViewsBottomExtraRightButton.setOnClickListener { bottomRightButtonClickAction?.invoke() }
        binding.retailViewsBottomExtraLeftButton.setOnClickListener { resetClickAction?.invoke() }
    }

    private fun hideKeyboard() {
        (context as? Activity)?.window?.let { window ->
            val mode = window.attributes.softInputMode.apply {
                SOFT_INPUT_STATE_ALWAYS_HIDDEN and SOFT_INPUT_MASK_STATE
            }

            window.setSoftInputMode(mode)
        }
    }

    private fun canScrollHorizontally(): Boolean {
        var view = parent
        while (view != null) {
            if (view is ScrollView) {
                return view.canScrollHorizontally()
            } else {
                view = view.parent
            }
        }
        return false
    }

    private fun ScrollView.canScrollHorizontally(): Boolean {
        val child = getChildAt(0)
        val childWidth = child.measuredWidth
        val horizontalMarginsSum: Int = marginLeft + marginRight + marginStart + marginEnd
        return width + horizontalMarginsSum < childWidth + paddingLeft + paddingRight
    }

    private fun setupDefaultBottomButtons() {
        setupRightButtonIcon(".")
        setupLeftButtonIcon(SbisMobileIcon.Icon.smi_navBarClose)
    }

    private fun createTextIcon(textIcon: CharSequence?) =
        SbisButtonTextIcon(
            icon = textIcon,
            size = SbisButtonIconSize.S
        )
}
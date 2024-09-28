package ru.tensor.sbis.design.view_ext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.R as R_Design

/**
 * Стандартный компонент кнопки с индикатором прогресса. Компонент обеспечивает возможность смены состояния кнопки с
 * помощью методов [showProgress] и [hideProgress]. Для кастомизации текстового поля необходимо задать стиль через
 * атрибут customButtonStyle, в котором определить необходимые атрибуты, для кастомизации индикатора прогресса
 * используется атрибут progressBarStyle.
 *
 * Для удобства могут быть использованы дополнительные атрибуты:
 * progressBarColor - цвет индикатора прогресса
 * progressVisible - видимость индикатора прогресса
 * progressSize - размер индикатора прогресса (подразумевается что высота и ширина совпадают)
 * android:text - текст, выводимый на кнопке
 *
 * Padding для текста необходимо задавать с помощью customButtonStyle, установка padding напрямую не
 * окажет эффекта.
 */
class ProgressButton : RelativeLayout {

    private lateinit var button: View
    private lateinit var progressBar: SbisProgressBar
    private var buttonTextColor: Int = 0
    private var buttonDrawable: Drawable? = null
    private var isProgressVisible = false

    constructor(context: Context) : super(context) {
        initLayout(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context, attrs)
    }

    private fun initLayout(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton)
        setPadding(0, 0, 0, 0)
        initButton(typedArray)
        initProgressBar(typedArray)
        typedArray.recycle()
    }

    private fun initButton(typedArray: TypedArray) {
        @StyleRes val buttonStyleRes = typedArray.getResourceId(
            R.styleable.ProgressButton_customButtonStyle,
            R_Design.style.ProgressCircledButton
        )
        val srcDrawable = if (typedArray.hasValue(R.styleable.ProgressButton_android_src))
            typedArray.getDrawable(R.styleable.ProgressButton_android_src)
        else
            null
        button = if (srcDrawable == null)
            AppCompatButton(ContextThemeWrapper(context, buttonStyleRes)).apply {
                text = typedArray.getString(R.styleable.ProgressButton_android_text)
                if (typedArray.hasValue(R.styleable.ProgressButton_android_textColor)) {
                    setTextColor(
                        typedArray.getColor(
                            R.styleable.ProgressButton_android_textColor,
                            ContextCompat.getColor(context, android.R.color.black)
                        )
                    )
                }
                if (typedArray.hasValue(R.styleable.ProgressButton_android_textSize)) {
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        typedArray.getDimension(
                            R.styleable.ProgressButton_android_textSize,
                            context.resources.getDimension(R_Design.dimen.size_title2_scaleOff)
                        )
                    )
                }
                buttonTextColor = currentTextColor
            }
        else
            AppCompatImageButton(context).apply {
                setImageDrawable(srcDrawable)
                buttonDrawable = srcDrawable
            }

        button.background = null
        button.isClickable = false

        val buttonLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        buttonLayoutParams.addRule(CENTER_IN_PARENT)
        addView(button, buttonLayoutParams)
    }

    private fun initProgressBar(typedArray: TypedArray) {
        @StyleRes val progressBarStyleRes = typedArray.getResourceId(R.styleable.ProgressButton_progressBarStyle, 0)
        progressBar = SbisProgressBar(ContextThemeWrapper(context, progressBarStyleRes))

        val progressBarSize = typedArray.getDimension(R.styleable.ProgressButton_progressSize,
                                                      context.resources.getDimension(R_Design.dimen.progress_bar_size))
        val progressBarParams = LayoutParams(progressBarSize.toInt(), progressBarSize.toInt())
        progressBarParams.addRule(CENTER_IN_PARENT)
        addView(progressBar, progressBarParams)
        val defaultProgressColor = ContextCompat.getColor(context, R_Design.color.text_color_accent_5)
        progressBar.setIndeterminateColor(
            typedArray.getColor(
                R.styleable.ProgressButton_progressBarColor,
                defaultProgressColor
            )
        )
        isProgressVisible = typedArray.getBoolean(R.styleable.ProgressButton_progressVisible, false)
        if (isProgressVisible) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    /** @SelfDocumented */
    fun showProgress() {
        isProgressVisible = true
        isClickable = false
        button.let {
            val transparentColor = ContextCompat.getColor(context, R_Design.color.palette_color_transparent)
            when (it) {
                is AppCompatImageButton -> it.setImageDrawable(ColorDrawable(transparentColor))
                is AppCompatButton -> {
                    buttonTextColor = it.currentTextColor
                    it.setTextColor(transparentColor)
                }
                else -> throw ClassCastException("button is not AppCompatButton or AppCompatImageButton")
            }
        }
        progressBar.visibility = View.VISIBLE
    }

    /** @SelfDocumented */
    fun hideProgress() {
        isProgressVisible = false
        isClickable = true
        button.let {
            when (it) {
                is AppCompatImageButton -> it.setImageDrawable(buttonDrawable)
                is AppCompatButton -> it.setTextColor(buttonTextColor)
                else -> throw ClassCastException("button is not AppCompatButton or AppCompatImageButton")
            }
        }
        progressBar.visibility = View.GONE
    }

    /**
     * Изменения состояния enabled/disabled, которое дополнительно пробрасывается в текстовое поле, что необходимо,
     * например, для смены цвета текста в зависимости от состояния
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        button.isEnabled = enabled
    }

    /** @SelfDocumented */
    fun isProgressVisible() = isProgressVisible
}

/**
 * Аттрибут для показа/прятанья прогрессбара в кнопке.
 * Даже если будет свойство, то ObservableField-ы не будут биндиться, что приведёт к ошибке сборки.
 */
@BindingAdapter("isProgressVisible")
fun ProgressButton.changeProgressVisibility(progressVisible: Boolean) {
    if (progressVisible) {
        showProgress()
    } else {
        hideProgress()
    }
}
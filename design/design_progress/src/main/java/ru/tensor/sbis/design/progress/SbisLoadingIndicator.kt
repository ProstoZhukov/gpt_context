package ru.tensor.sbis.design.progress

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.loadEnum

/**
 * Индикатор загрузки с поддержкой отложенного появления.
 *
 * [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D0%B8%D0%BD%D0%B4%D0%B8%D0%BA%D0%B0%D1%82%D0%BE%D1%80_%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B0__%D1%80%D0%BE%D0%BC%D0%B0%D1%88%D0%BA%D0%B0_&g=1)
 *
 * @author ps.smirnyh
 */
class SbisLoadingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisLoadingIndicatorTheme,
    @StyleRes defStyleRes: Int = R.style.SbisLoadingIndicatorDefaultTheme
) : ProgressBar(
    ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private var isRunningDelayedShowing = false
    private val delayedShowing = Runnable {
        if (isRunningDelayedShowing) {
            super.setVisibility(VISIBLE)
            isRunningDelayedShowing = false
        }
    }

    /**
     * Отображение прогресса с задержкой.
     */
    var isDelayedShowing: Boolean = false
        set(value) {
            if (field && !value) stopDelayedShowing(completeImmediately = true)
            field = value
        }

    /**
     * Значение задержки появления прогресса.
     */
    var appearanceDelay: Int = 0

    /**
     * Использование стандартного drawable для прогресса.
     * Если значение false, то drawable будет покрашен в [R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_progressColor].
     */
    var strictlyUseProgressDrawable: Boolean = false

    /**
     * Размер прогресса.
     *
     * @see InlineHeight
     */
    var progressSize: InlineHeight = InlineHeight.XS
        set(value) {
            field = value
            val size = field.getDimenPx(context)
            if (width == size && height == size) return
            updateLayoutParams {
                width = size
                height = size
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.SbisLoadingIndicator, defStyleAttr, defStyleRes) {
            isDelayedShowing =
                getBoolean(R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_delayedShowing, false)
            strictlyUseProgressDrawable =
                getBoolean(
                    R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_strictlyUseProgressDrawable,
                    false
                )
            appearanceDelay =
                getInteger(R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_appearanceDelay, appearanceDelay)
            if (!strictlyUseProgressDrawable) {
                val progressColor =
                    getColor(R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_progressColor, Color.MAGENTA)
                setIndeterminateColor(progressColor)
            }
            loadEnum(
                R.styleable.SbisLoadingIndicator_SbisLoadingIndicator_progressSize,
                progressSize,
                *InlineHeight.values()
            )
        }
        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    /**
     * Изменить видимость игнорируя включенную настройку задержки отображения.
     */
    fun forceSetVisibility(visibility: Int) {
        stopDelayedShowing(completeImmediately = false)
        super.setVisibility(visibility)
    }

    /**
     * Сделать вью невидимой и установить видимость с задержкой по умолчанию.
     */
    fun postDefaultDelayedVisible() {
        if (visibility == VISIBLE) {
            forceSetVisibility(INVISIBLE)
        }
        isDelayedShowing = true
        visibility = VISIBLE
    }

    /** @SelfDocumented */
    fun setIndeterminateColor(progressColor: Int) {
        indeterminateTintList = ColorStateList.valueOf(progressColor)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopDelayedShowing(completeImmediately = true)
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == VISIBLE && isDelayedShowing) {
            if (!isRunningDelayedShowing) {
                isRunningDelayedShowing = true
                postDelayed(delayedShowing, appearanceDelay.toLong())
            }
        } else {
            forceSetVisibility(visibility)
        }
    }

    private fun stopDelayedShowing(completeImmediately: Boolean) {
        if (!isRunningDelayedShowing) return
        isRunningDelayedShowing = false
        removeCallbacks(delayedShowing)
        if (completeImmediately) {
            super.setVisibility(VISIBLE)
        }
    }
}
package ru.tensor.sbis.design.retail_views.banknote

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.BanknoteViewBinding
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.design.utils.getThemeInteger
import kotlin.properties.Delegates

/**
 * View отображающая картинку банкноты. Используется на экране продажи.
 *
 * Построена на новом дизайне окна оплаты в Рознице, Курьере. Меняет свой стиль в зависимости
 * от приложения в которое встроена.
 */
class BanknoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_banknote_theme_customized,
    @StyleRes defStyleRes: Int = R.style.RetailViewsBanknoteCustomizedTheme_RetailLight
) : FrameLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val CHANGE_DRAWABLE_VALUE = 10
    }

    /*
     * ВАЖНО: тут 'хитрая' инициализация. Переменная 'banknote' должна быть объявлена выше,
     * чем блок 'init { ... }', внутри которого в методе 'initViews(...)' выполняется
     * установка конкретного значения в неё.
     *
     * Перешел на более явный 'СТОП', чтобы ловить подобное в процессе разработки, а не на бою:
     * https://online.sbis.ru/opendoc.html?guid=7f961de0-352d-4566-ba50-09c340ee255c&client=3
     */
    private var banknote: BanknoteCountry by Delegates.notNull()

    private var binding: BanknoteViewBinding by Delegates.notNull()

    init {
        binding = BanknoteViewBinding.inflate(
            LayoutInflater.from(getContext()), this, true
        ).apply {
            /* Отключаем обрезание счетчика банкнот, когда он выходит за пределы родительского контейнера. */
            clipChildren = false

            /* Устанавливаем шрифт для счетчика банкнот. */
            retailViewsCounter.typeface = TypefaceManager.getRobotoRegularFont(context)
        }

        initViews(attrs, defStyleAttr, defStyleRes)
    }

    private var count = 0

    private val banknoteButton: SbisButton
        get() = binding.retailViewsButton

    private val counterView: SbisTextView
        get() = binding.retailViewsCounter

    /** @SelfDocumented */
    fun setOnBanknoteClickListener(listener: ((BanknoteView, Int) -> Unit)?) {
        if (listener == null) {
            banknoteButton.setOnClickListener(null)
            return
        }

        banknoteButton.setOnClickListener {
            listener(this, banknote.value)
        }
    }

    /** @SelfDocumented */
    fun setOnCounterClickListener(listener: ((BanknoteView, Int) -> Unit)?) {
        if (listener == null) {
            counterView.setOnClickListener(null)
            return
        }

        counterView.setOnClickListener {
            listener(this, banknote.value)
        }
    }

    /** @SelfDocumented */
    fun increaseCounter() {
        updateCounterDrawableIfNeeded(count, ++count)
        updateCounter()
    }

    /** @SelfDocumented */
    fun decreaseCounter() {
        updateCounterDrawableIfNeeded(count, --count)
        updateCounter()
    }

    /** @SelfDocumented */
    fun resetCounter() {
        updateCounterDrawableIfNeeded(count, 0)
        count = 0
        updateCounter()
    }

    /** Обновить ширину [BanknoteView] на указанную [newViewWidth]. */
    fun updateBanknoteWidth(newViewWidth: Int) {
        with(binding) {
            /* Root контейнер со счетчиком. */
            root.updateLayoutParams { width = newViewWidth }
            /* SbisButton, который выступает в качестве подложки. */
            retailViewsButton.updateLayoutParams { width = newViewWidth }
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun initViews(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val attrValues = context.obtainStyledAttributes(
            attrs, R.styleable.RetailViewsBanknoteViewCustomizedAttrs, defStyleAttr, defStyleRes
        )

        banknote = BanknoteCountry.values()[
            attrValues.getIntegerOrThrow(
                R.styleable.RetailViewsBanknoteViewCustomizedAttrs_retail_views_banknote_country_position_value
            )
        ]

        with(banknoteButton) {
            setTitle(banknote.value.toString())
            backgroundType = SbisButtonBackground.Contrast
            style = SbisButtonCustomStyle(
                backgroundColor = context.getThemeInteger(banknote.backgroundColorRes),
                titleStyle = SbisButtonTitleStyle.create(
                    color = context.getThemeColor(R.attr.retail_views_banknote_view_customized_text_color),
                    colorDisabled = context.getThemeColor(R.attr.retail_views_banknote_view_customized_text_color)
                )
            )
        }

        attrValues.recycle()
    }

    private fun updateCounter() {
        counterView.isVisible = count > 0
        counterView.text = count.toString()
    }

    private fun updateCounterDrawableIfNeeded(oldCount: Int, newCount: Int) {
        val needChangeDrawable =
            (CHANGE_DRAWABLE_VALUE in (oldCount + 1)..newCount) ||
                (CHANGE_DRAWABLE_VALUE in (newCount + 1)..oldCount)

        if (needChangeDrawable) {
            val counterDrawableResId =
                if (count < CHANGE_DRAWABLE_VALUE) R.drawable.retail_views_banknote_counter_circle_background
                else R.drawable.retail_views_banknote_counter_rectangle_background
            counterView.background = ContextCompat.getDrawable(context, counterDrawableResId)
        }
    }
}
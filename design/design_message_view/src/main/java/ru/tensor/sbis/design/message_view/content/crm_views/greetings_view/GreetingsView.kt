package ru.tensor.sbis.design.message_view.content.crm_views.greetings_view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.marginBottom
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.utils.extentions.setBottomMargin
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding
import ru.tensor.sbis.design.utils.extentions.setVerticalPadding
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью для отображения кнопок приветствий чата коснультации.
 *
 * @author dv.baranov
 */
class GreetingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val defaultTextSize by lazy { context.getDimen(RDesign.attr.fontSize_m_scaleOff) }
    private val defaultTextColor by lazy { context.getThemeColorInt(RDesign.attr.textColor) }
    private val viewEndPadding by lazy { context.getDimenPx(RDesign.attr.offset_m) }
    private val viewStartPadding by lazy {
        resources.getDimensionPixelSize(R.dimen.design_message_view_start_padding_greeting_view)
    }
    private val viewBottomPadding by lazy { context.getDimenPx(RDesign.attr.offset_m) }
    private val buttonHorizontalPadding by lazy { context.getDimenPx(RDesign.attr.offset_l) }
    private val buttonVerticalPadding by lazy { context.getDimenPx(RDesign.attr.offset_m) }
    private val spaceBetweenButtons by lazy { context.getDimenPx(RDesign.attr.offset_m) }

    private var onGreetingClick: (title: String) -> Unit = {}
    private var currentTitles: List<String> = emptyList()
    init {
        setHorizontalPadding(viewStartPadding, viewEndPadding)
        setBottomPadding(viewBottomPadding)
        orientation = VERTICAL
        gravity = Gravity.END
    }

    /**
     * Задать обработчик кликов на кнопку приветствий.
     */
    fun setOnGreetingClick(onGreetingClick: (title: String) -> Unit) {
        this.onGreetingClick = onGreetingClick
    }

    /**
     * Создать кнопки приветствий, где текст кнопки - переданный title из списка.
     */

    fun setTitles(titles: List<String>) {
        if (currentTitles == titles) return
        currentTitles = titles
        removeAllViews()
        titles.forEach { title ->
            val button = SbisTextView(context).apply {
                textSize = defaultTextSize
                setTextColor(defaultTextColor)
                text = title
                background = AppCompatResources.getDrawable(context, R.drawable.design_message_view_greeting_bg)
                setHorizontalPadding(buttonHorizontalPadding)
                setVerticalPadding(buttonVerticalPadding)
                setOnClickListener {
                    DebounceActionHandler(DEBOUNCE_TIMEOUT).handle {
                        onGreetingClick(title)
                    }
                }
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                maxLines = 2
            }
            addView(button)
            button.setBottomMargin(spaceBetweenButtons)
        }
    }

    /**
     * Получить сумму высот всех кнопок со всеми вертикальными отступами самих кнопок и вьюхи.
     */
    fun getContentHeight(): Int {
        var sum = viewBottomPadding
        children.forEach {
            sum += it.height + it.marginBottom
        }
        return sum
    }
}

private const val DEBOUNCE_TIMEOUT = 2000L

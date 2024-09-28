package ru.tensor.sbis.design.cloud_view.layout.children

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.textStyleProvider
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import java.util.Date

/**
 * Дата и время для компонента ячейка-облако [CloudView].
 *
 * @author vv.chekurda
 */
class CloudDateTimeView(
    context: Context,
    @StyleRes styleRes: Int = R.style.IncomeCloudViewDateStyle
) : View(context) {

    /**
     * Конструктор для отображения preview в студии
     */
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : this(ContextThemeWrapper(context, R.style.DefaultCloudViewTheme_Income), R.style.IncomeCloudViewDateStyle)

    /**
     * Разметка текста для отображения даты или времени сообщения.
     */
    private val textLayout: TextLayout

    /**
     * Метка о том, что необходимо отобразить дату.
     */
    private var isDate = false

    /**
     * Форматтер даты и времени.
     */
    private val dateTimeFormatter: ListDateFormatter by lazy {
        ListDateFormatter.DateTimeWithTodayShort(context)
    }

    /**
     * Установить дату/время.
     */
    var date: Date? = null
        set(value) {
            field = value
            val formattedDateTime = value?.let(dateTimeFormatter::format)
            val dateText = if (isDate) formattedDateTime?.date else formattedDateTime?.time
            text = dateText ?: EMPTY
        }

    /**
     * Установить текст.
     */
    var text: CharSequence = EMPTY
        set(value) {
            field = value
            val isChanged = textLayout.configure {
                text = value
                this@CloudDateTimeView.isVisible = value.isNotBlank()
            }
            if (isChanged) safeRequestLayout()
        }

    /**
     * Координата центра текста по y относительно view.
     */
    val textCenterY: Int
        get() = textLayout.let {
            it.top + it.paddingTop + (
                it.getDesiredHeight() -
                    it.paddingTop - it.paddingBottom
                ) / 2
        }

    init {
        setWillNotDraw(false)
        context.withStyledAttributes(attrs = R.styleable.CloudDateTimeView, defStyleRes = styleRes) {
            isDate = getBoolean(R.styleable.CloudDateTimeView_CloudDateTimeView_isDate, isDate)
        }
        textLayout = createTextLayoutByStyle(context, styleRes, textStyleProvider)
        isVisible = text.isNotBlank()
        if (isInEditMode) date = Date()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val isChanged = textLayout.updatePadding(left, top, right, bottom)
        if (isChanged) safeRequestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(textLayout.width, textLayout.height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        textLayout.layout(0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        textLayout.draw(canvas)
    }

    override fun getBaseline(): Int =
        textLayout.baseline

    override fun onSetAlpha(alpha: Int): Boolean =
        true.also {
            textLayout.textPaint.alpha = alpha
        }

    override fun hasOverlappingRendering(): Boolean = false
}
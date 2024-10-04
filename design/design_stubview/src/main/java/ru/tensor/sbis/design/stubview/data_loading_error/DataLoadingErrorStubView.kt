package ru.tensor.sbis.design.stubview.data_loading_error

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Заглушка для декорирования пустой области во время пагинации. Содержит иконку и сообщение.
 * Применяется для информирования пользователя об отсутствии контента или об ошибке, например нет подключения к интернету.
 *
 * [Стандарт](http://axure.tensor.ru/MobileStandart8/#g=1&p=%D0%BE%D1%84%D0%BB%D0%B0%D0%B9%D0%BD_%D1%80%D0%B5%D0%B6%D0%B8%D0%BC__ver_2_)
 *
 * @author da.zhukov
 */
class DataLoadingErrorStubView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.dataLoadingErrorStubViewTheme,
    @StyleRes defStyleRes: Int = R.style.DataLoadingErrorStubViewDefaultStyle
) : View(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {
    private val errorIconLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.DataLoadingErrorStubView_iconStyle, styleRes = R.style.DataLoadingErrorContentIcon)
    )
    private val errorMessageLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.DataLoadingErrorStubView_messageStyle, styleRes = R.style.DataLoadingErrorContentMessage)
    )

    init {
        setWillNotDraw(false)
        if (isInEditMode) {
            errorIconLayout.configure {
                paint.textSize = dp(24).toFloat()
            }
            errorMessageLayout.configure {
                paint.textSize = dp(14).toFloat()
            }
        }
        accessibilityDelegate = TextLayoutAutoTestsHelper(this, errorIconLayout, errorMessageLayout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureDirection(widthMeasureSpec) {
            paddingStart + paddingEnd + errorIconLayout.width + errorMessageLayout.getDesiredWidth()
        }
        val availableWidth = width - paddingEnd - paddingStart
        val messageWidth = availableWidth - errorIconLayout.width
        errorMessageLayout.configure { layoutWidth = messageWidth }
        val height = measureDirection(heightMeasureSpec) {
            paddingTop + paddingBottom + maxOf(errorIconLayout.height, errorMessageLayout.height)
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingStart
        val top = paddingTop
        val availableHeight = measuredHeight - paddingTop - paddingBottom
        errorIconLayout.layout(left, top + (availableHeight - errorIconLayout.height) / 2)
        errorMessageLayout.layout(errorIconLayout.right, top + (availableHeight - errorMessageLayout.height) / 2)
    }

    override fun onDraw(canvas: Canvas) {
        errorIconLayout.draw(canvas)
        errorMessageLayout.draw(canvas)
    }
}
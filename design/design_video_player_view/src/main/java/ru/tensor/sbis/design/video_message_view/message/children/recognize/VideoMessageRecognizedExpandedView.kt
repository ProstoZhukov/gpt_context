package ru.tensor.sbis.design.video_message_view.message.children.recognize

import android.content.Context
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.video_message_view.R

/**
 * Вью для отображения облочка с распознанным текстом.
 *
 * @author da.zhukov
 */
internal class VideoMessageRecognizedExpandedView(
    context: Context
) : ViewGroup(context) {

    /**
     * Разметка для отображения распознанного текста внутри облачка.
     */
    private val recognizedTextLayout = TextLayout.createTextLayoutByStyle(
        context,
        R.style.VideoMessageRecognizedText
    ) { maxLines = Int.MAX_VALUE }

    /**
     * Разметка для отображения иконки сворачивания текста внутри облачка.
     */
    private val collapseIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.VideoMessageCollapseIcon,
        VideoMessageRecognizedExpandedViewStylesProvider.textStyleProvider
    )

    private val outcomeBackground by lazy {
        AppCompatResources.getDrawable(context, R.drawable.video_message_recognized_out_bg)
    }

    private val incomeBackground by lazy {
        AppCompatResources.getDrawable(context, R.drawable.video_message_recognized_in_bg)
    }

    /**
     * Распознанный текст.
     */
    private var recognizeText = ""

    init {
        setWillNotDraw(false)
        isClickable = true
        setOutcome(true)
        recognizedTextLayout.textPaint.alpha = (255 * 0.6).toInt()
    }

    fun setOutcome(isOutcome: Boolean) {
        background = if (isOutcome) {
            outcomeBackground
        } else {
            incomeBackground
        }
    }

    fun setRecognizeText(text: CharSequence?) {
        recognizeText = text.toString()
        val isChanged = recognizedTextLayout.configure {
            this.text = recognizeText
        }
        if (isChanged) {
            safeRequestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        configureLayout(specWidth)
        val width = recognizedTextLayout.width + collapseIcon.width + paddingStart + paddingEnd
        setMeasuredDimension(
            width,
            MeasureSpecUtils.measureDirection(heightMeasureSpec) {
                recognizedTextLayout.height + paddingBottom + paddingTop
            }
        )
    }

    private fun configureLayout(sizeWidth: Int) {
        val contentAvailableWidth = sizeWidth - paddingStart - paddingEnd
        collapseIcon.configure {
            isVisible = true
        }
        recognizedTextLayout.configure {
            maxWidth = contentAvailableWidth - collapseIcon.width
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        internalLayout()
    }

    private fun internalLayout() {
        val topPos = paddingTop
        val rightPos = measuredWidth - paddingRight
        collapseIcon.layout(rightPos - collapseIcon.width, measuredHeight - collapseIcon.height)
        recognizedTextLayout.layout(collapseIcon.left - recognizedTextLayout.width, topPos)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        recognizedTextLayout.draw(canvas)
        collapseIcon.draw(canvas)
    }
}

/**
 * Поставщик закешированных стилей для компонента ячейка-облако.
 */
internal object VideoMessageRecognizedExpandedViewStylesProvider : CanvasStylesProvider()
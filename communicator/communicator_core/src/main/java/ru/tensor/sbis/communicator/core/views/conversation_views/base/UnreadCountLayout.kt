package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import androidx.core.graphics.withSave
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unreadCountOrangePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_contrastUnreadCountPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unaccentedUnreadCountPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unreadCountSecondaryPaint
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutPadding
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Layout для отображения количества непрочитанных сообщений в ячейках реестра диалогов/каналов.
 *
 * @author vv.chekurda
 */
internal class UnreadCountLayout {

    /**
     * Разметка с цифрой количества непрочитанных сообщений.
     */
    val countLayout: TextLayout?
        get() = if (isVisible) lazyCountLayout else null

    private val lazyCountLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout(theme_unaccentedUnreadCountPaint) {
            alignment = Layout.Alignment.ALIGN_CENTER
            includeFontPad = false
            val horizontalPadding = CommunicatorTheme.offset2XS
            padding = TextLayoutPadding(start = horizontalPadding, end = horizontalPadding)
        }
    }

    /**
     * Краска для рисования фона счетчика.
     */
    private var backgroundPaint: Paint? = null

    /**
     * Rect для рисования фона.
     */
    private val backgroundRect = RectF()

    /**
     * Признак видимости разметки.
     */
    var isVisible: Boolean = false
        private set

    /**
     * Ширирна разметки.
     */
    val width: Int
        get() = if (isVisible) {
            max(countLayout!!.width, height)
        } else {
            0
        }

    /**
     * Высота разметки.
     */
    val height: Int
        get() = if (isVisible) {
            CommunicatorTheme.iconSizeM.roundToInt()
        } else {
            0
        }

    /** Радиус скругления фона. */
    private val cornersRadius = CommunicatorTheme.iconSizeM.roundToInt() / 2f

    /**
     * Установить данные.
     *
     * @param unreadCountString строка с количеством непрочитанных сообщений.
     * @param isSecondary true, если счетчик второстепенный (серого цвета).
     */
    fun setData(unreadCountString: String?, isSecondary: Boolean = false) {
        isVisible = !unreadCountString.isNullOrEmpty()
        backgroundPaint = if (isSecondary) theme_unreadCountSecondaryPaint else theme_unreadCountOrangePaint
        countLayout?.configure {
            text = unreadCountString.orEmpty()
            paint = if (isSecondary) theme_unaccentedUnreadCountPaint else theme_contrastUnreadCountPaint
        }
    }

    /**
     * Разместить разметку счетчика непрочитанных [UnreadCountLayout]
     * по левой [left] и верхней [top] позициям, определяемой родителем.
     * Возможно выровнять базовую линию счетчика [countLayout] по базовой линии другой разметки,
     * передав ее в [toBaseLine].
     */
    fun layout(left: Int, top: Int, toBaseLine: Int = 0) {
        val countLayout = countLayout ?: return
        val (countTop, backgroundTop) =
            if (toBaseLine == 0) {
                top + (height - countLayout.height) / 2 to top
            } else {
                val countTop = top + toBaseLine - countLayout.baseline
                countTop to countTop - (height - countLayout.height) / 2
            }

        countLayout.layout(
            left + (width - countLayout.width) / 2,
            countTop
        )
        backgroundRect.set(
            left.toFloat(),
            backgroundTop.toFloat(),
            left + width.toFloat(),
            backgroundTop + height.toFloat()
        )
    }

    /**
     * Нарисовать разметку счетчика непрочитанных сообщений.
     *
     * @param canvas canvas родительской view, в которой будет рисоваться разметка.
     */
    fun draw(canvas: Canvas) {
        val countLayout = countLayout ?: return
        canvas.withSave { drawRoundRect(backgroundRect, cornersRadius, cornersRadius, backgroundPaint ?: return) }
        countLayout.draw(canvas)
    }
}
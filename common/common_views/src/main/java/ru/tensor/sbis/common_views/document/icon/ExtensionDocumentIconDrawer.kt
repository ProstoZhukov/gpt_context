package ru.tensor.sbis.common_views.document.icon

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import androidx.annotation.ColorInt
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.getNewUnknownFileIcon

private const val MAX_EXTENSION_LENGTH = 4
private const val ELLIPSIS = "\u2026"

//Отношение ширины иконки файла неизвестного типа к её высоте
private const val UNKNOWN_ICON_WIDTH_TO_HEIGHT_RATION = 0.77f

//Отношение паддингов (для текста внутри) иконки файла неизвестного типа к её ширине
private const val UNKNOWN_ICON_PADDING_TO_WIDTH_RATION = 0.75f

//Множитель для высчитывания правильного размера текста расширения внутри иконки файла неизвестного типа
private const val EXTENSION_WIDTH_FACTOR = UNKNOWN_ICON_WIDTH_TO_HEIGHT_RATION * UNKNOWN_ICON_PADDING_TO_WIDTH_RATION

//Размер текста, с помощью которого через пропорцию определяется правильный размер текста расширения
private const val SUPPORT_TEXT_SIZE = 100000f

/**
 * Вспомогательный класс для отрисовки иконки документа, содержащей расширение
 *
 * @param extension         Расширение документа
 * @param insideIcon        Признак, обозначающий, следует ли рисовать расширение внутри иконки
 * @param clipExtension     Признак, обозначающий, следует ли обрезать расширение, если оно длинное
 *
 * @property boundingIconParams     Параметры иконки, "окружающей" расширение
 *
 * @author sa.nikitin
 */
class ExtensionDocumentIconDrawer(
    extension: String,
    insideIcon: Boolean,
    clipExtension: Boolean,
    private var sizePx: Int,
    @ColorInt private var color: Int
) {

    var boundingIconParams: DefaultDocumentIconParams? = null
        private set
    private var extension: String = StringUtils.EMPTY

    private val paint: TextPaint =
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            isUnderlineText = false
        }
    private var drawYPos: Float = 0f
    private val supportRect: Rect = Rect()

    init {
        update(extension, insideIcon, clipExtension)
    }

    /**
     * Обновить состояние
     */
    fun update(extension: String, insideIcon: Boolean, clipExtension: Boolean) {
        boundingIconParams = if (insideIcon) DefaultDocumentIconParams(getNewUnknownFileIcon(), 0, -1) else null
        var finalExtension = if (extension.startsWith(".")) extension.substring(1) else extension
        if (clipExtension && finalExtension.length > MAX_EXTENSION_LENGTH) {
            finalExtension = finalExtension.take(MAX_EXTENSION_LENGTH - 1) + ELLIPSIS
        }
        this.extension = finalExtension.toUpperCase()
        updateColor()
        boundingIconParams?.sizePx = sizePx
        updateTextSize()
        updateDrawYPos()
    }

    /**
     * Обновить размер иконки
     *
     * @param size Новый размер иконки
     */
    fun updateSize(size: Int) {
        this.sizePx = size
        boundingIconParams?.sizePx = size
        updateTextSize()
        updateDrawYPos()
    }

    /**
     * Обновить цвет иконки
     *
     * @param color Новый цвет иконки
     */
    fun updateColor(@ColorInt color: Int) {
        this.color = color
        updateColor()
    }

    /**
     * Отрисовать иконку на канвасе [canvas] с границами [bounds]
     */
    fun draw(canvas: Canvas, bounds: Rect) {
        canvas.drawText(extension, bounds.exactCenterX(), drawYPos, paint)
    }

    private fun updateColor() {
        boundingIconParams?.color = color
        paint.color = color
    }

    private fun updateTextSize() {
        if (sizePx != 0) {
            if (boundingIconParams != null) {
                val extensionWidth = sizePx * EXTENSION_WIDTH_FACTOR
                paint.textSize = SUPPORT_TEXT_SIZE
                paint.getTextBounds(extension, 0, extension.length, supportRect)
                val finalTextSize = SUPPORT_TEXT_SIZE * extensionWidth / supportRect.width()
                paint.textSize = finalTextSize
            } else {
                paint.textSize = sizePx.toFloat()
            }
        }
    }

    private fun updateDrawYPos() {
        if (sizePx != 0) {
            paint.getTextBounds(extension, 0, extension.length, supportRect)
            drawYPos = (sizePx + supportRect.height()) * 0.5f
        }
    }
}
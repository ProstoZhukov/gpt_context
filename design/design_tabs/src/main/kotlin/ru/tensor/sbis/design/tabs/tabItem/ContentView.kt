package ru.tensor.sbis.design.tabs.tabItem

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.withTranslation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterDrawable
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterStyle
import ru.tensor.sbis.design.counters.textcounter.SbisTextCounterDrawable
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Представление элемента контента на вкладке.
 *
 * @author da.zolotarev
 * TODO https://dev.sbis.ru/opendoc.html?guid=fb09f43d-d780-420f-a29a-5132c72ec2e6&client=3
 */
internal sealed class ContentView {

    /** @SelfDocumented */
    abstract fun onMeasure(left: Int, height: Int)

    /** @SelfDocumented */
    abstract fun onDraw(canvas: Canvas)

    /** @SelfDocumented */
    abstract fun getWidth(): Int

    /** @SelfDocumented */
    abstract fun getHeight(): Int

    /**
     * Получить описание контента.
     */
    abstract fun getContentDescription(): String

    /** @SelfDocumented */
    open fun setSelected(isSelected: Boolean) = Unit

    /** Обновить значения, полученные из [SbisTabItemStyleHolder]. */
    open fun updateStyleHolder() = Unit

    /**
     * Выполнить действие на [View.onAttachedToWindow]
     */
    open fun onAttachedToWindow(scope: CoroutineScope?) = Unit

    /** Элемент заголовка вкладки. */
    internal class Text(
        text: CharSequence,
        private val styleHolder: SbisTabItemStyleHolder,
        private val parent: View,
        @ColorInt private val textCustomColor: Int?
    ) :
        ContentView() {

        val textLayout = TextLayout {
            paint.textSize = styleHolder.textSize
            paint.typeface = styleHolder.textFont
            includeFontPad = false
            this.text = text
        }.apply {
            makeClickable(parent)
            colorStateList = if (textCustomColor != null) {
                ColorStateList(
                    SbisTabItemStyleHolder.STATES,
                    intArrayOf(textCustomColor, textCustomColor)
                )
            } else {
                styleHolder.textColor
            }
        }

        override fun onMeasure(left: Int, height: Int) {
            textLayout.layout(left, (height - textLayout.height) / 2)
        }

        override fun onDraw(canvas: Canvas) {
            textLayout.draw(canvas)
        }

        override fun getWidth() = textLayout.width
        override fun getHeight() = textLayout.height
        override fun getContentDescription() = "${this.javaClass.simpleName}: ${textLayout.text}"

        override fun setSelected(isSelected: Boolean) {
            textLayout.isSelected = isSelected
        }

        override fun updateStyleHolder() {
            textLayout.configure {
                paint.textSize = styleHolder.textSize
                paint.typeface = styleHolder.textFont
            }
            textLayout.colorStateList = if (textCustomColor != null) {
                ColorStateList(
                    SbisTabItemStyleHolder.STATES,
                    intArrayOf(textCustomColor, textCustomColor)
                )
            } else {
                styleHolder.textColor
            }
        }
    }

    /** Элемент дополнительного текста вкладки. */
    internal open class AdditionalText(
        text: String,
        @ColorInt private val textCustomColor: Int?,
        private val styleHolder: SbisTabItemStyleHolder
    ) : ContentView() {
        open val textLayout = TextLayout {
            paint.color = textCustomColor ?: styleHolder.additionalTextColor
            paint.textSize = styleHolder.additionalTextSize
            paint.typeface = styleHolder.additionalTextFont
            this.text = text
        }

        override fun onMeasure(left: Int, height: Int) {
            textLayout.layout(left, (height - textLayout.height) / 2)
        }

        override fun onDraw(canvas: Canvas) {
            textLayout.draw(canvas)
        }

        override fun getWidth() = textLayout.width

        override fun getHeight() = textLayout.height

        override fun getContentDescription() = "${this.javaClass.simpleName}: ${textLayout.text}"

        override fun updateStyleHolder() {
            textLayout.configure { paint.textSize = styleHolder.textSize }
        }
    }

    /** Элемент иконки вкладки. */
    internal class Icon(
        icon: String,
        customSize: Float?,
        @ColorInt iconCustomColor: Int?,
        styleHolder: SbisTabItemStyleHolder
    ) :
        AdditionalText(icon, iconCustomColor, styleHolder) {
        override val textLayout = TextLayout {
            paint.color = iconCustomColor ?: styleHolder.iconColor
            paint.textSize = customSize ?: styleHolder.iconSize
            paint.typeface = styleHolder.iconFont
            this.text = icon
        }
    }

    /** Элемент иконки с счетчиком в углу. */
    internal class IconCounter(
        context: Context,
        icon: String,
        customSize: Float?,
        @ColorInt iconCustomColor: Int?,
        private val counterValue: StateFlow<Int>?,
        counterStyle: SbisCounterStyle,
        private val styleHolder: SbisTabItemStyleHolder,
        private val onEmitNewValue: (Boolean) -> Unit
    ) :
        AdditionalText(icon, iconCustomColor, styleHolder) {

        private val counter = SbisCounterDrawable(context).also { counter ->
            counter.style = counterStyle
            counterValue?.let { counter.count = it.value }
        }

        override val textLayout = TextLayout {
            paint.color = iconCustomColor ?: styleHolder.iconColor
            paint.textSize = customSize ?: styleHolder.iconSize
            paint.typeface = styleHolder.iconFont
            this.text = icon
        }

        private var counterDx = 0f
        private var counterDy = 0f

        override fun onAttachedToWindow(scope: CoroutineScope?) {
            scope?.launch {
                counterValue?.collect {
                    val oldCounter = counter.count
                    counter.count = it
                    onEmitNewValue(isCounterSizeChanged(oldCounter, it))
                }
            }
        }

        override fun onMeasure(left: Int, height: Int) {
            textLayout.layout(left, height - textLayout.height)
            counterDx = left.toFloat() + textLayout.width - (counter.intrinsicWidth / 2.0f) + getCounterStartPadding()
            counterDy = textLayout.top - styleHolder.iconCounterTopPadding.toFloat()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.withTranslation(counterDx, counterDy) {
                counter.draw(this)
            }
        }

        override fun getWidth(): Int {
            return super.getWidth() + (counter.intrinsicWidth / 2) + getCounterStartPadding()
        }

        override fun getHeight(): Int {
            return super.getHeight() + styleHolder.iconCounterTopPadding
        }

        private fun getCounterStartPadding() =
            if (isCounterHasMoreOneDigit(counter)) styleHolder.iconCounterStartPadding else 0
    }

    /** Элемент счетчика вкладки. */
    internal class Counter(
        context: Context,
        private val accentedCounter: StateFlow<Int>?,
        private val unaccentedCounter: StateFlow<Int>?,
        private val styleHolder: SbisTabItemStyleHolder,
        private val onEmitNewValue: (Boolean) -> Unit
    ) : ContentView() {
        private val counter = SbisTextCounterDrawable(context).also { counter ->
            accentedCounter?.let { counter.accentedCounter = it.value }
            unaccentedCounter?.let { counter.unaccentedCounter = it.value }
            counter.setCounterSize(styleHolder.counterSize)
        }

        private var counterDx = 0f
        private var counterDy = 0f

        override fun onAttachedToWindow(scope: CoroutineScope?) {
            scope?.launch {
                accentedCounter?.collect {
                    val oldCounter = counter.accentedCounter
                    counter.accentedCounter = it
                    onEmitNewValue(isCounterSizeChanged(oldCounter, it))
                }
            }

            scope?.launch {
                unaccentedCounter?.collect {
                    val oldCounter = counter.unaccentedCounter
                    counter.unaccentedCounter = it
                    onEmitNewValue(isCounterSizeChanged(oldCounter, it))
                }
            }
        }

        override fun onMeasure(left: Int, height: Int) {
            counterDx = left.toFloat()
            counterDy = (height - counter.intrinsicHeight) / 2.0f
        }

        override fun onDraw(canvas: Canvas) {
            canvas.withTranslation(counterDx, counterDy) {
                counter.draw(this)
            }
        }

        override fun getWidth() = counter.intrinsicWidth
        override fun getHeight() = counter.intrinsicHeight

        override fun getContentDescription() =
            "${this.javaClass.simpleName}: Accented: ${counter.accentedCounter}, " +
                "Unaccented: ${counter.unaccentedCounter}"
    }

    /** Элемент изображения вкладки. */
    internal class Image(private val image: Drawable, private val styleHolder: SbisTabItemStyleHolder) : ContentView() {

        override fun onMeasure(left: Int, height: Int) {
            val imageDy = ((height - styleHolder.imageSize) / 2.0f).toInt()
            image.setBounds(left, imageDy, left + styleHolder.imageSize, imageDy + styleHolder.imageSize)
        }

        override fun onDraw(canvas: Canvas) {
            image.draw(canvas)
        }

        override fun getWidth() = styleHolder.imageSize
        override fun getHeight() = styleHolder.imageSize

        override fun getContentDescription(): String = this.javaClass.simpleName
    }

    companion object {
        private fun isCounterSizeChanged(oldCounter: Int, newCounter: Int) =
            (oldCounter.toString().length != newCounter.toString().length) ||
                (oldCounter == 0 && newCounter != 0) ||
                (newCounter == 0 && oldCounter != 0)

        private fun isCounterHasMoreOneDigit(counter: SbisCounterDrawable) = counter.count.toString().length > 1
    }
}
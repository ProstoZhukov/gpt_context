package ru.tensor.sbis.design_selection.contract.customization.selected.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.children

/**
 * Вспомогательный контейнер для оборачивания дочернего [TextView] строго по ширине текста
 * без лишнего пространства после сокращения.
 *
 * Поведение нативных компонентов и контейнеров не предоставляют такой возможности.
 * https://online.sbis.ru/opendoc.html?guid=df949ca3-c02e-4aa5-b46d-7b384a562c40&client=3
 * Планируется излечение нативной болячки в рамках проекта SbisTextView.
 * https://online.sbis.ru/opendoc.html?guid=2eb2f061-e8fb-48f7-88f0-326352d47f10&client=3
 *
 * @author vv.chekurda
 */
class TextWidthWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val hardWrappedWidth = with((children.first() as TextView)) {
            var maxTextWidth = 0f
            repeat(layout.lineCount) { line ->
                val lineWidth = layout.getLineWidth(line)
                maxTextWidth = lineWidth.coerceAtLeast(maxTextWidth)
            }
            paddingStart + maxTextWidth + paddingEnd
        }.toInt()
        setMeasuredDimension(hardWrappedWidth, measuredHeight)
    }
}
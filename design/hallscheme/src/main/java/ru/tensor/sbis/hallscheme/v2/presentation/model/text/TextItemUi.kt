package ru.tensor.sbis.hallscheme.v2.presentation.model.text

import android.graphics.BitmapShader
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.text.TextItem
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontDecoration
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontStyle
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontWeight
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeTextConfig
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi

/**
 * Класс для отображения произвольного текста на схеме зала.
 * @author aa.gulevskiy
 */
internal class TextItemUi(
    private val textItem: TextItem,
    private val itemDefaultColor: Int,
    private val fontConfig: HallSchemeTextConfig?
) : HallSchemeItemUi(textItem) {

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw(viewGroup, null)
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw3D(viewGroup, pressedShader, unpressedShader, null)
    }

    override fun getView(viewGroup: ViewGroup): View {
        return getTextView(viewGroup)
            .apply { alpha = textItem.opacity }
    }

    override fun get3dView(viewGroup: ViewGroup): View {
        val view = getTextView(viewGroup)
        view.alpha = textItem.opacity
        return view
    }

    private fun getTextView(viewGroup: ViewGroup): View =
        SbisTextView(viewGroup.context).apply {
            x = textItem.rect.left.toFloat()
            y = textItem.rect.top.toFloat()

            setTextSize(TypedValue.COMPLEX_UNIT_DIP, textItem.size.toFloat())

            setTextColor(
                getHallSchemeColor(context, textItem.color) ?: ContextCompat.getColor(context, itemDefaultColor)
            )

            when (fontConfig?.textDecoration) {
                HallSchemeFontDecoration.UNDERLINE -> {
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                }
                HallSchemeFontDecoration.LINE_THROUGH -> {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                HallSchemeFontDecoration.UNDERLINE_LINE_THROUGH -> {
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG or Paint.STRIKE_THRU_TEXT_FLAG
                }
                else -> Unit
            }

            typeface = Typeface.create(
                Typeface.DEFAULT,
                when (fontConfig?.fontWeight to fontConfig?.textStyle) {
                    HallSchemeFontWeight.NORMAL to HallSchemeFontStyle.ITALIC -> Typeface.ITALIC
                    HallSchemeFontWeight.BOLD to HallSchemeFontStyle.NORMAL -> Typeface.BOLD
                    HallSchemeFontWeight.BOLD to HallSchemeFontStyle.ITALIC -> Typeface.BOLD_ITALIC
                    else -> Typeface.NORMAL
                }
            )

            val boundsRect = Rect()
            val longestWord = textItem.name
                ?.split(" ")
                ?.map { it.trim() + "_" }
                ?.maxByOrNull { it.length }
                ?: ""

            paint.getTextBounds(longestWord, 0, longestWord.length, boundsRect)
            val measuredWidth = boundsRect.width()

            paint.getTextBounds("_", 0, 1, boundsRect)
            val itemWidth = textItem.width + boundsRect.width()

            val width = maxOf(measuredWidth, itemWidth)
            layoutParams = RelativeLayout.LayoutParams(width, WRAP_CONTENT).apply {
                setMargins(
                    textItem.margin, textItem.margin,
                    textItem.margin, textItem.margin
                )
            }

            text = textItem.name
        }
}
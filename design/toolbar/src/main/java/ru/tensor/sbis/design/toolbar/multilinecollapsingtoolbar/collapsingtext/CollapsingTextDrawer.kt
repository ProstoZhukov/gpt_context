package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withSave
import kotlin.math.min

private const val DEBUG_DRAW = false
private const val ALPHA_OPAQUE = 255

/**
 * Выполняет отрисовку текста в графической шапке.
 *
 * @author us.bessonov
 */
internal class CollapsingTextDrawer {

    var expandedFirstLineDrawX = 0f
    var collapsedTextBlend = 0f
    var expandedTextBlend = 0f
    var textToDrawCollapsed: CharSequence = ""
    var titleScale = 0f

    fun draw(
        canvas: Canvas,
        currentState: CollapsingTextState,
        titleConfig: TitleConfig,
        subtitleConfig: TitleConfig,
        rightSubtitleConfig: TitleConfig
    ) {
        val titleSaveCount = canvas.save()
        if (titleConfig.textToDraw.isNullOrBlank()) return

        val titleX = currentState.titleState.x
        val titleY = currentState.titleState.y
        val titleLayout = titleConfig.layout!!
        val titleState = currentState.titleState

        // Update the TextPaint to the current text size
        titleConfig.paint.textSize = titleState.size
        subtitleConfig.paint.textSize = currentState.subtitleState.size
        rightSubtitleConfig.paint.textSize = currentState.rightSubtitleState.size

        val titleAscent = titleConfig.paint.ascent() * titleScale
        DEBUG_DRAW_PAINT?.let {
            // Just a debug tool, which drawn a magenta rect in the text bounds
            canvas.drawRect(
                currentState.bounds.left, titleY, currentState.bounds.right,
                titleY + titleLayout.height * titleScale,
                it
            )
        }
        if (!subtitleConfig.text.isNullOrBlank()) {
            canvas.withSave {
                translate(currentState.subtitleState.x, currentState.subtitleState.y)
                setTitlePaintAlpha(subtitleConfig, titleState.shadow, expandedTextBlend, expandedTextBlend)
                subtitleConfig.layout!!.draw(canvas)
            }
        }

        if (!rightSubtitleConfig.text.isNullOrBlank()) {
            canvas.withSave {
                canvas.translate(currentState.rightSubtitleState.x, currentState.rightSubtitleState.y)
                setTitlePaintAlpha(
                    rightSubtitleConfig,
                    currentState.rightSubtitleState.shadow,
                    expandedTextBlend,
                    expandedTextBlend
                )
                rightSubtitleConfig.layout!!.draw(canvas)
            }
        }

        if (titleScale != 1f) {
            canvas.scale(titleScale, titleScale, titleX, titleY)
        }

        // Compute where to draw mTitle.layout for this frame
        val currentExpandedX: Float = titleState.x + titleLayout.getLineLeft(0) - expandedFirstLineDrawX * 2
        // positon expanded text appropriately
        canvas.translate(currentExpandedX, titleY)
        // Expanded text
        setTitlePaintAlpha(titleConfig, titleState.shadow, expandedTextBlend, expandedTextBlend)
        titleLayout.draw(canvas)

        // position the overlays
        canvas.translate(titleX - currentExpandedX, 0f)

        // Collapsed text
        if (collapsedTextBlend <= expandedTextBlend) {
            setTitlePaintAlpha(titleConfig, titleState.shadow, collapsedTextBlend, collapsedTextBlend)
        } else {
            setTitlePaintAlpha(titleConfig, titleState.shadow, collapsedTextBlend, expandedTextBlend)
        }
        canvas.drawText(
            textToDrawCollapsed,
            0,
            textToDrawCollapsed.length,
            0f,
            -titleAscent / titleScale,
            titleConfig.paint
        )

        // Remove ellipsis for Cross-section animation
        var tmp: String = textToDrawCollapsed.toString()
        if (tmp.endsWith("\u2026")) {
            tmp = tmp.substring(0, tmp.length - 1)
        }
        // Cross-section between both texts (should stay at alpha = 255)
        titleConfig.paint.alpha = ALPHA_OPAQUE
        titleConfig.paint.clearShadowLayer()
        canvas.drawText(
            tmp,
            0,
            min(titleLayout.getLineEnd(0), tmp.length),
            0f,
            -titleAscent / titleScale,
            titleConfig.paint
        )

        canvas.restoreToCount(titleSaveCount)
    }

    private fun setTitlePaintAlpha(
        config: TitleConfig,
        currentShadow: TitleShadow,
        alphaFraction: Float,
        shadowAlphaFraction: Float
    ) {
        setPaintAlpha(
            config.paint, alphaFraction, shadowAlphaFraction, currentShadow.color,
            currentShadow.radius, currentShadow.dx, currentShadow.dy
        )
    }

    private fun setPaintAlpha(
        paint: Paint,
        alphaFraction: Float,
        shadowAlphaFraction: Float,
        shadowColor: Int,
        shadowRadius: Float,
        shadowDx: Float,
        shadowDy: Float
    ) {
        val shadowColorWithAlpha =
            ColorUtils.setAlphaComponent(shadowColor, (shadowAlphaFraction * ALPHA_OPAQUE).toInt())
        paint.alpha = (alphaFraction * ALPHA_OPAQUE).toInt()
        paint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColorWithAlpha)
    }
}

private val DEBUG_DRAW_PAINT = if (DEBUG_DRAW) {
    Paint().apply {
        isAntiAlias = true
        color = Color.MAGENTA
    }
} else {
    null
}
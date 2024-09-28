package ru.tensor.sbis.hallscheme.v2.presentation.model.bars

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarRounded
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения полукруглого бара.
 * @author aa.gulevskiy
 */
internal class BarRoundedUi(
    private val bar: BarRounded,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : BarUi(bar, drawablesHolder, tableConfig, color) {

    private var startAngleOfLargeArc = 0F
    private var sweepAngleOfLargeArc = 0F

    override fun constructMainLayerPath() {
        with(mainLayerPath) {
            drawBottomPath()
            drawRightCorner()
            drawTopPath()
            drawLeftCorner()
            close()
        }
    }

    private fun Path.drawBottomPath() {
        val largeArcInfo = bar.getMainLayerLargeArcInfo()
        val largeArcRectF = with(largeArcInfo.schemeItemBoundsF) {
            RectF(
                left,
                top,
                right,
                bottom - bar.occupationOffset.bottom
            )
        }

        startAngleOfLargeArc = largeArcInfo.startAngle
        sweepAngleOfLargeArc = largeArcInfo.sweepAngle

        arcTo(largeArcRectF, startAngleOfLargeArc, sweepAngleOfLargeArc)
        largeArcRectF.offset(bar.addedLength.toFloat(), 0F)
        arcTo(largeArcRectF, 90F, sweepAngleOfLargeArc)
    }

    private fun Path.drawRightCorner() {
        cornerCircleRect.offsetTo(
            bar.rect.width - bar.tablePadding.right - bar.tableCornerRadius * 2F + bar.occupationOffset.right,
            bar.tablePadding.top.toFloat() - bar.occupationOffset.vertical
        )
        arcTo(cornerCircleRect, 0F, -90F)
    }

    private fun Path.drawTopPath() {
        val smallArcInfo = bar.getMainLayerSmallArcInfo()
        val smallArcRectF = with(smallArcInfo.schemeItemBoundsF) {
            RectF(left, top, right, bottom)
        }
        val startAngleOfSmallArc = smallArcInfo.startAngle
        val sweepAngleOfSmallCircle = smallArcInfo.sweepAngle

        smallArcRectF.offset(
            bar.addedLength.toFloat(),
            -bar.occupationOffset.vertical.toFloat()
        )
        arcTo(smallArcRectF, startAngleOfSmallArc, sweepAngleOfSmallCircle)

        smallArcRectF.offset(-bar.addedLength.toFloat(), 0F)
        arcTo(smallArcRectF, 90F, sweepAngleOfSmallCircle)
    }

    private fun Path.drawLeftCorner() {
        cornerCircleRect.offsetTo(
            bar.tablePadding.left.toFloat() - bar.occupationOffset.left,
            bar.tablePadding.top.toFloat() - bar.occupationOffset.vertical.toFloat()
        )
        arcTo(cornerCircleRect, 270F, -90F)
    }

    override fun constructBottomDepthLayerPath() {
        val largeArcInfo = bar.getMainLayerLargeArcInfo()
        val largeArcRectF = with(largeArcInfo.schemeItemBoundsF) {
            RectF(
                left,
                top,
                right,
                bottom - bar.occupationOffset.bottom
            )
        }

        val arcInfo = bar.getDepthArcInfo()
        val depthLayerRectF = with(arcInfo.schemeItemBoundsF) {
            RectF(
                left,
                top,
                right,
                bottom - bar.occupationOffset.bottom
            )
        }

        with(depthLayerPath) {
            moveTo(
                bar.tablePadding.left.toFloat() - bar.occupationOffset.left,
                bar.tablePadding.top.toFloat() - bar.occupationOffset.top
            )
            drawDepthLayerBottomArc(largeArcRectF, depthLayerRectF)
            drawDepthLayerTopArc(depthLayerRectF, arcInfo.startAngle, arcInfo.sweepAngle)
            close()
        }
    }

    private fun Path.drawDepthLayerBottomArc(largeRectF: RectF, depthLayerRectF: RectF) {
        arcTo(largeRectF, startAngleOfLargeArc, sweepAngleOfLargeArc)
        largeRectF.offset(bar.addedLength.toFloat(), 0F)
        depthLayerRectF.offset(bar.addedLength.toFloat(), 0F)
        arcTo(largeRectF, 90F, sweepAngleOfLargeArc)
    }

    private fun Path.drawDepthLayerTopArc(
        depthLayerRect: RectF,
        startAngleOfDepthArc: Float,
        sweepAngleOfDepthCircle: Float
    ) {
        arcTo(depthLayerRect, startAngleOfDepthArc, sweepAngleOfDepthCircle)
        depthLayerRect.offset(-bar.addedLength.toFloat(), 0F)
        arcTo(depthLayerRect, 90F, sweepAngleOfDepthCircle)
    }

    override fun drawFlatChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = bar.getChairType(number)
            val drawable = drawablesHolder.getChairFlatDrawable(chairType, color)

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = false)
                canvas.save()
                if (number <= 4) {
                    val horizontalOffset = if (number > 2) bar.addedLength else -bar.addedLength
                    canvas.rotate(
                        bar.getChairAngle(number),
                        bar.tablePadding.left + (bar.rect.width - bar.tablePadding.horizontal + horizontalOffset) / 2F,
                        -bar.tablePadding.bottom.toFloat()
                    )
                }
                drawable.draw(canvas)
                canvas.restore()
            }
        }
    }

    override fun draw3dChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = bar.getChairType(number)
            val drawable = drawablesHolder.chair3dDrawablesMap[chairType]

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = true)
                canvas.save()
                if (number <= 4) {
                    val horizontalOffset = if (number > 2) bar.addedLength else -bar.addedLength
                    canvas.rotate(
                        bar.getChairAngle(number),
                        bar.tablePadding.left + (bar.rect.width - bar.tablePadding.horizontal + horizontalOffset) / 2F,
                        -bar.tablePadding.bottom.toFloat()
                    )
                }
                drawable.draw(canvas)
                canvas.restore()
            }
        }
    }
}
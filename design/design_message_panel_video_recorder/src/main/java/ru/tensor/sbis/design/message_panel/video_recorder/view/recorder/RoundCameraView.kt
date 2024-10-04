package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.setPadding
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children.CameraFlickerStubView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children.RoundProgressView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraApi
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.RoundCameraController
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Компонент для записи круглых видео и показа изображения с камеры.
 * Не включает в себя какие либо элементы управления записью.
 * @see RoundCameraApi
 *
 * @author vv.chekurda
 */
internal class RoundCameraView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    val controller: RoundCameraController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    RoundCameraApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttr, defStyleRes, RoundCameraController())

    private val textureView = TextureView(context, attrs)
        .apply {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val minDirection = minOf(view.measuredWidth, view.measuredHeight)
                    outline.setOval(0, 0, minDirection, minDirection)
                }
            }
            clipToOutline = true
        }
    private val progressView = RoundProgressView(context).apply { setPadding(Offset.XS.getDimenPx(context)) }
    private val cameraStubView = CameraFlickerStubView(context)

    init {
        addView(progressView)
        addView(cameraStubView)
        controller.attachViews(this, textureView, progressView, cameraStubView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpecUtils.measureDirection(widthMeasureSpec) { suggestedMinimumWidth }
        val height = MeasureSpecUtils.measureDirection(heightMeasureSpec) { suggestedMinimumHeight }

        val minChildSide = minOf(
            width - paddingStart - paddingEnd,
            height - paddingTop - paddingBottom
        )
        val childMeasureSpec = MeasureSpecUtils.makeExactlySpec(minChildSide)
        progressView.measure(childMeasureSpec, childMeasureSpec)
        textureView.measure(childMeasureSpec, childMeasureSpec)
        cameraStubView.measure(childMeasureSpec, childMeasureSpec)

        val minDirection = minOf(width, height)
        setMeasuredDimension(minDirection, minDirection)
    }

    override fun getSuggestedMinimumWidth(): Int =
        super.getSuggestedMinimumWidth() + paddingStart + paddingEnd

    override fun getSuggestedMinimumHeight(): Int =
        super.getSuggestedMinimumHeight() + paddingTop + paddingBottom
}
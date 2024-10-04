package ru.tensor.sbis.design.toolbar.util.collapsingimage

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.PathParser
import com.google.android.material.math.MathUtils.lerp
import ru.tensor.sbis.design.profile.imageview.drawer.ShapedDrawer
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile.titleview.utils.SbisAppBarTitleViewHelper
import ru.tensor.sbis.design.profile.util.clippath.DynamicClipPath
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.behavior.APP_BAR_MEDIATE_SNAP_OFFSET_POSITION
import ru.tensor.sbis.design.toolbar.appbar.offset.NormalOffsetObserver
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationInfo
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.updateValue
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.R as DesignR

private const val SUPER_ELLIPSE_FRACTION_THRESHOLD = 0.95f
internal const val MEDIATE_APPBAR_OFFSET_THRESHOLD = 0.6f
internal const val ANIMATION_DURATION = 150L
private const val SCALE_FULL = 1f

/**
 * Управляет анимацией изменения формы и позиционированием [View] в графической шапке, в зависимости от степени её
 * разворота.
 * В развёрнутом состоянии [View] имеет форму квадрата и занимает всю ширину. В свёрнутом состоянии [View] имеет форму
 * суперэллипса, и её расположение совпадает с расположением изображения в [SbisTitleView].
 * По достижении порогового значения разворота графической шапки ([MEDIATE_APPBAR_OFFSET_THRESHOLD]), происходит
 * преобразование формы и размера, из квадрата в суперэллипс, либо наоборот.
 *
 * @author us.bessonov
 */
internal class CollapsingPersonViewShapedDrawer(
    private val view: View,
    private val titleViewHelper: SbisAppBarTitleViewHelper,
    private val appBar: SbisAppBarLayout,
    private val collapsingToolbar: CollapsingToolbarLayout
) : ShapedDrawer {

    private val square = PathParser.createNodesFromPathData(
        view.resources.getString(R.string.toolbar_square_shape_path)
    )
    private val superEllipse by lazy {
        PathParser.createNodesFromPathData(view.resources.getString(DesignR.string.design_superellipse_shape_path))
    }

    private val result by lazy { PathParser.deepCopyNodes(square) }

    private val path = Path().apply {
        PathParser.PathDataNode.nodesToPath(square, this)
    }

    private val mediateImageSize = view.resources.getDimensionPixelSize(R.dimen.toolbar_mediate_image_size)
    private val collapsedImageSize = view.resources.getDimensionPixelSize(R.dimen.toolbar_collapsed_image_size)
    private val mediateImageMargin = view.resources.getDimensionPixelSize(R.dimen.toolbar_mediate_image_margin_start)
    private val collapsedImageMargin =
        view.resources.getDimensionPixelSize(R.dimen.toolbar_collapsed_image_margin_start)

    private var lastOffset = 1f
    private var offsetDelta = 0f

    private var mediateTranslation = 0f

    private var scaleAnimationInfo = AnimationInfo()
    private var translationXAnimationInfo = AnimationInfo()
    private var translationYAnimationInfo = AnimationInfo()

    private var toSuperEllipse = true

    private val clipPath = DynamicClipPath().apply {
        setPath(path)
        initClippingView(view)
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f)
        .setDuration(ANIMATION_DURATION)
        .apply {
            addUpdateListener {
                updateViewParameters()
            }
        }

    var collapsingImageStateListener: CollapsingImageStateListener? = null

    init {
        appBar.addOffsetObserver(object : NormalOffsetObserver {
            override fun onOffsetChanged(position: Float) = updateViewPosition(position)
        })
    }

    override fun onDraw(drawable: Drawable?, canvas: Canvas) {
        drawable?.draw(canvas)
        clipPath.drawPath(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        clipPath.setupClearPath(w, h)
    }

    override fun setBackgroundColor(color: Int) = Unit

    override fun invalidate() = Unit

    override fun setShape(shape: Drawable) = Unit

    private fun updateViewPosition(appBarOffset: Float) {
        offsetDelta = appBarOffset - lastOffset
        val hadOffsetLessThanThresholdOrEqual = lastOffset <= MEDIATE_APPBAR_OFFSET_THRESHOLD
        lastOffset = appBarOffset
        if (appBarOffset > MEDIATE_APPBAR_OFFSET_THRESHOLD && hadOffsetLessThanThresholdOrEqual) {
            toSquare()
        } else if (appBarOffset <= MEDIATE_APPBAR_OFFSET_THRESHOLD && !hadOffsetLessThanThresholdOrEqual) {
            toSuperEllipse()
        } else {
            updateViewParameters()
        }
    }

    private fun updateViewParameters() {
        val isAnimating = animator.isRunning

        if (isAnimating) updatePath()

        val scale = updateCurrentValue(scaleAnimationInfo, ::getScale) { view.scaleX }
        view.apply {
            scaleX = scale
            scaleY = scale

            translationX = updateCurrentValue(translationXAnimationInfo, ::getTranslationX) { view.translationX }
            translationY = updateCurrentValue(translationYAnimationInfo, ::getTranslationY) { view.translationY }

            invalidate()
        }

        collapsingImageStateListener?.onStateChanged(
            when {
                view.scaleX == SCALE_FULL || lastOffset == 0f -> Settled
                isAnimating && toSuperEllipse -> {
                    val rect = getImageRect()
                    Collapsing(
                        animator.animatedFraction,
                        mediateImageMargin + mediateImageSize.toFloat(),
                        rect.top,
                        rect.bottom
                    )
                }

                isAnimating -> Expanding(animator.animatedFraction)
                else -> {
                    val rect = getImageRect()
                    Collapsed(rect.right, rect.top, rect.bottom)
                }
            }
        )
    }

    private fun getImageRect() = RectF().apply {
        top = view.top + view.translationY
        left = view.left + view.translationX - view.width * (1 - view.scaleX) / 2f
        bottom = top + view.height
        right = left + view.width
    }

    private fun getExpectedImageTopInMediateSnap() = getExpectedViewPosition(APP_BAR_MEDIATE_SNAP_OFFSET_POSITION)

    private fun updatePath() {
        PathParser.interpolatePathDataNodes(result, square, superEllipse, animator.animatedFraction)
        path.reset()
        PathParser.PathDataNode.nodesToPath(result, path)
        clipPath.setPath(path)
        clipPath.setupClearPath(view.measuredWidth, view.measuredHeight)
    }

    private fun updateCurrentValue(
        animationInfo: AnimationInfo,
        getTargetValue: (appBarOffset: Float) -> Float,
        getCurrentValue: () -> Float
    ): Float = updateValue(
        animator.isRunning,
        !toSuperEllipse,
        lastOffset,
        animator.animatedFraction,
        animationInfo,
        getTargetValue,
        getCurrentValue
    )

    private fun getScale(appBarOffset: Float) = if (appBarOffset > MEDIATE_APPBAR_OFFSET_THRESHOLD) {
        SCALE_FULL
    } else {
        lerp(
            collapsedImageSize.toFloat() / view.measuredWidth,
            mediateImageSize.toFloat() / view.measuredWidth,
            appBarOffset / MEDIATE_APPBAR_OFFSET_THRESHOLD
        )
    }

    private fun getTranslationX(appBarOffset: Float) = if (appBarOffset > MEDIATE_APPBAR_OFFSET_THRESHOLD) {
        0f
    } else {
        val collapsedTranslation = (view.measuredWidth - collapsedImageSize) * 0.5f - collapsedImageMargin
        val mediateTranslation = (view.measuredWidth - mediateImageSize) * 0.5f - mediateImageMargin
        val fraction = appBarOffset / MEDIATE_APPBAR_OFFSET_THRESHOLD
        -lerp(collapsedTranslation, mediateTranslation, fraction)
    }

    private fun getTranslationY(appBarOffset: Float) = if (appBarOffset > MEDIATE_APPBAR_OFFSET_THRESHOLD) {
        0f
    } else {
        val fraction = appBarOffset / MEDIATE_APPBAR_OFFSET_THRESHOLD
        val statusBarHeight = getStatusBarHeight()
        val collapsedHeight = getCollapsedAppBarHeight()
        val expectedAppBarPosition = getExpectedAppBarPosition(MEDIATE_APPBAR_OFFSET_THRESHOLD)
        val expectedVisibleHeight = appBar.height + expectedAppBarPosition
        val mediateTranslation = expectedVisibleHeight * 0.5f -
            (getExpectedViewPosition(MEDIATE_APPBAR_OFFSET_THRESHOLD) + view.height * 0.5f)
        val collapsedTranslation = (statusBarHeight + titleViewHelper.getImageTop()) -
            (collapsedHeight - collapsedImageSize) * 0.5f
        lerp(collapsedTranslation, mediateTranslation, fraction)
    }

    private fun getStatusBarHeight() = StatusBarHelper.getStatusBarHeight(view.context).toFloat()

    private fun getCollapsedAppBarHeight() = appBar.minimumHeight + getStatusBarHeight()

    private fun getExpectedAppBarPosition(offset: Float) = lerp(
        -(appBar.height.toFloat() - getCollapsedAppBarHeight()),
        0f,
        offset
    )

    private fun getExpectedViewPosition(offset: Float) = getExpectedAppBarPosition(offset) -
        collapsingToolbar.calculateParallaxViewOffset(view, offset)

    private fun toSquare() {
        toSuperEllipse = false

        scaleAnimationInfo = AnimationInfo(
            startValue = SCALE_FULL,
            targetValue = view.scaleX
        )
        translationXAnimationInfo = AnimationInfo(targetValue = view.translationX)
        translationYAnimationInfo = AnimationInfo(targetValue = view.translationY)

        animator.reverse()
    }

    private fun toSuperEllipse() {
        toSuperEllipse = true

        scaleAnimationInfo = AnimationInfo(startValue = SCALE_FULL)
        translationXAnimationInfo = AnimationInfo()
        translationYAnimationInfo = AnimationInfo()

        animator.start()
    }
}
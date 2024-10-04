package ru.tensor.sbis.design.skeleton_view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.skeleton_view.mask.SkeletonMask
import ru.tensor.sbis.design.skeleton_view.mask.SkeletonMaskFactory
import ru.tensor.sbis.design.skeleton_view.mask.SkeletonShimmerDirection
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Layout, позволяющий работать со view как со Skeleton
 *
 * @param context
 * @param attrs атрибуты
 * @param defStyleAttr стили
 * @param originView view для оборачивания в SkeletonView
 *
 * @author us.merzlikina
 */
class SkeletonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.skeletonViewTheme,
    @StyleRes defStyleRes: Int = R.style.SkeletonViewDefaultTheme,
    originView: View? = null,
    private val config: SkeletonConfig = SkeletonConfig.default(context)
) : FrameLayout(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr),
    Skeleton,
    SkeletonStyle by config {

    internal constructor(
        originView: View,
        config: SkeletonConfig
    ) : this(originView.context, null, R.attr.skeletonViewTheme, R.style.SkeletonViewDefaultTheme, originView, config)

    private var mask: SkeletonMask? = null
    private var isSkeleton: Boolean = false
    private var isRendered: Boolean = false

    init {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.SkeletonViewStyle, defStyleAttr, defStyleRes)
            this.maskColor = typedArray.getColor(R.styleable.SkeletonViewStyle_SkeletonView_mask_color, maskColor)
            this.maskCornerRadius = typedArray.getDimensionPixelSize(
                R.styleable.SkeletonViewStyle_SkeletonView_mask_corner_radius,
                maskCornerRadius.toInt()
            ).toFloat()
            showShimmer = typedArray.getBoolean(R.styleable.SkeletonViewStyle_SkeletonView_show_shimmer, showShimmer)
            shimmerColor = typedArray.getColor(R.styleable.SkeletonViewStyle_SkeletonView_shimmer_сolor, shimmerColor)
            shimmerDuration = typedArray
                .getInt(R.styleable.SkeletonViewStyle_SkeletonView_shimmer_duration, shimmerDuration.toInt()).toLong()
            shimmerDirection = SkeletonShimmerDirection.valueOf(
                typedArray.getInt(
                    R.styleable.SkeletonViewStyle_SkeletonView_shimmer_direction,
                    shimmerDirection.ordinal
                )
            ) ?: DEFAULT_SHIMMER_DIRECTION
            typedArray.recycle()
        }
        config.addValueObserver(::invalidateMask)
        originView?.let(::addView)
    }

    override fun hideSkeleton() {
        isSkeleton = false

        if (childCount > 0) {
            views().forEach { it.visibility = View.VISIBLE }
            mask?.stop()
            mask = null
        }
    }

    override fun showSkeleton() {
        isSkeleton = true

        if (isRendered) {
            if (childCount > 0) {
                views().forEach { it.visibility = View.INVISIBLE }
                setWillNotDraw(false)
                invalidateMask()
                mask?.invalidate()

            } else {
                Timber.i("No views to mask")
            }
        }
    }

    override fun isSkeletonActive(): Boolean = isSkeleton

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isRendered = true

        if (isSkeleton) {
            showSkeleton()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        mask?.invalidate()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        when (hasWindowFocus) {
            true -> mask?.start()
            false -> mask?.stop()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isRendered) {
            invalidateMask()
            mask?.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mask?.stop()
    }

    override fun onDraw(canvas: Canvas) {
        mask?.draw(canvas)
    }

    private fun invalidateMask() {
        if (isRendered) {
            mask?.stop()
            mask = null
            if (isSkeleton) {
                if (width > 0 && height > 0) {
                    mask = SkeletonMaskFactory
                        .createMask(this, config)
                        .also { mask -> mask.mask(this, maskCornerRadius) }
                } else {
                    Timber.e("Failed to mask view with invalid width and height")
                }
            }
        } else {
            Timber.e("Skipping invalidation until view is rendered")
        }
    }

    companion object {
        val DEFAULT_MASK_COLOR = RDesign.color.palette_alpha_color_black1
        const val DEFAULT_MASK_CORNER_RADIUS = 4f
        const val DEFAULT_SHIMMER_SHOW = true
        val DEFAULT_SHIMMER_COLOR = RDesign.color.palette_alpha_color_black2
        const val DEFAULT_SHIMMER_DURATION_IN_MILLIS = 2000L
        val DEFAULT_SHIMMER_DIRECTION = SkeletonShimmerDirection.TOP_LEFT_BOTTOM_RIGHT
    }
}

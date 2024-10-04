package ru.tensor.sbis.design.retail_views.shadow_scroll_view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.ShadowScrollViewBinding

private const val FADE_ANIMATION_DURATION = 150
private const val SCROLL_DIRECTION_UP = -1
private const val SCROLL_DIRECTION_DOWN = 1
private const val NO_VALUE = Integer.MIN_VALUE

/**
 * Скроллируемая область с тенью. Тень соответствует спецификации:
 * http://axure.tensor.ru/themes2/%D1%81%D0%BA%D1%80%D0%BE%D0%BB%D0%BB%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D0%B5%D0%B9.html
 * Содержимое области задается с помощью атрибута [R.styleable.RetailViewsShadowScrollView_retail_contentLayoutRes]
 */
class ShadowScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var shadowScrollViewOnAttachStateChangeListener: OnAttachStateChangeListener? = null

    private val binding: ShadowScrollViewBinding =
        ShadowScrollViewBinding.inflate(LayoutInflater.from(getContext()), this)

    init {
        initAttrs(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        shadowScrollViewOnAttachStateChangeListener =
            ShadowScrollViewOnAttachStateChangeListener(binding.scrollView, binding.shadowTop, binding.shadowBottom)
        binding.scrollView.addOnAttachStateChangeListener(shadowScrollViewOnAttachStateChangeListener)
    }

    override fun onDetachedFromWindow() {
        binding.scrollView.removeOnAttachStateChangeListener(shadowScrollViewOnAttachStateChangeListener)
        shadowScrollViewOnAttachStateChangeListener = null
        super.onDetachedFromWindow()
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?) {
        val attrValues = context.obtainStyledAttributes(attrs, R.styleable.RetailViewsShadowScrollViewAttrs)

        val contentLayoutRes = attrValues.getResourceId(
            R.styleable.RetailViewsShadowScrollViewAttrs_retail_views_content_layout_res,
            NO_VALUE
        )
        if (contentLayoutRes != NO_VALUE) {
            addContent(contentLayoutRes)
        }
        attrValues.recycle()
    }

    private fun addContent(@LayoutRes contentLayoutRes: Int) {
        val content = LayoutInflater.from(context).inflate(contentLayoutRes, this, false)

        binding.scrollView.addView(content)
    }
}

private class ShadowScrollViewOnAttachStateChangeListener(
    private val scrollView: ScrollView,
    private val shadowTop: View,
    private val shadowBottom: View
) : View.OnAttachStateChangeListener {

    private var shadowScrollViewOnScrollChangedListener: ShadowScrollViewOnScrollChangedListener? = null

    override fun onViewAttachedToWindow(p0: View) {
        shadowScrollViewOnScrollChangedListener =
            ShadowScrollViewOnScrollChangedListener(scrollView, shadowTop, shadowBottom)
        scrollView.viewTreeObserver.addOnScrollChangedListener(shadowScrollViewOnScrollChangedListener)
    }

    override fun onViewDetachedFromWindow(p0: View) {
        scrollView.viewTreeObserver.removeOnScrollChangedListener(shadowScrollViewOnScrollChangedListener)
        shadowScrollViewOnScrollChangedListener = null
    }
}

private class ShadowScrollViewOnScrollChangedListener(
    private val scrollView: ScrollView,
    private val shadowTop: View,
    private val shadowBottom: View
) : ViewTreeObserver.OnScrollChangedListener {
    override fun onScrollChanged() {
        val canScrollUp = scrollView.canScrollVertically(SCROLL_DIRECTION_UP)
        val canScrollDown = scrollView.canScrollVertically(SCROLL_DIRECTION_DOWN)

        if (!canScrollUp && !canScrollDown) {
            setNoShadow()
        }
        if (!canScrollUp && shadowTop.visibility == View.VISIBLE) {
            hideShadow(shadowTop)
        }
        if (!canScrollDown && shadowBottom.visibility == View.VISIBLE) {
            hideShadow(shadowBottom)
        }
        if (canScrollUp && shadowTop.visibility != View.VISIBLE) {
            showShadow(shadowTop)
        }
        if (canScrollDown && shadowBottom.visibility != View.VISIBLE) {
            showShadow(shadowBottom)
        }
    }

    private fun showShadow(shadowView: View) {
        shadowView.alpha = 0f
        shadowView.visibility = View.VISIBLE
        shadowView.animate()
            .alpha(1f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(null)
    }

    private fun hideShadow(shadowView: View) {
        shadowView.animate()
            .alpha(0f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    shadowView.visibility = View.INVISIBLE
                }
            })
    }

    private fun setNoShadow() {
        shadowTop.visibility = View.INVISIBLE
        shadowBottom.visibility = View.INVISIBLE
    }
}
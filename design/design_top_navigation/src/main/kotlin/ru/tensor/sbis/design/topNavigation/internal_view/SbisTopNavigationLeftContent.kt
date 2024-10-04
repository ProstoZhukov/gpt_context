package ru.tensor.sbis.design.topNavigation.internal_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.topNavigation.util.isVisibleNullable
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setRightPadding
import kotlin.math.max

/**
 * Левый контент шапки.
 *
 * @author da.zolotarev
 */
internal class SbisTopNavigationLeftContent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
    @StyleRes
    defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    internal constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
        @StyleRes
        defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle,
        styleHolder: SbisTopNavigationStyleHolder,
        childViewFactory: ChildViewFactory
    ) : this(context, attrs, defStyleAttr, defStyleRes) {
        leftContentStyleHolder = styleHolder
        childViewFactory.addListener {
            if (it.id == R.id.top_navigation_counter_ct) {
                counterContainer = it
                addView(counterContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
            }
            if (it.id == R.id.top_navigation_btn_back) {
                backBtn = it
                addView(backBtn, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
            }
            if (it.id == R.id.top_navigation_left_custom_content) {
                customContentContainer = it
                addView(customContentContainer)
            }
        }
    }

    private var backBtn: View? = null
    private var customContentContainer: View? = null
    private var leftContentStyleHolder = SbisTopNavigationStyleHolder()

    /**
     * Кликабелен ли остальной контент шапки (влияет на падинг)
     */
    internal var isClickableTopNavContent = false

    /**
     * Контейнер счетчика.
     */
    var counterContainer: View? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentMaxWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewWidth = 0

        viewWidth = fakeMeasureButtonBack(viewWidth)
        viewWidth = measureCustomContent(parentMaxWidth, viewWidth)

        val viewHeight = getMeasuredViewHeight()
        measureButtonBack(viewHeight)

        viewWidth = measureCounter(viewWidth, viewHeight)

        setMeasuredDimension(
            MeasureSpecUtils.makeExactlySpec(
                maxOf(
                    viewWidth,
                    minimumWidth
                )
            ),
            MeasureSpecUtils.makeExactlySpec(viewHeight)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var dx = l
        if (backBtn.isVisibleNullable) {
            dx += leftContentStyleHolder.backBtnLeftPadding
            backBtn?.layout(dx, 0, dx + (backBtn?.measuredWidth ?: 0), 0 + (backBtn?.measuredHeight ?: 0))
            dx += backBtn?.measuredWidth ?: 0
        }
        if (counterContainer.isVisibleNullable && backBtn.isVisibleNullable) {
            counterContainer?.layout(dx, 0)
            dx += counterContainer?.measuredWidth ?: 0
        }
        if (customContentContainer.isVisibleNullable) {
            customContentContainer?.layout(
                dx,
                (measuredHeight - customContentContainer.measuredHeightOrZero) / 2
            )
        }
    }

    private fun fakeMeasureButtonBack(suggestedViewWidth: Int): Int {
        var viewWidth = suggestedViewWidth
        if (backBtn.isVisibleNullable) {
            backBtn?.setRightPadding(
                if (counterContainer.isVisibleNullable) 0 else getBackBtnRightPadding()
            )
            measureChild(
                backBtn,
                MeasureSpecUtils.makeExactlySpec(leftContentStyleHolder.backBtnWidth + (backBtn?.paddingEnd ?: 0)),
                MeasureSpecUtils.makeUnspecifiedSpec()
            )
            viewWidth = leftContentStyleHolder.backBtnLeftPadding + (backBtn?.measuredWidth ?: 0)
        }
        return viewWidth
    }

    private fun getBackBtnRightPadding() =
        if (isClickableTopNavContent) {
            leftContentStyleHolder.backBtnRightPaddingClickableContent
        } else {
            leftContentStyleHolder.backBtnRightPadding
        }

    private fun measureCustomContent(parentMaxWidth: Int, suggestedViewWidth: Int): Int {
        var viewWidth = suggestedViewWidth
        if (customContentContainer.isVisibleNullable) {
            customContentContainer?.measure(
                if (isChildMatchParent()) {
                    MeasureSpecUtils.makeExactlySpec(parentMaxWidth - viewWidth)
                } else {
                    MeasureSpecUtils.makeAtMostSpec(parentMaxWidth - viewWidth)
                },
                MeasureSpecUtils.makeUnspecifiedSpec()
            )
            viewWidth += customContentContainer?.measuredWidth ?: 0
        }
        return viewWidth
    }

    private fun isChildMatchParent() =
        (customContentContainer as? ViewGroup)?.getChildAt(0)?.layoutParams?.width == LayoutParams.MATCH_PARENT

    private fun getMeasuredViewHeight() = if (customContentContainer.isVisibleNullable) {
        max(
            customContentContainer?.measuredHeight ?: 0,
            leftContentStyleHolder.viewHeight
        )
    } else {
        leftContentStyleHolder.viewHeight
    }

    private fun measureButtonBack(viewHeight: Int) {
        if (backBtn.isVisibleNullable) {
            measureChild(
                backBtn,
                MeasureSpecUtils.makeExactlySpec(leftContentStyleHolder.backBtnWidth + (backBtn?.paddingEnd ?: 0)),
                MeasureSpecUtils.makeExactlySpec(viewHeight)
            )
        }
    }

    private fun measureCounter(suggestedViewWidth: Int, suggestedViewHeight: Int): Int {
        var viewWidth = suggestedViewWidth
        if (counterContainer.isVisibleNullable && backBtn.isVisibleNullable) {
            measureChild(
                counterContainer,
                MeasureSpecUtils.makeUnspecifiedSpec(),
                MeasureSpecUtils.makeExactlySpec(suggestedViewHeight)
            )
            viewWidth += (counterContainer?.measuredWidth ?: 0)
        }
        return viewWidth
    }

    private val View?.measuredHeightOrZero: Int
        get() = this?.measuredHeight ?: 0
}
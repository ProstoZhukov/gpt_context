package ru.tensor.sbis.design.topNavigation.internal_view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import ru.tensor.sbis.common.util.illegalArg
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationInternalApi
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterBehavior
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterContent
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem.Companion.DEFAULT_ID
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterPlacement
import ru.tensor.sbis.design.topNavigation.internal_view.footer.SbisTopNavigationSearchFooterItemView
import ru.tensor.sbis.design.topNavigation.internal_view.footer.SbisTopNavigationTabsFooterItemView
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import kotlin.math.abs
import kotlin.math.floor

/**
 * Подвал шапки.
 *
 * @author da.zolotarev
 */
class SbisTopNavigationFooterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
    @StyleRes
    defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle
) : LinearLayout(
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
        styleHolder: SbisTopNavigationStyleHolder = SbisTopNavigationStyleHolder()
    ) : this(context, attrs, defStyleAttr, defStyleRes) {
        footerStyleHolder = styleHolder
    }

    private var footerStyleHolder = SbisTopNavigationStyleHolder()

    @PublishedApi
    internal var modelToView: LinkedHashMap<SbisTopNavigationFooterItem, View> = linkedMapOf()

    private val flingFooterAnimator = ValueAnimator()

    internal val safeMeasuredInBackgroundHeight: Int
        get() {
            return children
                .filter { child ->
                    modelToView
                        .filterValues { child == it }.keys
                        .firstOrNull()?.placement == SbisTopNavigationFooterPlacement.INSIDE_GRAPHIC_BACKGROUND
                }
                .sumOf { it.safeMeasuredHeight }
        }

    @PublishedApi
    internal var items: List<SbisTopNavigationFooterItem> = listOf()
        set(value) {
            field = value
            createFooterViews()
            isVisible = field.isNotEmpty()
            safeRequestLayout()
        }

    init {
        isVisible = false
        orientation = VERTICAL
    }

    /**
     * Получить доступ ко view внутри подвала.
     */
    inline fun <reified T : View> configure(id: String = DEFAULT_ID, view: (T) -> Unit) {
        val footerItemView = modelToView[items.first { it.id == id }] as? T
        footerItemView?.let { view(it) } ?: illegalArg { CONFIGURE_ERROR_TEXT }
    }

    /**
     * Анимировать покадрово подвалы на [dY].
     */
    internal fun animateItemsByYDistance(dY: Int) {
        if (dY == 0) return

        if (isScrollOffsetNotHandleByFooters(dY)) {
            // dY > 0 swipe up
            (parent as? SbisTopNavigationInternalApi)?.animateTopNavigationFolding(dY)
            return
        }
        var offsetLeft = dY
        for (child in children.toList().reversed()) {
            if (modelToView
                    .filterValues { child == it }
                    .keys.firstOrNull()?.behaviour == SbisTopNavigationFooterBehavior.FIXED
            ) {
                continue
            }
            if (offsetLeft > 0 && child.marginTop == -child.measuredHeight) continue
            if (offsetLeft < 0 && child.marginTop == 0) continue
            val margin = (child.marginTop - offsetLeft).coerceIn(-child.measuredHeight..0)
            val usedMargin = margin - child.marginTop
            child.updateTopMargin(margin)
            if (abs(usedMargin) >= abs(offsetLeft)) break
            offsetLeft += usedMargin
        }
    }

    /**
     * Анимировать подвалы по скорости свайпа.
     */
    internal fun animateItemsByYVelocity(velocity: Float, onAnimationEnd: () -> Unit) {
        if (flingFooterAnimator.isRunning) return
        var itemMargin: Int
        val isDown: Boolean?
        if (velocity > 0) {
            // swipe down
            isDown = true
            val currMargin = getFootersMarginSum()
            itemMargin = currMargin

            val distance = -currMargin
            val animTime = distance / velocity

            flingFooterAnimator.setIntValues(currMargin, 0)
            flingFooterAnimator.duration = floor(animTime).toLong()
        } else {
            // swipe up
            isDown = false
            val velocityAbs = abs(velocity)
            val currMargin = getFootersMarginSum()
            itemMargin = currMargin

            val distance = currMargin + getFootersMeasuredHeight()
            val animTime = distance / velocityAbs

            flingFooterAnimator.setIntValues(currMargin, -1 * getFootersMeasuredHeight())
            flingFooterAnimator.duration = floor(animTime).toLong()
        }

        flingFooterAnimator.removeAllUpdateListeners()
        flingFooterAnimator.addUpdateListener {
            if (isDown) {
                val dYOffset = itemMargin - it.animatedValue as Int
                itemMargin = it.animatedValue as Int
                animateItemsByYDistance(dYOffset)
            }

            if (!isDown) {
                val b = itemMargin - it.animatedValue as Int
                itemMargin = it.animatedValue as Int
                animateItemsByYDistance(b)
            }
        }
        flingFooterAnimator.removeAllListeners()
        flingFooterAnimator.addListener(
            onEnd = {
                onAnimationEnd()
            }
        )
        flingFooterAnimator.start()
    }

    /** @SelfDocumented */
    internal fun stopAnimation() {
        if (flingFooterAnimator.isRunning) {
            flingFooterAnimator.removeAllListeners()
            flingFooterAnimator.cancel()
        }
    }

    /**
     * Доанимировать подвалы, если они в промежуточном состоянии.
     */
    internal fun snapFooters() {
        val snapAnimator = ValueAnimator()
        snapAnimator.duration = ANIMATION_DURATION
        val notFullScrolledItem = getNotFullScrolledFooterItem() ?: return

        val currItemMargin = notFullScrolledItem.marginTop
        val currItemMarginAbs = abs(currItemMargin)
        val currItemHeight = notFullScrolledItem.measuredHeight
        val onePercent = currItemHeight.toFloat() / PERCENT_100
        val currPercent = PERCENT_100 - currItemMarginAbs / onePercent
        if (currPercent >= MEDIATE_SNAP_OFFSET_POSITION) {
            // анимируем показ подвала
            snapAnimator.setIntValues(currItemMargin, 0)
        } else {
            // анимируем скрытие подвала
            snapAnimator.setIntValues(currItemMargin, -currItemHeight)
        }
        snapAnimator.addUpdateListener {
            notFullScrolledItem.updateTopMargin(it.animatedValue as Int)
        }
        snapAnimator.start()
    }

    /** @SelfDocumented */
    internal fun getFootersMarginSum() = modelToView
        .filterKeys { it.behaviour != SbisTopNavigationFooterBehavior.FIXED }
        .values
        .sumOf { it.marginTop }

    /** @SelfDocumented */
    internal fun getFootersMeasuredHeight() = modelToView
        .filterKeys { it.behaviour != SbisTopNavigationFooterBehavior.FIXED }
        .values
        .sumOf { it.measuredHeight }

    private fun getNotFullScrolledFooterItem() = modelToView
        .filterKeys { it.behaviour != SbisTopNavigationFooterBehavior.FIXED }
        .filterValues { it.marginTop != 0 && it.marginTop != -it.measuredHeight }
        .values.singleOrNull()

    private fun isScrollOffsetNotHandleByFooters(dY: Int) =
        (dY < 0 && getFootersMarginSum() == 0) || (dY > 0 && getFootersMarginSum() == -getFootersMeasuredHeight())

    private fun createFooterViews() {
        removeAllViews()
        modelToView.clear()
        createViewsOfType(SbisTopNavigationFooterPlacement.INSIDE_GRAPHIC_BACKGROUND)
        createViewsOfType(SbisTopNavigationFooterPlacement.BELOW_GRAPHIC_BACKGROUND)
        setCascadeZOffset()
    }

    private fun setCascadeZOffset() {
        var zOffset = 0f
        children.forEach {
            it.translationZ = zOffset
            zOffset--
        }
    }

    private fun createViewsOfType(filterType: SbisTopNavigationFooterPlacement) {
        items
            .filter { it.placement == filterType }
            .forEach {
                when (it.content) {
                    SbisTopNavigationFooterContent.SearchInput -> {
                        val view = SbisTopNavigationSearchFooterItemView(context, styleHolder = footerStyleHolder)
                        modelToView[it] = view
                        view
                    }

                    is SbisTopNavigationFooterContent.Custom -> {
                        val view = it.content.viewFactory(context)
                        modelToView[it] = view
                        view
                    }

                    is SbisTopNavigationFooterContent.Tabs -> {
                        val view = SbisTopNavigationTabsFooterItemView(context, styleHolder = footerStyleHolder)
                        it.content.configurator(view.tabsView)
                        modelToView[it] = view
                        view
                    }
                }.apply {
                    if (it.behaviour == SbisTopNavigationFooterBehavior.INITIALLY_HIDDEN) {
                        measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
                        addView(this, generateDefaultLayoutParams().apply { topMargin = -measuredHeight })
                    } else {
                        addView(this)
                    }
                }
            }
    }

    companion object {
        /** @SelfDocumented */
        const val CONFIGURE_ERROR_TEXT = "Required id or view type was invalid"
        private const val MEDIATE_SNAP_OFFSET_POSITION = 50f
        private const val ANIMATION_DURATION = 300L
        private const val PERCENT_100 = 100
    }
}
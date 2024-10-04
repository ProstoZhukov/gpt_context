package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.appcompat.R
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.behavior.APP_BAR_MEDIATE_SNAP_OFFSET_POSITION
import ru.tensor.sbis.design.toolbar.appbar.offset.NormalOffsetChangeListener
import ru.tensor.sbis.design.toolbar.appbar.offset.NormalOffsetObserver
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationInfo
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.OnExpandedTitleLineCountChangeListener
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.getTypefaceByFontFilePathForSamsung
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.isSamsungDevice
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.updateValue
import ru.tensor.sbis.design.toolbar.util.collapsingimage.Collapsed
import ru.tensor.sbis.design.toolbar.util.collapsingimage.Collapsing
import ru.tensor.sbis.design.toolbar.util.collapsingimage.CollapsingImageState
import ru.tensor.sbis.design.toolbar.util.collapsingimage.CollapsingImageStateListener
import ru.tensor.sbis.design.toolbar.util.collapsingimage.Expanding
import ru.tensor.sbis.design.toolbar.util.collapsingimage.Settled
import ru.tensor.sbis.design.utils.getExpectedTextWidth
import kotlin.math.abs
import kotlin.math.roundToInt
import ru.tensor.sbis.design.toolbar.R as ToolbarR

private const val DEFAULT_MAX_TITLE_LINES = 3
private const val LINE_SPACING_EXTRA = 0f
private const val LINE_SPACING_MULTIPLIER = 1f
private const val MAX_LINES_WITH_RIGHT_SUBTITLE = 1
private const val MAX_SUBTITLE_LINES = 2
private const val DEFAULT_TITLE_SIZE = 15f

/**
 * Управляет расположением и анимацией текста в графической шапке.
 * Необходимо проинициализировать, используя нужную версию [initAppBar], в зависимости от типа [AppBarLayout].
 *
 * @author us.bessonov
 */
internal class CollapsingTextAnimationController(private val view: CollapsingToolbarLayout) :
    CollapsingImageStateListener {

    private val collapsingTextDrawer = CollapsingTextDrawer()

    @Px
    private val textMarginStart = view.resources.getDimension(ToolbarR.dimen.toolbar_collapsing_title_margin_start)

    private val expandedState = CollapsingTextState()
    private var mediateState: CollapsingTextState? = null
    private val collapsedState = CollapsingTextState()
    private val currentState = CollapsingTextState()

    private var titleAnimationInfoX = AnimationInfo()
    private var titleAnimationInfoY = AnimationInfo()

    private val titleConfig = TitleConfig()
    private val subtitleConfig = TitleConfig()
    private val rightSubtitleConfig = TitleConfig()

    private val collapsedBounds: RectF
        get() = collapsedState.bounds
    private val expandedBounds: RectF
        get() = expandedState.bounds
    private val currentBounds: RectF
        get() = currentState.bounds

    private var boundsChanged = false
    private var expandedTitleLineCount = 0
    private var notifyLineCountChangedAfterCalculation = false
    private var drawTitle = false

    private var fraction = 0f
    private var lastImageState: CollapsingImageState = Settled

    private var onTitleLineCountChangeListener: OnExpandedTitleLineCountChangeListener? = null

    private val rightSubtitle: CharSequence?
        get() = rightSubtitleConfig.text

    private var maxTitleLines = DEFAULT_MAX_TITLE_LINES

    var isSnapMode = false
        set(value) {
            field = value
            updateMediateState()
        }

    @Px
    var rightSubtitleMargin: Int = 0

    var collapsedTitleCenterVertical: Boolean = false
    var collapsedTitleCenterHorizontal: Boolean = false

    val title: CharSequence?
        get() = titleConfig.text

    val subtitle: CharSequence?
        get() = subtitleConfig.text

    init {
        updateMediateState()
    }

    fun initAppBar(appBar: SbisAppBarLayout) {
        appBar.addOffsetObserver(getNormalOffsetObserver())
    }

    fun initAppBar(appBar: AppBarLayout) {
        appBar.addOnOffsetChangedListener(
            NormalOffsetChangeListener(listOf(getNormalOffsetObserver()), appBar::getTotalScrollRange)
        )
    }

    private fun getNormalOffsetObserver() = object : NormalOffsetObserver {
        override fun onOffsetChanged(position: Float) = onOffsetChanged(offset = position)
    }

    fun setExpandedBounds(left: Float, top: Float, right: Float, bottom: Float) {
        if (!rectEquals(expandedState.bounds, left, top, right, bottom)) {
            expandedState.bounds.set(left, top, right, bottom)
            boundsChanged = true
            onBoundsChanged()
        }
    }

    fun setCollapsedBounds(left: Float, top: Float, right: Float, bottom: Float) {
        if (!rectEquals(collapsedState.bounds, left, top, right, bottom)) {
            collapsedState.bounds.set(left, top, right, bottom)
            boundsChanged = true
            onBoundsChanged()
        }
    }

    fun setTitle(title: CharSequence?) {
        if (title != null && title == this.title) {
            return
        }
        titleConfig.text = title ?: ""
        titleConfig.textToDraw = null
        recalculate()
        notifyTitleLineCountChanged()
    }

    /** SelfDocumented  */
    fun setSubtitle(subtitle: CharSequence?) {
        if (subtitle == null || subtitle != this.subtitle) {
            subtitleConfig.text = subtitle ?: ""
            subtitleConfig.textToDraw = null
            recalculate()
        }
    }

    /** SelfDocumented  */
    fun setRightSubtitle(rightSubtitle: CharSequence?) {
        if (rightSubtitle == this.rightSubtitle) return
        rightSubtitleConfig.text = rightSubtitle ?: ""
        rightSubtitleConfig.textToDraw = null
        recalculate()
    }

    fun setOnTitleLineCountChangeListener(listener: OnExpandedTitleLineCountChangeListener) {
        onTitleLineCountChangeListener = listener
        notifyTitleLineCountChanged()
    }

    fun setCollapsedTitleAppearance(@StyleRes resId: Int) {
        setTextAppearance(resId, collapsedState.titleState, titleConfig)
        mediateState?.let { setTextAppearance(resId, it.titleState, titleConfig) }
    }

    fun setExpandedTitleAppearance(@StyleRes resId: Int) {
        setTextAppearance(resId, expandedState.titleState, titleConfig)
    }

    fun setCollapsedTitleColor(@ColorInt color: Int) {
        if (collapsedState.titleState.color != color) {
            collapsedState.titleState.color = color
            recalculate()
        }
    }

    fun setExpandedTitleColor(@ColorInt color: Int) {
        if (expandedState.titleState.color != color) {
            expandedState.titleState.color = color
            recalculate()
        }
    }

    fun setSubtitleAppearance(@StyleRes resId: Int) {
        setTextAppearance(resId, collapsedState.subtitleState, subtitleConfig)
        setTextAppearance(resId, expandedState.subtitleState, subtitleConfig)
        mediateState?.let { setTextAppearance(resId, it.subtitleState, subtitleConfig) }
        currentState.subtitleState.size = expandedState.subtitleState.size
    }

    fun setRightSubtitleAppearance(@StyleRes resId: Int) {
        setTextAppearance(resId, collapsedState.rightSubtitleState, rightSubtitleConfig)
        setTextAppearance(resId, expandedState.rightSubtitleState, rightSubtitleConfig)
        mediateState?.let { setTextAppearance(resId, it.rightSubtitleState, rightSubtitleConfig) }
        currentState.rightSubtitleState.size = expandedState.rightSubtitleState.size
    }

    fun setCollapsedTitleSize(@Px size: Float) {
        collapsedState.titleState.size = size
    }

    /** @SelfDocumented */
    fun setMaxTitleLines(count: Int) {
        if (maxTitleLines != count) {
            maxTitleLines = count
            recalculate()
        }
    }

    fun draw(canvas: Canvas) {
        if (drawTitle) {
            collapsingTextDrawer.draw(canvas, currentState, titleConfig, subtitleConfig, rightSubtitleConfig)
        }
    }

    fun getEstimatedTitleHeightUnscaled(@Px marginStart: Int, @Px marginEnd: Int): Int {
        var availableWidth = expandedBounds.width().roundToInt()
        if (availableWidth == 0) {
            availableWidth = getScreenWidth(view.context) - marginStart - marginEnd
        }
        val oldSize = titleConfig.paint.textSize
        titleConfig.paint.textSize = expandedState.titleState.size

        var layout: StaticLayout = createStaticLayout(title, titleConfig.paint, availableWidth)
        val truncatedText = truncateIfNeeded(
            title ?: "", layout, maxTitleLines,
            availableWidth.toFloat(), titleConfig.paint
        )
        if (truncatedText !== titleConfig.text) {
            layout = createStaticLayout(truncatedText, titleConfig.paint, availableWidth)
        }
        titleConfig.paint.textSize = oldSize
        return layout.height
    }

    fun getTitleHeightUnscaled() = titleConfig.layout?.height ?: 0

    fun recalculate() {
        if (view.height > 0 && view.width > 0) {
            // If we've already been laid out, calculate everything now otherwise we'll wait
            // until a layout
            calculateBaseOffsets()
            calculateCurrentOffsets()
        }
    }

    override fun onStateChanged(state: CollapsingImageState) {
        if (!isSnapMode || state == lastImageState) return
        updateImageState(state)
        calculateCurrentOffsets()
    }

    private fun updateImageState(state: CollapsingImageState) {
        when {
            state is Collapsing && lastImageState !is Collapsing -> {
                // будем анимировать из текущего положения в позицию рядом с изображением, по его вертикальному центру
                titleAnimationInfoX =
                    AnimationInfo(currentState.titleState.x, getTextPositionWithMarginFromImage(state.targetImageEnd))
                titleAnimationInfoY = AnimationInfo(
                    currentState.titleState.y,
                    getTextPositionAtImageVerticalCenter(state.targetImageTop, state.targetImageBottom)
                )
            }

            state is Settled && lastImageState !is Settled -> calculateBaseOffsets() // обычное поведение
            state is Expanding && lastImageState !is Expanding -> {
                // начальное состояние - collapsed, зафиксируем текущую позицию, от которой нужно анимировать
                titleAnimationInfoX =
                    AnimationInfo(currentState.titleState.x, getLerpPositionX(getTitleStates(), fraction))
                titleAnimationInfoY =
                    AnimationInfo(currentState.titleState.y, getLerpPositionY(getTitleStates(), fraction))
            }
        }
        lastImageState = state
    }

    private fun onOffsetChanged(offset: Float) {
        // При реализации логики было принято, что значение 1 соответствует свёрнутому состоянию, а 0 - развёрнутому
        val newFraction = 1 - offset
        if (fraction != newFraction) {
            fraction = newFraction
            calculateCurrentOffsets()
        }
    }

    private fun calculateBaseOffsets() {
        val currentTitleSize = currentState.titleState.size

        calculateUsingTitleSize(collapsedState.titleState.size)
        updateSubtitleLayoutCollapsed()
        boundsChanged = false

        val textToDrawCollapsed = titleConfig.textToDraw ?: ""
        collapsingTextDrawer.textToDrawCollapsed = textToDrawCollapsed
        var rightSubtitleWidth = rightSubtitleConfig.layout?.getLineWidth(0) ?: 0f
        var subtitleHeight = subtitleConfig.layout?.height?.toFloat() ?: 0f
        var rightSubtitleHeight = rightSubtitleConfig.layout?.height?.toFloat() ?: 0f

        with(collapsedState) {
            if (lastImageState is Settled) {
                titleState.x = if (collapsedTitleCenterHorizontal) {
                    val titleWidth = getExpectedTextWidth(textToDrawCollapsed, titleConfig.paint)
                    bounds.centerX() - titleWidth / 2
                } else {
                    bounds.left
                }
                titleState.y = if (collapsedTitleCenterVertical) {
                    val titleHeight = titleConfig.layout?.height?.toFloat() ?: 0f
                    bounds.centerY() - titleHeight / 2
                } else {
                    bounds.top
                }
            }

            subtitleState.y = titleState.y - subtitleHeight
            subtitleState.x = bounds.left

            rightSubtitleState.y = titleState.y - rightSubtitleHeight
            rightSubtitleState.x = expandedBounds.right - rightSubtitleWidth
        }

        calculateUsingTitleSize(expandedState.titleState.size)
        updateSubtitleLayoutExpanded()
        boundsChanged = false

        rightSubtitleWidth = rightSubtitleConfig.layout?.getLineWidth(0) ?: 0f
        collapsingTextDrawer.expandedFirstLineDrawX = titleConfig.layout?.getLineLeft(0) ?: 0f
        val titleHeight = titleConfig.layout?.height?.toFloat() ?: 0f
        subtitleHeight = subtitleConfig.layout?.height?.toFloat() ?: 0f

        rightSubtitleHeight = rightSubtitleConfig.layout?.height?.toFloat() ?: 0f
        with(expandedState) {
            if (lastImageState is Settled) {
                titleState.y = expandedBounds.bottom - titleHeight
                titleState.x = expandedBounds.left
            }

            subtitleState.y = titleState.y - subtitleHeight
            subtitleState.x = expandedBounds.left

            rightSubtitleState.y = titleState.y - rightSubtitleHeight
            rightSubtitleState.x = expandedBounds.right - rightSubtitleWidth
        }

        setInterpolatedTitleSize(currentTitleSize)
    }

    private fun calculateCurrentOffsets() {
        interpolateBounds(currentState, expandedState, collapsedState, fraction)
        updateSubtitleLayout()
        updateCurrentTextX()
        updateCurrentTextY()
        lerpPositionX(getSubtitleStates(), fraction)
        lerpPositionY(getSubtitleStates(), fraction)
        currentState.rightSubtitleState.x = getCurrentRightSubtitleX()
        lerpPositionY(getRightSubtitleStates(), fraction)
        setInterpolatedTitleSize(lerpTitleSize(getTitleStates(), fraction))

        setCollapsedTextBlend(lerpCollapsedTextBlend(isSnapMode, fraction))
        setExpandedTextBlend(lerpExpandedTextBlend(isSnapMode, fraction))
        updateTextColor(titleConfig, getTitleStates(mediateState), fraction)
        lerpShadowValues(getTitleStates(), fraction)
        updateTextColor(subtitleConfig, getSubtitleStates(mediateState), fraction)
        lerpShadowValues(getSubtitleStates(), fraction)
        updateTextColor(rightSubtitleConfig, getRightSubtitleStates(mediateState), fraction)
        lerpShadowValues(getRightSubtitleStates(), fraction)
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private fun updateCurrentTextX() {
        fun updateX(
            animatedFraction: Float,
            getTargetValue: (appBarOffset: Float) -> Float
        ) {
            currentState.titleState.x = updateValue(
                isAnimating = true,
                isReverse = false,
                lastOffset = fraction,
                animatedFraction = animatedFraction,
                animationInfo = titleAnimationInfoX,
                getTargetValue = getTargetValue,
                getCurrentValue = { currentState.titleState.x }
            )
        }

        when (val state = lastImageState) {
            Settled -> lerpPositionX(getTitleStates(), fraction)
            is Collapsed -> currentState.titleState.x = getTextPositionWithMarginFromImage(state.imageEnd)
            is Collapsing -> updateX(
                state.fraction,
                getTargetValue = { getTextPositionWithMarginFromImage(state.targetImageEnd) }
            )

            is Expanding -> updateX(
                1 - state.fraction,
                getTargetValue = { getLerpPositionX(getTitleStates(), fraction) }
            )
        }
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private var maxCollapsingYBeforeSnap = 0f

    private fun updateCurrentTextY() {
        fun updateX(
            animatedFraction: Float,
            getTargetValue: (appBarOffset: Float) -> Float
        ) {
            currentState.titleState.y = updateValue(
                isAnimating = true,
                isReverse = false,
                lastOffset = fraction,
                animatedFraction = animatedFraction,
                animationInfo = titleAnimationInfoY,
                getTargetValue = getTargetValue,
                getCurrentValue = { currentState.titleState.y }
            )
        }

        when (val state = lastImageState) {
            Settled -> lerpPositionY(getTitleStates(), fraction)
            is Collapsed -> {
                if (fraction >= 1 - APP_BAR_MEDIATE_SNAP_OFFSET_POSITION) {
                    val scaledFraction = 1 + (fraction - 1) / APP_BAR_MEDIATE_SNAP_OFFSET_POSITION
                    currentState.titleState.y =
                        lerp(maxCollapsingYBeforeSnap, collapsedState.titleState.y, scaledFraction, null)
                } else {
                    val y = getTextPositionAtImageVerticalCenter(state.imageTop, state.imageBottom)
                    if (y > maxCollapsingYBeforeSnap) maxCollapsingYBeforeSnap = y
                    currentState.titleState.y = y
                }
            }

            is Collapsing -> updateX(
                state.fraction,
                getTargetValue = {
                    getTextPositionAtImageVerticalCenter(
                        state.targetImageTop,
                        state.targetImageBottom
                    )
                }
            )

            is Expanding -> updateX(
                1 - state.fraction,
                getTargetValue = { getLerpPositionY(getTitleStates(), fraction) }
            )
        }
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private fun getTextPositionAtImageVerticalCenter(imageTop: Float, imageBottom: Float): Float {
        val titleHeight = titleConfig.layout?.height ?: 0
        val imageHeight = imageBottom - imageTop
        return (imageTop + (imageHeight - titleHeight) / 2f)
    }

    private fun getTextPositionWithMarginFromImage(imageEnd: Float) = imageEnd + textMarginStart

    private fun updateTextColor(titleConfig: TitleConfig, titleStates: TitleStates, fraction: Float) {
        if (collapsedState.titleState.color != expandedState.titleState.color) {
            // If the collapsed and expanded text colors are different, blend them based on the
            // fraction
            titleConfig.paint.color = blendColors(
                titleStates.expanded.color,
                null,
                titleStates.collapsed.color,
                fraction
            )
        } else {
            titleConfig.paint.color = titleStates.collapsed.color
        }
    }

    private fun setCollapsedTextBlend(blend: Float) {
        collapsingTextDrawer.collapsedTextBlend = blend
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private fun setExpandedTextBlend(blend: Float) {
        collapsingTextDrawer.expandedTextBlend = blend
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private fun getCurrentRightSubtitleX(): Float {
        if (!hasRightSubtitle() || !hasSubtitle() || rightSubtitle == rightSubtitleConfig.textToDraw) {
            return expandedState.rightSubtitleState.x
        }
        val subtitleWidth = subtitleConfig.layout!!.getLineWidth(0)
        return currentState.subtitleState.x + subtitleWidth + rightSubtitleMargin
    }

    private fun updateSubtitleLayoutExpanded() {
        updateSubtitleLayout(expandedBounds.width())
    }

    private fun updateSubtitleLayoutCollapsed() {
        updateSubtitleLayout(collapsedBounds.width())
    }

    private fun updateSubtitleLayout() {
        if (currentState.bounds.isEmpty) {
            updateSubtitleLayout(expandedBounds.width())
        } else {
            updateSubtitleLayout(expandedBounds.right - currentBounds.left)
        }
    }

    private fun updateSubtitleLayout(suggestedAvailableWidth: Float) {
        var availableWidth = suggestedAvailableWidth
        if (!hasSubtitle() && !hasRightSubtitle()) return
        if (hasSubtitle() && hasRightSubtitle()) {
            val subtitleLayout: StaticLayout =
                createSubtitleLayout(subtitleConfig, currentState.subtitleState, Int.MAX_VALUE)
            val rightSubtitleLayout: StaticLayout =
                createSubtitleLayout(rightSubtitleConfig, currentState.rightSubtitleState, Int.MAX_VALUE)
            val desiredSubtitleWidth = subtitleLayout.getLineWidth(0).roundToInt()
            val desiredRightSubtitleWidth = rightSubtitleLayout.getLineWidth(0).roundToInt()
            availableWidth -= rightSubtitleMargin
            val halfAvailableWidth = availableWidth / 2

            val availableSubtitleWidth: Float
            val availableRightSubtitleWidth: Float
            if (desiredSubtitleWidth + desiredRightSubtitleWidth <= availableWidth) {
                availableSubtitleWidth = availableWidth
                availableRightSubtitleWidth = availableWidth
            } else if (desiredRightSubtitleWidth <= halfAvailableWidth) {
                availableSubtitleWidth = availableWidth - desiredRightSubtitleWidth
                availableRightSubtitleWidth = availableWidth
            } else if (desiredSubtitleWidth <= halfAvailableWidth) {
                availableSubtitleWidth = availableWidth
                availableRightSubtitleWidth = availableWidth - desiredSubtitleWidth
            } else {
                availableSubtitleWidth = halfAvailableWidth
                availableRightSubtitleWidth = availableWidth - halfAvailableWidth
            }
            updateSubtitleTextToDrawLayout(
                subtitleConfig, subtitleLayout, availableSubtitleWidth,
                MAX_LINES_WITH_RIGHT_SUBTITLE
            )
            updateSubtitleTextToDrawLayout(
                rightSubtitleConfig, rightSubtitleLayout, availableRightSubtitleWidth,
                MAX_LINES_WITH_RIGHT_SUBTITLE
            )
        } else if (hasSubtitle()) {
            val subtitleLayout: StaticLayout =
                createSubtitleLayout(subtitleConfig, currentState.subtitleState, availableWidth.roundToInt())
            updateSubtitleTextToDrawLayout(
                subtitleConfig, subtitleLayout, availableWidth,
                MAX_SUBTITLE_LINES
            )
        } else {
            val rightSubtitleLayout: StaticLayout =
                createSubtitleLayout(rightSubtitleConfig, currentState.rightSubtitleState, Int.MAX_VALUE)
            updateSubtitleTextToDrawLayout(
                rightSubtitleConfig, rightSubtitleLayout, availableWidth,
                MAX_LINES_WITH_RIGHT_SUBTITLE
            )
        }
    }

    private fun updateSubtitleTextToDrawLayout(
        subtitle: TitleConfig,
        layout: StaticLayout,
        @Px availableWidth: Float,
        maxLines: Int
    ) {
        val truncatedSubtitle: CharSequence = if (maxLines > MAX_LINES_WITH_RIGHT_SUBTITLE) {
            truncateIfNeeded(
                subtitle.text!!, layout, maxLines, availableWidth,
                subtitle.paint
            )
        } else {
            TextUtils.ellipsize(
                subtitle.text, subtitle.paint, availableWidth,
                TextUtils.TruncateAt.END
            )
        }
        if (truncatedSubtitle != subtitle.textToDraw) {
            subtitle.textToDraw = truncatedSubtitle
        }
        subtitle.layout = createStaticLayout(subtitle.textToDraw, subtitle.paint, availableWidth.roundToInt())
    }

    private fun createSubtitleLayout(
        subtitle: TitleConfig,
        currentState: TitleState,
        @Px availableWidth: Int
    ): StaticLayout = with(subtitle) {
        paint.textSize = currentState.size
        paint.typeface = typeface
        return createStaticLayout(text, paint, availableWidth)
    }

    private fun createStaticLayout(text: CharSequence?, paint: TextPaint, availableWidth: Int): StaticLayout {
        return StaticLayout(
            text, paint, availableWidth, Layout.Alignment.ALIGN_NORMAL,
            LINE_SPACING_MULTIPLIER, LINE_SPACING_EXTRA, false
        )
    }

    private fun hasSubtitle() = !subtitle.isNullOrBlank()

    private fun hasRightSubtitle() = !rightSubtitle.isNullOrBlank()

    private fun setInterpolatedTitleSize(textSize: Float) {
        calculateUsingTitleSize(textSize)
        ViewCompat.postInvalidateOnAnimation(view)
    }

    private fun calculateUsingTitleSize(textSize: Float) {
        val title = title?.takeUnless { it.isBlank() } ?: return
        val collapsedWidth: Float = collapsedBounds.width()
        val expandedWidth: Float = expandedBounds.width()
        val availableWidth: Float
        val newTextSize: Float

        val maxLines: Int
        val collapsedTitleSize = collapsedState.titleState.size
        val expandedTitleSize = expandedState.titleState.size
        if (isClose(textSize, collapsedTitleSize)) {
            newTextSize = collapsedTitleSize
            collapsingTextDrawer.titleScale = 1f
            availableWidth = collapsedWidth
            maxLines = 1
        } else {
            newTextSize = expandedTitleSize
            if (isClose(textSize, expandedTitleSize)) {
                // If we're close to the expanded text size, snap to it and use a scale of 1
                collapsingTextDrawer.titleScale = 1f
            } else {
                // Else, we'll scale down from the expanded text size
                collapsingTextDrawer.titleScale = textSize / expandedTitleSize
            }
            val textSizeRatio: Float = collapsedTitleSize / expandedTitleSize
            // This is the size of the expanded bounds when it is scaled to match the
            // collapsed text size
            val scaledDownWidth = expandedWidth * textSizeRatio
            availableWidth = if (scaledDownWidth > collapsedWidth) {
                // If the scaled down size is larger than the actual collapsed width, we need to
                // cap the available width so that when the expanded text scales down, it matches
                // the collapsed width
                expandedWidth
            } else {
                // Otherwise we'll just use the expanded width
                expandedWidth
            }

            maxLines = maxTitleLines
        }

        var updateDrawText = false
        if (availableWidth > 0) {
            val currentTitleSize = currentState.titleState.size
            updateDrawText = currentTitleSize != newTextSize || boundsChanged
            currentState.titleState.size = newTextSize
        }
        if (titleConfig.textToDraw == null || updateDrawText) {
            val titlePaint = titleConfig.paint
            titlePaint.textSize = currentState.titleState.size
            titlePaint.typeface = titleConfig.typeface
            subtitleConfig.paint.typeface = subtitleConfig.typeface
            rightSubtitleConfig.paint.typeface = rightSubtitleConfig.typeface

            // BEGIN MODIFICATION: Text layout creation and text truncation
            val layout = StaticLayout(
                titleConfig.text, titleConfig.paint, availableWidth.toInt(),
                Layout.Alignment.ALIGN_NORMAL, LINE_SPACING_MULTIPLIER, LINE_SPACING_EXTRA, false
            )
            updateExpandedTitleLineCount(layout.lineCount)
            val truncatedText =
                truncateIfNeeded(title, layout, maxLines, availableWidth, titleConfig.paint)
            if (truncatedText != titleConfig.textToDraw) {
                titleConfig.textToDraw = truncatedText
            }

            titleConfig.layout =
                createStaticLayout(titleConfig.textToDraw, titleConfig.paint, availableWidth.roundToInt())
        }
    }

    private fun updateExpandedTitleLineCount(fullTitleLineCount: Int) {
        expandedTitleLineCount = fullTitleLineCount.coerceAtMost(maxTitleLines)
        if (notifyLineCountChangedAfterCalculation) {
            notifyTitleLineCountChanged()
            notifyLineCountChangedAfterCalculation = false
        }
    }

    private fun notifyTitleLineCountChanged() {
        if (onTitleLineCountChangeListener == null) return
        if (expandedTitleLineCount != 0) {
            onTitleLineCountChangeListener!!.onExpandedTitleLineCountChanged(expandedTitleLineCount)
        } else {
            notifyLineCountChangedAfterCalculation = true
        }
    }

    private fun onBoundsChanged() {
        drawTitle = !collapsedState.bounds.isEmpty && !expandedState.bounds.isEmpty
    }

    private fun setTextAppearance(resId: Int, state: TitleState, config: TitleConfig) {
        val a: TypedArray = view.context.obtainStyledAttributes(
            resId,
            R.styleable.TextAppearance
        )
        if (a.hasValue(R.styleable.TextAppearance_android_textColor)) {
            state.color = a.getColorStateList(R.styleable.TextAppearance_android_textColor)?.defaultColor
                ?: Color.MAGENTA
        }
        if (a.hasValue(R.styleable.TextAppearance_android_textSize)) {
            state.size = a.getDimension(
                R.styleable.TextAppearance_android_textSize,
                DEFAULT_TITLE_SIZE
            )
        }
        setShadowAppearance(a, state.shadow)
        a.recycle()
        config.typeface = readFontFamilyTypeface(resId)
    }

    private fun setShadowAppearance(a: TypedArray, shadow: TitleShadow) {
        shadow.color = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0)
        shadow.dx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0f)
        shadow.dy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0f)
        shadow.radius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0f)
    }

    private fun readFontFamilyTypeface(resId: Int): Typeface? {
        val a: TypedArray = view.context.obtainStyledAttributes(resId, intArrayOf(android.R.attr.fontFamily))
        try {
            val family = a.getString(0)
            if (family != null) {
                return if (isSamsungDevice()) {
                    getTypefaceByFontFilePathForSamsung(family, view.context)
                } else {
                    Typeface.create(family, Typeface.NORMAL)
                }
            }
        } finally {
            a.recycle()
        }
        return null
    }

    private fun getTitleStates(mediateState: CollapsingTextState? = null) = TitleStates(
        collapsedState.titleState,
        expandedState.titleState,
        mediateState?.titleState,
        currentState.titleState
    )

    private fun getSubtitleStates(mediateState: CollapsingTextState? = null) = TitleStates(
        collapsedState.subtitleState,
        expandedState.subtitleState,
        mediateState?.subtitleState,
        currentState.subtitleState
    )

    private fun getRightSubtitleStates(mediateState: CollapsingTextState? = null) = TitleStates(
        collapsedState.rightSubtitleState,
        expandedState.rightSubtitleState,
        mediateState?.rightSubtitleState,
        currentState.rightSubtitleState
    )

    private fun updateMediateState() {
        mediateState = if (isSnapMode) CollapsingTextState() else null
    }
}

private fun truncateIfNeeded(
    text: CharSequence,
    layout: Layout,
    maxLines: Int,
    availableWidth: Float,
    paint: TextPaint
): CharSequence {
    return if (layout.lineCount > maxLines) {
        val lastLine = maxLines - 1
        val textBefore = if (lastLine > 0) text.subSequence(0, layout.getLineEnd(lastLine - 1)) else ""
        val lineText: CharSequence = if (maxLines > 1) {
            text.subSequence(layout.getLineStart(lastLine), text.length)
        } else {
            text
        }
        // if the text is too long, truncate it
        val truncatedLineText = TextUtils.ellipsize(
            lineText, paint, availableWidth,
            TextUtils.TruncateAt.END
        )
        TextUtils.concat(textBefore, truncatedLineText)
    } else {
        text
    }
}

private fun rectEquals(r: RectF, left: Float, top: Float, right: Float, bottom: Float): Boolean {
    return !(r.left != left || r.top != top || r.right != right || r.bottom != bottom)
}

private fun isClose(value: Float, targetValue: Float): Boolean {
    return abs(value - targetValue) < 0.001f
}

@Px
private fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val rect = wm.currentWindowMetrics.bounds
        rect.width()
    } else {
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(metrics)
        metrics.widthPixels
    }
    return width
}

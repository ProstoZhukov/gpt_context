package ru.tensor.sbis.design.topNavigation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.design.theme.res.createColor
import ru.tensor.sbis.design.theme.res.createString
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.zen.ZenThemeSupport
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationActionItem
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationIconButtonViewConfigurator
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationImage
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationInternalApi
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationPresentationContext
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationSyncState
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationTitleHandler
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem
import ru.tensor.sbis.design.topNavigation.internal_view.FrameLayoutWithBadge
import ru.tensor.sbis.design.topNavigation.util.AttachedViewGestureDetector
import ru.tensor.sbis.design.topNavigation.util.ButtonsFactory.createDefaultButton
import ru.tensor.sbis.design.topNavigation.util.GraphicBackgroundManager
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.topNavigation.util.isVisibleNullable
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.setTopPadding
import ru.tensor.sbis.design.utils.image_loading.BitmapSource
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.text.MultilineInputView
import ru.tensor.sbis.design.R as RD

/**
 * Логика компонента шапка.
 *
 * @author da.zolotarev
 */
internal class SbisTopNavigationController(
    internal var publishScope: CoroutineScope = CoroutineScope(SupervisorJob())
) : SbisTopNavigationApi, SbisTopNavigationInternalApi {
    private val sbisTopNavigationStyleHolder: SbisTopNavigationStyleHolder
        get() = sbisTopNavigationView.styleHolder

    private val imageLoader: ViewImageLoader = ViewImageLoader()
    private var isNeedChangeBackground = true

    private lateinit var sbisTopNavigationView: SbisTopNavigationView
    private lateinit var graphicBackgroundManager: GraphicBackgroundManager
    private var defaultBackground = ColorDrawable()

    private val currContext: Context
        get() = sbisTopNavigationView.currContext

    private var gestureListener: AttachedViewGestureDetector? = null

    /**
     * Необходимо ли обновить позицию заголовка при отрисовке.
     * Используется для того, чтобы пересчитать размер и положение заголовка при изменении размера других элементов.
     */
    internal var isNeedUpdateTitlePosition = false

    internal lateinit var gestureDetector: GestureDetector

    override var content: SbisTopNavigationContent
        by delegateNotEqual(SbisTopNavigationContent.NotInitializedContent) { _ ->
            sbisTopNavigationView.inflateLayout(content)
            sbisTopNavigationView.leftContent.counterContainer?.setOnClickListener {
                sbisTopNavigationView.backBtn?.callOnClick()
            }
            when (content) {
                is SbisTopNavigationContent.LargeTitle -> (content as SbisTopNavigationContent.LargeTitle).run {
                    sbisTopNavigationView.titleView?.value = title.getString(currContext)
                    sbisTopNavigationView.collapsedTitleView.text = title.getString(currContext)

                    sbisTopNavigationView.subtitleView?.text = subtitle?.getString(currContext)
                    sbisTopNavigationView.subtitleView?.isVisible = subtitle != null

                    sbisTopNavigationView.titleRightIcon?.text = icon?.character.toString()
                    sbisTopNavigationView.titleRightIcon?.isVisible = icon != null
                    sbisTopNavigationView.titleRightIcon?.setOnClickListener {
                        onIconClicked.invoke()
                    }

                    sbisTopNavigationView.largeTitleCounter?.accentedCounter = accentedCounter.value
                    sbisTopNavigationView.largeTitleCounter?.unaccentedCounter = unaccentedCounter.value
                    setCountersValue(accentedCounter.value, unaccentedCounter.value)

                    publishScope.launch(Dispatchers.Main) {
                        accentedCounter.collect {
                            setCountersValue(it, unaccentedCounter.value)
                        }
                    }
                    publishScope.launch(Dispatchers.Main) {
                        unaccentedCounter.collect {
                            setCountersValue(accentedCounter.value, it)
                        }
                    }
                }

                is SbisTopNavigationContent.SmallTitle -> (content as SbisTopNavigationContent.SmallTitle).run {
                    sbisTopNavigationView.titleView?.value = title.getString(currContext)
                    sbisTopNavigationView.collapsedTitleView.text = title.getString(currContext)
                    sbisTopNavigationView.subtitleView?.text = subtitle?.getString(currContext)
                    sbisTopNavigationView.subtitleView?.isVisible = subtitle != null
                    sbisTopNavigationView.setImage(image)

                    sbisTopNavigationView.titleRightIcon?.text = icon?.character.toString()
                    sbisTopNavigationView.titleRightIcon?.isVisible = icon != null
                    sbisTopNavigationView.titleRightIcon?.setOnClickListener {
                        onIconClicked.invoke()
                    }
                }

                is SbisTopNavigationContent.SmallTitleListContent ->
                    (content as SbisTopNavigationContent.SmallTitleListContent).run {
                        if (model.list.isEmpty()) {
                            sbisTopNavigationView.titleView?.value = model.title
                            sbisTopNavigationView.collapsedTitleView.text = model.title
                        }

                        sbisTopNavigationView.subtitleView?.text = model.subtitle
                        sbisTopNavigationView.subtitleView?.isVisible = true
                        if (image != null) {
                            sbisTopNavigationView.setImage(image)
                        } else {
                            sbisTopNavigationView.setImage(
                                SbisTopNavigationImage.Photos(
                                    model.list.map {
                                        it.photoData
                                    }
                                )
                            )
                        }
                    }

                is SbisTopNavigationContent.Tabs -> {
                    val tabs = (content as SbisTopNavigationContent.Tabs).tabs
                    sbisTopNavigationView.tabsView?.isOldToolbarDesign = isOldToolbarDesign
                    sbisTopNavigationView.tabsView?.tabs = tabs
                    tabs.getOrNull(sbisTopNavigationView.tabsView?.selectedTabIndex ?: 0)?.let {
                        setFirstTabTitleToCollapsedTitle(it)
                    }
                    sbisTopNavigationView.tabsView?.setOnTabClickListener {
                        setFirstTabTitleToCollapsedTitle(it)
                    }

                }

                is SbisTopNavigationContent.Logo -> (content as SbisTopNavigationContent.Logo).run {
                    sbisTopNavigationView.logoView?.type = logoType
                }

                else -> Unit
            }
            updateAllViews()
            updateItemsVisibility()
            contentChanges.tryEmit(content)
        }

    override var showBackButton: Boolean by delegateNotEqual(false) { newValue ->
        updateBackNavigationVisibility(newValue, presentationContext)
        setLeftContentMinWidth()
    }

    override var isEditingEnabled by delegateNotEqual(false, ::configureIsEditingEnabled)

    override var smallTitleMaxLines by delegateNotEqual(5, ::configureSmallTitle)

    @Deprecated("Используй rightActions")
    override var rightButtons
        by delegateNotEqual<List<AbstractSbisButton<*, *>>>(emptyList()) { oldValue, newValue ->
            oldValue.forEach { it.removeSelf() }
            configureRightItems(newValue)
        }

    @Deprecated("Используй rightActions")
    override var rightItems
        by delegateNotEqual<List<View>>(emptyList()) { oldValue, newValue ->
            oldValue.forEach { it.removeSelf() }
            configureRightItems(newValue)
        }

    override var rightActions: List<SbisTopNavigationActionItem> = emptyList()
        set(value) {
            // TODO https://dev.sbis.ru/opendoc.html?guid=838d8aa6-a78e-4d99-ac27-af4a970902db&client=3
            field = value
            removeOldViews()
            configureRightActions(value)
        }

    override var counter by delegateNotEqual(0, ::configureCounter)

    override var customView by delegateNotEqual<View?>(null) { oldValue, newValue ->
        oldValue.removeSelf()
        configureCustomView(newValue)
    }

    override var leftCustomView by delegateNotEqual<View?>(null) { oldValue, newValue ->
        oldValue.removeSelf()
        configureLeftCustomView(newValue)
    }

    override var leftIconSize by delegateNotEqual<SbisDimen>(
        SbisDimen.Attr(RD.attr.iconSize_6xl),
        ::configureLeftIconSize
    )

    override var syncState
        by delegateNotEqual<SbisTopNavigationSyncState>(SbisTopNavigationSyncState.NotRunning) { newValue ->
            newValue.applyState(sbisTopNavigationView.flatIndicator, sbisTopNavigationView.noNetworkIcon)
            isNeedUpdateTitlePosition = true
        }

    override var titlePosition: HorizontalAlignment by delegateNotEqual(HorizontalAlignment.LEFT) { newValue ->
        configureTitlePosition(newValue)
    }

    override var presentationContext by delegateNotEqual(SbisTopNavigationPresentationContext.DEFAULT) { newValue ->
        updateBackNavigationVisibility(showBackButton, newValue)
        configureCounter(counter)
    }

    override var isOldToolbarDesign by delegateNotEqual(false) { newValue ->
        configureIsOldToolbarDesign(newValue)
    }

    override var footerItems: List<SbisTopNavigationFooterItem> by delegateNotEqual(listOf()) { newValue ->
        sbisTopNavigationView.footerView.items = newValue
        graphicBackgroundManager.reinitZenTheme()
    }

    override var isDividerVisible by delegateNotEqual(false) { newValue ->
        sbisTopNavigationView.invalidate()
    }

    override var isTransparent by delegateNotEqual(false) { newValue ->
        reinstallBackgroundColor()
        sbisTopNavigationView.invalidate()
    }

    override val contentChanges = MutableSharedFlow<SbisTopNavigationContent>(extraBufferCapacity = 1)

    override fun setBackgroundColor(color: SbisColor) {
        isNeedChangeBackground = false

        val backgroundColorToBeSet = if (isTransparent) {
            Color.TRANSPARENT
        } else {
            color.getColor(currContext)
        }

        defaultBackground = ColorDrawable(backgroundColorToBeSet)
        sbisTopNavigationView.background = defaultBackground
        sbisTopNavigationView.setChildBgColor(backgroundColorToBeSet)
    }

    override fun setTitleColor(color: SbisColor) {
        sbisTopNavigationStyleHolder.customTitleColor = color
        configureCustomColors()
    }

    override fun setSubTitleColor(color: SbisColor) {
        sbisTopNavigationStyleHolder.customSubTitleColor = color.getColor(currContext)
        configureCustomColors()
    }

    override fun setBackBtnTextColor(color: SbisColor) {
        sbisTopNavigationStyleHolder.customBackBtnTextColor = color.getColor(currContext)
        configureCustomColors()
    }

    override fun setGraphicBackground(bg: BitmapSource?, roundCorners: Boolean) {
        isNeedChangeBackground = bg == null
        graphicBackgroundManager.isRoundBottomCorners = roundCorners
        configureGraphicBackground(bg)
    }

    override fun attachScrollableView(view: RecyclerView) {
        gestureListener?.recycler = view
        setScrollListener(view)
    }

    override var titleTextHandler: SbisTopNavigationTitleHandler? = null
        set(value) {
            if (field == value) return
            field = value
            sbisTopNavigationView.requestLayout()
        }

    override fun getIconButtonViewConfigurator(model: SbisTopNavigationActionItem):
        SbisTopNavigationIconButtonViewConfigurator? {
        if (model !is SbisTopNavigationActionItem.IconButton) return null
        val index = rightActions.indexOf(model)
        val child = sbisTopNavigationView.rightBtnContainer?.getChildAt(index)
        return if (child is FrameLayout) {
            (child.getChildAt(0) as? SbisRoundButton)?.let {
                SbisTopNavigationIconButtonViewConfigurator(child, it)
            }
        } else {
            null
        }
    }

    override fun onViewMeasured() {
        imageLoader.onViewMeasured()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        imageLoader.onVisibilityAggregated(isVisible)
    }

    override fun getFootersMarginSum() = sbisTopNavigationView.footerView.getFootersMarginSum()
    override fun getFootersMeasuredHeight() = sbisTopNavigationView.footerView.getFootersMeasuredHeight()

    override fun animateTopNavigationFolding(dY: Int) {
        if (dY == 0 || !content.isCollapsible) return
        if (dY > 0) {
            // swipe up
            sbisTopNavigationView.apply {
                if (isCollapsed) return
                isCollapsed = true
                sbisTopNavigationStyleHolder.setCollapsedHeight()
                minimumHeight = sbisTopNavigationStyleHolder.viewHeight
                moveContentGuideline()
            }
        } else {
            // swipe down
            sbisTopNavigationView.apply {
                if (!isCollapsed) return
                isCollapsed = false
                sbisTopNavigationStyleHolder.setExpandHeight()
                minimumHeight = sbisTopNavigationStyleHolder.viewHeight
                moveContentGuideline()
            }
        }
        sbisTopNavigationView.requestLayout()
        updateItemsVisibility()
    }

    private fun updateItemsVisibility() = with(sbisTopNavigationView) {
        if (isCollapsed) {
            personView.isVisibleNullable = false
            searchInput.isVisibleNullable = false
            tabsView.isVisibleNullable = false
            titleView.isVisibleNullable = false
            subtitleView.isVisibleNullable = false
            personView.isVisibleNullable = false
            leftIconView.isVisibleNullable = false
            titleRightIcon.isVisibleNullable = false
            leftImageView.isVisibleNullable = false
            counterView.isVisibleNullable = false
            customViewContainer.isVisibleNullable = false
            leftCustomViewContainer.isVisibleNullable = false
            logoView.isVisibleNullable = false

            collapsedTitleView.isVisibleNullable = true
            return@with
        }

        when (content) {
            is SbisTopNavigationContent.LargeTitle -> (content as SbisTopNavigationContent.LargeTitle).run {
                titleRightIcon.isVisibleNullable = icon != null
                subtitleView.isVisibleNullable = subtitleView?.text?.isNotEmpty() ?: false
            }

            is SbisTopNavigationContent.Logo -> {
                logoView.isVisibleNullable = true
            }

            SbisTopNavigationContent.SearchInput -> {
                searchInput.isVisibleNullable = true
            }

            is SbisTopNavigationContent.Tabs -> {
                tabsView.isVisibleNullable = true
            }

            is SbisTopNavigationContent.SmallTitle -> (content as SbisTopNavigationContent.SmallTitle).run {
                titleRightIcon.isVisibleNullable = icon != null
                subtitleView.isVisibleNullable = subtitleView?.text?.isNotEmpty() ?: false

                leftIconView.isVisibleNullable = image is SbisTopNavigationImage.Icon
                personView.isVisibleNullable =
                    image is SbisTopNavigationImage.Photos || image is SbisTopNavigationImage.Photo
                leftImageView.isVisibleNullable = image is SbisTopNavigationImage.Drawable
            }

            is SbisTopNavigationContent.SmallTitleListContent -> {
                subtitleView.isVisibleNullable = subtitleView?.text?.isNotEmpty() ?: false
            }

            else -> Unit
        }
        titleView.isVisibleNullable = true
        collapsedTitleView.isVisibleNullable = false
    }

    /** @SelfDocumented */
    fun attach(
        sbisTopNavigationView: SbisTopNavigationView,
        bgManager: GraphicBackgroundManager,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        this.sbisTopNavigationView = sbisTopNavigationView
        this.graphicBackgroundManager = bgManager
        imageLoader.init(
            sbisTopNavigationView,
            sbisTopNavigationView,
            {},
            { null },
            ::getImageWidthAndHeight
        )
        setIsOldToolbarFromAttrs(attrs, defStyleAttr, defStyleRes)
        sbisTopNavigationStyleHolder.initResources(currContext)

        currContext.withStyledAttributes(
            attrs,
            R.styleable.SbisTopNavigation,
            defStyleAttr,
            defStyleRes
        ) {
            showBackButton = getBoolean(R.styleable.SbisTopNavigation_SbisTopNavigation_showBackButton, showBackButton)
            isEditingEnabled =
                getBoolean(R.styleable.SbisTopNavigation_SbisTopNavigation_isEditingEnabled, isEditingEnabled)
            isDividerVisible =
                getBoolean(R.styleable.SbisTopNavigation_SbisTopNavigation_isDividerVisible, isDividerVisible)
            smallTitleMaxLines =
                getInt(R.styleable.SbisTopNavigation_SbisTopNavigation_smallTitleMaxLines, smallTitleMaxLines)
                    .coerceIn(getAllowedTextLines())
            defaultBackground = ColorDrawable(
                getColor(
                    R.styleable.SbisTopNavigation_SbisTopNavigation_backgroundColor,
                    StyleColor.UNACCENTED.getAdaptiveBackgroundColor(currContext)
                )
            )
            sbisTopNavigationView.background = defaultBackground
            sbisTopNavigationView.setChildBgColor(getDefaultBackgroundColor())

            getColorOrNull(R.styleable.SbisTopNavigation_SbisTopNavigation_titleColor)?.let {
                setTitleColor(SbisColor.Int(it))
            }
            getColorOrNull(R.styleable.SbisTopNavigation_SbisTopNavigation_subtitleColor)?.let {
                setSubTitleColor(SbisColor.Int(it))
            }
            getColorOrNull(R.styleable.SbisTopNavigation_SbisTopNavigation_backBtnColor)?.let {
                if (isDefaultBackBtnColor(it)) {
                    setBackBtnTextColor(SbisColor.Int(it))
                }
            }
        }
        gestureListener = AttachedViewGestureDetector(sbisTopNavigationView, publishScope, this).also {
            gestureDetector = GestureDetector(currContext, it)
        }
        sbisTopNavigationView.inflateLayout(content)
    }

    /**
     * Обновить позицию заголовка.
     */
    internal fun configureTitlePosition(alignment: HorizontalAlignment) {
        sbisTopNavigationView.alignmentStrategy.alignText(alignment)
        isNeedUpdateTitlePosition = true
    }

    /**
     * @SelfDocumented
     */
    internal fun getDefaultBackgroundColor() =
        if (isOldToolbarDesign) {
            BackgroundColor.HEADER.getValue(currContext)
        } else {
            defaultBackground.color
        }

    /** @SelfDocumented */
    internal fun onAttachedToWindow() {
        publishScope = CoroutineScope(SupervisorJob())
        publishScope.launch(Dispatchers.Main) {
            gestureListener?.dYShiftEvents?.collect(::animateFooter)
        }
    }

    /** @SelfDocumented */
    internal fun onDetachedFromWindow() {
        publishScope.cancel()
    }

    internal fun setZenTheme(themeModel: ZenThemeModel) {
        getAllChildrenViews(sbisTopNavigationView).filterIsInstance<ZenThemeSupport>().forEach {
            it.setZenTheme(themeModel)
        }

        getIconButtonsViews().filterNotNull().forEach {
            val backgroundColor = ColorStateList(
                buttonStateSet,
                intArrayOf(
                    themeModel.elementsColors.translucentButtonColor.getColor(currContext),
                    themeModel.elementsColors.translucentActiveButtonColor.getColor(currContext)
                )
            )
            it.style = SbisButtonCustomStyle(
                backgroundColors = backgroundColor,
                iconStyle = SbisButtonIconStyle(
                    ColorStateList.valueOf(
                        themeModel.elementsColors.contrastColor.getColor(
                            currContext
                        )
                    )
                )
            )
            it.invalidate()
        }
    }

    private fun removeOldViews() {
        sbisTopNavigationView.rightBtnContainer?.children?.forEach {
            if (it is ViewGroup) it.removeAllViews()
        }
        sbisTopNavigationView.rightBtnContainer?.removeAllViews()
    }

    private fun animateFooter(dY: Int) {
        sbisTopNavigationView.footerView.animateItemsByYDistance(dY)
    }

    /**
     * @SelfDocumented
     */
    private fun configureGraphicBackground(bg: BitmapSource?) {
        if (bg == null) {
            imageLoader.clearImages()
        } else {
            imageLoader.setImages(listOf(bg))
        }
    }

    /**
     * @SelfDocumented
     */
    private fun getImageWidthAndHeight(): Pair<Int, Int> =
        sbisTopNavigationView.measuredWidth to sbisTopNavigationView.measuredHeight

    private fun setIsOldToolbarFromAttrs(
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        currContext.withStyledAttributes(
            attrs,
            R.styleable.SbisTopNavigation,
            defStyleAttr,
            defStyleRes
        ) {
            isOldToolbarDesign =
                getBoolean(R.styleable.SbisTopNavigation_SbisTopNavigation_isOldToolbarDesign, false)
        }
    }

    private fun isDefaultBackBtnColor(color: Int) = color != StyleColor.PRIMARY.getIconColor(currContext)

    private fun updateBackNavigationVisibility(isShownBackBtn: Boolean, context: SbisTopNavigationPresentationContext) =
        sbisTopNavigationView.run {
            when (context) {
                SbisTopNavigationPresentationContext.DEFAULT -> {
                    rightBackBtnContainer?.isVisible = false
                    backBtn?.isVisible = isShownBackBtn
                    leftContent.counterContainer?.isVisible = isShownBackBtn && counterView?.counter != 0
                }

                SbisTopNavigationPresentationContext.MODAL -> {
                    rightBackBtnContainer?.isVisible = isShownBackBtn
                    backBtn?.isVisible = false
                    leftContent.counterContainer?.isVisible = isShownBackBtn && counterView?.counter != 0
                }
            }
            isNeedUpdateTitlePosition = true
        }

    private fun configureCounter(counter: Int) {
        sbisTopNavigationView.counterView?.let {
            it.counter = counter
            sbisTopNavigationView.leftContent.counterContainer?.isVisible =
                counter != 0 && showBackButton && presentationContext != SbisTopNavigationPresentationContext.MODAL
        }
        isNeedUpdateTitlePosition = true
    }

    private fun configureLeftIconSize(leftIconSize: SbisDimen) {
        sbisTopNavigationView.leftIconView?.textSize = leftIconSize.getDimen(currContext)
        isNeedUpdateTitlePosition = true
    }

    private fun configureSmallTitle(maxLines: Int) {
        // Проверяем, что выбрана разметка с маленьким заголовком.
        (sbisTopNavigationView.titleView as? MultilineInputView)?.let {
            it.maxLines = maxLines.coerceIn(getAllowedTextLines())
        }
    }

    private fun View?.removeSelf() {
        this ?: return
        val parentView = parent as? ViewGroup ?: return
        parentView.removeView(this)
    }

    private fun configureCustomView(view: View?) {
        sbisTopNavigationView.customViewContainer?.run {
            if (view != null) addView(view, 0)
        }
        isNeedUpdateTitlePosition = true
    }

    private fun configureLeftCustomView(view: View?) {
        sbisTopNavigationView.leftCustomViewContainer?.run {
            if (view != null) addView(view, 0)
            isVisible = view != null
        }
        isNeedUpdateTitlePosition = true
    }

    private fun configureRightItems(buttons: List<View>) {
        sbisTopNavigationView.rightBtnContainer?.run {
            removeAllViews()
            isVisible = buttons.isNotEmpty()
            buttons.forEachIndexed { index, view ->
                if (view is AbstractSbisButton<*, *>) {
                    putButtonInContainer(view, index, false)
                } else {
                    addView(view, createCenteredLayoutParams())
                }

            }
        }
        isNeedUpdateTitlePosition = true
        graphicBackgroundManager.reinitZenTheme()
    }

    private fun configureRightActions(buttons: List<SbisTopNavigationActionItem>) {
        sbisTopNavigationView.rightBtnContainer?.run {
            isVisible = buttons.isNotEmpty()
            buttons.forEachIndexed { index, view ->
                when (view) {
                    is SbisTopNavigationActionItem.Button -> putButtonInContainer(view.button, index, true)
                    is SbisTopNavigationActionItem.CustomView -> addView(view.view, createCenteredLayoutParams())
                    is SbisTopNavigationActionItem.IconButton -> {
                        view.button.let { model ->
                            val button =
                                sbisTopNavigationView.createDefaultButton(createString(model.icon.character.toString()))
                                    .apply {
                                        this.counter = SbisButtonCounter(model.counter)
                                        setOnClickListener { model.action.invoke(it) }
                                    }
                            putButtonInContainer(button, index, false)
                        }
                    }
                }
            }
        }
        isNeedUpdateTitlePosition = true
        graphicBackgroundManager.reinitZenTheme()
    }

    private fun LinearLayout.putButtonInContainer(
        it: AbstractSbisButton<*, *>,
        index: Int,
        isNotNeedBadge: Boolean
    ) {
        it.setContentDescription(RIGHT_BUTTONS_CONTENT_DESC)
        if (it.getId() == View.NO_ID && index < RIGHT_BUTTONS_IDS.size) {
            it.setId(RIGHT_BUTTONS_IDS[index])
        }
        val btnContainer = createBtnContainer(it, isNotNeedBadge)
        addView(btnContainer, createCenteredLayoutParams())
    }

    private fun createCenteredLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            /*
            Ставим CENTER_VERTICAL, чтобы в том случае, если кнопки будут разных размеров,
            они все были выравнены по вертикали.
             */
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    /**
     * Создать контейнер для одной кнопки.
     */
    private fun createBtnContainer(button: AbstractSbisButton<*, *>, isNotNeedBadge: Boolean) =
        FrameLayoutWithBadge(currContext).apply {
            clipChildren = false
            if (button is SbisRoundButton || button is SbisLinkButton) {
                (button as? SbisRoundButton)?.let {
                    isNeedBadge = it.type == SbisRoundButtonType.Transparent && !isNotNeedBadge && !isOldToolbarDesign
                }
                minimumWidth =
                    sbisTopNavigationView.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_button_min_width)
                minimumHeight =
                    sbisTopNavigationView.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_button_min_height)
            }
            // Убираем родителя кнопки, нужно если мы вставляем тот же instance кнопки (уже лежит в контейнере) повторно
            button.removeSelf()
            addView(
                button,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    .apply {
                        gravity = Gravity.CENTER
                    }
            )

        }

    private fun configureIsEditingEnabled(isEditingEnabled: Boolean) {
        sbisTopNavigationView.titleView?.onHideKeyboard = !isEditingEnabled
    }

    private fun configureIsOldToolbarDesign(isOldDesign: Boolean) {
        sbisTopNavigationView.apply {
            if (isNeedChangeBackground) background = ColorDrawable(getDefaultBackgroundColor())
            if (isOldDesign) {
                backBtn?.setTextColor(currContext.getColorFromAttr(RD.attr.toolbarBackIconColor))
                minimumHeight = sbisTopNavigationStyleHolder.oldToolbarHeight
                if (content is SbisTopNavigationContent.LargeTitle) {
                    titleView?.valueSize = sbisTopNavigationStyleHolder.oldToolbarlargeTitleFontSize
                }
                TypefaceManager.getRobotoRegularFont(currContext)?.let {
                    titleView?.setTypeface(it)
                }
            } else {
                backBtn?.setTextColor(currContext.getColorFromAttr(R.attr.SbisTopNavigation_backBtnColor))
                minimumHeight = sbisTopNavigationStyleHolder.viewHeight
                if (content is SbisTopNavigationContent.LargeTitle) {
                    titleView?.valueSize = sbisTopNavigationStyleHolder.largeTitleFontSize
                }
                TypefaceManager.getRobotoBoldFont(currContext)?.let {
                    titleView?.setTypeface(it)
                }
                titleView?.setTopPadding(0)
            }
        }
    }

    private fun updateAllViews() {
        setupTitleViewClickDelegation()

        customView.removeSelf()
        configureCustomView(customView)

        configureSmallTitle(smallTitleMaxLines)
        configureLeftIconSize(leftIconSize)
        configureIsEditingEnabled(isEditingEnabled)

        rightButtons.forEach { it.removeSelf() }
        rightItems.forEach { it.removeSelf() }
        configureRightItems(rightItems.ifEmpty { rightButtons })
        if (rightActions.isNotEmpty()) {
            configureRightActions(rightActions)
        }

        syncState.applyState(sbisTopNavigationView.flatIndicator, sbisTopNavigationView.noNetworkIcon)
        configureIsOldToolbarDesign(isOldToolbarDesign)
        configureCustomColors()

        setLeftContentMinWidth()
        graphicBackgroundManager.reinitZenTheme()
    }

    private fun setupTitleViewClickDelegation() {
        sbisTopNavigationView.titleView?.setOnTouchListener { v, event ->
            (v as? BaseInputView)?.let {
                if (isNotNeedDelegateInputViewClickToTopNavView(it)) {
                    it.onTouchEvent(event)
                } else {
                    if (event.action == ACTION_UP) {
                        sbisTopNavigationView.performClick()
                        true
                    } else {
                        false
                    }
                }
            } ?: false
        }
    }

    private fun isNotNeedDelegateInputViewClickToTopNavView(view: BaseInputView) =
        view.onFieldClickListener != null || isEditingEnabled

    private fun configureCustomColors() {
        sbisTopNavigationStyleHolder.customTitleColor?.let {
            sbisTopNavigationView.titleView?.valueColor = it
        }
        if (content is SbisTopNavigationContent.SmallTitle ||
            content is SbisTopNavigationContent.SmallTitleListContent
        ) {
            sbisTopNavigationStyleHolder.customSubTitleColor?.let {
                sbisTopNavigationView.subtitleView?.setTextColor(it)
            }
        }
        sbisTopNavigationStyleHolder.customBackBtnTextColor?.let {
            sbisTopNavigationView.backBtn?.setTextColor(it)
        }
    }

    private fun getAllowedTextLines() =
        MIN_MAX_LINES..if (DeviceConfigurationUtils.isLandscape(currContext)) {
            MAX_MAX_LINES_LANDSCAPE
        } else {
            MAX_MAX_LINES_PORTRAIT
        }

    private fun setCountersValue(accentedCounter: Int, unaccenteCounter: Int) {
        sbisTopNavigationView.largeTitleCounter?.accentedCounter = accentedCounter
        sbisTopNavigationView.largeTitleCounter?.unaccentedCounter = unaccenteCounter
        sbisTopNavigationView.largeTitleCounter?.isVisible = accentedCounter != 0 || unaccenteCounter != 0
    }

    private fun setLeftContentMinWidth() {
        sbisTopNavigationView.leftContent.minimumWidth = if (!showBackButton) {
            when (content) {
                SbisTopNavigationContent.SearchInput -> sbisTopNavigationStyleHolder.leftContentWithSearchMinWidth
                is SbisTopNavigationContent.Tabs -> 0
                else -> sbisTopNavigationStyleHolder.leftContentMinWidth
            }
        } else {
            sbisTopNavigationStyleHolder.leftContentMinWidth
        }
    }

    /** @SelfDocumented */
    private fun TypedArray.getColorOrNull(@StyleableRes color: Int) =
        if (getType(color) == TypedValue.TYPE_ATTRIBUTE) null else getColor(color, 0)

    private fun reinstallBackgroundColor() {
        setBackgroundColor(createColor(defaultBackground.color))
    }

    private fun getAllChildrenViews(v: View): List<View> {
        val visited: MutableList<View> = ArrayList()
        val unvisited: MutableList<View> = ArrayList()
        unvisited.add(v)

        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)
            if (child !is ViewGroup) continue
            val childCount = child.childCount
            for (i in 0 until childCount) unvisited.add(child.getChildAt(i))
        }

        return visited
    }

    private fun getIconButtonsViews() = rightActions.filterIsInstance<SbisTopNavigationActionItem.IconButton>().map {
        val index = rightActions.indexOf(it)
        val singleButtonContainer = (sbisTopNavigationView.rightBtnContainer?.getChildAt(index) as? ViewGroup)
        singleButtonContainer?.getChildAt(0) as? SbisRoundButton
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setScrollListener(view: RecyclerView) {
        var oldX: Float? = null
        var currentMovementY: Float? = null

        view.setOnTouchListener { rv, ev ->
            gestureDetector.onTouchEvent(ev)

            val consume: Boolean
            when (ev.actionMasked) {
                ACTION_DOWN -> {
                    gestureListener?.rvHeight = rv.measuredHeight
                    currentMovementY = ev.y
                    consume = false
                }

                ACTION_UP -> {
                    gestureListener?.rvHeight = null
                    oldX = null
                    currentMovementY = null
                    consume = false
                    sbisTopNavigationView.footerView.snapFooters()
                }

                ACTION_MOVE -> {
                    val dX = oldX?.let { ev.x - it }
                    oldX = ev.x

                    if (dX != null && dX != 0f) {
                        // Подменяем событие, чтобы для RecyclerView движение осуществлялось только по горизонтали.
                        currentMovementY?.let {
                            gestureListener?.recycler?.onTouchEvent(
                                MotionEvent.obtain(ev).apply {
                                    setLocation(ev.x, it)
                                }
                            )
                        }
                    }

                    if (currentMovementY == null) {
                        currentMovementY = ev.y
                    }

                    consume = true
                }

                else -> {
                    consume = false
                }
            }

            return@setOnTouchListener consume
        }
    }

    private fun setFirstTabTitleToCollapsedTitle(it: SbisTabsViewItem) {
        (it.content.firstOrNull() as? SbisTabViewItemContent.Text)?.text?.let { tabTitle ->
            sbisTopNavigationView.collapsedTitleView.text = tabTitle.getString(currContext)
        }
    }

    private companion object {
        const val MIN_MAX_LINES = 1
        const val MAX_MAX_LINES_PORTRAIT = 5
        const val MAX_MAX_LINES_LANDSCAPE = 2

        val RIGHT_BUTTONS_IDS: List<Int> = listOf(
            R.id.top_navigation_right_button_1,
            R.id.top_navigation_right_button_2,
            R.id.top_navigation_right_button_3
        )
        const val RIGHT_BUTTONS_CONTENT_DESC = "top_nav_right_button"

        val buttonStateSet = arrayOf(
            intArrayOf(-android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_pressed)
        )
    }
}

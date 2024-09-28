package ru.tensor.sbis.design.topNavigation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.counters.textcounter.SbisTextCounter
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.logo.SbisLogoView
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationImage
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationInternalApi
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationSyncState
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationTitleText
import ru.tensor.sbis.design.topNavigation.internal_view.ChildViewFactory
import ru.tensor.sbis.design.topNavigation.internal_view.FlatIndicatorView
import ru.tensor.sbis.design.topNavigation.internal_view.SbisTopNavigationFooterView
import ru.tensor.sbis.design.topNavigation.internal_view.SbisTopNavigationLeftContent
import ru.tensor.sbis.design.topNavigation.util.AlignmentHelper
import ru.tensor.sbis.design.topNavigation.util.GraphicBackgroundManager
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.topNavigation.util.isNotVisibleNullable
import ru.tensor.sbis.design.topNavigation.util.isVisibleNullable
import ru.tensor.sbis.design.topNavigation.util.safeAddView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.fullMeasuredWidth
import ru.tensor.sbis.design.utils.extentions.updateBottomMargin
import ru.tensor.sbis.design.utils.extentions.updateRightMargin
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.design.utils.image_loading.DrawableImageView
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.design.view.input.text.MultilineInputView

/**
 *  Компонент шапка.
 *  Ссылка на стандарт - http://axure.tensor.ru/MobileStandart8/компоновка_шапок_карточек__mobile__23_4204.html
 *
 * @author da.zolotarev
 */
class SbisTopNavigationView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes
    defStyleAttr: Int,
    @StyleRes
    defStyleRes: Int,
    internal val controller: SbisTopNavigationController,
    private val bgManager: GraphicBackgroundManager
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    DrawableImageView by bgManager,
    SbisTopNavigationApi by controller,
    SbisTopNavigationInternalApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
        @StyleRes
        defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisTopNavigationController(), GraphicBackgroundManager())

    /** @SelfDocumented */
    var isCollapsed: Boolean = false

    /** @SelfDocumented */
    internal var graphicBackground: Drawable? = null

    private val oldToolbarContext = ThemeContextBuilder(
        getContext(),
        ru.tensor.sbis.design.R.attr.globalToolbarStyle
    ).build()

    private val childViewFactory = ChildViewFactory(context, this)

    private val rightBtnBackCt: View?
        get() = findViewById(R.id.top_navigation_right_back_btn_container)

    private var needMeasureMainContent = false

    private val titleText = SbisTopNavigationTitleText()

    /** @SelfDocumented */
    internal val rightContentGuideline: Guideline?
        get() = findViewById(R.id.top_navigation_guideline)

    /**
     * Контент с большой шапкой.
     */
    internal val largeTitleContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        inflate(currContext, R.layout.top_navigation_large_title, null)
    }

    /**
     * Контент с маленькой шапкой.
     */
    internal val smallTitleContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        inflate(currContext, R.layout.top_navigation_small_title, null)
    }

    /**
     * Контент с вкладками.
     */
    internal val tabsContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        inflate(currContext, R.layout.top_navigation_tabs, null)
    }

    /**
     * Контент с поиском.
     */
    internal val searchContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        val view = inflate(currContext, R.layout.top_navigation_search_input, null)
        val searchInputCt: FrameLayout = view.findViewById(R.id.top_navigation_search_input_ct)
        val searchInput = SearchInput(ContextThemeWrapper(this.context, R.style.SearchInputTheme)).apply {
            id = R.id.top_navigation_search_input
            isRoundSearchInputBackground = true
        }
        searchInputCt.addView(searchInput, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        view
    }

    /**
     * Пустой контент.
     */
    internal val emptyContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        inflate(currContext, R.layout.top_navigation_empty, null)
    }

    /**
     * Контент с логотипом.
     */
    internal val logoContainerView: View by lazy(LazyThreadSafetyMode.NONE) {
        inflate(currContext, R.layout.top_navigation_logo, null)
    }

    /**
     * Ресурсы view.
     */
    internal val styleHolder = SbisTopNavigationStyleHolder()

    /**
     * @SelfDocumented
     */
    internal val alignmentStrategy = AlignmentHelper(this)

    /** @SelfDocumented */
    internal val leftContent =
        SbisTopNavigationLeftContent(context, styleHolder = styleHolder, childViewFactory = childViewFactory)

    /** Счетчик справа от большого заголовка*/
    internal val largeTitleCounter: SbisTextCounter?
        get() = findViewById(R.id.top_navigation_large_title_counter)

    internal val flatIndicator: FlatIndicatorView?
        get() = findViewById(R.id.top_navigation_loading_indicator)

    /**
     * Контекст, используемый во view, может измениться если используется старый дизайн [isOldToolbarDesign].
     */
    val currContext
        get() = if (isOldToolbarDesign) oldToolbarContext else context

    /**
     * Подвал.
     */
    val footerView: SbisTopNavigationFooterView by lazy(LazyThreadSafetyMode.NONE) {
        SbisTopNavigationFooterView(currContext, styleHolder = styleHolder)
    }

    /**
     * Строка поиска.
     */
    @Suppress("unused")
    val searchInput: SearchInput?
        get() = findViewById(R.id.top_navigation_search_input)

    /**
     * Панель вкладок.
     */
    val tabsView: SbisTabsView?
        get() = findViewById(R.id.top_navigation_tabs_view)

    /**
     * Заголовок.
     */
    val titleView: BaseInputView?
        get() = findViewById(R.id.top_navigation_title)

    /**
     * Подзаголовок.
     */
    val subtitleView: SbisTextView?
        get() = findViewById(R.id.top_navigation_subtitle)

    /**
     * Фото персоны.
     */
    val personView: PersonCollageView?
        get() = findViewById(R.id.top_navigation_person_view)

    /**
     * Иконка слева от заголовка.
     */
    val leftIconView: SbisTextView?
        get() = findViewById(R.id.top_navigation_left_icon)

    /** Иконка справа от заголовка*/
    val titleRightIcon: SbisTextView?
        get() = findViewById(R.id.top_navigation_title_right_icon)

    /**
     * Изображение слева от заголовка.
     */
    val leftImageView: ImageView?
        get() = findViewById(R.id.top_navigation_left_image)

    /**
     * Счетчик.
     */
    val counterView: SbisCounter?
        get() = childViewFactory.getLeftContentCounter()

    /**
     * Кнопка назад (стрелка).
     */
    val backBtn: SbisTextView?
        get() = childViewFactory.getBackBtn()

    /**
     * Кнопка назад справа (круг с крестом).
     */
    val rightBtnBack: SbisRoundButton?
        get() = findViewById(R.id.top_navigation_right_back_btn)

    /**
     * Контейнер кнопки "Назад" с крестиком справа.
     */
    val rightBackBtnContainer: FrameLayout?
        get() = findViewById(R.id.top_navigation_right_back_btn_container)

    /**
     * Контейнер кастомной view.
     */
    val customViewContainer: FrameLayout?
        get() = findViewById(R.id.top_navigation_custom_content)

    /**
     * Контейнер кастомной view слева.
     */
    val leftCustomViewContainer: FrameLayout?
        get() = childViewFactory.getLeftCustomViewContainer()

    /**
     * Контейнер кнопок справа.
     */
    val rightBtnContainer: LinearLayout?
        get() = findViewById(R.id.top_navigation_right_btn_container)

    /**
     * Логотип.
     */
    val logoView: SbisLogoView?
        get() = findViewById(R.id.top_navigation_logo_view)

    /**
     * Индикатор загрузки (крутилка) в правой части шапки.
     */
    @Deprecated("Use syncState")
    val loadingIndicator: SbisLoadingIndicator?
        get() = null

    /**
     * Индикатор отсутствия сети (иконка) в правой части шапки.
     */
    val noNetworkIcon: SbisTextView?
        get() = findViewById(R.id.top_navigation_loading_no_network_icon)

    /**
     * Заголовок свернутой шапки.
     */
    val collapsedTitleView: SbisTextView
        get() = childViewFactory.getOrCreateCollapsedTitleView()

    init {
        addView(
            leftContent,
            generateDefaultLayoutParams().apply {
                height = LayoutParams.WRAP_CONTENT
            }
        )
        controller.attach(this, bgManager, attrs, defStyleAttr, defStyleRes)
        bgManager.attach(this)
        isClickable = true
        contentDescription = SBIS_TOP_NAVIGATION_CONTENT_DESC
    }

    override fun onMeasure(widthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        setTitleSubTitleBottomMargin()
        setupSmallTitleSpaceVisibility()
        setupSearchMarginStart()
        var suggestedHeight = 0
        var suggestedWidth = 0
        val minMainContentWidth = fakeMeasureMainContent()

        val heightMeasureSpec = if (isCollapsed) {
            MeasureSpecUtils.makeExactlySpec(styleHolder.collapsedTopNavHeight)
        } else {
            parentHeightMeasureSpec
        }

        leftContent.isClickableTopNavContent = isContentClickable()
        leftContent.measure(
            MeasureSpecUtils.makeAtMostSpec(MeasureSpec.getSize(widthMeasureSpec) - minMainContentWidth),
            heightMeasureSpec
        )

        suggestedHeight += leftContent.measuredHeight

        if (needMeasureMainContent) {
            val mainContentWidthSpec = MeasureSpecUtils.makeExactlySpec(
                MeasureSpec.getSize(widthMeasureSpec) - leftContent.measuredWidth - paddingStart - paddingEnd
            )
            suggestedHeight = maxOf(
                suggestedHeight,
                content.measurer.measureAndGetHeight(
                    this,
                    mainContentWidthSpec,
                    heightMeasureSpec,
                    isCollapsed
                )
            )
        }
        footerView.measure(
            MeasureSpecUtils.makeExactlySpec(MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd),
            parentHeightMeasureSpec
        )
        children.filter { it != footerView && it != collapsedTitleView }.forEach { suggestedWidth += it.measuredWidth }

        graphicBackground?.setBounds(
            0,
            0,
            paddingStart + suggestedWidth + paddingEnd,
            paddingTop + suggestedHeight + footerView.safeMeasuredInBackgroundHeight + paddingBottom
        )

        suggestedHeight += footerView.safeMeasuredHeight
        alignmentStrategy.onMeasureView()
        controller.onViewMeasured()

        setMeasuredDimension(
            paddingStart + suggestedWidth + paddingEnd,
            paddingTop + maxOf(suggestedHeight, minimumHeight) + paddingBottom
        )

        val rightHalfCollapsedTitleMaxWidth = measuredWidth / 2 - rightBtnContainer.fullMeasuredWidth
        collapsedTitleView.measure(
            MeasureSpecUtils.makeAtMostSpec(rightHalfCollapsedTitleMaxWidth * 2),
            MeasureSpecUtils.makeUnspecifiedSpec()
        )
        titleView?.let { inputView ->
            titleText.inputView = inputView
            controller.titleTextHandler?.format(
                inputView.measuredWidth,
                measuredHeight,
                titleText,
                content,
                smallTitleMaxLines
            )?.let {
                inputView.value = it
                collapsedTitleView.text = it
            }
        }
        setTitleTopMargin()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var dx = paddingStart
        var dy = paddingTop
        if (leftContent.measuredHeight > minimumHeight) {
            leftContent.layout(dx, dy)
        } else {
            leftContent.layout(dx, dy + ((minimumHeight - leftContent.measuredHeight) / 2))
        }
        dx += leftContent.measuredWidth
        dy += content.measurer.layoutAndGetHeight(dx, dy, this)
        footerView.layout(paddingStart + left, dy)
        if (controller.isNeedUpdateTitlePosition) {
            controller.configureTitlePosition(titlePosition)
            controller.isNeedUpdateTitlePosition = false
        }
        if (collapsedTitleView.isVisible) {
            collapsedTitleView.layout(
                (measuredWidth - collapsedTitleView.measuredWidth) / 2,
                (styleHolder.viewHeight - collapsedTitleView.measuredHeight) / 2
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onDetachedFromWindow()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        controller.onVisibilityAggregated(isVisible)
    }

    override fun dispatchDraw(canvas: Canvas) {
        graphicBackground?.draw(canvas)
        super.dispatchDraw(canvas)
        if (!isDividerVisible) return
        canvas.drawRect(
            0f,
            measuredHeight - styleHolder.dividerHeight,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            styleHolder.dividerPaint
        )
    }

    override fun setBackgroundColor(color: Int) {
        controller.setBackgroundColor(SbisColor.Int(color))
    }

    /**
     * Отрисовать выбранный тип контента.
     */
    internal fun inflateLayout(content: SbisTopNavigationContent) {
        for (i in childCount - 1 downTo 1) {
            removeViewAt(i)
        }
        safeAddView(
            when (content) {
                is SbisTopNavigationContent.LargeTitle -> largeTitleContainerView
                is SbisTopNavigationContent.SmallTitle -> smallTitleContainerView
                is SbisTopNavigationContent.SmallTitleListContent -> smallTitleContainerView
                is SbisTopNavigationContent.Tabs -> tabsContainerView
                is SbisTopNavigationContent.Logo -> logoContainerView
                SbisTopNavigationContent.SearchInput -> searchContainerView
                else -> emptyContainerView
            },
            generateDefaultLayoutParams()
        )
        needMeasureMainContent = true
        safeAddView(footerView)
        safeAddView(collapsedTitleView)
        // Устанавливаем глубину view, чтобы подвал был под основным контентом при анимации.
        footerView.translationZ = FOOTER_TRANSLATION_Z
    }

    /**
     * Установить изображение в шапку в зависимости от типа [SbisTopNavigationImage].
     */
    internal fun setImage(imageModel: SbisTopNavigationImage?) {
        personView.isVisibleNullable = false
        leftIconView.isVisibleNullable = false
        leftImageView.isVisibleNullable = false
        if (imageModel == null) return

        when (imageModel) {
            is SbisTopNavigationImage.Drawable -> leftImageView?.run {
                background = imageModel.getDrawable(currContext)
                isVisible = background != null
            }

            is SbisTopNavigationImage.Icon -> leftIconView?.run {
                text = imageModel.getIcon(currContext)
                isVisible = true
            }

            is SbisTopNavigationImage.Photos -> personView?.run {
                setDataList(imageModel.photos)
                isVisible = true
            }

            is SbisTopNavigationImage.Photo -> personView?.run {
                setDataList(listOf(imageModel.photoData))
                isVisible = true
            }
        }
    }

    /**
     * Получить левый отступ, если слева от текста ничего нет.
     */
    internal fun getTitleAndSubtitleStartMargin(): Int = if (
        leftContent.isNotVisibleNullable &&
        personView.isNotVisibleNullable &&
        leftIconView.isNotVisibleNullable &&
        leftImageView.isNotVisibleNullable
    ) {
        Offset.M.getDimenPx(currContext)
    } else {
        0
    }

    /** @SelfDocumented */
    internal fun moveContentGuideline() {
        (rightContentGuideline?.layoutParams as? ConstraintLayout.LayoutParams)?.let {
            it.guideBegin = styleHolder.viewHeight
            rightContentGuideline?.layoutParams = it
        }
    }

    /**
     * Установить фон для контента шапки.
     */
    internal fun setChildBgColor(color: Int) {
        when (content) {
            is SbisTopNavigationContent.LargeTitle -> largeTitleContainerView.setBackgroundColor(color)
            is SbisTopNavigationContent.SmallTitle -> smallTitleContainerView.setBackgroundColor(color)
            is SbisTopNavigationContent.SmallTitleListContent -> smallTitleContainerView.setBackgroundColor(color)
            is SbisTopNavigationContent.Tabs -> tabsContainerView.setBackgroundColor(color)
            is SbisTopNavigationContent.Logo -> logoContainerView.setBackgroundColor(color)
            SbisTopNavigationContent.SearchInput -> searchContainerView.setBackgroundColor(color)
            else -> emptyContainerView.setBackgroundColor(color)
        }
    }

    private fun fakeMeasureMainContent(): Int {
        if (!needMeasureMainContent) return 0
        val unspecifiedSpec = MeasureSpecUtils.makeUnspecifiedSpec()
        content.measurer.measureAndGetHeight(
            this,
            unspecifiedSpec,
            unspecifiedSpec,
            isCollapsed
        )
        return (rightBtnContainer?.measuredWidth ?: 0) + (rightBtnContainer?.marginEnd ?: 0)
    }

    private fun setupSearchMarginStart() = doOnLayout {
        searchInput?.updateRightMargin(
            if (
                syncState == SbisTopNavigationSyncState.NotRunning &&
                (!rightBtnBackCt.isVisibleNullable || rightButtons.isEmpty())
            ) {
                Offset.XS.getDimenPx(currContext)
            } else {
                0
            }
        )
    }

    /**
     * Устанавливаем отступ заголовка, если он не влезает в минимальный размер шапки.
     */
    private fun setTitleTopMargin() {
        if (measuredHeight == 0) return
        if ((measuredHeight - paddingBottom - paddingTop - footerView.safeMeasuredHeight) > minimumHeight) {
            titleView?.updateTopMargin(
                resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_title_min_top_margin)
            )
        } else {
            post {
                titleView?.updateTopMargin(0)
            }
        }
    }

    /**
     * Установить видимость вьюхи, создающей отступ между фото и текстом.
     */
    private fun setupSmallTitleSpaceVisibility() {
        if (
            content is SbisTopNavigationContent.SmallTitle ||
            content is SbisTopNavigationContent.SmallTitleListContent
        ) {
            findViewById<View>(R.id.top_navigation_small_title_space).isVisible =
                (personView.isVisibleNullable || leftImageView.isVisibleNullable || leftIconView.isVisibleNullable)
        }
    }

    /**
     * Устанавить отступ заголовка и подзаголовка, если они не влезают в минимальный размер шапки.
     */
    private fun setTitleSubTitleBottomMargin() = doOnLayout {
        titleView?.updateBottomMargin(if (isTitleNotFitViewMinHeight()) styleHolder.titleMinTopMargin else 0)
        subtitleView?.updateBottomMargin(if (isSubtitleNotFitViewMinHeight()) styleHolder.titleMinTopMargin else 0)
    }

    private fun isSubtitleNotFitViewMinHeight(): Boolean =
        (subtitleView.isVisibleNullable && (subtitleView?.lineCount ?: 0) > getSubtitleMinLinesCount()) ||
            titleView.isNotFit(TITLE_SUB_TITLE_LINES_COUNT)

    private fun isTitleNotFitViewMinHeight(): Boolean =
        !subtitleView.isVisibleNullable && titleView.isNotFit(TITLE_LINES_COUNT)

    private fun BaseInputView?.isNotFit(linesCount: Int) =
        this.isVisibleNullable && ((this as? MultilineInputView)?.lineCount ?: 0) > linesCount

    private fun getCustomViewFromXml() {
        if (childCount > 0) {
            val customView = getChildAt(0)
            removeView(customView)
            customViewContainer?.addView(customView, 0, customView.layoutParams)
        }
    }

    private fun getSubtitleMinLinesCount() =
        if (content is SbisTopNavigationContent.LargeTitle) LARGE_SUB_TITLE_LINES_COUNT else TITLE_SUB_TITLE_LINES_COUNT

    private fun isContentClickable(): Boolean {
        val isContentContainerClicked = when (content) {
            SbisTopNavigationContent.EmptyContent -> emptyContainerView.hasOnClickListeners()
            is SbisTopNavigationContent.LargeTitle -> largeTitleContainerView.hasOnClickListeners()
            is SbisTopNavigationContent.Logo -> logoContainerView.hasOnClickListeners()
            SbisTopNavigationContent.NotInitializedContent -> false
            SbisTopNavigationContent.SearchInput -> true
            is SbisTopNavigationContent.SmallTitle -> smallTitleContainerView.hasOnClickListeners()
            is SbisTopNavigationContent.SmallTitleListContent -> smallTitleContainerView.hasOnClickListeners()
            is SbisTopNavigationContent.Tabs -> true
        }
        return isContentContainerClicked ||
            titleView?.hasOnClickListeners() == true ||
            personView?.hasOnClickListeners() == true ||
            leftIconView?.hasOnClickListeners() == true ||
            leftImageView?.hasOnClickListeners() == true ||
            leftCustomViewContainer?.hasOnClickListeners() == true ||
            logoView?.hasOnClickListeners() == true
    }

    private companion object {
        const val TITLE_SUB_TITLE_LINES_COUNT = 1
        const val LARGE_SUB_TITLE_LINES_COUNT = 0
        const val TITLE_LINES_COUNT = 2
        const val FOOTER_TRANSLATION_Z = -1f
        const val SBIS_TOP_NAVIGATION_CONTENT_DESC = "sbis_top_navigation"
    }
}
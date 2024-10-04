package ru.tensor.sbis.design.toolbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.core.view.updatePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.counters.textcounter.SbisTextCounter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.toolbar.util.DEFAULT_TAB_FORMAT
import ru.tensor.sbis.design.toolbar.util.TabFormatter
import ru.tensor.sbis.design.toolbar.util.ToolbarTabLayoutTabsViewAdapter
import ru.tensor.sbis.design.toolbar.util.createCustomLayoutToolbarTab
import ru.tensor.sbis.design.toolbar.util.createIconToolbarTab
import ru.tensor.sbis.design.toolbar.util.createTitleToolbarTab
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.design.R as RDesign

private const val MIN_COUNT = 1

/**
 * Тулбар с вкладками
 *
 * @author ps.smirnyh
 */
class ToolbarTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.ToolbarTabLayout_theme,
    @StyleRes defStyleRes: Int = R.style.ToolbarTabLayoutTheme
) : HorizontalScrollView(
    ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    /**
     * Вкладка тулбара
     *
     * @see createTitleToolbarTab
     * @see createIconToolbarTab
     * @see createCustomLayoutToolbarTab
     */
    @Parcelize
    data class ToolbarTab(
        internal val id: Int,
        val title: String,
        @StringRes
        val titleRes: Int = ResourcesCompat.ID_NULL,
        internal var secondaryCounter: Int = 0,
        internal var primaryCounter: Int = 0,
        var isImageTab: Boolean = false,
        @LayoutRes
        val customLayout: Int = ResourcesCompat.ID_NULL,
        val isEnabled: Boolean = true,
        internal var counterFormatter: TabFormatter = DEFAULT_TAB_FORMAT,
        internal val navxId: NavxIdDecl? = null
    ) : Parcelable {

        @JvmOverloads
        constructor(
            @IdRes
            idRes: Int,
            @StringRes
            titleRes: Int,
            secondaryCounter: Int = 0,
            primaryCounter: Int = 0,
            isImageTab: Boolean = false,
            @LayoutRes
            customLayout: Int = 0,
            isEnabled: Boolean = true,
            counterFormatter: TabFormatter = DEFAULT_TAB_FORMAT,
            navxId: NavxIdDecl? = null
        ) : this(
            idRes,
            "",
            titleRes,
            secondaryCounter,
            primaryCounter,
            isImageTab,
            customLayout,
            isEnabled,
            counterFormatter,
            navxId
        )

    }

    private var tabs: LinkedHashMap<Int, ToolbarTab> = linkedMapOf()
    private val tabsContainer: LinearLayout = LinearLayout(getContext())
    private var listener: OnTabClickListener? = null
    private var selectedTabView: View? = null

    @Px
    private val tabSpacing: Int

    private val tabsVisibilityController: ToolbarTabsController
        get() = ToolbarPlugin.tabsVisibilityController

    private val tabsViewAdapter = ToolbarTabLayoutTabsViewAdapter(this)

    private val publishScope = CoroutineScope(SupervisorJob())

    private val _selectionChangeFlow = MutableSharedFlow<NavxIdDecl>()

    val selectionChangeFlow: Flow<NavxIdDecl> = _selectionChangeFlow

    var isTabsVisibilityControllerEnabled: Boolean = true

    private var fadeEdgeSize = resources.getDimensionPixelSize(R.dimen.toolbar_tabs_shadow_width)

    private val fadeMatrixRight by lazy { Matrix() }

    private val fadeMatrixLeft by lazy { Matrix() }

    private val fadePaintRight by lazy {
        Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
    }

    private val fadePaintLeft by lazy {
        Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
    }

    // TODO: Небходимо избавиться https://dev.saby.ru/opendoc.html?guid=27f9661a-9964-4767-999c-62f690c7477c&client=3
    var isApplyCheckPermissions: Boolean = false

    private var fadeShaderRight: Lazy<Shader> = createFadeShaderRight(fadeEdgeSize)
    private var fadeShaderLeft: Lazy<Shader> = createFadeShaderLeft(fadeEdgeSize)

    private fun createFadeShaderRight(fadeSize: Int): Lazy<Shader> = lazy {
        LinearGradient(
            0f,
            0f,
            fadeSize.toFloat(),
            0f,
            Color.TRANSPARENT,
            Color.WHITE,
            Shader.TileMode.CLAMP
        ).also {
            fadePaintRight.shader = it
        }
    }

    private fun createFadeShaderLeft(fadeSize: Int): Lazy<Shader> = lazy {
        LinearGradient(
            0f,
            0f,
            fadeSize.toFloat(),
            0f,
            Color.WHITE,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        ).also {
            fadePaintLeft.shader = it
        }
    }

    init {

        val typedArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.ToolbarTabLayout,
            defStyleAttr,
            defStyleRes
        )

        try {
            tabSpacing = typedArray.getDimensionPixelSize(
                R.styleable.ToolbarTabLayout_ToolbarTabLayout_tabSpacing,
                context.getDimenPx(RDesign.attr.offset_2xl)
            )
        } finally {
            typedArray.recycle()
        }

        setWillNotDraw(false)
        overScrollMode = View.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        isHorizontalFadingEdgeEnabled = false
        tabsContainer.orientation = LinearLayout.HORIZONTAL
        addView(tabsContainer)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isTabsVisibilityControllerEnabled) {
            tabsVisibilityController.attachView(tabsViewAdapter)
        }
    }

    override fun onDetachedFromWindow() {
        publishScope.cancel()
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas) = drawFadeEdges(canvas) {
        super.draw(canvas)
    }

    private fun drawFadeEdges(canvas: Canvas, function: (Canvas) -> Unit) {
        val saveCount = canvas.saveLayer(0f, 0f, (right + scrollX).toFloat(), bottom.toFloat(), null)
        function(canvas)
        if (scrollX > 0) drawFadeEdgeLeft(canvas)
        if (scrollX + width < tabsContainer.width) drawFadeEdgeRight(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun drawFadeEdgeLeft(canvas: Canvas) {
        val fadePositionLeft = scrollX
        val fadeLeft = fadePositionLeft - marginStart - paddingLeft + fadeEdgeSize
        fadeMatrixLeft.reset()
        fadeMatrixLeft.postTranslate(fadePositionLeft.toFloat(), 0f)
        fadeShaderLeft.value.setLocalMatrix(fadeMatrixLeft)
        canvas.drawRect(
            fadeLeft.toFloat(),
            0f,
            fadePositionLeft.toFloat(),
            bottom.toFloat(),
            fadePaintLeft
        )
    }

    private fun drawFadeEdgeRight(canvas: Canvas) {
        val fadePositionRight = right + scrollX
        val fadeRight = fadePositionRight - marginStart - paddingLeft - fadeEdgeSize
        fadeMatrixRight.reset()
        fadeMatrixRight.postTranslate(fadeRight.toFloat(), 0f)
        fadeShaderRight.value.setLocalMatrix(fadeMatrixRight)
        canvas.drawRect(
            fadeRight.toFloat(),
            0f,
            fadePositionRight.toFloat(),
            bottom.toFloat(),
            fadePaintRight
        )
    }

    /**@SelfDocumented**/
    fun getSelectedButtonId() = selectedTabView?.id ?: 0

    /**@SelfDocumented**/
    @JvmOverloads
    fun setTabs(tabs: LinkedHashMap<Int, ToolbarTab>?, selectedTabId: Int = -1) {
        if (tabs.isNullOrEmpty()) {
            return
        }
        this.tabs = tabs
        addTabs(tabs, selectedTabId)
        if (isTabsVisibilityControllerEnabled) {
            tabsVisibilityController.onTabsChanged(tabsViewAdapter)
        }
    }

    /**@SelfDocumented**/
    fun setOnTabClickListener(listener: OnTabClickListener?) {
        this.listener = listener
    }

    /**
     * Устанавливает значение дополнительного счётчика (общего числа событий), либо счётчика на подложке для вкладки с
     * иконкой
     */
    fun setSecondaryCounter(@IdRes id: Int, value: Int): Boolean {
        val tab = tabs[id] ?: return false
        tab.secondaryCounter = value
        displayCount(id, false, value)
        return true
    }

    /**
     * Устанавливает значение основного счётчика (непрочитанных событий), либо счётчика на подложке для вкладки с
     * иконкой
     */
    fun setPrimaryCounter(@IdRes id: Int, value: Int): Boolean {
        val tab = tabs[id] ?: return false
        tab.primaryCounter = value
        displayCount(id, true, value)
        return true
    }

    /**@SelfDocumented**/
    fun setCounterFormatter(@IdRes tabId: Int, formatter: TabFormatter) {
        tabs[tabId]?.counterFormatter = formatter
        findTabView(tabId) {
            val counter = it.findViewById<SbisTextCounter>(R.id.toolbar_counter)
            counter.formatter = formatter
        }
    }

    internal fun updateTabsVisibility(visibleNavxIds: Set<NavxIdDecl>?) {
        tabs.values.forEach { tab ->
            findTabView(tab.id) {
                it.isVisible = tab.navxId == null ||
                    visibleNavxIds?.contains(tab.navxId) ?: true
            }
        }
        onTabsChanged()
    }

    private fun onTabsChanged() {
        ensureSelectionIsMovedWhenExpectedTabGone()
        updateTabsMargin()
    }

    private fun displayCount(@IdRes tabId: Int, counterPrimary: Boolean, value: Int) {
        val tab = tabs[tabId] ?: return
        findTabView(tabId) {
            if (tab.isImageTab) {
                displayCountBadge(tab, it, value)
            } else {
                val counter = it.findViewById<SbisTextCounter>(R.id.toolbar_counter)
                if (counterPrimary)
                    counter.accentedCounter = value
                else
                    counter.unaccentedCounter = value
                counter.apply {
                    isVisible = accentedCounter != 0 || unaccentedCounter != 0
                }
            }
        }
    }

    private fun displayCountBadge(tab: ToolbarTab, view: View, value: Int) = with(view) {
        val counter = findViewById<SbisCounter>(R.id.toolbar_badge)
        val title = findViewById<SbisTextView>(R.id.toolbar_title)
        counter.formatter = tab.counterFormatter
        counter.counter = value
        title.updatePadding(
            right = when {
                !counter.isVisible -> resources.getDimensionPixelSize(R.dimen.toolbar_tab_image_no_counter_padding_end)
                counter.counter > MAX_COUNTER ->
                    resources.getDimensionPixelSize(R.dimen.toolbar_tab_image_long_counter_padding_end)

                else -> resources.getDimensionPixelSize(R.dimen.toolbar_tab_image_short_counter_padding_end)
            }
        )
        findViewById<View>(R.id.toolbar_counter_alignment_helper).isVisible = counter.isVisible
    }

    /**@SelfDocumented**/
    fun setSelection(@IdRes id: Int) {
        findTabView(id, ::selectView)
    }

    /** @SelfDocumented */
    fun setSelection(navxId: NavxIdDecl) {
        findTabView(navxId, ::selectView)
    }

    private fun shouldShowIndicator(tabsCount: Int): Boolean {
        return tabsCount > 1
    }

    private fun addTabs(tabs: Map<Int, ToolbarTab>, selectedTabId: Int) {
        tabsContainer.removeAllViews()
        changeSelectedTabView(null)
        tabs.onEach { (tabId, tab) ->
            val tabView = createTabView(tab)

            tabView.isEnabled = tab.isEnabled
            if (tabId == selectedTabId) {
                changeSelectedTabView(tabView)
            }
            val tabLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            if (tabsContainer.childCount > 0) {
                tabLayoutParams.marginStart = tabSpacing
            }
            tabsContainer.addView(tabView, tabLayoutParams)
            if (tab.isImageTab) {
                val count = tab.primaryCounter
                    .takeUnless { it < MIN_COUNT }
                    ?: tab.secondaryCounter
                displayCountBadge(tab, tabView, count)
            } else {
                displayCount(tab.id, false, tab.secondaryCounter)
                displayCount(tab.id, true, tab.primaryCounter)
            }
        }

        tabsContainer.doOnNextLayout {
            /*
             Доскролл до выделенного таба, позицию определяем после layout контейнера
             Актуально, если экран будет открываться на табе, который находится в невидимой области.
             */
            scrollToSelectedTab(false)
        }
    }

    private fun createTabView(tab: ToolbarTab): View {
        val layoutRes = when {
            tab.customLayout != 0 -> tab.customLayout
            tab.isImageTab -> R.layout.toolbar_image_tab
            else -> R.layout.toolbar_tab
        }

        return LayoutInflater.from(context).inflate(layoutRes, tabsContainer, false).apply {
            id = tab.id

            val title = findViewById<SbisTextView>(R.id.toolbar_title)
            val text = tab.titleRes.takeUnless { it == ResourcesCompat.ID_NULL }
                ?.let { resources.getString(it) }
                ?: tab.title
            title.text = text
            setOnClickListener { v ->
                if (getVisibleTabs().size < 2) return@setOnClickListener
                changeSelectedTabView(v)
                scrollToSelectedTab()
                listener?.onTabClicked(v.id)
            }
        }
    }

    private fun changeSelectedTabView(tabView: View?) {
        selectedTabView?.isSelected = false
        selectedTabView = tabView
        selectedTabView?.isSelected = shouldShowIndicator(tabs.size)
        val selectedNavxId = selectedTabView?.let { tabs[it.id] }?.navxId
            ?: return
        publishScope.launch {
            _selectionChangeFlow.emit(selectedNavxId)
        }
    }

    private fun ensureSelectionIsMovedWhenExpectedTabGone() {
        val visibleTabs = getVisibleTabs()
        val firstVisibleTabId = visibleTabs.firstOrNull()?.id
        val isFirstVisibleTabSelected = selectedTabView?.id == firstVisibleTabId
        if (visibleTabs.size < 2) {
            changeSelectedTabView(null)
            if (!isFirstVisibleTabSelected) {
                firstVisibleTabId?.let { listener?.onTabClicked(it) }
            }
        } else if (selectedTabView?.isVisible != true) {
            val id = firstVisibleTabId!!
            setSelection(id)
            if (!isFirstVisibleTabSelected) {
                listener?.onTabClicked(id)
            }
        }
    }

    private fun updateTabsMargin() {
        tabsContainer.children.filter { it.isVisible }
            .forEachIndexed { i, view ->
                val lp = view.layoutParams as MarginLayoutParams
                if (i == 0 && lp.leftMargin > 0) {
                    lp.leftMargin = 0
                    requestLayout()
                } else if (i > 0 && lp.leftMargin != tabSpacing) {
                    lp.leftMargin = tabSpacing
                    requestLayout()
                }
            }
    }

    private fun scrollToSelectedTab(smoothScroll: Boolean = true) {
        selectedTabView?.let { view ->
            view.post {
                val scrollX = view.left - tabSpacing / 2
                if (smoothScroll) smoothScrollTo(scrollX, 0) else scrollTo(scrollX, 0)
            }
        }
    }

    private fun getVisibleTabs() = tabsContainer.children.filter { it.isVisible }.toList()

    private inline fun findTabView(@IdRes id: Int, actionWithTab: (view: View) -> Unit) {
        val tabView = tabsContainer.findViewById<View>(id)
        checkNotNullSafe(tabView) { "Unable to get tab view for id $id" }?.run(actionWithTab)
    }

    private inline fun findTabView(navxId: NavxIdDecl, actionWithTab: (view: View) -> Unit) {
        tabsContainer.children.find { tabs[it.id]?.navxId == navxId }
            ?.let(actionWithTab)
    }

    private fun selectView(view: View) {
        changeSelectedTabView(view)
        ensureSelectionIsMovedWhenExpectedTabGone()
        scrollToSelectedTab()
    }

    interface OnTabClickListener {
        fun onTabClicked(@IdRes tabId: Int)
    }

    companion object {
        const val MAX_COUNTER = 9
    }
}
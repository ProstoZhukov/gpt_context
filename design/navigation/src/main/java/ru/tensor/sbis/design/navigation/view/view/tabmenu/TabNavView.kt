package ru.tensor.sbis.design.navigation.view.view.tabmenu

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.adapter.NavigationViewHelper
import ru.tensor.sbis.design.navigation.view.view.AbstractNavViewDelegate
import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.NavListView
import ru.tensor.sbis.design.navigation.view.view.NavigationView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScroll
import ru.tensor.sbis.design.navigation.view.view.tabmenu.list.TabNavigationViewHelper
import ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation.HorizontalOrientationStrategy
import ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation.OrientationStrategy
import ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation.VerticalOrientationStrategy
import ru.tensor.sbis.design.navigation.view.view.util.setTypefaceAndSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDimen
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Навигационная панель с "вкладками" (может называться ННП). Панель поддерживает стилизацию, а так
 * же горизонтальную и вертикальную ориентации. Может быть добавлено управление [DrawerLayout]
 * кнопкой меню при связывании вызовом [bindToNavigationDrawer].
 *
 * **В горизонтальной ориентации**
 * Компонент предоставляет реализацию поведения по скрытию при прокрутке,
 * если размещается в [CoordinatorLayout]. Так же предоставляются методы для принудительного
 * скрытия/отображения реализацией интерфейса[HideableNavigationView]. Для их работы с анимацией
 * компонент должен размещаться непосредственно в [CoordinatorLayout].
 * Компонент имеет подъём [R.dimen.tab_navigation_menu_horizontal_elevation], который можно
 * переопределить в разметке или вызовом [setElevation]. Подъём `0dp` в разметке не поддерживается. Вне зависимости от
 * подъёма, тень у компонента не отображается.
 *
 * **В вертикальной ориентации**
 * Есть возможность указать внешний вид кнопки меню. Для этого нужно указать атрибут
 * [R.attr.menuButtonLayout] в разметке. Если атрибут не указан, будут применены кнопки по
 * уполчанию. Для автоматической установки высоты кнопки (как у toolbar например), можно
 * воспользоваться методом [adjustMenuBtnHeight]. Реализация [HideableNavigationView] не
 * поддерживается в вертикальной ориентации.
 *
 * Для стилизации нужно установить тему в атрибут [R.attr.tabNavStyle]. При реализации собственной
 * кнопки меню (в вертикальной ориентации) можно использовать стиль из атрибута
 * [R.attr.tabNavVerticalMenuBtnStyle], который предоставляется темой [R.attr.tabNavStyle]. Перечень
 * доступных атрибутов смотреть в примерах реализации стилей [R.attr.tabNavVerticalMenuBtnStyle].
 *
 * @see ru.tensor.sbis.design.navigation.view.view.NavView
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
class TabNavView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val delegate: AbstractNavViewDelegate
) : ViewGroup(ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr, defStyleRes),
    NavigationView by delegate,
    CoordinatorLayout.AttachedBehavior,
    HideableNavigationView {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.tabNavStyle,
        @StyleRes defStyleRes: Int = R.style.TabNavView
    ) : this(context, attrs, defStyleAttr, defStyleRes, AbstractNavViewDelegate())

    private val horizontal: Boolean
    private val strategy: OrientationStrategy
    private var viewHolderHelper: NavigationViewHelper

    private var menuVisibility = MenuButtonVisibility.AUTO

    private val name: String

    private val tabList: NavListView
    private val scrollView: FrameLayout

    @Px
    private val tabNavViewHorizontalHeight =
        resources.getDimensionPixelSize(RDesign.dimen.tab_navigation_menu_horizontal_height)

    @Dimension
    private val horizontalDividerHeight = context.getDimen(RDesign.attr.borderThickness_s)

    private val dividerPaint = Paint(ANTI_ALIAS_FLAG)

    @Px
    private val shadowWidth = resources.getDimensionPixelSize(R.dimen.tab_navigation_menu_shadow_width)

    private var startShadow: Drawable? = null
    private var endShadow: Drawable? = null

    private var shadowStartVisibility = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    private var shadowEndVisibility = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    override var pinned: Boolean
        get() = strategy.isViewPinned()
        set(value) = strategy.setViewPinned(this, value)

    /** @SelfDocumented */
    val menuBtn: View

    init {
        // using of wrapped context from parent
        val attributes = getContext().theme.obtainStyledAttributes(
            attrs, R.styleable.TabNavView, defStyleAttr, defStyleRes
        )

        // get view orientation
        horizontal = attributes.run {
            when (val orientation = getInteger(R.styleable.TabNavView_orientation, HORIZONTAL)) {
                HORIZONTAL -> true
                VERTICAL -> false
                else -> throw AssertionError("Unexpected orientation $orientation")
            }
        }
        attributes.getResourceId(R.styleable.TabNavView_tabNavDividerTheme, R.style.TabNavDivider).let {
            context.withStyledAttributes(it, intArrayOf(android.R.attr.background)) {
                dividerPaint.color = getColor(0, Color.MAGENTA)
            }
        }

        if (horizontal) {
            val horizontalShadowTheme = attributes.getResourceId(
                R.styleable.TabNavView_tabNavViewHorizontalShadowTheme,
                R.style.NavViewHorizontalShadow
            )
            this.context.withStyledAttributes(horizontalShadowTheme, intArrayOf(android.R.attr.background)) {
                startShadow = getDrawable(0)?.apply {
                    bounds = Rect(0, 0, shadowWidth, tabNavViewHorizontalHeight)
                }
                endShadow = getDrawable(0)?.apply {
                    bounds = Rect(0, 0, shadowWidth, tabNavViewHorizontalHeight)
                }
            }
        } else {
            val verticalShadowTheme = attributes.getResourceId(
                R.styleable.TabNavView_tabNavViewVerticalShadowTheme,
                R.style.NavViewVerticalShadow
            )
            this.context.withStyledAttributes(verticalShadowTheme, intArrayOf(android.R.attr.background)) {
                startShadow = getDrawable(0)?.apply {
                    bounds = Rect(0, 0, tabNavViewHorizontalHeight, shadowWidth)
                }
                endShadow = getDrawable(0)?.apply {
                    bounds = Rect(0, 0, tabNavViewHorizontalHeight, shadowWidth)
                }
            }
        }
        viewHolderHelper = TabNavigationViewHelper(context, attrs, defStyleAttr, defStyleRes, horizontal)

        // inflate design_menu button view
        menuBtn = attributes.run {
            val defaultMenuLayoutId = if (horizontal)
                R.layout.tab_horizontal_menu_button
            else
                R.layout.tab_vertical_menu_button
            getResourceId(R.styleable.TabNavView_menuButtonLayout, defaultMenuLayoutId)
        }.let { layoutId ->
            inflate(getContext(), layoutId, this)
            // меню создаётся первым
            getChildAt(0)
        }
        menuVisibility = attributes.run {
            when (val code = getInteger(R.styleable.TabNavView_menuVisibility, MenuButtonVisibility.AUTO.code)) {
                MenuButtonVisibility.HIDDEN.code -> MenuButtonVisibility.HIDDEN
                MenuButtonVisibility.VISIBLE.code -> MenuButtonVisibility.VISIBLE
                MenuButtonVisibility.AUTO.code -> MenuButtonVisibility.AUTO
                else -> error("Unexpected code $code")
            }
        }
        if (elevation == 0.0F && horizontal) {
            elevation = resources.getDimension(R.dimen.tab_navigation_menu_horizontal_elevation)
        }
        // предотвращение отображения тени
        outlineProvider = null

        attributes.recycle()

        with(getContext().theme.obtainStyledAttributes(attrs, R.styleable.AbstractNavView, 0, 0)) {
            name = getString(R.styleable.AbstractNavView_name) ?: "${this@TabNavView.javaClass}_$id"
            recycle()
        }

        tabList = NavListView(getContext(), viewHolderHelper)

        strategy = if (horizontal) {
            val horizontalScrollView = SbisHorizontalScrollView(context)
            horizontalScrollView.onScrollStopped = {
                if (horizontalScrollView.canScrollHorizontally(1)) {
                    snapToScroll(horizontalScrollView)
                }
            }

            scrollView = horizontalScrollView
            scrollView.id = R.id.bottom_navigation_recycler
            scrollView.isHorizontalScrollBarEnabled = false
            tabList.orientation = NavListView.Orientation.HORIZONTAL
            horizontalScrollView.overScrollMode = OVER_SCROLL_NEVER
            HorizontalOrientationStrategy()
        } else {
            scrollView = ScrollView(context)
            scrollView.id = R.id.bottom_navigation_recycler_vertical
            scrollView.isVerticalScrollBarEnabled = false
            tabList.orientation = NavListView.Orientation.VERTICAL
            VerticalOrientationStrategy()
        }

        scrollView.addView(tabList, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(scrollView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            shadowStartVisibility = scrollView.getShadowVisibility(-1)
            shadowEndVisibility = scrollView.getShadowVisibility(1)
        }
        viewHolderHelper.sourceName = name
        context.withStyledAttributes(attrs, R.styleable.AbstractNavView, 0, 0) {
            setIsUsedNavigationIcons(getBoolean(R.styleable.AbstractNavView_isUsedNavigationIcons, false))
        }
        delegate.init(viewHolderHelper, tabList, scrollView)
    }

    /**
     * Метод для установки видимости кнопки меню.
     * Заменяет флаг [R.styleable.TabNavView_menuVisibility] из атрибутов.
     *
     * @param menuButtonVisibility - состояние видимости кнопки меню.
     */
    fun setMenuVisibility(menuButtonVisibility: MenuButtonVisibility) {
        menuVisibility = menuButtonVisibility
        tabList.requestLayout()
    }

    /**
     * Метод устанавливает подписку на нажатие кнопки меню, которая будет управлять раскрытием.
     * [drawer]
     *
     * @param drawer контейнер бокового меню.
     */
    fun bindToNavigationDrawer(drawer: DrawerLayout) {
        menuBtn.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers()
            } else {
                drawer.openDrawer(GravityCompat.START, true)
            }
        }
    }

    /**
     * Подгоняет высоту кнопки меню в вертикальной ориентации под высоту [view]. В горизонтальной
     * ориентации метод не оказывает влияния.
     *
     * @param view элемент UI, высоту которого нужно применить к кнопке меню.
     * @param dynamicView нужно установить параметр `true`, если [view] динамически меняет высоту.
     * Если параметр установлен `false` размер кнопки меню будет обновлён только при первом вызове
     * [onLayout] у [view].
     */
    @JvmOverloads
    fun adjustMenuBtnHeight(view: View, dynamicView: Boolean = false) {
        if (!horizontal) {
            val listener = object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    // apply view height
                    menuBtn.layoutParams.height = bottom - top
                    menuBtn.requestLayout()

                    if (!dynamicView) {
                        view.removeOnLayoutChangeListener(this)
                    }
                }
            }
            view.addOnLayoutChangeListener(listener)
        }
    }

    override fun setIsUsedNavigationIcons(isUsed: Boolean) {
        viewHolderHelper.isUsedNavigationIcons = isUsed
        menuBtn.findViewById<SbisTextView>(R.id.tab_icon_text)?.let {
            it.setTypefaceAndSize(isUsed)
            it.text = if (isUsed) resources.getString(RDesign.string.design_nav_icon_menu) else
                resources.getString(RDesign.string.design_mobile_icon_menu)
        }
    }

    override fun isUsedNavigationIcons() = viewHolderHelper.isUsedNavigationIcons

    /**
     * Запрос скрытия панели меню. Выполнение запроса зависит от ориентации и привязанного
     * поведения [CoordinatorLayout.Behavior]
     *
     * @throws UnsupportedOperationException при вызове в вертикальной ориентации
     *
     * @see OrientationStrategy
     * @see BottomHideOnScroll
     */
    override fun hide(animated: Boolean) = strategy.hide(this, animated)

    /**
     * Запрос отображения панели меню. Выполнение запроса зависит от ориентации и привязанного
     * поведения [CoordinatorLayout.Behavior]
     *
     * @throws UnsupportedOperationException при вызове в вертикальной ориентации
     *
     * @see OrientationStrategy
     * @see BottomHideOnScroll
     */
    override fun show(animated: Boolean) = strategy.show(this, animated)

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = strategy.getDefaultBehaviour()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        if (horizontal) {
            val minItemWidth = resources.getDimensionPixelSize(R.dimen.tab_navigation_menu_item_content_width)
            val itemCount = tabList.getVisibleItemCount()
            val menuCount = if (
                menuVisibility == MenuButtonVisibility.VISIBLE ||
                menuVisibility == MenuButtonVisibility.AUTO && menuBtn.hasOnClickListeners()
            ) 1 else 0
            val currentTabsCount = min(getCurrentTubsCount(), itemCount + menuCount)

            val availableSize: Float =
                if ((itemCount + menuCount) > currentTabsCount) {
                    size * MIN_VISIBLE_PERCENTAGE
                } else {
                    size.toFloat()
                }

            // условие необходимо для защиты от деления на ноль
            var itemWidth = if (currentTabsCount > 0) (availableSize / currentTabsCount).roundToInt() else 0
            itemWidth = max(itemWidth, minItemWidth)
            Timber.d(
                buildString {
                    append("ItemCount: $itemCount ")
                    append("menuVisibility: $menuVisibility ")
                    append("listener: ${menuBtn.hasOnClickListeners()} ")
                    append("itemSize:$itemWidth")
                    append("size:$size")
                }
            )
            tabList.itemWidth = itemWidth
            snapToScroll(scrollView as SbisHorizontalScrollView)
            menuBtn.updateLayoutParams<LayoutParams> {
                width = if (menuCount > 0) itemWidth else 0
            }
            strategy.updateMenuVisibility(menuBtn, menuVisibility, (minItemWidth > itemWidth))
            val tabNavViewHeightMeasureSpec = MeasureSpecUtils.makeExactlySpec(tabNavViewHorizontalHeight)
            measureChild(
                menuBtn,
                MeasureSpecUtils.makeExactlySpec(itemWidth),
                MeasureSpecUtils.makeExactlySpec(tabNavViewHorizontalHeight)
            )
            measureChild(scrollView, widthMeasureSpec, tabNavViewHeightMeasureSpec)
            setMeasuredDimension(widthMeasureSpec, tabNavViewHeightMeasureSpec)
        } else {
            measureChild(scrollView, widthMeasureSpec, heightMeasureSpec)
            measureChild(
                menuBtn,
                widthMeasureSpec,
                MeasureSpecUtils.makeExactlySpec(
                    resources.getDimensionPixelSize(R.dimen.tab_navigation_menu_item_height)
                )
            )
            startShadow?.bounds?.right = size
            endShadow?.bounds?.right = size
            strategy.updateMenuVisibility(menuBtn, menuVisibility, false)
            setMeasuredDimension(
                resources.getDimensionPixelSize(R.dimen.tab_navigation_menu_vertical_width),
                heightMeasureSpec
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (horizontal) {
            var dX = 0
            if (menuBtn.isVisible) {
                menuBtn.layout(0, 0, menuBtn.measuredWidth, menuBtn.measuredHeight)
                dX += menuBtn.measuredWidth
            }
            scrollView.layout(dX, 0, r, tabNavViewHorizontalHeight)
        } else {
            var dY = 0
            if (menuBtn.isVisible) {
                menuBtn.layout(0, 0, menuBtn.measuredWidth, menuBtn.measuredHeight)
                dY += menuBtn.measuredHeight
            }
            scrollView.layout(0, dY, r, b)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (horizontal) {
            canvas.drawRect(0f, 0f, width.toFloat(), horizontalDividerHeight, dividerPaint)
        } else {
            canvas.drawRect(
                right - horizontalDividerHeight,
                menuBtn.measuredHeight.toFloat(),
                right.toFloat(), bottom.toFloat(),
                dividerPaint
            )
        }
        if (shadowStartVisibility) {
            drawStartFade(canvas)
        }
        if (shadowEndVisibility) {
            drawEndFade(canvas)
        }
    }

    private fun snapToScroll(horizontalScrollView: SbisHorizontalScrollView) {
        // защита от деления на ноль
        if (tabList.itemWidth == 0) return

        val offset = horizontalScrollView.scrollX % tabList.itemWidth
        val itemIndex =
            horizontalScrollView.scrollX / tabList.itemWidth + if (offset > tabList.itemWidth / 2) 1 else 0
        horizontalScrollView.smoothScrollTo(itemIndex * tabList.itemWidth, 0)
    }

    private fun View.getShadowVisibility(scrollDirection: Int): Boolean =
        canScrollHorizontally(scrollDirection) || canScrollVertically(scrollDirection)

    private fun drawStartFade(canvas: Canvas) {
        if (horizontal) {
            canvas.withTranslation(menuBtn.measuredWidth.toFloat(), 0f) {
                startShadow?.draw(this)
            }
        } else {
            canvas.withTranslation(0f, menuBtn.measuredHeight.toFloat()) {
                startShadow?.draw(this)
            }
        }
    }

    private fun getCurrentTubsCount() = if (resources.getBoolean(RDesign.bool.is_tablet)) {
        TABLET_TABS_COUNT
    } else {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            PORTRAIT_TABS_COUNT
        } else {
            LANDSCAPE_TABS_COUNT
        }
    }

    private fun drawEndFade(canvas: Canvas) {
        if (horizontal) {
            canvas.save()
            canvas.rotate(180f, (canvas.width / 2f), (canvas.height / 2f))
            canvas.translate(0f, 0f)
            endShadow?.draw(canvas)
            canvas.restore()
        } else {
            canvas.save()
            canvas.rotate(180f, (canvas.width / 2f), (canvas.height / 2f))
            canvas.translate(0f, 0f)
            endShadow?.draw(canvas)
            canvas.restore()
        }
    }

    companion object Orientation {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val PORTRAIT_TABS_COUNT = 5
        private const val LANDSCAPE_TABS_COUNT = 7
        private const val TABLET_TABS_COUNT = 9
        private const val MIN_VISIBLE_PERCENTAGE = 0.92f
    }
}

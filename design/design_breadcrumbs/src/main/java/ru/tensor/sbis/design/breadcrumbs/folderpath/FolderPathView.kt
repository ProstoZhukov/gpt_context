@file:Suppress("unused")

package ru.tensor.sbis.design.breadcrumbs.folderpath

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.annotation.StringRes
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import androidx.core.view.isVisible
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.breadcrumbs.R
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.BreadCrumbsView
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.design.breadcrumbs.folderpath.HomeIconVisibility.*
import ru.tensor.sbis.design.breadcrumbs.folderpath.util.resolveFolderPathViewsWidth
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.R as RDesign

private enum class HomeIconVisibility { IN_FOLDER_PATH, IN_BREADCRUMBS, HIDDEN }

/**
 * [View] компонента "Разделитель-заголовок 'Назад'", для использования в конфигурации планшета.
 * Предусматривает совместное отображение заголовка текущего раздела ([CurrentFolderView]) и хлебных крошек
 * ([BreadCrumbsView]).
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=кнопка_назад&g=1)
 *
 * @author us.bessonov
 */
class FolderPathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val currentFolderView = CurrentFolderView(getContext(), attrs)
    private val breadCrumbsView = BreadCrumbsView(getContext(), attrs)
    private val dividerView = createDivider()
    private val centeredHomeIcon = createCenteredHomeIcon(attrs)

    @Px
    private val dividerHeight = resources.getDimensionPixelSize(RDesign.dimen.common_separator_size)
    @Px
    private val endMarginWithBreadCrumbs =
        resources.getDimensionPixelSize(R.dimen.folder_path_view_current_folder_margin_end_with_breadcrumbs)
    @Px
    private val endMarginWithoutBreadCrumbs =
        resources.getDimensionPixelSize(R.dimen.folder_path_view_current_folder_margin_end_without_breadcrumbs)
    @Px
    private val breadCrumbsPaddingStart =
        resources.getDimensionPixelSize(R.dimen.folder_path_view_breadcrumbs_padding_start)

    private var homeIconVisibility = HIDDEN
    private val useCenteredHomeIcon: Boolean

    private var hasItems = false

    init {
        addView(currentFolderView)
        addView(breadCrumbsView)

        with(getContext().obtainStyledAttributes(attrs, R.styleable.FolderPathView, defStyleAttr, 0)) {
            setTitle(getString(R.styleable.FolderPathView_FolderPathView_title).orEmpty())
            useCenteredHomeIcon = getBoolean(R.styleable.FolderPathView_FolderPathView_homeIconCenteredVertically, false)
            setHomeIconVisible(getBoolean(R.styleable.FolderPathView_FolderPathView_homeIconVisible, false))
            recycle()
        }

        if (useCenteredHomeIcon) {
            centeredHomeIcon.setPaddingStart(breadCrumbsPaddingStart)
            addView(centeredHomeIcon)
        }

        addView(dividerView)
        dividerView.isVisible = currentFolderView.isDividerVisible()
        dividerView.setBackgroundColor(currentFolderView.dividerBackgroundColor)
        currentFolderView.hideDivider()

        breadCrumbsView.apply {
            extendClickAreaToParentHeight()
            setPadding(
                breadCrumbsPaddingStart,
                paddingTop,
                resources.getDimensionPixelSize(R.dimen.folder_path_view_breadcrumbs_padding_end),
                paddingBottom
            )
        }

        setBackgroundColor(currentFolderView.backgroundColor)
    }

    /**
     * Устанавливает заголовок текущего раздела
     *
     * @see CurrentFolderView.setTitle
     */
    fun setTitle(text: String) = currentFolderView.setTitle(text)

    /**
     * Устанавливает заголовок текущего раздела
     *
     * @see CurrentFolderView.setTitle
     */
    fun setTitle(@StringRes textRes: Int) = currentFolderView.setTitle(textRes)

    /**
     * @see [BreadCrumbsView.setItems]
     */
    fun setItems(list: List<BreadCrumb>) {
        breadCrumbsView.setItems(list)
        hasItems = list.isNotEmpty()
        updateBreadCrumbsVisibility()
    }

    /**
     * Задает обработчик нажатий на заголовок со стрелкой для перехода назад.
     */
    fun setCurrentFolderClickListener(clickListener: () -> Unit) {
        currentFolderView.setOnClickListener { clickListener() }
    }

    /**
     * @see [BreadCrumbsView.setItemClickListener]
     */
    fun setItemClickListener(clickListener: (item: BreadCrumb) -> Unit) {
        breadCrumbsView.setItemClickListener(clickListener)
    }

    /**
     * @see [BreadCrumbsView.setHomeIconClickListener]
     */
    fun setHomeIconClickListener(clickListener: () -> Unit) {
        breadCrumbsView.setHomeIconClickListener(clickListener)
        centeredHomeIcon.setOnClickListener { clickListener() }
    }

    /**
     * @see [BreadCrumbsView.setHomeIconVisible]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setHomeIconVisible(isVisible: Boolean) {
        homeIconVisibility = when {
            !isVisible          -> HIDDEN
            useCenteredHomeIcon -> IN_FOLDER_PATH
            else                -> IN_BREADCRUMBS
        }
        breadCrumbsView.setHomeIconVisible(homeIconVisibility == IN_BREADCRUMBS)
        centeredHomeIcon.isVisible = homeIconVisibility == IN_FOLDER_PATH
        updateBreadCrumbsVisibility()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidthWithPadding = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = availableWidthWithPadding - paddingStart - paddingEnd
        val wrapHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(heightMeasureSpec),
            MeasureSpec.AT_MOST
        )
        val dividerHeightMeasureSpec = MeasureSpec.makeMeasureSpec(dividerHeight, MeasureSpec.EXACTLY)

        // определяем желаемую ширину заголовка и хлебных крошек
        if (breadCrumbsView.isVisible) {
            measureChildView(currentFolderView, availableWidth, wrapHeightMeasureSpec, MeasureSpec.AT_MOST)
            measureChildView(breadCrumbsView, availableWidth, wrapHeightMeasureSpec, MeasureSpec.AT_MOST)
        } else {
            measureChildView(currentFolderView, availableWidth, wrapHeightMeasureSpec, MeasureSpec.EXACTLY)
        }
        if (useCenteredHomeIcon) {
            val homeIconHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                currentFolderView.measuredHeight,
                MeasureSpec.EXACTLY
            )
            val homeIconAvailableWidth = if (centeredHomeIcon.isVisible) availableWidth else 0
            measureChildView(centeredHomeIcon, homeIconAvailableWidth, homeIconHeightMeasureSpec, MeasureSpec.AT_MOST)
        }
        measureChildView(dividerView, availableWidth, dividerHeightMeasureSpec, MeasureSpec.EXACTLY)

        // если заголовок и хлебные крошки не помещаются полностью, ограничиваем ширину по крайней мере одного из них
        with(
            resolveFolderPathViewsWidth(
                desiredFolderWidth = currentFolderView.measuredWidth,
                desiredBreadCrumbsWidth = if (breadCrumbsView.isVisible) breadCrumbsView.measuredWidth else 0,
                homeIconWidth = centeredHomeIcon.measuredWidth,
                availableWidth = availableWidth
            )
        ) {
            if (folderWidth != WRAP_CONTENT) {
                measureChildView(currentFolderView, folderWidth, wrapHeightMeasureSpec)
            }
            if (breadCrumbsWidth != WRAP_CONTENT) {
                measureChildView(breadCrumbsView, breadCrumbsWidth, wrapHeightMeasureSpec)
            }
        }

        val measuredWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            availableWidthWithPadding
        } else {
            currentFolderView.measuredWidth + centeredHomeIcon.measuredWidth + breadCrumbsView.measuredWidth +
                    paddingStart + paddingEnd
        }
        setMeasuredDimension(measuredWidth, currentFolderView.measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val dividerBottom = bottom - top
        val dividerTop = dividerBottom - dividerView.measuredHeight
        dividerView.layout(0, dividerTop, right - left, dividerBottom)

        val currentFolderRight = paddingLeft + currentFolderView.measuredWidth
        currentFolderView.layout(paddingLeft, 0, currentFolderRight, currentFolderView.measuredHeight)

        if (centeredHomeIcon.isVisible) {
            val iconRight = currentFolderRight + centeredHomeIcon.measuredWidth
            centeredHomeIcon.layout(currentFolderRight, 0, iconRight, currentFolderView.measuredHeight)
        }

        if (!breadCrumbsView.isVisible) return

        val breadCrumbsTop = currentFolderView.baseline - breadCrumbsView.baseline
        val breadCrumbsBottom = breadCrumbsTop + breadCrumbsView.measuredHeight
        val breadCrumbsLeft = currentFolderRight + centeredHomeIcon.measuredWidth
        val breadCrumbsRight = breadCrumbsLeft + breadCrumbsView.measuredWidth
        breadCrumbsView.layout(breadCrumbsLeft, breadCrumbsTop, breadCrumbsRight, breadCrumbsBottom)
    }

    override fun getBaseline() = currentFolderView.baseline

    private fun measureChildView(
        child: View,
        @Px
        width: Int,
        heightMeasureSpec: Int,
        widthMode: Int = MeasureSpec.EXACTLY
    ) {
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode)
        measureChild(child, widthMeasureSpec, heightMeasureSpec)
    }

    private fun createDivider() = View(context)

    private fun createCenteredHomeIcon(attrs: AttributeSet?) = SbisTextView(
        ThemeContextBuilder(
            context,
            defStyleAttr = R.attr.breadCrumbsViewTheme,
            defaultStyle = R.style.BreadCrumbsViewDefaultTheme
        ).build(),
        attrs,
        R.attr.BreadCrumbsView_homeIconStyle
    ).apply { gravity = Gravity.CENTER_VERTICAL }

    private fun updateBreadCrumbsVisibility() {
        breadCrumbsView.isVisible = hasItems || homeIconVisibility == IN_BREADCRUMBS
        updateFolderTitleMarginEnd()
        updateBreadCrumbsPaddingStart()
    }

    private fun updateFolderTitleMarginEnd() {
        currentFolderView.setTitleMarginEnd(
            if (breadCrumbsView.isVisible || homeIconVisibility == IN_FOLDER_PATH) {
                endMarginWithBreadCrumbs
            } else {
                endMarginWithoutBreadCrumbs
            }
        )
    }

    private fun updateBreadCrumbsPaddingStart() {
        breadCrumbsView.setPaddingStart(if (homeIconVisibility == IN_FOLDER_PATH) 0 else breadCrumbsPaddingStart)
    }

    private fun View.setPaddingStart(@Px paddingStart: Int) {
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }
}
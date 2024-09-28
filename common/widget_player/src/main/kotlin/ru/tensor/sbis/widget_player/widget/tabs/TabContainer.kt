package ru.tensor.sbis.widget_player.widget.tabs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import ru.tensor.sbis.design.tabs.api.SbisTabsStyle
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout

/**
 * Виджет вкладок.
 *
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class TabContainer(
    context: Context,
    private val options: TabOptions
) : VerticalBlockLayout(context) {

    private val tabsView = SbisTabsView(context).apply {
        isBottomBorderVisible = true
        style = SbisTabsStyle(
            customTitleFontSize = SbisDimen.Px(options.titleFontSize.getValuePx(context))
        )
        setOnTabClickListener {
            updateSelectedTabContent(selectedIndex = tabs.indexOf(it))
        }
    }
    private val contentLayout = object : FrameLayout(context) {
        override fun onViewAdded(child: View) {
            super.onViewAdded(child)
            updateSelectedTabContent(
                fromIndex = childCount - 1,
                selectedIndex = tabsView.selectedTabIndex
            )
        }
    }.apply {
        layoutParams = generateDefaultLayoutParams()
        with(options) {
            setPadding(
                contentPaddingLeft.getValuePx(context),
                contentPaddingTop.getValuePx(context),
                contentPaddingRight.getValuePx(context),
                contentPaddingBottom.getValuePx(context)
            )
        }
    }

    val childrenContainer: ViewGroup = contentLayout

    init {
        addView(tabsView)
        addView(contentLayout)
        background = getBackgroundDrawable()
    }

    /**
     * Устанавливает список вкладок для рендера.
     */
    fun setTabData(data: TabContainerData) {
        tabsView.selectedTabIndex = data.selectedIndex
        tabsView.tabs = data.tabs
        updateSelectedTabContent(selectedIndex = tabsView.selectedTabIndex)
    }

    private fun getBackgroundDrawable() = GradientDrawable().apply {
        cornerRadius = options.borderRadius.getValue(context)
        setStroke(
            options.borderThickness.getValuePx(context),
            options.borderColor.getValue(context)
        )
    }

    private fun updateSelectedTabContent(fromIndex: Int = 0, selectedIndex: Int) {
        for (i in fromIndex until contentLayout.childCount) {
            contentLayout.getChildAt(i).isVisible = i == selectedIndex
        }
    }
}
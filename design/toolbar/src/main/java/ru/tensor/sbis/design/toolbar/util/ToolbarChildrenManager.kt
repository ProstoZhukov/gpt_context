package ru.tensor.sbis.design.toolbar.util

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.SbisToolbarSpinner
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.view_ext.SimplifiedTextView
import ru.tensor.sbis.design.view_ext.UiUtils
import ru.tensor.sbis.design.R as DesignR

/**
 * Предназначен для ленивой инициализации и добавления в иерархию [View] элементов [Toolbar].
 *
 * @author us.bessonov
 */
internal class ToolbarChildrenManager(
    private val toolbar: ViewGroup,
    private val containerContext: Context
) {

    @get:Px
    private val iconWidth by lazy {
        toolbar.resources.getDimensionPixelSize(DesignR.dimen.size_title1_scaleOff)
    }

    private val toolbarLeftPadding by lazy {
        Offset.M.getDimenPx(toolbar.context)
    }
    private val toolbarButtonsPadding by lazy {
        toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_buttons_padding)
    }

    private val iconStyleProvider by lazy(::ToolbarStylesProvider)

    private val titleStyleProvider by lazy(::ToolbarStylesProvider)

    /** @SelfDocumented */
    val tabLayout = LazyViewContainer(::createToolbarTabs)

    /** @SelfDocumented */
    val centerText = LazyViewContainer(::createCenterText)

    // region Left container
    /**
     * Иконка в [extraLeftPanel]
     */
    val extraLeftIcon = LazyViewContainer(::createExtraLeftIcon)

    /**
     * Контейнер с иконкой, левее [leftPanel]
     */
    val extraLeftPanel =
        LazyViewContainer(::createExtraLeftPanelToggleArea).apply {
            add(extraLeftIcon)
        }

    /**
     * Иконка в [leftPanel] (обычно стрелка для возврата)
     */
    val leftIcon = LazyViewContainer(::createLeftIcon)

    val leftCounter = LazyViewContainer(::createLeftCounter)

    /**
     * Контейнер с иконкой, расположенный слева
     */
    val leftPanel = LazyViewContainer(::createLeftPanelToggleArea).apply {
        add(leftIcon)
        add(leftCounter)
    }

    /**
     * Заголовок слева
     */
    val leftText = LazyViewContainer(::createLeftText)

    /**
     * Контейнер для произвольного [View]
     */
    val customViewContainer = LazyViewContainer(::createCustomViewContainer)

    private val leftContainer = LazyViewContainer<View> { createLeftContainer() }.apply {
        add(extraLeftPanel)
        add(leftPanel)
        add(leftText)
        add(customViewContainer)
    }
    // endregion

    // region Right container
    /**
     * Заголовок, отображаемый в правой части
     */
    val rightText = LazyViewContainer(::createRightText)

    /**
     * Иконка в [rightPanel1]
     */
    val rightIcon1 = LazyViewContainer {
        createIcon(R.id.toolbar_right_icon1, isRightAlignment = true)
    }

    /**
     * Первый контейнер с иконкой и бейджем, расположенный справа
     */
    val rightPanel1 = LazyViewContainer {
        createRightPanelToggleArea(R.id.toolbar_right_panel_toggle_area1, R.dimen.toolbar_buttons_padding)
    }.apply {
        add(rightIcon1)
    }

    /**
     * Спиннер в [rightPanel2]
     */
    val spinner = LazyViewContainer(::createSpinner)

    /**
     * Индикатор загрузки в [rightContainer].
     */
    val loadingIndicator = LazyViewContainer(::createLoadingIndicator)

    /**
     * Иконка в [rightPanel2]
     */
    val rightIcon2 = LazyViewContainer {
        createIcon(R.id.toolbar_right_icon2, true, DesignR.string.design_mobile_icon_dots_vertical)
    }

    /**
     * Иконка отсутствия сети в [rightContainer].
     */
    val syncIcon = LazyViewContainer {
        createIcon(R.id.toolbar_sync_icon, text = DesignR.string.design_mobile_icon_wifi_none).apply {
            updateLayoutParams<MarginLayoutParams> {
                marginEnd = toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_right_buttons_inner_padding)
            }
        }
    }

    /**
     * Второй контейнер, расположенный справа, с иконкой, бейджем, индикатором загрузки и спиннером
     */
    val rightPanel2 = LazyViewContainer {
        createRightPanelToggleArea(R.id.toolbar_right_panel_toggle_area2, R.dimen.toolbar_right_buttons_inner_padding)
    }.apply {
        add(spinner)
        add(rightIcon2)
    }
    val rightContainer = LazyViewContainer<View> { createRightContainer() }.apply {
        add(rightText)
        add(loadingIndicator)
        add(syncIcon)
        add(rightPanel1)
        add(rightPanel2)
    }
    // endregion

    val divider = LazyViewContainer { createDivider() }

    val shadow = LazyViewContainer { createShadow() }

    /** [R.id.toolbar] */
    val toolbarContainer = LazyViewContainer<View> {
        createContentContainer().apply { toolbar.addView(this) }
    }.apply {
        add(tabLayout)
        add(centerText)
        add(leftContainer)
        add(rightContainer)
        add(divider)
        add(shadow)
    }

    private fun createToolbarTabs() = ToolbarTabLayout(toolbar.context).apply {
        layoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.START_OF, R.id.toolbar_right_container)
            leftMargin = toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_tabs_left_margin)
            rightMargin = toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_tabs_right_margin)
        }
        isVisible = false
        id = R.id.toolbar_tabs
        rightContainer.get()
    }

    private fun createCenterText() = createTitleView().apply {
        layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
            val marginHorizontal =
                toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_center_text_margin_horizontal)
            leftMargin = marginHorizontal
            rightMargin = marginHorizontal
        }
        isVisible = false
        gravity = Gravity.CENTER
        id = R.id.toolbar_center_text
    }

    private fun createLeftText() = createTitleView().apply {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            leftMargin = toolbarLeftPadding
            gravity = Gravity.CENTER
        }
        isVisible = false
        id = R.id.toolbar_left_text
    }

    private fun createRightText() = createTitleView().apply {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            leftMargin = toolbarButtonsPadding
            gravity = Gravity.CENTER_VERTICAL
        }
        isVisible = false
        id = R.id.toolbar_right_text
    }

    private fun createExtraLeftIcon() = createIconView().apply {
        layoutParams = FrameLayout.LayoutParams(iconWidth, MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
        setTextColor(
            ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf()
                ),
                intArrayOf(
                    context.getThemeColorInt(DesignR.attr.readonlyMarkerColor),
                    context.getThemeColorInt(DesignR.attr.iconColor)
                )
            )
        )
        setText(DesignR.string.design_mobile_icon_search)
        id = R.id.toolbar_extra_left_icon
    }

    private fun createLeftCounter() = SbisCounter(
        toolbar.context,
        null
    ).apply {
        layoutParams = LinearLayout.LayoutParams(this.background.minimumWidth, MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = Offset.X3S.getDimenPx(toolbar.context)
        }
        id = R.id.toolbar_left_counter
    }

    private fun createLeftIcon() = SbisTextView(
        toolbar.context,
        null,
        R.attr.Toolbar_iconStyle,
        R.style.ToolbarIconStyle
    ).apply {
        typeface = TypefaceManager.getSbisMobileIconTypeface(toolbar.context)
        setLeftPadding(Offset.M.getDimenPx(context))
        layoutParams = LinearLayout.LayoutParams(paddingStart + iconWidth, MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        this.isVisible = false
        setTextColor(
            ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf()
                ),
                intArrayOf(
                    context.getThemeColorInt(DesignR.attr.readonlyMarkerColor),
                    context.getThemeColorInt(DesignR.attr.toolbarBackIconColor)
                )
            )
        )
        text = text
        id = R.id.toolbar_left_icon
    }

    private fun createIcon(
        @IdRes viewId: Int,
        isVisible: Boolean = false,
        @StringRes text: Int? = null,
        isRightAlignment: Boolean = false
    ) =
        SimplifiedTextView(
            toolbar.context,
            null,
            R.attr.Toolbar_iconStyle,
            R.style.ToolbarIconStyle
        ).apply {
            if (isRightAlignment) gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams = LinearLayout.LayoutParams(iconWidth, MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
            this.isVisible = isVisible
            setTextColor(
                ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_enabled),
                        intArrayOf()
                    ),
                    intArrayOf(
                        context.getThemeColorInt(DesignR.attr.readonlyMarkerColor),
                        context.getThemeColorInt(DesignR.attr.iconColor)
                    )
                )
            )
            text?.let(::setText)
            id = viewId
        }

    private fun createCustomViewContainer() = FrameLayout(toolbar.context).apply {
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        setLeftPadding(toolbarLeftPadding)
        isVisible = false
        id = R.id.toolbar_custom_view_container
    }

    private fun createSpinner() = SbisToolbarSpinner(getThemedContext(R.style.ToolbarSpinnerStyle)).apply {
        layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
            weight = 1f
        }
        id = R.id.toolbar_spinner
    }

    private fun createLoadingIndicator() = SbisLoadingIndicator(toolbar.context).apply {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginEnd = toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_right_buttons_inner_padding)
        }
        progressSize = InlineHeight.X6S
        isVisible = false
        id = R.id.toolbar_loading_indicator
    }

    private fun createLeftPanelToggleArea() = LinearLayout(toolbar.context).apply {
        val panelWidth =
            toolbar.context.resources.getDimensionPixelSize(R.dimen.toolbar_left_panel_width)
        layoutParams = LinearLayout.LayoutParams(panelWidth, MATCH_PARENT).apply {
            rightMargin = toolbar.resources.getDimensionPixelSize(R.dimen.toolbar_left_panel_right_margin)
        }
        id = R.id.toolbar_left_panel_toggle_area
    }

    private fun createExtraLeftPanelToggleArea() = FrameLayout(toolbar.context).apply {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
        updatePadding(
            left = toolbarButtonsPadding,
            right = toolbarButtonsPadding
        )
        isVisible = false
        id = R.id.toolbar_extra_left_icon_toggle_area
    }

    private fun createRightPanelToggleArea(@IdRes viewId: Int, @DimenRes paddingLeft: Int) =
        LinearLayout(toolbar.context).apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            updatePadding(
                left = toolbar.resources.getDimensionPixelSize(paddingLeft),
                right = Offset.M.getDimenPx(toolbar.context)
            )
            isVisible = false
            id = viewId
        }

    private fun createTitleView() = SimplifiedTextView(
        toolbar.context,
        null,
        R.attr.Toolbar_titleStyle,
        R.style.ToolbarTitleText,
        styleProvider = titleStyleProvider.textStyleProvider
    )

    private fun createIconView() = SimplifiedTextView(
        toolbar.context,
        null,
        R.attr.Toolbar_iconStyle,
        R.style.ToolbarIconStyle,
        styleProvider = iconStyleProvider.textStyleProvider
    )

    private fun createLeftContainer() = LinearLayout(toolbar.context).apply {
        layoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            addRule(RelativeLayout.LEFT_OF, R.id.toolbar_right_container)
        }
        id = R.id.toolbar_left_container
        rightContainer.get()
    }

    private fun createRightContainer() = LinearLayout(toolbar.context).apply {
        layoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            rightMargin = resources.getDimensionPixelSize(R.dimen.toolbar_right_buttons_gone_margin)
        }
        id = R.id.toolbar_right_container
    }

    private fun createShadow() = View(toolbar.context).apply {
        layoutParams = RelativeLayout.LayoutParams(
            MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.toolbar_shadow_height)
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            bottomMargin = resources.getDimensionPixelSize(R.dimen.toolbar_shadow_margin_bottom)
        }
        setBackgroundResource(R.drawable.toolbar_shadow_top_to_bottom)
        id = R.id.toolbar_shadow
    }

    private fun createDivider() = View(toolbar.context, null, View.NO_ID, R.style.ToolbarDividerStyle).apply {
        layoutParams = RelativeLayout.LayoutParams(
            MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.toolbar_divider_height)
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
        id = R.id.toolbar_divider
    }

    private fun createContentContainer() = RelativeLayout(
        toolbar.context,
        null,
        DesignR.attr.Toolbar_container_style,
        DesignR.style.SbisToolbar
    ).apply {
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, UiUtils.getToolBarHeight(containerContext))
        setPadding(0)
        id = R.id.toolbar
    }

    private fun getThemedContext(@StyleRes style: Int) =
        ThemeContextBuilder(toolbar.context, defaultStyle = style).build()
}

private class ToolbarStylesProvider : CanvasStylesProvider()
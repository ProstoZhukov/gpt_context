package ru.tensor.sbis.design.toolbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.BorderColor
import ru.tensor.sbis.design.toolbar.util.LazyViewContainer
import ru.tensor.sbis.design.toolbar.util.ToolbarChildrenManager
import ru.tensor.sbis.design.toolbar.util.ToolbarSyncIndicatorsState
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.view_ext.SimplifiedTextView

//region Константы из аттрибутов R.styleable.Toolbar
private const val CHILD_VISIBLE = 0
private const val CHILD_INVISIBLE = 1
private const val CHILD_GONE = 2
//endregion

/**
 * Custom Toolbar
 *
 * @author us.bessonov
 */
class Toolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.Toolbar_theme,
    @StyleRes defStyleRes: Int = ID_NULL
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    @Px
    private val centeredLeftTextMarginEnd =
        resources.getDimensionPixelSize(R.dimen.toolbar_center_text_margin_horizontal)

    @Px
    private val previewSize = resources.getDimensionPixelSize(R.dimen.toolbar_size)

    private val childrenManager: ToolbarChildrenManager

    val leftPanel: View
        get() = childrenManager.leftPanel.get()

    val leftIcon: SbisTextView
        get() = childrenManager.leftIcon.get()

    private val leftCounter: SbisCounter
        get() = childrenManager.leftCounter.get()

    val extraLeftPanel: View
        get() = childrenManager.extraLeftPanel.get()

    private val extraLeftIcon: SimplifiedTextView
        get() = childrenManager.extraLeftIcon.get()

    val leftText: SimplifiedTextView
        get() = childrenManager.leftText.get()

    val rightText: SimplifiedTextView
        get() = childrenManager.rightText.get()

    val centerText: SimplifiedTextView
        get() = childrenManager.centerText.get()

    /**
     * Контейнер для прикладного контента. Прикладную view нужно создавать на основе [Toolbar.getContext] чтобы
     * были доступны стилевые атрибуты из темы [Toolbar]
     */
    val customViewContainer: FrameLayout
        get() = childrenManager.customViewContainer.get()

    val rightPanel1: View
        get() = childrenManager.rightPanel1.get()

    val rightIcon1: SimplifiedTextView
        get() = childrenManager.rightIcon1.get()

    val rightPanel2: View
        get() = childrenManager.rightPanel2.get()

    val rightIcon2: SimplifiedTextView
        get() = childrenManager.rightIcon2.get()

    val spinner: SbisToolbarSpinner
        get() = childrenManager.spinner.get()

    /**
     * Индикатор загрузки в rightContainer.
     *
     * @see changeSyncIndicatorsState
     */
    val loadingIndicator: SbisLoadingIndicator
        get() = childrenManager.loadingIndicator.get()

    /**
     * Иконка для отображения отсутствия сети в rightContainer.
     *
     * @see changeSyncIndicatorsState
     */
    private val syncIcon: SimplifiedTextView
        get() = childrenManager.syncIcon.get()

    val tabLayout: ToolbarTabLayout
        get() = childrenManager.tabLayout.get()

    @JvmField
    var toolbar: View

    val toolbarContainer: View
        get() = childrenManager.toolbarContainer.get()

    val divider: View
        get() = childrenManager.divider.get()

    val shadow: View
        get() = childrenManager.shadow.get()

    init {
        clipChildren = false

        val toolbarContainerContext = ThemeContextBuilder(
            context,
            attrs,
            ru.tensor.sbis.design.R.attr.Toolbar_container_style,
            defStyleRes
        ).build()

        childrenManager = ToolbarChildrenManager(this@Toolbar, toolbarContainerContext)

        isClickable = true // prevent passing through click events
        toolbar = this

        val typedArray = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.Toolbar,
            defStyleAttr,
            defStyleRes
        )

        try {
            //region Visibility
            typedArray.apply {
                with(childrenManager) {
                    setVisibility(R.styleable.Toolbar_toolbarContainerVisibility, toolbarContainer, CHILD_VISIBLE)
                    setVisibility(R.styleable.Toolbar_tabLayoutVisibility, tabLayout)

                    setVisibility(R.styleable.Toolbar_leftPanelVisibility, leftPanel)
                    setVisibility(R.styleable.Toolbar_extraLeftPanelVisibility, extraLeftPanel)
                    setVisibility(R.styleable.Toolbar_leftIconVisibility, leftIcon)
                    setVisibility(R.styleable.Toolbar_leftTextVisibility, leftText)

                    setVisibility(R.styleable.Toolbar_customViewContainerVisibility, customViewContainer)

                    setVisibility(R.styleable.Toolbar_rightPanel1Visibility, rightPanel1)
                    setVisibility(R.styleable.Toolbar_rightIcon1Visibility, rightIcon1)
                    setVisibility(R.styleable.Toolbar_rightTextVisibility, rightText)

                    setVisibility(R.styleable.Toolbar_rightPanel2Visibility, rightPanel2)
                    setVisibility(R.styleable.Toolbar_rightIcon2Visibility, rightIcon2)
                    setVisibility(R.styleable.Toolbar_spinnerVisibility, spinner)

                    setVisibility(R.styleable.Toolbar_centerTextVisibility, centerText)
                }
                getColor(R.styleable.Toolbar_Toolbar_dividerColor, BorderColor.DEFAULT.getValue(context)).let {
                    divider.setBackgroundColor(it)
                }

                setVisibility(divider, getInt(R.styleable.Toolbar_toolbarDivider, CHILD_GONE))
                setVisibility(shadow, getInt(R.styleable.Toolbar_toolbarShadow, CHILD_GONE))
                //endregion

                //region Icons/Text
                getString(R.styleable.Toolbar_leftIconText)?.let { leftIcon.text = it }
                getInt(R.styleable.Toolbar_leftCounterNumber, 0).let { leftCounter.counter = it }
                getString(R.styleable.Toolbar_extraLeftIconText)?.let { extraLeftIcon.text = it }
                getString(R.styleable.Toolbar_leftTextText)?.let { leftText.text = it }
                leftText.gravity = when (getInteger(R.styleable.Toolbar_leftTextGravity, 0)) {
                    1 -> Gravity.CENTER
                    2 -> Gravity.RIGHT
                    else -> Gravity.LEFT
                }

                getString(R.styleable.Toolbar_rightIcon1Text)?.let { rightIcon1.text = it }

                getString(R.styleable.Toolbar_rightTextText)?.let { rightText.text = it }
                if (hasValue(R.styleable.Toolbar_rightIcon2Text)) {
                    rightIcon2.text = getString(R.styleable.Toolbar_rightIcon2Text)
                }

                getString(R.styleable.Toolbar_centerTextText)?.let { centerText.text = it }
            }
            //endregion
        } finally {
            typedArray.recycle()
        }
        extractBackgroundFromContainer()
    }

    /**
     * Задать основной цвет тулбара.
     */
    fun setMainColor(@ColorInt color: Int) {
        setBackgroundColor(color)
    }

    /**
     * Скрывает rightContainer, занимающий некоторое место даже, если скрыто его содержимое
     */
    fun hideRightContainer() {
        childrenManager.rightContainer.get().isVisible = false
    }

    /**
     * Метод для более удобного управления состоянием видимости
     * индикаторов загрузки и отсутствия сети при синхронизации данных.
     *
     * При [syncState] NOT_RUNNING делаем [loadingIndicator] gone и [syncIcon] gone.
     *
     * При [syncState] RUNNING делаем [loadingIndicator] visible, а [syncIcon] gone.
     *
     * При [syncState] NO_INTERNET делаем [loadingIndicator] gone, а [syncIcon] visible.
     *
     * @see ToolbarSyncIndicatorsState
     */
    fun changeSyncIndicatorsState(syncState: ToolbarSyncIndicatorsState) = when (syncState) {
        ToolbarSyncIndicatorsState.NOT_RUNNING -> {
            loadingIndicator.isVisible = false
            syncIcon.isVisible = false
        }

        ToolbarSyncIndicatorsState.RUNNING -> {
            loadingIndicator.isVisible = true
            syncIcon.isVisible = false
        }

        ToolbarSyncIndicatorsState.NO_INTERNET -> {
            loadingIndicator.isVisible = false
            syncIcon.isVisible = true
        }
    }

    /**
     * Возвращает кастомный [View], добавленный в шапку, если он соответствует заданному типу
     */
    inline fun <reified VIEW : View> getCustomView(): VIEW? {
        return customViewContainer.getChildAt(0) as? VIEW?
            ?: getChildAt(1) as? VIEW?
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 1) {
            val customView = getChildAt(1)
            removeView(customView)
            customViewContainer.addView(customView, 0, customView.layoutParams)
        }

        addMarginToCenteredLeftTextIfNeeded()
        val heightSpec = if (isInEditMode && MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(previewSize, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
        if (isInEditMode) toolbarContainer.measure(widthMeasureSpec, heightSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        outlineProvider = CustomOutline(w, h)
    }

    private fun TypedArray.setVisibility(
        @StyleableRes index: Int,
        view: LazyViewContainer<*>,
        defaultVisibility: Int = CHILD_GONE,
    ) {
        val visibility = getInt(index, defaultVisibility)
        if (visibility == CHILD_GONE) {
            view.defaultVisibility = View.GONE
            return
        }
        setVisibility(view.get(), visibility)
    }

    private fun setVisibility(view: View, visibility: Int) {
        view.visibility = when (visibility) {
            CHILD_VISIBLE -> View.VISIBLE
            CHILD_INVISIBLE -> View.INVISIBLE
            else -> View.GONE
        }
    }

    private fun extractBackgroundFromContainer() {
        val contentBackground = toolbarContainer.background
        toolbarContainer.background = null
        if (background == null) {
            background = contentBackground
        }
    }

    /**
     * Для типового случая, когда текст может отображаться как слева, так и по центру, при обнаружении второго варианта,
     * и отсутствии содержимого справа, заголовку добавляется симметричный отступ справа для корректного выравнивания
     */
    private fun addMarginToCenteredLeftTextIfNeeded() = with(childrenManager) {
        leftText.instance?.let {
            (it.layoutParams as MarginLayoutParams).rightMargin = if (
                it.gravity == Gravity.CENTER &&
                leftIcon.instance?.isVisible == true &&
                leftPanel.instance?.isVisible == true &&
                rightPanel1.instance?.isVisible != true &&
                rightPanel2.instance?.isVisible != true

            ) {
                it.layoutParams.width = LayoutParams.MATCH_PARENT
                centeredLeftTextMarginEnd - rightContainer.get().marginEnd
            } else {
                it.layoutParams.width = LayoutParams.WRAP_CONTENT
                0
            }
        }
    }
}

private class CustomOutline(private var width: Int, private var height: Int) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        outline.setRect(0, 0, width, height)
    }
}
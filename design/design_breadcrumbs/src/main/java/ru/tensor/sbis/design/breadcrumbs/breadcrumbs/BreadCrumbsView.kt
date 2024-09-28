package ru.tensor.sbis.design.breadcrumbs.breadcrumbs

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.breadcrumbs.R
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import androidx.core.view.isVisible
import ru.tensor.sbis.design.utils.getExpectedTextWidth
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util.ELLIPSIS_ID
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util.prepareBreadCrumbs
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util.showPreview
import ru.tensor.sbis.design.breadcrumbs.databinding.BreadcrumbsViewBinding
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

private const val FIRST_TITLE_INDEX = 1
private const val HOME_ICON_INDEX = 0

/**
 * [View] компонента "Хлебные крошки".
 * Предназначен для отображения пути от корня до некоторого дочернего элемента в иерархии. При навигации позволяет
 * вернуться в корень, либо на один из промежуточных экранов. Поддерживает подсветку совпадений в элементах, если они
 * представляют результаты поиска.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=кнопка_назад&g=1)
 * - [Стандарт для Web](http://axure.tensor.ru/standarts/v7/хлебные_крошки__версия_02_.html)
 *
 * @author us.bessonov
 */
class BreadCrumbsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.breadCrumbsViewTheme
) : LinearLayout(
    ThemeContextBuilder(
        context,
        defStyleAttr = defStyleAttr,
        defaultStyle = R.style.BreadCrumbsViewDefaultTheme
    ).build(),
    attrs,
    defStyleAttr
) {

    private val titlePaint: TextPaint
    @ColorInt
    private val titleHighlightColor: Int
    @Px
    private var arrowWidth = getArrowWidth()
    @Px
    private var fullAvailableWidth = 0
    @Px
    private var availableForItemsWidth = 0
    private var isClickAreaHeightExtended = false

    private val items = mutableListOf<BreadCrumb>()

    private var itemClickListener: ((item: BreadCrumb) -> Unit)? = null

    private val viewBinding = BreadcrumbsViewBinding.inflate(LayoutInflater.from(getContext()), this)

    init {
        with(getContext().obtainStyledAttributes(attrs, R.styleable.BreadCrumbsView, defStyleAttr, 0)) {
            setHomeIconVisible(getBoolean(R.styleable.BreadCrumbsView_BreadCrumbsView_homeIconVisible, true))
            recycle()
        }

        with(createTitleView(BreadCrumb("", ""))) {
            titlePaint = paint
            titleHighlightColor = getHighlightColor()
        }

        orientation = HORIZONTAL

        if (isInEditMode) showPreview()
    }

    /**
     * Задаёт список хлебных крошек
     */
    fun setItems(list: List<BreadCrumb>) {
        if (items == list) return
        items.clear()
        items.addAll(list)
        configureViews()
    }

    /**
     * Задаёт обработчик нажатий на элементы
     *
     * @param clickListener лямбда, принимающая на вход элемент, по которому совершено нажатие
     */
    fun setItemClickListener(clickListener: (item: BreadCrumb) -> Unit) {
        itemClickListener = clickListener
    }

    /**
     * Задаёт обработчик нажатий на иконку домика (для возврата в корневой раздел)
     */
    fun setHomeIconClickListener(clickListener: () -> Unit) {
        viewBinding.breadcrumbsHomeIcon.setOnClickListener {
            clickListener.invoke()
        }
    }

    /**
     * Задаёт видимость иконки домика
     */
    fun setHomeIconVisible(isVisible: Boolean) {
        viewBinding.breadcrumbsHomeIcon.isVisible = isVisible
    }

    /**
     * Позволяет расширить область нажатий на элементы на всю высоту родительского [View]
     */
    internal fun extendClickAreaToParentHeight() {
        isClickAreaHeightExtended = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        fullAvailableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = calculateAvailableWidth()
        if (availableWidth != availableForItemsWidth) {
            availableForItemsWidth = availableWidth
            configureViews()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as View?)?.setOnTouchListener { _, event ->
            if (isClickAreaHeightExtended) {
                processTouchEvent(event, horizontalOffset = left)
            } else {
                false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return processTouchEvent(event)
    }

    private fun processTouchEvent(event: MotionEvent?, @Px horizontalOffset: Int = 0): Boolean {
        event ?: return false
        val x = event.x - horizontalOffset
        if (x < 0 || x > width) return false
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            if (event.action == MotionEvent.ACTION_UP) {
                // нажатие на элемент определяется только по x-координате, в том числе в пределах родительского view,
                // если обрабатываются и его события
                getTouchedView(x)?.performClick()
            }
            return true
        }
        return false
    }

    private fun getTouchedView(x: Float): View? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if ((x >= child.left || i == 0) && (x <= child.right || i == childCount - 1)) {
                return child
            }
        }
        return null
    }

    private fun configureViews() {
        removeViews(FIRST_TITLE_INDEX, childCount - FIRST_TITLE_INDEX)

        prepareBreadCrumbs(
            items,
            titlePaint,
            arrowWidth,
            availableForItemsWidth,
            titleHighlightColor
        ).forEachIndexed { i, it ->
            addView(createTitleView(items[i], it.title, it.id), it.width, WRAP_CONTENT)
            if (it.hasArrow) addView(createArrowView())
        }

        baselineAlignedChildIndex = if (childCount > 1) FIRST_TITLE_INDEX else HOME_ICON_INDEX
    }

    @Px
    private fun calculateAvailableWidth(): Int {
        return (fullAvailableWidth - getHomeIconWidth() - paddingStart - paddingEnd).coerceAtLeast(0)
    }

    private fun createTitleView(item: BreadCrumb, title: CharSequence = "", id: String = ""): SbisTextView {
        return SbisTextView(context, null, R.attr.BreadCrumbsView_textStyle).apply {
            text = title
            if (id != ELLIPSIS_ID) {
                setOnClickListener { itemClickListener?.invoke(item) }
            }
        }
    }

    private fun createArrowView() = SbisTextView(context, null, R.attr.BreadCrumbsView_arrowIconStyle)
        .apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                .apply { gravity = Gravity.CENTER_VERTICAL }
        }

    @Px
    private fun getArrowWidth() = with(createArrowView()) {
        getExpectedTextWidth(text, paint) + paddingStart + paddingEnd
    }

    @Px
    private fun getHomeIconWidth() = with(viewBinding.breadcrumbsHomeIcon) {
        if (isVisible) getExpectedTextWidth(text, paint) + paddingEnd else 0
    }
}
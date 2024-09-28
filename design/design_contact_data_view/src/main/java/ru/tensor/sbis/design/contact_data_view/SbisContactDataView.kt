package ru.tensor.sbis.design.contact_data_view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.view.children
import androidx.core.view.isVisible
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.contact_data_view.api.SbisContactDataApi
import ru.tensor.sbis.design.contact_data_view.api.SbisContactDataController
import ru.tensor.sbis.design.contact_data_view.factory.ViewFactory
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Вью для отображения контактных данных(номер телефона)
 *
 * @author av.efimov1
 */
class SbisContactDataView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisContactDataController
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes),
    SbisContactDataApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.SbisContactDataDefaultTheme,
        @StyleRes defStyleRes: Int = R.style.SbisContactDataDefaultTheme,
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisContactDataController())

    internal val icon: TextLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        }
        text = SbisMobileIcon.Icon.smi_answerOnAudio.character.toString()
    }

    internal val moreText: TextLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = resources.getDimensionPixelSize(R.dimen.design_contact_data_more_text_size).toFloat()
        }
        isVisible = false
    }.apply {
        setOnClickListener(OnClickListener { controller.clickMore() })
    }

    /** Отступ между элементами списка **/
    @Px
    private val innerSpacing =
        context.resources.getDimensionPixelSize(R.dimen.design_contact_data_inner_spacing_between_items)

    /** Отступ от последнего элемента списка до кнопки "еще" **/
    @Px
    private val moreMargin =
        context.resources.getDimensionPixelSize(R.dimen.design_contact_data_inner_spacing_between_more)

    init {
        setWillNotDraw(false)
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
        controller.contactDataViewFactory = ViewFactory()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val visibleChildren = children.toList().filter { it.isVisible }
        if (visibleChildren.isNotEmpty()) {
            var sumInnerSpacing = 0
            visibleChildren.forEachIndexed { index, view ->
                view.measure(widthMeasureSpec, heightMeasureSpec)
                if (index != visibleChildren.size - 1) {
                    sumInnerSpacing += innerSpacing
                }
            }
            val sumHeightChildren = visibleChildren.sumOf { it.measuredHeight }
            val maxWidthChild = visibleChildren.maxOfOrNull { it.measuredWidth } ?: 0

            val allWidth = icon.width + maxWidthChild + paddingStart + paddingEnd
            val allHeight =
                sumHeightChildren + moreText.height + sumInnerSpacing + moreMargin + paddingBottom + paddingTop
            setMeasuredDimension(allWidth, allHeight)
        } else {
            setMeasuredDimension(0, 0)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val visibleChildren = children.toList().filter { it.isVisible }
        if (visibleChildren.isNotEmpty()) {
            val childLeft = icon.width + paddingStart
            var childTop = paddingTop
            visibleChildren.forEachIndexed { index, view ->
                if (index == 0) {
                    icon.layout(paddingStart, paddingTop + (view.measuredHeight - icon.height) / 2)
                }
                view.layout(childLeft, childTop, view.measuredWidth + childLeft, childTop + view.measuredHeight)
                childTop += view.measuredHeight + if (index != visibleChildren.size - 1) innerSpacing else 0
            }
            moreText.layout(childLeft, childTop + moreMargin)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        icon.draw(canvas)
        moreText.draw(canvas)
    }
}
package ru.tensor.sbis.design.profile.personcollagelist

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.personcollagelist.controller.PersonCollageLineViewApi
import ru.tensor.sbis.design.profile.personcollagelist.controller.PersonCollageLineViewController
import ru.tensor.sbis.design.profile.personcollagelist.controller.PersonCollageLineViewControllerImpl
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.R as RDesign

/**
 * View компонента "Коллаж в строку".
 * Предназначен для отображения коллажа из нескольких фото в строку, а также счётчика скрытых фото при их наличии.
 * API компонента представлен в [PersonCollageLineViewApi].
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=изображения_3&g=1)
 *
 * @author us.bessonov
 */
class PersonCollageLineView private constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int = R.style.DesignProfilePersonCollageLineViewDefaultStyle,
    private val controller: PersonCollageLineViewController
) : View(context, attrs, defStyleAttr, defStyleRes), PersonCollageLineViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        defStyleRes: Int = R.style.DesignProfilePersonCollageLineViewDefaultStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, PersonCollageLineViewControllerImpl())

    init {
        setWillNotDraw(false)
        controller.setCollageView(this)
        getContext().withStyledAttributes(attrs, R.styleable.PersonCollageLineView, defStyleAttr, defStyleRes) {
            val sizeOrdinal = getInt(
                R.styleable.PersonCollageLineView_PersonCollageLineView_size,
                PhotoSize.UNSPECIFIED.ordinal
            )
            val maxVisibleCount = getInt(
                R.styleable.PersonCollageLineView_PersonCollageLineView_maxVisibleCount,
                0
            )
            val shapeOrdinal =
                getInt(R.styleable.PersonCollageLineView_PersonCollageLineView_shape, Shape.SUPER_ELLIPSE.ordinal)

            setSize(PhotoSize.values()[sizeOrdinal])
            if (maxVisibleCount > 0) {
                setMaxVisibleCount(maxVisibleCount)
            }
            controller.setShape(Shape.values()[shapeOrdinal])
            controller.setInitialsColor(
                getColor(
                    R.styleable.PersonCollageLineView_PersonCollageLineView_initialsColor,
                    ContextCompat.getColor(context, RDesign.color.palette_color_white1)
                )
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        controller.performMeasure(widthMeasureSpec, heightMeasureSpec, minimumWidth).apply {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        controller.performLayout()
    }

    override fun onDraw(canvas: Canvas) {
        controller.performDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        controller.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun invalidate() {
        controller.performInvalidate()
        super.invalidate()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        controller.onVisibilityAggregated(isVisible)
    }
}

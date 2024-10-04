package ru.tensor.sbis.design.profile.personcollage

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.profile.personcollage.controller.CollageGridViewController
import ru.tensor.sbis.design.profile.personcollage.controller.CollageGridViewControllerImpl

/**
 * View для отображения коллажа до четырёх фото в виде плитки.
 *
 * @author us.bessonov
 */
@Suppress("unused")
internal class CollageGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val controller: CollageGridViewController = CollageGridViewControllerImpl(
        // По стандарту фон не темизируется
        backgroundColor = ContextCompat.getColor(context, R.color.palette_color_white1)
    )
) : View(context, attrs, defStyleAttr), CollageGridViewController by controller {

    init {
        init(this@CollageGridView)
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        onMeasured(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        performLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        performDraw(canvas)
    }

    override fun invalidate() {
        super.invalidate()
        performInvalidate()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        controller.onVisibilityAggregated(isVisible)
    }
}
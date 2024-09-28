package ru.tensor.sbis.design.gallery.impl.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import ru.tensor.sbis.design.gallery.R

internal class GalleryItemContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var childView: View

    init {
        setBackgroundResource(R.drawable.design_gallery_camera_preview_bg)
        clipToOutline = true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        childView = findViewById(R.id.gallery_item_container)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredWidth
        setMeasuredDimension(width, height)
        measureChild(
            childView,
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

}
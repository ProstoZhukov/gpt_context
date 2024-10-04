package ru.tensor.sbis.design.gallery.impl.ui

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.camera.view.PreviewView
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.theme.global_variables.Offset

internal class GalleryCameraPreviewContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var previewView: PreviewView
    var isSmall = false
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    init {
        setBackgroundResource(R.drawable.design_gallery_camera_preview_bg)
        clipToOutline = true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        previewView = findViewById(R.id.gallery_preview_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val orientation = context.resources.configuration.orientation
        val isTablet = DeviceConfigurationUtils.isTablet(context)
        val height =
            if (isSmall) {
                measuredWidth
            } else if (orientation == ORIENTATION_LANDSCAPE && !isTablet) {
                measuredWidth / 2 - Offset.X3S.getDimenPx(context)
            } else {
                measuredWidth * 2 + Offset.X3S.getDimenPx(context) * 2
            }
        setMeasuredDimension(measuredWidth, height)
        measureChild(
            previewView,
            MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

}

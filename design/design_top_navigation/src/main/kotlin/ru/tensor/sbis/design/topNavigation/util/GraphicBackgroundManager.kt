package ru.tensor.sbis.design.topNavigation.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.facebook.drawee.drawable.RoundedBitmapDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.zen.getZenTheme
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.asMutableBitmap
import ru.tensor.sbis.design.utils.image_loading.DrawableImageView

/**
 * Хелпер для установки графического фона.
 *
 * @author da.zolotarev
 */
internal class GraphicBackgroundManager : DrawableImageView {

    private lateinit var sbisTopNavigationView: SbisTopNavigationView

    private var backgroundBitmap: Bitmap? = null

    private var zenThemeModel: ZenThemeModel? = null

    /** @SelfDocumented */
    internal var isRoundBottomCorners = true

    /**
     * Привязать [SbisTopNavigationView] к [GraphicBackgroundManager].
     */
    fun attach(view: SbisTopNavigationView) {
        sbisTopNavigationView = view
    }

    /**
     * Вызвать повторную установку дзен темы, нужно при смене контента, чтобы внутренние элементы приняли дзен тему.
     */
    fun reinitZenTheme() {
        if (sbisTopNavigationView.graphicBackground == null || zenThemeModel == null) return
        zenThemeModel?.let { sbisTopNavigationView.controller.setZenTheme(it) }
    }

    override fun getPlaceholderBitmap(width: Int, height: Int, withTint: Boolean) = null

    override fun setBitmap(bitmap: Bitmap?): Boolean {
        return if (bitmap != null) {
            backgroundBitmap = bitmap
            sbisTopNavigationView.apply {
                graphicBackground = cropBitmapAndConvertToDrawable(bitmap)
                setChildBgColor(Color.TRANSPARENT)
                sbisTopNavigationView.controller.publishScope.launch(Dispatchers.Main) {
                    zenThemeModel = getZenTheme(bitmap)
                    zenThemeModel?.let { sbisTopNavigationView.controller.setZenTheme(it) }
                }
                safeRequestLayout()
            }
            true
        } else {
            sbisTopNavigationView.graphicBackground = null
            sbisTopNavigationView.apply {
                val defaultBgColor = controller.getDefaultBackgroundColor()
                setBackgroundColor(defaultBgColor)
                setChildBgColor(defaultBgColor)
                safeRequestLayout()
            }
            false
        }
    }

    override fun setPreparedBitmap(bitmap: Bitmap) {
        backgroundBitmap = bitmap
        sbisTopNavigationView.apply {
            graphicBackground = cropBitmapAndConvertToDrawable(bitmap)
            safeRequestLayout()
        }
    }

    override fun isBitmapRecycled() = backgroundBitmap?.isRecycled ?: false

    override fun hasValidBitmap() = backgroundBitmap?.isRecycled == false

    /**
     * Вернуть закругленный [Drawable] с обрезанным [bitmap]
     */
    private fun cropBitmapAndConvertToDrawable(bitmap: Bitmap): Drawable {
        return bitmap.asMutableBitmap(
            sbisTopNavigationView.measuredWidth,
            sbisTopNavigationView.measuredHeight,
            false
        ).run {
            RoundedBitmapDrawable(sbisTopNavigationView.resources, this).apply {
                if (isRoundBottomCorners) radii = createImageBottomCorners()
            }
        }
    }

    private fun createImageBottomCorners() = FloatArray(8).apply {
        fill(0f, 0, size)
        for (i in LEFT_CORNER_X_RADIUS..RIGHT_CORNER_Y_RADIUS) {
            set(i, sbisTopNavigationView.styleHolder.graphicBackgroundCornerRadius)
        }
    }

    private companion object {
        const val LEFT_CORNER_X_RADIUS = 4
        const val RIGHT_CORNER_Y_RADIUS = 7
    }
}

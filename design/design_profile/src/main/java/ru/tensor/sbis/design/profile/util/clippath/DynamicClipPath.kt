package ru.tensor.sbis.design.profile.util.clippath

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import androidx.annotation.Px
import kotlin.math.min

/**
 * Реализация [ViewClipPath] с возможностью изменения используемого [Path] для обрезки.
 *
 * @author us.bessonov
 */
class DynamicClipPath : ViewClipPath() {

    private val bounds = RectF()

    override var shapePath = Path()

    /**
     * Задаёт контур для обрезки [View].
     */
    fun setPath(path: Path) {
        shapePath = path
        path.computeBounds(bounds, true)
    }

    override fun getTransform(@Px width: Int, @Px height: Int) = Matrix().apply {
        val pathWidth = bounds.width()
        val pathHeight = bounds.height()
        postTranslate((width - pathWidth) / 2, (height - pathHeight) / 2)

        val widthRatio = width / pathWidth
        val heightRatio = height / pathHeight
        val ratio = min(widthRatio, heightRatio)
        postScale(ratio, ratio, width / 2f, height / 2f)
    }
}
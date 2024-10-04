package ru.tensor.sbis.design.utils.shadow_clipper

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.utils.R
import ru.tensor.sbis.design.utils.shadow_clipper.utils.HORIZONTAL_SHADOW_CONTAINER_PADDING
import ru.tensor.sbis.design.utils.shadow_clipper.utils.VERTICAL_SHADOW_CONTAINER_PADDING

/**
 * Класс-контроллер для компонентов [ViewShadow].
 * Реализует поведение [ViewGroup] для размещения в себе "сурогатных"-view из компонента [ViewShadow].
 *
 * @property viewGroup - оригинальный родитель компонентов, для которых нужно совершить обрезку тени.
 * @constructor        - сразу после инициализации добавляет себя в overlay оригинального родителя.
 *
 * @author ra.geraskin
 */
@SuppressLint("ViewConstructor")
internal class ParentShadowController(private val viewGroup: ViewGroup) :
    ViewGroup(viewGroup.context),
    View.OnLayoutChangeListener {

    private val shadows = mutableListOf<ViewShadow>()

    init {
        if (viewGroup.isLaidOut) layout(
            -HORIZONTAL_SHADOW_CONTAINER_PADDING,
            -VERTICAL_SHADOW_CONTAINER_PADDING,
            viewGroup.width + HORIZONTAL_SHADOW_CONTAINER_PADDING,
            viewGroup.height + VERTICAL_SHADOW_CONTAINER_PADDING
        )
        viewGroup.addOnLayoutChangeListener(this)
        viewGroup.overlay.add(this)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        shadows.sortedBy { it.z }.forEach {
            val path = Path()
            if (it.prepareForClip(path, RectF())) clipOutPath(canvas, path)
        }
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onLayoutChange(v: View, l: Int, t: Int, r: Int, b: Int, ol: Int, ot: Int, or: Int, ob: Int) {
        val newWidth = r - l
        val newHeight = b - t
        if (width != newWidth || height != newHeight) {
            layout(
                -HORIZONTAL_SHADOW_CONTAINER_PADDING,
                -VERTICAL_SHADOW_CONTAINER_PADDING,
                newWidth + HORIZONTAL_SHADOW_CONTAINER_PADDING,
                newHeight + VERTICAL_SHADOW_CONTAINER_PADDING
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // ignore
    }

    /** @SelfDocumented */
    fun addShadow(view: View) {
        val shadow = ViewShadow(view)
        shadow.attach(this)
        shadows.add(shadow)
    }

    /** @SelfDocumented */
    fun removeShadow(view: View) {
        val shadow = view.getTag(R.id.target_overlay_shadow) as? ViewShadow ?: return
        shadow.detach(this)
        shadows.remove(shadow)
        if (shadows.isEmpty()) detachFromOriginal()
    }

    private fun detachFromOriginal() {
        viewGroup.removeOnLayoutChangeListener(this)
        viewGroup.overlay.remove(this)
        viewGroup.setTag(R.id.shadow_controller, null)
    }

    /**
     * Обрезка тени по [Path] из оригинальной view.
     */
    @Suppress("DEPRECATION")
    private fun clipOutPath(canvas: Canvas, path: Path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) canvas.clipOutPath(path)
        else canvas.clipPath(path, Region.Op.DIFFERENCE)
    }
}
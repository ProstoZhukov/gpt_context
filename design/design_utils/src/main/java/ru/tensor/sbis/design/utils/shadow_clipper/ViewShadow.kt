package ru.tensor.sbis.design.utils.shadow_clipper

import android.graphics.Outline
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver.OnDrawListener
import androidx.core.view.isVisible
import ru.tensor.sbis.design.utils.R
import ru.tensor.sbis.design.utils.shadow_clipper.utils.EmptyDrawable
import ru.tensor.sbis.design.utils.shadow_clipper.providers.ProviderWrapper
import ru.tensor.sbis.design.utils.shadow_clipper.providers.SurrogateViewProviderWrapper
import ru.tensor.sbis.design.utils.shadow_clipper.utils.OutlineReflector
import ru.tensor.sbis.design.utils.shadow_clipper.utils.HORIZONTAL_SHADOW_CONTAINER_PADDING
import ru.tensor.sbis.design.utils.shadow_clipper.utils.VERTICAL_SHADOW_CONTAINER_PADDING

/**
 * Класс управляющий поведением и отображением тени для оригинального view.
 *
 * @property targetView - экземпляр оригинальной view, для которой будет создана новая обрезанная тень.
 * @constructor         - под капотом создаёт новое view-"сурогат", которое будет отбрасывать обрезанную тень, вместо
 *                        оригинального view.
 *
 * @author ra.geraskin
 */
internal class ViewShadow(private val targetView: View) {

    private val originalRect = Rect()

    private var originalRadius: Float = 0.0F

    private var originalProvider: ViewOutlineProvider = targetView.outlineProvider

    /** View-"сурогат", который отбрасывает тень вместо оригинальной view. */
    private val shadowView = View(targetView.context).apply {
        background = EmptyDrawable
        outlineProvider = SurrogateViewProviderWrapper(originalProvider, targetView)
        clipToOutline = true
        elevation = targetView.elevation
        translationZ = targetView.translationZ
    }

    private val onDrawListener = OnDrawListener { updateShadowView() }

    init {
        targetView.viewTreeObserver.addOnDrawListener(onDrawListener)
    }

    /** @SelfDocumented */
    val z get() = targetView.z

    /** @SelfDocumented */
    fun attach(controller: ParentShadowController) {
        targetView.setTag(R.id.target_overlay_shadow, this)
        targetView.outlineProvider = ProviderWrapper(originalProvider, this::setOutline)
        controller.addView(shadowView)
    }

    /** @SelfDocumented */
    fun detach(controller: ParentShadowController) {
        targetView.setTag(R.id.target_overlay_shadow, null)
        targetView.outlineProvider = originalProvider
        targetView.viewTreeObserver.removeOnDrawListener(onDrawListener)
        controller.removeView(shadowView)
    }

    @Suppress("LiftReturnOrAssignment")
    private fun setOutline(outline: Outline) {
        if (getRect(outline, originalRect)) {
            originalRadius = getRadius(outline)
        } else {
            originalRect.setEmpty()
            originalRadius = 0.0f
        }
        originalRect.offset(HORIZONTAL_SHADOW_CONTAINER_PADDING, VERTICAL_SHADOW_CONTAINER_PADDING)
    }

    /**
     * Вычисление [Path] по которому будет производиться обрезка тени.
     */
    fun prepareForClip(path: Path, boundsF: RectF): Boolean {
        if (originalRect.isEmpty) return false
        boundsF.set(originalRect)
        boundsF.offset(targetView.left.toFloat(), targetView.top.toFloat())
        path.rewind()
        path.addRoundRect(boundsF, originalRadius, originalRadius, Path.Direction.CW)
        return true
    }

    /**
     * Обновление представления тени.
     */
    private fun updateShadowView() {
        if (needUpdateUiParams()) updateUiParams()
        if (targetView.outlineProvider !is ProviderWrapper) wrapTargetViewOutlineProvider()
    }

    /**
     * Оборачивание [ViewOutlineProvider] оригинальной view в провайдер-обёртку.
     */
    private fun wrapTargetViewOutlineProvider() {
        originalProvider = targetView.outlineProvider
        targetView.outlineProvider = ProviderWrapper(originalProvider, this::setOutline)
        shadowView.outlineProvider = SurrogateViewProviderWrapper(originalProvider, targetView)
    }

    /**
     * Обновление UI параметров view-"сурогата", чтобы они соответствовали параметрам оригинального view.
     */
    private fun updateUiParams() = with(shadowView) {
        alpha = targetView.alpha
        pivotX = targetView.pivotX
        pivotY = targetView.pivotY
        scaleX = targetView.scaleX
        scaleY = targetView.scaleY
        rotation = targetView.rotation
        isVisible = targetView.isVisible
        elevation = targetView.elevation
        rotationY = targetView.rotationY
        rotationX = targetView.rotationX
        translationX = targetView.translationX
        translationY = targetView.translationY
        translationZ = targetView.translationZ
        cameraDistance = targetView.cameraDistance
        layout(
            targetView.left + HORIZONTAL_SHADOW_CONTAINER_PADDING,
            targetView.top + VERTICAL_SHADOW_CONTAINER_PADDING,
            targetView.right + HORIZONTAL_SHADOW_CONTAINER_PADDING,
            targetView.bottom + VERTICAL_SHADOW_CONTAINER_PADDING
        )
    }

    /**
     * Сравнение параметров оригинального view и view - "сурогата".
     */
    private fun needUpdateUiParams() = with(shadowView) {
        return@with alpha != targetView.alpha ||
            pivotX != targetView.pivotX ||
            pivotY != targetView.pivotY ||
            scaleX != targetView.scaleX ||
            scaleY != targetView.scaleY ||
            rotation != targetView.rotation ||
            isVisible != targetView.isVisible ||
            elevation != targetView.elevation ||
            rotationY != targetView.rotationY ||
            rotationX != targetView.rotationX ||
            translationX != targetView.translationX ||
            translationY != targetView.translationY ||
            translationZ != targetView.translationZ ||
            cameraDistance != targetView.cameraDistance
    }

    private fun getRadius(outline: Outline) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> outline.radius
        OutlineReflector.isValid -> OutlineReflector.getRadius(outline)
        else -> 0f
    }

    private fun getRect(outline: Outline, rect: Rect) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> outline.getRect(rect)
        OutlineReflector.isValid -> OutlineReflector.getRect(outline, rect)
        else -> false
    }

}
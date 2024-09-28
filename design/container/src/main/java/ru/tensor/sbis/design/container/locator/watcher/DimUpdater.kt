package ru.tensor.sbis.design.container.locator.watcher

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.R
import ru.tensor.sbis.design.container.locator.getRectDescendantParent
import ru.tensor.sbis.design.container.view.OverlayWithRectView
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Объект обновляющий затемнение с вырезом при обновлении якоря
 * @author ma.kolpakov
 */
internal class DimUpdater(
    internal var dimType: DimType = DimType.NONE,
    internal var cutoutBounds: Rect? = null,
    internal var isAnimated: Boolean = true
) {
    internal var outerCutCornersRadius: Float = 0f
        set(value) {
            field = value
            selectedMessageOverlay?.outerCutCornersRadius = value
        }
    private var selectedMessageOverlay: OverlayWithRectView? = null

    fun onAnchorUpdate(parent: ViewGroup, anchor: View?) {
        var areas = emptyList<Area>()
        val rootView = parent.rootView as ViewGroup
        if (dimType == DimType.CUTOUT) {
            anchor?.let {
                // TODO https://online.sbis.ru/opendoc.html?guid=af5c8f02-0e0a-49a1-8339-b46022111d42&client=3
                areas = if (anchor is AnchorWithManyAreas) {
                    anchor.getAreas().map { area ->
                        val anchorRect = Rect(area.rect)
                        rootView.offsetDescendantRectToMyCoords(anchor, anchorRect)
                        Area(anchorRect, area.cornerRadius)
                    }
                } else {
                    val anchorRect = Rect()
                    it.getDrawingRect(anchorRect)
                    rootView.offsetDescendantRectToMyCoords(anchor, anchorRect)
                    listOf(Area(anchorRect, it.resources.getDimension(R.dimen.container_cutout_radius)))
                }
            }
        }
        if (dimType != DimType.NONE && dimType != DimType.SHADOW) {
            showOverlay(areas, cutoutBounds ?: parent.getRectDescendantParent(rootView), rootView)
        }
    }

    fun close() {
        clearOverlay()
    }

    private fun showOverlay(theAreas: List<Area>, bounds: Rect, rootView: ViewGroup) {
        selectedMessageOverlay = rootView.findViewById(R.id.container_dim_overlay_view)
        if (selectedMessageOverlay == null) {
            selectedMessageOverlay = OverlayWithRectView(rootView.context).apply {
                id = R.id.container_dim_overlay_view
                areas = theAreas
                boundsRect = bounds
                dimColor = rootView.context.getThemeColorInt(ru.tensor.sbis.design.R.attr.dimBackgroundColor)
                this@apply.outerCutCornersRadius = this@DimUpdater.outerCutCornersRadius
            }.also {
                showWithAnimation(it, rootView)
            }
        } else {
            selectedMessageOverlay?.apply {
                areas = theAreas
                boundsRect = bounds
                this@apply.outerCutCornersRadius = this@DimUpdater.outerCutCornersRadius
            }
        }
    }

    private fun showWithAnimation(view: View, parent: ViewGroup) {
        parent.addView(view)
        if (isAnimated) {
            val animation = AnimationUtils.loadAnimation(view.context, R.anim.container_fade_in)
            view.startAnimation(animation)
        }
    }

    private fun hideWithAnimation(view: View, parent: ViewGroup) {
        if (isAnimated) {
            val animation = AnimationUtils.loadAnimation(view.context, R.anim.container_fade_out)
            view.startAnimation(animation)
        }
        parent.removeView(view)
    }

    private fun clearOverlay() {
        (selectedMessageOverlay?.rootView as ViewGroup?)?.let {
            selectedMessageOverlay?.let { view ->
                hideWithAnimation(view, it)
            }
        }
        selectedMessageOverlay = null
    }
}
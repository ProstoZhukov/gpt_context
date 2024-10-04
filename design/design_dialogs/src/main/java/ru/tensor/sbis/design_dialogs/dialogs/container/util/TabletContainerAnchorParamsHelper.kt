package ru.tensor.sbis.design_dialogs.dialogs.container.util

import android.graphics.Rect
import android.view.Gravity
import android.widget.FrameLayout
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.AnchorGravity
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.AnchorType

/**
 * Конфигурирует [FrameLayout.LayoutParams] контейнера содержимого, применяя заданный тип закрепления и положение
 * относительно якоря
 */
class TabletContainerAnchorParamsHelper(
    private val decorOffsetTop: Int,
    private val availableHeight: Int,
    private val availableWidth: Int,
    private val containerViewWidth: Int,
    private val anchorRect: Rect,
    private val initialAnchorRect: Rect?,
    private val contentRect: Rect,
    private val horizontalMargin: Int
) {

    /**
     * Применяет заданный тип закрепления для [FrameLayout.LayoutParams] контейнера
     * @param anchorType тип закрепления контейнера
     * @param params настраиваемые [FrameLayout.LayoutParams]
     */
    fun applyAnchorType(
        anchorType: AnchorType,
        params: FrameLayout.LayoutParams
    ) {
        when (anchorType) {
            AnchorType.TOP -> applyAnchorTop(params)
            AnchorType.TOP_WITH_OVERLAY -> applyAnchorTopWithOverlay(params)
            AnchorType.BOTTOM -> applyAnchorBottom(params)
            AnchorType.AUTO -> applyAnchorAuto(params)
            AnchorType.AUTO_WITH_OVERLAY -> applyAnchorAutoWithOverlay(params)
            AnchorType.AUTO_WITH_OVERLAY_IF_NOT_ENOUGH_SPACE -> applyAnchorAutoWithOverlayIfNotEnoughSpace(params)
            AnchorType.RIGHT -> applyAnchorRight(params)
        }
    }

    /**
     * Применяет заданное положение относительно якоря для [FrameLayout.LayoutParams] контейнера
     * @param anchorGravity положение контейнера относительно якоря
     * @param params настраиваемые [FrameLayout.LayoutParams]
     */
    fun applyAnchorGravity(
        anchorGravity: AnchorGravity,
        params: FrameLayout.LayoutParams
    ) {
        if (contentRect.isEmpty) return

        when (anchorGravity) {
            AnchorGravity.START -> applyGravityStart(params)
            AnchorGravity.END -> applyGravityEnd(params)
            AnchorGravity.CENTER -> applyGravityCenter(params)
            AnchorGravity.UNSPECIFIED -> {
                // изменение параметров не требуется
            }
        }
    }

    // region anchor type
    private fun applyAnchorTop(params: FrameLayout.LayoutParams) {
        params.placeBelowAnchor(
            anchorRect,
            decorOffsetTop
        )
    }

    private fun applyAnchorRight(params: FrameLayout.LayoutParams) {
        params.placeLeftOfAnchor(anchorRect)
    }

    private fun applyAnchorTopWithOverlay(params: FrameLayout.LayoutParams) {
        params.placeOverAnchorTop(
            anchorRect,
            decorOffsetTop
        )
    }

    private fun applyAnchorBottom(params: FrameLayout.LayoutParams) {
        params.placeAboveAnchor(
            anchorRect,
            availableHeight
        )
    }

    private fun applyAnchorAuto(params: FrameLayout.LayoutParams) {
        if (anchorRect.centerY() > availableHeight / 2) {
            params.placeAboveAnchor(
                anchorRect,
                availableHeight
            )
        } else {
            params.placeBelowAnchor(
                anchorRect,
                decorOffsetTop
            )
        }
    }

    private fun applyAnchorAutoWithOverlay(params: FrameLayout.LayoutParams) {
        val centerY = (initialAnchorRect ?: anchorRect).centerY()
        if (centerY > availableHeight / 2) {
            params.placeOverAnchorBottom(
                anchorRect,
                availableHeight
            )
        } else {
            params.placeOverAnchorTop(
                anchorRect,
                decorOffsetTop
            )
        }
    }

    private fun applyAnchorAutoWithOverlayIfNotEnoughSpace(params: FrameLayout.LayoutParams) {
        val gravity = if (anchorRect.centerY() > availableHeight / 2) Gravity.TOP else Gravity.BOTTOM
        when {
            contentRect.isEmpty -> params.gravity = gravity
            gravity == Gravity.TOP && contentRect.bottom <= anchorRect.top -> params.placeAboveAnchor(
                anchorRect,
                availableHeight
            )
            gravity == Gravity.BOTTOM && contentRect.top >= anchorRect.bottom -> params.placeBelowAnchor(
                anchorRect,
                decorOffsetTop
            )
            else -> params.gravity = gravity
        }
    }
    // endregion

    // region anchor gravity
    private fun applyGravityStart(params: FrameLayout.LayoutParams) {
        params.setLeftMargin(
            anchorRect.left,
            contentRect.width(),
            availableWidth
        )
    }

    private fun applyGravityEnd(params: FrameLayout.LayoutParams) {
        params.setRightMargin(
            availableWidth - anchorRect.right,
            contentRect.width(),
            availableWidth
        )
    }

    private fun applyGravityCenter(params: FrameLayout.LayoutParams) {
        val anchorCenter = (anchorRect.left + anchorRect.right) / 2
        if (anchorCenter > availableWidth / 2) {
            params.setRightMargin(
                availableWidth - (anchorCenter + containerViewWidth / 2),
                contentRect.width(),
                availableWidth
            )
        } else {
            params.setLeftMargin(
                anchorCenter - containerViewWidth / 2,
                contentRect.width(),
                availableWidth
            )
        }
    }
    // endregion

    // region extensions
    private fun FrameLayout.LayoutParams.placeBelowAnchor(anchorRect: Rect, decorOffsetTop: Int) {
        gravity = Gravity.TOP
        topMargin = anchorRect.bottom - decorOffsetTop
    }

    private fun FrameLayout.LayoutParams.placeAboveAnchor(anchorRect: Rect, availableHeight: Int) {
        gravity = Gravity.BOTTOM
        bottomMargin = availableHeight - anchorRect.top
    }

    private fun FrameLayout.LayoutParams.placeOverAnchorTop(anchorRect: Rect, decorOffsetTop: Int) {
        gravity = Gravity.TOP
        topMargin = anchorRect.top - decorOffsetTop
        bottomMargin = horizontalMargin
    }

    private fun FrameLayout.LayoutParams.placeLeftOfAnchor(anchorRect: Rect) {
        gravity = Gravity.TOP
        topMargin = anchorRect.top - (contentRect.height() / 2) - (anchorRect.height() / 2)
        rightMargin = (availableWidth - anchorRect.right) + anchorRect.width()
    }

    private fun FrameLayout.LayoutParams.placeOverAnchorBottom(anchorRect: Rect, availableHeight: Int) {
        gravity = Gravity.BOTTOM
        topMargin = horizontalMargin
        bottomMargin = availableHeight - anchorRect.bottom
    }

    private fun FrameLayout.LayoutParams.setLeftMargin(desiredMargin: Int, contentWidth: Int, availableWidth: Int) {
        gravity = gravity or Gravity.START
        leftMargin = Math.max(leftMargin, Math.min(desiredMargin, availableWidth - contentWidth - rightMargin))
    }

    private fun FrameLayout.LayoutParams.setRightMargin(desiredMargin: Int, contentWidth: Int, availableWidth: Int) {
        gravity = gravity or Gravity.END
        rightMargin = Math.max(rightMargin, Math.min(desiredMargin, availableWidth - contentWidth - leftMargin))
    }
    // endregion
}
package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

import androidx.annotation.IdRes
import android.view.View.NO_ID
import java.io.Serializable

/**
 * Класс якоря, необходимого для закрепления контента выше или ниже заданного View
 * @param type тип закрепления
 * @param viewId идентификатор View, к которой необходимо закрепить контент
 * @param viewTag тэг View, к которой необходимо закрепить контент
 * @param anchorParentTag тэг родительской View якоря. Используется при динамическом добавлении view якоря (например,
 * если якорем является элемент списка)
 * @param gravity определяет положение контента относительно якоря
 */
class Anchor private constructor(
    val type: AnchorType,
    @IdRes val viewId: Int = NO_ID,
    val viewTag: String? = null,
    val anchorParentTag: String? = null,
    val gravity: AnchorGravity = AnchorGravity.UNSPECIFIED
) : Serializable {

    companion object {
        fun createTopAnchor(viewId: Int, gravity: AnchorGravity) =
            Anchor(AnchorType.TOP, viewId, gravity = gravity)

        fun createTopWithOverlayAnchor(viewId: Int, gravity: AnchorGravity) =
            Anchor(AnchorType.TOP_WITH_OVERLAY, viewId, gravity = gravity)

        fun createBottomAnchor(viewId: Int, gravity: AnchorGravity) =
            Anchor(AnchorType.BOTTOM, viewId, gravity = gravity)

        fun createAnchor(anchorType: AnchorType, viewTag: String, parentViewTag: String?, gravity: AnchorGravity) =
            Anchor(anchorType, viewTag = viewTag, anchorParentTag = parentViewTag, gravity = gravity)

        fun createRightAnchor(viewId: Int) =
            Anchor(AnchorType.RIGHT, viewId, gravity = AnchorGravity.START)
    }
}

/**
 * Положение контента относительно якоря
 */
enum class AnchorGravity { START, END, CENTER, UNSPECIFIED }
package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

import androidx.annotation.IdRes
import android.view.View.NO_ID
import java.io.Serializable

/**
 * Класс объектов, способных ограничивать ширину контента диалогового окна на планшете
 * @param type тип ограничивающего объекта
 * @param viewId идентификатор ограничивающего View
 * @param ensureDefaultMinWidth указывает, что, независимо от ширины ограничивающего объекта, ширина окна не должна быть
 * меньше стандартного минимального значения
 */
class BoundingObject private constructor(
    val type: BoundingObjectType,
    @IdRes
    val viewId: Int = NO_ID,
    val ensureDefaultMinWidth: Boolean = true
) : Serializable {

    companion object {
        fun fromParentFragment(ensureDefaultMinWidth: Boolean = true) =
            BoundingObject(BoundingObjectType.PARENT_FRAGMENT, ensureDefaultMinWidth = ensureDefaultMinWidth)

        fun fromTargetFragment(ensureDefaultMinWidth: Boolean = true) =
            BoundingObject(BoundingObjectType.TARGET_FRAGMENT, ensureDefaultMinWidth = ensureDefaultMinWidth)

        fun fromView(id: Int, ensureDefaultMinWidth: Boolean = true) =
            BoundingObject(BoundingObjectType.VIEW, id, ensureDefaultMinWidth = ensureDefaultMinWidth)
    }
}
package ru.tensor.sbis.design.utils.shadow_clipper.utils

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.utils.R
import ru.tensor.sbis.design.utils.shadow_clipper.ParentShadowController

/**
 * Утилитные функции для ParentShadowController.
 *
 * @author ra.geraskin
 */

/** @SelfDocumented */
internal fun attachShadow(view: View) {
    val parentViewGroup = view.parent as? ViewGroup ?: return
    val shadowController = getOrCreateController(parentViewGroup)
    shadowController.addShadow(view)
}

/** @SelfDocumented */
internal fun detachShadow(view: View) {
    val parentViewGroup = view.parent as? ViewGroup ?: return
    val shadowController = getController(parentViewGroup) ?: return
    shadowController.removeShadow(view)
}

/** @SelfDocumented */
internal fun getOrCreateController(viewGroup: ViewGroup) = getController(viewGroup) ?: createController(viewGroup)

/** @SelfDocumented */
internal fun getController(viewGroup: ViewGroup) = viewGroup.getTag(R.id.shadow_controller) as? ParentShadowController

/** @SelfDocumented */
internal fun createController(viewGroup: ViewGroup) = ParentShadowController(viewGroup).also { controller ->
    viewGroup.setTag(R.id.shadow_controller, controller)
}

/**
 * Значения отступов для расширения overlay контейнера. Нужны для предотвращения обрезки тени родителем родителя.
 * Решение служит обходным путём для использования флагов clipToPadding и clipChildren, потому что для ViewGroup,
 * которая находится в overlay, они не работают.
 */
internal const val VERTICAL_SHADOW_CONTAINER_PADDING = 50
internal const val HORIZONTAL_SHADOW_CONTAINER_PADDING = 50

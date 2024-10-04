package ru.tensor.sbis.design.utils

import android.view.View
import java.util.EnumSet

/**
 * Направления движения
 *
 * @author us.bessonov
 */
enum class DragDirection {
    LEFT, RIGHT, TOP, BOTTOM
}

/**
 * [View], поддерживающий жесты движения в определённых направлениях
 *
 * @author us.bessonov
 */
interface DraggableView {
    /**
     * В каких направлениях можно "тянуть" данный [View]
     */
    val supportedDragDirections: EnumSet<DragDirection>
}
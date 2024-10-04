package ru.tensor.sbis.design.skeleton_view.mask

/**
 * Направления отображения анимации
 *
 * @property stableId идентефикатор типа направления анимации (используется в xml атрибутах)
 * @property angle уголн наклона анимации
 *
 * @author us.merzlikina
 */
enum class SkeletonShimmerDirection(private val stableId: Int, val angle: Int) {
    TOP_LEFT_BOTTOM_RIGHT(0, 45),
    BOTTOM_RIGHT_TOP_LEFT(1, 45),
    LEFT_TO_RIGHT(2, 0),
    RIGHT_TO_LEFT(3, 0);

    companion object {
        fun valueOf(stableId: Int): SkeletonShimmerDirection? = values().find { it.stableId == stableId }
    }
}
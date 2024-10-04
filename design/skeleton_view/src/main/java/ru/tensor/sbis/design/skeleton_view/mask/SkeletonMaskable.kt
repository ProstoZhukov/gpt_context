package ru.tensor.sbis.design.skeleton_view.mask

/**
 * Интерфейс для работы с масками Skeleton
 *
 * @author us.merzlikina
 */
internal interface SkeletonMaskable {
    fun invalidate() = Unit
    fun start() = Unit
    fun stop() = Unit
}
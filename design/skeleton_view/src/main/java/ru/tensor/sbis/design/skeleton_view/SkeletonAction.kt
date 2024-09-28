package ru.tensor.sbis.design.skeleton_view

/**
 * Интерфейс действий со Skeleton
 *
 * @author us.merzlikina
 */
interface SkeletonAction {

    /**
     * показать исходный layout и скрыть скелетон
     */
    fun hideSkeleton()

    /**
     * показать скелетон и скрыть исходный layout
     */
    fun showSkeleton()

    /**
     * @return True если исходный layout скрыт скелетоном
     */
    fun isSkeletonActive(): Boolean
}
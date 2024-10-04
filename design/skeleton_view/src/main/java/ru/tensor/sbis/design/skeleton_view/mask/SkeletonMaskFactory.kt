package ru.tensor.sbis.design.skeleton_view.mask

import android.view.View
import ru.tensor.sbis.design.skeleton_view.SkeletonConfig

/*
 * @author us.merzlikina
 */
internal object SkeletonMaskFactory {

    /**
     * Создание маски в соответствии с конфигуратором
     *
     * @param parentView родительское view
     * @param config конфигурация Skeleton
     */
    fun createMask(
        parentView: View,
        config: SkeletonConfig
    ): SkeletonMask = when (config.showShimmer) {
        true -> SkeletonMaskShimmer(
            parentView,
            config.maskColor,
            config.shimmerColor,
            config.shimmerDuration,
            config.shimmerDirection
        )
        false -> SkeletonMaskSolid(parentView, config.maskColor)
    }
}
package ru.tensor.sbis.design.topNavigation.api

import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader

/**
 * Внутренний API компонента шапка для работы [ViewImageLoader].
 *
 * @author da.zolotarev
 */
internal interface SbisTopNavigationInternalApi {
    /**
     * @see [ViewImageLoader.onViewMeasured]
     */
    fun onViewMeasured()

    /**
     * @see [ViewImageLoader.onVisibilityAggregated]
     */
    fun onVisibilityAggregated(isVisible: Boolean)

    /** @SelfDocumented */
    fun getFootersMarginSum(): Int

    /** @SelfDocumented */
    fun getFootersMeasuredHeight(): Int

    /**
     * Анимировать сворачивание или разворачивание шапки.
     */
    fun animateTopNavigationFolding(dY: Int)
}
package ru.tensor.sbis.cashboxes_lite_decl.pricing

import io.reactivex.Single
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Синхронизатор прайсов и скидок
 */
interface PricingSynchronizeFeature : Feature {

    /**
     * Метод сбрасывает флаг первичной синхронизации прайсов и скидок и начинает синхронизацию заново
     * (по умолчанию первичная синхронизация осуществляется только один раз)
     */
    fun syncPricingForce(): Single<Unit>
}
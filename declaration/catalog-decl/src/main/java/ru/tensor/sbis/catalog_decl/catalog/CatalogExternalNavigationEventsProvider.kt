package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 *  Поставщик наблюдателя для подписки на внешние события каталога
 *
 *  @see CatalogFeature
 *
 *  @author sp.lomakin
 */
interface CatalogExternalNavigationEventsProvider : Feature {

    /**
     *  Наблюдатель внешних событий каталога
     */
    fun getObservable(): Observable<NavigationEvent>

}
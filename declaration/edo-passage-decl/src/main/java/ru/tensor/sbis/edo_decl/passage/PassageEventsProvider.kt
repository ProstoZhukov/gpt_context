package ru.tensor.sbis.edo_decl.passage

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик событий перехода
 *
 * @author sa.nikitin
 */
interface PassageEventsProvider : Feature {

    /**
     * [Observable] событий перехода
     * Необходимо указать поток, см. [Observable.observeOn]
     */
    val events: Observable<PassageEvent>
}
package ru.tensor.sbis.video_monitoring_decl.model

import android.os.Parcelable

/**
 * Дополнительный фильтр по коллекции камер.
 */
interface CollectionFilter : Parcelable {
    /** Строковый идентификатор тарифа. */
    val tariff: String?

    /** Фильтровать по активным - активные/неактивные/все. */
    val active: Boolean?

    /** Фильтровать по рабочим  - рабочие/нерабочие/все. */
    val working: Boolean?

    /** Флаг настройки записи по движению. */
    val detection: Boolean?

    companion object {
        /** @SelfDocumented */
        val default: CollectionFilter = DefaultCollectionFilter()
    }
}
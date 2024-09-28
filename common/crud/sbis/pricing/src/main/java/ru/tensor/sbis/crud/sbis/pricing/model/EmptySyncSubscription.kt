package ru.tensor.sbis.crud.sbis.pricing.model

import ru.tensor.sbis.pricing.generated.PriceEntityModel
import ru.tensor.sbis.pricing.generated.PricelistNomenclatureModel
import ru.tensor.sbis.pricing.generated.SyncSubscription
import java.util.ArrayList

/**
 * Реализация класса синхронизации с пустой имплементацией
 */
abstract class EmptySyncSubscription : SyncSubscription() {

    /**
     * Выполнение действия на вставку или замену прайс листа
     */
    override fun OnInsertedOrReplaced(p0: ArrayList<PriceEntityModel>) { }

    /**
     * Выполнение действия на завершение загрузки содержимого прайс листа
     */
    override fun OnPricelistContentComplete(pricelistId: Long) { }

    /**
     * Выполнение действия на обновление прайс листа
     */
    override fun OnUpdated(p0: ArrayList<PriceEntityModel>) {  }

    /**
     * Выполнение действия на удаление прайс листа
     */
    override fun OnRemoved(p0: ArrayList<PriceEntityModel>) { }

    /**
     * Выполнение действия на завершение синхронизации
     */
    override fun OnEndSync() {  }

    /**
     * Выполнение действия на начало синхронизации
     */
    override fun OnBeginSync() { }

    /**
     * Выполнение действия на завершение загрузки содержимого прайс листа
     */
    override fun OnPricelistsContentComplete() {  }

    /**
     * Выполнение действия на завершение загрузки прайс листа
     */
    override fun OnPricelistsComplete() {  }

    /**
     * Выполнение действия на удаление всего
     */
    override fun OnAllRemoved() {  }

    /**
     * Выполнение действия на результат поиска по серийному номеру
     */
    override fun OnSerialNumberSearchResult(
        model: PricelistNomenclatureModel,
        searchString: String,
        searchId: String
    ) {
    }

    override fun OnSerialNumberSearch(
        model: ArrayList<PricelistNomenclatureModel>,
        searchString: String,
        searchId: String
    ) {

    }

    override fun OnDiscountsComplete() { }
}
package ru.tensor.sbis.catalog_decl.catalog

import android.content.Context
import android.view.View

/**
 * Контракт view информация по номенклатуре
 * Данный интерфейс реализует view, объект должен использоваться в соответствующем life cycle
 *
 * @author sp.lomakin
 */
interface NomenclatureBodyViewContract {

    /**
     * Заполнить данными
     */
    fun setData(data: NomenclatureBodyViewData)

    /**
     * Получить view для встраивания
     */
    fun getView(): View

    interface Factory {

        /**
         * Создать экземпляр
         */
        fun createInstance(context: Context): NomenclatureBodyViewContract

    }

}
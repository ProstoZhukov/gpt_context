package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.ListResultOfSettingsMapOfStringString
import ru.tensor.sbis.retail_settings.generated.Settings
import ru.tensor.sbis.retail_settings.generated.SettingsFilter

/**
 * Интерфейс для связи с контроллером.
 */
interface RetailSettingsRepository :
        CRUDRepository<Settings>,
        BaseListRepository<ListResultOfSettingsMapOfStringString, SettingsFilter, DataRefreshedCallback> {

        /**
         * Функция для установки настроек причин возвратов/удалений
         *
         * @param allowCashierCancel - флаг, обозначающий возможность удаления причин возвратов всем пользователям, если true - можно всем, иначе - только администратору
         * @param returnRequireSale - флаг, обозначающий возможность делать возврат только по чеку, если true - только по чеку, иначе - нет
         */
        fun setSettings(allowCashierCancel: Boolean, returnRequireSale: Boolean)
}

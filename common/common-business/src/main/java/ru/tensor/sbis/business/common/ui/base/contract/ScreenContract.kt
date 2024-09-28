package ru.tensor.sbis.business.common.ui.base.contract

/** Контракт для связи и взаимодействия вью-модели с View экрана со списком */
interface ScreenContract :
    ListContract,
    MoneyTopNavigationContract {

    /** Принудительное обновление данных во вью-модели */
    fun refreshForceUpdate() = Unit
}

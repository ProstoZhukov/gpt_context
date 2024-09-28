package ru.tensor.sbis.base_app_components.settings

/**
 * Поставщик модели настроек
 *
 * @author ma.kolpakov
 */
interface SettingItemsProvider {
    /**
     * Метод подготовки всех элементов настрое к первоначальному отображению. Рекомендуется запускать в фоне.
     * Пока он выполняется в настройках отобразится ромашка загрузки, если требуется.
     */
    suspend fun prepare()

    /**
     * Вернуть модель натсроек
     */
    fun provide(): SettingsModel
}
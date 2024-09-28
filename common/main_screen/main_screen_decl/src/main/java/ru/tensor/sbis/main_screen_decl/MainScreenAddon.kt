package ru.tensor.sbis.main_screen_decl

/**
 * Интерфейс плагина для главного экрана.
 *
 * @author kv.martyshenko
 */
interface MainScreenAddon {

    /**
     * Метод для настройки плагина.
     *
     * @param mainScreen компонент главного экрана
     */
    fun setup(mainScreen: ConfigurableMainScreen)

    /**
     * Метод для сброса изначально выполненных настроек.
     *
     * @param mainScreen компонент главного экрана
     */
    fun reset(mainScreen: ConfigurableMainScreen)

}
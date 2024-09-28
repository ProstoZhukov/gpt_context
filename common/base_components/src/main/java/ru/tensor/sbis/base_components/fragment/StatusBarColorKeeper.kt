package ru.tensor.sbis.base_components.fragment

/**
 * Интерфейс для получения цвета иконок для статус бара текущего экрана
 */
interface StatusBarColorKeeper {

    /**
     * Получить режим статус бара. true - иконки статус бара будут темными, иначе светлыми
     */
    fun isStatusBarLightMode(): Boolean
}
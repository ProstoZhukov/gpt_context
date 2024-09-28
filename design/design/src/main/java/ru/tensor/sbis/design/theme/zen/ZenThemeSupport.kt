package ru.tensor.sbis.design.theme.zen

/**
 * Интерфейс, определяющий, что View должна поддерживать Дзен-тему.
 *
 * https://dev.sbis.ru/article/fecfb715-c8d6-4616-a7a8-ce8cc27c0f52
 *
 * @author da.zolotarev
 */
interface ZenThemeSupport {

    /**
     * Установка темной/светлой Дзен-темы, на основе переданного изображения.
     */
    fun setZenTheme(themeModel: ZenThemeModel)
}
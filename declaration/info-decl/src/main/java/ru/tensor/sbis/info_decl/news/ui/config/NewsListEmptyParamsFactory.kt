package ru.tensor.sbis.info_decl.news.ui.config

/**
 * Фабрика по созданию параметров для отображения/переопределения стандартных заглушек реестра новостей
 *
 * @author am.boldinov
 */
interface NewsListEmptyParamsFactory {

    /**
     * Создает параметры для отображения заглушки
     *
     * @param mode режим отображения заглушки
     */
    fun create(mode: NewsListEmptyViewMode): NewsEmptyViewParams?

    /**
     * Получить конфигурацию рендера заглушек
     */
    fun getRenderConfiguration(): NewsEmptyViewRenderConfiguration
}
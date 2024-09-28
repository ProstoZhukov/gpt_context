package ru.tensor.sbis.onboarding.ui.host.adapter

/**
 * Интерфейс холдера списка страниц приветственного экрана
 *
 * @author as.chadov
 */
internal interface PageListHolder {

    /**
     * Получить количество страниц фич приветственного экрана
     *
     * @return размер списка приветственного экрана
     */
    fun getPageCount(): Int

    /**
     * Получить идентификатор страницы по позиции
     *
     * @return идентификатор страницы
     */
    fun getPageId(position: Int): String

    /**
     * Получить параметры экрана конкретной фичи по ее позиции
     *
     * @return параметры страницы
     */
    fun getPageParams(position: Int): PageParams

    interface Provider {
        val holder: PageListHolder
    }
}
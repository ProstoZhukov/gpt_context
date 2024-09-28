package ru.tensor.sbis.design.link_share.utils

/** Интерфейс предоставляет метод для получения URL ссылки*/
internal interface LinkShareURLProvider {
    /** Метод для получения URL ссылки для выбранной вкладки */
    fun getURLForSelectedTab(selectedLinkTab: Int): String
}
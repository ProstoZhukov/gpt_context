package ru.tensor.sbis.webviewer.utils

/**
 * Предназначен для уведомления о событии загрузки заголовка страницы
 *
 * @author us.bessonov
 */
internal interface TitleLoadedListener {

    /** @SelfDocumented */
    fun onTitleLoaded()
}
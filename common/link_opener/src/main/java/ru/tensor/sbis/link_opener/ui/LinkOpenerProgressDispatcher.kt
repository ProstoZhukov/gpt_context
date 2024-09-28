package ru.tensor.sbis.link_opener.ui

import ru.tensor.sbis.link_opener.domain.router.LinkOpenerRouter
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository

/**
 * Интерфейс диспетчера связывающего процесс открытия ссылки с состоянием UI МП (видимостью прогресса открытия ссылки).
 *
 * @author as.chadov
 */
internal interface LinkOpenerProgressDispatcher {
    /**
     * Регистрируем мониторинг появления экранов приложения.
     * Следует вызывать как можно раньше.
     */
    fun register()

    /**
     * Отменяем регистрацию мониторинга появления экранов приложения.
     * Должен быть вызван:
     * 1. до выполнения целевого обработчика ссылки [LinkOpenEventHandler] в [LinkOpenerRouter]!
     * 2. в том случаем если ссылка не будет передана на обработку в [LinkOpenerRouter]!
     */
    fun unregister()

    /**
     * Показать прогресс-диалог открытия ссылки на текущих экранах переднего плана приложения.
     * Следует вызывать до выполнения запроса на разбор ссылки микросервисом
     * декорирования [LinkDecoratorServiceRepository].
     */
    fun showProgress()
}

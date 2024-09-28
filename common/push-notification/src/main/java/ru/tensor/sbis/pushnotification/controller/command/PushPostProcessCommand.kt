package ru.tensor.sbis.pushnotification.controller.command

import ru.tensor.sbis.pushnotification.controller.HandlingResult

/**
 * Интерфейс команды постобработки входящих пуш уведомлений, которая применяется для уведомлений,
 * прошедших буферизацию и обработку (парсинг и кеширование).
 * Вызывается непосредственно перед показом списка уведомлений пользователю.
 *
 * @author ev.grigoreva
 */
interface PushPostProcessCommand {

    /**
     * Обрабатывает набор уведомлений непосредственно перед показом.
     *
     * @param handleResult результат обработки поступивших уведомлений контроллером.
     */
    fun process(handleResult: HandlingResult)
}
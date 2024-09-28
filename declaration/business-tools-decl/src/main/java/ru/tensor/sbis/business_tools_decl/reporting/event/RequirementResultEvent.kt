package ru.tensor.sbis.business_tools_decl.reporting.event

/**
 * Событие выполнения действия с требованием
 *
 * @property message текст сообщения для пользователя
 *
 * @author ev.grigoreva
 */
data class RequirementResultEvent(val message: String)

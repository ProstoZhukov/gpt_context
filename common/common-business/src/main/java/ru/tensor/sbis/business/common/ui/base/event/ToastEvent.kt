package ru.tensor.sbis.business.common.ui.base.event

/**
 * Событие отображения всплывающего сообщения
 *
 * @author as.chadov
 *
 * @property receiverId id получателя результата
 * @property text текст для отображения,
 * @property error пользовательская ошибка/исключение приложения Бизнес
 */
data class ToastEvent(
    val receiverId: String = "",
    val text: String = "",
    val error: Throwable? = null
)
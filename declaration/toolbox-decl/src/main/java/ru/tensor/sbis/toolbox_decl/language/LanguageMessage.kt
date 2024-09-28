package ru.tensor.sbis.toolbox_decl.language

/**
 * Модель сообщения об ошибке "язык не поддерживается"
 * @property title заголовок
 * @property message сообщение
 * @property okText текст на кнопке подтверждения
 *
 * @author av.krymov
 */
data class LanguageMessage(
    var title: String,
    var message: String,
    var okText: String
)
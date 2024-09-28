package ru.tensor.sbis.communicator.common.conversation.data

import ru.tensor.sbis.communicator.generated.ListResultOfBinaryMapOfStringString
import ru.tensor.sbis.communicator.generated.Message

/**
 * Подобный класс использовался раньше для получения данных из контроллера сообщений.
 * Сейчас из контроллера сообщений возвращаются бинарные данные [ListResultOfBinaryMapOfStringString],
 * которые будут сконвертированы в этот класс.
 */
data class ListResultOfMessageMapOfStringString(var result: ArrayList<Message>, var haveMore: Boolean, var metadata: HashMap<String, String>?)
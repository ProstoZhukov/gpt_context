package ru.tensor.sbis.android_ext_decl

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.NotSerializableException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Проверить возможность сериализации объекта.
 *
 * @param key Опциональный ключ для идентификации ошибки в случае исключения.
 * @throws IllegalStateException выбрасывается если сериализация не возможна.
 */
@Throws(IllegalStateException::class)
fun <T : Serializable> T.validate(key: String? = null) {
    try {
        val byteArray = ByteArrayOutputStream().let { byteArrayStream ->
            ObjectOutputStream(byteArrayStream).use {
                it.writeObject(this)
                it.flush()
            }
            byteArrayStream.toByteArray()
        }

        ObjectInputStream(ByteArrayInputStream(byteArray))
            .use(ObjectInputStream::readObject)

    } catch (e: NotSerializableException) {
        val keyText = key?.let { "[$it]" }.orEmpty()
        throw IllegalStateException("Object $keyText is not serializable", e)
    }
}
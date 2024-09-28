package ru.tensor.sbis.verification_decl.permission

/**
 * Исключение, которое можно использовать в потоках данных для индикации отсутствия полномочий
 *
 * @param scope область, по которой было запрошено полномочие
 *
 * @author ma.kolpakov
 * Создан 3/19/2019
 */
class NoPermissionException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)
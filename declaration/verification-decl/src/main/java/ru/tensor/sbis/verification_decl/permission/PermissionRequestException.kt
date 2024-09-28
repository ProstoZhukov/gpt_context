package ru.tensor.sbis.verification_decl.permission

/**
 * Ошибка при запросе уровня доступа
 */
class PermissionRequestException : Exception {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
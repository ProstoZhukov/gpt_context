package ru.tensor.sbis.common.exceptions

/**
 * Исключение, которое может возникнуть во время блокировки сервиса, например, СБИС Диска
 *
 * @author sa.nikitin
 */
class ServiceUnavailableException(cause: Throwable? = null) : Exception(cause)
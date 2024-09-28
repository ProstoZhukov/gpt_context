package ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model

/**
 * Класс предназначен для упаковки результатов
 * выполнения команды контроллера.
 *
 * @author da.pavlov
 * Дубль: ru.tensor.presto.wrapper.common.data
 * */
sealed class BlResult<out T> {

    /**
     * Успешное выполнение операции.
     * @property value данные, полученные от контроллера.
     */
    class Success<T>(val value: T?) : BlResult<T>()

    /**
     * Ошибка при выполнении операции.
     * @property errorCode код ошибки.
     * @property description описание ошибки.
     * @property details детали ошибки.
     */
    class Failure(val errorCode: Int, val description: String?, val details: String? = null) : BlResult<Nothing>()

    /*** Индикатор успешности выполнения операции. */
    val isSuccess: Boolean get() = this is Success

    /*** Индикатор ошибки при выполнении операции. */
    @Suppress("MemberVisibilityCanBePrivate")
    val isFailure: Boolean get() = this is Failure

    /*** Метод для чтения успешного результата. */
    fun getValueAsSuccess() = if (isSuccess) getValueOrNull()
    else throw Exception("$this is not a BlResult.Success type")

    /*** Метод для чтения ошибки. */
    fun asFailure() = if (isFailure) (this as Failure)
    else throw Exception("$this is not a BlResult.Failure type")

    /*** Возвращает опциональное значение результата. */
    fun getValueOrNull(): T? =
        when (this) {
            is Success -> value
            is Failure -> null
        }
}
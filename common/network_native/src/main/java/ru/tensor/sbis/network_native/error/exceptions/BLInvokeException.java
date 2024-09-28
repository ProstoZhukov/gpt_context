package ru.tensor.sbis.network_native.error.exceptions;


import ru.tensor.sbis.network_native.error.ISbisError;

/**
 * Исключение вызова метода бизнес-логики
 */
@SuppressWarnings("unused")
public class BLInvokeException extends CommonSbisException {

    public BLInvokeException(ISbisError error) {
        super(error);
    }

}

package ru.tensor.sbis.network_native.error.exceptions;

/**
 * Исключение использования неизвестного типа поля. Например, при парсинге Record-а.
 */
public class UnknownFieldTypeException extends CommonSbisException {

    public UnknownFieldTypeException(String typeName) {
        super("Неизвестный тип: " + typeName, "Неизвестный тип: " + typeName);
    }
}

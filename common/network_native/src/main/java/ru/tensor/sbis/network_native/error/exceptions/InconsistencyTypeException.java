package ru.tensor.sbis.network_native.error.exceptions;

/**
 * Ошибка несоответствия типов
 */
public class InconsistencyTypeException extends CommonSbisException {

    public InconsistencyTypeException(String left, String right) {
        super("Несоответствие типов \"" + left + "\" и \"" + right + "\"", "Несоответствие типов \"" + left + "\" и \"" + right + "\"");
    }

}

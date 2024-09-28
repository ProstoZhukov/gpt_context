package ru.tensor.sbis.network_native.error.exceptions;

/**
 * Исключение дублирования ключа. Например. в Record-е.
 */
public class DuplicateKeyException extends CommonSbisException {

    /**
     * Конструктор
     *
     * @param key Название ключа
     */
    public DuplicateKeyException(String key) {
        super("Ключ \"" + key + "\" не найден в текущей сессии программы",
                "Ключ \"" + key + "\" не найден в текущей сессии программы");
    }

}

package ru.tensor.sbis.persons.util;

/**
 * Created by aa.mironychev on 12.08.16.
 */
interface NameFormat {

    /**
     * Возвращает форматированное ФИО
     */
    String format(String surname, String name, String patronymic);
}

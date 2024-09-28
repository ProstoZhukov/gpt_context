package ru.tensor.sbis.network_native.type;

/**
 * Тип ObjectId, соответствующий типу данных "Идентификатор объекта"
 */
public class ObjectId {

    private final long value;
    private final String object;

    /**
     * Конструктор
     *
     * @param value Значение идентификатора
     */
    @SuppressWarnings("unused")
    public ObjectId(long value) {
        this(value, "");
    }

    /**
     * Конструктор
     *
     * @param value Значение идентификатора
     * @param obj   Название объекта
     */
    public ObjectId(long value, String obj) {
        this.value = value;
        this.object = obj;
    }

    /**
     * Значение идентификатора
     */
    public long getValue() {
        return value;
    }

    /**
     * Название объекта
     */
    public String getObject() {
        return object;
    }
}

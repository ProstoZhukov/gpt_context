package ru.tensor.sbis.network_native.type;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.tensor.sbis.network_native.error.exceptions.UnknownFieldTypeException;


/**
 * Типы полей
 */
public enum FieldType {
    RECORD("Запись"),
    RECORDSET("Выборка"),
    INT("Число целое"),
    STRING("Строка"),
    TEXT("Строка"),
    FLOAT("Число вещественное"),
    DOUBLE("Число вещественное"),
    MONEY("Деньги"),
    DATE("Дата"),
    TIME("Время"),
    DATETIME("Дата и время"),
    ARRAY("Массив"),
    BOOLEAN("Логическое"),
    HIERARCHY("Иерархия"),
    IDENTIFIER("Идентификатор"),
    ENUM("Перечисляемое"),
    FLAGS("Флаги"),
    LINK("Связь"),
    BINARY("Двоичное"),
    UUID("UUID");

    private final String typeName;

    private static final Map<String, FieldType> objectTypeMatcher = new HashMap<String, FieldType>() {
        {
            put(Record.class.getName(), FieldType.RECORD);
            put(RecordSet.class.getName(), FieldType.RECORDSET);
            put(Integer.class.getName(), FieldType.INT);
            put(Long.class.getName(), FieldType.INT);
            put(String.class.getName(), FieldType.STRING);
            put(Float.class.getName(), FieldType.FLOAT);
            put(Double.class.getName(), FieldType.DOUBLE);
            put(Money.class.getName(), FieldType.MONEY);
            put(Date.class.getName(), FieldType.DATETIME);
            put(Boolean.class.getName(), FieldType.BOOLEAN);
        }
    };

    private static final Map<String, FieldType> typeNameToFieldTypeMatcher = new HashMap<String, FieldType>() {
        {
            put("Запись", FieldType.RECORD);
            put("Выборка", FieldType.RECORDSET);
            put("Число целое", FieldType.INT);
            put("Строка", FieldType.STRING);
            put("Число вещественное", FieldType.FLOAT);
            put("Деньги", FieldType.MONEY);
            put("Дата", FieldType.DATE);
            put("Время", FieldType.TIME);
            put("Дата и время", FieldType.DATETIME);
            put("Массив", FieldType.ARRAY);
            put("Логическое", FieldType.BOOLEAN);
            put("Иерархия", FieldType.HIERARCHY);
            put("Идентификатор", FieldType.IDENTIFIER);
            put("Перечисляемое", FieldType.ENUM);
            put("Флаги", FieldType.FLAGS);
            put("Связь", FieldType.LINK);
            put("Двоичное", FieldType.BINARY);
            put("UUID", FieldType.UUID);
            put("Текст", FieldType.TEXT);
        }
    };

    FieldType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Строковое название типа согласно БЛ.
     */
    public String getName() {
        return typeName;
    }

    /**
     * Получить тип объекта в рамках БЛ.
     *
     * @param instance объект, тип которого нужно узнать
     * @return тип объекта
     * @throws UnknownFieldTypeException Exception
     */
    @SuppressWarnings("unused")
    public static FieldType getType(@NonNull Object instance) throws UnknownFieldTypeException {
        return getType(instance.getClass());
    }

    /**
     * Получить тип объекта в рамках БЛ.
     *
     * @param instanceType тип объекта в java
     * @return тип объекта
     * @throws UnknownFieldTypeException Exception
     */
    public static FieldType getType(@NonNull Type instanceType) throws UnknownFieldTypeException {
        return getType(instanceType.getClass());
    }

    static FieldType getType(@NonNull Class<?> instanceClass) throws UnknownFieldTypeException {
        String instanceClassName = instanceClass.getName();
        FieldType type = objectTypeMatcher.get(instanceClassName);
        if (type == null) {
            throw new UnknownFieldTypeException(instanceClassName);
        }

        return type;
    }

    /**
     * Получить тип по его названию.
     *
     * @param typeName название типа
     * @return тип поля
     * @throws UnknownFieldTypeException Exception
     */
    public static FieldType getTypeByName(@NonNull String typeName) throws UnknownFieldTypeException {
        FieldType type = typeNameToFieldTypeMatcher.get(typeName);
        if (type == null && typeName.equals("JSON-объект")) {
            return FieldType.STRING; //TODO костыль для метода Персона.ССписокПоДиалогу, новые тип данных появился у физика, временно не будем их парсить
        }
        if (type == null) {
            throw new UnknownFieldTypeException(typeName);
        }

        return type;
    }
}

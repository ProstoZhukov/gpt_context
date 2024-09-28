package ru.tensor.sbis.network_native.type;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.tensor.sbis.network_native.error.exceptions.DuplicateKeyException;
import ru.tensor.sbis.network_native.error.exceptions.UnknownFieldTypeException;


/**
 * Класс для хранения формата рекорда: описания его полей.
 */
public class RecFormat extends ArrayList<FieldDescription> {

    // TODO: если из объекта удалять записи - требуется переформирование карты индексов:
    private final Map<String, Integer> fieldNameIndexes = new HashMap<>();

    @Override
    public boolean add(@NonNull FieldDescription fieldDescription) {
        fieldNameIndexes.put(fieldDescription.getFieldName(), size());

        return super.add(fieldDescription);
    }

    /**
     * Добавить описание в формат рекорда.
     *
     * @param fieldName название поля
     * @param instance  объект, который будем заносить в рекорд
     * @param ft        тип поля заносимого объекта
     * @return true в случае успеха
     * @throws DuplicateKeyException     Exception
     * @throws UnknownFieldTypeException Exception
     */
    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue"})
    public boolean add(String fieldName, @NonNull Object instance, @NonNull FieldType ft) throws DuplicateKeyException, UnknownFieldTypeException {
        if (fieldNameIndexes.containsKey(fieldName)) {
            throw new DuplicateKeyException(fieldName);
        }

        fill(fieldName, instance, ft);

        return true;
    }

    /**
     * Получить индекс поля внутри рекорда по его названию.
     *
     * @param fieldName название поля
     * @return индекс поля начиная с 0; если поле не найдено - вернется -1.
     */
    public int getIndexByFieldName(String fieldName) {
        Integer index = fieldNameIndexes.get(fieldName);
        if (index == null) {
            return -1;
        }

        return index;
    }

    /**
     * Добавить новую запись в список формата рекорда.
     *
     * @param fieldName название поля
     * @param instance  объект, который хотим занести
     * @param ft        тип поля
     * @throws UnknownFieldTypeException Exception
     */
    private void fill(String fieldName, @NonNull Object instance, @NonNull FieldType ft) throws UnknownFieldTypeException {
        String ftStr = ft.getName();
        JsonObject descr = new JsonObject();
        descr.addProperty("n", fieldName);

        switch (ft) {
            case RECORD:
            case RECORDSET:
            case INT:
            case STRING:
            case FLOAT:
            case DOUBLE:
            case MONEY:
            case DATE:
            case TIME:
            case DATETIME:
            case BOOLEAN:
            case IDENTIFIER:
            case LINK:
            case UUID:
            case BINARY:
                descr.addProperty("t", ftStr);
                add(new FieldDescription(fieldName, ftStr, descr));
                break;
            case ARRAY:
                JsonObject t = new JsonObject();
                t.addProperty("n", ftStr);

                Type instanceParametersType = ((ParameterizedType) instance.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                t.addProperty("t", (FieldType.getType(instanceParametersType)).getName());

                descr.add("t", t);
                add(new FieldDescription(fieldName, ftStr, descr));

                break;
            case HIERARCHY:
                descr.addProperty("t", "Идентификатор");
                descr.addProperty("s", "Иерархия");
                add(new FieldDescription(fieldName, ftStr, descr));

                descr = new JsonObject();
                descr.addProperty("n", fieldName + "@");
                descr.addProperty("t", "Логическое");
                add(new FieldDescription(fieldName, ftStr, descr));

                descr = new JsonObject();
                descr.addProperty("n", fieldName + "$");
                add(new FieldDescription(fieldName, ftStr, descr));

                break;
            case ENUM:
            case FLAGS:
            default:
                break;
        }
    }
}

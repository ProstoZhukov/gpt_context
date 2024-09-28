package ru.tensor.sbis.network_native.type;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

/**
 * Класс для описания поля внутри рекорда.
 */
@SuppressWarnings("unused")
public class FieldDescription {

    private String fieldName;
    private String fieldTypeString;
    private JsonObject fieldDescription;

    /**
     * Конструктор.
     *
     * @param fieldName        название поля
     * @param fieldTypeString  название типа поля
     * @param fieldDescription json объект описания поля
     */
    public FieldDescription(String fieldName, String fieldTypeString, JsonObject fieldDescription) {
        this.fieldName = fieldName;
        this.fieldTypeString = fieldTypeString;
        this.fieldDescription = fieldDescription;
    }

    /**
     * Getter для описания поля.
     *
     * @return описание поля
     */
    public JsonObject getFieldDescription() {
        return fieldDescription;
    }

    /**
     * Setter для описания поля
     *
     * @param fieldDescription JsonObject
     */
    public void setFieldDescription(JsonObject fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    /**
     * Getter для названия поля
     *
     * @return название поля
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Setter для названия поля
     *
     * @param fieldName наименование поля
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Getter для названия типа поля
     *
     * @return название типа поля
     */
    public String getFieldTypeString() {
        return fieldTypeString;
    }

    /**
     * Setter для названия типа поля
     *
     * @param fieldTypeString тип поля
     */
    public void setFieldTypeString(String fieldTypeString) {
        this.fieldTypeString = fieldTypeString;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FieldDescription)) {
            return super.equals(obj);
        }

        FieldDescription fieldObj = (FieldDescription) obj;

        return fieldObj.getFieldName().equals(fieldName) && fieldObj.getFieldTypeString().equals(fieldTypeString);
    }
}

package ru.tensor.sbis.network_native.type;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.tensor.sbis.network_native.error.exceptions.CommonSbisException;
import ru.tensor.sbis.network_native.error.exceptions.DuplicateKeyException;
import ru.tensor.sbis.network_native.error.exceptions.InconsistencyTypeException;
import ru.tensor.sbis.network_native.error.exceptions.UnknownFieldTypeException;


/**
 * Тип Record
 */
@SuppressWarnings("unused")
public class Record implements Iterable<Object> {

    private final RecFormat format;
    @NonNull
    private final List<Object> values;

    /**
     * Конструктор
     */
    public Record() {
        this(new RecFormat());
    }

    /**
     * Конструктор
     *
     * @param format формат рекорда
     */
    public Record(@NonNull RecFormat format) {
        this.format = format;
        values = new ArrayList<>(format.size());

        for (int i = 0, end = format.size(); i < end; ++i) {
            values.add(null);
        }
    }

    /**
     * Конструктор
     *
     * @param format формат рекорда
     * @param values значения
     */
    public Record(RecFormat format, @NonNull List<Object> values) {
        this.format = format;
        this.values = new ArrayList<>(values);
    }

    @NonNull
    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }

    /**
     * Содержит ли рекорда данное поле.
     *
     * @param fieldName название поля
     * @return true если содержит; иначе - false
     */
    @SuppressWarnings("unused")
    public boolean containsField(String fieldName) {
        return format.getIndexByFieldName(fieldName) != -1;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, long value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.INT);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public Record append(String name, int value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.INT);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, String value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.STRING);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public Record append(String name, boolean value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.BOOLEAN);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, double value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.DOUBLE);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, float value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.FLOAT);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, Money value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.MONEY);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, Date value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.DATETIME);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException Exception
     */
    @NonNull
    public Record append(String name, ObjectId value) throws DuplicateKeyException {
        try {
            append(name, value, FieldType.IDENTIFIER);
        } catch (UnknownFieldTypeException unknownFieldTypeException) {
            // не должно вообще произойти
        }

        return this;
    }

    /**
     * Функция добавления поля в запись. В случае существования одноименного поля или неверного типа объекта будет сгенерировано исключение.
     *
     * @param name  название поля
     * @param value присваиваемое значение
     * @param type  тип добавляемого объекта
     * @return ссылка на текущий рекорд
     * @throws DuplicateKeyException     Exception
     * @throws UnknownFieldTypeException Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public Record append(String name, Object value, FieldType type) throws UnknownFieldTypeException, DuplicateKeyException {
        format.add(name, value, type);
        values.add(value);

        return this;
    }

    /**
     * Установить соответствующему полю новое значение. В случае отсутствия такого поля или несоответствия типов будет выброшено исключение.
     *
     * @param fieldName название поля
     * @param value     новое значение поля
     * @return ссылка на текущий рекорд
     * @throws ru.tensor.sbis.network_native.error.exceptions.CommonSbisException Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public Record setValue(String fieldName, @NonNull Object value) throws CommonSbisException {
        int idx = format.getIndexByFieldName(fieldName);
        if (idx < 0) {
            throw new CommonSbisException("Поле \"" + fieldName + "\" отсутствует в формате записи",
                    "Поле \"" + fieldName + "\" отсутствует в формате записи");
        }

        Object oldValue = values.get(idx);
        if (oldValue != null && !oldValue.getClass().equals(value.getClass())) {
            throw new InconsistencyTypeException(oldValue.getClass().getName(), value.getClass().getName());
        }

        values.set(idx, value);

        return this;
    }

    /**
     * Получить значение из указанного поля.
     * Если в поле лежит неопределенное значение, то вернется значение по умолчанию.
     *
     * @param fieldName    название поля
     * @param defaultValue Значение по умолчанию. Возвращается в случае неопределенного значения в указанном поле.
     * @return значение поля
     */
    public <TResult> TResult getValue(String fieldName, TResult defaultValue) {
        int idx = format.getIndexByFieldName(fieldName);
        if (idx == -1) {
            return defaultValue;
        }

        Object value = values.get(idx);
        if (value == null) {
            return defaultValue;
        }

        //noinspection unchecked
        return (TResult) value;
    }

    public RecFormat getFormat() {
        return format;
    }
}

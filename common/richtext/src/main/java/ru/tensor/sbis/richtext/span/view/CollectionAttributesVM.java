package ru.tensor.sbis.richtext.span.view;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Вью-модель коллекции атрибутов-вью-моделей, используется для реализаций списочных View
 *
 * @author am.boldinov
 */
public abstract class CollectionAttributesVM<T extends BaseAttributesVM> extends BaseAttributesVM {

    @NonNull
    private final List<T> mAttributes = new ArrayList<>();

    public CollectionAttributesVM(@NonNull String tag) {
        super(tag);
    }

    /**
     * Добавляет вью-модель с атрибутами в коллекцию
     */
    public void addAttributes(@NonNull T vm) {
        mAttributes.add(vm);
    }

    /**
     * Возвращает вью-модель из коллекции по позиции
     */
    @NonNull
    public T getAttributesAt(int index) {
        return mAttributes.get(index);
    }

    /**
     * Возвращает список всех вью-моделей
     */
    @NonNull
    public final List<T> getAttributesList() {
        return mAttributes;
    }

    /**
     * Возвращает количество вью-моделей в коллекции
     */
    public final int getSize() {
        return mAttributes.size();
    }

}

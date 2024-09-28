package ru.tensor.sbis.richtext.span.view;

import android.text.Spannable;

import java.util.Iterator;

import androidx.annotation.NonNull;

/**
 * Вью-модель атрибутов, содержащая внутри себя стилизованниый контент (богатый текст).
 * Поддерживает списочные компоненты с богатым текстом.
 *
 * @author am.boldinov
 */
public abstract class ContentAttributesVM extends BaseAttributesVM {

    public ContentAttributesVM(@NonNull String tag) {
        super(tag);
    }

    /**
     * Создает итератор для перемещения по контенту вью-модели
     */
    @NonNull
    public final Iterator<Spannable> contentIterator() {
        return new Iterator<Spannable>() {
            private int position = -1;

            @Override
            public boolean hasNext() {
                return position < size() - 1;
            }

            @Override
            public Spannable next() {
                return getContent(++position);
            }
        };
    }

    /**
     * Возвращает размер размер вью-модели (количество вложенных элементов с богатым текстом).
     */
    protected abstract int size();

    /**
     * Возвращает стилизованный контент по позиции, на основе размера вью-модели
     */
    @NonNull
    protected abstract Spannable getContent(int index);
}

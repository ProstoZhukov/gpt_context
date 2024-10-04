package ru.tensor.sbis.design.view_factory.collection;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Объект, выполняющий построение макета.
 * Предназначен для инкапсуляции логики построения макета и возможности её повторного использования в различных View.
 *
 * @param <T> - тип макета
 *
 * @author am.boldinov
 */
public class LayoutDelegate<T extends View & DelegatingLayout> {

    /**
     * Макет, построение которого выполняется данным делегатом.
     */
    @NonNull
    protected final T layout;

    /**
     * Конструктор для создания объекта {@link LayoutDelegate}.
     *
     * @param layout layout, который необходимо построить.
     * */
    public LayoutDelegate(@NonNull T layout) {
        this.layout = layout;
    }

    /**
     * Получить макет, построение которого выполняется данным делегатом.
     */
    @NonNull
    public T getLayout() {
        return layout;
    }

    /**
     * Выполнить вычисление размеров макета.
     *
     * @return true - если delegate берет на себя построение макета и оно прошло успешно
     * (в этом случае метод должен вызвать {@link DelegatingLayout#setMeasuredDimensions(int, int)}
     * для передачи размеров макету), false - иначе
     */
    public boolean onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        return false;
    }

    /**
     * Выполнить размещение дочерних элементов.
     *
     * @return true - если delegate берет на себя размещение дочерних элментов макета
     * и оно прошло успешно, false - иначе
     */
    public boolean onLayout(boolean changed, int l, int t, int r, int b) {
        return false;
    }

}

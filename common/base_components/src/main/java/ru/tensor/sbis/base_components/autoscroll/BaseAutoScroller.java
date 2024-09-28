package ru.tensor.sbis.base_components.autoscroll;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.util.CommonUtils;

/**
 * Базовая реализация авто-скроллера для списка.
 *
 * @author am.boldinov
 */
@SuppressWarnings("rawtypes")
public abstract class BaseAutoScroller implements AutoScroller {

    /**
     * Интерфейс для сравнения элементов списка.
     */
    public interface Matcher {
        /**
         * Проверить, совпадают ли элементы списка.
         */
        boolean areItemsTheSame(@Nullable Object item1, @Nullable Object item2);
    }

    /**
     * Делегат для сравнения элементов списка.
     */
    @NonNull
    private final Matcher mMatcher;

    /**
     * Первый в списке объект до изменения данных.
     */
    @Nullable
    private Object mFirstBefore;

    @SuppressWarnings({"Convert2Lambda"})
    protected BaseAutoScroller() {
        this(new Matcher() {
            @Override
            public boolean areItemsTheSame(@Nullable Object item1, @Nullable Object item2) {
                return CommonUtils.equal(item1, item2);
            }
        });
    }

    protected BaseAutoScroller(@NonNull Matcher matcher) {
        mMatcher = matcher;
    }

    /**
     * Подготовиться к изменению контента.
     */
    protected abstract void onBeforeContentChanged();

    /**
     * Обработать изменение контента.
     * @param firstChanged - изменился ли первый элемент в списке
     */
    protected abstract void onAfterContentChanged(boolean firstChanged);

    @Override
    @CallSuper
    public void onBeforeContentChanged(@Nullable List beforeContent) {
        if (beforeContent != null && !beforeContent.isEmpty()) {
            // Запоминаем первый в списке объект до обновления данных
            mFirstBefore = beforeContent.get(0);
        } else {
            mFirstBefore = null;
        }
        onBeforeContentChanged();
    }

    @SuppressWarnings("unused")
    @Override
    @CallSuper
    public void onAfterContentChanged(@Nullable List afterContent) {
        boolean afterChanged = true;
        if (afterContent != null && !afterContent.isEmpty()) {
            // Проверяем, изменился ли первый элемент в списке после обновления данных
            afterChanged = !mMatcher.areItemsTheSame(mFirstBefore, afterContent.get(0));
        }
        onAfterContentChanged(afterChanged);
    }

}

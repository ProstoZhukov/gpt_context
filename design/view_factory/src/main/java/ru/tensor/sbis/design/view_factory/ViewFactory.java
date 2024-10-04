package ru.tensor.sbis.design.view_factory;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Интерфейс для объявления View Factory
 *
 * @author am.boldinov
 */
public interface ViewFactory<V extends View> {

    /** Метод для создания View элемента. */
    @NonNull V createView();

}

package ru.tensor.sbis.design.swipeback;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Операции со свайпом
 */
public interface SwipeBackFragmentOperations {

    /**
     * Установка слушателя
     */
    void setOnFragmentAddedListener(@Nullable OnFragmentAddedListener listener);

    /**
     * Установка фона
     */
    @SuppressWarnings("unused")
    void setBackground(@Nullable View view);

    /**
     * Обновление фона
     */
    void updateViewBackground(@Nullable BitmapDrawable background);
}

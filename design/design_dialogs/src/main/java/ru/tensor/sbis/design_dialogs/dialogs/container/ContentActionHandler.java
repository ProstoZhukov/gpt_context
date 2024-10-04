package ru.tensor.sbis.design_dialogs.dialogs.container;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Интерфейс, декларирующий возможность обработки какого-либо действия с контентом.
 *
 * @author sa.nikitin
 */
public interface ContentActionHandler {

    /**
     * Обработать действие с контентом.
     *
     * @param actionId      - идентификатор контента
     * @param data          - данные по действию
     */
    void onContentAction(@NonNull String actionId, @Nullable Bundle data);

    /**
     * Вспомогательные класс для реализации обработки действий.
     */
    class Helper {

        /**
         * Получить обработчик действий с контентом.
         *
         * @param fragment - фрагмент для которого нужно найти обработчика
         * @return контейнер, приведенный к типа {@link ContentActionHandler}
         * либо null, если не найден контейнер, реализующий данный тип.
         */
        @Nullable
        public static ContentActionHandler getActionHandler(@NonNull Fragment fragment) {
            Object parent = fragment.getTargetFragment();
            if (parent instanceof ContentActionHandler) {
                return (ContentActionHandler) parent;
            }
            parent = fragment.getParentFragment();
            if (parent instanceof ContentActionHandler) {
                return (ContentActionHandler) parent;
            }
            parent = fragment.getActivity();
            if (parent instanceof ContentActionHandler) {
                return (ContentActionHandler) parent;
            }
            return null;
        }
    }

}
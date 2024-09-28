package ru.tensor.sbis.design_dialogs.dialogs.content;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Интерфейс создателя экземпляра контента. Используется контейнером для
 * создания экземпляра фрагмента, содержащего отображаемый контент.
 *
 * @author sa.nikitin
 */
public interface BaseContentCreator {

    /**
     * Создать экземпляр фрагмента с контентом для отображения внутри контейнера
     *
     * @return экземпляр фрагмента с контентом
     */
    @NonNull
    Fragment createFragment();
}

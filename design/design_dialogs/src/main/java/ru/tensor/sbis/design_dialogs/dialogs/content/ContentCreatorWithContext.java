package ru.tensor.sbis.design_dialogs.dialogs.content;

import android.content.Context;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Интерфейс создателя экземпляра контента. Используется контейнером для
 * создания экземпляра фрагмента, содержащего отображаемый контент.
 */
public interface ContentCreatorWithContext extends Serializable {

    /**
     * Создать экземпляр фрагмента с контентом для отображения внутри контейнера
     *
     * @return экземпляр фрагмента с контентом
     */
    @NonNull
    Fragment createFragment(Context context);
}
//TODO Доработать работу с реализацией ContentCreator с контекстом
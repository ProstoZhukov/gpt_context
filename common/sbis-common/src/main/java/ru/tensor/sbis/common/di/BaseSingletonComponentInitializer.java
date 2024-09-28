package ru.tensor.sbis.common.di;

import androidx.annotation.NonNull;

/**
 * Created by am.boldinov on 16.08.2017.
 */

public abstract class BaseSingletonComponentInitializer<T> {

    @NonNull
    public final T init(@NonNull CommonSingletonComponent commonSingletonComponent) {
        T component = createComponent(commonSingletonComponent);
        initSingletons(component);
        return component;
    }

    @NonNull
    protected abstract T createComponent(@NonNull CommonSingletonComponent commonSingletonComponent);

    /**
     * Метод для создания "висящих" синглтонов при инициализации компонента,
     * которые провайдятся, к примеру, только для получения событий.
     *
     * @param singletonComponent инициализируемый компонент
     */
    protected void initSingletons(@NonNull T singletonComponent) {
    }

}

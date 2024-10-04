package ru.tensor.sbis.design_dialogs.dialogs.content.utils;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.tensor.sbis.design_dialogs.dialogs.container.Container;
import ru.tensor.sbis.design_dialogs.dialogs.content.Content;

/**
 * Вспомогательные методы для реализации фрагментов с контентом и их взаимодействия с контейнерами.
 *
 * @author sa.nikitin
 */
public class ContentFragmentUtils {

    private static Fragment requireFragment(@NonNull Content content) {
        if (content instanceof Fragment) {
            return (Fragment) content;
        }
        throw new IllegalArgumentException("Content " + content + " must be instance of " + Fragment.class.getCanonicalName());
    }

    /**
     * Получить контейнер, приведенный к типу {@link Container}.
     * @param content - фрагмент с контентом
     * @return контейнер приведенный к типу {@link Container}.
     */
    public static Container container(@NonNull Content content) {
        return containerAs(content, Container.class);
    }

    /**
     * Получение контейнера контента, приведенного к указанному типу.
     *
     * @param content   - фрагмент с конетнтом
     * @param type      - тип, к которому необходимо привести фрагмент
     * @param <T>       - тип, к которому необходимо привести фрагмент
     * @return контейнер, приведенный к нужному типу либо null
     */
    @Nullable
    public static <T> T containerAs(@NonNull Content content, @NonNull Class<? extends T> type) {
        Fragment fragment = requireFragment(content);
        Object parent = fragment.getParentFragment();
        if (parent == null) {
            parent = fragment.getActivity();
        }
        if (parent != null) {
            if (type.isInstance(parent)) {
                return type.cast(parent);
            }
        }
        return null;
    }

    /**
     * Получение контейнера контента, приведенного к указанному типу.
     * Если контейнер не наследут указанный тип, но сам является контентом, то идём вверх, к следующему контейнеру
     *
     * @param content   - фрагмент с конетнтом
     * @param type      - тип, к которому необходимо привести фрагмент
     * @return контейнер, приведенный к нужному типу либо null
     */
    @Nullable
    public static <T> T anyContainerAs(@NonNull Content content, @NonNull Class<? extends T> type) {
        T typedContainer = containerAs(content, type);
        if (typedContainer != null) {
            return typedContainer;
        } else {
            Content containerAsContent = containerAs(content, Content.class);
            //Если контейнер является контентом, то идём выше, к следующему контейнеру, возможно, он реализует нужный type
            if (containerAsContent != null) {
                return anyContainerAs(containerAsContent, type);
            } else {
                return null;
            }
        }
    }
    /**
     * Проверить контейнер на принадлежность указанному типу.
     *
     * @param content   - фрагмент с контентом
     * @param type      - тип, к которому необходимо проверить принадлежность
     * @return true если контейнер принадлежит указанному типу, false - иначе
     */
    @SuppressWarnings("rawtypes")
    public static boolean containerIs(@NonNull Content content, @NonNull Class type) {
        return containerAs(content, type) != null;
    }

    /**
     * Уведомить контейнер о совершении действия.
     *
     * @param content   - фрагмент с контентом
     * @param actionId  - идентификатор действия
     * @param bundle    - данные по действию
     */
    public static void didAction(@NonNull Content content, @NonNull String actionId, @Nullable Bundle bundle) {
        Container container = container(content);
        if (container != null) {
            container.onContentAction(actionId, bundle);
        }
    }

}

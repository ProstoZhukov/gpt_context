package ru.tensor.sbis.base_components.adapter.universal.diffutil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.base_components.adapter.universal.ObservableFieldsRebindHandler;
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
@SuppressWarnings("unused")
public interface DiffUtilBackgroundWrapper<T extends UniversalBindingItem> {

    @NonNull
    DiffResultWrapper<List<T>> calculateDiff(@Nullable List<T> oldItems, @NonNull List<T> newItems);

    @NonNull
    <D extends DiffUtilListProvider<T>> ru.tensor.sbis.base_components.adapter.universal.diffutil.DiffResultWrapper<D> calculateDiff(@Nullable List<T> oldItems,
                                                                                                                                                                             @NonNull D newListData);

    @NonNull
    DiffResultWrapper<List<T>> calculateDiff(@Nullable List<T> oldItems, @NonNull List<T> newItems,
                                                                                              @NonNull ObservableFieldsRebindHandler<T> observableFieldsRebindHandler);

    @NonNull
    <D extends DiffUtilListProvider<T>> DiffResultWrapper<D> calculateDiff(@Nullable List<T> oldItems,
                                                                           @NonNull D newListData,
                                                                           @NonNull ObservableFieldsRebindHandler<T> observableFieldsRebindHandler);

}

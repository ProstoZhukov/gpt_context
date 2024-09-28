package ru.tensor.sbis.base_components.adapter.universal.diffutil;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import ru.tensor.sbis.base_components.adapter.universal.ObservableFieldsRebindHandler;
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
@SuppressWarnings("unused")
public class DiffUtilBackgroundWrapperImpl<T extends UniversalBindingItem> implements DiffUtilBackgroundWrapper<T> {

    @NonNull
    @Override
    public DiffResultWrapper<List<T>> calculateDiff(@Nullable List<T> oldItems,
                                                                                                     @NonNull List<T> newItems) {
        return new DiffResultWrapper<>(newItems, calculateDiffResult(oldItems, newItems, null));
    }

    @NonNull
    @Override
    public <D extends DiffUtilListProvider<T>> DiffResultWrapper<D> calculateDiff(@Nullable List<T> oldItems,
                                                                                                                                                                                    @NonNull D newListData) {
        return new DiffResultWrapper<>(newListData, calculateDiffResult(oldItems, newListData.provideList(), null));
    }

    @NonNull
    @Override
    public DiffResultWrapper<List<T>> calculateDiff(@Nullable List<T> oldItems,
                                                                                                     @NonNull List<T> newItems,
                                                                                                     @NonNull ObservableFieldsRebindHandler<T> observableFieldsRebindHandler) {
        return new DiffResultWrapper<>(newItems, calculateDiffResult(oldItems, newItems, observableFieldsRebindHandler));
    }

    @NonNull
    @Override
    public <D extends DiffUtilListProvider<T>> DiffResultWrapper<D> calculateDiff(@Nullable List<T> oldItems,
                                                                                                                                   @NonNull D newListData,
                                                                                                                                   @NonNull ObservableFieldsRebindHandler<T> observableFieldsRebindHandler) {
        return new DiffResultWrapper<>(newListData, calculateDiffResult(oldItems, newListData.provideList(), observableFieldsRebindHandler));
    }

    @NonNull
    private DiffUtil.DiffResult calculateDiffResult(@Nullable List<T> oldItems, @NonNull List<T> newItems,
                                                    @Nullable ObservableFieldsRebindHandler<T> observableFieldsRebindHandler) {
        if (oldItems == null) {
            oldItems = Collections.emptyList();
        }
        UniversalDiffCallback<T> diffCallback;
        if (observableFieldsRebindHandler == null) {
            diffCallback = createDiffCallback(oldItems, newItems);
        } else {
            diffCallback = createDiffCallback(oldItems, newItems, observableFieldsRebindHandler);
        }
        return DiffUtil.calculateDiff(diffCallback);
    }

    @NonNull
    protected UniversalDiffCallback<T> createDiffCallback(@NonNull List<T> oldItems,
                                                                                                           @NonNull List<T> newItems) {
        return createDiffCallback(oldItems, newItems, null);
    }

    @NonNull
    protected UniversalDiffCallback<T> createDiffCallback(@NonNull List<T> oldItems,
                                                                                                           @NonNull List<T> newItems,
                                                                                                           @Nullable ObservableFieldsRebindHandler<T> observableFieldsRebindHandler) {
        return new UniversalDiffCallback<>(oldItems, newItems, observableFieldsRebindHandler);
    }
}

package ru.tensor.sbis.base_components.adapter.checkable.impl;

import java.util.HashSet;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableListAdapter;
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckItemsHelper;

/**
 * In this class used key and type (see {@link ru.tensor.sbis.base_components.adapter.checkable.impl.CheckHelperImpl.KeyFactory}
 * interface) as equal values for ability observe checked items not count only.
 * That needs, for example, in dialogs screen where needed to analyse checked items for
 * properly state of bottom actions panel (actually button visibility in panel).
 * For example, see {@link ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper} that
 * only provides count of checked items.
 * @param <T> - type of items.
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class ObservableCheckItemsHelperImpl<T> extends CheckHelperImpl<T, T> implements ObservableCheckItemsHelper<T> {

    private final PublishSubject<HashSet<T>> mSubject;

    public ObservableCheckItemsHelperImpl(@NonNull KeyFactory<T, T> factory) {
        super(factory);
        mSubject = PublishSubject.create();
    }

    @Override
    public void attachToAdapter(@NonNull CheckableListAdapter<T> adapter) {
        super.attachToAdapter(adapter);
        raise();
    }

    @Override
    public void setChecked(@NonNull T item, boolean checked) {
        super.setChecked(item, checked);
        raise();
    }

    @Override
    public void clearChecks() {
        super.clearChecks();
        raise();
    }

    @Override
    public void checkAll() {
        super.checkAll();
        raise();
    }

    @Override
    public void invertAll() {
        super.invertAll();
        raise();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        raise();
    }

    private void raise() {
        mSubject.onNext(mCheckedKeys);
    }

    @Override
    protected boolean needSaveState() {
        return false;
    }

    @NonNull
    @Override
    public Observable<HashSet<T>> getCheckedItemsObservable() {
        return mSubject;
    }

    @NonNull
    public HashSet<T> getCheckedItems() {
        return mCheckedKeys;
    }

}

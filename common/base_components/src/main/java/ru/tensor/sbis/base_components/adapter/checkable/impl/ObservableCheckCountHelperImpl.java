package ru.tensor.sbis.base_components.adapter.checkable.impl;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableListAdapter;
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper;

/**
 * SelfDocumented
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class ObservableCheckCountHelperImpl<T> extends CheckHelperImpl<String, T> implements ObservableCheckCountHelper<T> {

    private final PublishSubject<Integer> mSubject;

    public ObservableCheckCountHelperImpl(@NonNull KeyFactory<String, T> factory) {
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
        mSubject.onNext(getCheckedCount());
    }

    @NonNull
    @Override
    public Observable<Integer> getCheckedCountObservable() {
        return mSubject;
    }

}

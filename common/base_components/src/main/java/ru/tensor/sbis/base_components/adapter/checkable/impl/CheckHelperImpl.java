package ru.tensor.sbis.base_components.adapter.checkable.impl;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableListAdapter;

/**
 * Implementation of helper with storage keys of checked items.
 *
 * @param <ITEM> - type of items
 */
@SuppressWarnings("unused")
public class CheckHelperImpl<KEY, ITEM> implements CheckHelper<ITEM> {

    private static final String CHECKED_KEYS_STATE = CheckHelperImpl.class.getCanonicalName() + ".CHECKED_KEYS_STATE";

    /** SelfDocumented */
    protected HashSet<KEY> mCheckedKeys = new HashSet<>();

    @NonNull
    private final KeyFactory<KEY, ITEM> mKeyFactory;

    @Nullable
    private CheckableListAdapter<ITEM> mAdapter;

    @NonNull
    private final BehaviorSubject<Boolean> mCheckModeEnabledSubject;

    public CheckHelperImpl(@NonNull KeyFactory<KEY, ITEM> factory) {
        mKeyFactory = factory;
        mCheckModeEnabledSubject = BehaviorSubject.createDefault(false);
    }

    @Override
    public void attachToAdapter(@NonNull CheckableListAdapter<ITEM> adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            mAdapter.attachHelper(this);
        }
    }

    @Override
    public void detachFromAdapter() {
        if (mAdapter != null) {
            mAdapter.detachHelper(this);
            mAdapter = null;
        }
    }

    /** SelfDocumented */
    @Nullable
    public KEY getKey(@NonNull ITEM item) {
        return mKeyFactory.getKey(item);
    }

    @CallSuper
    @Override
    public void setChecked(@NonNull ITEM item, boolean checked) {
        final KEY key = getKey(item);
        if (key != null) {
            if (checked) {
                mCheckedKeys.add(key);
            } else {
                mCheckedKeys.remove(key);
            }
            if (mAdapter != null) {
                mAdapter.onChecked(item, checked);
            }
        }
    }

    @Override
    public void setCheckedTheseAndUncheckedOther(@NonNull ArrayList<ITEM> items) {
        if (mAdapter != null) {
            for (ITEM item : mAdapter.getContent()) {
                final KEY key = getKey(item);
                if (items.contains(item)) {
                    if (!mCheckedKeys.contains(key)) {
                        mCheckedKeys.add(key);
                        mAdapter.onChecked(item, true);
                    }
                } else {
                    if (mCheckedKeys.contains(key)) {
                        mCheckedKeys.remove(key);
                        mAdapter.onChecked(item, false);
                    }
                }
            }
        }
    }

    @CallSuper
    @Override
    public void clearChecks() {
        if (mAdapter != null) {
            mAdapter.onClearChecks();
        }
        mCheckedKeys.clear();
    }

    @CallSuper
    @Override
    public void checkAll() {
        if (mAdapter != null) {
            mAdapter.onCheckAll();
            for (ITEM item : mAdapter.getContent()) {
                mCheckedKeys.add(mKeyFactory.getKey(item));
            }
        }
    }

    @CallSuper
    @Override
    public void invertAll() {
        if (mAdapter != null) {
            mAdapter.onInvertCheckAll();
            for (ITEM item : mAdapter.getContent()) {
                KEY key = mKeyFactory.getKey(item);
                if (key != null) {
                    if (mCheckedKeys.contains(key)) {
                        mCheckedKeys.remove(key);
                    } else {
                        mCheckedKeys.add(key);
                    }
                }
            }
        }
    }

    @Override
    public boolean isChecked(@NonNull ITEM item) {
        final KEY key = getKey(item);
        return mCheckedKeys.contains(key);
    }

    @Override
    public int getCheckedCount() {
        if (mAdapter != null) {
            List<ITEM> presented = mAdapter.getContent();
            if (presented.isEmpty()) {
                return 0;
            }
            // Create set of presented keys
            Set<KEY> presentedKeys = new HashSet<>(presented.size());
            for (ITEM item : presented) {
                presentedKeys.add(getKey(item));
            }
            int count = 0;
            // Iterate checked keys and try to find it in presented keys
            for (KEY checkedKey : mCheckedKeys) {
                if (presentedKeys.contains(checkedKey)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    /**
     * NOTE! Because adapter based on two way pagination, adapter can store
     * max 200 items!
     *
     * @return - list of checked items from adapter that match checked keys.
     */
    @NonNull
    @Override
    public List<ITEM> getChecked() {
        if (mAdapter != null) {
            return mAdapter.getChecked();
        }
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<ITEM> getUnchecked() {
        if (mAdapter != null) {
            return mAdapter.getUnchecked();
        }
        return Collections.emptyList();
    }

    @NonNull
    public HashSet<KEY> getCheckedKeys() {
        return mCheckedKeys;
    }

    @Override
    public void onContentChanged() {
        // do nothing
    }

    @Override
    public void clearNotPresentedChecks() {
        if (mAdapter != null) {
            List<ITEM> presented = mAdapter.getContent();
            if (presented.isEmpty()) {
                mCheckedKeys.clear();
                return;
            }
            // Create set of presented keys
            Set<KEY> presentedKeys = new HashSet<>(presented.size());
            for (ITEM item : presented) {
                presentedKeys.add(getKey(item));
            }
            // Iterate checked keys and try to find it in presented keys
            Iterator<KEY> iterator = mCheckedKeys.iterator();
            while (iterator.hasNext()) {
                KEY checkedKey = iterator.next();
                if (!presentedKeys.contains(checkedKey)) {
                    // Remove if key not presented already
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void disableCheckMode() {
        mCheckModeEnabledSubject.onNext(false);
        clearChecks();
    }

    @Override
    public void enableCheckMode() {
        mCheckModeEnabledSubject.onNext(true);
    }

    @Override
    public boolean isCheckModeEnabled() {
        return mCheckModeEnabledSubject.getValue();
    }

    @Override
    public Observable<Boolean> getCheckModeEnabledObservable() {
        return mCheckModeEnabledSubject.distinctUntilChanged();
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (needSaveState()) {
            String[] checked = new String[mCheckedKeys.size()];
            //noinspection SuspiciousToArrayCall
            mCheckedKeys.toArray(checked);
            outState.putStringArray(CHECKED_KEYS_STATE, checked);
        }
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (needSaveState()) {
            if (savedInstanceState == null) {
                return;
            }
            String[] checked = savedInstanceState.getStringArray(CHECKED_KEYS_STATE);
            if (checked != null && checked.length > 0) {
                mCheckedKeys.clear();
                mCheckedKeys.addAll(Arrays.asList((KEY[]) checked));
            }
        }
    }

    /**
     * Override if not needed to save and restore helper's state using
     * onSaveInstanceState/onRestoreInstance mechanism. It needed in news module,
     * where legacy code and not MVP. See {@link #onSaveInstanceState(Bundle)} and
     * {@link #onRestoreInstanceState(Bundle)} methods for more information.
     *
     * @return - false if not needed to save and restore helper's state using
     * onSaveInstanceState/onRestoreInstance mechanism.
     */
    protected boolean needSaveState() {
        return true;
    }

    /** SelfDocumented */
    public interface KeyFactory<KEY, ITEM> {

        @Nullable
        KEY getKey(@NonNull ITEM entity);
    }

}

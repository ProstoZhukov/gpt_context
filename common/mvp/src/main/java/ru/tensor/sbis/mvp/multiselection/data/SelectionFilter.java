package ru.tensor.sbis.mvp.multiselection.data;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.communication_decl.recipient_selection.FilterKey;
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionInteractor;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionPresenter;

/**
 * Data class for transferring values from any implementations of {@link MultiSelectionPresenter} to implementation of{@link MultiSelectionInteractor}.
 * The set of keys for bundle is expanded by creating a custom enum with new keys for your screen (for example {@link RecipientSelectionFilterKeys}).
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "CanBeFinal", "unused", "RedundantSuppression"})
public class SelectionFilter {
    @SuppressWarnings("FieldMayBeFinal")
    @NonNull
    private Bundle mBundle;

    public SelectionFilter(@NonNull String searchQuery) {
        mBundle = new Bundle();
        with(BaseFilterKeys.SEARCH_QUERY, searchQuery);
    }

    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, @Nullable Serializable value) {
        mBundle.putSerializable(key.key(), value);
        return this;
    }

    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, boolean value) {
        mBundle.putBoolean(key.key(), value);
        return this;
    }

    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, int value) {
        mBundle.putInt(key.key(), value);
        return this;
    }


    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, long value) {
        mBundle.putLong(key.key(), value);
        return this;
    }

    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, @Nullable List<UUID> value) {
        mBundle.putSerializable(key.key(), new ArrayList<>(value));
        return this;
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("UnusedReturnValue")
    public SelectionFilter with(@NonNull FilterKey key, @Nullable String value) {
        mBundle.putString(key.key(), value);
        return this;
    }

    /**
     * @SelfDocumented
     */
    public SelectionFilter with(@NonNull FilterKey key, @Nullable Parcelable value) {
        mBundle.putParcelable(key.key(), value);
        return this;
    }

    /**
     * @SelfDocumented
     */
    @Nullable
    public Serializable getSerializable(@NonNull FilterKey key) {
        return mBundle.getSerializable(key.key());
    }

    /**
     * @SelfDocumented
     */
    public ArrayList<String> getSerializableArrayList(FilterKey key) {
        return mBundle.getStringArrayList(key.key());
    }

    /**
     * @SelfDocumented
     */
    public ArrayList<Integer> getIntegerArrayList(FilterKey key) {
        return mBundle.getIntegerArrayList(key.key());
    }

    /**
     * @SelfDocumented
     */
    @Nullable
    public Parcelable getParcelable(@NonNull FilterKey key) {
        return mBundle.getParcelable(key.key());
    }

    /**
     * @SelfDocumented
     */
    public boolean getBoolean(@NonNull FilterKey key) {
        return mBundle.getBoolean(key.key());
    }

    /**
     * @SelfDocumented
     */
    public int getInt(@NonNull FilterKey key) {
        return mBundle.getInt(key.key());
    }

    /**
     * @SelfDocumented
     */
    public long getLong(@NonNull FilterKey key) {
        return mBundle.getLong(key.key(), 0L);
    }

    /**
     * @SelfDocumented
     */
    @Nullable
    public String getString(@NonNull FilterKey key) {
        return mBundle.getString(key.key());
    }

    /**
     * @SelfDocumented
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public List<UUID> getUuidList(@NonNull FilterKey key) {
        return (List<UUID>) mBundle.get(key.key());

    }

}

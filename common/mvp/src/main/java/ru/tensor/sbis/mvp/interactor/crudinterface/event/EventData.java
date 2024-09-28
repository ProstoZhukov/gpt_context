package ru.tensor.sbis.mvp.interactor.crudinterface.event;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * Событие
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public class EventData {

    @NonNull
    private final String mName;
    @NonNull
    private final Map<String, String> mData;

    public EventData(@NonNull String name, @NonNull Map<String, String> data) {
        mName = name;
        mData = data;
    }

    /**
     * @SelfDocumented
     */
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * @SelfDocumented
     */
    @NonNull
    public Map<String, String> getData() {
        return mData;
    }

    /**
     * @SelfDocumented
     */
    public boolean isEvent(@NonNull String name) {
        return mName.equals(name);
    }
}

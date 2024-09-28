package ru.tensor.sbis.common.util.sharedprefs;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;

/**
 * List provider implementation that stores list in shared preferences.
 * @param <T> - type of items in list
 *
 * @author am.boldinov
 */
public abstract class ListPreferenceProvider<T> implements ListProvider<T> {

    @NonNull
    private final SharedPreferences preferences;

    @NonNull
    private final String key;

    @NonNull
    private final Type type;

    @NonNull
    protected abstract Type createType();

    /**
     * Create gson for serializing/deserializing.
     * Override this method to specify gson properties.
     * @return instance of gson
     */
    protected Gson createGson() {
        return new Gson();
    }

    /**
     * Получит экземпляр Gson.
     * @return экземпляр Gson
     */
    @NonNull
    private Gson getGson() {
        return createGson();
    }

    public ListPreferenceProvider(@NonNull SharedPreferences preferences, @NonNull String key) {
        this.preferences = preferences;
        this.key = key;
        type = createType();
    }

    /**
     * Deserialize list from shared preferences.
     * @return list
     */
    @Nullable
    @Override
    public List<T> get() {
        String serialized = preferences.getString(key, null);
        if (!TextUtils.isEmpty(serialized)) {
            try {
                return getGson().fromJson(serialized, type);
            } catch (JsonSyntaxException e) {
                Timber.e(e);
                return null;
            }
        }
        return null;
    }

    /**
     * Serialize list to shared preferences.
     * @param list
     */
    @SuppressLint("ApplySharedPref")
    @Override
    public void set(@Nullable List<T> list) {
        String serialized = getGson().toJson(list, type);
        preferences.edit()
                .putString(key, serialized)
                .commit();
    }

    protected SharedPreferences getPreferences() {
        return preferences;
    }

}

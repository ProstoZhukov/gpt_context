package ru.tensor.sbis.common.data.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.tensor.sbis.common.util.CommonUtils;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public class JsonViewModel {

    public interface Consumer {
        boolean accept(@NonNull JsonViewModel jsonViewModel);
    }

    private static final class GsonHolder {
        @NonNull
        private static final Gson gson = new Gson();
        @NonNull
        private static final Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
    }

    @NonNull
    private final Map<String, Object> mProperties;

    private JsonViewModel(@NonNull Map<String, Object> properties) {
        mProperties = properties;
    }

    public JsonViewModel(@NonNull String json) {
        mProperties = GsonHolder.gson.fromJson(json, GsonHolder.type);
    }

    public Object get(String key) {
        return mProperties.get(key);
    }

    public String getAsString(String key) {
        return getAsString(key, null);
    }

    public String getAsString(String key, String defaultValue) {
        final Object value = get(key);
        if (value != null) {
            return (String) value;
        }
        return defaultValue;
    }

    public String getAsStringNonEmpty(String key) {
        return getAsStringNonEmpty(key, null);
    }

    public String getAsStringNonEmpty(String key, String defaultValue) {
        final String value = getAsString(key);
        if (!CommonUtils.isEmpty(value)) {
            return value;
        }
        return defaultValue;
    }

    public int getAsInt(String key) {
        return getAsInt(key, -1);
    }

    public int getAsInt(String key, int defaultValue) {
        final Object value = get(key);
        if (value != null) {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Double) {
                return ((Double) value).intValue();
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            }
        }
        return defaultValue;
    }

    public long getAsLong(String key) {
        return getAsLong(key, -1L);
    }

    public long getAsLong(String key, long defaultValue) {
        final Object value = get(key);
        if (value != null) {
            if (value instanceof Double) {
                return ((Double) value).longValue();
            }
        }
        return defaultValue;
    }

    public double getAsDouble(String key) {
        return getAsDouble(key, -1);
    }

    public double getAsDouble(String key, double defaultValue) {
        final Object value = get(key);
        if (value != null) {
            if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (!getAsString(key).isEmpty()) {
                try {
                    return Double.parseDouble(getAsString(key));
                } catch (NumberFormatException e) {
                    Timber.d(e, "Parsing error: %s", value);
                }
            }
        }
        return defaultValue;
    }

    public boolean getAsBoolean(String key) {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(String key, boolean defaultValue) {
        final Object value = get(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean(getAsString(key));
            } else if (value instanceof Integer) {
                return getAsInt(key) == 1;
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public JsonViewModel getAsViewModel(String key) {
        final Object value = get(key);
        if (value instanceof Map) {
            return new JsonViewModel((Map<String, Object>) value);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAsPropertiesList(String key) {
        return (List<Map<String, Object>>) get(key);
    }

    @NonNull
    public Map<String, Object> getProperties() {
        return mProperties;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public boolean isEmpty() {
        return mProperties.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        builder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                if ((src == Math.floor(src)) && !Double.isInfinite(src)) {
                    return new JsonPrimitive(src.longValue());
                }
                return new JsonPrimitive(src);
            }
        });
        return builder.create().toJson(mProperties, GsonHolder.type);
    }

    @SuppressWarnings("unchecked")
    public static void forEach(JsonViewModel jsonViewModel, String key, @NonNull Consumer consumer) {
        final Object value = jsonViewModel.get(key);
        if (value instanceof List) {
            final List<Object> itemList = (List<Object>) value;
            for (Object item : itemList) {
                if (item instanceof Map) {
                    final JsonViewModel model = new JsonViewModel((Map<String, Object>) item);
                    if (!consumer.accept(model)) {
                        break;
                    }
                }
            }
        }
    }
}

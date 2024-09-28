package ru.tensor.sbis.network_native.parser.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 17.11.2015.
 */
@SuppressWarnings("unused")
public class BaseModel {

    @NonNull
    private final Map<String, Object> mFieldMap;

    public BaseModel() {
        mFieldMap = new HashMap<>();
    }

    public Object put(String key, Object value) {
        return mFieldMap.put(key, value);
    }

    public Object get(String key) {
        return mFieldMap.get(key);
    }

    @NonNull
    public String getAsString(String key) {
        return (String) mFieldMap.get(key);
    }

    public int getAsInt(String key) {
        int result = -1;
        if (mFieldMap.get(key) != null) {
            if (mFieldMap.get(key) instanceof Double) {
                result = ((Double) mFieldMap.get(key)).intValue();
            }
        }
        return result;
    }

    public int getAsIntFromArray(String key) {
        int result = -1;
        if (mFieldMap.get(key) != null) {
            if (mFieldMap.get(key) instanceof Object[]) {
                Object[] keyValues = (Object[]) mFieldMap.get(key);
                if (keyValues.length > 0) {
                    result = ((Double) keyValues[0]).intValue();
                }
            }
        }
        return result;
    }

    public long getAsLong(String key) {
        long result = -1L;
        if (mFieldMap.get(key) != null) {
            if (mFieldMap.get(key) instanceof Double) {
                result = ((Double) mFieldMap.get(key)).longValue();
            }
        }
        return result;
    }

    public boolean getAsBoolean(String key) {
        boolean result = false;
        if (mFieldMap.get(key) != null) {
            if (mFieldMap.get(key) instanceof Boolean) {
                result = (boolean) mFieldMap.get(key);
            } else {
                result = Boolean.parseBoolean(getAsString(key));
            }
        }
        return result;
    }

    public double getAsDouble(String key) {
        double result = -1;
        if (mFieldMap.get(key) != null && mFieldMap.get(key) instanceof Double) {
            return (Double) mFieldMap.get(key);
        }
        if (mFieldMap.get(key) != null && !getAsString(key).isEmpty()) {
            try {
                result = Double.parseDouble(getAsString(key));
            } catch (NumberFormatException e) {
                Timber.d(e, "Parsing error: %s", mFieldMap.get(key));
            }
        }
        return result;
    }

    @Nullable
    public Double getAsCoordinate(String key) {
        Double result = null;
        if (mFieldMap.get(key) != null) {
            if (mFieldMap.get(key) instanceof Double) {
                return (Double) mFieldMap.get(key);
            }
        }
        if (mFieldMap.get(key) != null && !getAsString(key).isEmpty()) {
            try {
                result = Double.valueOf(getAsString(key));
            } catch (NumberFormatException nfe) {
                Timber.d(nfe, "Method getAsCoordinate parsing error: %s", mFieldMap.get(key));
            }
        }
        return result;
    }

    @Nullable
    public BaseModelList getAsList(String key) {
        return (BaseModelList) mFieldMap.get(key);
    }


    public interface DateParser {
        Date convert(String s);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Date getAsDate(@NonNull String key, @NonNull DateParser parser) {
        Date dataAsDate = null;
        String data = getAsString(key);
        if (data != null) {
            return parser.convert(data);
        }
        return dataAsDate;
    }


    public boolean contains(String key) {
        return get(key) != null;
    }
}

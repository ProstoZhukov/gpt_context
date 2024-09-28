package ru.tensor.sbis.network_native.apiservice.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy-код
 * <p>
 * RecordSet для хранения данных, полученных от веб-сервиса
 */
@SuppressWarnings("unused")
public class RecordSet {

    @NonNull
    @SerializedName("s")
    private final List<Params.Param> mTypes;

    @NonNull
    @SerializedName("d")
    private final List<List<Object>> mValues = new ArrayList<>();

    public RecordSet(@NonNull List<Params.Param> types) {
        mTypes = types;
    }

    public void addRecord(@NonNull List<Object> record) {

        if (record.size() != mTypes.size()) {
            throw new IllegalStateException(
                    "The values size must be equals the types size set in constructor.");
        }

        mValues.add(record);
    }

    @NonNull
    List<List<Object>> getValues() {
        return mValues;
    }

    @NonNull
    List<Params.Param> getTypes() {
        return mTypes;
    }
}

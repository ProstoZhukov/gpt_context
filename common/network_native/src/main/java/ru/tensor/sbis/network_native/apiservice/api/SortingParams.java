package ru.tensor.sbis.network_native.apiservice.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * SelfDocumented
 */
@SuppressWarnings("FieldCanBeLocal")
class SortingParams {

    @SuppressWarnings("unused")
    @NonNull
    @SerializedName("s")
    private final List<Params.Param> mTypes;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @NonNull
    @SerializedName("d")
    private final List<List<Object>> mValues;

    public SortingParams(@NonNull Params sortingValues) {
        mTypes = sortingValues.getTypes();
        mValues = new ArrayList<>();
        mValues.add(sortingValues.getValues());
    }
}

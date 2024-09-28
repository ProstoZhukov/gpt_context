package ru.tensor.sbis.richtext.span.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Тип размера обтекаемой View в тексте
 *
 * @author am.boldinov
 */
public enum ViewSizeType {
    PERCENT("%"),
    PIXEL("px");

    @NonNull
    private final String mType;

    ViewSizeType(@NonNull String type) {
        mType = type;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    @Nullable
    public static ViewSizeType detect(@NonNull String input) {
        for (ViewSizeType value : values()) {
            if (input.contains(value.getType())) {
                return value;
            }
        }
        return null;
    }
}

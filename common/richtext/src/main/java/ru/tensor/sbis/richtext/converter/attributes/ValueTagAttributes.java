package ru.tensor.sbis.richtext.converter.attributes;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Атрибуты тега для одного значения
 *
 * @author am.boldinov
 */
public final class ValueTagAttributes implements TagAttributes  {

    @Nullable
    private final String mValue;
    @NonNull
    private final String mTag;
    @Nullable
    private final TagAttributes mParent;

    public ValueTagAttributes(@Nullable String value, @NonNull String tag, @Nullable TagAttributes parent) {
        mValue = value;
        mTag = tag;
        mParent = parent;
    }

    @Nullable
    @Override
    public String getValue(@NonNull String attr) {
        return mValue;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(mValue);
    }

    @NonNull
    @Override
    public String getTag() {
        return mTag;
    }

    @Nullable
    @Override
    public TagAttributes getParent() {
        return mParent;
    }
}

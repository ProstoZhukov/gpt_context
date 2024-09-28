package ru.tensor.sbis.richtext.converter.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Атрибуты json markup-model
 *
 * @author am.boldinov
 */
class JsonTagAttributes implements TagAttributes {

    @Nullable
    private final Map<String, String> mAttributes;
    @NonNull
    private final String mTag;
    @Nullable
    private TagAttributes mParent;

    private JsonTagAttributes(@NonNull String tag, @Nullable Map<String, String> attributes) {
        mTag = tag;
        mAttributes = attributes;
    }

    @NonNull
    static JsonTagAttributes wrap(@NonNull String tag, @Nullable Map<String, String> attributes) {
        return new JsonTagAttributes(tag, attributes);
    }

    void setParent(@Nullable TagAttributes parent) {
        mParent = parent;
    }

    @Nullable
    @Override
    public String getValue(@NonNull String attr) {
        return mAttributes != null ? mAttributes.get(attr) : null;
    }

    @Override
    public boolean isEmpty() {
        return mAttributes == null || mAttributes.isEmpty();
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

package ru.tensor.sbis.richtext.converter.attributes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Представление атрибутов для одновременного получения значений из двух разных источников-атрибутов
 * по принципу - если в первом значение отсутствует идем во второй
 *
 * @author am.boldinov
 */
public final class PairTagAttributes implements TagAttributes {

    @NonNull
    private final TagAttributes mFirst;
    @NonNull
    private final TagAttributes mSecond;

    public PairTagAttributes(@NonNull TagAttributes first, @NonNull TagAttributes second) {
        mFirst = first;
        mSecond = second;
    }

    @Nullable
    @Override
    public String getValue(@NonNull String attr) {
        final String first = mFirst.getValue(attr);
        return first == null ? mSecond.getValue(attr): first;
    }

    @Override
    public boolean isEmpty() {
        return mFirst.isEmpty() && mSecond.isEmpty();
    }

    @NonNull
    @Override
    public String getTag() {
        return mFirst.getTag();
    }

    @Nullable
    @Override
    public TagAttributes getParent() {
        return mFirst.getParent() != null ? mFirst.getParent() : mSecond.getParent();
    }
}

package ru.tensor.sbis.common.modelmapper;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class SetMapper<SOURCE_TYPE, TARGET_TYPE> implements Function<Set<SOURCE_TYPE>, Set<TARGET_TYPE>> {

    @NonNull
    private final Function<? super SOURCE_TYPE, ? extends TARGET_TYPE> mItemMapper;

    public SetMapper(@NonNull Function<? super SOURCE_TYPE, ? extends TARGET_TYPE> itemMapper) {
        mItemMapper = itemMapper;
    }

    @Override
    public Set<TARGET_TYPE> apply(@NonNull Set<SOURCE_TYPE> source) throws Exception {
        Set<TARGET_TYPE> result = new HashSet<>(source.size());
        for (SOURCE_TYPE item : source) {
            result.add(mItemMapper.apply(item));
        }
        return result;
    }
}
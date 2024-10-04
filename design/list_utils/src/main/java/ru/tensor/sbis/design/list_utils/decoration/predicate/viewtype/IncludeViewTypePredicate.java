package ru.tensor.sbis.design.list_utils.decoration.predicate.viewtype;

import androidx.annotation.NonNull;

import ru.tensor.sbis.design.list_utils.decoration.predicate.ViewTypePredicate;

/**
 * Реализация выбора элементов для декорирования на основе допустимых view type.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class IncludeViewTypePredicate extends ViewTypePredicate {

    /**
     * Допустимые типы view.
     */
    private final int[] mViewTypes;

    public IncludeViewTypePredicate(int... viewTypes) {
        this(Target.CURRENT, false, viewTypes);
    }

    public IncludeViewTypePredicate(@NonNull Target target, boolean decorateIfTargetMissing, int... viewTypes) {
        super(target, decorateIfTargetMissing);
        mViewTypes = viewTypes;
    }

    @Override
    protected boolean needToDecorate(int viewType) {
        for (int includingType : mViewTypes) {
            if (viewType == includingType) {
                return true;
            }
        }
        return false;
    }

}

package ru.tensor.sbis.design.list_utils.decoration.predicate.viewtype;

import androidx.annotation.NonNull;

import ru.tensor.sbis.design.list_utils.decoration.predicate.ViewTypePredicate;

/**
 * Реализация выбора элементов для декорирования на основе недопустимых view type.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class ExcludeViewTypePredicate extends ViewTypePredicate {

    /**
     * Недопустимые типы view.
     */
    private final int[] mViewTypes;

    public ExcludeViewTypePredicate(int... viewTypes) {
        this(Target.CURRENT, false, viewTypes);
    }

    public ExcludeViewTypePredicate(@NonNull Target target, boolean decorateIfTargetMissing, int... viewTypes) {
        super(target, decorateIfTargetMissing);
        mViewTypes = viewTypes;
    }

    @Override
    protected boolean needToDecorate(int viewType) {
        for (int includingType : mViewTypes) {
            if (viewType == includingType) {
                return false;
            }
        }
        return true;
    }

}
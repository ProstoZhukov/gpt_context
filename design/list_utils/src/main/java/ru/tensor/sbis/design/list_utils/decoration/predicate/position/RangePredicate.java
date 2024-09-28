package ru.tensor.sbis.design.list_utils.decoration.predicate.position;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.design.list_utils.decoration.predicate.PositionPredicate;

/**
 * Реализация выбора элементов декорирования на основе диапазона позиций в списке.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class RangePredicate extends PositionPredicate {

    /**
     * Начиная с какой позиции необходимо декорировать.
     */
    private final int mStart;

    /**
     * До какой позиции необходимо декорировать.
     */
    private final int mEnd;

    public RangePredicate(int start, int end) {
        mStart = start;
        mEnd = end;
    }

    @Override
    protected boolean needToDecorateItem(int adapterPosition, @NonNull RecyclerView.State state) {
        return mStart <= adapterPosition && adapterPosition < mEnd;
    }

}

package ru.tensor.sbis.design.list_utils.decoration.predicate.position;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.design.list_utils.decoration.predicate.PositionPredicate;

/**
 * Реализация выбора элементов декорирования с применением отступов от начала и от конца списка.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class OffsetPredicate extends PositionPredicate {

    /**
     * Начиная с какого элемента необходимо декорировать.
     */
    private final int mStartOffset;

    /**
     * Сколько элементов с конца не нужно декорировать
     */
    private final int mEndOffset;

    public OffsetPredicate(int startOffset, int endOffset) {
        mStartOffset = startOffset;
        mEndOffset = endOffset;
    }

    @Override
    protected boolean needToDecorateItem(int adapterPosition,
                                         @NonNull RecyclerView.State state) {
        return mStartOffset <= adapterPosition && adapterPosition < state.getItemCount() - mEndOffset;
    }

}

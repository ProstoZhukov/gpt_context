package ru.tensor.sbis.design.list_utils.decoration.predicate.position;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.design.list_utils.decoration.predicate.PositionPredicate;

/**
 * Реализация выбора указанного количества элементов с конца списка для декорирования.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class TailPredicate extends PositionPredicate {

    /**
     * Сколько элементов с конца декорировать.
     */
    private final int mTail;

    public TailPredicate(int tail) {
        mTail = tail;
    }

    @Override
    protected boolean needToDecorateItem(int adapterPosition, @NonNull RecyclerView.State state) {
        return adapterPosition >= state.getItemCount() - mTail;
    }

}

package ru.tensor.sbis.design.list_utils.decoration.predicate.composition;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.list_utils.decoration.Decoration;
import android.view.View;

/**
 * Вспомогательная реализация выбора элементов для декорирования,
 * применяющая логическую связку AND для указанных {@link Decoration.Predicate}.
 *
 * @author sa.nikitin
 */
public final class AndPredicate implements Decoration.Predicate {

    /**
     * Массив элементов списка, которые нужно декорировать.
     */
    @SuppressWarnings("CanBeFinal")
    public Decoration.Predicate[] mPredicates;

    public AndPredicate(Decoration.Predicate... predicates) {
        mPredicates = predicates;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean needToDecorate(@NonNull View itemView,
                                  @NonNull RecyclerView parent,
                                  @NonNull RecyclerView.State state) {
        for (Decoration.Predicate predicate : mPredicates) {
            if (!predicate.needToDecorate(itemView, parent, state)) {
                return false;
            }
        }
        return true;
    }

}

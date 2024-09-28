package ru.tensor.sbis.modalwindows.bottomsheet.check;

import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.modalwindows.bottomsheet.binding.UniversalBottomSheetOptionsAdapter;

/**
 * SelfDocumented
 * @author sr.golovkin
 */
public class CheckableOptionsAdapter<T extends CheckableBottomSheetOption> extends UniversalBottomSheetOptionsAdapter<T> {

    public CheckableOptionsAdapter(@NonNull List<T> options, @Nullable Listener<T> listener) {
        super(options, listener);
    }

    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case CheckableBottomSheetOption.VIEW_TYPE:
                return new CheckableOptionHolder(parent, this);
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }
}

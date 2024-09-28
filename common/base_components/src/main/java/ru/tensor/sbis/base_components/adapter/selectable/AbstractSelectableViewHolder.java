package ru.tensor.sbis.base_components.adapter.selectable;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** Legacy-код */
public class AbstractSelectableViewHolder
        extends RecyclerView.ViewHolder
        implements SelectableViewHolder {

    public AbstractSelectableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //region SelectableViewHolder interface implementation
    @Override
    public void updateSelectionState(boolean selected) {
        //ignore
        //override this method to implement selection logic
    }
    //endregion SelectableViewHolder interface implementation

}

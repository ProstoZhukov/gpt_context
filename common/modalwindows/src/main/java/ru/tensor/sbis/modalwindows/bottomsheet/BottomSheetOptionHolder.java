package ru.tensor.sbis.modalwindows.bottomsheet;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Холдер элемента из списка опций.
 *
 * @author sr.golovkin
 */
public class BottomSheetOptionHolder extends RecyclerView.ViewHolder {

    public BottomSheetOptionHolder(@NonNull View itemView, @Nullable OnOptionClickListener listener) {
        super(itemView);
        if (listener != null) {
            itemView.setOnClickListener(v -> listener.onOptionClick(getAdapterPosition()));
        }
    }

}

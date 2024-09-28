package ru.tensor.sbis.base_components.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import ru.tensor.sbis.base_components.R;

/**
 * Holder ячейки со ссылкой для открытия карточки в WebView ("Открыть на портале СБИС")
 *
 * @author ev.grigoreva
 */
@SuppressWarnings("rawtypes")
public class OpenOutsideViewHolder extends AbstractDataViewHolder {

    public OpenOutsideViewHolder(@NonNull View itemView, @NonNull ru.tensor.sbis.base_components.adapter.ItemClickListener listener) {
        super(itemView);
        itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
    }

    public OpenOutsideViewHolder(@NonNull ViewGroup parent, @NonNull ItemClickListener listener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.base_components_open_outside_item, parent, false), listener);
    }
}

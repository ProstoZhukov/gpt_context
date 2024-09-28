package ru.tensor.sbis.base_components.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.utils.KeyboardUtils;

/** SelfDocumented*/
public class HideKeyboardOnScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        KeyboardUtils.hideKeyboard(recyclerView);
    }
}

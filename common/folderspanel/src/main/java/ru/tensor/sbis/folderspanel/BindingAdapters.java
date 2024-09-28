package ru.tensor.sbis.folderspanel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

/** SelfDocumented*/
public class BindingAdapters {

    @BindingAdapter("swipeMenuItems")
    public static void setSwipeOpenStartListener(@NonNull SwipeMenuRecyclerView swipeMenuRecyclerView, @Nullable List<?> menuItems) {
        if (menuItems != null) {
            swipeMenuRecyclerView.addMenuItems(menuItems);
        }
    }
}
package ru.tensor.sbis.design.toolbar.behavior;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;

@SuppressWarnings("unused")
// Легаси код
public class NotDraggableAppBarBehavior extends AppBarLayout.Behavior {

    public NotDraggableAppBarBehavior() {
    }

    public NotDraggableAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }

}

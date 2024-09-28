package ru.tensor.sbis.base_components.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** SelfDocumented*/
public class ShowHideControlsScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 1;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    /** SelfDocumented*/
    protected void showOrHideControls(int dy, @NonNull OnShowHideListener listener) {
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            listener.onHideControls();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            listener.onShowControls();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    /** SelfDocumented*/
    public void reset(@NonNull OnShowHideListener listener) {
        controlsVisible = true;
        scrolledDistance = 0;
        listener.onShowControls();
    }

    /** SelfDocumented*/
    public interface OnShowHideListener {

        void onShowControls();

        void onHideControls();
    }

}

package ru.tensor.sbis.design.view_ext.swipereveallayout.updated;

import android.os.Bundle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ViewBinderHelper provides a quick and easy solution to restore the open/close state
 * of the items in RecyclerView, ListView, GridView or any view that requires its child view
 * to bind the view to a data object.
 * <p>
 * <p>When you bind you data object to a view, use {@link #bind(UpdatedSwipeRevealLayout, T)} to
 * save and restore the open/close state of the view.</p>
 * <p>
 * <p>Optionally, if you also want to save and restore the open/close state when the device's
 * orientation is changed, call {@link #saveStates(Bundle)} in {@link android.app.Activity#onSaveInstanceState(Bundle)}
 * and {@link #restoreStates(Bundle)} in {@link android.app.Activity#onRestoreInstanceState(Bundle)}</p>
 *
 * @param <T> describes the type of unique model id (UUID and String in most cases).
 * @deprecated необходимо использовать SwipeableViewBinderHelper из common
 */
@SuppressWarnings({"deprecation", "CanBeFinal", "FieldMayBeFinal", "unused", "Convert2Diamond", "unchecked", "JavadocReference", "DeprecatedIsStillUsed", "ConstantConditions"})
@Deprecated
public class UpdatedViewBinderHelper<T extends Serializable> {
    private static final String BUNDLE_MAP_KEY = "ViewBinderHelper_Bundle_Map_Key";

    private Map<T, Integer> mapStates = Collections.synchronizedMap(new HashMap<T, Integer>());
    private Map<T, UpdatedSwipeRevealLayout> mapLayouts = Collections.synchronizedMap(new HashMap<T, UpdatedSwipeRevealLayout>());
    private Set<T> lockedSwipeSet = Collections.synchronizedSet(new HashSet<T>());

    private volatile boolean openOnlyOne = false;
    private final Object stateChangeLock = new Object();

    /**
     * Help to save and restore open/close state of the swipeLayout. Call this method
     * when you bind your view holder with the data object.
     * (!!!) If you use data binding call this bind before executePendingBindings.
     *
     * @param swipeLayout swipeLayout of the current view.
     * @param uuid        a string that uniquely defines the data object of the current view.
     */
    public void bind(final UpdatedSwipeRevealLayout swipeLayout, final T uuid) {
        if (swipeLayout.shouldRequestLayout()) {
            swipeLayout.requestLayout();
        }

        mapLayouts.values().remove(swipeLayout);
        mapLayouts.put(uuid, swipeLayout);

        swipeLayout.abort();
        swipeLayout.setDragStateChangeListener(state -> {
            if (state != UpdatedSwipeRevealLayout.STATE_DRAGGING) {
                mapStates.put(uuid, state);
            }

            if (openOnlyOne) {
                closeOthers(uuid, swipeLayout);
            }
        });

        // first time binding.
        if (!mapStates.containsKey(uuid)) {
            mapStates.put(uuid, UpdatedSwipeRevealLayout.STATE_CLOSE);
            swipeLayout.close(false);
        }

        // not the first time, then close or open depends on the current state.
        else {
            int state = mapStates.get(uuid);

            if (state == UpdatedSwipeRevealLayout.STATE_CLOSE || state == UpdatedSwipeRevealLayout.STATE_CLOSING) {
                swipeLayout.close(false);
            } else {
                swipeLayout.open(false);
            }
        }

        // set lock swipe
        swipeLayout.setLockDrag(lockedSwipeSet.contains(uuid));
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        if (outState == null)
            return;
        outState.putSerializable(BUNDLE_MAP_KEY, new HashMap<>(mapStates));
    }


    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    @SuppressWarnings({"unchecked", "ConstantConditions", "RedundantSuppression"})
    public void restoreStates(Bundle inState) {
        if (inState == null)
            return;
        if (inState.containsKey(BUNDLE_MAP_KEY)) {
            mapStates = Collections.synchronizedMap((Map<T, Integer>) inState.getSerializable(BUNDLE_MAP_KEY));
        }
    }

    /**
     * Lock swipe for some layouts.
     *
     * @param uuids a string that uniquely defines the data object.
     */
    public void lockSwipe(T... uuids) {
        setLockSwipe(true, uuids);
    }

    /**
     * Unlock swipe for some layouts.
     *
     * @param uuids a string that uniquely defines the data object.
     */
    public void unlockSwipe(T... uuids) {
        setLockSwipe(false, uuids);
    }

    /**
     * @param openOnlyOne If set to true, then only one row can be opened at a time.
     */
    public void setOpenOnlyOne(boolean openOnlyOne) {
        this.openOnlyOne = openOnlyOne;
    }

    /**
     * Open a specific layout.
     *
     * @param uuid unique uuid which identifies the data object which is bind to the layout.
     */
    public void openLayout(final T uuid) {
        synchronized (stateChangeLock) {
            mapStates.put(uuid, UpdatedSwipeRevealLayout.STATE_OPEN);

            if (mapLayouts.containsKey(uuid)) {
                final UpdatedSwipeRevealLayout layout = mapLayouts.get(uuid);
                layout.open(true);
            } else if (openOnlyOne) {
                closeOthers(uuid, mapLayouts.get(uuid));
            }
        }
    }

    /**
     * Close a specific layout.
     *
     * @param uuid unique uuid which identifies the data object which is bind to the layout.
     */
    public void closeLayout(final T uuid) {
        synchronized (stateChangeLock) {
            mapStates.put(uuid, UpdatedSwipeRevealLayout.STATE_CLOSE);

            if (mapLayouts.containsKey(uuid)) {
                final UpdatedSwipeRevealLayout layout = mapLayouts.get(uuid);
                layout.close(true);
            }
        }
    }

    /**
     * Close others swipe layout.
     *
     * @param uuid        layout which bind with this data object uuid will be excluded.
     * @param swipeLayout will be excluded.
     */
    private void closeOthers(T uuid, UpdatedSwipeRevealLayout swipeLayout) {
        synchronized (stateChangeLock) {
            // close other rows if openOnlyOne is true.
            if (getOpenCount() > 1) {
                for (Map.Entry<T, Integer> entry : mapStates.entrySet()) {
                    if (entry.getKey() != uuid || !(entry.getKey() != null && entry.getKey().equals(uuid))) {
                        entry.setValue(UpdatedSwipeRevealLayout.STATE_CLOSE);
                    }
                }

                for (UpdatedSwipeRevealLayout layout : mapLayouts.values()) {
                    if (layout != swipeLayout) {
                        layout.close(true);
                    }
                }
            }
        }
    }

    public void closeAll() {
        synchronized (stateChangeLock) {
            // close other rows if openOnlyOne is true.
            if (getOpenCount() >= 1) {
                for (Map.Entry<T, Integer> entry : mapStates.entrySet()) {
                    entry.setValue(UpdatedSwipeRevealLayout.STATE_CLOSE);
                }

                for (UpdatedSwipeRevealLayout layout : mapLayouts.values()) {
                    layout.close(true);
                }
            }
        }
    }

    private void setLockSwipe(boolean lock, T... uuids) {
        if (uuids == null || uuids.length == 0)
            return;

        if (lock)
            lockedSwipeSet.addAll(Arrays.asList(uuids));
        else
            lockedSwipeSet.removeAll(Arrays.asList(uuids));

        for (T s : uuids) {
            UpdatedSwipeRevealLayout layout = mapLayouts.get(s);
            if (layout != null) {
                layout.setLockDrag(lock);
            }
        }
    }

    private int getOpenCount() {
        int total = 0;

        for (int state : mapStates.values()) {
            if (state == UpdatedSwipeRevealLayout.STATE_OPEN || state == UpdatedSwipeRevealLayout.STATE_OPENING) {
                total++;
            }
        }
        return total;
    }

}

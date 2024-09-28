package ru.tensor.sbis.common.util.scroll;

import android.app.Activity;

public enum ScrollEvent {
    /**
     * Real scroll events produced by user.
     */
    SCROLL_UP,
    SCROLL_DOWN,

    /**
     * Fake scroll event.
     * Used for cases when user returns back in main screen, app returns from
     * background and screen on (actually cover {@link Activity#onResume()}
     * lifecycle callback).
     */
    SCROLL_UP_FAKE_SOFT,

    /**
     * Fake scroll events.
     * Used for some actions in content fragments.
     */
    SCROLL_UP_FAKE,
    SCROLL_DOWN_FAKE
}

package ru.tensor.sbis.base_components;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import ru.tensor.sbis.verification_decl.auth.AuthAware;
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable;

/**
 * Активити для трэкинга активности пользователя на экране.
 * <p>
 * Created by vi.demidov on 30/09/16.
 */
public abstract class TrackingActivity extends BaseActivity
        implements UserActivityTrackable, AuthAware {
    /** SelfDocumented */
    public static final String TRACKING_ACTIVITY_ENABLED_INTENT_KEY = "TRACKING_ENABLED_ARG";

    private boolean isTrackingEnabled;

    // region UserActivityTrackable
    @Override
    public boolean isTrackActivityEnabled() {
        return isTrackingEnabled;
    }

    @NotNull
    @Override
    public String getScreenName() {
        return getClass().getName();
    }
    //end region

    // region AuthAware
    @NotNull
    @Override
    public CheckAuthStrategy getCheckAuthStrategy() {
        return CheckAuthStrategy.CheckWithForceJumpToLogin.INSTANCE;
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTrackingEnabled = isNeedTrack();
    }

    /** SelfDocumented */
    protected boolean isNeedTrack() {
        return getIntent().getBooleanExtra(TRACKING_ACTIVITY_ENABLED_INTENT_KEY, true);
    }

    /** SelfDocumented */
    @Deprecated
    protected boolean needToCheckVersion() {
        return true;
    }
}
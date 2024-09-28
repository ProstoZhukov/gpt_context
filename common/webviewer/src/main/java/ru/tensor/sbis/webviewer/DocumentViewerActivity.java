package ru.tensor.sbis.webviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import kotlin.Unit;
import ru.tensor.sbis.base_components.TrackingActivity;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design.swipeback.SwipeBackLayout;
import ru.tensor.sbis.design.theme.res.PlatformSbisString;
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent;
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView;
import ru.tensor.sbis.design.utils.ThemeUtil;
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard;
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard.LegacyEntryPoint;
import ru.tensor.sbis.version_checker_decl.VersionedComponent;

/**
 * Активити для открытия документа через WebView
 *
 * @author ma.kolpakov
 */
@SuppressWarnings("rawtypes")
public class DocumentViewerActivity extends TrackingActivity
        implements DocumentViewerFragment.DocumentViewerHolder, VersionedComponent, LegacyEntryPoint {
    /** SelfDocumented */
    public static final String EXTRA_DOCUMENT_TITLE = "EXTRA_DOCUMENT_TITLE";
    /** SelfDocumented */
    public static final String EXTRA_DOCUMENT_URL = "EXTRA_DOCUMENT_URL";
    /** SelfDocumented */
    public static final String EXTRA_DOCUMENT_ID = "EXTRA_DOCUMENT_ID";
    /** SelfDocumented */
    public static final String EXTRA_HIDE_TOOLBAR = "EXTRA_USE_TOOLBAR";
    /** SelfDocumented */
    public static final int NOT_FOUND_ATTR_RESULT = -1;

    private SbisTopNavigationView mSbisToolbar;
    private DocumentViewerFragment mFragment;
    private boolean mExitOnBack;
    @Nullable
    private String mCustomTitle;

    @Override
    protected void attachBaseContext(@Nullable Context base) {
        EntryPointGuard.INSTANCE.getActivityAssistant().interceptAttachBaseContextLegacy(this, base, context -> {
            DocumentViewerActivity.super.attachBaseContext(context);
            return Unit.INSTANCE;
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtil.obtainThemeAttrsAndMerge(
                this,
                R.attr.webviewerTheme, ru.tensor.sbis.design.R.style.AppTheme_Swipe_Back
        );
        super.onCreate(savedInstanceState);
        overridePendingTransition(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.right_out);

        setContentView(R.layout.docwebviewer_activity);

        DocumentViewerFragment existedFragment = (DocumentViewerFragment) getSupportFragmentManager().findFragmentById(getContainerId());
        if (existedFragment != null) {
            mFragment = existedFragment;
        } else {
            mFragment = new DocumentViewerFragment();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getContainerId(), mFragment)
                    .commitNow();
        }

        final String documentUrl = getIntent().getStringExtra(EXTRA_DOCUMENT_URL);
        mCustomTitle = getIntent().getStringExtra(EXTRA_DOCUMENT_TITLE);
        final String documentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);
        mFragment.setUrl(Objects.requireNonNull(documentUrl), documentId, true);
        final boolean hideToolbar = getIntent().getBooleanExtra(EXTRA_HIDE_TOOLBAR, false);

        initToolbar(mCustomTitle, hideToolbar);
        setDragDirectMode(SwipeBackLayout.DragDirectMode.EDGE);
    }

    @Override
    protected void onDestroy() {
        mFragment = null;
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mFragment.onBackPressed() || mExitOnBack) {
            super.onBackPressed();
        } else {
            showToast(R.string.webviewer_tap_again_to_exit_to_list);
            mExitOnBack = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mFragment != null) {
            mFragment.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    protected boolean swipeBackEnabled() {
        return true;
    }

    private void initToolbar(String title, boolean hideToolbar) {
        mSbisToolbar = findViewById(R.id.docwebviewer_sbisToolbar);
        if (hideToolbar) {
            mSbisToolbar.setVisibility(View.GONE);
        } else {
            mSbisToolbar.setContent(new SbisTopNavigationContent.SmallTitle(
                    new PlatformSbisString.Value(title),
                    null,
                    null,
                    null,
                    null,
                    () -> Unit.INSTANCE));
            mSbisToolbar.setShowBackButton(true);
            SbisTextView backBtn = mSbisToolbar.getBackBtn();
            if (backBtn != null) {
                backBtn.setOnClickListener(view -> finish());
            }
        }
    }

    private int getContainerId() {
        return R.id.docwebviewer_layout_content;
    }

    @Override
    public void onPageTitleLoaded(@Nullable String title) {
        if (mSbisToolbar != null && CommonUtils.isEmpty(mCustomTitle) && title != null) {
            mSbisToolbar.setContent(new SbisTopNavigationContent.SmallTitle(
                    new PlatformSbisString.Value(title),
                    null,
                    null,
                    null,
                    null,
                    () -> Unit.INSTANCE));
        }
    }

    @NonNull
    @Override
    public Strategy getVersioningStrategy() {
        // экраны используется для открытия UpdateSource.SBIS_ONLINE
        return Strategy.SKIP;
    }
}

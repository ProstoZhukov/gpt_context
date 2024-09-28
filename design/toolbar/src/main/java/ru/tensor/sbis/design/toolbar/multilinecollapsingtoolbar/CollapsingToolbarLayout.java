package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.coordinatorlayout.widget.ViewGroupUtils.getDescendantRect;
import static ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayoutKt.TAG_GRADIENT;
import static ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationUtils.ACCELERATE_INTERPOLATOR;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.math.MathUtils;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ru.tensor.sbis.design.toolbar.R;
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout;
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext.CollapsingTextAnimationController;
import ru.tensor.sbis.design.toolbar.util.collapsingimage.CollapsingImageStateListener;

/**
 * Кастомный  CollapsingToolbarLayout с расширением multiline title, а также возможностью установки надзаголовка.
 * За основу взят код из https://github.com/opacapp/multiline-collapsingtoolbar
 * Библиотека намеренно не подключена, а скопирована, т.к. её дальнейшее поддержание автором не планируется.
 * Ожидается слияние с Material Components Android library.
 * PR: https://github.com/material-components/material-components-android/pull/413
 *
 * @author us.bessonov
 */
@SuppressWarnings("unused")
public class CollapsingToolbarLayout extends FrameLayout {

    private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;

    private boolean mRefreshToolbar = true;
    @SuppressWarnings("FieldMayBeFinal")
    private int mToolbarId;
    private CollapsingLayoutChildToolbar mToolbar;
    private View mToolbarDirectChild;
    private View mDummyView;

    private int mExpandedMarginStart;
    private int mExpandedMarginTop;
    private int mExpandedMarginEnd;
    private int mExpandedMarginBottom;

    private final Rect mTmpRect = new Rect();
    private final CollapsingTextAnimationController mTextAnimationController;
    private boolean mCollapsingTitleEnabled;
    private boolean mDrawCollapsingTitle;

    private Drawable mContentScrim;
    Drawable mStatusBarScrim;
    private int mScrimAlpha;
    private boolean mScrimsAreShown;
    private ValueAnimator mScrimAnimator;
    private long mScrimAnimationDuration;
    @SuppressWarnings("UnusedAssignment")
    private int mScrimVisibleHeightTrigger = -1;

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    int mCurrentOffset;

    WindowInsetsCompat mLastInsets;

    private boolean mIsTitleWithBackground = false;
    private int mUnscaledTitleHeight;
    private int mDefaultMinHeight;

    private boolean mIsTopInsetIncludedInMinHeight = true;

    public CollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("PrivateResource")
    public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ThemeUtils.checkAppCompatTheme(context);

        mTextAnimationController = new CollapsingTextAnimationController(this);

        // BEGIN MODIFICATION: use own default style
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.google.android.material.R.styleable.CollapsingToolbarLayout, defStyleAttr,
                R.style.Widget_Design_MultilineCollapsingToolbar);
        // END MODIFICATION

        int collapsedTextGravity = a.getInt(
                com.google.android.material.R.styleable.CollapsingToolbarLayout_collapsedTitleGravity,
                GravityCompat.START | Gravity.CENTER_VERTICAL);
        mTextAnimationController.setCollapsedTitleCenterHorizontal(
                (collapsedTextGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL);
        mTextAnimationController.setCollapsedTitleCenterVertical(
                (collapsedTextGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL);

        mExpandedMarginStart = mExpandedMarginTop = mExpandedMarginEnd = mExpandedMarginBottom =
                a.getDimensionPixelSize(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);

        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
            mExpandedMarginStart = a.getDimensionPixelSize(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
        }
        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
            mExpandedMarginEnd = a.getDimensionPixelSize(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
        }
        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
            mExpandedMarginTop = a.getDimensionPixelSize(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
        }
        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
            mExpandedMarginBottom = a.getDimensionPixelSize(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
        }

        mTextAnimationController.setRightSubtitleMargin(getContext().getResources()
                .getDimensionPixelSize(R.dimen.toolbar_right_subtitle_margin));

        mCollapsingTitleEnabled = a.getBoolean(
                com.google.android.material.R.styleable.CollapsingToolbarLayout_titleEnabled, true);
        setTitle(a.getText(com.google.android.material.R.styleable.CollapsingToolbarLayout_title));

        // First load the default text appearances
        mTextAnimationController.setExpandedTitleAppearance(
                com.google.android.material.R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
        // BEGIN MODIFICATION: use own default style
        mTextAnimationController.setCollapsedTitleAppearance(
                R.style.ActionBar_Title);
        // END MODIFICATION

        // Now overlay any custom text appearances
        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
            mTextAnimationController.setExpandedTitleAppearance(
                    a.getResourceId(
                            com.google.android.material.R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
        }
        if (a.hasValue(com.google.android.material.R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
            mTextAnimationController.setCollapsedTitleAppearance(
                    a.getResourceId(
                            com.google.android.material.R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
        }

        mScrimVisibleHeightTrigger = a.getDimensionPixelSize(
                com.google.android.material.R.styleable.CollapsingToolbarLayout_scrimVisibleHeightTrigger, -1);

        mScrimAnimationDuration = a.getInt(
                com.google.android.material.R.styleable.CollapsingToolbarLayout_scrimAnimationDuration,
                DEFAULT_SCRIM_ANIMATION_DURATION);

        setContentScrim(a.getDrawable(com.google.android.material.R.styleable.CollapsingToolbarLayout_contentScrim));
        setStatusBarScrim(a.getDrawable(com.google.android.material.R.styleable.CollapsingToolbarLayout_statusBarScrim));

        mToolbarId = a.getResourceId(com.google.android.material.R.styleable.CollapsingToolbarLayout_toolbarId, -1);

        a.recycle();

        setWillNotDraw(false);

        ViewCompat.setOnApplyWindowInsetsListener(this,
                (v, insets) -> onWindowInsetChanged(insets));

        // BEGIN MODIFICATION: set the value of maxNumberOfLines attribute to the mCollapsingTextHelper
        @SuppressLint("CustomViewStyleable") TypedArray typedArray =
                context.obtainStyledAttributes(attrs, ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension, defStyleAttr, 0);

        // First load the default subtitle appearances
        mTextAnimationController.setSubtitleAppearance(
                R.style.SbisAppBar_CollapsingLayout_SubtitleText_Light);
        mTextAnimationController.setRightSubtitleAppearance(
                R.style.SbisAppBarCollapsingLayoutRightSubtitleText);

        // Now overlay any custom subtitle appearances
        if (typedArray.hasValue(ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_subtitleTextAppearance)) {
            mTextAnimationController.setSubtitleAppearance(typedArray.getResourceId(
                    ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_subtitleTextAppearance, 0));
        }
        if (typedArray.hasValue(ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_rightSubtitleTextAppearance)) {
            mTextAnimationController.setRightSubtitleAppearance(typedArray.getResourceId(
                    ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_rightSubtitleTextAppearance, 0));
        }

        setRightSubtitle(typedArray.getString(ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_rightSubtitle));

        mIsTopInsetIncludedInMinHeight = typedArray.getBoolean(
                ru.tensor.sbis.design.R.styleable.CollapsingToolbarLayoutExtension_isTopInsetIncludedInMinHeight,
                mIsTopInsetIncludedInMinHeight
        );

        typedArray.recycle();
        // END MODIFICATION

        int[] attributes = new int[]{com.google.android.material.R.attr.subtitle};
        typedArray = context.getTheme().obtainStyledAttributes(attrs, attributes, defStyleAttr, 0);
        setSubtitle(typedArray.getString(0));
        typedArray.recycle();
    }

    /**
     * Задаёт максимальное число строк у заголовка.
     * По умолчанию - 3.
     */
    public void setMaxLines(int maxLines) {
        mTextAnimationController.setMaxTitleLines(maxLines);
    }

    public void setTitleWithBackground() {
        mIsTitleWithBackground = true;
    }

    public int getFullTitleHeight() {
        if (TextUtils.isEmpty(getTitle())) {
            return 0;
        }
        if (mUnscaledTitleHeight == 0) {
            updateUnscaledTitleHeight();
        }
        return mUnscaledTitleHeight + mExpandedMarginBottom + mExpandedMarginTop;
    }

    /**
     * Возвращает предполагаемую высоту заголовка в развёрнутом состоянии.
     * До вызова метода должны быть заданы заголовок и требуемые горизонтальные отступы.
     */
    @Px
    public int getEstimatedFullTitleHeight() {
        if (TextUtils.isEmpty(getTitle())) {
            return 0;
        }
        return mTextAnimationController.getEstimatedTitleHeightUnscaled(mExpandedMarginStart,
                mExpandedMarginEnd);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Add an OnOffsetChangedListener if possible
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            // Copy over from the ABL whether we should fit system windows
            ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View) parent));

            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
            if (parent instanceof SbisAppBarLayout) {
                mTextAnimationController.initAppBar((SbisAppBarLayout) parent);
            } else {
                mTextAnimationController.initAppBar(((AppBarLayout) parent));
            }

            // We're attached, so lets request an inset dispatch
            ViewCompat.requestApplyInsets(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        super.onDetachedFromWindow();
    }

    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;

        if (ViewCompat.getFitsSystemWindows(this)) {
            // If we're set to fit system windows, keep the insets
            newInsets = insets;
        }

        // If our insets have changed, keep them and invalidate the scroll ranges...
        if (!ObjectsCompat.equals(mLastInsets, newInsets)) {
            mLastInsets = newInsets;
            requestLayout();
        }

        // Consume the insets. This is done so that child views with fitSystemWindows=true do not
        // get the default padding functionality from View
        return WindowInsetsCompat.CONSUMED;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // If we don't have a toolbar, the scrim will be not be drawn in drawChild() below.
        // Instead, we draw it here, before our collapsing text.
        ensureToolbar();
        if (mToolbar == null && mContentScrim != null && mScrimAlpha > 0) {
            mContentScrim.mutate().setAlpha(mScrimAlpha);
            mContentScrim.draw(canvas);
        }

        // Let the collapsing text helper draw its text
        if (mCollapsingTitleEnabled && mDrawCollapsingTitle) {
            mTextAnimationController.draw(canvas);
        }

        // Now draw the status bar scrim
        if (mStatusBarScrim != null && mScrimAlpha > 0) {
            final int topInset = mLastInsets != null ?
                    mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top : 0;
            if (topInset > 0) {
                mStatusBarScrim.setBounds(0, -mCurrentOffset, getWidth(),
                        topInset - mCurrentOffset);
                mStatusBarScrim.mutate().setAlpha(mScrimAlpha);
                mStatusBarScrim.draw(canvas);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // This is a little weird. Our scrim needs to be behind the Toolbar (if it is present),
        // but in front of any other children which are behind it. To do this we intercept the
        // drawChild() call, and draw our scrim just before the Toolbar is drawn
        boolean invalidated = false;
        if (mContentScrim != null && mScrimAlpha > 0 && isToolbarChild(child)) {
            mContentScrim.mutate().setAlpha(mScrimAlpha);
            mContentScrim.draw(canvas);
            invalidated = true;
        }
        return super.drawChild(canvas, child, drawingTime) || invalidated;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mContentScrim != null) {
            mContentScrim.setBounds(0, 0, w, h);
        }
    }

    private void ensureToolbar() {
        if (!mRefreshToolbar) {
            return;
        }

        // First clear out the current Toolbar
        mToolbar = null;
        mToolbarDirectChild = null;

        if (mToolbarId != -1) {
            // If we have an ID set, try and find it and it's direct parent to us
            View toolbar = findViewById(mToolbarId);
            mToolbar = recognizeToolbar(toolbar);

            if (mToolbar != null) {
                mToolbarDirectChild = findDirectChild(mToolbar.getView());
            }
        }

        if (mToolbar == null) {
            // If we don't have an ID, or couldn't find a Toolbar with the correct ID, try and find
            // one from our direct children
            CollapsingLayoutChildToolbar toolbar;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                final View child = getChildAt(i);
                toolbar = recognizeToolbar(child);
                if (toolbar != null) {
                    mToolbar = toolbar;
                    break;
                }
            }
        }

        updateDummyView();
        mRefreshToolbar = false;
    }

    @Nullable
    private CollapsingLayoutChildToolbar recognizeToolbar(@Nullable View view) {
        if (view instanceof ru.tensor.sbis.design.toolbar.Toolbar) {
            return new SbisCollapsingLayoutChildToolbar((ru.tensor.sbis.design.toolbar.Toolbar) view);
        }
        if (view instanceof Toolbar) {
            return new DefaultCollapsingLayoutChildToolbar((Toolbar) view);
        }
        return null;
    }

    private boolean isToolbarChild(View child) {
        return (mToolbarDirectChild == null || mToolbarDirectChild == this)
                ? child == mToolbar
                : child == mToolbarDirectChild;
    }

    /**
     * Returns the direct child of this layout, which itself is the ancestor of the
     * given view.
     */
    private View findDirectChild(final View descendant) {
        View directChild = descendant;
        for (ViewParent p = descendant.getParent(); p != this && p != null; p = p.getParent()) {
            if (p instanceof View) {
                directChild = (View) p;
            }
        }
        return directChild;
    }

    private void updateDummyView() {
        if (!mCollapsingTitleEnabled && mDummyView != null) {
            // If we have a dummy view and we have our title disabled, remove it from its parent
            final ViewParent parent = mDummyView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mDummyView);
            }
        }
        if (mCollapsingTitleEnabled && mToolbar != null) {
            if (mDummyView == null) {
                mDummyView = new View(getContext());
            }
            if (mDummyView.getParent() == null) {
                mToolbar.addDummyView(mDummyView);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureToolbar();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int mode = MeasureSpec.getMode(heightMeasureSpec);
        final int topInset = mLastInsets != null ?
                mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top : 0;
        if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
            // If we have a top inset and we're set to wrap_content height we need to make sure
            // we add the top inset to our height, therefore we re-measure
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() + topInset, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mLastInsets != null) {
            // Shift down any views which are not set to fit system windows
            final int insetTop = mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                if (!ViewCompat.getFitsSystemWindows(child)) {
                    if (child.getTop() < insetTop) {
                        // If the child isn't set to fit system windows but is drawing within
                        // the inset offset it down
                        ViewCompat.offsetTopAndBottom(child, insetTop);
                    }
                }
            }
        }

        // Update the collapsed bounds by getting it's transformed bounds
        if (mCollapsingTitleEnabled && mDummyView != null) {
            // We only draw the title if the dummy view is being displayed (Toolbar removes
            // views if there is no space)
            mDrawCollapsingTitle = ViewCompat.isAttachedToWindow(mDummyView)
                    && mDummyView.getVisibility() == VISIBLE;

            if (mDrawCollapsingTitle) {
                final boolean isRtl = ViewCompat.getLayoutDirection(this)
                        == ViewCompat.LAYOUT_DIRECTION_RTL;

                final View pinChild = mToolbarDirectChild != null ?
                        mToolbarDirectChild : mToolbar.getView();

                // Update the collapsed bounds
                final int maxOffset = getMaxOffsetForPinChild(pinChild, pinChild.getTop());
                getDescendantRect(this, mDummyView, mTmpRect);
                mTextAnimationController.setCollapsedBounds(
                        mTmpRect.left + (isRtl
                                ? mToolbar.getTitleMarginEnd()
                                : mToolbar.getTitleMarginStart()),
                        mTmpRect.top + maxOffset + mToolbar.getTitleMarginTop(),
                        mTmpRect.right + (isRtl
                                ? mToolbar.getTitleMarginStart()
                                : mToolbar.getTitleMarginEnd()),
                        mTmpRect.bottom + maxOffset - mToolbar.getTitleMarginBottom());

                // Update the expanded bounds
                updateExpandedBounds(right, left, bottom, top);
                if (mToolbar.getCustomTitleTextSize() != null) {
                    mTextAnimationController.setCollapsedTitleSize(mToolbar.getCustomTitleTextSize());
                }
                // Now recalculate using the new bounds
                mTextAnimationController.recalculate();
            }
        }

        // Update our child view offset helpers. This needs to be done after the title has been
        // setup, so that any Toolbars are in their original position
        for (int i = 0, z = getChildCount(); i < z; i++) {
            getViewOffsetHelper(getChildAt(i)).onViewLayout();
        }

        // Finally, set our minimum height to enable proper AppBarLayout collapsing
        if (mToolbar != null) {
            if (mCollapsingTitleEnabled && TextUtils.isEmpty(mTextAnimationController.getTitle())) {
                // If we do not currently have a title, try and grab it from the Toolbar
                setTitle(mToolbar.getTitle());
            }
            int minHeight;
            if (mToolbarDirectChild == null || mToolbarDirectChild == this) {
                minHeight = getHeightWithMargins(mToolbar.getView());
            } else {
                minHeight = getHeightWithMargins(mToolbarDirectChild);
            }
            if (mIsTopInsetIncludedInMinHeight) {
                setMinimumHeight(minHeight + getTopInset());
            } else {
                setMinimumHeight(minHeight);
            }
        }

        mDefaultMinHeight = ViewCompat.getMinimumHeight(this);

        updateScrimVisibility();
    }

    private int getTopInset() {
        if (mLastInsets == null) {
            return 0;
        }
        return mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
    }

    private void updateExpandedBounds(int right, int left, int bottom, int top) {
        final boolean isRtl = ViewCompat.getLayoutDirection(this)
                == ViewCompat.LAYOUT_DIRECTION_RTL;
        mTextAnimationController.setExpandedBounds(
                isRtl ? mExpandedMarginEnd : mExpandedMarginStart,
                mTmpRect.top + mExpandedMarginTop,
                right - left - (isRtl ? mExpandedMarginStart : mExpandedMarginEnd),
                bottom - top - mExpandedMarginBottom);
    }

    private static int getHeightWithMargins(@NonNull final View view) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            final MarginLayoutParams mlp = (MarginLayoutParams) lp;
            return view.getHeight() + mlp.topMargin + mlp.bottomMargin;
        }
        return view.getHeight();
    }

    static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(com.google.android.material.R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(com.google.android.material.R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    /**
     * Sets the title to be displayed by this view, if enabled.
     *
     * @see #setTitleEnabled(boolean)
     * @see #getTitle()
     */
    public void setTitle(@Nullable CharSequence title) {
        mTextAnimationController.setTitle(title);
        updateUnscaledTitleHeight();
        requestLayout();
    }

    /**
     * Returns the title currently being displayed by this view. If the title is not enabled, then
     * this will return {@code null}.
     */
    @Nullable
    public CharSequence getTitle() {
        return mCollapsingTitleEnabled ? mTextAnimationController.getTitle() : null;
    }

    /**
     * Задаёт подзаголовок, отображаемый над заголовком
     *
     * @param subtitle текст надзаголовка
     */
    public void setSubtitle(@Nullable CharSequence subtitle) {
        mTextAnimationController.setSubtitle(subtitle);
    }

    /**
     * Возвращает текст подзаголовка
     */
    @Nullable
    public CharSequence getSubtitle() {
        return mTextAnimationController.getSubtitle();
    }

    /**
     * Задаёт текст, отображаемый справа от основного надзаголовка
     *
     * @param rightSubtitle текст правого надзаголовка (комментария)
     */
    public void setRightSubtitle(@Nullable CharSequence rightSubtitle) {
        mTextAnimationController.setRightSubtitle(rightSubtitle);
    }

    /**
     * Sets whether this view should display its own title.
     *
     * <p>The title displayed by this view will shrink and grow based on the scroll offset.</p>
     *
     * @see #setTitle(CharSequence)
     * @see #isTitleEnabled()
     */
    public void setTitleEnabled(boolean enabled) {
        if (enabled != mCollapsingTitleEnabled) {
            mCollapsingTitleEnabled = enabled;
            updateDummyView();
            requestLayout();
        }
    }

    /**
     * Returns whether this view is currently displaying its own title.
     *
     * @see #setTitleEnabled(boolean)
     */
    public boolean isTitleEnabled() {
        return mCollapsingTitleEnabled;
    }

    /**
     * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
     * in the vertical scroll may overwrite this value. Any visibility change will be animated if
     * this view has already been laid out.
     *
     * @param shown whether the scrims should be shown
     * @see #getStatusBarScrim()
     * @see #getContentScrim()
     */
    public void setScrimsShown(boolean shown) {
        setScrimsShown(shown, ViewCompat.isLaidOut(this) && !isInEditMode());
    }

    /**
     * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
     * in the vertical scroll may overwrite this value.
     *
     * @param shown   whether the scrims should be shown
     * @param animate whether to animate the visibility change
     * @see #getStatusBarScrim()
     * @see #getContentScrim()
     */
    public void setScrimsShown(boolean shown, boolean animate) {
        if (mScrimsAreShown != shown) {
            if (animate) {
                animateScrim(shown ? 0xFF : 0x0);
            } else {
                setScrimAlpha(shown ? 0xFF : 0x0);
            }
            mScrimsAreShown = shown;
        }
    }

    @Px
    public int getDefaultMinimumHeight() {
        return mDefaultMinHeight;
    }

    @SuppressWarnings("Convert2Lambda")
    private void animateScrim(int targetAlpha) {
        ensureToolbar();
        if (mScrimAnimator == null) {
            mScrimAnimator = new ValueAnimator();
            mScrimAnimator.setDuration(mScrimAnimationDuration);
            mScrimAnimator.setInterpolator(
                    targetAlpha > mScrimAlpha
                            ? AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
                            : AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            mScrimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@Nullable ValueAnimator animator) {
                    setScrimAlpha((int) animator.getAnimatedValue());
                }
            });
        } else if (mScrimAnimator.isRunning()) {
            mScrimAnimator.cancel();
        }

        mScrimAnimator.setIntValues(mScrimAlpha, targetAlpha);
        mScrimAnimator.start();
    }

    void setScrimAlpha(int alpha) {
        if (alpha != mScrimAlpha) {
            final Drawable contentScrim = mContentScrim;
            if (contentScrim != null && mToolbar != null) {
                ViewCompat.postInvalidateOnAnimation(mToolbar.getView());
            }
            mScrimAlpha = alpha;
            ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
        }
    }

    int getScrimAlpha() {
        return mScrimAlpha;
    }

    /**
     * Set the drawable to use for the content scrim from resources. Providing null will disable
     * the scrim functionality.
     *
     * @param drawable the drawable to display
     * @see #getContentScrim()
     */
    public void setContentScrim(@Nullable Drawable drawable) {
        if (mContentScrim != drawable) {
            if (mContentScrim != null) {
                mContentScrim.setCallback(null);
            }
            mContentScrim = drawable != null ? drawable.mutate() : null;
            if (mContentScrim != null) {
                mContentScrim.setBounds(0, 0, getWidth(), getHeight());
                mContentScrim.setCallback(this);
                mContentScrim.setAlpha(mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Set the color to use for the content scrim.
     *
     * @param color the color to display
     * @see #getContentScrim()
     */
    public void setContentScrimColor(@ColorInt int color) {
        setContentScrim(new ColorDrawable(color));
    }

    /**
     * Set the drawable to use for the content scrim from resources.
     *
     * @param resId drawable resource id
     * @see #getContentScrim()
     */
    public void setContentScrimResource(@DrawableRes int resId) {
        setContentScrim(ContextCompat.getDrawable(getContext(), resId));

    }

    /**
     * Returns the drawable which is used for the foreground scrim.
     *
     * @see #setContentScrim(Drawable)
     */
    @Nullable
    public Drawable getContentScrim() {
        return mContentScrim;
    }

    /**
     * Set the drawable to use for the status bar scrim from resources.
     * Providing null will disable the scrim functionality.
     *
     * <p>This scrim is only shown when we have been given a top system inset.</p>
     *
     * @param drawable the drawable to display
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrim(@Nullable Drawable drawable) {
        if (mStatusBarScrim != drawable) {
            if (mStatusBarScrim != null) {
                mStatusBarScrim.setCallback(null);
            }
            mStatusBarScrim = drawable != null ? drawable.mutate() : null;
            if (mStatusBarScrim != null) {
                if (mStatusBarScrim.isStateful()) {
                    mStatusBarScrim.setState(getDrawableState());
                }
                DrawableCompat.setLayoutDirection(mStatusBarScrim,
                        ViewCompat.getLayoutDirection(this));
                mStatusBarScrim.setVisible(getVisibility() == VISIBLE, false);
                mStatusBarScrim.setCallback(this);
                mStatusBarScrim.setAlpha(mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        final int[] state = getDrawableState();
        boolean changed = false;

        Drawable d = mStatusBarScrim;
        if (d != null && d.isStateful()) {
            changed |= d.setState(state);
        }
        d = mContentScrim;
        if (d != null && d.isStateful()) {
            changed |= d.setState(state);
        }

        if (changed) {
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(@Nullable Drawable who) {
        return super.verifyDrawable(who) || who == mContentScrim || who == mStatusBarScrim;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        final boolean visible = visibility == VISIBLE;
        if (mStatusBarScrim != null && mStatusBarScrim.isVisible() != visible) {
            mStatusBarScrim.setVisible(visible, false);
        }
        if (mContentScrim != null && mContentScrim.isVisible() != visible) {
            mContentScrim.setVisible(visible, false);
        }
    }

    /**
     * Set the color to use for the status bar scrim.
     *
     * <p>This scrim is only shown when we have been given a top system inset.</p>
     *
     * @param color the color to display
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrimColor(@ColorInt int color) {
        setStatusBarScrim(new ColorDrawable(color));
    }

    /**
     * Set the drawable to use for the content scrim from resources.
     *
     * @param resId drawable resource id
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrimResource(@DrawableRes int resId) {
        setStatusBarScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * Returns the drawable which is used for the status bar scrim.
     *
     * @see #setStatusBarScrim(Drawable)
     */
    @Nullable
    public Drawable getStatusBarScrim() {
        return mStatusBarScrim;
    }

    /**
     * Sets the text color and size for the collapsed title from the specified
     * TextAppearance resource.
     */
    public void setCollapsedTitleTextAppearance(@StyleRes int resId) {
        mTextAnimationController.setCollapsedTitleAppearance(resId);
    }

    /**
     * Задаёт стиль текста основного надзаголовка
     *
     * @param resId стиль текста основного надзаголовка
     */
    public void setSubtitleTextAppearance(@StyleRes int resId) {
        mTextAnimationController.setSubtitleAppearance(resId);
    }

    /**
     * Задаёт стиль текста справа от основного надзаголовка
     *
     * @param resId стиль текста правого надзаголовка (комментария)
     */
    public void setRightSubtitleTextAppearance(@StyleRes int resId) {
        mTextAnimationController.setRightSubtitleAppearance(resId);
    }

    /**
     * Sets the text color and size for the expanded title from the specified
     * TextAppearance resource.
     */
    public void setExpandedTitleTextAppearance(@StyleRes int resId) {
        mTextAnimationController.setExpandedTitleAppearance(resId);
    }

    /**
     * Sets the text color of the collapsed title.
     *
     * @param color The new text color in ARGB format
     */
    public void setCollapsedTitleTextColor(@ColorInt int color) {
        mTextAnimationController.setCollapsedTitleColor(color);
    }

    /**
     * Sets the text color of the expanded title.
     *
     * @param color The new text color in ARGB format
     */
    public void setExpandedTitleColor(@ColorInt int color) {
        mTextAnimationController.setExpandedTitleColor(color);
    }

    /**
     * Sets the expanded title margins.
     *
     * @param start  the starting title margin in pixels
     * @param top    the top title margin in pixels
     * @param end    the ending title margin in pixels
     * @param bottom the bottom title margin in pixels
     * @see #getExpandedTitleMarginStart()
     * @see #getExpandedTitleMarginTop()
     * @see #getExpandedTitleMarginEnd()
     * @see #getExpandedTitleMarginBottom()
     */
    public void setExpandedTitleMargin(int start, int top, int end, int bottom) {
        mExpandedMarginStart = start;
        mExpandedMarginTop = top;
        mExpandedMarginEnd = end;
        mExpandedMarginBottom = bottom;
        requestLayout();
    }

    /**
     * @return the starting expanded title margin in pixels
     * @see #setExpandedTitleMarginStart(int)
     */
    public int getExpandedTitleMarginStart() {
        return mExpandedMarginStart;
    }

    /**
     * Sets the starting expanded title margin in pixels.
     *
     * @param margin the starting title margin in pixels
     * @see #getExpandedTitleMarginStart()
     */
    public void setExpandedTitleMarginStart(int margin) {
        mExpandedMarginStart = margin;
        if (isLaidOut()) {
            updateExpandedBounds(getRight(), getLeft(), getBottom(), getTop());
        }
        requestLayout();
    }

    /**
     * @return the top expanded title margin in pixels
     * @see #setExpandedTitleMarginTop(int)
     */
    public int getExpandedTitleMarginTop() {
        return mExpandedMarginTop;
    }

    /**
     * Sets the top expanded title margin in pixels.
     *
     * @param margin the top title margin in pixels
     * @see #getExpandedTitleMarginTop()
     */
    public void setExpandedTitleMarginTop(int margin) {
        mExpandedMarginTop = margin;
        requestLayout();
    }

    /**
     * @return the ending expanded title margin in pixels
     * @see #setExpandedTitleMarginEnd(int)
     */
    public int getExpandedTitleMarginEnd() {
        return mExpandedMarginEnd;
    }

    /**
     * Sets the ending expanded title margin in pixels.
     *
     * @param margin the ending title margin in pixels
     * @see #getExpandedTitleMarginEnd()
     */
    public void setExpandedTitleMarginEnd(int margin) {
        mExpandedMarginEnd = margin;
        requestLayout();
    }

    /**
     * @return the bottom expanded title margin in pixels
     * @see #setExpandedTitleMarginBottom(int)
     */
    public int getExpandedTitleMarginBottom() {
        return mExpandedMarginBottom;
    }

    /**
     * Sets the bottom expanded title margin in pixels.
     *
     * @param margin the bottom title margin in pixels
     * @see #getExpandedTitleMarginBottom()
     */
    public void setExpandedTitleMarginBottom(int margin) {
        mExpandedMarginBottom = margin;
        requestLayout();
    }

    /**
     * Set the amount of visible height in pixels used to define when to trigger a scrim
     * visibility change.
     *
     * <p>If the visible height of this view is less than the given value, the scrims will be
     * made visible, otherwise they are hidden.</p>
     *
     * @param height value in pixels used to define when to trigger a scrim visibility change
     */
    public void setScrimVisibleHeightTrigger(@IntRange(from = 0) final int height) {
        if (mScrimVisibleHeightTrigger != height) {
            mScrimVisibleHeightTrigger = height;
            // Update the scrim visibility
            updateScrimVisibility();
        }
    }

    /**
     * Returns the amount of visible height in pixels used to define when to trigger a scrim
     * visibility change.
     *
     * @see #setScrimVisibleHeightTrigger(int)
     */
    public int getScrimVisibleHeightTrigger() {
        if (mScrimVisibleHeightTrigger >= 0) {
            // If we have one explicitly set, return it
            return mScrimVisibleHeightTrigger;
        }

        // Otherwise we'll use the default computed value
        final int insetTop = mLastInsets != null ?
                mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top : 0;

        final int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight > 0) {
            // If we have a minHeight set, lets use 2 * minHeight (capped at our height)
            return Math.min((minHeight * 2) + insetTop, getHeight());
        }

        // If we reach here then we don't have a min height set. Instead we'll take a
        // guess at 1/3 of our height being visible
        return getHeight() / 3;
    }

    /**
     * Set the duration used for scrim visibility animations.
     *
     * @param duration the duration to use in milliseconds
     */
    public void setScrimAnimationDuration(@IntRange(from = 0) final long duration) {
        mScrimAnimationDuration = duration;
    }

    /**
     * Returns the duration in milliseconds used for scrim visibility animations.
     */
    public long getScrimAnimationDuration() {
        return mScrimAnimationDuration;
    }

    /**
     * @see CollapsingTextAnimationController#setOnTitleLineCountChangeListener(OnExpandedTitleLineCountChangeListener)
     */
    public void setOnTitleLineCountChangeListener(@NonNull OnExpandedTitleLineCountChangeListener listener) {
        mTextAnimationController.setOnTitleLineCountChangeListener(listener);
    }

    @NonNull
    public CollapsingImageStateListener getCollapsingImageStateListener() {
        return mTextAnimationController;
    }

    public void setSnapMode(boolean isSnapMode) {
        mTextAnimationController.setSnapMode(isSnapMode);
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) getLayoutParams();
        int scrollFlags = lp.getScrollFlags();
        int newScrollFlags = isSnapMode ? scrollFlags | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP :
                scrollFlags & ~AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
        lp.setScrollFlags(newScrollFlags);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * Вычисляет вертикальное смещение View, используемого в качестве фона, с эффектом parallax,
     * для заданной степени разворота графической шапки
     */
    public float calculateParallaxViewOffset(@NonNull View child, float fraction) {
        float verticalOffset = (1 - fraction) * getExpandRange();
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getParallaxViewOffset(lp, fraction, verticalOffset);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;

        /**
         * @hide
         */
        @SuppressWarnings("JavaDoc")
        @RestrictTo(LIBRARY_GROUP)
        @IntDef({
                COLLAPSE_MODE_OFF,
                COLLAPSE_MODE_PIN,
                COLLAPSE_MODE_PARALLAX
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface CollapseMode {
        }

        /**
         * The view will act as normal with no collapsing behavior.
         */
        public static final int COLLAPSE_MODE_OFF = 0;

        /**
         * The view will pin in place until it reaches the bottom of the
         * {@link CollapsingToolbarLayout}.
         */
        public static final int COLLAPSE_MODE_PIN = 1;

        /**
         * The view will scroll in a parallax fashion. See {@link #setParallaxMultiplier(float)}
         * to change the multiplier used.
         */
        public static final int COLLAPSE_MODE_PARALLAX = 2;

        int mCollapseMode = COLLAPSE_MODE_OFF;
        float mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;

        @SuppressLint("PrivateResource")
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_Layout);
            mCollapseMode = a.getInt(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_Layout_layout_collapseMode,
                    COLLAPSE_MODE_OFF);
            setParallaxMultiplier(a.getFloat(
                    com.google.android.material.R.styleable.CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier,
                    DEFAULT_PARALLAX_MULTIPLIER));
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            // The copy constructor called here only exists on API 19+.
            super(source);
        }

        /**
         * Set the collapse mode.
         *
         * @param collapseMode one of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
         *                     or {@link #COLLAPSE_MODE_PARALLAX}.
         */
        public void setCollapseMode(@CollapseMode int collapseMode) {
            mCollapseMode = collapseMode;
        }

        /**
         * Returns the requested collapse mode.
         *
         * @return the current mode. One of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
         * or {@link #COLLAPSE_MODE_PARALLAX}.
         */
        @CollapseMode
        public int getCollapseMode() {
            return mCollapseMode;
        }

        /**
         * Set the parallax scroll multiplier used in conjunction with
         * {@link #COLLAPSE_MODE_PARALLAX}. A value of {@code 0.0} indicates no movement at all,
         * {@code 1.0f} indicates normal scroll movement.
         *
         * @param multiplier the multiplier.
         * @see #getParallaxMultiplier()
         */
        public void setParallaxMultiplier(float multiplier) {
            mParallaxMult = multiplier;
        }

        /**
         * Returns the parallax scroll multiplier used in conjunction with
         * {@link #COLLAPSE_MODE_PARALLAX}.
         *
         * @see #setParallaxMultiplier(float)
         */
        public float getParallaxMultiplier() {
            return mParallaxMult;
        }
    }

    /**
     * Show or hide the scrims if needed
     */
    final void updateScrimVisibility() {
        if (mContentScrim != null || mStatusBarScrim != null) {
            setScrimsShown(getHeight() + mCurrentOffset < getScrimVisibleHeightTrigger());
        }
    }

    final int getMaxOffsetForPinChild(View child, @Px int childLayoutTop) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getHeight()
                - childLayoutTop
                - child.getHeight()
                - lp.bottomMargin;
    }

    @Px
    private int getOffsetHelperLayoutTop(View child) {
        return getViewOffsetHelper(child).getLayoutTop();
    }

    private void updateUnscaledTitleHeight() {
        mTextAnimationController.recalculate();
        mUnscaledTitleHeight = mTextAnimationController.getTitleHeightUnscaled();
    }

    private float getParallaxViewExtraOffset(float appBarOffset) {
        if (!mIsTitleWithBackground) return 0f;
        return getFullTitleHeight() - getVisibleTitleBackgroundHeight(appBarOffset);
    }

    private int getVisibleTitleBackgroundHeight(float appBarOffset) {
        if (!mIsTitleWithBackground) return 0;
        return AnimationUtils.lerp(
                getFullTitleHeight(),
                0,
                appBarOffset,
                ACCELERATE_INTERPOLATOR
        );
    }

    private int getParallaxViewOffset(LayoutParams lp, float fraction, float verticalOffset) {
        return Math.round((-verticalOffset + getParallaxViewExtraOffset(fraction)) * lp.mParallaxMult);
    }

    private int getExpandRange() {
        return getHeight() - ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this) -
                getInsetTop();
    }

    private int getInsetTop() {
        return mLastInsets != null ?
                mLastInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top : 0;
    }

    private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        OffsetUpdateListener() {
        }

        @Override
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            mCurrentOffset = verticalOffset;

            final int insetTop = getInsetTop();

            // Update the collapsing text's fraction
            float expansionFraction = Math.abs(verticalOffset) / (float) getExpandRange();
            float parallaxViewExtraOffset = getParallaxViewExtraOffset(expansionFraction);
            int visibleTitleBackgroundHeight = getVisibleTitleBackgroundHeight(expansionFraction);

            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

                if (mIsTitleWithBackground && child.getTag() == TAG_GRADIENT) {
                    offsetHelper.setTopAndBottomOffset(getFullTitleHeight() - visibleTitleBackgroundHeight);
                    continue;
                }

                switch (lp.mCollapseMode) {
                    case LayoutParams.COLLAPSE_MODE_PIN: {
                        offsetHelper.setTopAndBottomOffset(MathUtils.clamp(-verticalOffset, 0,
                                getMaxOffsetForPinChild(child, getOffsetHelperLayoutTop(child))));
                        break;
                    }
                    case LayoutParams.COLLAPSE_MODE_PARALLAX: {
                        offsetHelper.setTopAndBottomOffset(getParallaxViewOffset(
                                lp, expansionFraction, verticalOffset));
                        break;
                    }
                }
            }

            // Show or hide the scrims if needed
            updateScrimVisibility();

            if (mStatusBarScrim != null && insetTop > 0) {
                ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
            }
        }
    }
}

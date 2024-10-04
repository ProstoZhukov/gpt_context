package ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.TypedValue;

import java.util.Objects;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design.list_utils.util.FixClicksAfterScrollItemTouchListener;
import ru.tensor.sbis.design_dialogs.dialogs.container.Container;
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment;
import ru.tensor.sbis.design_dialogs.dialogs.content.Content;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.slideContentDownAndThenDismiss;
import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.slideContentDownAndThenDismissAllowingStateLoss;

/**
 * Реализация bottom sheet, представляющая собой контейнер для отображения контента.
 */
@SuppressWarnings("unused")
public class ContainerBottomSheet extends BaseContainerDialogFragment implements
        Container.Showable, Container.Closeable {

    /**
     * Ключ для хранения параметра о необходимости сразу отображать контент.
     */
    private static final String INSTANT_SHOW_CONTENT_ARG = ContainerBottomSheet.class.getSimpleName().concat(":INSTANT_SHOW_CONTENT_ARG");

    /**
     * Ключ для хранения параметра о режиме клавиатуры.
     */
    private static final String SOFT_INPUT_MODE_ARG = ContainerBottomSheet.class.getSimpleName().concat(":SOFT_INPUT_MODE_ARG");

    /**
     * Ключ для хранения параметра о закрытии диалога при клике на затемненную область.
     */
    private static final String CANCEL_ON_TOUCH_OUTSIDE_ARG = ContainerBottomSheet.class.getSimpleName().concat(":CANCEL_ON_TOUCH_OUTSIDE_ARG");

    /**
     * Ключ для хранения параметра о закрытии диалога при клике на контейнер область.
     */
    private static final String CANCEL_ON_TOUCH_CONTAINER_ARG = ContainerBottomSheet.class.getSimpleName().concat(":CANCEL_ON_TOUCH_CONTAINER_ARG");

    /**
     * Ключ для хранения параметра полноэкранного режима в альбомной ориентации.
     */
    private static final String FULLSCREEN_IN_LANDSCAPE_ARG = ContainerBottomSheet.class.getSimpleName().concat(":FULLSCREEN_IN_LANDSCAPE_ARG");

    /**
     * Ключ для хранения поведения. Если true, то после того как панель дошла до состояния HIDDEN, будет выставлено состояние COLLAPSED
     * Зачем так делается не очень понятно, оставлено для обратной совместимости.
     */
    private static final String COLLAPSE_AFTER_HIDDEN = ContainerBottomSheet.class.getSimpleName().concat(":COLLAPSE_AFTER_HIDDEN");

    /**
     * Ключ для хранения параметра режима позиционирования контента.
     */
    private static final String VISUAL_MODE_ARG = ContainerBottomSheet.class.getSimpleName().concat(":VISUAL_MODE_ARG");

    /**
     * Ключ для хранения параметра показа диалога в полноэкранном immersive режиме.
     */
    private static final String IMMERSIVE_FULLSCREEN_ARG = ContainerBottomSheet.class.getSimpleName().concat(":IMMERSIVE_FULLSCREEN_ARG");

    /**
     * Ключ для хранения состояния флага, отображен ли контент.
     */
    private static final String CONTENT_SHOWN_STATE_KEY = ContainerBottomSheet.class.getSimpleName().concat(":CONTENT_SHOWN_STATE_KEY");

    /**
     * Ключ для хранения максимального времени анимации показа/скрытия содержимого.
     */
    private static final String MAX_CONTENT_ANIMATION_DURATION_KEY = ContainerBottomSheet.class.getSimpleName().concat(":MAX_CONTENT_ANIMATION_DURATION_KEY");

    /**
     * Ключ для хранения времени до уведомления о завершении отображения содержимого.
     */
    private static final String CONTENT_SHOWN_NOTIFICATION_DELAY_KEY = ContainerBottomSheet.class.getSimpleName().concat(":CONTENT_SHOWN_NOTIFICATION_DELAY_KEY");

    /**
     * По умолчанию контент отображается сразу.
     */
    private static final boolean DEFAULT_INSTANT_SHOW_CONTENT_VALUE = true;

    /**
     * По умолчанию клавиатура ужимает контент.
     */
    private static final int defaultSoftInputMode = SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_RESIZE;

    /**
     * По умолчанию диалог закрывается при клике на затемненную область.
     */
    private static final boolean DEFAULT_CANCEL_ON_TOUCH_OUTSIDE_VALUE = true;

    /**
     * По умолчанию диалог закрывается при клике на контейнер.
     */
    private static final boolean DEFAULT_CANCEL_ON_TOUCH_CONTAINER_VALUE = true;

    /**
     * По умолчанию в альбомной ориентации отображается в полноэкранном режиме.
     */
    private static final boolean DEFAULT_FULLSCREEN_IN_LANDSCAPE_VALUE = true;

    /**
     * По умолчанию режим отображения по стандарту окно выбора.
     */
    private static final VisualMode DEFAULT_VISUAL_MODE_VALUE = SelectionPaneVisualMode.INSTANCE;

    /**
     * По умолчанию не отображается в полноэкранном immersive режиме.
     */
    private static final boolean DEFAULT_IMMERSIVE_FULLSCREEN_VALUE = false;

    /**
     * Значение по умолчанию времени, через которое требуется инициировать уведомление о завершении анимации показа
     * содержимого
     */
    private static final int DEFAULT_CONTENT_SHOWN_NOTIFICATION_DELAY = 250;

    private static final int DEFAULT_MAX_CONTENT_ANIMATION_DURATION = 150;

    /**
     * Экземпляр {@link CustomBottomSheetBehavior} для управления поведением bottom sheet.
     */
    @SuppressWarnings("rawtypes")
    protected CustomBottomSheetBehavior mBehavior;

    /**
     * Отображен ли контент на данный момент.
     */
    private boolean mContentShown;

    /**
     * Флаг, сигнализирующий о том, что ожидается отображение контента.
     */
    private boolean mPendingShowContent;

    @Nullable
    private Runnable mAnimationRunnable;

    /**
     * Задать режим отображения контента: мгновенно после появления
     * панели на экране или по сигналу через интерфейс {@link Showable}.
     *
     * @param instant - режим отображения контента
     */
    public ContainerBottomSheet instant(boolean instant) {
        getOrCreateArguments().putBoolean(INSTANT_SHOW_CONTENT_ARG, instant);
        return this;
    }

    /**
     * Задать режим отображения клавиатуры.
     *
     * @param softInputMode - режим отображения клавиатуры
     */
    public ContainerBottomSheet softInputMode(int softInputMode) {
        getOrCreateArguments().putInt(SOFT_INPUT_MODE_ARG, softInputMode);
        return this;
    }

    /**
     * Задать параметр поведения диалога: закрывать диалог при клике
     * на затемненную область или нет.
     *
     * @param closeable - нужно ли закрывать
     */
    public ContainerBottomSheet cancelable(boolean closeable) {
        getOrCreateArguments().putBoolean(CANCEL_ON_TOUCH_OUTSIDE_ARG, closeable);
        return this;
    }

    /**
     * Задать параметр поведения диалога: закрывать диалог при клике
     * на контейнер или нет.
     *
     * @param closeable - нужно ли закрывать
     */
    public ContainerBottomSheet cancelableOnContentClick(boolean closeable) {
        getOrCreateArguments().putBoolean(CANCEL_ON_TOUCH_CONTAINER_ARG, closeable);
        return this;
    }

    /**
     * Задать флаг полноэкранного режима в альбомной ориентации. По умолчанию значение истинно.
     * При использовании полноэкранного immersive режима ({@link #immersiveFullscreen(boolean)}) значение не будет
     * влиять на показ статусбара в альбомной ориентации
     *
     * @param isFullScreenInLandscape нужно ли в альбомной ориентации отображать диалог в полноэкранном режиме (скрывая
     *                                статусбар)
     */
    public ContainerBottomSheet fullScreenInLandscape(boolean isFullScreenInLandscape) {
        getOrCreateArguments().putBoolean(FULLSCREEN_IN_LANDSCAPE_ARG, isFullScreenInLandscape);
        return this;
    }

    public ContainerBottomSheet collapseAfterHidden(boolean collapseAfterHidden) {
        getOrCreateArguments().putBoolean(COLLAPSE_AFTER_HIDDEN, collapseAfterHidden);
        return this;
    }

    public ContainerBottomSheet setVisualMode(VisualMode mode) {
        getOrCreateArguments().putSerializable(VISUAL_MODE_ARG, mode);
        return this;
    }

    /**
     * Задать флаг полноэкранного immersive режима. По умолчанию значение false.
     *
     * @param isImmersiveFullscreen нужно ли отображать диалог в полноэкранном immersive режиме (скрывая статусбар и
     *                              панель навигации)
     */
    public ContainerBottomSheet immersiveFullscreen(boolean isImmersiveFullscreen) {
        getOrCreateArguments().putBoolean(IMMERSIVE_FULLSCREEN_ARG, isImmersiveFullscreen);
        return this;
    }

    /**
     * Задаёт максимальное время анимации показа/скрытия содержимого.
     *
     * @param maxDuration предельная длительность анимации (мс)
     */
    public ContainerBottomSheet maxContentAnimationDuration(int maxDuration) {
        getOrCreateArguments().putInt(MAX_CONTENT_ANIMATION_DURATION_KEY, maxDuration);
        return this;
    }

    /**
     * Задаёт время до уведомления о завершении отображения содержимого.
     *
     * @param delay время до уведомления о завершении отображения содержимого (мс)
     */
    public ContainerBottomSheet contentShownNotificationDelay(int delay) {
        getOrCreateArguments().putInt(CONTENT_SHOWN_NOTIFICATION_DELAY_KEY, delay);
        return this;
    }

    @Override
    protected int getContainerLayoutRes() {
        return R.layout.design_dialogs_bottom_sheet_container;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style;
        int customContainerBottomSheetTheme = getThemeRes(R.attr.customBottomSheetTheme);
        int customContainerBottomSheetThemeNoFullscreenInLandscape =
                getThemeRes(R.attr.customBottomSheetThemeNoFullscreenInLandscape);

        if (isFullscreenInLandscape()) {
            if (customContainerBottomSheetTheme != 0)
                style = customContainerBottomSheetTheme;
            else
                style = R.style.ContainerBottomSheetTheme;
        } else {
            if (customContainerBottomSheetThemeNoFullscreenInLandscape != 0)
                style = customContainerBottomSheetThemeNoFullscreenInLandscape;
            else
                style = R.style.ContainerBottomSheetThemeNoFullscreenInLandscape;
        }

        setStyle(CustomBottomSheetDialogFragment.STYLE_NORMAL, style);

    }

    private void setSoftInputMode() {
        if (getArguments() != null && getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(getArguments().getInt(SOFT_INPUT_MODE_ARG, defaultSoftInputMode));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        if (context == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        // Указываем CustomBottomSheetDialog в качестве диалогового окна
        return new CustomBottomSheetDialog(context, getTheme(), !isImmersiveFullScreen());
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSoftInputMode();

        if (isImmersiveFullScreen()) {
            // нужно сделать диалог non-focusable перед отображением, чтобы предотвратить сброс флагов видимости UI
            setDialogWindowFocusable(false);
            configureImmersiveFullscreen();
        }

        Objects.requireNonNull(getDialog()).setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (dialog instanceof CustomBottomSheetDialog) {
                            onShowDialog((CustomBottomSheetDialog) dialog, savedInstanceState);
                            if (isImmersiveFullScreen()) {
                                // после показа диалога можно сбросить значение FLAG_NOT_FOCUSABLE
                                setDialogWindowFocusable(true);
                            }
                        }
                    }
                }
        );
        View containerView = view.findViewById(getContainerViewId());
        if (isCancelOnTouchOutside() && isCancelOnTouchContent()) {
            containerView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeContainer();
                        }
                    }
            );
        }
        getVisualMode().apply(containerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        fixClicksAfterRecyclerViewScrollIfPresent();
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CONTENT_SHOWN_STATE_KEY, mContentShown);
    }

    @Override
    public void dismiss() {
        mContentShown = false;
        slideContentDownAndThenDismiss(this, getTag(), super::dismiss, super::dismissAllowingStateLoss);
    }

    @Override
    public void dismissAllowingStateLoss() {
        slideContentDownAndThenDismissAllowingStateLoss(this, getTag(), super::dismissAllowingStateLoss);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnimationRunnable != null) {
            getHandler().removeCallbacks(mAnimationRunnable);
            mAnimationRunnable = null;
        }
    }

    /**
     * Обрабатываем отображение диалогового окна.
     *
     * @param dialog             - отображенное диалоговое окно
     * @param savedInstanceState - сохраненное состояние фрагмента
     */
    @SuppressWarnings("Convert2Lambda")
    protected void onShowDialog(CustomBottomSheetDialog dialog, @Nullable Bundle savedInstanceState) {
        if (isCancelOnTouchOutside()) {
            // Подписываемся на закрытие диалога
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Content content = getContent();
                    if (content != null) content.onCloseContent();
                }
            });
        } else {
            dialog.setCanceledOnTouchOutside(false);
        }
        // Оборачиваем вью в bottom sheet и задаем callback
        View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet == null) {
            throw new IllegalStateException("Bottom sheet view not found.");
        }
        mBehavior = CustomBottomSheetBehavior.from(sheet);
        mBehavior.setBottomSheetCallback(
                new CustomBottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (isCollapseAfterHidden() && newState == CustomBottomSheetBehavior.STATE_HIDDEN) {
                            closeContainer();
                            mBehavior.setState(CustomBottomSheetBehavior.STATE_COLLAPSED);
                        }
                        if (newState == CustomBottomSheetBehavior.STATE_SETTLING &&
                                mBehavior.getLastTargetState() == CustomBottomSheetBehavior.STATE_HIDDEN) {
                            if (getDialog() != null) {
                                getDialog().getWindow()
                                        .setWindowAnimations(ResourcesCompat.ID_NULL);
                            }

                            closeContainer();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        // ignore
                    }
                }
        );
        mBehavior.setMaxSettleAnimationDuration(getMaxAnimationDuration());
        validateViewState(savedInstanceState);
    }

    protected void validateViewState(@Nullable Bundle savedInstanceState) {
        if (isInstantShowContent()) {
            // Отображаем контент сразу
            showContent(true);
        } else if (savedInstanceState != null && savedInstanceState.getBoolean(CONTENT_SHOWN_STATE_KEY)) {
            // Контент уже был отображен ранее
            showContent(false);
        } else if (mPendingShowContent) {
            showContent();
        } else {
            mBehavior.setPeekHeight(0); // Мгновенное скрытие панели
        }
    }

    // region Check configuration methods

    /**
     * Нужно ли отображать контент сразу.
     */
    protected final boolean isInstantShowContent() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(INSTANT_SHOW_CONTENT_ARG, DEFAULT_INSTANT_SHOW_CONTENT_VALUE);
        } else {
            return DEFAULT_INSTANT_SHOW_CONTENT_VALUE;
        }
    }

    /**
     * Нужно ли закрывать диалог при клике на контент.
     */
    protected final boolean isCancelOnTouchContent() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(CANCEL_ON_TOUCH_CONTAINER_ARG, DEFAULT_CANCEL_ON_TOUCH_CONTAINER_VALUE);
        } else {
            return DEFAULT_CANCEL_ON_TOUCH_CONTAINER_VALUE;
        }
    }

    /**
     * Нужно ли закрывать диалог при клике на затемненную область.
     */
    protected final boolean isCancelOnTouchOutside() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(CANCEL_ON_TOUCH_OUTSIDE_ARG, DEFAULT_CANCEL_ON_TOUCH_OUTSIDE_VALUE);
        } else {
            return DEFAULT_CANCEL_ON_TOUCH_OUTSIDE_VALUE;
        }
    }

    /**
     * Нужно ли отображать в полноэкранном режиме в альбомной ориентации.
     */
    protected final boolean isFullscreenInLandscape() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(FULLSCREEN_IN_LANDSCAPE_ARG, DEFAULT_FULLSCREEN_IN_LANDSCAPE_VALUE);
        } else {
            return DEFAULT_FULLSCREEN_IN_LANDSCAPE_VALUE;
        }
    }

    /**
     * Нужно ли выставлять состояние COLLAPSED после HIDDEN.
     */
    protected final boolean isCollapseAfterHidden() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(COLLAPSE_AFTER_HIDDEN, true);
        } else {
            return true;
        }
    }

    /**
     * Режим отображения контента в окне
     */
    protected final VisualMode getVisualMode() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(VISUAL_MODE_ARG)) {
            return (VisualMode) args.getSerializable(VISUAL_MODE_ARG);
        } else {
            return DEFAULT_VISUAL_MODE_VALUE;
        }
    }

    /**
     * Нужно ли отображать диалог в полноэкранном immersive режиме.
     */
    protected final boolean isImmersiveFullScreen() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getBoolean(IMMERSIVE_FULLSCREEN_ARG, DEFAULT_IMMERSIVE_FULLSCREEN_VALUE);
        } else {
            return DEFAULT_IMMERSIVE_FULLSCREEN_VALUE;
        }
    }

    /**
     * Заданное максимальное время анимации показа/скрытия содержимого.
     */
    protected final int getMaxAnimationDuration() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getInt(MAX_CONTENT_ANIMATION_DURATION_KEY, DEFAULT_MAX_CONTENT_ANIMATION_DURATION);
        } else {
            return DEFAULT_MAX_CONTENT_ANIMATION_DURATION;
        }
    }

    /**
     * Заданное время до уведомления о завершении отображения содержимого.
     */
    protected final int getContentShownNotificationDelay() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getInt(CONTENT_SHOWN_NOTIFICATION_DELAY_KEY, DEFAULT_CONTENT_SHOWN_NOTIFICATION_DELAY);
        } else {
            return DEFAULT_MAX_CONTENT_ANIMATION_DURATION;
        }
    }

    // endregion

    // region Internal methods

    /**
     * Отобразить контент.
     */
    protected void showContent(boolean animate) {
        if (mBehavior != null) {
            hideProgress();
            mPendingShowContent = false;
            if (animate) {
                if (!mContentShown) {
                    notifyStartShowingAnimation();
                    mAnimationRunnable = this::notifyFinishShowingAnimation;
                    getHandler().postDelayed(mAnimationRunnable, getContentShownNotificationDelay());
                }
            } else {
                mBehavior.setPeekHeight(CustomBottomSheetBehavior.PEEK_HEIGHT_AUTO); // Мгновенное отображение панели
            }
            mContentShown = true;
            mBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
            mBehavior.setSkipCollapsed(true); // Игнорируем состояние неполного отображения панели
        } else {
            mPendingShowContent = true;
        }
    }

    // endregion

    // region Properties

    @Override
    public void showContent() {
        showContent(true);
    }

    @Override
    public void closeContainer() {
        Content content = getContent();
        if (content != null) content.onCloseContent();
        dismissAllowingStateLoss();
    }

    // endregion

    private void setDialogWindowFocusable(boolean isFocusable) {
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        if (isFocusable) {
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            getDialog().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            );
        }
    }

    private void configureImmersiveFullscreen() {
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        getDialog().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void fixClicksAfterRecyclerViewScrollIfPresent() {
        RecyclerView recyclerView = findChildRecyclerView(getView());
        if (recyclerView != null) {
            recyclerView.addOnItemTouchListener(new FixClicksAfterScrollItemTouchListener());
        }
    }

    @Nullable
    private RecyclerView findChildRecyclerView(@Nullable View view) {
        if (view instanceof RecyclerView) {
            return (RecyclerView) view;
        }

        if (!(view instanceof ViewGroup)) {
            return null;
        }

        ViewGroup viewGroup = ((ViewGroup) view);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            RecyclerView recyclerView = findChildRecyclerView(viewGroup.getChildAt(i));
            if (recyclerView != null) {
                return recyclerView;
            }
        }

        return null;
    }

    private int getThemeRes(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}

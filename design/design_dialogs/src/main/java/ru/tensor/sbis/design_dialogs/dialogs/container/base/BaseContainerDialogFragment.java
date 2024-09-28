package ru.tensor.sbis.design_dialogs.dialogs.container.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design.util.DialogFragmentExtKt;
import ru.tensor.sbis.design.view_ext.SbisProgressBar;
import ru.tensor.sbis.design_dialogs.dialogs.container.Container;
import ru.tensor.sbis.design_dialogs.dialogs.container.ContentActionHandler;
import ru.tensor.sbis.design_dialogs.dialogs.content.BaseContentCreator;
import ru.tensor.sbis.design_dialogs.dialogs.content.Content;
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorWithContext;
import ru.tensor.sbis.design_dialogs.dialogs.helper.ViewVisibilityHelper;

/**
 * Базовая реализация контейнера, основанного на {@link DialogFragment}.
 * Также является контентом для удобства доступа к интерфейсам.
 * См {@link ru.tensor.sbis.design_dialogs.dialogs.content.utils.ContentFragmentUtils#anyContainerAs(Content, Class)}
 */
public class BaseContainerDialogFragment
        extends AppCompatDialogFragment
        implements Container, Container.HasProgress, Content {

    @IntDef({PROGRESS_MODE_NONE, PROGRESS_MODE_MANUAL, PROGRESS_MODE_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressMode { }

    /**
     * Прогресс бар не доступен.
     */
    public static final int PROGRESS_MODE_NONE = -1;

    /**
     * Прогресс бар управляется контентом.
     */
    public static final int PROGRESS_MODE_MANUAL = 0;

    /**
     * Прогресс бар отображается автоматически.
     */
    public static final int PROGRESS_MODE_AUTO = 1;

    /**
     * Ключ для хранения создателя контента, реализующего Serializable, в аргументах фрагмента-контейнера.
     */
    private static final String CONTENT_CREATOR_ARG = BaseContainerDialogFragment.class.getSimpleName().concat(":CONTENT_CREATOR_ARG");

    /**
     * Ключ для хранения создателя контента, реализующего Parcelable, в аргументах фрагмента-контейнера.
     */
    private static final String CONTENT_CREATOR_PARCELABLE_ARG = BaseContainerDialogFragment.class.getSimpleName().concat(":CONTENT_CREATOR_PARCELABLE_ARG");

    /**
     * Ключ для хранения параметра об отображении прогресс бара до появления контента.
     */
    private static final String PROGRESS_MODE_ARG = BaseContainerDialogFragment.class.getSimpleName().concat(":PROGRESS_MODE_ARG");

    /**
     * По умолчанию прогресс не отображается.
     */
    private static final int DEFAULT_PROGRESS_MODE = PROGRESS_MODE_NONE;

    /**
     * Задержка перед показом прогресс-бара по умолчанию.
     */
    private static final long AUTO_PROGRESS_MODE_DELAY = 1000;

    /**
     * Вспомогательный класс для работы с прогресс баром.
     */
    @Nullable
    private ViewVisibilityHelper mProgressBarHelper;

    @Nullable
    private Handler mHandler;

    @NonNull
    protected Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    /**
     * Задать режим отображения прогресса.
     *  -   {@link #PROGRESS_MODE_NONE}     - прогресс не доступен
     *  -   {@link #PROGRESS_MODE_MANUAL}   - прогрессом управляет контент
     *  -   {@link #PROGRESS_MODE_AUTO}     - прогресс отображается в автоматическом режиме
     * @param mode - режим отображения прогресса
     */
    public BaseContainerDialogFragment progressMode(@ProgressMode int mode) {
        getOrCreateArguments().putInt(PROGRESS_MODE_ARG, mode);
        return this;
    }

    /**
     * Получить идентификатор макета для контейнера.
     * В макете должна присутствовать вью с идентификатором,
     * указанном в методе {@link #getContainerViewId()}.
     */
    @LayoutRes
    protected int getContainerLayoutRes() {
        return R.layout.design_dialogs_simple_container;
    }

    /**
     * Получить идентификатор вью, выступающей в качестве
     * контейнера для контента. Вью с данным идентификатором
     * должна присутствовать в макете, указанном в методе
     * {@link #getContainerLayoutRes()}.
     */
    protected int getContainerViewId() {
        return R.id.design_dialogs_container_view;
    }

    /**
     * Получить фрагмент с содержимым.
     *
     * @return фрагмент с содержимым, или <code>null</code> если фрагмент еще не создан
     */
    @Nullable
    protected final Fragment getContentFragment() {
        if (isAdded()) {
            return getChildFragmentManager().findFragmentById(getContainerViewId());
        }
        return null;
    }

    /**
     * Получить контент.
     */
    @Nullable
    protected final Content getContent() {
        return contentAs(Content.class);
    }

    /**
     * Получить фрагмент с контентом, приведенный к указанному типу.
     *
     * @param type  - тип, к которому необходимо привести контнет
     * @param <T>   - тип, к которому необходимо привести контент
     * @return экземпляр типа {@link T} или <code>null</code>, если фрагмент с контентом
     * не принадлежит данному типу
     */
    protected final <T> T contentAs(@SuppressWarnings("SameParameterValue") @NonNull Class<? extends T> type) {
        Fragment container = getContentFragment();
        if (type.isInstance(container)) {
            return type.cast(container);
        }
        return null;
    }

    /**
     * Получить экземпляр создателя контента.
     * @return экземпляр создателя контента
     */
    @Nullable
    protected final Fragment getWithContentCreatorWithContext() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException("Attempt to get content creator while arguments is not defined.");
        }
        if (arguments.containsKey(CONTENT_CREATOR_ARG)) {
            Object creator = arguments.getSerializable(CONTENT_CREATOR_ARG);
            if (creator instanceof ContentCreatorWithContext) {
                return ((ContentCreatorWithContext) creator).createFragment(requireContext());
            }
        }
        return null;
    }

    /**
     * Получить экземпляр создателя контента.
     * @return экземпляр создателя контента
     */
    protected final BaseContentCreator getContentCreator() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException("Attempt to get content creator while arguments is not defined.");
        }
        if (arguments.containsKey(CONTENT_CREATOR_ARG)) {
            return (BaseContentCreator) arguments.getSerializable(CONTENT_CREATOR_ARG);
        } else if (arguments.containsKey(CONTENT_CREATOR_PARCELABLE_ARG)) {
            return (BaseContentCreator) arguments.getParcelable(CONTENT_CREATOR_PARCELABLE_ARG);
        }
        throw new IllegalStateException("Arguments do not contain content creator.");
    }
    /**
     * Задать экземпляр создателя контента. Этот метод необходимо вызывать
     * на новом экземпляре контейнера ДО попадания фрагмента в {@link FragmentManager}.
     *
     * @param creator - создатель контента
     */
    @NonNull
    public BaseContainerDialogFragment setContentCreator(@NonNull BaseContentCreator creator) {
        Bundle arguments = getOrCreateArguments();
        if (creator instanceof Serializable) {
            arguments.putSerializable(CONTENT_CREATOR_ARG, (Serializable) creator);
        } else if (creator instanceof Parcelable) {
            arguments.putParcelable(CONTENT_CREATOR_PARCELABLE_ARG, (Parcelable) creator);
        } else {
            throw new IllegalStateException("Content creator must implement Serializable or Parcelable");
        }
        return this;
    }

    @NonNull
    public BaseContainerDialogFragment setContentCreator(@NonNull ContentCreatorWithContext creator) {
        Bundle arguments = getOrCreateArguments();
        arguments.putSerializable(CONTENT_CREATOR_ARG, creator);
        return this;
    }

    @SuppressLint("InflateParams")
    @CallSuper
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isProgressBarAvailable()) {
            setupProgressBar(savedInstanceState);
            if (savedInstanceState == null && mProgressBarHelper != null
                    && getProgressMode() == PROGRESS_MODE_AUTO) {
                // Применение автоматически отображаемого прогресса
                mProgressBarHelper.setVisibilityDelayed(true, AUTO_PROGRESS_MODE_DELAY);
            }
        }
        // Создаем простой контейнер с FrameLayout для размещения внутри него контента
        return inflater.inflate(getContainerLayoutRes(), null);
    }

    private void setupProgressBar(@Nullable Bundle savedInstanceState) {
        final Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            final View decorView = window.getDecorView();
            if (decorView instanceof FrameLayout) {
                View progressView = createProgressView((FrameLayout) decorView);
                if (progressView != null) {
                    mProgressBarHelper = new ViewVisibilityHelper("progress", progressView, savedInstanceState, false);
                }
            }
        }
    }

    @Override
    public void showProgress() {
        if (mProgressBarHelper != null) {
            mProgressBarHelper.setVisibility(true);
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressBarHelper != null) {
            mProgressBarHelper.setVisibility(false);
        }
    }

    /**
     * Создать вью для отображеня прогресса.
     * @param container - контейнер, внутри которого можно разместить вью
     * @return экземпляр progress view
     */
    @SuppressWarnings("deprecation")
    @Nullable
    protected View createProgressView(@NonNull FrameLayout container) {
        final View progressBar = new SbisProgressBar(getContext());
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.CENTER;
        container.addView(progressBar, lp);
        return progressBar;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment contentFragment = getContentFragment();
        if (contentFragment == null) {
            contentFragment = getWithContentCreatorWithContext();
            if (contentFragment == null) {
                // Создаем новый экземпляр фрагмента с контентом
                contentFragment = getContentCreator().createFragment();
            }
        }
        // Вставляем фрагмент с контентом во вью-контейнер
        getChildFragmentManager().beginTransaction()
                .replace(getContainerViewId(), contentFragment)
                .commit();
        // Подписываемся на событие нажатия кнопки "назад"
        Dialog dialog = getDialog();
        if (dialog != null) {
            //noinspection Convert2Lambda
            dialog.setOnKeyListener(
                    new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                                Content content = getContent();
                                return content != null && content.onBackPressed();
                            }
                            return false;
                        }
                    }
            );
        }
        // Проверяем, нужно ли показывать статус бар
        DialogFragmentExtKt.checkStatusBarShouldBeShown(this);
    }

    @Override
    public void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mProgressBarHelper != null) {
            mProgressBarHelper.saveInstanceState(outState);
        }
    }

    // region Utility methods

    /**
     * Получить аргументы фрагмента или создать новый Bundle для них.
     */
    @NonNull
    protected final Bundle getOrCreateArguments() {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
            setArguments(args);
        }
        return args;
    }

    // endregion

    // region Container impl

    @Override
    public void onContentAction(@NonNull String actionId, @Nullable Bundle data) {
        ContentActionHandler handler = ContentActionHandler.Helper.getActionHandler(this);
        if (handler != null) {
            handler.onContentAction(actionId, data);
        }
    }

    // endregion

    // region Animation notifies

    /**
     * Уведомить контент о начала анимации отображения.
     */
    protected final void notifyStartShowingAnimation() {
        final Fragment content = getContentFragment();
        if (content instanceof Content.ShowAnimationListener) {
            ((Content.ShowAnimationListener) content).onStartShowAnimation();
        }
    }

    /**
     * Уведомить контент о завершении анимации отображения.
     */
    protected final void notifyFinishShowingAnimation() {
        final Fragment content = getContentFragment();
        if (content instanceof Content.ShowAnimationListener) {
            ((Content.ShowAnimationListener) content).onFinishShowAnimation();
        }
    }

    // endregion

    // region Check configuration methods

    /**
     * Получить режим отображения прогресс бара.
     */
    protected final int getProgressMode() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getInt(PROGRESS_MODE_ARG, DEFAULT_PROGRESS_MODE);
        } else {
            return DEFAULT_PROGRESS_MODE;
        }
    }

    /**
     * Проверить, доступен ли прогресс бар.
     */
    protected boolean isProgressBarAvailable() {
        return getProgressMode() != PROGRESS_MODE_NONE;
    }

    // endregion

    @Override
    public boolean onBackPressed() {
        Content content = getContent();
        return content != null && content.onBackPressed();
    }

    @Override
    public void onCloseContent() {
    }
}

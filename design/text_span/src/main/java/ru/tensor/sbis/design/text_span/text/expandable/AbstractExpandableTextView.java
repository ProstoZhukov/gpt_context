package ru.tensor.sbis.design.text_span.text.expandable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import ru.tensor.sbis.design.utils.animator.helper.SerialAnimatorController;
import ru.tensor.sbis.design.utils.animator.listener.HeightAnimatorListener;
import ru.tensor.sbis.design.text_span.R;

/**
 * Реализация TextView с возможностью сворачивания/разворачивания контента.
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public abstract class AbstractExpandableTextView extends AppCompatTextView {

    /**
     * Кнопка не отображается.
     */
    public static final int BUTTON_TYPE_NONE = 0;

    /**
     * Кнопка отображается под текстом.
     */
    public static final int BUTTON_TYPE_BELOW_TEXT = 1;

    /**
     * В свернутом состоянии кнопка отображается вместо последней строчки,
     * в развернутом - поведение аналогично {@link #BUTTON_TYPE_BELOW_TEXT}.
     */
    public static final int BUTTON_TYPE_REPLACE_LAST = 2;

    /**
     * Длительность анимации - фиксированная.
     */
    public static final int DURATION_TYPE_FIXED = 0;

    /**
     * Длительность анимации зависит от изменения высоты.
     */
    public static final int DURATION_TYPE_PER_100_DP = 1;

    private static final String ELLIPSIZE_END = "...";

    private static final int DEFAULT_BUTTON_TYPE = BUTTON_TYPE_BELOW_TEXT;

    private static final int DEFAULT_MAX_COLLAPSED_LINES = 2;

    private static final boolean DEFAULT_EXPAND_ON_CONTENT_CLICK = false;

    private static final boolean DEFAULT_ELLIPSIZE_END = false;

    private static final int DEFAULT_ANIMATION_DURATION_RES_ID = android.R.integer.config_mediumAnimTime;

    private static final int DEFAULT_ANIMATION_DURATION_TYPE = DURATION_TYPE_FIXED;

    private static final int DEFAULT_BUTTON_EXPANSION_RES_ID = R.dimen.text_span_expandable_text_view_default_button_expansion;

    private final int MIN_ANIMATION_DURATION = getResources().getInteger(android.R.integer.config_shortAnimTime);

    // region Configuration
    /**
     * Максимальное количество строк в свернутом состоянии.
     */
    private int mMaxLinesWhenCollapsed;

    /**
     * Тип кнопки "Раскрыть"/"Свернуть".
     */
    private int mButtonType;

    /**
     * Нужно ли заменять не поместивщийся текст на "...".
     */
    private boolean mEllipsizeEnd;

    /**
     * Размер области вокруг кнопки, перехватывающей нажатия.
     */
    private int mButtonExpansion;
    // endregion

    // region State
    /**
     * Текст данного TextView.
     */
    @NonNull
    private CharSequence mContent = StringUtils.EMPTY;

    /**
     * Флаг, сигнализирующий о том, что изменения контента нужно игнорировать.
     */
    private boolean mIgnoreContentChanges;

    /**
     * Вмещающийся текст данного TextView.
     */
    private final SpannableStringBuilder mEllipsizedContent = new SpannableStringBuilder();

    /**
     * Состояние view: раскрытый или свернутый вид.
     */
    private boolean mExpanded;

    /**
     * Нужно ли отображать кнопку.
     */
    private boolean mWithButton;

    /**
     * Высота view в развернутом состоянии.
     */
    private int mExpandedHeight;

    /**
     * Высота view в свернутом состоянии.
     */
    private int mCollapsedHeight;

    /**
     * Нужно ли изменять состояние view при нажатии на контент.
     */
    private boolean mExpandOnContentClick;

    /**
     * Слушатель изменений состояния.
     */
    private OnExpandStateListener mOnExpandStateListener;

    /**
     * Нужно ли блокировать вызовы super.requestLayout() и super.invalidate().
     */
    private boolean mLockRequestLayoutAndInvalidate = false;

    /**
     * Находится ли компонент в состоянии установки текста извне
     */
    private boolean mTextInBinding = false;
    // endregion

    // region Animations
    /**
     * Время анимации раскрытия/сворачивания.
     */
    private int mAnimationDuration;

    /**
     * Тип анимации: фиксированный или динамически вычисляемый.
     */
    private int mAnimationDurationType;

    /**
     * Объект, управляющий аниматорами.
     */
    private final SerialAnimatorController mAnimatorController = new SerialAnimatorController();

    /**
     * Слушатель аниматора, выполняющий обновление высоты на каждый такт.
     */
    @SuppressWarnings("Convert2Lambda")
    private final ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = (int) animation.getAnimatedValue();
            setLayoutHeight(height);
        }
    };
    // endregion

    // Cache
    private final Rect mCanvasRect = new Rect();
    private final Rect mButtonRect = new Rect();
    private OnClickListener mExpandOnContentListener;

    /**
     * Получить высоту кнопки "Раскрыть".
     * @return высота кнопки в пикселях
     */
    protected abstract int getExpandButtonHeight();

    /**
     * Получить высоту кнопки "Свернуть".
     * @return высота кнопки в пикселях
     */
    protected abstract int getCollapseButtonHeight();

    /**
     * Отрисовать кнопку в указанной области и на указанном canvas.
     * @param canvas    - canvas для отрисовки кнопки
     * @param rect      - область для отрисовки кнопки
     */
    protected abstract void drawButton(Canvas canvas, Rect rect);


    public AbstractExpandableTextView(Context context) {
        this(context, null);
    }

    public AbstractExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AbstractExpandableTextView);
            mMaxLinesWhenCollapsed = typedArray.getInt(R.styleable.AbstractExpandableTextView_maxLinesWhenCollapsed, DEFAULT_MAX_COLLAPSED_LINES);
            mButtonType = typedArray.getInt(R.styleable.AbstractExpandableTextView_abstract_expandable_text_view_buttonType, DEFAULT_BUTTON_TYPE);
            mExpandOnContentClick = typedArray.getBoolean(R.styleable.AbstractExpandableTextView_expandOnContentClick, DEFAULT_EXPAND_ON_CONTENT_CLICK);
            mEllipsizeEnd = typedArray.getBoolean(R.styleable.AbstractExpandableTextView_ellipsizeEnd, DEFAULT_ELLIPSIZE_END);
            mAnimationDuration = typedArray.getInt(R.styleable.AbstractExpandableTextView_animationDuration, getResources().getInteger(DEFAULT_ANIMATION_DURATION_RES_ID));
            mAnimationDurationType = typedArray.getInt(R.styleable.AbstractExpandableTextView_animationDurationType, DEFAULT_ANIMATION_DURATION_TYPE);
            int defaultExpansion = getContext().getResources().getDimensionPixelSize(DEFAULT_BUTTON_EXPANSION_RES_ID);
            mButtonExpansion = typedArray.getDimensionPixelSize(R.styleable.AbstractExpandableTextView_buttonExpansion, defaultExpansion);
            typedArray.recycle();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setFallbackLineSpacing(false);
        }
        validateToggleListener();
    }

    // region Properties
    @Override
    public CharSequence getText() {
        if (mTextInBinding) {
            return super.getText();
        }
        return mContent;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null) {
            text = StringUtils.EMPTY;
        }
        cancelAnimation();
        mTextInBinding = true;
        super.setText(text, type);
        if (!mIgnoreContentChanges) {
            mContent = super.getText();
        }
        mTextInBinding = false;
        requestLayoutIfButtonVisible();
    }

    /**
     * В развернутом ли состоянии находится view.
     * @return true - если в развернутом, false - иначе
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * В свернутом ли состоянии находится view.
     * @return true - если в свернутом, false - иначе
     */
    public boolean isCollapsed() {
        return !mExpanded;
    }

    /**
     * Задать состояние view (свернутый или развернутый вид).
     * @param expanded - true - развернуть текст, false - свернуть текст
     */
    public void setExpanded(boolean expanded) {
        if (mExpanded != expanded) {
            mExpanded = expanded;
            requestLayoutIfButtonVisible();
            onStateChanged(false, expanded);
        }
    }

    /**
     * Получить тип кнопки.
     * @return тип кнопки
     */
    public int getButtonType() {
        return mButtonType;
    }

    /**
     * Задать тип кнопки.
     * @param buttonType - новый тип кнопки
     */
    public void setButtonType(int buttonType) {
        if (mButtonType != buttonType) {
            mButtonType = buttonType;
            notifyButtonChanged();
        }
    }

    /**
     * Должно ли меняться состояние view при клике на контент.
     * @return true - меняется, false - не меняется
     */
    public boolean isExpandOnContentClick() {
        return mExpandOnContentClick;
    }

    /**
     * Задать поведение view при клике на контент.
     * @param expandOnContentClick - флаг, определяющий поведение view при клике на контент
     *                             true     - view должна изменять состояние при клике на контент,
     *                             false    - не должна изменять состояние при клике на контент
     */
    public void setExpandOnContentClick(boolean expandOnContentClick) {
        if (mExpandOnContentClick != expandOnContentClick) {
            mExpandOnContentClick = expandOnContentClick;
            validateToggleListener();
        }
    }

    /**
     * Нужно ли заменять не поместившийся текст на "...".
     * @return true - нужно, false - не нужно
     */
    public boolean isEllipsizeEnd() {
        return mEllipsizeEnd;
    }

    /**
     * Задать поведение view в случае, если контент не помещается.
     * @param ellipsize - true - заменять не поместившийся текст на "...", false - не заменять
     */
    public void setEllipsizeEnd(boolean ellipsize) {
        if (mEllipsizeEnd != ellipsize) {
            mEllipsizeEnd = ellipsize;
            if (isCollapsed()) {
                requestLayoutIfButtonVisible();
            }
        }
    }

    /**
     * Задать слушатель событий об изменении состояние view.
     * @param listener - слушатель
     */
    public void setOnExpandStateListener(@Nullable OnExpandStateListener listener) {
        if (mOnExpandStateListener != listener) {
            mOnExpandStateListener = listener;
        }
    }
    // endregion


    // region Public
    /**
     * Проверить, анимируется ли view в данный момент.
     * @return true - если анимируется, false - иначе
     */
    public boolean isAnimating() {
        return mAnimatorController.isRunning();
    }

    /**
     * Сменить текущее состояние view.
     */
    public boolean toggleState() {
        if (isExpanded()) {
            return collapse();
        } else {
            return expand();
        }
    }

    /**
     * Свернуть view.
     * @return true - если view удалось успешно свернуть,
     * false - если view уже в свернутом состоянии
     */
    public boolean collapse() {
        return changeState(false,
                new HeightAnimatorListener(this, ViewGroup.LayoutParams.WRAP_CONTENT) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // После окончания сворачивания, если текст должен обрезаться - вставляем обрезанную версию текста
                        if (mEllipsizeEnd) {
                            setTextInternal(mEllipsizedContent);
                        }
                        super.onAnimationEnd(animation);
                    }
                });
    }

    /**
     * Развернуть view.
     * @return true - если view удалось успешно развернуть,
     * false - если view уже в развернутом состоянии
     */
    public boolean expand() {
        return changeState(true,
                new HeightAnimatorListener(this, ViewGroup.LayoutParams.WRAP_CONTENT) {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Перед началом разворачивания, если текст обрезается - вставляем полную версию текста
                        if (mEllipsizeEnd) {
                            setTextInternal(mContent);
                        }
                        super.onAnimationStart(animation);
                    }
                });
    }
    // endregion


    // region Callbacks
    /**
     * Обработать изменение состояния view.
     * @param manually  - обновлено состояние в ручную, или нет
     * @param expanded  - новое состояние view
     */
    @CallSuper
    protected void onStateChanged(boolean manually, boolean expanded) {
        if (mOnExpandStateListener != null) {
            mOnExpandStateListener.onExpandStateChanged(manually, expanded);
        }
    }
    // endregion


    // region Internal
    /**
     * Изменить состоянии view с анимацией.
     * @param newExpanded   - новое состояние view
     * @param listener      - слушатель анимации
     * @return true - если состояние удалось изменить, false - если состояние уже такое
     */
    protected boolean changeState(boolean newExpanded, @NonNull Animator.AnimatorListener listener) {
        if (mExpanded != newExpanded) {
            mExpanded = newExpanded;
            if (mWithButton) {
                ValueAnimator animator = buildAnimator(mExpanded ? mExpandedHeight : mCollapsedHeight);
                animator.addListener(listener);
                mAnimatorController.start(animator);
            }
            onStateChanged(true, newExpanded);
            return true;
        }
        return false;
    }

    @NonNull
    protected ValueAnimator buildAnimator(int end) {
        int start = getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(mAnimatorUpdateListener);
        animator.setDuration(calcAnimationDuration(Math.abs(start - end)));
        return animator;
    }

    private int calcAnimationDuration(int deltaPx) {
        switch (mAnimationDurationType) {
            case DURATION_TYPE_FIXED:
                return mAnimationDuration;
            case DURATION_TYPE_PER_100_DP:
                float deltaDp = deltaPx / getResources().getDisplayMetrics().density;
                int duration = (int) (deltaDp / 100 * mAnimationDuration);
                return Math.max(duration, MIN_ANIMATION_DURATION);
        }
        throw new IllegalArgumentException("Unknown animation duration type " + mAnimationDurationType + ".");
    }

    /**
     * Обновить параметр высоты view.
     * @param height - новая высота view
     */
    protected void setLayoutHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    /**
     * Валидировать слушатель кликов.
     */
    protected void validateToggleListener() {
        if (mExpandOnContentClick) {
            if (mExpandOnContentListener == null) {
                // Инициализируем слушатель если нужно
                mExpandOnContentListener = v -> toggleState();
            }
            // Выставляем слушатель
            setOnClickListener(mExpandOnContentListener);
        } else if (mExpandOnContentListener != null) {
            // Удаляем слушатель
            setOnClickListener(null);
            mExpandOnContentListener = null;
        }
    }

    /**
     * Уведомить view об изменении кнопки.
     */
    protected void notifyButtonChanged() {
        requestLayoutIfButtonVisible();
    }

    /**
     * Отменить текущую анимацию.
     */
    protected void cancelAnimation() {
        if (mAnimatorController != null) {
            mAnimatorController.cancelCurrent();
        }
    }

    /**
     * Вызвать обновление view, если отображается кнопка.
     */
    protected void requestLayoutIfButtonVisible() {
        if (mWithButton) {
            // Отменяем текущую анимацию
            cancelAnimation();
            // Обновляем макет только если есть кнопка
            requestLayout();
        }
    }

    /**
     * Задать значение текст без изменения типа буфера.
     * @param text - значение текста
     */
    private void setTextInternal(CharSequence text) {
        mIgnoreContentChanges = true;
        super.setText(text);
        mIgnoreContentChanges = false;
    }

    /**
     * Проверить, находится ли точка внутри кликабельной области кнопки.
     * @param x - абсцисса точки
     * @param y - ордината точки
     * @return true - если кнопка имеет ненулевой размер и точка находит
     * на кнопке или в области расширния кнопки, false - иначе
     */
    protected boolean isInsideButton(float x, float y) {
        return !mButtonRect.isEmpty()
                && x > mButtonRect.left - mButtonExpansion
                && x < mButtonRect.right + mButtonExpansion
                && y > mButtonRect.top - mButtonExpansion
                && y < mButtonRect.bottom + mButtonExpansion;
    }

    /**
     * Вычислить текст, который умещается в view.
     * @param line      - номер последней вмещающейся строки
     * @param width     - ширина, которую может занимать последняя строка
     */
    protected void ellipsizeContent(int line, int width) {
        int lineStart = getLayout().getLineStart(line);
        int lineEnd = getLayout().getLineEnd(line);
        // Пропускаем все переносы строк на конце
        char last = mContent.charAt(lineEnd - 1);
        while (lineEnd > lineStart && (last == '\n' || last == ' ')) {
            --lineEnd;
            last = mContent.charAt(lineEnd - 1);
        }
        mEllipsizedContent.clear();
        mEllipsizedContent.clearSpans();
        mEllipsizedContent.append(mContent, 0, lineEnd).append(ELLIPSIZE_END);
        // Убираем по символу и проверяем, вмещается ли строка вместе с "..."
        float measuredWidth;
        while (lineEnd > lineStart) {
            measuredWidth = getPaint().measureText(mEllipsizedContent, lineStart, lineEnd + ELLIPSIZE_END.length());
            if (measuredWidth <= width) {
                break;
            }
            --lineEnd;
            mEllipsizedContent.replace(lineEnd, lineEnd + 1, StringUtils.EMPTY);
        }
    }

    /**
     * Заблокировать вызовы requestLayout() и invalidate().
     */
    private void lockRequestLayoutAndInvalidate() {
        mLockRequestLayoutAndInvalidate = true;
    }

    /**
     * Разблокировать вызовы requestLayout() и invalidate().
     */
    private void unlockRequestLayoutAndInvalidate() {
        mLockRequestLayoutAndInvalidate =  false;
    }
    // endregion


    @SuppressWarnings("Convert2Lambda")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        if (listener != null) {
            super.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Выполняем обработку клика по кнопке перед вызовом
                    // указанной в listener логики обработки тача
                    if (dispatchOnButtonClick(event)) {
                        return true;
                    }
                    return listener.onTouch(v, event);
                }
            });
        } else {
            super.setOnTouchListener(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dispatchOnButtonClick(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Обработать клик на кнопку.
     * @param event - событие интерфейса
     * @return true - если действие - клик по кнопке, false - иначе
     */
    private boolean dispatchOnButtonClick(MotionEvent event) {
        if (mWithButton) {
            // Проверяем, что за действие и в какой точке
            final int action = event.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                if (isInsideButton(event.getX(), event.getY())) {
                    // Отловили действие по кнопке
                    if (action == MotionEvent.ACTION_UP) {
                        toggleState();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Рендерим весь текст без ограничения строк.
     */
    @SuppressLint("WrongCall")
    protected void onMeasureDefault(int widthMeasureSpec, int heightMeasureSpec) {
        setTextInternal(mContent);
        setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("WrongCall")
    protected void onMeasureWhileAnimating(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            // Если в момент анимации вызывается onMeasure с нефиксированной высотой
            // - это лишний вызов, вычисление размера можно игнорировать
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("WrongCall")
    protected void onMeasureCollapsed(int widthMeasureSpec, int heightMeasureSpec) {
        // Вычисляем высоту в свернутом состоянии
        int collapseMeasureSpec = heightMeasureSpec;
        int buttonHeight = 0;
        if (mButtonType != BUTTON_TYPE_NONE) {
            buttonHeight = getExpandButtonHeight();
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode != MeasureSpec.UNSPECIFIED) {
                // Редактируем measure spec, чтобы оставить часть высоты для кнопки
                collapseMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(collapseMeasureSpec) - buttonHeight, heightMode);
            }
        }
        // Рендерим текст с ограниченным количеством строк
        int lines = mButtonType == BUTTON_TYPE_REPLACE_LAST
                ? mMaxLinesWhenCollapsed - 1
                : mMaxLinesWhenCollapsed;
        setMaxLines(lines);
        super.onMeasure(widthMeasureSpec, collapseMeasureSpec);
        mCollapsedHeight = getMeasuredHeight() + buttonHeight;
        // Убираем ограничение на количество строк, чтобы был виден полный текст при переходе в развернутое состояние
        setMaxLines(Integer.MAX_VALUE);

        // Вычисляем помещающийся текст, если нужно
        if (mEllipsizeEnd) {
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            ellipsizeContent(lines - 1, width);
        }
    }

    @SuppressLint("WrongCall")
    protected void onMeasureExpanded(int widthMeasureSpec, int heightMeasureSpec) {
        int expandMeasureSpec = heightMeasureSpec;
        int buttonHeight = 0;
        if (mButtonType != BUTTON_TYPE_NONE) {
            buttonHeight = getCollapseButtonHeight();
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode != MeasureSpec.UNSPECIFIED) {
                // Редактируем measure spec, чтобы оставить часть высоты для кнопки
                expandMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(expandMeasureSpec) - buttonHeight, heightMode);
            }
        }
        setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, expandMeasureSpec);
        mExpandedHeight = getMeasuredHeight() + buttonHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isAnimating()) {
            onMeasureWhileAnimating(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        lockRequestLayoutAndInvalidate();

        // Рендерим весь текст без ограничения строк
        onMeasureDefault(widthMeasureSpec, heightMeasureSpec);

        // Проверяем необходимость отображения кнопки
        mWithButton = getLineCount() > mMaxLinesWhenCollapsed;

        if (mWithButton) {
            if (mExpanded) {
                onMeasureCollapsed(widthMeasureSpec, heightMeasureSpec);
                onMeasureExpanded(widthMeasureSpec, heightMeasureSpec);
                if (mEllipsizeEnd) {
                    setTextInternal(mContent);
                }
                setMeasuredDimension(getMeasuredWidth(), mExpandedHeight);
            } else {
                onMeasureExpanded(widthMeasureSpec, heightMeasureSpec);
                onMeasureCollapsed(widthMeasureSpec, heightMeasureSpec);
                if (mEllipsizeEnd) {
                    setTextInternal(mEllipsizedContent);
                }
                setMeasuredDimension(getMeasuredWidth(), mCollapsedHeight);
            }
        }

        unlockRequestLayoutAndInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Очищаем границы кнопки
        mButtonRect.setEmpty();

        if (mWithButton && mButtonType != BUTTON_TYPE_NONE) {
            canvas.getClipBounds(mCanvasRect);
            // Вычисляем границы отрисовки кнопки
            int buttonHeight = mExpanded ? getCollapseButtonHeight() : getExpandButtonHeight();
            mButtonRect.left = mCanvasRect.left + getPaddingLeft();
            mButtonRect.right = mCanvasRect.right - getPaddingRight();
            mButtonRect.bottom = mCanvasRect.bottom - getPaddingBottom();
            mButtonRect.top = mButtonRect.bottom - buttonHeight;
            // Отрисовываем кнопку
            drawButton(canvas, mButtonRect);

            // Ограничиваем область отрисовки текста (исключаем область, где отрисована кнопка)
            canvas.clipRect(mCanvasRect.left, mCanvasRect.top, mCanvasRect.right, mButtonRect.top);
        }
        super.onDraw(canvas);
    }

    @Override
    public void requestLayout() {
        if (!mLockRequestLayoutAndInvalidate) {
            super.requestLayout();
        }
    }

    @Override
    public void invalidate() {
        if (!mLockRequestLayoutAndInvalidate) {
            super.invalidate();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.content = mContent;
        savedState.expanded = mExpanded;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mExpanded = savedState.expanded;
        if (savedState.content != null) {
            setText(savedState.content);
        }
    }

    private static class SavedState extends BaseSavedState {
        CharSequence content;
        boolean expanded;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            content = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            expanded = in.readInt() > 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            TextUtils.writeToParcel(content, out, flags);
            out.writeInt(expanded ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}

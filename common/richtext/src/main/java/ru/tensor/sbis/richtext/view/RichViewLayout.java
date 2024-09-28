package ru.tensor.sbis.richtext.view;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Trace;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.collection_view.CollectionView;
import ru.tensor.sbis.objectpool.ObjectPool;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.cfg.BrConfiguration;
import ru.tensor.sbis.richtext.converter.cfg.RichTextGlobalConfiguration;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.util.SpannableStreamBuilder;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.view.strategy.PrefetchStrategy;
import ru.tensor.sbis.richtext.view.strategy.SpannableLineBreakHandler;
import ru.tensor.sbis.richtext.view.strategy.ViewLayout;
import ru.tensor.sbis.richtext.view.strategy.WrapLineStrategy;

/**
 * Layout для отрисовки богатого текста {@link RichTextView} с возможностью добавления в него кастомных View
 *
 * @author am.boldinov
 */
@SuppressWarnings("NullableProblems")
public class RichViewLayout extends ViewGroup implements CollectionView, CloneableRichViewFactory, RichViewParent {

    private RichTextView mRichTextView;

    @Nullable
    private RichViewAdapter mAdapter;
    @Nullable
    private RichWrapLayoutManager mWrapLayoutManager;
    @NonNull
    private final BrConfiguration mLineBreakConfiguration = RichTextGlobalConfiguration.getBrConfiguration();
    @NonNull
    private final SpannableLineBreakHandler mLineBreakHandler = new SpannableLineBreakHandler(this);
    private final int mMaxViewHeight = getContext().getResources().getDimensionPixelSize(R.dimen.richtext_view_max_height);
    private CharSequence mPendingText;
    private int mPreviousMeasureSpecWidth;
    private boolean mDynamicLayoutExperimental;
    @Nullable
    private ObjectPool<RichViewLayout> mRichViewPool;

    public RichViewLayout(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public RichViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RichViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public RichViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        mRichTextView = new RichTextView(getContext(), attrs, defStyleAttr);
        mRichTextView.setId(R.id.richtext_rich_text_view);
        mRichTextView.setLayoutParams(generateDefaultLayoutParams());
        mRichTextView.setSpannableFactory(SpannableStreamBuilder.Factory.getInstance());
        addView(mRichTextView);
    }

    /**
     * Устанавливает стилизованный текст с набором View, который должен быть отрисован
     */
    public void setText(@Nullable CharSequence text) {
        Trace.beginSection("RichLayout setText");
        if (text instanceof Spanned) {
            final Spanned spanned = (Spanned) text;
            final ViewStubSpan[] viewSpans = spanned.getSpans(0, text.length(), ViewStubSpan.class);
            getAdapter().setData(viewSpans);
        } else {
            getAdapter().setData(null);
        }
        mPreviousMeasureSpecWidth = 0;
        if (getAdapter().getItemCount() > 0) {
            mPendingText = text;
            requestLayout();
        } else {
            mPendingText = null;
            mRichTextView.setText(text);
        }
        Trace.endSection();
    }

    /**
     * Устанавливает слушатель на клик по кастомным View внутри компонента, например по изображениям,
     * блокам с текстом, таблицам и т.п
     */
    public void setSingleViewClickListener(@Nullable SingleViewClickListener clickListener) {
        getAdapter().setSingleViewClickListener(clickListener);
    }

    /**
     * Устанавливает пул для переиспользованных вью-холдеров внутри компонента
     */
    public void setRecycledViewPool(@NonNull RecycledViewPool pool) {
        getAdapter().setRecycledViewPool(pool);
    }

    /**
     * Устанавливает пул для получения дочерних компонентов богатого текста (ячеек таблиц, цитат и т.п).
     * Рекомендуется его наполнять перед использованием компонента для более быстрого рендера.
     *
     * @see ru.tensor.sbis.richtext.view.prefetch.PrecomputedRichViewPool
     */
    public void setRichViewPool(@Nullable ObjectPool<RichViewLayout> pool) {
        mRichViewPool = pool;
    }

    /**
     * Отправляет все дочерние вью-холдеры на переиспользование.
     * Рекомендуется использовать в {@link androidx.recyclerview.widget.RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)}.
     */
    public void recycle() {
        Trace.beginSection("RichLayout recycleViews");
        getAdapter().recycleViews();
        Trace.endSection();
    }

    // TODO убрать метод после проверки стабильности решения https://online.sbis.ru/opendoc.html?guid=5135dbfd-49ed-4d37-b1c9-b2d251e7a01f
    public void setDynamicLayoutExperimental(boolean dynamic) {
        mDynamicLayoutExperimental = dynamic;
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (maxWidth == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        if (mPendingText == null || (mPreviousMeasureSpecWidth == maxWidth &&
                (!mDynamicLayoutExperimental || !isViewHolderLayoutRequested()))) {
            Trace.beginSection("RichLayout onMeasureDefault");
            if (mPendingText != null && mRichTextView.getLayout() != null && mRichTextView.getText() != null) {
                measureCompact(new ViewLayout(mRichTextView.getLayout(), mRichTextView.getText(), widthMeasureSpec, heightMeasureSpec));
            } else {
                measureDefault(widthMeasureSpec, heightMeasureSpec);
            }
            Trace.endSection();
            return;
        }
        Trace.beginSection("RichLayout onMeasureFull");
        mPreviousMeasureSpecWidth = maxWidth;
        // перед измерением TextView необходимо измерить все холдеры
        measureViewHolders(widthMeasureSpec, heightMeasureSpec);
        mRichTextView.setText(mPendingText, TextView.BufferType.SPANNABLE);
        // измеряем сразу для получения TextView Layout и его атрибутов
        measureChild(mRichTextView, widthMeasureSpec, heightMeasureSpec);
        final Layout textLayout = mRichTextView.getLayout();
        if (textLayout != null) {
            final ViewLayout layout = new ViewLayout(textLayout, mRichTextView.getText(), widthMeasureSpec, heightMeasureSpec);
            getWrapLayoutManager().wrap(layout);
            mLineBreakHandler.removeEmptyLineBreaks(layout.getText(), layout,
                    mLineBreakConfiguration.getMaxLineBreakCount());
            measureCompact(layout);
        } else {
            measureDefault(widthMeasureSpec, heightMeasureSpec);
        }
        Trace.endSection();
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (child == mRichTextView) {
            final int widthMode = MeasureSpec.getMode(parentWidthMeasureSpec);
            // по умолчанию TextView имеет динамическую ширину WRAP_CONTENT,
            // но если родитель хочет иметь фиксированный размер то поведение должно быть аналогично MATCH_PARENT
            if (widthMode == MeasureSpec.EXACTLY) {
                final int size = Math.max(0, MeasureSpec.getSize(parentWidthMeasureSpec) - getPaddingLeft() - getPaddingRight());
                child.measure(MeasureSpec.makeMeasureSpec(size, widthMode), parentHeightMeasureSpec);
                return;
            }
        }
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    private void measureDefault(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // только TextView, т.к холдеры измеряются в отдельном методе
                if (child == mRichTextView) {
                    final boolean fixMeasuredWidth = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
                            && mRichTextView.getLayout() == null && mRichTextView.getText() instanceof Spanned
                            && SpannableUtil.hasNextSpanTransition((Spanned) mRichTextView.getText(), 0, ReplacementSpan.class);
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    if (fixMeasuredWidth) {
                        // TextView имеет 2 алгоритма расчета ширины, на основе Layout.getDesiredWidthWithLimit и
                        // на основе ранее созданного на предыдущем измерении Layout через getLineWidth.
                        // Иногда они выдают разные результаты (происходит сдвиг offset при построчном измерении из-за \n)
                        // Поэтому для расчета конечного результата необходимо вызвать дважды (баг прослеживается при наличии ReplacementSpan)
                        measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    }
                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftOffset);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topOffset);
            }
        }

        setMeasuredDimensionInternal(maxWidth, maxHeight, widthMeasureSpec, heightMeasureSpec);
    }

    private void measureCompact(@NonNull ViewLayout layout) {
        final int maxWidth = layout.getCompactWidth();
        final int maxHeight = getCompactHeight(layout);
        setMeasuredDimensionInternal(maxWidth, maxHeight, layout.getWidthMeasureSpec(), layout.getHeightMeasureSpec());
        mRichTextView.setMeasuredDimensionProxy(getMeasuredWidth(), getMeasuredHeight());
    }

    @SuppressWarnings("rawtypes")
    private void measureViewHolders(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getAdapter().getItemCount(); i++) {
            final RichViewLayout.ViewHolder viewHolder = getAdapter().getViewHolder(i);
            final View view = viewHolder.view;
            final ViewStubSpan viewSpan = getAdapter().getItemInternal(i);
            final WrapLineStrategy wrapLineStrategy = viewSpan.getWrapLineStrategy();
            if (view.getVisibility() == GONE || wrapLineStrategy == null) {
                view.setVisibility(View.GONE);
                continue;
            }
            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.template = viewSpan.getAttributes().getTemplate();
            final int maxWidth = MeasureSpec.getSize(widthMeasureSpec) - viewSpan.getOptions().getLeadingOffset();
            final int maxHeight;
            final int desiredMaxHeight = viewSpan.getOptions().getMaxHeight();
            if (desiredMaxHeight > 0) {
                maxHeight = Math.min(mMaxViewHeight, desiredMaxHeight);
            } else if (lp.template == ViewTemplate.INLINE_SIZE) {
                maxHeight = Math.min(mMaxViewHeight, (int) getTextView().getTextSize());
            } else {
                maxHeight = mMaxViewHeight;
            }
            //noinspection unchecked
            viewHolder.onPreMeasure(viewSpan.getAttributes(), maxWidth, maxHeight);
            measureChild(view, MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.getMode(widthMeasureSpec)),
                    heightMeasureSpec);
            if (wrapLineStrategy instanceof PrefetchStrategy) {
                ((PrefetchStrategy) wrapLineStrategy).onViewMeasured(view, widthMeasureSpec);
            }
        }
    }

    private int getCompactHeight(@NonNull ViewLayout layout) {
        int height = layout.getCompactHeight();
        final int maxHeight = mRichTextView.getMaxHeight();
        final int maxLines = mRichTextView.getMaxLines();
        int lineCount = layout.getLineCount();
        if (maxHeight != -1) {
            height = Math.min(height, maxHeight);
        } else if (maxLines != -1 && lineCount > maxLines) {
            height = layout.getLineTop(maxLines);
            lineCount = maxLines;
        }
        final int minHeight = mRichTextView.getMinHeight();
        final int minLines = mRichTextView.getMinLines();
        if (minHeight != -1) {
            height = Math.max(height, minHeight);
        } else if (minLines != -1 && lineCount < minLines) {
            height += mRichTextView.getLineHeight() * (minLines - lineCount);
        }
        return height;
    }

    private boolean isViewHolderLayoutRequested() {
        for (int i = 0; i < getAdapter().getItemCount(); i++) {
            final View view = getAdapter().getViewHolder(i).view;
            if (view.isLayoutRequested()) {
                return true;
            }
        }
        return false;
    }

    private void setMeasuredDimensionInternal(int maxWidth, int maxHeight, int widthMeasureSpec, int heightMeasureSpec) {
        maxWidth += getPaddingStart() + getPaddingEnd();
        maxHeight += getPaddingTop() + getPaddingBottom();

        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Trace.beginSection("RichLayout onLayout");
        right = right - left - getPaddingEnd();
        left = getPaddingStart();
        top = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                int childLeft = left + lp.leftOffset;
                if (lp.template == ViewTemplate.CENTER) {
                    childLeft += Math.max((right - childLeft - width) / 2, 0);
                }
                final int childTop = top + lp.topOffset;
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
        Trace.endSection();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Возвращает вложенный компонент для рендера текста
     */
    @NonNull
    public RichTextView getTextView() {
        return mRichTextView;
    }

    @Override
    public void addChildInLayout(@NonNull View view, int position) {
        addViewInLayout(view, position, view.getLayoutParams(), true);
    }

    @NonNull
    @Override
    public RichViewLayout cloneView() {
        RichViewLayout layout = null;
        if (mRichViewPool != null) {
            layout = mRichViewPool.take();
        }
        if (layout == null) {
            layout = new RichViewLayout(getContext());
        }
        if (layout.getLayoutParams() == null) {
            layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        if (layout.mAdapter == null) {
            layout.mAdapter = new RichViewAdapter(getAdapter());
            layout.mAdapter.attachView(layout);
        }
        layout.setDynamicLayoutExperimental(mDynamicLayoutExperimental);
        layout.setRichViewPool(mRichViewPool);
        final TextView textView = layout.getTextView();
        textView.getPaint().set(getTextView().getPaint());
        textView.setTextIsSelectable(getTextView().isTextSelectable());
        textView.setTextColor(getTextView().getTextColors());
        textView.setHighlightColor(getTextView().getHighlightColor());
        textView.setLinkTextColor(getTextView().getLinkTextColors());
        textView.setLinksClickable(getTextView().getLinksClickable());
        return layout;
    }

    @NonNull
    @Override
    public RecycledViewPool getRecycledViewPool() {
        return getAdapter().getRecycledViewPool();
    }

    @NonNull
    @Override
    public CloneableRichViewFactory getRichViewFactory() {
        return this;
    }

    @NonNull
    private RichViewAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new RichViewAdapter();
            mAdapter.attachView(this);
        }
        return mAdapter;
    }

    @NonNull
    private RichWrapLayoutManager getWrapLayoutManager() {
        if (mWrapLayoutManager == null) {
            mWrapLayoutManager = new RichWrapLayoutManager(getAdapter());
        }
        return mWrapLayoutManager;
    }

    /**
     * Параметры размещения дочерних View
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {

        /**
         * Смещение View по оси Y
         */
        public int topOffset;
        /**
         * Смещение View по оси X
         */
        public int leftOffset;
        /**
         * Шаблон обтекания
         */
        ViewTemplate template;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * Слушатель нажатий на дочерние View
     */
    public interface SingleViewClickListener {

        /**
         * Событие о клике по View
         *
         * @param item        вью-модель, которая была установлена во View
         * @param serialIndex порядковый индекс View. Если View находится внутри коллекции, то
         *                    индекс будет расчитываться как сумма индекса внутри компонента и индекса
         *                    внутри коллекции.
         */
        void onViewClick(@NonNull BaseAttributesVM item, int serialIndex);
    }

    /**
     * Фабрика по созданию вью-холдеров для биндинга дочерних View
     */
    public interface ViewHolderFactory {

        /**
         * Создаёт новый экземпляр вью-холдера
         *
         * @param parent компонент, в который View будет добавлена
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        RichViewLayout.ViewHolder createViewHolder(@NonNull RichViewParent parent);

    }

    /**
     * Базовый класс вью-ходера для обтекаемой View богатым текстом.
     */
    public static abstract class ViewHolder<T extends BaseAttributesVM> extends CollectionView.ViewHolder {

        /**
         * Слушатель нажатий на содержимое {@link ViewHolder}
         */
        public interface ItemClickListener {

            /**
             * Вызывается в случае нажатия на View
             *
             * @param position позиция холдера в адаптере {@link #getAdapterPosition()}
             * @param view     ссылка на View, по которой произошел клик
             */
            void onItemClick(int position, @NonNull View view);

            /**
             * Вызывается в случае нажатия на View, которая находится внутри коллекции,
             * например внутри {@link androidx.recyclerview.widget.RecyclerView}
             *
             * @param position    позиция холдера в адаптере {@link #getAdapterPosition()}
             * @param serialIndex позиция View внутри коллекции
             * @param view        ссылка на View, по которой произошел клик
             */
            void onListItemClick(int position, int serialIndex, @NonNull View view);
        }

        @Nullable
        private ItemClickListener itemLickListener;
        @Nullable
        private ItemClickListener itemClickDelegate;

        public ViewHolder(@NonNull View view) {
            super(view);
        }

        /**
         * Привязывает вью-модель с данными ко View
         *
         * @param attributesVM вью-модель, созданная на этапе потоковой обработки текста
         */
        public abstract void bind(@NonNull T attributesVM);

        /**
         * Вызывается перед измерением View.
         * Метод необходим для дополнительного расчета {@link LayoutParams} или биндинга вью-модели,
         * в случае если для биндинга необходимы размеры.
         *
         * @param attributesVM вью-модель
         * @param maxWidth     максимально доступная ширина View
         * @param maxHeight    максимально-рекомендуемая высота для View, актуально для изображений и
         *                     похожих компонентов, которые не должны вытягиваться и всегда быть в поле
         *                     видимости пользователя целиком
         */
        protected void onPreMeasure(@NonNull T attributesVM, int maxWidth, int maxHeight) {

        }

        /**
         * @return слушатель нажатий на содержимое {@link ViewHolder}.
         * @see ItemClickListener
         */
        @NonNull
        protected final ItemClickListener getOnItemClickListener() {
            if (itemClickDelegate == null) {
                itemClickDelegate = new ItemClickListener() {
                    @Override
                    public void onItemClick(int position, @NonNull View view) {
                        if (itemLickListener != null) {
                            itemLickListener.onItemClick(position, view);
                        }
                    }

                    @Override
                    public void onListItemClick(int position, int serialIndex, @NonNull View view) {
                        if (itemLickListener != null) {
                            itemLickListener.onListItemClick(position, serialIndex, view);
                        }
                    }
                };
            }
            return itemClickDelegate;
        }

        /**
         * Устанавливает слушатель нажатий на содержимое {@link ViewHolder}.
         *
         * @param itemClickListener опциональный слушатель, который необходимо занулять
         *                          при перемещении холдера в {@link CollectionView.RecycledViewPool}
         */
        final void setOnItemClickListener(@Nullable ItemClickListener itemClickListener) {
            this.itemLickListener = itemClickListener;
        }
    }
}

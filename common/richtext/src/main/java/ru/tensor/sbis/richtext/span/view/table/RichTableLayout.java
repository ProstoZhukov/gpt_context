package ru.tensor.sbis.richtext.span.view.table;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Trace;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.collection_view.CollectionView;
import ru.tensor.sbis.design.theme.global_variables.FontSize;
import ru.tensor.sbis.design.theme.global_variables.Offset;
import ru.tensor.sbis.design.theme.global_variables.StyleColor;
import ru.tensor.sbis.design.theme.global_variables.TextColor;
import ru.tensor.sbis.design.view_ext.SimplifiedTextView;
import ru.tensor.sbis.jsonconverter.generated.Border;
import ru.tensor.sbis.jsonconverter.generated.CellContentMeasurer;
import ru.tensor.sbis.jsonconverter.generated.Frame;
import ru.tensor.sbis.jsonconverter.generated.Margin;
import ru.tensor.sbis.jsonconverter.generated.Size;
import ru.tensor.sbis.jsonconverter.generated.TableGeometry;
import ru.tensor.sbis.jsonconverter.generated.TableGeometrySettings;
import ru.tensor.sbis.jsonconverter.generated.TablesController;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.span.view.RecycledViewType;
import ru.tensor.sbis.richtext.view.CloneableRichViewFactory;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Layout для отрисовки таблицы, скроллящейся горизонтально, каждой ячейкой которой является богатый текст.
 * Количество ячеек для отображения может быть ограничено при задании конфигурации
 * {@link ru.tensor.sbis.richtext.converter.cfg.TableConfiguration}
 * {@link ru.tensor.sbis.richtext.converter.cfg.TableSize}
 *
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
final class RichTableLayout extends LinearLayout {

    @NonNull
    private final TableScrollLayout mTableScrollLayout;
    @NonNull
    private final CollectionView.RecycledViewPool mViewPool;
    @NonNull
    private final CloneableRichViewFactory mRichViewFactory;
    @Nullable
    private View mExpandButton;
    @Nullable
    private WeakReference<RichTableFullDialog> mFullDialog;

    public RichTableLayout(@NonNull Context context, @NonNull CollectionView.RecycledViewPool viewPool,
                           @NonNull CloneableRichViewFactory richViewFactory) {
        super(context);
        mViewPool = viewPool;
        mRichViewFactory = richViewFactory;
        setId(R.id.richtext_table_view);
        setOrientation(VERTICAL);
        mTableScrollLayout = new TableScrollLayout(getContext(), viewPool, richViewFactory);
        mTableScrollLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mTableScrollLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.EXACTLY
        ); // prevent forceUniformWidth measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Устанавливает вью-модель с данными таблицы для рендера
     */
    public void setViewData(@NonNull TableViewData data, @Nullable TableFullProvider fullData) {
        mTableScrollLayout.mTableLayout.setViewData(data);
        if (data.isShrink() && fullData != null) {
            if (mExpandButton == null) {
                mExpandButton = createExpandButton();
                mExpandButton.setOnClickListener((view) -> showFullTableDialog(fullData));
            }
            if (mExpandButton.getParent() == null) {
                addView(mExpandButton);
            }
            if (fullData.isShowing) {
                showFullTableDialog(fullData);
            }
        } else {
            if (mExpandButton != null && mExpandButton.getParent() != null) {
                removeView(mExpandButton);
            }
        }
    }

    public void recycle() {
        mTableScrollLayout.mTableLayout.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideFullTableDialog();
    }

    private void showFullTableDialog(@NonNull TableFullProvider fullData) {
        final RichTableFullDialog current = mFullDialog != null ? mFullDialog.get() : null;
        if (current == null || !current.isShowing()) {
            final RichTableLayout layout = createFullLayout();
            layout.setViewData(fullData.get(), null);
            final RichTableFullDialog dialog = new RichTableFullDialog(getContext(), layout);
            dialog.setOnCancelListener(d -> { // manual cancel
                fullData.isShowing = false;
                hideFullTableDialog();
            });
            mFullDialog = new WeakReference<>(dialog);
            dialog.show();
            fullData.isShowing = true;
        }
    }

    private void hideFullTableDialog() {
        if (mFullDialog != null) {
            final RichTableFullDialog dialog = mFullDialog.get();
            if (dialog != null) {
                dialog.dismiss();
            }
            mFullDialog.clear();
            mFullDialog = null;
        }
    }

    @NonNull
    private View createExpandButton() {
        final SimplifiedTextView view = new SimplifiedTextView(getContext());
        view.setId(R.id.richtext_table_expand_button);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final int margin = Offset.X2S.getDimenPx(getContext());
        lp.topMargin = margin;
        lp.bottomMargin = margin;
        view.setLayoutParams(lp);
        final int padding = Offset.X3S.getDimenPx(getContext());
        view.setPadding(0, padding, 0, padding);
        view.setText(getResources().getString(R.string.richtext_table_open_fully));
        view.getPaint().setTypeface(TypefaceManager.getRobotoRegularFont(getContext()));
        view.setTextColor(TextColor.LINK.getValue(getContext()));
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSize.XS.getScaleOffDimenPx(getContext()));
        return view;
    }

    @NonNull
    private RichTableLayout createFullLayout() {
        final RichTableLayout layout = new RichTableLayout(getContext(), mViewPool, mRichViewFactory);
        final int padding = getResources().getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.default_content_padding);
        layout.mTableScrollLayout.setPadding(padding, padding, padding, padding);
        layout.mTableScrollLayout.setClipToPadding(false);
        return layout;
    }

    private static final class TableScrollLayout extends HorizontalScrollView {

        @NonNull
        private final TableLayout mTableLayout;
        @NonNull
        private final NestedScrollTouchInterceptor mNestedScrollInterceptor = new NestedScrollTouchInterceptor(this);

        public TableScrollLayout(@NonNull Context context, @NonNull CollectionView.RecycledViewPool viewPool,
                                 @NonNull CloneableRichViewFactory richViewFactory) {
            super(context);
            mTableLayout = new TableLayout(getContext(), viewPool, richViewFactory);
            mTableLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            addView(mTableLayout);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return mNestedScrollInterceptor.onInterceptTouchEvent(ev) && super.onInterceptTouchEvent(ev);
        }

        @Override
        protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                               int parentHeightMeasureSpec, int heightUsed) {
            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
                            + heightUsed, lp.height);
            final int usedTotal = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin +
                    widthUsed;
            final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(0, MeasureSpec.getSize(parentWidthMeasureSpec) - usedTotal),
                    MeasureSpec.UNSPECIFIED);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        @SuppressWarnings("NullableProblems")
        private static final class TableLayout extends ViewGroup implements CollectionView {

            @NonNull
            private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            @NonNull
            private final CellContentMeasurer mCellContentMeasurer = new CellContentMeasurer() {
                @Override
                public int getWidth(int width, short cellOrder) {
                    final CellViewHolder viewHolder = mAdapter.getOrderedViewHolder(cellOrder);
                    if (viewHolder != null) {
                        return viewHolder.getCellSize(width).getWidth();
                    }
                    return 0;
                }

                @Override
                public int getHeight(int width, short cellOrder) {
                    final CellViewHolder viewHolder = mAdapter.getOrderedViewHolder(cellOrder);
                    if (viewHolder != null) {
                        return viewHolder.getCellSize(width).getHeight();
                    }
                    return 0;
                }
            };
            @NonNull
            private final TableGeometrySettings mTableSettings;
            @NonNull
            private final Adapter mAdapter;
            @Nullable
            private TableGeometry mGeometry;

            public TableLayout(Context context, @NonNull RecycledViewPool viewPool,
                               @NonNull CloneableRichViewFactory richViewFactory) {
                super(context);
                setWillNotDraw(false);
                mAdapter = new Adapter(richViewFactory, viewPool);
                mAdapter.attachView(this);
                mBorderPaint.setColor(StyleColor.UNACCENTED.getBorderColor(context));
                mBorderPaint.setStyle(Paint.Style.STROKE);
                mBorderPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.richtext_table_border_size));
                final int cellMargin = getResources().getDimensionPixelSize(R.dimen.richtext_table_cell_margin);
                final int minColumnWidth = getResources().getDimensionPixelSize(R.dimen.richtext_table_min_column_width);
                mTableSettings = new TableGeometrySettings(new Margin(),
                        new Margin(cellMargin, cellMargin, cellMargin, cellMargin),
                        (int) mBorderPaint.getStrokeWidth(), minColumnWidth);
            }

            public void setViewData(@NonNull TableViewData data) {
                mAdapter.setData(data);
            }

            /**
             * Отправляет дочерние View (ячейки таблицы) на переиспользование.
             */
            public void recycle() {
                mAdapter.recycleViews();
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                Trace.beginSection("RichTable onMeasure");
                if (mAdapter.getItemCount() > 0 && mAdapter.mData != null) {
                    invalidateRequestedMeasureCache();
                    final int size = MeasureSpec.getSize(widthMeasureSpec);
                    mGeometry = TablesController.getGeometryPrecomputed(mAdapter.mData.getTable(), mTableSettings, size, mCellContentMeasurer);
                    final Size tableSize = mGeometry.getTableSize();
                    setMeasuredDimension(tableSize.getWidth(), tableSize.getHeight());
                } else {
                    mGeometry = null;
                    setMeasuredDimension(0, 0);
                }
                Trace.endSection();
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                Trace.beginSection("RichTable onLayout");
                if (mGeometry != null) {
                    left = getPaddingStart();
                    top = getPaddingTop();
                    final List<Frame> frames = mGeometry.getFrames();
                    for (int i = 0; i < frames.size(); i++) {
                        final Frame frame = frames.get(i);
                        final View child = getChildAt(i);
                        if (child != null) {
                            final int childLeft = left + frame.getStartPointX();
                            final int childTop = top + frame.getStartPointY();
                            final int childRight = childLeft + frame.getWidth();
                            final int childBottom = childTop + frame.getHeight();
                            child.layout(childLeft, childTop, childRight, childBottom);
                        }
                    }
                }
                Trace.endSection();
            }

            @Override
            public void addChildInLayout(@NonNull View view, int position) {
                addViewInLayout(view, position, view.getLayoutParams(), true);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                drawBorders(canvas);
            }

            private void drawBorders(@NonNull Canvas canvas) {
                if (mGeometry != null) {
                    final List<Border> borders = mGeometry.getBorders();
                    for (Border border : borders) {
                        final int startX = border.getStartPointX();
                        final int stopX = border.getEndPointX();
                        final int startY = border.getStartPointY();
                        final int stopY = border.getEndPointY();
                        canvas.drawLine(startX, startY, stopX, stopY, mBorderPaint);
                    }
                }
            }

            private void invalidateRequestedMeasureCache() {
                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    final CellViewHolder viewHolder = mAdapter.getViewHolder(i);
                    if (viewHolder.view.isLayoutRequested()) { // инвалидируем кеш только у тех, кто запросил новый layout
                        viewHolder.invalidateMeasureCache();
                    }
                }
            }

            private static final class Adapter extends CollectionView.Adapter<CellViewHolder> {

                @NonNull
                private final CloneableRichViewFactory mCloneableViewFactory;
                @Nullable
                private TableViewData mData;

                private Adapter(@NonNull CloneableRichViewFactory richViewFactory, @NonNull RecycledViewPool pool) {
                    mCloneableViewFactory = richViewFactory;
                    pool.setMaxRecycledViews(RecycledViewType.TABLE_CELL, 200);
                    setRecycledViewPool(pool);
                }

                @Override
                public int getItemCount() {
                    return mData != null ? mData.getCellCount() : 0;
                }

                @Override
                protected int getItemViewType(int position) {
                    return RecycledViewType.TABLE_CELL;
                }

                @NonNull
                @Override
                protected CellViewHolder onCreateViewHolder(@NonNull CollectionView parent, int viewType) {
                    Trace.beginSection("RichTable onCreateViewHolder");
                    final CellViewHolder viewHolder = new CellViewHolder(mCloneableViewFactory.cloneView());
                    Trace.endSection();
                    return viewHolder;
                }

                @Override
                protected void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
                    Trace.beginSection("RichTable onBindViewHolder");
                    if (mData != null) {
                        holder.bind(mData.getCell(position));
                    }
                    Trace.endSection();
                }

                @Override
                protected int getStashSize() {
                    return 0;
                }

                @Nullable
                public CellViewHolder getOrderedViewHolder(int cellOrder) {
                    final int position = mData != null ? mData.getCellPosition(cellOrder) : cellOrder;
                    return getViewHolder(position);
                }

                public void clear() {
                    if (mData != null) {
                        mData = null;
                        notifyDataChanged();
                    }
                }

                public void setData(@NonNull TableViewData data) {
                    mData = data;
                    notifyDataChanged();
                }
            }

            private static final class CellViewHolder extends CollectionView.ViewHolder {

                @NonNull
                private final Size mMeasuredSize = new Size();
                private int mLastAvailableWidth;

                public CellViewHolder(@NonNull RichViewLayout view) {
                    super(view);
                }

                @Override
                public void onRecycle() {
                    super.onRecycle();
                    ((RichViewLayout) view).recycle();
                }

                void bind(@NonNull TableCell cell) {
                    invalidateMeasureCache();
                    ((RichViewLayout) view).setText(cell.getContent());
                }

                @NonNull
                Size getCellSize(int availableWidth) {
                    if (mLastAvailableWidth != availableWidth && !isSmallMeasuredWidth(availableWidth)) {
                        Trace.beginSection("RichTable measureCell");
                        measureView(view, availableWidth);
                        mMeasuredSize.setWidth(view.getMeasuredWidth());
                        mMeasuredSize.setHeight(view.getMeasuredHeight());
                        Trace.endSection();
                    }
                    mLastAvailableWidth = availableWidth;
                    return mMeasuredSize;
                }

                void invalidateMeasureCache() {
                    mLastAvailableWidth = 0;
                    mMeasuredSize.setWidth(0);
                    mMeasuredSize.setHeight(0);
                }

                private boolean isSmallMeasuredWidth(int availableWidth) {
                    // если на следующей итерации происходит измерение меньшего значения, а вьюха уже в него вписывается
                    // то повторное измерение не производим
                    return mLastAvailableWidth > availableWidth && mMeasuredSize.getWidth() <= availableWidth;
                }

                private static void measureView(@NonNull View view, int availableWidth) {
                    final int widthSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST);
                    view.measure(widthSpec, View.MeasureSpec.UNSPECIFIED);
                }
            }
        }
    }
}

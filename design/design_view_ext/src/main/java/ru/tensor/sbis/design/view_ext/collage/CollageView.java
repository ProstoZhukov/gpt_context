package ru.tensor.sbis.design.view_ext.collage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.view_ext.R;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "rawtypes", "UnusedAssignment", "JavaDoc"})
public class CollageView extends ViewGroup implements AdapterDataObserver {

    /** @SelfDocumented */
    protected static final int[] NO_SPLITS = new int[0];

    /** Align collage to start of view */
    public static final int GRAVITY_START = -1;

    /** Align collage to middle of view */
    public static final int GRAVITY_MIDDLE = 0;

    /** Align collage to end of view */
    public static final int GRAVITY_END = 1;

    /** Default stash size. Specifies how many view holders (beyond just used)
     * will be stashed instead of removing. */
    private static final int DEFAULT_STASH_SIZE = 5;

    private static final float DEFAULT_DESIRED_ASPECT_RATIO = 1.0f;

    private static final int DEFAULT_DIVIDER_WIDTH = 2;

    private static final int DEFAULT_MAX_HEIGHT = Integer.MAX_VALUE;

    private static final int DEFAULT_GRAVITY = GRAVITY_START;

    /** Desired aspect ratio for collage */
    private float mDesiredAspectRatio = DEFAULT_DESIRED_ASPECT_RATIO;

    /** Divider width in px */
    private int mDividerWidth = DEFAULT_DIVIDER_WIDTH;

    /** Height restriction in px */
    private int mMaxHeight = DEFAULT_MAX_HEIGHT;

    /** Collage gravity */
    private int mGravity = DEFAULT_GRAVITY;

    /** Current configuration of collage.
     * Should be recalculated if some params of collage was changed. */
    private Configuration mConfiguration;

    /** Flag, signals that any params was changed.
     * True if collage configuration is calculated and view can be measured, false otherwise. */
    private boolean mValid = false;

    /** Created ViewHolder's storage. */
    private final SparseArray<ViewHolder> mViewHolders = new SparseArray<>();

    /** Adapter for view.
     * Respond of creating view and binding models. */
    private @Nullable
    Adapter mAdapter;

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray collageAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CollageView,
                0, 0);
        try {
            String desiredAspectRatio = collageAttrs.getString(R.styleable.CollageView_desiredAspectRatio);
            mDesiredAspectRatio = TextUtils.isEmpty(desiredAspectRatio) ? DEFAULT_DESIRED_ASPECT_RATIO : Float.parseFloat(desiredAspectRatio);
            mDividerWidth = collageAttrs.getDimensionPixelSize(R.styleable.CollageView_dividerWidth, mDividerWidth);
            mMaxHeight = collageAttrs.getDimensionPixelSize(R.styleable.CollageView_maxHeight, DEFAULT_MAX_HEIGHT);
            mGravity = collageAttrs.getInt(R.styleable.CollageView_gravity, DEFAULT_GRAVITY);
        } finally {
            collageAttrs.recycle();
        }
    }

    @Nullable
    protected Configuration getConfiguration() {
        return mConfiguration;
    }

    /** @SelfDocumented */
    protected void setConfiguration(@Nullable Configuration configuration) {
        mConfiguration = configuration;
    }

    /** @SelfDocumented */
    protected int getMaxHeight() {
        return mMaxHeight;
    }

    /** @SelfDocumented */
    protected int getGravity() {
        return mGravity;
    }

    private int getGravityOffset() {
        if (mGravity == GRAVITY_START) {
            // Для GRAVITY_START не нужен отступ
            return 0;
        }
        // Вычисляем ширину, относительно которой высчитывался коллаж
        int activeWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
        float relativeOffset;
        if (mGravity == GRAVITY_END) {
            // Отступаем на всю свободную ширину
            relativeOffset = 1 - mConfiguration.getRelativeWidth();
        } else if (mGravity == GRAVITY_MIDDLE) {
            // Отступаем на половину свободной ширины
            relativeOffset = 0.5f - mConfiguration.getRelativeWidth()/2;
        } else {
            throw new IllegalArgumentException("Unknown gravity value!");
        }
        return (int) (activeWidth * relativeOffset);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed || !isValid()) {
            if (mAdapter != null) {
                // Get offset for emulating GRAVITY alignment
                final int gravityOffset = getGravityOffset();
                // Configure necessary views
                final int count = mAdapter.getItemCount();
                for (int i = 0; i < count; ++i) {
                    final RectF bounds = getRectForItem(i);
                    if (bounds != null) {
                        final ViewHolder holder = getViewHolderForPosition(i);
                        final int left = gravityOffset + Math.round(bounds.left);
                        final int right = gravityOffset + Math.round(bounds.right);
                        final int top = Math.round(bounds.top);
                        final int bottom = Math.round(bounds.bottom);
                        holder.view.measure(
                                MeasureSpec.makeMeasureSpec(right-left, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(bottom-top, MeasureSpec.EXACTLY));
                        holder.view.layout(left, top, right, bottom);
                        mAdapter.bindViewHolder(holder, i);
                    }
                }
                // Stash a part of redundant view is exists.
                final int stashSize = getStashSize();
                for (int i = 0; i < stashSize; ++i) {
                    final ViewHolder holder = mViewHolders.get(count + i);
                    if (holder != null) {
                        holder.stash();
                    }
                }
                // Remove views and holders beyond stash.
                final int start = count + stashSize;
                if (start < getChildCount()-1) {
                    for (int i = start; i < getChildCount(); ++i) {
                        final ViewHolder holder = mViewHolders.get(i);
                        if (holder != null) {
                            holder.release();
                            mViewHolders.delete(i);
                        }
                    }
                    removeViewsInLayout(start, getChildCount()-count);
                }
            } else {
                // Remove all view holders
                for (int i = 0; i < mViewHolders.size(); ++i) {
                    final ViewHolder holder = mViewHolders.valueAt(i);
                    if (holder != null) {
                        holder.release();
                    }
                }
                mViewHolders.clear();
                // Remove all views
                removeAllViewsInLayout();
            }
            setValid(true);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int maxWidth = MeasureSpec.getSize(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mAdapter != null) {
            int count = mAdapter.getItemCount();
            AbstractItemModel[] items = new AbstractItemModel[count];
            for (int i = 0; i < count; ++i) {
                items[i] = mAdapter.getItem(i);
            }
            int activeWidth = maxWidth - getPaddingLeft() - getPaddingRight();
            Configuration configuration = tryMeasure(items, activeWidth, 1);
            int occupiedHeight = Math.round(activeWidth / configuration.getAspectRatio());
            int permissibleHeight = mMaxHeight - getPaddingTop() - getPaddingBottom();
            if (occupiedHeight > permissibleHeight) {
                float compressionRate = (float) permissibleHeight / occupiedHeight;
                configuration = tryMeasure(items, activeWidth, (float) permissibleHeight / occupiedHeight);
                height = Math.round(activeWidth * compressionRate / configuration.getAspectRatio());
            } else {
                height = occupiedHeight;
            }
            height += getPaddingTop() + getPaddingBottom();
            // Сохраняем конфигурацию коллажа
            setConfiguration(configuration);
        }

        setMeasuredDimension(maxWidth, height);
    }

    private Configuration tryMeasure(AbstractItemModel[] items, int activeWidth, float relativeWidth) {
        return new Configuration.Calculator(
                mDesiredAspectRatio,
                (float) activeWidth / mMaxHeight,
                (float) mDividerWidth / activeWidth,
                relativeWidth,
                items)
                .calculate();
    }

    /**
     * Obtain a view holder initialized for the given position.
     *
     * @param position Position to obtain a view holder for
     * @return A view holder representing the view at <code>position</code>
     */
    private ViewHolder getViewHolderForPosition(int position) {
        if (mAdapter == null) {
            throw new IllegalStateException("adapter is null");
        }
        if (position < 0 || position >= mAdapter.getItemCount()) {
            throw new IllegalArgumentException("Invalid item position " + position
                    + ". Item count:" + (mAdapter.getItemCount()));
        }
        ViewHolder holder = mViewHolders.get(position);
        final int type = mAdapter.getItemViewType(position);
        boolean useExists = false;
        if (holder != null) {
            if (holder.getViewType() == type) {
                useExists = true;
                holder.prepare();
            } else {
                holder.release();
                removeViewInLayout(holder.view);
            }
        }
        if (!useExists) {
            holder = mAdapter.createViewHolder(this, type);
            addViewInLayout(holder.view, position, generateDefaultLayoutParams(), true);
            mViewHolders.put(position, holder);
        }
        return holder;
    }

    /** Returns the previously set adapter */
    public Adapter getAdapter() {
        return mAdapter;
    }

    /** Set new adapter for collage */
    public void setAdapter(Adapter<? extends ViewHolder> adapter) {
        boolean changed = false;
        if (adapter == null) {
            if (mAdapter != null) {
                changed = true;
            }
        } else if (!adapter.equals(mAdapter)) {
            changed = true;
        }

        if (changed) {
            if (mAdapter != null) {
                mAdapter.unregisterAdapterDataObserver(this);
            }
            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerAdapterDataObserver(this);
            }
            setValid(false);
            requestLayout();
        }
    }

    /** Returns desired value of aspect ratio for collage */
    public float getDesiredRatio() {
        return mDesiredAspectRatio;
    }

    /** Set desired value of aspect ratio for collage */
    public void setDesiredRatio(float desiredRatio) {
        if (mDesiredAspectRatio != desiredRatio) {
            mDesiredAspectRatio = desiredRatio;
            setValid(false);
            requestLayout();
        }
    }

    /** Returns divider width in px */
    public int getDividerWidth() {
        return mDividerWidth;
    }

    /** Set divider width in px */
    public void setDividerWidth(int dividerWidth) {
        if (mDividerWidth != dividerWidth) {
            mDividerWidth = dividerWidth;
            setValid(false);
            requestLayout();
        }
    }

    /** Set value of valid for collage */
    protected void setValid(boolean value) {
        mValid = value;
    }

    /** Returns value of mValid field */
    protected boolean isValid() {
        return mValid;
    }

    /** Returns rect for region specified by index */
    private RectF getRectForItem(int regionIndex) {
        if (mConfiguration == null) {
            return null;
        }
        RectF region = mConfiguration.getRegions()[regionIndex];

        float activeWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        float left = getPaddingLeft() + region.left * activeWidth;
        float right = getPaddingLeft() + region.right * activeWidth;
        float top = getPaddingTop() + region.top * activeWidth;
        float bottom = getPaddingTop() + region.bottom * activeWidth;

        return new RectF(left, top, right, bottom);
    }

    @Override
    public void onDataSetChanged() {
        setValid(false);
        requestLayout();
    }

    /**
     * Specifies how many view holders will be stashes (beyond just used)
     * will be stashed instead of removing.
     *
     * @return A size of stash.
     */
    protected int getStashSize() {
        return DEFAULT_STASH_SIZE;
    }

    /**
     * Base class for an Adapter
     *
     * Adapters provide a binding from an app-specified data set to views that are displayed
     * within a {@link CollageView}.
     */
    public static abstract class Adapter<VH extends ViewHolder> {

        private final Set<AdapterDataObserver> mObservers = new HashSet<>();

        /**
         * Called when CollageView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         */
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * Called by CollageView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#view} to reflect the item at the given
         * position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        protected abstract void onBindViewHolder(VH holder, int position);

        /**
         * This method calls {@link #onCreateViewHolder(ViewGroup, int)} to create a new
         * {@link ViewHolder} and cache viewType in holder's field.
         *
         * @see #onCreateViewHolder(ViewGroup, int)
         */
        public final VH createViewHolder(ViewGroup parent, int viewType) {
            VH holder = onCreateViewHolder(parent, viewType);
            holder.setViewType(viewType);
            return holder;
        }

        /**
         * This method internally calls {@link #onBindViewHolder(ViewHolder, int)} to update the
         * {@link ViewHolder} contents with the item at the given position.
         *
         * @see #onBindViewHolder(ViewHolder, int)
         */
        public final void bindViewHolder(VH holder, int position) {
            onBindViewHolder(holder, position);
        }

        /**
         * Return the view type of the item at <code>position</code>.
         *
         * @param position position to query
         * @return integer value identifying the type of the view needed to represent the item at
         *                 <code>position</code>. Type codes need not be contiguous.
         */
        public int getItemViewType(int position) {
            return 0;
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        public abstract int getItemCount();

        /**
         * Returns the item specified by position.
         *
         * @return A base item specified by position.
         */
        public abstract AbstractItemModel getItem(int position);

        /**
         * Register a new observer to listen for data changes.
         *
         * @param observer Observer to register
         *
         * @see #unregisterAdapterDataObserver(AdapterDataObserver)
         */
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservers.add(observer);
        }

        /**
         * Unregister an observer currently listening for data changes.
         *
         * @param observer Observer to unregister
         *
         * @see #registerAdapterDataObserver(AdapterDataObserver)
         */
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservers.remove(observer);
        }

        /**
         * Notify all registered observers that the data set has changed.
         */
        public final void notifyDataSetChanged() {
            for (AdapterDataObserver observer : mObservers) {
                observer.onDataSetChanged();
            }
        }
    }

    /**
     * A ViewHolder describes an item view.
     */
    public static abstract class ViewHolder {
        public final View view;
        private int viewType;
        private boolean inStash;
        private int beforeStashVisibility;

        public int getViewType() {
            return viewType;
        }

        /** Only for internal calling. */
        void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view may not be null");
            }
            this.view = view;
        }

        /**
         * Release resources. Calls when holder becomes needless.
         * Override if inheritor has resources which should be released.
         */
        @SuppressWarnings("EmptyMethod")
        public void release() {
            // Release resources if need
        }

        /**
         * Stash view. View will be hidden, until it is needed.
         */
        public void stash() {
            if (!inStash) {
                inStash = true;
                beforeStashVisibility = view.getVisibility();
                view.setVisibility(GONE);
            }
        }

        /**
         * Prepare view. View my be stashed.
         */
        public void prepare() {
            if (inStash) {
                inStash = false;
                view.setVisibility(beforeStashVisibility);
            }
        }
    }

    /** @SelfDocumented */
    protected static class Configuration {

        public static final Configuration NO_IMAGE = new Configuration(0, NO_SPLITS, 0, 0);

        private final int length;
        @NonNull
        private final int[] splits;
        private RectF[] regions;
        private final float aspectRatio;
        private final float relativeWidth;

        public Configuration(int length, @NonNull int[] splits, float aspectRatio, float relativeWidth) {
            this.length = length;
            this.splits = splits;
            this.aspectRatio = aspectRatio;
            this.relativeWidth = relativeWidth;
        }

        public RectF[] getRegions() {
            return regions;
        }

        public void setRegions(RectF[] regions) {
            this.regions = regions;
        }

        public float getAspectRatio() {
            return aspectRatio;
        }

        public int getFirstInRow(int row) {
            return row == 0 ? 0 : splits[row-1] + 1;
        }

        public int getLastInRow(int row) {
            return row == splits.length ? length-1 : splits[row];
        }

        public float getRelativeWidth() {
            return relativeWidth;
        }

        @SuppressWarnings({"FieldCanBeLocal", "ManualArrayCopy", "FieldMayBeFinal"})
        private static class Calculator {
            private static final float APPROPRIATE_RATE = 1.0f;
            private static final float INAPPROPRIATE_RATE = 0.4f;

            /** Params of calculation */
            private final int imageCount;
            private final float desiredRatio;
            private final float worstRatio;
            private final float divider;
            private final float relativeWidth;
            private AbstractItemModel[] items;

            /** Variables */
            private final float[] ratios;
            private final float[] integral;
            private float[] heights;
            private int rowsCount;

            public Calculator(float desiredRatio, float worstRatio, float divider, float relativeWidth, @NonNull AbstractItemModel[] items) {
                this.desiredRatio = desiredRatio;
                this.worstRatio = worstRatio;
                this.divider = divider;
                this.relativeWidth = relativeWidth;
                this.imageCount = items.length;
                this.items = items;
                ratios = calcRatios(this.items);
                integral = calcIntegral(ratios);
            }

            /** Calculate collage configuration */
            public Configuration calculate() {
                Configuration configuration = calcPartition();
                configuration.regions = calcRegions(configuration);
                return configuration;
            }

            private Configuration calcPartition() {
                if (imageCount == 0) {
                    return NO_IMAGE;
                }

                // Initialize auxiliary variables
                final float amount = integral[imageCount-1];

                // Best result
                int[] bestSplit = new int[0];
                float bestRatio = amount;
                float[] bestHeights = new float[] { 1 / bestRatio };

                final float[] preferred = new float[imageCount-1]; // Preferred points for split
                final int[] selected = new int[imageCount-1]; // Selected points for split
                final float[] heights = new float[imageCount]; // Heights of current configuration

                //Recalculate divider
                final float divider = this.divider / relativeWidth;
                // Bust of possible splits
                for (int rows = 2; rows < imageCount+1; ++rows) {
                    // Calculate preferred points
                    for (int i = 0; i < rows-1; ++i) {
                        preferred[i] = amount / rows * (i+1);
                    }

                    // Select points
                    int start = 0;
                    for (int i = 0; i < rows-1; ++i) {
                        // From 'start' cause of no sense to select point earlier then already selected
                        // To 'imageCount-1' cause of no sense to select last point (no images later)
                        for (int k = start; k < imageCount-1; ++k) {
                            final float oldDelta = Math.abs(integral[selected[i]] - preferred[i]);
                            final float newDelta = Math.abs(integral[k] - preferred[i]);
                            if (newDelta < oldDelta) {
                                selected[i] = k;
                                start = k+1;
                            }
                        }
                    }

                    // Calculate aspect ratio (width = 1)
                    float freeWidth = Math.max(1 - divider * selected[0], 0);
                    float height = 0;
                    heights[0] = freeWidth / integral[selected[0]];
                    for (int i = 1; i < rows-1; ++i) {
                        freeWidth = 1 - divider * (selected[i]-selected[i-1]-1);
                        heights[i] = freeWidth / (integral[selected[i]] - integral[selected[i-1]]);
                    }
                    freeWidth = 1 - divider * (imageCount - selected[rows-2] - 2);
                    heights[rows-1] = freeWidth / (amount - integral[selected[rows-2]]);
                    for (int i = 0; i < rows; ++i) {
                        height += heights[i];
                    }
                    height += divider * (rows-1);
                    final float ratio = 1 / height;

                    // Remember if better
                    float appropriateRate /* Less if ratio beforehand BAD and current best is NOT BAD */
                            = ratio < worstRatio && bestRatio >= worstRatio ? INAPPROPRIATE_RATE : APPROPRIATE_RATE;
                    if (dispersion(ratio, desiredRatio) / appropriateRate < dispersion(bestRatio, desiredRatio)) {
                        bestRatio = ratio;
                        bestSplit = new int[rows-1];
                        for (int i = 0; i < rows-1; ++i) {
                            bestSplit[i] = selected[i];
                        }
                        bestHeights = new float[rows];
                        for (int i = 0; i < rows; ++i) {
                            bestHeights[i] = heights[i];
                        }
                    }
                }
                this.rowsCount = bestHeights.length;
                this.heights = bestHeights;

                return new Configuration(imageCount, bestSplit, bestRatio, relativeWidth);
            }

            private RectF[] calcRegions(Configuration configuration) {
                if (imageCount == 0) {
                    return new RectF[0];
                }
                RectF[] regions = new RectF[imageCount];
                float bottom = 0;
                for (int row = 0; row < rowsCount; ++row) {
                    float height = heights[row] * relativeWidth;
                    calcRegionsForRow(configuration, bottom, bottom+height, row, regions);
                    bottom += height + divider;
                }
                return regions;
            }

            private void calcRegionsForRow(Configuration configuration, float top, float bottom, int row, RectF[] regions) {
                final int begin = configuration.getFirstInRow(row);
                final int end = configuration.getLastInRow(row);
                float left = 0;
                float amount = 0;

                for (int i = begin; i <= end; ++i) {
                    amount += ratios[i];
                }

                float freeWidth = Math.max(relativeWidth - divider * (end - begin), 0);

                for (int i = begin; i <= end; ++i) {
                    final float right = left + ratios[i] / amount * freeWidth;
                    regions[i] = new RectF(left, top, right, bottom);
                    left = right + divider;
                }
            }

            /** Calculate ratios array */
            private static float[] calcRatios(AbstractItemModel[] items) {
                final int length = items.length;
                final float[] ratios = new float[length];
                for (int i = 0; i < length; ++i) {
                    ratios[i] = (float) items[i].getWidth() / items[i].getHeight();
                }
                return ratios;
            }

            /** Calculate integral array with dividers */
            private static float[] calcIntegral(float[] values) {
                final float[] integral = new float[values.length];
                float left = 0;
                for (int i = 0; i < values.length; ++i) {
                    integral[i] = left + values[i];
                    left = integral[i];
                }
                return integral;
            }

            /** Similarity measure of two values */
            private static float dispersion(float a, float b) {
                return a > b ? Math.abs(1 - b / a) : Math.abs(1 - a / b);
            }

        }
    }

}

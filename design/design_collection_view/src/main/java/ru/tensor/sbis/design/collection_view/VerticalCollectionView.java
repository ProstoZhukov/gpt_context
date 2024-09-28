package ru.tensor.sbis.design.collection_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Базовая реализация для вертикальной коллекции элементов.
 * @param <VH>      - тип вью-холдеров
 * @param <ADAPTER> - тип адаптера
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "NullableProblems", "JavaDoc"})
public abstract class VerticalCollectionView<VH extends CollectionView.ViewHolder, ADAPTER extends CollectionView.Adapter<VH>>
        extends LinearLayout implements CollectionView {

    /**
     * Адаптер для работы с элементами коллекции.
     */
    private ADAPTER mAdapter;

    /** @SelfDocumented */
    public VerticalCollectionView(Context context) {
        this(context, null);
    }

    /** @SelfDocumented */
    public VerticalCollectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /** @SelfDocumented */
    public VerticalCollectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        if (isInEditMode()) {
            mockInEditor();
        }
    }

    /** @SelfDocumented */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalCollectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @CallSuper
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        setOrientation(VERTICAL);
        mAdapter = createAdapter(context, attrs);
        mAdapter.attachView(this);
    }

    protected void mockInEditor() {
        // override this method to mock data in editor mode
    }

    /** @SelfDocumented */
    @NonNull
    public ADAPTER getAdapter() {
        return mAdapter;
    }

    /**
     * Создать адаптер для элементов коллекции.
     */
    @NonNull
    protected abstract ADAPTER createAdapter(@NonNull Context context, @Nullable AttributeSet attrs);

}

package ru.tensor.sbis.design.list_utils;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.text_span.SimpleInformationView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * List view implementation, uses {@link SimpleInformationView} as InformationView.
 */
@SuppressWarnings("unused")
public class SbisListView extends AbstractListView<SimpleInformationView, SimpleInformationView.Content> {

    public SbisListView(@NonNull Context context) {
        super(context);
    }

    public SbisListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SbisListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    protected SimpleInformationView createInformationView(@NonNull ViewGroup container) {
        return new SimpleInformationView(getContext());
    }

    @Override
    protected void applyInformationViewData(@NonNull SimpleInformationView informationView, @Nullable SimpleInformationView.Content content) {
        informationView.fillData(content);
    }

    @Override
    protected void setEmptyViewTextColor(@NonNull SimpleInformationView informationView, @ColorInt int color) {
        informationView.setTextColor(color);
    }
}

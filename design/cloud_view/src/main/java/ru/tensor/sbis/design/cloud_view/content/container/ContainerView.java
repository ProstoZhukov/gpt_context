package ru.tensor.sbis.design.cloud_view.content.container;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.design.cloud_view.R;
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView;
import ru.tensor.sbis.design.cloud_view.model.CloudContent;
import ru.tensor.sbis.design.cloud_view.model.CloudViewData;

/**
 * Контейнер для вложенного содержимого сообщения
 *
 * @author ma.kolpakov
 */
public class ContainerView extends MessageBlockView {

    public ContainerView(@NonNull Context context) {
        this(context, null);
    }

    public ContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setMessage(@NonNull CloudViewData cloudViewData, boolean isOutcome) {
        mCloudViewData = cloudViewData;
        mDisabled = cloudViewData.isDisabledStyle();
    }

    @Override
    public void setMessageEntitiesList(@NonNull List<CloudContent> contentList,
                                       @NonNull List<Integer> childIndexes,
                                       @Px int messageWidth) {
        super.setMessageEntitiesList(contentList, childIndexes, messageWidth);
    }
}

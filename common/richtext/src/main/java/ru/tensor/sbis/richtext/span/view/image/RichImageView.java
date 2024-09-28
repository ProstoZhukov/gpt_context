package ru.tensor.sbis.richtext.span.view.image;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import androidx.annotation.Nullable;

import ru.tensor.sbis.design.theme.ThemeTokensProvider;

/**
 * @author am.boldinov
 */
public final class RichImageView extends SimpleDraweeView {

    public RichImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public RichImageView(Context context) {
        super(context);
        init();
    }

    public RichImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RichImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        getHierarchy().setPlaceholderImage(
                new ColorDrawable(
                        ThemeTokensProvider.INSTANCE.getColorInt(
                                getContext(),
                                ru.tensor.sbis.design.R.attr.paleColor
                        )
                )
        );
    }

    public void setImageUrl(@Nullable String url) {
        setImageUrl(url, 0, 0);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setImageUrl(@Nullable String url, int resizeWidth, int resizeHeight) {
        if (url == null) {
            setImageURI((Uri) null);
            return;
        }
        final ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true);
        if (resizeWidth > 0 && resizeHeight > 0) {
            requestBuilder.setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight));
        }
        final AbstractDraweeController newController = getControllerBuilder()
                .setOldController(getController())
                .setImageRequest(requestBuilder.build())
                .setRetainImageOnFailure(true)
                .setAutoPlayAnimations(true)
                .build();
        setController(newController);
    }
}

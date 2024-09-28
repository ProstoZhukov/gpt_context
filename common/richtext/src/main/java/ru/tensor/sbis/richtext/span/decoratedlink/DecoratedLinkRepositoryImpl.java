package ru.tensor.sbis.richtext.span.decoratedlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import android.os.Trace;
import android.util.LruCache;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.RoundedColorDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.common.util.PreviewerUrlUtil;
import ru.tensor.sbis.common.util.TextFormatUtils;
import ru.tensor.sbis.richtext.RichTextPlugin;
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedImageStyle;
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository;
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl;
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType;
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview;
import ru.tensor.sbis.toolbox_decl.linkopener.service.Subscription;
import timber.log.Timber;

/**
 * Используемая по умолчанию реализация репозитория для декорации ссылок
 *
 * @author am.boldinov
 */
@SuppressLint("CheckResult")
public final class DecoratedLinkRepositoryImpl implements DecoratedLinkRepository {

    private static final int CACHED_EVENT_MAX_SIZE = 200;

    @NonNull
    private final Context mContext;
    @NonNull
    private final DecoratedImageStyle mImageStyle;
    @Nullable
    private final LinkDecoratorServiceRepository mServiceRepository;
    @NonNull
    private final Map<String, Set<DataRefreshCallback>> mCallbackStore = new HashMap<>();
    @NonNull
    private final LruCache<String, DecoratedLinkData> mCachedEventLinkDataStore = new LruCache<>(CACHED_EVENT_MAX_SIZE);
    @NonNull
    private final PublishSubject<String> mDecorationServiceSubject = PublishSubject.create();
    @Nullable
    private volatile Subscription mSubscription;
    @Nullable
    private PipelineDraweeControllerBuilder mDraweeControllerBuilder;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DecoratedLinkRepositoryImpl(@NonNull Context context, @Nullable LinkDecoratorServiceRepository serviceRepository) {
        mContext = context;
        mServiceRepository = serviceRepository;
        mImageStyle = new DecoratedImageStyle(RichTextPlugin.themedContext(context));
        mDecorationServiceSubject.observeOn(Schedulers.io())
                .map(url -> getLinkDataInternal(url, true))
                .filter(DecoratedLinkData::isFullyLoaded)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(linkData -> {
                    synchronized (mCallbackStore) {
                        final Set<DataRefreshCallback> callbackSet = mCallbackStore.get(linkData.getSourceUrl());
                        if (callbackSet != null) {
                            for (DataRefreshCallback callback : callbackSet) {
                                callback.onRefresh(linkData);
                            }
                        }
                    }
                });
    }

    @Override
    public void attachDataRefreshCallback(@NonNull String url, @NonNull DataRefreshCallback dataRefreshCallback, boolean checkMissedEvent) {
        synchronized (mCallbackStore) {
            Set<DataRefreshCallback> callbackSet = mCallbackStore.get(url);
            if (callbackSet == null) {
                callbackSet = new HashSet<>();
                mCallbackStore.put(url, callbackSet);
            }
            callbackSet.add(dataRefreshCallback);
        }
        if (checkMissedEvent) {
            final DecoratedLinkData linkData = mCachedEventLinkDataStore.get(url);
            if (linkData != null) {
                dataRefreshCallback.onRefresh(linkData);
            } else if (mCachedEventLinkDataStore.size() == mCachedEventLinkDataStore.maxSize()) {
                // если кеш переполнен и данные в нем отсутствуют, необходимо их актуализировать и положить наверх, вытеснив более старые
                mDecorationServiceSubject.onNext(url);
            }
        }
    }

    @Override
    public void detachDataRefreshCallback(@NonNull String url, @NonNull DataRefreshCallback dataRefreshCallback) {
        synchronized (mCallbackStore) {
            final Set<DataRefreshCallback> callbackSet = mCallbackStore.get(url);
            if (callbackSet != null) {
                callbackSet.remove(dataRefreshCallback);
            }
        }
    }

    @WorkerThread
    private void subscribeOnDecorationService() {
        // держим подписку на все время жизни класса
        if (mSubscription == null && mServiceRepository != null) {
            synchronized (this) {
                if (mSubscription == null) {
                    mSubscription = mServiceRepository.subscribe(
                            new DecorationRefreshCallback(mDecorationServiceSubject)
                    );
                }
            }
        }
    }

    @WorkerThread
    @NonNull
    @Override
    public DecoratedLinkData getLinkData(@NonNull String url) {
        return getLinkDataInternal(url, false);
    }

    @NonNull
    @Override
    public DecoratedLinkData getInlineLinkData(@NonNull String url, @NonNull String decorationJson) {
        String imageUrl = null;
        String title = null;
        final int imageSize = mImageStyle.getSmall().getSize();
        final float imageCornerRadius = mImageStyle.getSmall().getCornerRadius();
        if (!decorationJson.isEmpty()) {
            try {
                final JSONObject data = new JSONObject(decorationJson);
                title = data.optString("title");
                final JSONObject logo = data.optJSONObject("logo");
                if (logo != null) {
                    imageUrl = PreviewerUrlUtil.formatImageUrl(logo.optString("value"), imageSize,
                            imageSize, PreviewerUrlUtil.ScaleMode.CROP);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        // если в исходном json нет изображения или заголовка - попытаемся достать из микросервиса
        if (CommonUtils.isEmpty(title) || imageUrl == null) {
            final LinkPreview linkPreview = loadLinkPreview(url);
            if (linkPreview != null) {
                if (CommonUtils.isEmpty(title)) {
                    title = TextFormatUtils.prepareEllipsize(linkPreview.getTitle().trim());
                }
                if (imageUrl == null) {
                    imageUrl = PreviewerUrlUtil.formatImageUrl(linkPreview.getImage(), imageSize,
                            imageSize, PreviewerUrlUtil.ScaleMode.CROP);
                }
            }
        }
        if (CommonUtils.isEmpty(title)) {
            title = url; // если информация по ссылке отсутствует, то отображается сама ссылка
        }
        //noinspection rawtypes
        final DraweeHolder draweeHolder = getImageHolder(imageUrl, imageCornerRadius);
        return new DecoratedLinkData(createDefaultLinkPreview(url), draweeHolder, title, null,
                null, null, UrlType.DEFAULT, imageSize, imageSize, true);
    }

    @WorkerThread
    @NonNull
    private DecoratedLinkData getLinkDataInternal(@NonNull String url, boolean fromEvent) {
        ensureWorkerThread();
        Trace.beginSection("Load decorated link data");
        final int imageSize = mImageStyle.getMedium().getSize();
        final float imageCornerRadius = mImageStyle.getMedium().getCornerRadius();
        String title = null;
        String subtitle = null;
        String imageUrl = null;
        String details = null;
        String date = null;
        boolean isFullyLoaded = false;
        subscribeOnDecorationService();
        LinkPreview linkPreview = loadLinkPreview(url);
        if (linkPreview != null) {
            isFullyLoaded = true;
            title = TextFormatUtils.prepareEllipsize(linkPreview.getTitle().trim());
            subtitle = StringUtils.trimToNull(TextFormatUtils.prepareEllipsize(linkPreview.getSubtitle()));
            details = StringUtils.trimToNull(TextFormatUtils.prepareEllipsize(linkPreview.getDetails()));
            date = StringUtils.trimToNull(TextFormatUtils.prepareEllipsize(linkPreview.getDate()));
            imageUrl = PreviewerUrlUtil.formatImageUrl(linkPreview.getImage(), imageSize, imageSize);
        } else {
            linkPreview = createDefaultLinkPreview(url);
        }
        if (CommonUtils.isEmpty(title)) {
            title = url; // если информация по ссылке отсутствует, то отображается сама ссылка
        }
        @SuppressWarnings("rawtypes") final DraweeHolder draweeHolder = getImageHolder(imageUrl, imageCornerRadius);
        final UrlType urlType = UrlType.fromValue(linkPreview.getUrlType());
        final DecoratedLinkData linkData = new DecoratedLinkData(linkPreview, draweeHolder,
                title, subtitle, details, date, urlType, imageSize, imageSize, isFullyLoaded);
        putLinkDataToEventCache(linkData, fromEvent); // актуализируем информацию по linkData в кеше событий
        Trace.endSection();
        return linkData;
    }

    @Nullable
    private LinkPreview loadLinkPreview(@NonNull String url) {
        if (mServiceRepository != null) {
            Trace.beginSection("getDecoratedLinkWithoutDetection");
            final LinkPreview preview = mServiceRepository.getDecoratedLinkWithoutDetection(url);
            Trace.endSection();
            return preview;
        } else {
            CommonUtils.handleException(new IllegalStateException("Failed to load LinkPreview, " +
                    "LinkDecoratorServiceRepository dependency not found, " +
                    "please provide this Feature in module plugin or disable link decoration in RenderOptions"));
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    @NonNull
    private DraweeHolder getImageHolder(@Nullable String imageUrl, float cornerRadius) {
        final Drawable placeholder = new RoundedColorDrawable(cornerRadius, mImageStyle.getPlaceholderBackgroundColor()) {

            private final IconicsDrawable icon = new IconicsDrawable(mContext, mImageStyle.getPlaceholderIcon())
                    .color(mImageStyle.getPlaceholderColor());

            @Override
            protected void onBoundsChange(Rect bounds) {
                super.onBoundsChange(bounds);
                icon.sizePx((int) (bounds.height() * 0.6))
                        .iconOffsetXPx((bounds.width() - icon.getBounds().width()) / 2) // center x
                        .iconOffsetYPx((bounds.height() - icon.getBounds().height()) / 2); // center y
            }

            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                icon.draw(canvas);
            }
        };
        final RoundingParams roundingParams = new RoundingParams();
        roundingParams.setCornersRadius(cornerRadius);
        final DraweeHierarchy draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(mContext.getResources())
                .setPlaceholderImage(placeholder)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setRoundingParams(roundingParams)
                .setFadeDuration(0)
                .build();
        DraweeHolder draweeHolder = DraweeHolder.create(draweeHierarchy, mContext);
        if (mDraweeControllerBuilder == null) {
            mDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        }
        final DraweeController controller = mDraweeControllerBuilder
                .setUri(imageUrl)
                .build();
        draweeHolder.setController(controller);
        return draweeHolder;
    }

    private void putLinkDataToEventCache(@NonNull DecoratedLinkData linkData, boolean fromEvent) {
        final DecoratedLinkData cached = mCachedEventLinkDataStore.get(linkData.getSourceUrl());
        if (cached != null || linkData.isFullyLoaded() && fromEvent) {
            mCachedEventLinkDataStore.put(linkData.getSourceUrl(), linkData);
        }
    }

    private static void ensureWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Method must be called on the worker thread");
        }
    }

    @NonNull
    private static LinkPreview createDefaultLinkPreview(@NonNull String url) {
        final LinkPreviewImpl preview = new LinkPreviewImpl();
        preview.setHref(url);
        preview.setDocType(DocType.UNKNOWN);
        preview.setUrlType(UrlType.DEFAULT.getValue());
        return preview;
    }

    private static final class DecorationRefreshCallback implements LinkDecoratorServiceRepository.DataRefreshedCallback {

        @NonNull
        private final WeakReference<Observer<String>> mUrlObserver;

        DecorationRefreshCallback(@NonNull Observer<String> urlObserver) {
            mUrlObserver = new WeakReference<>(urlObserver);
        }

        @Override
        public void onEvent(@NonNull LinkPreview params) {
            final String targetUrl = params.getHref();

            final Observer<String> observer = mUrlObserver.get();
            if (observer != null) {
                observer.onNext(targetUrl);
            }
        }
    }
}

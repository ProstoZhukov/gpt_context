package ru.tensor.sbis.common_views.document

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.image.QualityInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.mikepenz.iconics.IconicsDrawable
import me.relex.photodraweeview.IAttacher
import me.relex.photodraweeview.PhotoDraweeView
import ru.tensor.sbis.common.exceptions.ServiceUnavailableException
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common_views.OnSingleTapListener
import ru.tensor.sbis.common_views.ProgressBarVisibilitySwitcher
import ru.tensor.sbis.common_views.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.stubview.*
import ru.tensor.sbis.design.view_ext.SbisProgressBar
import ru.tensor.sbis.frescoutils.FrescoCache
import java.net.UnknownHostException
import kotlin.math.min
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.stubview.R as RStubView

private const val CHANGE_BG_COLOR_ANIM_DURATION = 300L
private const val MAX_ZOOM_FACTOR = 5f
private const val PROGRESS_DELAY_MILLIS = 400

/**
 * Класс view, отвечающей за загрузку и отображение превью документа
 *
 * @property onSingleTapListener            Слушатель одиночного нажатия на данную view
 * @property loadPreviewListener            Слушатель событий загрузки превью
 * @property progressBarVisibilitySwitcher  Переключатель видимости прогресс бара, являющегося индикатором загрузки превью
 * @property autoPlayAnimations             Автоматически воспроизводить анимированные превью
 * @property zoomToBorders                  Принудительно зуммировать изображения до краёв данной view после загрузки
 *
 * @author sa.nikitin
 */
@SuppressLint("ClickableViewAccessibility")
class DocumentPreviewView : FrameLayout, ProgressBarVisibilitySwitcher {

    var onSingleTapListener: OnSingleTapListener? = null
    var loadPreviewListener: LoadImageListener<ImageInfo>? = null
    var progressBarVisibilitySwitcher: ProgressBarVisibilitySwitcher = this
        set(value) {
            field = value
            if (value !== this) {
                switchProgressBarVisibility(false)
            }
        }
    var autoPlayAnimations: Boolean = true
    var zoomToBorders: Boolean = false

    private lateinit var previewView: PhotoDraweeView
    private lateinit var progressBar: SbisProgressBar
    private lateinit var stubView: StubView

    private var currentBackgroundColor: Int = ContextCompat.getColor(context, android.R.color.transparent)
    private var measured: Boolean = false
    private var needProcessImageInfo: Boolean = false
    private var previewUri: String? = null
    private var imageInfo: ImageInfo? = null

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                onSingleTapListener?.onSingleTap(this@DocumentPreviewView)
                return false
            }
        })

    //region constructor
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        createViews()
        addViews()
        setBackgroundColor(currentBackgroundColor)
    }

    private fun createViews() {
        previewView = PhotoDraweeView(context)
        previewView.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
        progressBar = SbisProgressBar(context)
        progressBar.setShowingDelay(PROGRESS_DELAY_MILLIS)
        progressBar.setDelayedShowing(true)
        stubView = StubView(context)
    }

    private fun addViews() {
        addView(previewView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(progressBar, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
        addView(stubView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }
    //endregion

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        needProcessImageInfo = true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        measured = true
        processImageInfoIfNeed()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (stubView.visibility == View.VISIBLE) {
            if (!stubView.dispatchTouchEvent(event)) {
                gestureDetector.onTouchEvent(event)
            }
        } else {
            previewView.dispatchTouchEvent(event)
            gestureDetector.onTouchEvent(event)
        }
        return true
    }

    private fun changeBackgroundColor(@ColorRes newColorRes: Int) {
        val colorTo: Int = ContextCompat.getColor(context, newColorRes)
        if (currentBackgroundColor != colorTo) {
            val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), currentBackgroundColor, colorTo)
            colorAnimator.duration = CHANGE_BG_COLOR_ANIM_DURATION
            colorAnimator.addUpdateListener { animator -> setBackgroundColor(animator.animatedValue as Int) }
            colorAnimator.start()
        }
    }

    /**
     * Метод загрузки превью документа по [previewUri]
     *
     * @param previewUri        URI в виде строки, ссылающийся на превью документа
     * @param lowResPreviewUri  URI в виде строки, ссылающийся на превью документа низкого качества
     */
    fun loadPreview(previewUri: String, lowResPreviewUri: String? = null) {
        this.previewUri = previewUri
        val parsedPreviewUri = FileUriUtil.parseUri(previewUri)
        if (imageInfo == null) {
            onLoadingStarted(parsedPreviewUri)
        } else {
            imageInfo = null
        }
        previewView.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(
                ImageRequestBuilder.newBuilderWithSource(parsedPreviewUri)
                    .setProgressiveRenderingEnabled(lowResPreviewUri.isNullOrEmpty())
                    .build()
            )
            .setLowResImageRequest(
                if (lowResPreviewUri == null) null else ImageRequest.fromUri(FileUriUtil.parseUri(lowResPreviewUri))
            )
            .setRetainImageOnFailure(true)
            .setAutoPlayAnimations(autoPlayAnimations)
            .setOldController(previewView.controller)
            .setControllerListener(object : BaseControllerListener<ImageInfo>() {

                private var atLeastOneImageLoaded = false

                override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                    super.onIntermediateImageSet(id, imageInfo)
                    atLeastOneImageLoaded = true
                    previewView.setOnTouchListener(null)
                    onLoadingSuccess(imageInfo)
                    progressBarVisibilitySwitcher.changeProgressBarAlpha(0.5f)
                }

                override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                    super.onFinalImageSet(id, imageInfo, animatable)
                    loadPreviewListener?.onImageSuccessLoad(imageInfo)
                    atLeastOneImageLoaded = true
                    previewView.setOnTouchListener(previewView.attacher)
                    onLoadingSuccess(imageInfo)
                    hideProgressBar()
                }

                override fun onFailure(id: String?, throwable: Throwable) {
                    super.onFailure(id, throwable)
                    if (atLeastOneImageLoaded) {
                        previewView.setOnTouchListener(previewView.attacher)
                    } else {
                        onLoadingError(throwable)
                    }
                    hideProgressBar()
                }
            })
            .build()
    }

    /**
     * Метод загрузки превью документа по [previewBitmap]
     *
     * @param previewBitmap     Bitmap превью документа
     */
    @Suppress("DEPRECATION")
    fun loadPreview(previewBitmap: Bitmap?) {
        previewBitmap?.let {
            previewView.hierarchy.setImage(BitmapDrawable(resources, it), 1f, true)
            previewView.setOnTouchListener(previewView.attacher)
            loadPreviewListener?.onImageSuccessLoad(null)
            onLoadingSuccess(object : ImageInfo {
                override fun getWidth(): Int = it.width

                override fun getHeight(): Int = it.height

                override fun getQualityInfo(): QualityInfo? = null

                override fun getExtras(): MutableMap<String, Any> = mutableMapOf()
            })
            hideProgressBar()
        }
    }

    private fun onLoadingStarted(previewUri: Uri) {
        stubView.visibility = View.GONE
        if (FrescoCache.isCached(previewUri)) {
            hideProgressBar()
        } else {
            showProgressBar()
        }
        previewView.visibility = View.VISIBLE
        previewView.setOnTouchListener(null)
    }

    private fun onLoadingSuccess(imageInfo: ImageInfo?) {
        changeBackgroundColor(android.R.color.transparent)
        stubView.visibility = View.GONE
        this.imageInfo = imageInfo
        needProcessImageInfo = true
        processImageInfoIfNeed()
    }

    private fun onLoadingError(throwable: Throwable) {
        previewView.visibility = View.GONE
        previewView.setOnTouchListener(null)
        if (loadPreviewListener?.onLoadImageFailure(throwable) != true) {
            changeBackgroundColor(RDesign.color.palette_color_white1)
            stubView.setContent(
                when (throwable) {
                    is UnknownHostException -> offlineStubContent()
                    is ServiceUnavailableException -> serviceUnavailableStub()
                    else -> errorStubContent()
                }
            )
            stubView.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar() {
        progressBarVisibilitySwitcher.changeProgressBarAlpha(1f)
        progressBarVisibilitySwitcher.switchProgressBarVisibility(true)
    }

    private fun hideProgressBar() {
        progressBarVisibilitySwitcher.switchProgressBarVisibility(false)
        progressBarVisibilitySwitcher.changeProgressBarAlpha(1f)
    }

    private fun offlineStubContent(): StubViewContent =
        DrawableImageStubContent(
            icon = IconicsDrawable(context, SbisMobileIcon.Icon.smi_alert).apply {
                colorRes(RDesign.color.palette_colorAttention)
                sizeRes(RStubView.dimen.stub_view_icon_default_size)
            },
            messageRes = R.string.common_views_document_preview_load_error,
            details = StringBuilder().run {
                append(context.getString(R.string.common_views_document_preview_offline_error_pre_clickable_text))
                append(" ")
                append(context.getString(R.string.common_views_document_preview_offline_error_clickable_text))
                toString()
            },
            actions = mapOf(R.string.common_views_document_preview_offline_error_clickable_text to ::reloadPreview)
        )

    private fun serviceUnavailableStub(): StubViewContent =
        IconStubContent(
            icon = SbisMobileIcon.Icon.smi_alert,
            iconColor = RDesign.color.text_color_attention,
            iconSize = RStubView.dimen.stub_view_icon_default_size,
            messageRes = StubViewCase.SERVICE_UNAVAILABLE.messageRes,
            detailsRes = StubViewCase.SERVICE_UNAVAILABLE.detailsRes
        )

    private fun errorStubContent(): StubViewContent =
        DrawableImageStubContent(
            icon = IconicsDrawable(context, SbisMobileIcon.Icon.smi_alert).apply {
                colorRes(RDesign.color.palette_color_red1)
                sizeRes(RStubView.dimen.stub_view_icon_default_size)
            },
            messageRes = R.string.common_views_document_preview_load_error,
            details = null
        )

    private fun reloadPreview() {
        previewUri?.let { loadPreview(it) }
    }

    private fun processImageInfoIfNeed() {
        if (needProcessImageInfo && measured) {
            imageInfo?.let { imageInfo -> processImageInfo(imageInfo) }
        }
    }

    private fun processImageInfo(imageInfo: ImageInfo) {
        needProcessImageInfo = false
        previewView.update(imageInfo.width, imageInfo.height)
        val widthRatio: Float = measuredWidth / imageInfo.width.toFloat()
        val heightRatio: Float = measuredHeight / imageInfo.height.toFloat()
        if (zoomToBorders || widthRatio <= MAX_ZOOM_FACTOR || heightRatio <= MAX_ZOOM_FACTOR) {
            val minZoomFactor: Float = min(widthRatio, heightRatio)
            if (minZoomFactor > 1) {
                zoomImage(minZoomFactor)
            }
        } else {
            zoomImage(MAX_ZOOM_FACTOR)
        }
    }

    private fun zoomImage(zoomFactor: Float) {
        val newMediumZoomFactor: Float = zoomFactor * IAttacher.DEFAULT_MID_SCALE
        if (previewView.mediumScale < newMediumZoomFactor) {
            previewView.maximumScale = zoomFactor * IAttacher.DEFAULT_MAX_SCALE
            previewView.mediumScale = newMediumZoomFactor
        }
        previewView.scale = zoomFactor
    }

    //region ProgressBarVisibilitySwitcher
    override fun switchProgressBarVisibility(visible: Boolean) {
        progressBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun changeProgressBarAlpha(alpha: Float) {
        progressBar.alpha = alpha
    }
    //endregion
}
package ru.tensor.sbis.design.profile.person.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.updatePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.imageview.drawer.ShapedDrawer
import ru.tensor.sbis.design.profile.person.ActivityStatusDrawable
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.person.data.DisplayMode
import ru.tensor.sbis.design.profile.person.feature.PersonViewPlugin
import ru.tensor.sbis.design.profile.person.hasPreviewSizeTemplate
import ru.tensor.sbis.design.profile.util.getPreviewerPhotoUri
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.ImageSize
import ru.tensor.sbis.design.utils.getViewIdName
import ru.tensor.sbis.design.utils.image_loading.ImageLoaderDiagnostics
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import java.util.UUID

/**
 * Реализует логику компонента [PersonView].
 *
 * @author us.bessonov
 */
internal class PersonViewControllerImpl(
    @Px
    private val maxSizeWithActivityStatus: Int,
    @Px
    private val toolbarPadding: Int,
    @ColorInt
    private var initialsColor: Int,
    @ColorInt
    private val backgroundColor: Int,
    private val personActivityStatusNotifier: PersonActivityStatusNotifier?,
    imageLoaderProvider: (Int) -> ViewImageLoader = { id -> ViewImageLoader(diagnosticsId = id) },
    private val providePersonImageView: (Context, Int) -> PersonImageView = { context, id ->
        PersonImageView(context, id)
    }
) : PersonViewController {

    private val id = ID++

    private lateinit var view: PersonView
    private lateinit var activityStatusDrawable: ActivityStatusDrawable

    private val personImageView by lazy {
        providePersonImageView(getContext(), id)
    }

    private val imageLoader = imageLoaderProvider(id)

    private var displayMode = DisplayMode.REGISTRY

    private var photoData: PhotoData = PersonData()

    private var isReloadedAfterRecycled = false

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var activityStatusJob: Job? = null

    private var hasActivityStatus = false

    private var displayOfflineHomeStatus = false

    override var personFullName: String = ""
        private set

    override var photoSizePx: Int = 0
        private set

    override var photoSize = PhotoSize.UNSPECIFIED
        private set

    override var activityStatus = ActivityStatus.UNKNOWN
        private set

    override val nodeInfoText: String
        get() = if (photoData.photoUrl != null && personFullName.isNotEmpty()) {
            personFullName
        } else {
            (photoData as? PersonData)?.initialsStubData?.initials ?: ""
        }

    // region PersonViewApi
    override fun setData(data: PhotoData) {
        if (data is DepartmentData && data.persons.isEmpty()) {
            data.isStub = true
        }
        photoData = data
        personImageView.setData(data)

        setOpaqueBackground(data !is CompanyData)
        imageLoader.setImages(listOf(ImageUrl(getPreviewerPhotoUri(data.photoUrl.orEmpty(), getPhotoSize()))))
        if (supportsActivityStatus()) {
            data.uuid?.let(::observeActivityStatus)
        }
        isReloadedAfterRecycled = false
    }

    override fun setPersonImage(lastName: String, firstName: String, uuid: UUID, masterImageThumbUrl: String) {
        setData(
            PersonData(
                uuid,
                masterImageThumbUrl,
                InitialsStubData.createByNameParts(lastName, firstName)
            )
        )
    }

    override fun setSize(size: PhotoSize) = setSize(size, photoSizePx)

    override fun setShape(shape: Shape) {
        personImageView.setShape(shape)
    }

    override fun setCornerRadius(radius: BorderRadius?) =
        setCornerRadius(radius?.getDimen(getContext()) ?: 0f)

    override fun setHasActivityStatus(hasActivityStatus: Boolean, displayOfflineHomeStatus: Boolean) {
        this.hasActivityStatus = hasActivityStatus
        this.displayOfflineHomeStatus = displayOfflineHomeStatus
        if (supportsActivityStatus()) {
            photoData.uuid?.let(::observeActivityStatus)
        } else {
            activityStatusJob?.cancel()
            updateActivityStatus(ActivityStatus.UNKNOWN)
        }
    }

    override fun setClickListener(onClick: (UUID?) -> Unit) {
        view.setOnClickListener { onClick(photoData.uuid) }
    }

    override fun setFullNameForNodeInfo(personFullName: String) {
        this.personFullName = personFullName
    }
    // endregion

    // region PersonViewController

    override fun init(personView: PersonView, activityStatusDrawable: ActivityStatusDrawable) {
        view = personView
        personImageView.viewIdName = getViewIdName(personView)
        this.activityStatusDrawable = activityStatusDrawable
        setActivityStatusSize()
        initDefaultClickListener()
        imageLoader.init(
            view,
            personImageView,
            { },
            { null },
            ::getImageWidthAndHeight
        )
    }

    private fun setActivityStatusSize() {
        this.activityStatusDrawable.size = if (view.width <= view.dp(MIN_STATUS_WIDTH)) {
            activityStatusDrawable.styleHolder.statusSizeSmall
        } else {
            activityStatusDrawable.styleHolder.statusSizeMedium
        }
    }

    override fun onSizeChanged(size: Int) {
        setSize(photoSize, size)
        setActivityStatusSize()
        if (photoSize == PhotoSize.UNSPECIFIED) {
            activityStatusDrawable.setVisible(size <= maxSizeWithActivityStatus, false)
        }
    }

    override fun onMeasured() {
        imageLoader.onViewMeasured()
    }

    override fun performLayout() {
        personImageView.layout(
            view.paddingStart,
            view.paddingTop,
            view.paddingStart + getPhotoSize(),
            view.paddingTop + getPhotoSize()
        )
    }

    override fun performDraw(canvas: Canvas) {
        if (personImageView.isBitmapRecycled() && !isReloadedAfterRecycled) {
            isReloadedAfterRecycled = true
            view.post { imageLoader.reloadImage() }
        }
        personImageView.draw(canvas)
    }

    override fun performInvalidate() {
        log("invalidate")
        personImageView.invalidate()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        imageLoader.onVisibilityAggregated(isVisible)
    }

    override fun onViewDetachedFromWindow() {
        activityStatusJob?.cancel()
        activityStatusJob = null
    }

    override fun setPreviewActivityStatus() {
        updateActivityStatus(ActivityStatus.ONLINE_WORK)
    }

    override fun setDisplayMode(mode: DisplayMode) {
        this.displayMode = mode
        if (mode == DisplayMode.TOOLBAR) {
            view.updatePadding(right = toolbarPadding, bottom = toolbarPadding)
        }
    }

    override fun setCornerRadius(radius: Float) {
        personImageView.setCornerRadius(radius)
    }

    override fun setShapedDrawer(drawer: ShapedDrawer) {
        personImageView.drawer = drawer
    }

    override fun setInitialsColor(@ColorInt color: Int) {
        initialsColor = color
        personImageView.initialsColor = color
    }
    // endregion

    @Px
    private fun getPhotoSize(): Int {
        return photoSizePx
            .takeUnless { it <= 0 }
            ?: view.layoutParams.width
    }

    private fun getImageWidthAndHeight() = if (view.measuredWidth > 0 && view.measuredHeight > 0) {
        view.measuredWidth to view.measuredHeight
    } else {
        val size = getPhotoSize()
        size to size
    }

    private fun setSize(size: PhotoSize, @Px customSize: Int) {
        photoSize = size
        val newSize = size.photoImageSize.takeUnless { it == null }
            ?.let(::getDimensionPixelSizeNullable)
            ?: customSize
        if (size == photoSize && newSize == photoSizePx) return

        val isBiggerPreviewRequired = isBiggerPreviewRequired(photoSizePx, newSize)
        photoSizePx = newSize

        personImageView.setPhotoSize(photoSizePx)
        personImageView.initialsTextSize = photoSize.getInitialsTextSize(getContext())

        val currentPhotoViewSize = maxOf(view.measuredWidth - view.paddingStart - view.paddingEnd, 0)
        if (currentPhotoViewSize != photoSizePx) {
            view.run {
                if (!isLayoutRequested) requestLayout()
                invalidate()
            }
        }
        if (isBiggerPreviewRequired) setData(photoData)
        if (photoSizePx != 0) {
            activityStatusDrawable.setVisible(photoSizePx <= maxSizeWithActivityStatus, false)
            view.invalidate()
        }
    }

    private fun isBiggerPreviewRequired(oldSize: Int, newSize: Int) =
        photoData.photoUrl?.let(::hasPreviewSizeTemplate) == true && newSize > oldSize

    private fun initDefaultClickListener() {
        PersonViewPlugin.personClickListenerProvider?.get()
            ?.let { listener ->
                view.setOnClickListener {
                    (photoData as? PersonData)?.uuid?.let {
                        listener.onPersonClicked(getContext(), it)
                    }
                }
            }
    }

    private fun setOpaqueBackground(isOpaque: Boolean) {
        personImageView.setShapeBackgroundColor(
            if (isOpaque) backgroundColor else Color.TRANSPARENT
        )
    }

    private fun getContext() = view.context

    @Px
    private fun getDimensionPixelSizeNullable(size: ImageSize) = try {
        size.getDimenPx(getContext())
    } catch (e: Resources.NotFoundException) {
        null
    }

    private fun observeActivityStatus(uuid: UUID) {
        val statusNotifier = personActivityStatusNotifier ?: return
        updateActivityStatus(statusNotifier.getStatus(uuid).activityStatus)
        activityStatusJob?.cancel()
        activityStatusJob = scope.launch {
            statusNotifier.observe(uuid)
                .collect {
                    updateActivityStatus(it.activityStatus)
                }
        }
    }

    private fun updateActivityStatus(status: ActivityStatus) {
        this.activityStatus = status
        activityStatusDrawable.setActivityStatus(status, displayOfflineHomeStatus)
        view.invalidate()
    }

    private fun supportsActivityStatus() = hasActivityStatus && photoData is PersonData

    private fun log(msg: String) = ImageLoaderDiagnostics.log(id, msg)

    companion object {
        const val MIN_STATUS_WIDTH = 30
    }
}

private var ID = 0
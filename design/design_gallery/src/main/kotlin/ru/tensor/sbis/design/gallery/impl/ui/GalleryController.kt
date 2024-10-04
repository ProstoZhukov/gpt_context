package ru.tensor.sbis.design.gallery.impl.ui

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.apache.commons.lang3.time.DurationFormatUtils
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderFeature
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderParams
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeSymbology
import ru.tensor.sbis.barcode_decl.barcodereader.ManualInputStrategy
import ru.tensor.sbis.common.util.DeviceUtils
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.files_picker.decl.CropParams
import ru.tensor.sbis.design.files_picker.decl.CropShape
import ru.tensor.sbis.design.files_picker.decl.GalleryCameraType
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.files_picker.view.pushSelectionLimitNotification
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.decl.GalleryEvent
import ru.tensor.sbis.design.gallery.decl.GalleryMode
import ru.tensor.sbis.design.gallery.impl.GalleryComponentImpl
import ru.tensor.sbis.design.gallery.impl.GalleryPlugin
import ru.tensor.sbis.design.gallery.impl.store.GalleryStore
import ru.tensor.sbis.design.gallery.impl.store.GalleryStoreFactory
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAlbumItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryIntent
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryLabel
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryState
import ru.tensor.sbis.design.gallery.impl.ui.adapter.GalleryAlbumsAdapter.Companion.GALLERY_ALBUM_VIEW_TYPE
import ru.tensor.sbis.design.gallery.impl.ui.adapter.GalleryItemsAdapter
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryAlbumItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryCameraPreviewItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryCameraStub
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryEventMvi
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryStorageStub
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryModel
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess
import ru.tensor.sbis.design.gallery.impl.utils.pushSizeLimitNotification
import ru.tensor.sbis.design.gallery.impl.viewer.GalleryImageViewerArgs
import ru.tensor.sbis.design.gallery.impl.viewer.GalleryVideoViewerArgs
import ru.tensor.sbis.design.gallery.impl.viewer.GalleryViewerActivity
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.design.text_span.span.SbisSpannableStringBuilder
import ru.tensor.sbis.design.text_span.span.TextGravitySpan
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.slider.source.ViewerArgsSource
import ru.tensor.sbis.viewer.decl.viewer.ImageUri
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

internal const val VIEWER_SELECTED_IDS_RESULT_EXTRA_KEY = "viewer_selected_ids_result_extra_key"
internal const val VIEWER_IS_SELECTION_CONFIRMED_RESULT_EXTRA_KEY = "viewer_is_selection_confirmed_result_extra_key"
internal const val PERMISSION_IN_SETTINGS_DIALOG_REQUEST_CODE = 1
private const val PERMISSION_IN_SETTINGS_DIALOG_TAG = "permission_in_settings_dialog_tag"
private const val ALBUM_ITEM_IMAGE_SIZE = 82

/**
 * Эмпирическая константа выравнивания иконки относительно текста (см. PersonCardMapper)
 */
private const val ICON_BASELINE_SHIFT_FACTOR = 0.132f

internal class GalleryController @Inject constructor(
    viewFactory: (View) -> GalleryView,
    private val fragment: GalleryFragment,
    private val config: GalleryConfig,
    private val storeFactory: GalleryStoreFactory,
    private val component: GalleryComponentImpl
) {
    private val context = fragment.requireContext()
    private val store: GalleryStore = fragment.provideStore { storeFactory.create(it, GalleryState.Loading()) }
    private val notGrantedPermissions: Array<String> = getNotGrantedPermissions().toTypedArray()
    private val permissionsContract =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            createIntentFromPermissions()
        }
    private val permissionsInSettingsContract =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            createIntentFromPermissions()
        }
    private val cameraContract: ActivityResultLauncher<Uri> =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { bool ->
            logAttachProcess("cameraActivityResultCallback, uri - $cameraSnapshotUri, isSuccess - $bool")
            if (bool) {
                cameraSnapshotUri?.let {
                    store.accept(GalleryIntent.SnapshotTaken)
                } ?: illegalState { "Camera snapshot uri can't be null" }
            }
        }
    private val viewerSliderContract =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedItemsIds = it.data?.getIntArrayExtra(VIEWER_SELECTED_IDS_RESULT_EXTRA_KEY)?.toMutableList()
                val isSelectionConfirmed =
                    it.data?.getBooleanExtra(VIEWER_IS_SELECTION_CONFIRMED_RESULT_EXTRA_KEY, false) ?: false
                if (selectedItemsIds != null) {
                    if (isSelectionConfirmed) {
                        store.accept(GalleryIntent.SelectionConfirmed(selectedItemsIds))
                    } else {
                        store.accept(GalleryIntent.CloseViewer(selectedItemsIds))
                    }
                }
            }
        }
    private val barcodeReaderContract =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
            if (intent.resultCode == Activity.RESULT_OK) {
                val barcodeValue = intent.data?.getStringExtra(BarcodeReaderFeature.RESULT_KEY_BARCODE)
                val barcodeType = intent.data?.getStringExtra(BarcodeReaderFeature.RESULT_KEY_BARCODE_TYPE)?.let {
                    BarcodeSymbology.valueOf(it)
                }
                val filePath = intent.data?.getStringExtra(BarcodeReaderFeature.RESULT_KEY_BARCODE_FILE)
                if (filePath != null) {
                    val fileUri = Uri.fromFile(File(filePath)).toString()
                    store.accept(GalleryIntent.OnBarcodeScannerResult(barcodeValue, barcodeType, fileUri))
                }
            }
        }
    private var cameraSnapshotUri: Uri? = null
    private val baselineShift by lazy {
        (
            fragment
                .requireContext()
                .getDimen(ru.tensor.sbis.design.R.attr.fontSize_3xs_scaleOff) * ICON_BASELINE_SHIFT_FACTOR
            )
            .roundToInt()
    }
    private val itemsCountPerLine by lazy { fragment.getItemsCountPerLine() }
    private val barcodeFeature: BarcodeReaderFeature? by lazy {
        GalleryPlugin.barcodeReaderFeatureProvider?.get()
    }

    init {
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { view ->
            bind {
                view.events.map { it.toIntent() } bindTo store
                store.states.map { it.toModel() } bindTo view
                store.labels bindTo { it.handle() }
                component.addButtonEvent
                    .onEach { onFilesPickerAddButtonCustomClick() }
                    .launchIn(fragment.viewLifecycleOwner.lifecycleScope)
            }
        }
        cameraSnapshotUri = fragment.cameraSnapshotUri
        logAttachProcess("init, uri - $cameraSnapshotUri")
    }

    private fun checkPermissions() {
        if (getNotGrantedPermissions().isNotEmpty()) {
            createIntentFromPermissions()
        }
        permissionsContract.launch(notGrantedPermissions)
        fragment.isFirstPermissionsCheck = false
    }

    private fun createIntentFromPermissions() =
        store.accept(
            when (hasCameraPermission()) {
                true ->
                    if (hasStoragePermission()) {
                        GalleryIntent.LoadItems
                    } else {
                        GalleryIntent.StoragePermissionDenied
                    }

                false ->
                    if (hasStoragePermission()) {
                        GalleryIntent.CameraPermissionDenied
                    } else {
                        GalleryIntent.AllPermissionsDenied
                    }
            }
        )

    private fun GalleryEventMvi.toIntent(): GalleryIntent =
        when (this) {
            is GalleryEventMvi.ItemClicked -> GalleryIntent.ItemClicked(id)
            is GalleryEventMvi.AlbumClicked -> GalleryIntent.AlbumClicked(id)
            is GalleryEventMvi.ItemCheckboxClicked -> GalleryIntent.ItemCheckboxClicked(id)
            is GalleryEventMvi.AddButtonClick -> GalleryIntent.SelectionConfirmed()
            is GalleryEventMvi.BackPressed -> GalleryIntent.BackPressed
            is GalleryEventMvi.CancelButtonClick -> GalleryIntent.CancelButtonClicked
            is GalleryEventMvi.CameraPreviewClick -> GalleryIntent.OpenCamera
            is GalleryEventMvi.CameraStubClick -> GalleryIntent.RequestCameraPermission
            is GalleryEventMvi.StorageStubClick -> GalleryIntent.RequestStoragePermission
            is GalleryEventMvi.MainStubClick -> GalleryIntent.RequestPermissions
        }

    private fun GalleryState.toModel(): GalleryModel =
        when (this) {
            is GalleryState.Content -> {
                this@GalleryController.cameraSnapshotUri = cameraSnapshotUri
                fragment.cameraSnapshotUri = cameraSnapshotUri
                when (type) {
                    is GalleryState.Content.Type.Media -> {
                        GalleryModel.Content.Media(
                            items = buildList {
                                val galleryVMs = type.items.toViewModels()
                                if (config.mode is GalleryMode.AllMedia) {
                                    if (cameraStubVisible) {
                                        add(GalleryCameraStub(GalleryItemsAdapter.GALLERY_CAMERA_STUB_VIEW_TYPE))
                                    } else {
                                        add(
                                            GalleryCameraPreviewItemVM(
                                                adapterViewType = GalleryItemsAdapter.CAMERA_VIEW_TYPE,
                                                isSmall = galleryVMs.isEmpty() && storageStubVisible
                                            )
                                        )
                                    }
                                }
                                if (storageStubVisible) {
                                    add(GalleryStorageStub(GalleryItemsAdapter.GALLERY_STORAGE_STUB_VIEW_TYPE))
                                } else {
                                    addAll(galleryVMs)
                                }
                            },
                            barConfig = barConfig,
                            isEnabledAddButton = isEnabledAddButton
                        )
                    }

                    is GalleryState.Content.Type.Albums ->
                        GalleryModel.Content.Albums(
                            items = type.items.toViewModels(selectedItemsIds),
                            isEnabledAddButton = isEnabledAddButton
                        )
                }
            }

            is GalleryState.Stub -> GalleryModel.Stub
            is GalleryState.Loading -> GalleryModel.Loading
        }

    private fun GalleryLabel.handle() =
        when (this) {
            is GalleryLabel.ShowViewerSlider ->
                viewerSliderContract.launch(
                    GalleryViewerActivity.createGalleryViewerIntent(
                        context,
                        ViewerSliderArgs(
                            source = ViewerArgsSource.Fixed(
                                items.toViewerArgs(),
                                items.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: 0
                            )
                        ),
                        selectedItemsIds.toIntArray(),
                        config.selectionMode,
                        config.sizeInMBytesLimit
                    )
                )

            is GalleryLabel.CheckPermissions -> checkPermissions()
            is GalleryLabel.SelectionLimit -> pushSelectionLimitNotification(fragment.resources, limit)
            is GalleryLabel.SizeLimit -> pushSizeLimitNotification(fragment.resources, sizeInBytesLimit)
            is GalleryLabel.AddButtonClicked -> component.sendEvent(GalleryEvent.OnAddButtonClick(items.toLocalFiles()))
            is GalleryLabel.ItemsSelected -> component.sendEvent(GalleryEvent.OnFilesSelected(items.toLocalFiles()))
            is GalleryLabel.CancelButtonClicked -> component.sendEvent(GalleryEvent.OnCancelButtonClick())
            is GalleryLabel.OpenCamera ->
                if (hasStoragePermission()) {
                    when (config.cameraType) {
                        is GalleryCameraType.Default -> openDefaultCamera()
                        is GalleryCameraType.BarcodeScanner -> openBarcodeReader()
                    }
                } else {
                    createDialogForPermissionsReadStorage()
                }
            is GalleryLabel.CameraStubClicked ->
                createDialogForPermission(
                    CAMERA,
                    R.string.design_gallery_camera_permission_in_settings_dialog_message
                )
            is GalleryLabel.StorageStubClicked -> createDialogForPermissionsReadStorage()
            is GalleryLabel.MainStubClicked -> checkPermissionOrOpenSettings()
            is GalleryLabel.SnapshotTaken -> {
                logAttachProcess("sendEvent to component, uri - ${item.uri}")
                component.sendEvent(GalleryEvent.OnCameraSnapshotSuccess(item))
            }
            is GalleryLabel.CropImage -> cropImage(uri, cropParams)
            is GalleryLabel.OnBarcodeScannerResult -> component.sendEvent(GalleryEvent.OnBarcodeScannerResult(item))
        }

    private fun createDialogForPermissionsReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            createDialogForPermission(
                READ_MEDIA_IMAGES,
                R.string.design_gallery_storage_permission_in_settings_dialog_message
            )
        } else {
            createDialogForPermission(
                READ_EXTERNAL_STORAGE,
                R.string.design_gallery_storage_permission_in_settings_dialog_message
            )
        }
    }

    private fun openBarcodeReader() =
        barcodeFeature?.let {
            barcodeReaderContract.launch(
                it.createIntentBarcodeCaptureActivity(
                    context,
                    BarcodeReaderParams(
                        manualInputStrategy = ManualInputStrategy.NONE,
                        needShowQrAndShot = true,
                        galleryIconVisible = false
                    )
                )
            )
        } ?: {
            illegalState { "Dependency not provided" }
            openDefaultCamera()
        }

    private fun List<GalleryItem>.toViewModels(): List<GalleryItemVM> {
        val imageSize = DeviceUtils.getScreenMinSideInPx(context) / itemsCountPerLine
        return map {
            val uri: Uri = Uri.parse(it.uri)
            val imageRequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions.forSquareSize(imageSize))
                .build()
            GalleryItemVM(
                adapterViewType = GalleryItemsAdapter.GALLERY_ITEM_VIEW_TYPE,
                id = it.id ?: Random.nextInt(),
                uri = uri,
                imageRequest = imageRequest,
                isVideo = it.isVideo,
                duration = SbisSpannableStringBuilder().formattedDuration(it.duration),
                selectionNumber = it.selectionNumber
            )
        }
    }

    private fun List<GalleryAlbumItem>.toViewModels(selectedItemsIds: Map<Int, Int>): List<GalleryAlbumItemVM> =
        map {
            val coverPhoto = it.coverPhoto
            if (coverPhoto != null) {
                val uri = Uri.parse(coverPhoto.uri)
                val imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(ResizeOptions.forSquareSize(ALBUM_ITEM_IMAGE_SIZE))
                    .build()
                GalleryAlbumItemVM(
                    adapterViewType = GALLERY_ALBUM_VIEW_TYPE,
                    id = it.id,
                    name = it.name,
                    imageRequest = imageRequest,
                    itemsCount = it.items.size.toString(),
                    selectedItemsCount =
                    it.items.map { item -> item.id }
                        .intersect(selectedItemsIds.filterValues { value -> value == it.id }.keys)
                        .size
                )
            } else {
                GalleryAlbumItemVM(
                    adapterViewType = GALLERY_ALBUM_VIEW_TYPE,
                    id = it.id,
                    name = it.name,
                    imageRequest = null,
                    itemsCount = it.items.size.toString(),
                    selectedItemsCount =
                    it.items.map { item -> item.id }
                        .intersect(selectedItemsIds.filter { entry -> entry.value == it.id }.keys)
                        .size
                )
            }
        }

    private fun List<GalleryItem>.toLocalFiles(): List<SbisPickedItem.LocalFile> =
        map {
            SbisPickedItem.LocalFile(it.uri)
        }

    private fun List<GalleryItem>.toViewerArgs(): List<ViewerArgs> =
        map {
            val uri = it.uri
            if (it.isVideo) {
                GalleryVideoViewerArgs(localUri = uri, title = "", id = it.id.toString(), fileSize = it.size?.toInt())
            } else {
                GalleryImageViewerArgs(imageSource = ImageUri(uri), id = it.id.toString(), fileSize = it.size?.toInt())
            }
        }

    private fun createDialogForPermission(permission: String, @StringRes message: Int) {
        if (fragment.shouldShowRequestPermissionRationale(permission)) {
            permissionsContract.launch(arrayOf(permission))
        } else {
            if (fragment.childFragmentManager.findFragmentByTag(PERMISSION_IN_SETTINGS_DIALOG_TAG) == null) {
                PopupConfirmation.newMessageInstance(
                    PERMISSION_IN_SETTINGS_DIALOG_REQUEST_CODE,
                    fragment.getString(message)
                )
                    .requestNegativeButton(fragment.getString(RCommon.string.dialog_button_cancel))
                    .requestPositiveButton(
                        fragment.getString(R.string.design_gallery_permission_in_settings_dialog_positive_btn)
                    )
                    .setEventProcessingRequired(true)
                    .also { dialogFragment ->
                        dialogFragment.isCancelable = false
                    }
                    .show(fragment.childFragmentManager, PERMISSION_IN_SETTINGS_DIALOG_TAG)
            } else {
                return
            }
        }
    }

    private fun checkPermissionOrOpenSettings() {
        var permissionsCount = 0
        notGrantedPermissions.forEach {
            if (fragment.shouldShowRequestPermissionRationale(it)) permissionsCount++
        }
        if (permissionsCount != 0) {
            permissionsContract.launch(notGrantedPermissions)
        } else {
            startRequestPermissionsActivity()
        }
    }

    private fun openDefaultCamera() {
        val uri = cameraSnapshotUri
        if (uri != null) {
            try {
                logAttachProcess("openCamera, uri - $uri")
                cameraContract.launch(uri)
            } catch (e: Exception) {
                SbisPopupNotification.push(
                    SbisPopupNotificationStyle.ERROR,
                    fragment.resources.getString(R.string.design_gallery_camera_open_error_message)
                )
            }
        } else {
            illegalState { "Camera snapshot uri can't be null" }
        }
    }

    private fun cropImage(uri: Uri, params: CropParams) {
        val croppedFile = File(
            GalleryPlugin.internalStorageProvider.get().internalStorage.cacheDir(),
            FileUriUtil.generateCroppedSnapshotName()
        )
        val croppedUri = Uri.fromFile(croppedFile)
        CropImage
            .activity(uri)
            .apply {
                setOutputUri(croppedUri)
                setMultiTouchEnabled(true)
                setCropShape(
                    when (params.shape) {
                        CropShape.RECTANGLE -> CropImageView.CropShape.RECTANGLE
                        CropShape.OVAL -> CropImageView.CropShape.OVAL
                    }
                )
                params.aspectRatio?.let { ratio -> setAspectRatio(ratio.width, ratio.height) }
                params.minSizeLimit?.let { limit -> setMinCropResultSize(limit.minWidth, limit.minHeight) }
            }
            .start(context, fragment)
    }

    fun onYesDialogPermissions() {
        startRequestPermissionsActivity()
    }

    fun onImageCropResult(data: Intent?, isSuccess: Boolean) {
        if (isSuccess) {
            val uri = CropImage.getActivityResult(data).uri
            if (uri != null) {
                component.sendEvent(GalleryEvent.OnImageCropped(SbisPickedItem.LocalFile(uri.toString())))
            } else {
                illegalState { "Cropped image uri can't be null" }
            }
        }
    }

    private fun onFilesPickerAddButtonCustomClick() {
        store.accept(GalleryIntent.FilesPickerAddButtonClicked)
    }

    private fun startRequestPermissionsActivity() {
        permissionsInSettingsContract.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", context.packageName, null))
        )
    }

    private fun hasStoragePermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                context,
                READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            context,
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun getNotGrantedPermissions(): List<String> =
        buildList {
            if (!hasCameraPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(CAMERA)
                } else {
                    addAll(arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE))
                }
            }
            if (!hasStoragePermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    addAll(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
                } else {
                    add(READ_EXTERNAL_STORAGE)
                }
            }
        }

    private fun Long.millisToMinutes(): String = DurationFormatUtils.formatDuration(this, "mm:ss")

    private fun SbisSpannableStringBuilder.formattedDuration(duration: Long?): CharSequence =
        apply {
            if (duration != null) {
                append(SbisMobileIcon.Icon.smi_playAudioMessage.character.toString())
                setSpan(
                    CustomTypefaceSpan(TypefaceManager.getSbisMobileIconTypeface(context)),
                    0,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                append(" ")
                setSpan(TextGravitySpan(baselineShift), 0, length, 0)
                append(duration.millisToMinutes())
                setSpan(
                    AbsoluteSizeSpan(context.getDimenPx(ru.tensor.sbis.design.R.attr.fontSize_3xs_scaleOff)),
                    0,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
}
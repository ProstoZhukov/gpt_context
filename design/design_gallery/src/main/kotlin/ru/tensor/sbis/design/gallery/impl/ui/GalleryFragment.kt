package ru.tensor.sbis.design.gallery.impl.ui

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import com.theartofdev.edmodo.cropper.CropImage
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.findFirstVisibleItemPosition
import ru.tensor.sbis.common.util.findOrCreateViewModel
import ru.tensor.sbis.common.util.findViewModelHierarchical
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.gallery.databinding.DesignGalleryFragmentMainBinding
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.impl.GalleryComponentImpl
import ru.tensor.sbis.design.gallery.impl.di.DaggerGalleryDIComponent
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation

class GalleryFragment : Fragment(), PopupConfirmation.DialogYesNoWithTextListener {

    companion object {
        private const val CONFIG_ARG = "config_arg"
        private const val CAMERA_SNAPSHOT_URI_KEY = "camera_snapshot_uri_key"
        private const val ITEMS_COLUMN_COUNT_PORTRAIT = 3
        private const val ITEMS_COLUMN_COUNT_LANDSCAPE = 5
        private const val FIRST_PERMISSIONS_CHECK_KEY = "first_permissions_check_key"
        private const val FIRST_VISIBLE_ITEM_POSITION_KEY = "first_visible_item_position_key"

        fun newInstance(config: GalleryConfig): Fragment {
            return GalleryFragment().withArgs {
                putParcelable(CONFIG_ARG, config)
            }
        }
    }

    private var controller: GalleryController? = null

    var cameraSnapshotUri: Uri? = null
    var isFirstPermissionsCheck: Boolean = true

    private val config: GalleryConfig by lazy {
        requireArguments().getParcelableUniversally(CONFIG_ARG)!!
    }

    private val component: GalleryComponentImpl by lazy {
        findViewModelHierarchical() ?: findOrCreateViewModel(
            parentFragment ?: parentFragment?.parentFragment ?: requireActivity()
        ) {
            GalleryComponentImpl(
                SbisFilesPickerTab.Gallery(
                    selectionMode = config.selectionMode,
                    fileSizeInMBytesLimit = config.sizeInMBytesLimit
                )
            )
        }
    }

    private lateinit var binding: DesignGalleryFragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraSnapshotUri = savedInstanceState?.getString(CAMERA_SNAPSHOT_URI_KEY)?.toUri()
        logAttachProcess("onCreate, uri - $cameraSnapshotUri")
        isFirstPermissionsCheck = savedInstanceState?.getBoolean(FIRST_PERMISSIONS_CHECK_KEY) ?: true
        val firstVisibleItemPosition: Int? = savedInstanceState?.getInt(FIRST_VISIBLE_ITEM_POSITION_KEY)
        val component = DaggerGalleryDIComponent.factory().create(
            fragment = this,
            context = requireContext(),
            view = { view ->
                GalleryView(
                    requireContext(),
                    DesignGalleryFragmentMainBinding.bind(view),
                    viewLifecycleOwner,
                    config.mode,
                    getItemsCountPerLine(),
                    firstVisibleItemPosition,
                    config.isNeedBottomPadding
                )
            },
            config = config,
            component = component,
            resourceProvider = ResourceProvider(requireActivity().application),
            contentResolver = requireActivity().contentResolver,
            fileUriUtil = FileUriUtil(requireContext()),
            clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager,
            needOnlyImages = config.needOnlyImages
        )
        controller = component.injectController()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CAMERA_SNAPSHOT_URI_KEY, cameraSnapshotUri.toString())
        logAttachProcess("onSaveInstanceState, uri - $cameraSnapshotUri")
        outState.putBoolean(FIRST_PERMISSIONS_CHECK_KEY, isFirstPermissionsCheck)
        if (activity?.isChangingConfigurations == true && binding.galleryList.layoutManager != null) {
            outState.putInt(FIRST_VISIBLE_ITEM_POSITION_KEY, binding.galleryList.findFirstVisibleItemPosition())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        DesignGalleryFragmentMainBinding.inflate(inflater, container, false)
            .apply { binding = this }
            .root

    override fun onYes(requestCode: Int, text: String?) {
        controller?.onYesDialogPermissions()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            controller?.onImageCropResult(data, isSuccess = resultCode == Activity.RESULT_OK)
        }
    }

    fun getItemsCountPerLine(): Int =
        if (
            resources.configuration.orientation == ORIENTATION_LANDSCAPE &&
            !DeviceConfigurationUtils.isTablet(requireContext())
        ) {
            ITEMS_COLUMN_COUNT_LANDSCAPE
        } else {
            ITEMS_COLUMN_COUNT_PORTRAIT
        }
}
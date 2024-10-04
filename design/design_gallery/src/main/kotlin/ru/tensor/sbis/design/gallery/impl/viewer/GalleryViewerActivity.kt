package ru.tensor.sbis.design.gallery.impl.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnAttach
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.attachments.ui.view.attachmentselection2.checkboxcounter.CheckboxCounter
import ru.tensor.sbis.common.util.DeviceUtils
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode
import ru.tensor.sbis.design.files_picker.view.pushSelectionLimitNotification
import ru.tensor.sbis.viewer.slider.presentation.ViewerSliderActivity
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.impl.store.GalleryExecutor.Companion.TO_BYTES_COEFFICIENT
import ru.tensor.sbis.design.gallery.impl.ui.VIEWER_IS_SELECTION_CONFIRMED_RESULT_EXTRA_KEY
import ru.tensor.sbis.design.gallery.impl.ui.VIEWER_SELECTED_IDS_RESULT_EXTRA_KEY
import ru.tensor.sbis.design.gallery.impl.utils.pushSizeLimitNotification
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.files_picker.R as RFilesPicker
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.extentions.setBottomMargin
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import kotlin.math.roundToInt

/**
 * Активити просмотра элементов галереи устройства.
 * Поддерживает выбор элементов
 *
 * @author ia.nikitin
 */
internal class GalleryViewerActivity : ViewerSliderActivity() {

    private val checkbox by lazy { CheckboxCounter(this) }
    private val selectionMode: GallerySelectionMode? by lazy {
        intent.getParcelableUniversally(VIEWER_SELECTION_MODE_EXTRA_KEY)
    }
    private val sizeInMBytesLimit: Int? by lazy {
        intent.getIntExtra(VIEWER_SIZE_LIMIT_EXTRA_KEY, 0)
    }
    private var selectedItemsIds: MutableList<Int> = mutableListOf()
    private var currentId: Int? = null

    private val onBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishWithData(isSelectionConfirmed = false)
            }
        }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean =
                    when (selectionMode) {
                        is GallerySelectionMode.Multiple -> selectItemForMultipleSelectionMode()
                        is GallerySelectionMode.Single -> selectItemForSingleSelectionMode()
                        null -> {
                            illegalState { "Selection mode can't be null" }
                            false
                        }
                    }
            }
        )
    }

    companion object {

        private const val VIEWER_SELECTED_ITEMS_IDS_EXTRA_KEY = "viewer_selected_items_ids_send_extra_items"
        private const val VIEWER_SELECTION_MODE_EXTRA_KEY = "viewer_selection_mode_send_extra_key"
        private const val VIEWER_SIZE_LIMIT_EXTRA_KEY = "viewer_size_limit_send_extra_key"
        private const val SELECTED_ITEMS_SAVE_KEY = "selected_items_save_key"
        private const val CURRENT_ID_SAVE_KEY = "current_id_save_key"

        fun createGalleryViewerIntent(
            context: Context,
            args: ViewerSliderArgs,
            selectedItemsIds: IntArray,
            selectionMode: GallerySelectionMode,
            sizeInMBytesLimit: Int?
        ): Intent =
            Intent(context, GalleryViewerActivity::class.java).apply {
                putArgsToIntent(context = context, intent = this, args = args)
                putExtra(VIEWER_SELECTED_ITEMS_IDS_EXTRA_KEY, selectedItemsIds)
                putExtra(VIEWER_SELECTION_MODE_EXTRA_KEY, selectionMode)
                putExtra(VIEWER_SIZE_LIMIT_EXTRA_KEY, sizeInMBytesLimit)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        selectedItemsIds =
            if (savedInstanceState != null) {
                currentId = savedInstanceState.getInt(CURRENT_ID_SAVE_KEY)
                savedInstanceState.getIntArray(SELECTED_ITEMS_SAVE_KEY)?.toMutableList() ?: mutableListOf()
            } else {
                intent.getIntArrayExtra(VIEWER_SELECTED_ITEMS_IDS_EXTRA_KEY)?.toMutableList() ?: mutableListOf()
            }
        super.onCreate(savedInstanceState)
        changeImmersiveModeSupport(false)
        initAddButton()
    }

    override fun onPositionChanged(position: Int) {
        currentId = presenter.getViewerId(position).toIntOrNull()
        updateCheckboxCount()
    }

    override fun customizeToolbar() {
        initCheckbox()
    }

    private fun initAddButton() {
        binding.root.apply {
            val container = LayoutInflater.from(context).inflate(R.layout.design_gallery_viewer_add_button, this, false)
            addView(container)
            val button: SbisButton = findViewById(R.id.gallery_viewer_add_button)
            button.apply {
                layoutParams.width = (DeviceUtils.getScreenWidthInPx(context) * 0.4).roundToInt()
                doOnAttach {
                    setBottomMargin(initialSystemBarsInsets?.bottom ?: 0)
                }
                setOnClickListener {
                    if (selectedItemsIds.isEmpty()) {
                        currentId?.let { selectedItemsIds.add(it) }
                    }
                    finishWithData(isSelectionConfirmed = true)
                }
            }
        }
    }

    private fun initCheckbox() {
        checkbox.apply {
            setSize(R.dimen.design_gallery_viewer_checkbox_size)
            setBackground(R.drawable.design_gallery_viewer_checkbox_background)
            setTextSize(FontSize.XL.getScaleOffDimenPx(context))
            layoutParams =
                RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            setHorizontalPadding(Offset.M.getDimenPx(context))
        }
        toolbarBinding.root.addView(checkbox)
    }

    private fun updateCheckboxCount() {
        if (currentId != null) {
            val selectedItemIndex = selectedItemsIds.indexOfFirst { id -> id == currentId }
            if (selectedItemIndex != -1) {
                checkbox.setCount(selectedItemIndex + 1)
            } else {
                checkbox.setCount(0)
            }
        } else {
            illegalState { "Id of current item can't be null" }
        }
    }

    override fun changeTitle(title: String) {
        updateTitle()
    }

    private fun updateTitle() {
        toolbarBinding.toolbarTitle.text =
            if (selectedItemsIds.isEmpty()) {
                ""
            } else {
                resources.getString(RFilesPicker.string.files_picker_panel_header_title, selectedItemsIds.size)
            }
    }

    private fun selectItemForMultipleSelectionMode(): Boolean {
        currentId?.let { id ->
            if (!selectedItemsIds.remove(id)) {
                (selectionMode as? GallerySelectionMode.Multiple)?.selectionLimit?.let {
                    if (selectedItemsIds.size >= it) {
                        pushSelectionLimitNotification(resources, it)
                    } else if (isExceededItemFileSize(id)) {
                        pushSizeLimitNotification(resources, sizeInMBytesLimit!!)
                    } else {
                        selectedItemsIds.add(id)
                    }
                } ?: illegalState { "Selection limit can't be null" }
            }
            updateCheckboxCount()
            updateTitle()
        } ?: illegalState { "Id of current item can't be null" }
        return true
    }

    private fun selectItemForSingleSelectionMode(): Boolean {
        currentId?.let {
            if (selectedItemsIds.isEmpty()) {
                selectedItemsIds.add(it)
            } else if (selectedItemsIds.contains(currentId)) {
                selectedItemsIds.remove(it)
            } else {
                selectedItemsIds.clear()
                selectedItemsIds.add(it)
            }
            updateCheckboxCount()
            updateTitle()
        } ?: illegalState { "Id of current item can't be null" }
        return true
    }

    private fun isExceededItemFileSize(currentId: Int): Boolean {
        val currentFileSize =
            when (val currentArg = presenter.adapterData.firstOrNull { it.id.toIntOrNull() == currentId }) {
                is GalleryImageViewerArgs -> currentArg.fileSize
                is GalleryVideoViewerArgs -> currentArg.fileSize
                else -> null
            }
        val sizeLimit = sizeInMBytesLimit
        return selectionMode is GallerySelectionMode.Multiple &&
            sizeLimit != null &&
            sizeLimit != 0 &&
            currentFileSize != null &&
            currentFileSize > sizeLimit * TO_BYTES_COEFFICIENT
    }

    override fun onViewGoneBySwipe() {
        finishWithData(isSelectionConfirmed = false)
    }

    override fun finishWithData(isSelectionConfirmed: Boolean) {
        val intent = Intent()
        intent.putExtra(VIEWER_SELECTED_IDS_RESULT_EXTRA_KEY, selectedItemsIds.toIntArray())
        intent.putExtra(VIEWER_IS_SELECTION_CONFIRMED_RESULT_EXTRA_KEY, isSelectionConfirmed)
        setResult(intent)
        finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)
        return true
    }

    override fun changeImmersiveModeSupport(isImmersiveModeSupported: Boolean) {
        super.changeImmersiveModeSupport(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(SELECTED_ITEMS_SAVE_KEY, selectedItemsIds.toIntArray())
        currentId?.let {
            outState.putInt(CURRENT_ID_SAVE_KEY, it)
        } ?: illegalState { "Id of current item can't be null" }
    }
}
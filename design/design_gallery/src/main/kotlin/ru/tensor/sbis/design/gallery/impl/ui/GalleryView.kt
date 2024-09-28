package ru.tensor.sbis.design.gallery.impl.ui

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.databinding.DesignGalleryFragmentMainBinding
import ru.tensor.sbis.design.gallery.decl.GalleryMode
import ru.tensor.sbis.design.gallery.impl.ui.adapter.GalleryAdapter
import ru.tensor.sbis.design.gallery.impl.ui.adapter.GalleryAlbumsAdapter
import ru.tensor.sbis.design.gallery.impl.ui.adapter.GalleryItemsAdapter
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryAlbumItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryCameraPreviewItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryEventMvi
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryModel
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess
import ru.tensor.sbis.design.list_utils.decoration.dsl.decorate
import ru.tensor.sbis.design.list_utils.decoration.dsl.offsets
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.extentions.setLeftMargin
import ru.tensor.sbis.design.files_picker.R as RPicker

internal class GalleryView(
    private val context: Context,
    private val binding: DesignGalleryFragmentMainBinding,
    private val viewLifecycleOwner: LifecycleOwner,
    private val mode: GalleryMode,
    private val itemsCount: Int,
    private var firstVisibleItemPosition: Int?,
    isNeedBottomPadding: Boolean
) : BaseMviView<GalleryModel, GalleryEventMvi>(), GalleryClickHandler {

    private val adapter: GalleryAdapter?
        get() = binding.galleryList.adapter as? GalleryAdapter
    private var lastHasStorageStubValue: Boolean? = null
    private val isTablet = DeviceConfigurationUtils.isTablet(context)

    init {
        binding.galleryBar.setOnClickListener { dispatch(GalleryEventMvi.BackPressed) }
        binding.galleryAddButton.setOnClickListener { dispatch(GalleryEventMvi.AddButtonClick) }
        binding.galleryCancelButton.setOnClickListener { dispatch(GalleryEventMvi.CancelButtonClick) }
        binding.galleryStubButton.setOnClickListener { dispatch(GalleryEventMvi.MainStubClick) }
        if (isNeedBottomPadding) {
            binding.galleryList.updatePadding(
                bottom = binding.root.resources.getDimensionPixelSize(
                    RPicker.dimen.design_files_picker_content_bottom_padding
                )
            )
        }
    }

    override val renderer: ViewRenderer<GalleryModel> = with(binding) {
        diff {
            diff(
                get = { it },
                set = { model ->
                    galleryButtons.isVisible = mode is GalleryMode.ByAlbums && model !is GalleryModel.Loading
                    initListVisibility(model)
                    initToolbarVisibility(model)
                    initStubsVisible(model)
                    galleryAddButton.isEnabled = false
                    when (model) {
                        is GalleryModel.Content.Media ->
                            diff<GalleryModel.Content.Media> {
                                diff(
                                    get = { it },
                                    set = {
                                        lastHasStorageStubValue = it.hasStorageStub
                                        val layoutManager = createLayoutManager()
                                        if (
                                            adapter !is GalleryItemsAdapter &&
                                            layoutManager.javaClass != galleryList.layoutManager?.javaClass
                                        ) {
                                            val adapter = GalleryItemsAdapter(this@GalleryView, viewLifecycleOwner)
                                            if (layoutManager is StaggeredGridLayoutManager) {
                                                adapter.registerAdapterDataObserver(
                                                    createAdapterObserver(galleryList, layoutManager)
                                                )
                                            }
                                            galleryList.adapter = adapter
                                            galleryList.layoutManager = layoutManager
                                        }
                                        (galleryList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                                            false
                                        setItemsDecorations(galleryList)
                                        adapter?.setContent(it.items)
                                        firstVisibleItemPosition?.let { position ->
                                            galleryList.scrollToPosition(position)
                                            firstVisibleItemPosition = null
                                        }
                                        if (mode is GalleryMode.ByAlbums) {
                                            galleryAddButton.isEnabled = it.isEnabledAddButton
                                            if (!it.hasStorageStub) {
                                                val config = it.barConfig
                                                if (it.hasNoStubs) {
                                                    galleryTitle.text = config.title
                                                    galleryTitle.setLeftMargin(Offset.S.getDimenPx(context))
                                                    galleryBackArrow.isVisible = config.hasBackArrow
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                                .render(model)

                        is GalleryModel.Content.Albums ->
                            diff<GalleryModel.Content.Albums> {
                                diff(
                                    get = { it },
                                    set = {
                                        galleryAddButton.isEnabled = it.isEnabledAddButton
                                        if (adapter !is GalleryAlbumsAdapter) {
                                            galleryList.adapter = GalleryAlbumsAdapter(this@GalleryView)
                                            galleryList.layoutManager = LinearLayoutManager(context)
                                        }
                                        adapter?.setContent(model.items)
                                        galleryBackArrow.visibility = View.GONE
                                        galleryTitle.apply {
                                            isVisible = true
                                            text = context.getString(R.string.design_gallery_albums_title)
                                            setLeftMargin(Offset.L.getDimenPx(context))
                                        }
                                    }
                                )
                            }
                                .render(model)

                        else -> Unit
                    }
                }
            )
        }
    }

    private fun initToolbarVisibility(model: GalleryModel) {
        val isVisible =
            if (model is GalleryModel.Content.Media && mode is GalleryMode.ByAlbums) {
                model.hasNoStubs && !model.hasNoItems
            } else {
                model is GalleryModel.Content.Albums
            }
        binding.galleryBar.isVisible = isVisible
        binding.galleryTitle.isVisible = isVisible
        binding.galleryBackArrow.isVisible = isVisible
        if (isTablet && mode is GalleryMode.ByAlbums) {
            binding.root.updatePadding(top = Offset.ST.getDimenPx(context))
        }
    }

    private fun initListVisibility(model: GalleryModel) {
        val isVisible =
            if (model is GalleryModel.Content.Media && mode is GalleryMode.ByAlbums) {
                model.hasStorageStub || !model.hasNoItems
            } else {
                model is GalleryModel.Content
            }
        binding.galleryList.isVisible = isVisible
    }

    private fun initStubsVisible(model: GalleryModel) {
        changeEmptyStubVisibility(model)
        changeMainStubVisibility(isVisible = model is GalleryModel.Stub)
    }

    private fun isVisibleEmptyGalleryStub(model: GalleryModel) =
        model is GalleryModel.Content.Media && mode is GalleryMode.ByAlbums && model.hasNoItems && !model.hasStorageStub

    override fun onAlbumItemClick(item: GalleryAlbumItemVM) {
        dispatch(GalleryEventMvi.AlbumClicked(item.id))
    }

    override fun onMediaItemClick(item: GalleryItemVM) {
        dispatch(GalleryEventMvi.ItemClicked(item.id))
    }

    override fun onCheckboxClick(item: GalleryItemVM) {
        dispatch(GalleryEventMvi.ItemCheckboxClicked(item.id))
    }

    override fun onCameraPreviewClick() {
        logAttachProcess("onCameraPreviewClick")
        dispatch(GalleryEventMvi.CameraPreviewClick)
    }

    override fun onCameraStubClick() {
        dispatch(GalleryEventMvi.CameraStubClick)
    }

    override fun onStorageStubClick() {
        dispatch(GalleryEventMvi.StorageStubClick)
    }

    private fun changeMainStubVisibility(isVisible: Boolean) {
        binding.galleryStubButton.isVisible = isVisible
        binding.galleryStubMessage.isVisible = isVisible
        binding.galleryStubIcon.isVisible = isVisible
    }

    private fun changeEmptyStubVisibility(model: GalleryModel) {
        val emptyStubContent =
            ImageStubContent(
                imageType = StubViewImageType.ETC,
                messageRes = R.string.design_gallery_stub_empty_message,
                details = null
            )
        binding.galleryStubEmpty.setContent(emptyStubContent)
        binding.galleryStubEmpty.isVisible = isVisibleEmptyGalleryStub(model)
    }

    private fun setItemsDecorations(galleryList: RecyclerView) {
        while (galleryList.itemDecorationCount > 0) {
            galleryList.removeItemDecorationAt(0)
        }
        galleryList.decorate {
            val offset = Offset.X3S.getDimenPx(context)
            offsets {
                left = offset
                top = offset
                right = offset
                bottom = offset
            }
        }
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        val orientation = context.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet) {
            GridLayoutManager(context, itemsCount).apply {
                spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val item = adapter?.getItem(position)
                        return if (position == 0 &&
                            item is GalleryCameraPreviewItemVM &&
                            lastHasStorageStubValue == false
                        ) {
                            2
                        } else {
                            1
                        }
                    }
                }
            }
        } else {
            StaggeredGridLayoutManager(itemsCount, LinearLayoutManager.VERTICAL)
        }
    }

    private fun createAdapterObserver(
        galleryList: RecyclerView,
        layoutManager: StaggeredGridLayoutManager
    ): RecyclerView.AdapterDataObserver =
        object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0 && itemCount == 1) {
                    galleryList.smoothScrollToPosition(0)
                    galleryList.postDelayed(
                        { layoutManager.invalidateSpanAssignments() }, 100
                    )
                }
            }
        }
}
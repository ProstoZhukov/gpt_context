package ru.tensor.sbis.design.picker_files_tab.view.ui

import android.view.View
import androidx.core.view.isVisible
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.design.picker_files_tab.R
import ru.tensor.sbis.design.picker_files_tab.databinding.PickerFilesTabFragmentBinding
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * MVI View для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
internal interface PickerFilesTabView : MviView<PickerFilesTabView.ViewModel, PickerFilesTabView.Event> {

    /**
     * Фабрика [PickerFilesTabView].
     */
    fun interface Factory : (View) -> PickerFilesTabView

    /**
     * UI события от [PickerFilesTabView].
     */
    sealed interface Event {

        /**
         * Клик по плашке "Выбрать из галереи"
         */
        object OnGalleryFolderClick : Event

        /**
         * Клик по плашке "Выбрать из файлов"
         */
        object OnStorageFolderClick : Event
    }

    /**
     * UI модель для [PickerFilesTabView].
     */
    data class ViewModel(val isGalleryVisible: Boolean)
}

/**
 * Реализация MVI View для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
internal class PickerFilesTabViewImpl(
    private val binding: PickerFilesTabFragmentBinding
) : BaseMviView<PickerFilesTabView.ViewModel, PickerFilesTabView.Event>(), PickerFilesTabView {

    init {
        with(binding) {
            docviewGalleryFolder.dispatchOnClick(PickerFilesTabView.Event.OnGalleryFolderClick)
            docviewStorageFolder.dispatchOnClick(PickerFilesTabView.Event.OnStorageFolderClick)
            pickerFilesTabStubView.setContent(
                ImageStubContent(
                    imageType = StubViewCase.NO_FILES.imageType,
                    message = null,
                    detailsRes = R.string.picker_files_tab_stub_desc
                )
            )
        }
    }

    override val renderer: ViewRenderer<PickerFilesTabView.ViewModel> = diff {
        diff(
            get = PickerFilesTabView.ViewModel::isGalleryVisible,
            set = { binding.docviewGalleryFolder.isVisible = it }
        )
    }

    private fun View.dispatchOnClick(event: PickerFilesTabView.Event) {
        setOnClickListener { dispatch(event) }
    }
}
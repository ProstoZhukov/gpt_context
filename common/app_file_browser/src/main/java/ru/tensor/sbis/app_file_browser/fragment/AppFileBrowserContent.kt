package ru.tensor.sbis.app_file_browser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.app_file_browser.data.Crud3MobileFileControllerWrapper
import ru.tensor.sbis.app_file_browser.databinding.AppFileBrowserPanelContentBinding
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeatureImpl
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeatureInternal
import ru.tensor.sbis.app_file_browser.presentation.AppFileBrowserViewModel
import ru.tensor.sbis.app_file_browser.presentation.AppFileBrowserViewModelFactory
import ru.tensor.sbis.app_file_browser.presentation.EmptyFolderStubViewContentFactory
import ru.tensor.sbis.app_file_browser.presentation.Mapper
import ru.tensor.sbis.app_file_browser.util.getThemedContext
import ru.tensor.sbis.crud3.createListComponentViewViewModel
import ru.tensor.sbis.crud3.view.StubFactoryOneForAll
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerAs

/**
 * Содержимое файлового браузера - шторка с шапкой и списком файлов и папок.
 *
 * @author us.bessonov
 */
internal class AppFileBrowserContent : Fragment(), Content {

    private var disposable: Disposable? = null
    private val feature: AppFileBrowserFeatureInternal by viewModels<AppFileBrowserFeatureImpl>({ findHostOwner() })
    private val listComponentViewModel by lazy {
        createListComponentViewViewModel(
            this@AppFileBrowserContent,
            lazy { Crud3MobileFileControllerWrapper(feature.controller) },
            lazy { Mapper(lazy { viewModel::onSelectionChanged }, lazy { viewModel::onShowItemSize }, lazy { viewModel::onDeleteItem }) },
            lazy { StubFactoryOneForAll(EmptyFolderStubViewContentFactory) }
        )
    }
    private val viewModel: AppFileBrowserViewModel by viewModels(
        factoryProducer = { AppFileBrowserViewModelFactory(feature, listComponentViewModel) }
    )
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            this@AppFileBrowserContent.containerAs<Container.Closeable>()?.closeContainer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher
            .addCallback(this, backPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return AppFileBrowserPanelContentBinding
            .inflate(inflater.cloneInContext(getThemedContext(requireContext())), container, false)
            .apply {
                listComponentViewModel.apply {
                    appFileBrowserListComponent.bindViewModel(this)
                    onItemClick.observe(viewLifecycleOwner) {
                        viewModel.onFolderClicked(it)
                    }
                }
                appFileBrowserCurrentFolder.setOnClickListener {
                    viewModel.onGoBackClicked()
                }

                disposable = listComponentViewModel.dataChangeMapped
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateCurrentFolder(appFileBrowserCurrentFolder)
                    }

                updateCurrentFolder(appFileBrowserCurrentFolder)
            }.root
    }

    override fun onBackPressed(): Boolean = false

    private fun updateCurrentFolder(currentFolder: CurrentFolderView) = with(currentFolder) {
        setTitle(viewModel.currentFolder.value!!)
        isVisible = viewModel.isCurrentFolderVisible.value!!
    }

    private fun findHostOwner(): ViewModelStoreOwner {
        return requireParentFragment()
            // Отображается в контейнере, поэтому hostOwner это parentFragment контейнера
            .run { parentFragment ?: requireActivity() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
        disposable = null
    }
}
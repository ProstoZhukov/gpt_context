package ru.tensor.sbis.design.files_picker.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat.isNestedScrollingEnabled
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.common.util.findOrCreateViewModel
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.files_picker.SbisFilesPickerPlugin
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.feature.SbisFilesPickerImpl
import ru.tensor.sbis.design.files_picker.view.ui.FilesPickerViewImpl
import ru.tensor.sbis.design.files_picker.R
import ru.tensor.sbis.design.files_picker.databinding.FilesPickerV2FragmentBinding
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.view.store.FilesPickerExecutor
import ru.tensor.sbis.design.files_picker.view.store.FilesPickerReducer
import ru.tensor.sbis.design.files_picker.view.store.FilesPickerStoreFactory
import ru.tensor.sbis.design.files_picker.view.ui.FilesPickerController
import ru.tensor.sbis.design.files_picker.view.ui.FilesPickerRouter
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegate
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.mvi_extension.LabelBufferStrategy

/**
 * Экран "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerFragment : BaseFragment() {

    companion object {

        private const val CONFIG_ARG_KEY = "FilesPickerFragment.CONFIG_ARG_KEY"

        fun newInstance(config: FilesPickerConfig): FilesPickerFragment =
            FilesPickerFragment().withArgs {
                putParcelable(CONFIG_ARG_KEY, config)
            }
    }

    private var controller: FilesPickerController? = null
    private val containerMovableDelegate: ContainerMovableDelegate? by lazy {
        parentFragment as? ContainerMovableDelegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            val config: FilesPickerConfig = requireArguments().getParcelableUniversally(CONFIG_ARG_KEY) ?: kotlin.run {
                illegalState { "Not found config." }
                return@launch
            }
            val baseStoreFactory: StoreFactory = if (BuildConfig.DEBUG) {
                LoggingStoreFactory(AndroidStoreFactory(TimeTravelStoreFactory(), LabelBufferStrategy.Buffer()))
            } else {
                AndroidStoreFactory(DefaultStoreFactory(), LabelBufferStrategy.Buffer())
            }
            val reducer = FilesPickerReducer()
            val tabFeatures: List<SbisFilesPickerTabFeature<*>> = getTabFeatures(tabs = config.tabs)
            val filesPickerFeature: SbisFilesPickerImpl = findOrCreateViewModel(
                storeOwner = findParentStoreOwnerByClass(
                    fragment = requireParentFragment(),
                    storeOwnerClass = config.featureStoreOwnerClass
                ),
                key = config.featureKey
            ) {
                SbisFilesPickerImpl(config.featureKey, config.featureStoreOwnerClass)
            }
            val executorFactory = {
                FilesPickerExecutor(
                    tabFeatures = tabFeatures,
                    filesPickerFeature = filesPickerFeature
                )
            }
            val storeFactory = FilesPickerStoreFactory(
                storeFactory = baseStoreFactory,
                reducer = reducer,
                executorFactory = executorFactory,
                tabFeatures = tabFeatures
            )
            val viewFactory = { view: View ->
                FilesPickerViewImpl(
                    view = view,
                    containerMovableDelegate = containerMovableDelegate
                )
            }
            val router = FilesPickerRouter(contentContainerId = R.id.filesPickerContentContainer)
            controller = FilesPickerController(
                fragment = this@FilesPickerFragment,
                storeFactory = storeFactory,
                viewFactory = viewFactory,
                router = router
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FilesPickerV2FragmentBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenIfNeed()
        containerMovableDelegate?.setCurrentScrollViewProvider { findScrollView(view) }
    }

    override fun onBackPressed(): Boolean =
        controller?.onBackPressed() ?: false

    private fun findParentStoreOwnerByClass(fragment: Fragment, storeOwnerClass: Class<Any>): ViewModelStoreOwner {
        if (fragment.javaClass == storeOwnerClass) {
            return fragment
        }
        val parentFragment = fragment.parentFragment
            ?: kotlin.run {
                illegalState { "Not found parentStoreOwner." }
                return requireActivity()
            }
        return findParentStoreOwnerByClass(parentFragment, storeOwnerClass)
    }

    private fun setFullScreenIfNeed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            containerMovableDelegate?.setPeekHeight(ContainerMovableDelegateImpl.PeekHeightType.EXPANDED)
            @Suppress("DEPRECATION")
            (parentFragment as? DialogFragment)?.dialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun findScrollView(view: View): View? {
        // Если view невидима, то нет смысла её смотреть
        if (view.isVisible.not()) {
            return null
        }
        // Если view isNestedScrolling, то учитываем её скролл
        if (isNestedScrollingEnabled(view)) {
            return view
        }
        // Если view является контейнером, то пытаемся найти скролл среди детей,
        // начиная с самого вверхнего для пользователя
        if (view is ViewGroup) {
            for (i in view.childCount - 1 downTo 0) {
                val child = view.getChildAt(i)
                val scrollingChild = findScrollView(child)
                if (scrollingChild != null) {
                    return scrollingChild
                }
            }
        }
        return null
    }

    private fun getTabFeatures(tabs: Set<SbisFilesPickerTab>): List<SbisFilesPickerTabFeature<*>> {
        val features = mutableListOf<SbisFilesPickerTabFeature<*>>()
        tabs.forEach { inputTab ->
            SbisFilesPickerPlugin.tabRegistrar
                .getTabFeature(
                    tab = inputTab,
                    storeOwner = this@FilesPickerFragment
                )
                ?.let(features::add)
        }
        return features
    }
}
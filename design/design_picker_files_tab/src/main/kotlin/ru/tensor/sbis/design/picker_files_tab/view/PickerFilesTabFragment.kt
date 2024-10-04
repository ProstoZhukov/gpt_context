package ru.tensor.sbis.design.picker_files_tab.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.findOrCreateViewModel
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.picker_files_tab.PickerFilesTabPlugin
import ru.tensor.sbis.design.picker_files_tab.databinding.PickerFilesTabFragmentBinding
import ru.tensor.sbis.design.picker_files_tab.feature.PickerFilesTabFeature
import ru.tensor.sbis.design.picker_files_tab.view.di.DaggerPickerFilesTabDIComponent
import ru.tensor.sbis.design.picker_files_tab.view.ui.PickerFilesTabController
import javax.inject.Inject
import ru.tensor.sbis.design.picker_files_tab.R
import ru.tensor.sbis.design.picker_files_tab.view.ui.PickerFilesTabViewImpl

/**
 * Экран "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
internal class PickerFilesTabFragment : BaseFragment() {

    companion object {

        private const val CONFIG_ARG_KEY = "PickerFilesTabFragment.CONFIG_ARG_KEY"

        fun newInstance(config: PickerFilesTabConfig): PickerFilesTabFragment =
            PickerFilesTabFragment().withArgs {
                putParcelable(CONFIG_ARG_KEY, config)
            }
    }

    @Inject
    lateinit var controller: PickerFilesTabController

    private var binding: PickerFilesTabFragmentBinding? = null

    private val config: PickerFilesTabConfig by lazy {
        arguments?.getParcelableUniversally(CONFIG_ARG_KEY)!!
    }
    private val tabFeature: PickerFilesTabFeature by lazy {
        findOrCreateViewModel(requireParentFragment()) {
            PickerFilesTabFeature(config.tab)
        }
    }
    private val storageResultLauncher: ActivityResultLauncher<Intent> by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK && data != null) {
                controller.onSelectFilesFromStorage(data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerPickerFilesTabDIComponent.factory()
            .create(
                application = PickerFilesTabPlugin.application,
                fragment = this,
                viewFactory = { PickerFilesTabViewImpl(binding!!) },
                config = config,
                tabFeature = tabFeature,
                galleryComponentFactory = PickerFilesTabPlugin.galleryComponentFactoryProvider.get(),
                containerId = R.id.fragmentContainer,
                storageResultLauncher = storageResultLauncher
            )
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        PickerFilesTabFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onBackPressed(): Boolean = controller.onBackPressed()
}
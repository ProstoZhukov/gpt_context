package ru.tensor.sbis.version_checker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.databinding.VersioningFragmentSettingsVersionUpdateDebugBinding
import ru.tensor.sbis.version_checker.ui.settings.viewmodel.SettingsVersionUpdateDebugViewModelImpl
import ru.tensor.sbis.version_checker.ui.settings.viewmodel.SettingsVersionUpdateDebugVmFactory
import ru.tensor.sbis.version_checker_decl.VersionedComponent
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus
import javax.inject.Inject

/**
 * Экран настроек отладки обновлений
 *
 * @author us.bessonov
 */
internal class SettingsVersionUpdateDebugFragment :
    Fragment(),
    VersionedComponent {

    @Inject
    lateinit var viewModelFactory: SettingsVersionUpdateDebugVmFactory

    private val viewModel: SettingsVersionUpdateDebugViewModelImpl by viewModels(factoryProducer = {
        viewModelFactory
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        VersionCheckerPlugin.versioningComponent
            .debugComponentFactory()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return VersioningFragmentSettingsVersionUpdateDebugBinding.inflate(inflater, container, false)
            .apply {
                initView()
                initClickListeners()
            }.root
    }

    private fun VersioningFragmentSettingsVersionUpdateDebugBinding.initView() {
        initClickListeners()
        initVersionNumber()
        viewModel.selectedStatus.observe(viewLifecycleOwner) { setSelectedType(it) }
    }

    private fun VersioningFragmentSettingsVersionUpdateDebugBinding.initClickListeners() {
        val selectRecommended = View.OnClickListener { viewModel.setSelectedUpdateStatus(UpdateStatus.Recommended) }
        val selectMandatory = View.OnClickListener { viewModel.setSelectedUpdateStatus(UpdateStatus.Mandatory) }

        versioningUpdateRecommended.setOnClickListener(selectRecommended)
        versioningUpdateRecommendedMark.setOnClickListener(selectRecommended)
        versioningUpdateMandatory.setOnClickListener(selectMandatory)
        versioningUpdateMandatoryMark.setOnClickListener(selectMandatory)
    }

    private fun VersioningFragmentSettingsVersionUpdateDebugBinding.initVersionNumber() =
        with(versioningVersionNumber) {
            value = viewModel.version
            onValueChanged = { _, value -> viewModel.onVersionChanged(value) }
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) KeyboardUtils.hideKeyboard(v)
            }
        }

    private fun VersioningFragmentSettingsVersionUpdateDebugBinding.setSelectedType(status: UpdateStatus) {
        versioningUpdateMandatoryMark.isVisible = status == UpdateStatus.Mandatory
        versioningUpdateRecommendedMark.isVisible = status == UpdateStatus.Recommended
    }
}
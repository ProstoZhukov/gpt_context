package ru.tensor.sbis.logging.settings.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.design.list_utils.decoration.drawer.divider.SolidDividerDrawer
import ru.tensor.sbis.design.list_utils.decoration.dsl.decorate
import ru.tensor.sbis.design.theme.global_variables.SeparatorColor
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.logging.LoggingComponentProvider
import ru.tensor.sbis.logging.databinding.LoggingSettingsFragmentBinding
import ru.tensor.sbis.logging.settings.model.CategoryVm
import ru.tensor.sbis.logging.settings.viewModel.LogSettingsViewModel
import javax.inject.Inject
import ru.tensor.sbis.design.R as RDesign

/**
 * Фрагмент экрана настроек логирования.
 */
class LogSettingsFragment : BaseFragment() {

    @Suppress("ProtectedInFinal")
    @Inject
    internal lateinit var viewModel: LogSettingsViewModel

    internal interface Host {
        fun onChangeCategory(category: CategoryVm?)
        fun onUpdateOption()
    }

    override fun onAttach(context: Context) {
        LoggingComponentProvider.get(context)
            .logSettingsComponentBuilder()
            .with(this)
            .build()
            .inject(this)

        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return LoggingSettingsFragmentBinding.inflate(inflater)
            .setupList()
            .root
    }

    private fun LoggingSettingsFragmentBinding.setupList(): LoggingSettingsFragmentBinding {
        val logSettingsAdapter = LogSettingsAdapter(
            categoryClick = {
                viewModel.onCategoryClick(it)
                (parentFragment as? Host)?.onChangeCategory(it)
            },
            optionClick = viewModel::onOptionClick,
            wifiUploadChange = viewModel::onChangeWifiUpload,
            logQueryPlanChange = viewModel::onChangeLogQueryPlan
        )
        loggingRecyclerView.apply {

            adapter = logSettingsAdapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)

            decorate {
                setDrawer(
                    SolidDividerDrawer(
                        SeparatorColor.DEFAULT.getValue(context),
                        false,
                        resources.getDimensionPixelSize(RDesign.dimen.common_separator_size)
                    )
                )
            }
        }
        with(viewModel) {
            items.observe(viewLifecycleOwner) { items ->
                items?.also {
                    logSettingsAdapter.reload(it)
                }
            }

            successUpdateOption.observe(viewLifecycleOwner) {
                (parentFragment as? Host)?.onUpdateOption()
            }

            toastMsg.observe(viewLifecycleOwner) { message ->
                message?.also {
                    SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, it)
                }
            }
        }

        return this
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onBackPressed() = viewModel.onBackPressed()
}
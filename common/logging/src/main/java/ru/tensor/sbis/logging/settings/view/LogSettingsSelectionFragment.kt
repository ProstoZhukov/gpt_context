package ru.tensor.sbis.logging.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.selection.shadow.RecyclerViewVisibilityDispatcher
import ru.tensor.sbis.base_components.fragment.selection.shadow.ShadowVisibilityDispatcher
import ru.tensor.sbis.common_filters.FilterWindowHeaderItem
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.settings.model.CategoryVm
import ru.tensor.sbis.logging.settings.presenter.LogSettingsContract
import ru.tensor.sbis.logging.settings.presenter.LogSettingsPresenterImpl
import ru.tensor.sbis.mvp.fragment.selection.SelectionWindowContent
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Фрагмент обертка, для показа настроек логирования в виде реестра.
 *
 * @author av.krymov
 */
internal class LogSettingsSelectionFragment :
    SelectionWindowContent<LogSettingsContract.View, LogSettingsContract.Presenter>(),
    LogSettingsFragment.Host, LogSettingsContract.View {

    class Creator : ContentCreator {
        override fun createFragment(): Fragment {
            return LogSettingsSelectionFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().theme.applyStyle(R.style.LoggingSelectionWindow, true)
        return super.onCreateView(inflater, container, savedInstanceState).also { changeHeader(null) }
    }

    override fun onCloseClick() {
        onBackPressed()
    }

    //Передача обработки нажатия кнопки назад дочернему фрагменту
    override fun onBackPressed(): Boolean {
        changeHeader(null)
        val fragment =
            childFragmentManager.findFragmentById(RBaseComponents.id.base_components_content_container) as BaseFragment
        if (!fragment.onBackPressed()) closeWindow()
        return true
    }

    override fun onChangeCategory(category: CategoryVm?) = changeHeader(category)

    override fun onUpdateOption() = changeHeader(null)

    private fun changeHeader(category: CategoryVm?) {
        setHeaderViewModel(
            FilterWindowHeaderItem(
                titleRes = category?.headerTitle ?: R.string.logging_settings_header_title_root,
                hasBackArrow = category != null,
                onBackClick = { onBackPressed() }
            )
        )
    }

    // region SelectionWindowContent
    override fun inject() {
        // ignore
    }

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        childFragmentManager.findFragmentById(RBaseComponents.id.base_components_content_container).also { fragment ->
            if (fragment == null) {
                childFragmentManager.beginTransaction()
                    .add(
                        RBaseComponents.id.base_components_content_container,
                        LogSettingsFragment()
                    )
                    .commit()
            }
        }
    }

    override fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher = RecyclerViewVisibilityDispatcher()

    override fun getContentViewId(): Int = R.id.logging_recycler_view

    override fun createPresenter(): LogSettingsContract.Presenter = LogSettingsPresenterImpl()

    override fun getPresenterView(): LogSettingsContract.View = this
    // endregion
}
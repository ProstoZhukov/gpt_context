package ru.tensor.sbis.localfeaturetoggle.presentation.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.localfeaturetoggle.databinding.LocalFeatureToggleFragmentBinding
import ru.tensor.sbis.localfeaturetoggle.presentation.ui.adapter.LocalFeatureToggleLastItemOffsetDecorator
import ru.tensor.sbis.localfeaturetoggle.presentation.ui.adapter.LocalFeatureToggleAdapter

/**
 * Отвечает за обработку событий (Intent) и отрисовку изменений состояний, приходящих из модели.
 *
 * @author mb.kruglova
 */
internal class LocalFeatureToggleViewImpl(
    root: View
) : BaseMviView<LocalFeatureToggleView.Model, LocalFeatureToggleView.Event>(), LocalFeatureToggleView {

    private var binding = LocalFeatureToggleFragmentBinding.bind(root)

    private val localFeatureToggleAdapter = LocalFeatureToggleAdapter { feature, isActivated ->
        dispatch(
            LocalFeatureToggleView.Event.ClickSwitchItem(
                feature,
                isActivated
            )
        )
    }

    init {
        binding.localFeatureToggleList.apply {
            layoutManager = LinearLayoutManager(root.context)
            adapter = localFeatureToggleAdapter
            addItemDecoration(LocalFeatureToggleLastItemOffsetDecorator())
        }
    }

    override val renderer: ViewRenderer<LocalFeatureToggleView.Model> =
        diff {
            diff(
                get = LocalFeatureToggleView.Model::listItems,
                set = { (binding.localFeatureToggleList.adapter as? LocalFeatureToggleAdapter)?.setList(it) }
            )
        }
}
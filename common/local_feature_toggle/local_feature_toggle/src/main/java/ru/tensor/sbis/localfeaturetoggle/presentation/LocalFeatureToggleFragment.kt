package ru.tensor.sbis.localfeaturetoggle.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType.PADDING
import ru.tensor.sbis.design.utils.insets.Position.TOP
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.localfeaturetoggle.R
import ru.tensor.sbis.localfeaturetoggle.presentation.di.DaggerLocalFeatureToggleComponent
import ru.tensor.sbis.localfeaturetoggle.presentation.ui.LocalFeatureToggleViewImpl

/**
 * Фрагмент Фичетогл.
 *
 * @author mb.kruglova
 */
class LocalFeatureToggleFragment :
    Fragment(R.layout.local_feature_toggle_fragment),
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerLocalFeatureToggleComponent.factory().create(
            context = requireContext(),
            viewFactory = {
                LocalFeatureToggleViewImpl(it)
            }
        ).injector().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInsetListener(DefaultViewInsetDelegateParams(listOf(ViewToAddInset(requireView(), listOf(PADDING to TOP)))))
    }
}
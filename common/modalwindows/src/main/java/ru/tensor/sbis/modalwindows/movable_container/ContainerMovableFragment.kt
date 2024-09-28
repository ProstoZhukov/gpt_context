package ru.tensor.sbis.modalwindows.movable_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment.Builder

/**
 * Фрагмент-обертка над компонентом шторка [MovablePanel] в виде [BaseFragment]
 * По умолчанию два состояния, закрыто/открыто
 * Создается посредством [Builder]
 *
 * @author ga.malinskiy
 */
class ContainerMovableFragment : BaseFragment(),
    ContainerMovableDelegate by ContainerMovableDelegateImpl(true) {

    companion object {
        private fun newInstance(newArgs: Bundle) = ContainerMovableFragment().apply { arguments = newArgs }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popBackStack = { getSupportFragmentManager().popBackStack() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        createView(inflater, container, requireArguments())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated(requireView(), activity, childFragmentManager)
    }

    override fun onDestroyView() {
        destroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        destroy()
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        backPressed()
        return true
    }

    class Builder : ContainerMovableDelegateImpl.AbstractBuilder<ContainerMovableFragment>() {

        override fun build(): ContainerMovableFragment = newInstance(bundle)
    }
}
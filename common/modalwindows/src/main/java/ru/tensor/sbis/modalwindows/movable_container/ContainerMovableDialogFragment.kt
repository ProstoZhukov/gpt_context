package ru.tensor.sbis.modalwindows.movable_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.WindowCompat
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.Companion.SOFT_INPUT_MODE
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment.Builder

/**
 * Фрагмент-обертка над компонентом шторка [MovablePanel] в виде [AppCompatDialogFragment]
 * По умолчанию два состояния, закрыто/открыто
 * Создается посредством [Builder]
 *
 * @author ga.malinskiy
 */
class ContainerMovableDialogFragment : AppCompatDialogFragment(),
    ContainerMovableDelegate by ContainerMovableDelegateImpl(false) {

    companion object {
        private fun newInstance(newArgs: Bundle) =
            ContainerMovableDialogFragment().apply { arguments = newArgs }
    }

    override fun getTheme(): Int =
        context?.getDataFromAttrOrNull(R.attr.containerMovableDialogTheme, false) ?: R.style.ContainerMovableDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popBackStack = {
            if (isAdded) {
                if (isStateSaved) super.dismissAllowingStateLoss() else super.dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        createView(inflater, container, requireArguments())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
                requireArguments().getInt(SOFT_INPUT_MODE, -1)
                    .takeIf { mode -> mode != -1 }
                    ?.also { mode -> it.setSoftInputMode(mode) }
            }
            (this as AppCompatDialog).onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        backPressed()
                    }
                })

        }
        viewCreated(requireView(), activity, childFragmentManager)
    }

    override fun onDestroyView() {
        destroyView()
        super.onDestroyView()
    }

    override fun dismiss() {
        requestCloseContent()
        super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        requestCloseContent()
        super.dismissAllowingStateLoss()
    }

    class Builder : ContainerMovableDelegateImpl.AbstractBuilder<ContainerMovableDialogFragment>() {

        override fun build(): ContainerMovableDialogFragment = newInstance(bundle)
    }
}
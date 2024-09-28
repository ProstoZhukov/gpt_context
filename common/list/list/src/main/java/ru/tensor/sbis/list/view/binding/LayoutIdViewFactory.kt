package ru.tensor.sbis.list.view.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Реализация фабрика для создания [View] с использованием ресурса [layoutId].
 */
class LayoutIdViewFactory(@LayoutRes private val layoutId: Int) : ViewFactory {

    override fun createView(parentView: ViewGroup): View {
        return LayoutInflater.from(parentView.context)
            .inflate(
                layoutId,
                parentView,
                false
            )
            .rootView
    }

    override fun getType() = layoutId
}
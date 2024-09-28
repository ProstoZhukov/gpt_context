package ru.tensor.sbis.main_screen_decl.env

import android.view.View
import android.view.ViewGroup

/**
 * Делегат реализует интерфейс [BottomBarContainer].
 * Для добавления и удаления View из контейнера ViewGroup с ленивой инициализацией.
 * Контейнер предоставляется снаружи через [setupContainer] метод.
 *
 * @author mb.kruglova
 */
class BottomBarContainerDelegate : BottomBarContainer {
    private var getContainerView: (() -> ViewGroup)? = null

    fun setupContainer(getContainerView: () -> ViewGroup) {
        this.getContainerView = getContainerView
    }

    override fun addView(view: View, position: Int) {
        getContainerView?.invoke()?.run {
            if (position > childCount - 1) {
                addView(view)
            } else {
                addView(view, position)
            }
        }
    }

    override fun removeView(containerId: Int) {
        getContainerView?.invoke()?.run { removeView(findViewById(containerId)) }
    }
}
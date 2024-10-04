package ru.tensor.sbis.design.container

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Интерфейс контента предоставляемого в виде View
 * @author ma.kolpakov
 */
interface ViewContent : Content {
    /**
     * Метод предоставляющий вью для отображения контейнера
     * @param containerFragment фрагмент контейнера
     * @param container родительская view в которой будет размещен контент
     */
    fun getView(containerFragment: SbisContainerImpl, container: ViewGroup): View

    /**
     * Этот метод будет вызван после того, как view уже создан,
     * в том числе при пересоздании контейнера ([containerFragment]).
     * В нем можно проводить дополнительную инициализацию диалога.
     */
    fun onViewCreated(containerFragment: SbisContainerImpl) = Unit
}

/**
 * Интерфейс контента предоставляемого в виде Fragment
 */
interface FragmentContent : Content {
    /**
     * Метод предоставляющий фрагмент для отображения контейнера
     * @param containerFragment фрагмент контейнера
     */
    fun getFragment(containerFragment: SbisContainerImpl): Fragment

    /**
     * Функция для получения восстановленного экземпляра фрагмента.
     * Будет вызвана каждый раз при восстановлении контейнера
     * @param fragment - восстановленный экземпляр фрагмента
     */
    fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment)
}
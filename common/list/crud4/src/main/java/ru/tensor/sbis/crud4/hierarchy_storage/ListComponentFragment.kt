package ru.tensor.sbis.crud4.hierarchy_storage

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.crud4.ListComponentViewViewModel
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.swipeback.SwipeBackLayout

/**
 * Прикладной фрагмент с иерархическим списком.
 *
 * @author ma.kolpakov
 */
abstract class ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENTIFIER> : SwipeBackFragment() {
    private val _swipeBackEVent = MutableLiveData(SwipeBackEvent.IDLE)
    internal val swipeBackEvent: LiveData<SwipeBackEvent> = _swipeBackEVent

    /**
     * Предоставить вью-модель иерархической коллекции
     */
    abstract fun getViewModel(): ListComponentViewViewModel<COLLECTION, *, FILTER, *, PATH_MODEL, IDENTIFIER>

    /**
     * Сменить корень для текущего списка по модели.
     *
     * Можно переопределить для дополнительных настроек списка при переходе в папку.
     */
    open fun changeRoot(parentFolder: PATH_MODEL?) = getViewModel().changeRoot(parentFolder)

    /**
     * Установить приоритетность отображения экрана.
     */
    open fun setPriority(isForegroundFragment: Boolean) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeBackLayout?.setOnSwipeBackListener(object : SwipeBackLayout.SwipeBackListener {
            override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) = Unit
            override fun onViewGoneBySwipe() = Unit

            override fun onStartSwipe() {
                _swipeBackEVent.postValue(SwipeBackEvent.START)
            }

            override fun onEndSwipe(isBack: Boolean) {
                _swipeBackEVent.postValue(if (isBack) SwipeBackEvent.END_BACK else SwipeBackEvent.END)
            }
        })
    }
}


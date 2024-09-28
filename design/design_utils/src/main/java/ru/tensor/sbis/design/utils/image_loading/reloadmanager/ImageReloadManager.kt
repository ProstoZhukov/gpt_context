package ru.tensor.sbis.design.utils.image_loading.reloadmanager

import android.annotation.SuppressLint
import android.view.View
import io.reactivex.Observable

/**
 * Инструмент для возможности повторной загрузки изображений у [View], в случае когда их не удалось из-за отсутствия
 * подключения к сети, либо загрузка не удалась до того как была переключена активная сеть
 *
 * @author us.bessonov
 */
object ImageReloadManager {

    private val attachedViews = mutableListOf<ImageLoadingView>()

    /** @SelfDocumented */
    @SuppressLint("CheckResult")
    fun initialize(networkStateObservable: Observable<Boolean>) {
        networkStateObservable
            .filter { isConnected -> isConnected }
            .subscribe { reloadFailedImages() }
    }

    /**
     * Регистрирует [View] для возможности перезагрузки изображений, как только подключение появится на устройстве
     */
    fun attach(view: ImageLoadingView) {
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = onViewAttachedToWindow(view)

            override fun onViewDetachedFromWindow(v: View) = onViewDetachedFromWindow(view)
        })
    }

    private fun onViewAttachedToWindow(v: ImageLoadingView) {
        attachedViews.add(v)
        if (v.isLoadingFailedBecauseIoException) v.reloadImage()
    }

    private fun onViewDetachedFromWindow(v: ImageLoadingView) {
        attachedViews.remove(v)
    }

    private fun reloadFailedImages() {
        attachedViews.filter { it.isLoading || it.isLoadingFailedBecauseIoException }
            .forEach { it.reloadImage() }
    }

}
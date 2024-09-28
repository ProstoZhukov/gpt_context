package ru.tensor.sbis.design.utils.image_loading.reloadmanager

import android.view.View
import java.io.IOException

/**
 * Контракт [View], осуществляющего загрузку изображения
 *
 * @author us.bessonov
 */
interface ImageLoadingView {

    /**
     * При загрузке изображения возникло исключение [IOException]
     */
    val isLoadingFailedBecauseIoException: Boolean

    /**
     * Осуществляется ли сейчас загрузка изображения
     */
    val isLoading: Boolean

    /** @SelfDocumented */
    fun addOnAttachStateChangeListener(listener: View.OnAttachStateChangeListener)

    /** @SelfDocumented */
    fun reloadImage()
}

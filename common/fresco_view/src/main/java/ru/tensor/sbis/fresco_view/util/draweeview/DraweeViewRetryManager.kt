package ru.tensor.sbis.fresco_view.util.draweeview

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.view.View
import com.facebook.drawee.controller.AbstractDraweeController
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.DraweeView
import io.reactivex.Observable
import ru.tensor.sbis.fresco_view.R
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager.attachToController
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager.attachToView
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager.initialize
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager.onFailure
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager.onIntermediateImageSet
import java.net.UnknownHostException

/**
 * Менеджер для повторения загрузки изображения в [DraweeView] при появлении сети
 *
 * Повторная загрузка происходит, только если ни один слой (Progressive JPEG или изображение низкого качества)
 * не был загружен (см. [onIntermediateImageSet]), и в [onFailure] в качестве исключения пришло [UnknownHostException]
 *
 * Перед использованием объект необходимо единожды проинициализировать через [initialize]
 *
 * Для повторения загрузки на конкретной [DraweeView] необходимо:
 * 1. Прикрепить объект к [DraweeView] через [attachToView]. Не следует прикреплять дважды
 * 2. Прикрепить объект к [AbstractDraweeController] через [attachToController]. Не следует прикреплять дважды, но
 * необходимо прикреплять к каждому новому [AbstractDraweeController]-у. Можно воспользоваться расширением [enableRetry]
 *
 * @author sa.nikitin
 */
object DraweeViewRetryManager : View.OnAttachStateChangeListener, ControllerListener<Any> {

    private data class DraweeViewState(var isAnyImageSet: Boolean = false, var isFinalImageFetchFailed: Boolean = false)

    private val attachedDraweeViews = mutableListOf<DraweeView<*>>()

    /**
     * Инициализировать объект
     *
     * @param networkStateObservable Наблюдаемое состояние сети: true, если сеть подключена, false - иначе
     */
    @SuppressLint("CheckResult")//Это объект, он не уничтожается, поэтому нет смысла в Disposable
    fun initialize(networkStateObservable: Observable<Boolean>) {
        networkStateObservable
            .filter { isConnected -> isConnected }
            .subscribe { fetchFailedDraweeViews().forEach { it.retry() } }
    }

    /**
     * Прикрепить к [DraweeView]. Объект добавляется как [View.OnAttachStateChangeListener]
     * Открепление не требуется, т.к. при очистке [DraweeView] очистятся и её [View.OnAttachStateChangeListener]-ы
     */
    @JvmStatic
    fun attachToView(draweeView: DraweeView<*>) {
        draweeView.addOnAttachStateChangeListener(this)
    }

    /**
     * Прикрепить к [AbstractDraweeController]. Объект добавляется как [ControllerListener]
     * Открепление не требуется, т.к. при очистке [AbstractDraweeController] очистятся и его [ControllerListener]-ы
     */
    @JvmStatic
    fun attachToController(controller: AbstractDraweeController<*, *>) {
        controller.addControllerListener(this)
    }

    //region OnAttachStateChangeListener
    override fun onViewAttachedToWindow(view: View) {
        if (view is DraweeView<*>) {
            if (view.state == null) {
                view.state = DraweeViewState()
            }
            attachedDraweeViews.add(view)
        }
    }

    override fun onViewDetachedFromWindow(view: View) {
        if (view is DraweeView<*>) {
            attachedDraweeViews.remove(view)
        }
    }
    //endregion

    //region ControllerListener
    override fun onSubmit(id: String, callerContext: Any?) {
        findStateByControllerId(id)?.apply {
            isAnyImageSet = false
            isFinalImageFetchFailed = false
        }
    }

    override fun onIntermediateImageSet(id: String, imageInfo: Any?) {
        findStateByControllerId(id)?.isAnyImageSet = true
    }

    override fun onFinalImageSet(id: String, imageInfo: Any?, animatable: Animatable?) {
        findStateByControllerId(id)?.isAnyImageSet = true
    }

    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) = Unit

    override fun onFailure(id: String?, throwable: Throwable?) {
        val state: DraweeViewState = findStateByControllerId(id) ?: return
        state.isFinalImageFetchFailed = throwable is UnknownHostException
    }

    override fun onRelease(id: String?) = Unit
    //endregion

    private fun findStateByControllerId(id: String?): DraweeViewState? =
        if (id != null) {
            attachedDraweeViews.find { it.abstractController?.id == id }?.state
        } else {
            null
        }

    private fun fetchFailedDraweeViews(): List<DraweeView<*>> =
        attachedDraweeViews.filter {
            val state: DraweeViewState? = it.state
            state != null && state.isFinalImageFetchFailed && !state.isAnyImageSet
        }

    private var DraweeView<*>.state: DraweeViewState?
        get() = getTag(R.id.fresco_view_drawee_view_state_tag_key) as DraweeViewState?
        set(value) {
            setTag(R.id.fresco_view_drawee_view_state_tag_key, value)
        }

    private val DraweeView<*>.abstractController: AbstractDraweeController<*, *>?
        get() = controller as AbstractDraweeController<*, *>?

    private fun DraweeView<*>.retry() {
        controller = controller ?: return
    }
}

/** @SelfDocumented */
fun <T : AbstractDraweeController<*, *>> T.enableRetry(): T = apply { attachToController(this) }
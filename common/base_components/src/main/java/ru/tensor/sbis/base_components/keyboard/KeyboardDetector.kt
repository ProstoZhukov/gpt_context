@file:JvmName("KeyboardDetectorExtension")
@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.base_components.keyboard

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * Детектор появления/скрытия клавиатуры
 *
 * @param activity
 * @param rootViewProvider поставщик корневой [View].
 * Используется именно ленивая версия для поддержки кода в [ru.tensor.sbis.base_components.AdjustResizeActivity].
 * @param delegate ответственный за обработку скрытия/появления
 * @param heightRecalculate функция для пересчета высоты. Если отсутствует, то высота прокидывается без модификации.
 * Механизм используется в [MainActivity] приложения коммуникатор.
 *
 * @author kv.martyshenko
 */
class KeyboardDetector @JvmOverloads internal constructor(
    private val activity: Activity,
    private val rootViewProvider: () -> View,
    private val delegate: Delegate,
    private val heightRecalculate: ((Int) -> Int)? = null
) : AdjustResizeHelper.AdjustResizeHelperHost,
    AdjustResizeHelper.KeyboardStateInterface {

    /**
     * Делегат, ответственный за реагирование на появление/скрытие клавиатуры
     */
    interface Delegate : AdjustResizeHelper.KeyboardEventListener

    private var resizeHelper = AdjustResizeHelper(this)

    /**
     * исходя из документации, при обращении к viewTreeObserver через метод View.getViewTreeObserver может создаться
     * новый treeObserver и вернуться нам. Из-за этого при выходе с экрана не вызывался метод turnOff, так как он
     * обращался не к тому обсерверу. Поэтому мы запоминаем его.
     * @see View.getViewTreeObserver
     * https://developer.android.com/reference/android/view/View#getViewTreeObserver()
     */
    private var treeObserver: ViewTreeObserver? = null

    /**
     * Метод установки кастомного подсчета высоты клавиатуры
     */
    fun setResizeHelper(resizeHelper: AdjustResizeHelper) {
        this.resizeHelper = resizeHelper
    }

    /**
     * Метод для активации детектора
     */
    fun turnOn() {
        treeObserver = contentView.viewTreeObserver
        treeObserver!!.addOnGlobalLayoutListener(resizeHelper)
    }

    /**
     * Метод для деактивации детектора
     */
    fun turnOff() {
        val actualTreeObserver = contentView.viewTreeObserver
        if (treeObserver != null && treeObserver!!.isAlive) {
            //workaround for proper keyboard measurements on new application opening (ex. push).
            treeObserver!!.removeOnGlobalLayoutListener(resizeHelper)
        }
        //библиотека viewTree сама подменяет обсерверы внутри в зависимости от своего состояния.
        //ожидается, что мы будем всегда обращаться к актуальному, но иногда из неё утекают
        //обсерверы, в результате мы должны его держать, чтобы корректно удалять resizeHelper
        //
        //https://online.sbis.ru/opendoc.html?guid=b88c2a5b-caa5-4339-b8d7-0a1ba980c43c
        //Но в случае, если мы имеем несколько keyboardDetector одновременно на экране,
        //ссылка на удержанный обсервер становится невалидной после созданя нового keyboardDetector
        //на такие случаи мы обращаемся к актуальной версии viewTree, в которую добавятся все
        //существующие обсерверы, когда система решит подменить viewTreeObserver
        else if (actualTreeObserver.isAlive){
            actualTreeObserver.removeOnGlobalLayoutListener(resizeHelper)
        }
        treeObserver = null
    }

    // region AdjustResizeHelper.AdjustResizeHelperHost
    override fun onKeyboardOpenMeasure(keyboardHeight: Int) {
        dispatchKeyboardAction(keyboardHeight, Delegate::onKeyboardOpenMeasure)
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int) {
        dispatchKeyboardAction(keyboardHeight, Delegate::onKeyboardCloseMeasure)
    }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getContentView(): View {
        return rootViewProvider()
    }
    // endregion

    // region AdjustResizeHelper.KeyboardStateInterface
    override fun isKeyboardOpen(): Boolean {
        return resizeHelper.isKeyboardOpen
    }
    //endregion

    private inline fun dispatchKeyboardAction(height: Int, action: Delegate.(Int) -> Unit) {
        val adjustedHeight = heightRecalculate?.invoke(height) ?: height
        delegate.action(adjustedHeight)
    }

}

/**
 * Метод позволяет автоматически включать и отключать детектор появления клавиатуры
 * под управлением событий жизненного цикла.
 * Активирует в момент [Lifecycle.Event.ON_RESUME], деактивирует - [Lifecycle.Event.ON_PAUSE]
 *
 * @author kv.martyshenko
 */
fun KeyboardDetector.manageBy(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            turnOn()
        }

        override fun onPause(owner: LifecycleOwner) {
            turnOff()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
        }

    })
}
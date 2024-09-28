package ru.tensor.sbis.communicator.sbis_conversation.utils

import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.tracing.Trace
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.sbis_conversation.utils.ListUpdateHelper.MissedState.MissedAction
import ru.tensor.sbis.communicator.sbis_conversation.utils.animation.FragmentSoftOpenAnimation
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView
import java.util.concurrent.TimeUnit

/**
 * Вспомогательный класс для управления списочным компонентом [ListViewContract]
 * и предотвращения установки данных в период анимации фрагмента, чтобы избежать фризов анимации, например:
 * - когда после первого list пришли колбэки, списки загрузились и хотят установиться
 * - или когда отработала пагинация после первой установки данных.
 * Все действия, которые были применены к списку во время анимации - сохраняются,
 * а по окончанию анимации применяются в заданной последовательности [MissedState.order].
 *
 * Анимация показа фрагмента запускается после установки первых данных из кэша:
 * пустой или существующий список из кэша контроллера - неважно,
 * главное что-то получить и не показывать лишний раз голый экран.
 *
 * @author vv.chekurda
 */
internal class ListUpdateHelper<FRAGMENT, T>(
    private val fragment: FRAGMENT
) : LifecycleObserver where FRAGMENT : Fragment, FRAGMENT : ListViewContract<T> {

    var isAnimationRunning = false
        private set
    private var firstDataSet = false
    private val missedState = MissedState().apply {
        order = listOf(
            showOlderProgressAction,
            showNewerProgressAction,
            showOlderErrorAction,
            showNewerErrorAction,
            setRelevantMessageAction,
            setAdapterDataAction,
            scrollAction
        )
    }
    private val dataWaitingTimeout: Completable
        get() = Observable.timer(DATA_WAITING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .ignoreElements()
    private val timeoutDisposable = SerialDisposable()

    init {
        fragment.lifecycle.addObserver(this)
    }

    fun init(postponeAnimation: Boolean) {
        if (postponeAnimation) {
            fragment.postponeEnterTransition()
            startDataWaitingTimeout()
        }
        if (!fragment.isStateSaved) firstDataSet = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        fragment.lifecycle.removeObserver(this)
        missedState.clear()
        timeoutDisposable.dispose()
    }

    /**
     * Запустить таймер ожидания данных.
     * По окончанию таймера будет запущена анимация открытия экрана.
     * Механика необходима для предотвращения случаев, когда контроллер ничего не отдал по списку сообщений
     * или по данным переписки для шапки.
     */
    private fun startDataWaitingTimeout() {
        dataWaitingTimeout.subscribe { fragment.startPostponedEnterTransition() }
            .storeIn(timeoutDisposable)
    }

    /**
     * Метод для создания аниации показа/скрытия фрагмента.
     */
    fun onCreateAnimation(enter: Boolean, nextAnim: Int): Animation? =
        if (enter && nextAnim != 0x0) {
            val animation = FragmentSoftOpenAnimation(fragment.requireContext())
            animation.setAnimationListener(object : CommonUtils.SimpleAnimationListener() {
                override fun onAnimationStart(animation: Animation?) {
                    isAnimationRunning = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    isAnimationRunning = false
                    Trace.endAsyncSection("FragmentSoftOpenAnimation.running", 0)
                    missedState.invokeMissedActions()
                }
            })
            animation
        } else null

    /** @see ListViewContract.internalUpdateDataList */
    fun updateDataList(data: List<T>?, offset: Int) {
        safeInvoke(missedState.setAdapterDataAction) {
            fragment.internalUpdateDataList(data, offset)
            if (firstDataSet) {
                firstDataSet = false
                timeoutDisposable.dispose()
                fragment.startPostponedEnterTransition()
            }
        }
    }

    /** @see ListViewContract.internalUpdateDataListWithoutNotification */
    fun updateDataListWithoutNotification(data: List<T>?, offset: Int) {
        val action =
            if (isAnimationRunning) Runnable { fragment.internalUpdateDataList(data, offset) }
            else Runnable { fragment.internalUpdateDataListWithoutNotification(data, offset) }
        return safeInvoke(missedState.setAdapterDataAction, action)
    }

    /** @see ListViewContract.internalScrollToPosition */
    fun scrollToPosition(position: Int) =
        safeInvoke(missedState.scrollAction) { fragment.internalScrollToPosition(position) }

    /** @see ListViewContract.internalScrollToBottom */
    fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean) =
        safeInvoke(missedState.scrollAction) { fragment.internalScrollToBottom(skipScrollToPosition, withHide) }

    /** @see ListViewContract.internalSetRelevantMessagePosition */
    fun setRelevantMessagePosition(position: Int) =
        safeInvoke(missedState.setRelevantMessageAction) { fragment.internalSetRelevantMessagePosition(position) }

    /** @see ListViewContract.internalShowOlderLoadingProgress */
    fun showOlderLoadingProgress(show: Boolean) =
        safeInvoke(missedState.showOlderProgressAction) { fragment.internalShowOlderLoadingProgress(show) }

    /** @see ListViewContract.internalShowNewerLoadingProgress */
    fun showNewerLoadingProgress(show: Boolean) =
        safeInvoke(missedState.showNewerProgressAction) { fragment.internalShowNewerLoadingProgress(show) }

    /** @see ListViewContract.internalShowOlderLoadingError */
    fun showOlderLoadingError() =
        safeInvoke(missedState.showOlderErrorAction) { fragment.internalShowOlderLoadingError() }

    /** @see ListViewContract.internalShowNewerLoadingError */
    fun showNewerLoadingError() =
        safeInvoke(missedState.showNewerErrorAction) { fragment.internalShowNewerLoadingError() }

    /** @see ListViewContract.internalNotifyItemsChanged */
    fun notifyItemsChanged(position: Int, count: Int, payLoad: Any? = null) {
        if (!isAnimationRunning) fragment.internalNotifyItemsChanged(position, count, payLoad)
    }

    /** @see ListViewContract.internalNotifyItemsChanged */
    fun notifyItemsChanged(position: Int, count: Int) {
        if (!isAnimationRunning) fragment.internalNotifyItemsChanged(position, count)
    }

    /** @see ListViewContract.internalNotifyDataSetChanged */
    fun notifyDataSetChanged() {
        if (!isAnimationRunning) fragment.internalNotifyDataSetChanged()
    }

    /** @see ListViewContract.internalNotifyItemsInserted */
    fun notifyItemsInserted(position: Int, count: Int) {
        if (!isAnimationRunning) fragment.internalNotifyItemsInserted(position, count)
    }

    /** @see ListViewContract.internalNotifyItemsRemoved */
    fun notifyItemsRemoved(position: Int, count: Int) {
        if (!isAnimationRunning) fragment.internalNotifyItemsRemoved(position, count)
    }

    /**
     * Безопасно исполнить действие [action].
     * В момент воспроизводенения анимации фрагмента по признаку [isAnimationRunning] - действие будет отложено
     * путем сохранения в соответствующий [MissedAction] до момента окончания анимации.
     */
    private fun safeInvoke(missedActionField: MissedAction, action: Runnable) {
        if (!isAnimationRunning) action.run()
        else missedActionField.action = action
    }

    /**
     * Класс, отвечающий за сохранение пропущенных [MissedAction] действий,
     * а такще позволяет запустить пропущенные действия в установленном порядке [order].
     */
    private class MissedState {

        /**
         * Очередность исполнения пропущенных действтий.
         */
        lateinit var order: List<MissedAction>

        val setRelevantMessageAction = MissedAction()
        val setAdapterDataAction = MissedAction()
        val scrollAction = MissedAction()
        val showOlderProgressAction = MissedAction()
        val showNewerProgressAction = MissedAction()
        val showOlderErrorAction = MissedAction()
        val showNewerErrorAction = MissedAction()

        /**
         * Исполнить пропущенные действия в установленном порядке [order]
         */
        fun invokeMissedActions() {
            order.forEach { it.run() }
        }

        /**
         * Очистить пропущенные действия.
         */
        fun clear() {
            order.forEach { it.action = null }
        }

        /**
         * Пропущенное действие.
         * Контейнер для хранения и одноразового использования [action]
         */
        class MissedAction(var action: Runnable? = null) : Runnable {
            override fun run() {
                action?.let {
                    it.run()
                    action = null
                }
            }
        }
    }
}

/**
 * Интерфейс для управления спискочным компонентом.
 */
internal interface ListViewContract<T> {

    /**
     * @see BaseTwoWayPaginationView.updateDataList - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalUpdateDataList(dataList: List<T>?, offset: Int)

    /**
     * @see BaseTwoWayPaginationView.updateDataListWithoutNotification - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalUpdateDataListWithoutNotification(dataList: List<T>?, offset: Int)

    /**
     * @see BaseTwoWayPaginationView.notifyItemsChanged - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalNotifyItemsChanged(position: Int, count: Int, payLoad: Any? = null)

    /**
     * @see BaseTwoWayPaginationView.notifyItemsChanged - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalNotifyItemsChanged(position: Int, count: Int)

    /**
     * @see BaseTwoWayPaginationView.notifyDataSetChanged - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalNotifyDataSetChanged()

    /**
     * @see BaseTwoWayPaginationView.notifyItemsInserted - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalNotifyItemsInserted(position: Int, count: Int)

    /**
     * @see BaseTwoWayPaginationView.notifyItemsRemoved - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalNotifyItemsRemoved(position: Int, count: Int)

    /**
     * @see BaseTwoWayPaginationView.showOlderLoadingProgress - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalShowOlderLoadingProgress(show: Boolean)

    /**
     * @see BaseTwoWayPaginationView.showNewerLoadingProgress - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalShowNewerLoadingProgress(show: Boolean)

    /**
     * @see BaseTwoWayPaginationView.scrollToPosition - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalScrollToPosition(position: Int)

    /**
     * @see BaseConversationViewContract.scrollToBottom - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalScrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean)

    /**
     * @see BaseConversationViewContract.setRelevantMessagePosition - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalSetRelevantMessagePosition(position: Int)

    /**
     * @see BaseConversationViewContract.showOlderLoadingError - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalShowOlderLoadingError()

    /**
     * @see BaseConversationViewContract.showNewerLoadingError - основной метод.
     * Текущий метод необходим для исполнения основного метода делегатом [ListUpdateHelper].
     */
    fun internalShowNewerLoadingError()
}

/**
 * Таймаут ожидания данных с контроллера для запуска анимации показа фрагмента.
 */
private const val DATA_WAITING_TIMEOUT_MS = 800L
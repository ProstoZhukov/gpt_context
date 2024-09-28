package ru.tensor.sbis.common.rx.scheduler

import android.os.Handler
import android.os.Looper
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.*

/**
 * @author Dmitry Subbotenko
 */

object TensorSchedulers {

    /**
     *      Шедулер AndroidSchedulers.mainThread() обладает одним очень серьезным недостатком.
     *      Он не проверяет поток на котором исполняется.
     *
     *      Это приводит к неприятной блокировке потока например при выполнении кода типа
     *
     *      Observable.just(0)
     *               .observeOnMainThread(AndroidSchedulers.mainThread() )
     *               .subscribe{ do something }
     *
     *      Код do something выполнится в непредсказуемый момент времени, и если внутри него будет еще одно переключение на
     *      mainThread может вообще возникнуть дедлок главного потока.
     *      подробнее см. https://github.com/ReactiveX/RxAndroid/issues/335
     *
     *      Этот планировщик использует HandlerExecutorService от Фрески, с частичной перегрузкой методов,
     *      что до некоторой степени решает проблему.
     *
     *      В целом это костыль который обеспечивает правильную работу стейтмашины, предотвращая дедлок в мейнтреде.
     *      Мне за 8 часов не удалось придумать иного решения этой проблемы.
     *      Как только оно появится, этот класс можно будет удалить
     *      FIXME: https://online.sbis.ru/opendoc.html?guid=df2295a0-9cb0-4856-a549-49f502857a03
     */

    val androidUiScheduler get() = Schedulers.from(UIExecutor())
}

class UIExecutor(
    private val handler: Handler = Handler(Looper.getMainLooper())
) : ExecutorService by HandlerExecutorServiceImpl(handler) {

    private val commands: BlockingQueue<Runnable> = LinkedBlockingQueue()

    override fun execute(command: Runnable) =
        if (isUiThread()) {
            command.run()
        } else {
            postOnUi(command)
        }

    private fun postOnUi(command: Runnable) {
        commands.add(command)
        handler.postDelayed({ commands.forEach(Runnable::run); commands.clear() }, 1)
    }

    private fun isUiThread() = Thread.currentThread() === Looper.getMainLooper().thread
}


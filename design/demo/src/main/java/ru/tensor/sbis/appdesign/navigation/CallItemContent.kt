package ru.tensor.sbis.appdesign.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import java.util.concurrent.TimeUnit

/**
 * Демо дополнительного контента в элементе меню аккордеона
 *
 * @author ma.kolpakov
 */
internal class CallItemContent private constructor(
    private val counterDisposable: SerialDisposable
) : NavigationItemContent, Disposable by counterDisposable {

    private lateinit var counterText: TextView
    private lateinit var startButton: Button

    override val visibility = Observable.interval(5, TimeUnit.SECONDS).map { it % 2 == 0L }

    constructor(): this(SerialDisposable())

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.navigation_item_content, container, false).also {
            counterText = it.findViewById(R.id.counter)
            startButton = it.findViewById(R.id.start_button)
            startButton.setOnClickListener {
                if (counterDisposable.get() == null) {
                    startButton.setText(R.string.navigation_content_counter_stop)
                    startCounter()
                } else {
                    startButton.setText(R.string.navigation_content_counter_start)
                    stopCounter()
                }
            }
        }
    }

    override fun onContentOpened() {
        startButton.setText(R.string.navigation_content_counter_start)
    }

    override fun onContentClosed() {
        stopCounter()
    }

    private fun startCounter() {
        counterDisposable.set(
            Observable.interval(500L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { counterText.text = it.toString() }
        )
    }

    private fun stopCounter() {
        counterDisposable.set(null)
        counterText.setText(R.string.navigation_content_counter_default_value)
    }
}